<template>
  <PatientLayout>
    <h1>Patient Profile</h1>

    <div class="profile-sections">
      <!-- Personal Information Section -->
      <div class="update-section">
        <h2>Personal Information</h2>
        <form @submit.prevent="updatePersonalInfo" class="update-form">
          <div class="row-2">
            <div class="form-group">
              <label>Preferred Name</label>
              <input v-model="patientInfo.preferredName" type="text" />
            </div>
            <div class="form-group">
              <label>First Name</label>
              <input v-model="patientInfo.firstName" type="text" required />
            </div>
          </div>
          <div class="row-2">
            <div class="form-group">
              <label>Last Name</label>
              <input v-model="patientInfo.lastName" type="text" required />
            </div>
            <div class="form-group">
              <label>Middle Name</label>
              <input v-model="patientInfo.middleName" type="text" />
            </div>
          </div>
          <div class="row-2">
            <div class="form-group">
              <label>Date of Birth</label>
              <input v-model="patientInfo.dob" type="date" required />
            </div>
            <div class="form-group">
              <label>Pronouns</label>
              <select v-model="patientInfo.pronouns" :disabled="pronounsLoading">
                <option value="">Select</option>
                <option
                    v-for="p in pronouns"
                    :key="p.id"
                    :value="p.id">
                  {{ p.name }}
                </option>
              </select>
              <small v-if="pronounsError" class="error">
                {{ pronounsError }}
              </small>
            </div>
          </div>
          <div class="row-1">
            <div class="form-group">
              <label>Gender</label>
              <select v-model="patientInfo.gender" :disabled="gendersLoading">
                <option value="">Select</option>
                <option
                    v-for="p in genders"
                    :key="p.id"
                    :value="p.id">
                  {{ p.name }}
                </option>
              </select>
              <small v-if="gendersError" class="error">
                {{ gendersError }}
              </small>
            </div>
          </div>
          <button type="submit" :disabled="personalLoading || !personalInfoChanged" class="update-btn">
            {{ personalLoading ? 'Updating...' : 'Update Personal Info' }}
          </button>
          <div v-if="personalMessage" :class="personalMessageClass">
            {{ personalMessage }}
          </div>
        </form>
      </div>


      <!-- Phone and Email Update Sections -->
      <div class="contact-row">

        <!-- Email Section -->
        <div class="update-section">
          <h2>Email Address</h2>
          <form @submit.prevent="updateEmail" class="update-form">
            <div class="form-group">
              <input v-model="editableEmail.emailAddress" type="email" required />
            </div>
            <button type="submit" :disabled="emailLoading || !emailChanged" class="update-btn">
              {{ emailLoading ? 'Updating...' : 'Update Email' }}
            </button>
            <div v-if="emailMessage" :class="emailMessageClass">
              {{ emailMessage }}
            </div>
          </form>
        </div>

        <!-- Phone Section -->
        <div class="update-section">
          <h2>Phone Number</h2>
          <form @submit.prevent="updatePhone" class="update-form">
            <div class="form-group">
              <input v-model="editablePhone.phoneNumber" v-mask="'(###) ###-####'" type="tel" required />
            </div>
            <div class="checkboxes">
              <label>
                <input v-model="editablePhone.sms" type="checkbox" />
                Send me SMS messages
              </label>
            </div>
            <br>
            <button type="submit" :disabled="phoneLoading || !phoneChanged" class="update-btn">
              {{ phoneLoading ? 'Updating...' : 'Update Phone' }}
            </button>
            <div v-if="phoneMessage" :class="phoneMessageClass">
              {{ phoneMessage }}
            </div>
          </form>
        </div>

      </div>

      <!-- Address Update Section -->
      <div class="update-section">
        <h2>Address</h2>
        <form @submit.prevent="updateAddress" class="update-form">
          <div class="form-group">
            <label>Address Line 1</label>
            <input v-model="editableAddress.addressLine1" type="text" required />
          </div>
          <div class="form-group">
            <label>Address Line 2</label>
            <input v-model="editableAddress.addressLine2" type="text"/>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>City</label>
              <input v-model="editableAddress.city" type="text" required />
            </div>
            <div class="form-group">
              <label>State</label>
              <input v-model="editableAddress.state" type="text" required />
            </div>
            <div class="form-group">
              <label>ZIP Code</label>
              <input v-model="editableAddress.zip" type="text" required />
            </div>
          </div>
          <button type="submit" :disabled="addressLoading || !addressChanged" class="update-btn">
            {{ addressLoading ? 'Updating...' : 'Update Address' }}
          </button>
          <div v-if="addressMessage" :class="addressMessageClass">
            {{ addressMessage }}
          </div>
        </form>
      </div>
    </div>
  </PatientLayout>
</template>

<script setup>
import {computed, onMounted, ref} from 'vue'
import PatientLayout from './PatientLayout.vue'

///////////////////////////////////
// Pronouns
const pronouns = ref([])
const pronounsLoading = ref(false)
const pronounsError = ref('')

const loadPronouns = async () => {
  pronounsLoading.value = true
  pronounsError.value = ''

  try {
    const response = await fetch('/api/get-pronouns')

    if (response.ok) {
      pronouns.value = await response.json()
    }
  } catch (err) {
    pronounsError.value = 'Unable to load pronouns'
    console.error(err)
  } finally {
    pronounsLoading.value = false
  }
}

///////////////////////////////////
// Genders
const genders = ref([])
const gendersLoading = ref(false)
const gendersError = ref('')

const loadGenders = async () => {
  gendersLoading.value = true
  gendersError.value = ''
  try {
    const response = await fetch('/api/get-genders')
    if (response.ok) {
      genders.value = await response.json()
    }
  } catch (err) {
    gendersError.value = 'Unable to load genders'
    console.error(err)
  } finally {
    gendersLoading.value = false
  }
}

///////////////////////////////////
// Phone Types
// const phoneTypes = ref([])
// const phoneTypesLoading = ref(false)
// const phoneTypesError = ref('')
//
// const loadPhoneTypes = async () => {
//   phoneTypesLoading.value = true
//   phoneTypesError.value = ''
//   try {
//     const response = await fetch('/api/get-phone-types')
//     if (response.ok) {
//       phoneTypes.value = await response.json()
//     }
//   } catch (err) {
//     phoneTypesError.value = 'Unable to load phone types'
//     console.error(err)
//   } finally {
//     phoneTypesLoading.value = false
//   }
// }

/////////////////////////////////
// Profile
const profile = ref({
  email: {
    emailAddress: ''
  },
  phone: {
    phoneNumber: '',
    sms: false
  },
  address: {
    addressLine1: '',
    addressLine2: '',
    city: '',
    state: '',
    zip: '',
    type: ''
  }
})

const patientInfo = ref({
  patientId:'',
  firstName: '',
  middleName: '',
  lastName: '',
  preferredName: '',
  pronouns: '',
  gender: '',
  dob: ''
})

const originalPersonalInfo = ref({})

const editablePhone = ref({
  patientId: '',
  phoneNumber: '',
  sms: false
})

const editableEmail = ref({
  patientId: '',
  emailAddress: ''
})

const editableAddress = ref({
  patientId: '',
  addressLine1: '',
  addressLine2: '',
  city: '',
  state: '',
  zip: '',
  type: ''
})

const phoneLoading = ref(false)
const emailLoading = ref(false)
const addressLoading = ref(false)
const personalLoading = ref(false)

const phoneMessage = ref('')
const emailMessage = ref('')
const addressMessage = ref('')
const personalMessage = ref('')

const phoneMessageClass = ref('')
const emailMessageClass = ref('')
const addressMessageClass = ref('')
const personalMessageClass = ref('')

const phoneChanged = computed(() =>
    stripNonNumeric(editablePhone.value.phoneNumber) !== stripNonNumeric(profile.value.phone.phoneNumber)
    || editablePhone.value.sms  !== profile.value.phone.sms)
const emailChanged = computed(() => editableEmail.value.emailAddress !== profile.value.email.emailAddress)

const addressChanged = computed(() => {
  const orig = profile.value.address
  const edit = editableAddress.value
  return orig.addressLine1 !== edit.addressLine1
      || orig.addressLine2 !== edit.addressLine2
      || orig.city !== edit.city
      || orig.state !== edit.state
      || orig.zip !== edit.zip
})
// determines if there are changes
const personalInfoChanged = computed(() => {
  const orig = originalPersonalInfo.value
  const edit = patientInfo.value
  return orig.firstName !== edit.firstName
      || orig.middleName !== edit.middleName
      || orig.lastName !== edit.lastName
      || orig.preferredName !== edit.preferredName
      || orig.pronouns !== edit.pronouns
      || orig.gender !== edit.gender
      || orig.dob !== edit.dob
})

const loadPatientData = async () => {
  try {
    const patientId = new URLSearchParams(window.location.search).get('id');
    const response = await fetch(`/api/get-patient?id=${patientId}`);
    if (response.ok) {
      const data = await response.json();

      // Convert dob array to JS Date
      let dobString = '';
      if (Array.isArray(data.patientInfo.dob)) {
        const [year, month, day] = data.patientInfo.dob;
        const dateObj = new Date(year, month - 1, day);

        // Convert to YYYY-MM-DD string for input[type="date"]
        const yyyy = dateObj.getFullYear();
        const mm = String(dateObj.getMonth() + 1).padStart(2, '0');
        const dd = String(dateObj.getDate()).padStart(2, '0');
        dobString = `${yyyy}-${mm}-${dd}`;
      }

      // Populate personal info
      patientInfo.value = {
        patientId: data.patientInfo.patientId || '',
        firstName: data.patientInfo.firstName || '',
        middleName: data.patientInfo.middleName || '',
        lastName: data.patientInfo.lastName || '',
        preferredName: data.patientInfo.preferredName || '',
        pronouns: data.patientInfo.pronouns || '',
        gender: data.patientInfo.gender || '',
        dob: dobString
      };
      originalPersonalInfo.value = { ...patientInfo.value };

      // Populate email
      editableEmail.value.emailAddress = data.email?.emailAddress || '';
      editableEmail.value.patientId = data.patientInfo.patientId || '';

      // Populate phone numbers (optional: pick primary or first)
      editablePhone.value.patientId = data.patientInfo.patientId || '';
      editablePhone.value.phoneNumber = stripNonNumeric(data.phone?.phoneNumber) || '';
      editablePhone.value.sms = data.phone?.sms || false;

      // Populate address (take first if exists)
      const addr = data.address || {};
      editableAddress.value = {
        patientId: data.patientInfo.patientId || '',
        addressLine1: addr.addressLine1 || '',
        addressLine2: addr.addressLine2 || null,
        city: addr.city || '',
        state: addr.state || '',
        zip: addr.zip || ''
      };

      // Update profile reference (raw objects)
      profile.value = {
        email: data.email || {},
        phone: data.phone || {},
        address: data.address || {}
      };
    }
  } catch (err) {
    console.error('Failed to load patient data:', err);
  }
};

onMounted(() => {
  loadPronouns()
  loadGenders()
  // data on patient
  loadPatientData()

  patientInfo.value = {
    firstName: '',
    middleName: '',
    lastName: '',
    preferredName: '',
    pronouns: '',
    gender: ''
  }

  originalPersonalInfo.value = { ...patientInfo.value }
  editablePhone.value.phoneNumber = stripNonNumeric(profile.value.phone.phoneNumber)
  editableEmail.value.emailAddress = profile.value.email.emailAddress
  editableAddress.value = { ...profile.value.address }
})

const updatePersonalInfo = async () => {
  personalLoading.value = true
  personalMessage.value = ''

  try {

    const response = await fetch('/api/update-personal-info', {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        patientInfo: patientInfo.value,
      }),
    })

    if (response.ok) {
      originalPersonalInfo.value = { ...patientInfo.value }
      personalMessage.value = 'Personal information updated successfully!'
      personalMessageClass.value = 'success'
    } else {
      personalMessage.value = 'Error updating personal information'
      personalMessageClass.value = 'error'
    }
  } catch (error) {
    personalMessage.value = 'Connection error'
    personalMessageClass.value = 'error'
  } finally {
    personalLoading.value = false
  }
}

const stripNonNumeric = (str) => str.replace(/\D+/g, '')

const updatePhone = async () => {
  phoneLoading.value = true
  phoneMessage.value = ''
  const cleanedPhone = {
    ...editablePhone.value,
    phoneNumber: stripNonNumeric(editablePhone.value.phoneNumber),
  }

  try {
    const response = await fetch('/api/update-phone', {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        phone: cleanedPhone,
      }),
    })

    if (response.ok) {
      profile.value.phone = editablePhone.value
      phoneMessage.value = 'Phone updated successfully!'
      phoneMessageClass.value = 'success'
    } else {
      phoneMessage.value = 'Error updating phone'
      phoneMessageClass.value = 'error'
    }
  } catch (error) {
    phoneMessage.value = 'Connection error'
    phoneMessageClass.value = 'error'
  } finally {
    phoneLoading.value = false
  }
}

const updateEmail = async () => {
  emailLoading.value = true
  emailMessage.value = ''

  try {
    const response = await fetch('/api/update-email', {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        patientId: 1,
        email: editableEmail.value,
      }),
    })

    if (response.ok) {
      profile.value.email = editableEmail.value
      emailMessage.value = 'Email updated successfully!'
      emailMessageClass.value = 'success'
    } else {
      emailMessage.value = 'Error updating email'
      emailMessageClass.value = 'error'
    }
  } catch (error) {
    emailMessage.value = 'Connection error'
    emailMessageClass.value = 'error'
  } finally {
    emailLoading.value = false
  }
}

const updateAddress = async () => {
  addressLoading.value = true
  addressMessage.value = ''

  try {
    const response = await fetch('/api/update-address', {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        patientId: 1,
        address: editableAddress.value,
      }),
    })

    if (response.ok) {
      profile.value.address = { ...editableAddress.value }
      addressMessage.value = 'Address updated successfully!'
      addressMessageClass.value = 'success'
    } else {
      addressMessage.value = 'Error updating address'
      addressMessageClass.value = 'error'
    }
  } catch (error) {
    addressMessage.value = 'Connection error'
    addressMessageClass.value = 'error'
  } finally {
    addressLoading.value = false
  }
}

</script>

<style scoped>
h1 {
  font-size: 2.5rem;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 2rem;
  text-align: center;
}

.profile-sections {
  display: grid;
  gap: 2rem;
  max-width: 800px;
  margin: 0 auto;
}

.contact-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
}

.update-section {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.update-section h2 {
  font-size: 1.5rem;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 1.5rem;
  border-bottom: 2px solid #e5e7eb;
  padding-bottom: 0.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  font-weight: 500;
  color: #374151;
  margin-bottom: 0.5rem;
}

.form-group input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 1rem;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-group input[readonly] {
  background: #f9fafb;
  color: #6b7280;
}

.form-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  gap: 1rem;
}

.update-btn {
  width: 100%;
  padding: 0.75rem;
  background: #3b82f6;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
  margin-bottom: 1rem;
}

.update-btn:hover:not(:disabled) {
  background: #2563eb;
}

.update-btn:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}

.error {
  color: #dc2626;
  text-align: center;
  padding: 0.5rem;
  background: #fef2f2;
  border-radius: 4px;
}

.row-1 {
  display: grid;
  grid-template-columns: 1fr;
  gap: 1rem;
}

.row-2 {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}
</style>