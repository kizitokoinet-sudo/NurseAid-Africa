package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.NurseAidRepository
import com.example.model.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class UiState(
    val currentRole: UserRole = UserRole.PATIENT,
    val currentLanguage: Language = Language.EN,
    val patientTab: String = "find_nurse", // "find_nurse", "my_bookings", "wallet", "maternal", "meds", "education"
    val nurseTab: String = "requests",      // "requests", "active", "wallet", "notifications", "completed"
    val adminTab: String = "audit_queue",   // "audit_queue", "analytics", "verified_registry"
    val selectedNeighborhood: String = "All",
    val selectedSpecialty: String = "All",
    val isEmergencyDialogOpen: Boolean = false,
    val isAboutNurviaDialogOpen: Boolean = false,
    val isPatientRegDialogOpen: Boolean = false,
    val isNurseRegDialogOpen: Boolean = false,
    val isUrgentFirstAidDialogOpen: Boolean = false,
    val selectedNurseForBooking: NurseProfile? = null,
    val selectedNurseForAudit: NurseProfile? = null,
    val selectedBookingForEncounter: Booking? = null,
    val selectedArticle: HealthArticle? = null,
    val bannerNotice: String? = null,
    val isPaymentProcessing: Boolean = false,
    val paymentErrorMessage: String? = null,
    val currentPatientId: String = "patient-amina"
)

class NurseAidViewModel(
    val repository: NurseAidRepository = NurseAidRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val nurses: StateFlow<List<NurseProfile>> = repository.nurses
    val bookings: StateFlow<List<Booking>> = repository.bookings
    val articles: StateFlow<List<HealthArticle>> = repository.articles
    val maternalRecords: StateFlow<List<MaternalChildRecord>> = repository.maternalRecords
    val medications: StateFlow<List<MedicationItem>> = repository.medications
    val emergencyProtocols: StateFlow<List<EmergencyProtocol>> = repository.emergencyProtocols
    val patients: StateFlow<List<PatientProfile>> = repository.patients
    val walletTransactions: StateFlow<List<WalletTransaction>> = repository.walletTransactions
    val notifications: StateFlow<List<NotificationItem>> = repository.notifications

    fun setRole(role: UserRole) {
        _uiState.update { it.copy(currentRole = role) }
    }

    fun setLanguage(language: Language) {
        _uiState.update { it.copy(currentLanguage = language) }
    }

    fun setPatientTab(tab: String) {
        _uiState.update { it.copy(patientTab = tab) }
    }

    fun setNurseTab(tab: String) {
        _uiState.update { it.copy(nurseTab = tab) }
    }

    fun setAdminTab(tab: String) {
        _uiState.update { it.copy(adminTab = tab) }
    }

    fun openAboutNurviaDialog(open: Boolean) {
        _uiState.update { it.copy(isAboutNurviaDialogOpen = open) }
    }

    fun openPatientRegDialog(open: Boolean) {
        _uiState.update { it.copy(isPatientRegDialogOpen = open) }
    }

    fun openNurseRegDialog(open: Boolean) {
        _uiState.update { it.copy(isNurseRegDialogOpen = open) }
    }

    fun openUrgentFirstAidDialog(open: Boolean) {
        _uiState.update { it.copy(isUrgentFirstAidDialogOpen = open) }
    }

    fun setNeighborhoodFilter(neighborhood: String) {
        _uiState.update { it.copy(selectedNeighborhood = neighborhood) }
    }

    fun setSpecialtyFilter(specialty: String) {
        _uiState.update { it.copy(selectedSpecialty = specialty) }
    }

    fun openEmergencyDialog(open: Boolean) {
        _uiState.update { it.copy(isEmergencyDialogOpen = open) }
    }

    fun selectNurseForBooking(nurse: NurseProfile?) {
        _uiState.update { it.copy(selectedNurseForBooking = nurse, paymentErrorMessage = null) }
    }

    fun selectNurseForAudit(nurse: NurseProfile?) {
        _uiState.update { it.copy(selectedNurseForAudit = nurse) }
    }

    fun selectBookingForEncounter(booking: Booking?) {
        _uiState.update { it.copy(selectedBookingForEncounter = booking) }
    }

    fun selectArticle(article: HealthArticle?) {
        _uiState.update { it.copy(selectedArticle = article) }
    }

    fun clearBanner() {
        _uiState.update { it.copy(bannerNotice = null) }
    }

    fun createBooking(
        nurse: NurseProfile,
        service: ServiceCategory,
        visitType: VisitType,
        date: String,
        time: String,
        neighborhood: String,
        address: String,
        recipientName: String,
        recipientAge: Int,
        recipientGender: String,
        clinicalNotes: String,
        isSelf: Boolean,
        provider: PaymentProvider,
        phone: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isPaymentProcessing = true, paymentErrorMessage = null) }

            val totalAmount = nurse.hourlyRateTZS + 5000L
            val paymentResult = repository.simulateMobileMoneyPayment(provider, phone, totalAmount)

            if (paymentResult.isFailure) {
                _uiState.update {
                    it.copy(
                        isPaymentProcessing = false,
                        paymentErrorMessage = paymentResult.exceptionOrNull()?.message ?: "Payment verification failed."
                    )
                }
                return@launch
            }

            val paymentRef = paymentResult.getOrThrow()
            val booking = Booking(
                id = "BK-${(100000..999999).random()}",
                patientId = "patient-amina",
                patientName = "Amina Salum",
                patientPhone = phone,
                nurseId = nurse.id,
                nurseName = nurse.name,
                service = service,
                visitType = visitType,
                scheduledDate = date,
                scheduledTime = time,
                status = BookingStatus.PENDING_ACCEPTANCE,
                neighborhood = neighborhood,
                streetAddress = address,
                recipientName = recipientName,
                recipientAge = recipientAge,
                recipientGender = recipientGender,
                clinicalNotes = clinicalNotes,
                isSelf = isSelf,
                feeTZS = nurse.hourlyRateTZS,
                platformFeeTZS = 5000L,
                totalTZS = totalAmount,
                paymentStatus = "PAID_ESCROW",
                paymentMethod = provider,
                paymentReference = paymentRef,
                createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
            )

            val (saved, message) = repository.saveBooking(booking)
            if (!saved) {
                _uiState.update {
                    it.copy(
                        isPaymentProcessing = false,
                        paymentErrorMessage = message
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    isPaymentProcessing = false,
                    selectedNurseForBooking = null,
                    bannerNotice = "✓ Booking confirmed! Ref: $paymentRef. Assigned to ${nurse.name}."
                )
            }
            onSuccess()
        }
    }

    fun updateBookingStatus(bookingId: String, status: BookingStatus) {
        repository.updateBookingStatus(bookingId, status)
        _uiState.update {
            it.copy(bannerNotice = "Appointment status updated to: ${status.labelEn}")
        }
    }

    fun submitClinicalEncounter(
        bookingId: String,
        bp: String,
        hr: String,
        spo2: String,
        glucose: String,
        temp: String,
        wound: String,
        procedures: String,
        instructions: String,
        signedBy: String
    ) {
        val note = ClinicalEncounterNote(
            bloodPressure = bp,
            heartRate = hr,
            spo2 = spo2,
            bloodGlucose = glucose,
            temperature = temp,
            woundAssessment = wound,
            proceduresPerformed = procedures,
            patientInstructions = instructions,
            signedBy = signedBy,
            signedAt = SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
        )
        repository.updateBookingStatus(bookingId, BookingStatus.COMPLETED, note)
        _uiState.update {
            it.copy(
                selectedBookingForEncounter = null,
                bannerNotice = "✓ Clinical encounter documented and securely signed by $signedBy."
            )
        }
    }

    fun auditNurse(nurseId: String, status: VerificationStatus, auditNotes: String) {
        repository.verifyNurse(nurseId, status, auditNotes)
        _uiState.update {
            it.copy(
                selectedNurseForAudit = null,
                bannerNotice = "Nurse verification status updated to: ${status.labelEn}."
            )
        }
    }

    fun toggleMedication(id: String) {
        repository.toggleMedicationTaken(id)
    }

    fun addMedication(name: String, dosage: String, time: String, instructions: String) {
        repository.addMedication(name, dosage, time, instructions)
        _uiState.update {
            it.copy(bannerNotice = "✓ New medication reminder scheduled: $name ($dosage).")
        }
    }

    fun toggleMaternal(id: String) {
        repository.toggleMaternalRecord(id)
    }

    fun registerPatient(
        name: String,
        nidaId: String,
        phone: String,
        email: String,
        age: Int,
        gender: String,
        neighborhood: String,
        streetAddress: String,
        emergencyContact: String
    ) {
        val newPatient = repository.registerPatient(
            name = name,
            nidaId = nidaId,
            phone = phone,
            email = email,
            age = age,
            gender = gender,
            neighborhood = neighborhood,
            streetAddress = streetAddress,
            emergencyContact = emergencyContact
        )
        _uiState.update {
            it.copy(
                isPatientRegDialogOpen = false,
                currentPatientId = newPatient.id,
                currentRole = UserRole.PATIENT,
                bannerNotice = "✓ Welcome, ${newPatient.name}! Your patient profile is active."
            )
        }
    }

    fun registerNurse(
        name: String,
        email: String,
        phone: String,
        cadre: String,
        tnmcRegistrationNumber: String,
        experienceYears: Int,
        qualifications: List<String>,
        serviceRadiusKm: Int,
        serviceAreas: List<String>,
        specialties: List<ServiceCategory>,
        hourlyRateTZS: Long,
        bio: String
    ) {
        val newNurse = repository.registerNurse(
            name = name,
            email = email,
            phone = phone,
            cadre = cadre,
            tnmcRegistrationNumber = tnmcRegistrationNumber,
            experienceYears = experienceYears,
            qualifications = qualifications,
            serviceRadiusKm = serviceRadiusKm,
            serviceAreas = serviceAreas,
            specialties = specialties,
            hourlyRateTZS = hourlyRateTZS,
            bio = bio
        )
        _uiState.update {
            it.copy(
                isNurseRegDialogOpen = false,
                bannerNotice = "✓ Application submitted for ${newNurse.name}! Status: Pending TNMC license audit."
            )
        }
    }

    fun requestNursePayout(
        nurseId: String,
        amountTZS: Long,
        provider: PaymentProvider,
        phone: String
    ) {
        val (success, message) = repository.requestNursePayout(nurseId, amountTZS, provider, phone)
        _uiState.update {
            it.copy(bannerNotice = message)
        }
    }

    fun getBusinessAnalytics(): BusinessAnalytics {
        return repository.calculateBusinessAnalytics()
    }

    fun markNotificationRead(id: String) {
        repository.markNotificationRead(id)
    }
}
