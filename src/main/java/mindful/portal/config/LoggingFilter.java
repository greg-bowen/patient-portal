package mindful.portal.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Logs incoming request body and outgoing response body for debugging/auditing purposes.
 * Works with JSON, form data, text, etc.
 */
@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

    @Value("${spring.profiles.active}")
    private String env;

    final String[] SKIP_STARTS_WITH_PATH = new String[]{
            "/actuator",
            "/swagger",
            "/api/",
            "/v3/api-docs"
    };

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Skip logging for certain paths if needed (e.g. actuator, static resources)
        if (shouldSkipLogging(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        // Wrap request to cache body
        CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);
        // Wrap response to capture output
        CachedBodyHttpServletResponse cachedResponse = new CachedBodyHttpServletResponse(response);

        try {
            // Proceed with the filter chain
            filterChain.doFilter(cachedRequest, cachedResponse);

            // Log AFTER the request has been processed
            logRequestDetails(cachedRequest);
            logResponseDetails(cachedResponse, response.getStatus());

            // Write the captured response back to the original response
            cachedResponse.copyBodyToResponse();
        } catch (Exception e) {
            log.error("Error during request processing", e);
            throw e;
        }
    }

    private boolean shouldSkipLogging(HttpServletRequest request) {
        String path = request.getRequestURI();
        // local gets everything
        if ("local".equals(env))
            return false;

        for (String skipPath : SKIP_STARTS_WITH_PATH) {
            if (path.startsWith(skipPath)) {
                return true;
            }
        }
        return "GET".equalsIgnoreCase(request.getMethod()) && path.contains("/static/");
    }

    private void logRequestDetails(CachedBodyHttpServletRequest request) {
        StringBuilder sb = new StringBuilder();

        sb.append("\n╔═══════════════════════════════════════ REQUEST ═══════════════════════════════════════╗\n");
        sb.append("Method     : ").append(request.getMethod()).append("\n");
        sb.append("URI        : ").append(request.getRequestURI()).append("\n");
        sb.append("Query      : ").append(request.getQueryString() != null ? request.getQueryString() : "-").append("\n");
        sb.append("RemoteAddr : ").append(request.getRemoteAddr()).append("\n");

        // Headers
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            sb.append("Headers    :\n");
            Collections.list(headerNames).forEach(name ->
                    sb.append("  ").append(name).append(": ").append(request.getHeader(name)).append("\n"));
        }

        // Body
        String body = request.getCachedBodyAsString();
        if (!body.isBlank()) {
            sb.append("Body       :\n").append(body).append("\n");
        } else {
            sb.append("Body       : <empty>\n");
        }

        sb.append("╚═══════════════════════════════════════════════════════════════════════════════════════╝");

        log.info(sb.toString());
    }

    private void logResponseDetails(CachedBodyHttpServletResponse response, int status) {
        StringBuilder sb = new StringBuilder();

        sb.append("\n╔══════════════════════════════════════ RESPONSE ═══════════════════════════════════════╗\n");
        sb.append("Status     : ").append(status).append("\n");

        // Important response headers
        sb.append("Headers    :\n");
        response.getHeaderNames().forEach(name ->
                sb.append("  ").append(name).append(": ").append(response.getHeader(name)).append("\n"));

        String body = response.getCachedBodyAsString();
        if (!body.isBlank()) {
            sb.append("Body       :\n").append(body).append("\n");
        } else {
            sb.append("Body       : <empty or binary>\n");
        }

        sb.append("╚═══════════════════════════════════════════════════════════════════════════════════════╝");

        if (status >= 400) {
            log.warn(sb.toString());
        } else {
            log.info(sb.toString());
        }
    }

    // ──────────────────────────────────────────────────────────────────────────────
    // Request Wrapper – caches body so it can be read multiple times
    // ──────────────────────────────────────────────────────────────────────────────
    private static class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {
        private final byte[] cachedBody;
        public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
            super(request);
            InputStream requestInputStream = request.getInputStream();
            this.cachedBody = StreamUtils.copyToByteArray(requestInputStream);
        }
        @Override
        public ServletInputStream getInputStream() {
            return new CachedBodyServletInputStream(this.cachedBody);
        }
        @Override
        public BufferedReader getReader() {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
            return new BufferedReader(new InputStreamReader(byteArrayInputStream, StandardCharsets.UTF_8));
        }
        public String getCachedBodyAsString() {
            return new String(this.cachedBody, StandardCharsets.UTF_8);
        }
    }

    private static class CachedBodyServletInputStream extends ServletInputStream {
        private final ByteArrayInputStream cachedBodyInputStream;
        public CachedBodyServletInputStream(byte[] cachedBody) {
            this.cachedBodyInputStream = new ByteArrayInputStream(cachedBody);
        }
        @Override
        public boolean isFinished() {
            return cachedBodyInputStream.available() == 0;
        }
        @Override
        public boolean isReady() {
            return true;
        }
        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }
        @Override
        public int read() {
            return cachedBodyInputStream.read();
        }
    }

    // ──────────────────────────────────────────────────────────────────────────────
    // Response Wrapper – captures output stream
    // ──────────────────────────────────────────────────────────────────────────────
    private static class CachedBodyHttpServletResponse extends HttpServletResponseWrapper {

        private final ByteArrayOutputStream capturedBody = new ByteArrayOutputStream();
        private PrintWriter writer;
        private ServletOutputStream outputStream;

        public CachedBodyHttpServletResponse(HttpServletResponse response) {
            super(response);
        }

        @Override
        public ServletOutputStream getOutputStream() {
            if (writer != null) {
                throw new IllegalStateException("getWriter() already called");
            }
            if (outputStream == null) {
                outputStream = new CachedBodyServletOutputStream(capturedBody);
            }
            return outputStream;
        }

        @Override
        public PrintWriter getWriter() {
            if (outputStream != null) {
                throw new IllegalStateException("getOutputStream() already called");
            }
            if (writer == null) {
                writer = new PrintWriter(new OutputStreamWriter(capturedBody, StandardCharsets.UTF_8));
            }
            return writer;
        }

        public String getCachedBodyAsString() {
            if (writer != null) {
                writer.flush();
            }
            return capturedBody.toString(StandardCharsets.UTF_8);
        }

        public void copyBodyToResponse() throws IOException {
            if (capturedBody.size() > 0) {
                getResponse().getOutputStream().write(capturedBody.toByteArray());
            }
        }
    }

    private static class CachedBodyServletOutputStream extends ServletOutputStream {
        private final ByteArrayOutputStream outputStream;
        public CachedBodyServletOutputStream(ByteArrayOutputStream outputStream) {
            this.outputStream = outputStream;
        }
        @Override
        public boolean isReady() {
            return true;
        }
        @Override
        public void setWriteListener(WriteListener writeListener) {
            throw new UnsupportedOperationException();
        }
        @Override
        public void write(int b) {
            outputStream.write(b);
        }
        @Override
        public void write(@NonNull byte[] b, int off, int len) {
            outputStream.write(b, off, len);
        }
    }
}