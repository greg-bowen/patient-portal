package mindful.portal.config;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Slf4j
@Configuration
public class RequestFilter extends OncePerRequestFilter {

    // whitelist
    // if true skip doFilterInternal
    @Override
    protected boolean shouldNotFilter(@Nonnull HttpServletRequest request) {
        return "/actuator/health".equals(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws ServletException, IOException {
        try {
            // Wrap the request
            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
            String uri = request.getRequestURI();
            log.info("{} - endpoint: {}", request.getMethod(), uri);
            // Force caching of the body so downstream can read it
            wrappedRequest.getContentAsByteArray();
            // fill out key values from request

            // says "I'm done processing, pass this request to the next step."
            filterChain.doFilter(wrappedRequest, response);
        } finally {
            MDC.clear();
        }
    }

    public static JSONObject getBody(HttpServletResponse request) {
        try {
            ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
            return new JSONObject(new String(wrapper.getContentAsByteArray(), request.getCharacterEncoding()));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
