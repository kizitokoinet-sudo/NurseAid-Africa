package com.example.model

enum class UserRole(val labelEn: String, val labelSw: String) {
    PATIENT("Patient / Caregiver", "Mgonjwa / Mlezi"),
    NURSE("Nurse Practitioner", "Muuguzi Mtaalamu"),
    ADMIN("Clinical Ops Admin", "Msimamizi wa Uendeshaji")
}

enum class Language(val code: String, val displayName: String) {
    EN("en", "English"),
    SW("sw", "Kiswahili")
}

enum class VerificationStatus(val labelEn: String, val labelSw: String) {
    PENDING("Under Review", "Inakaguliwa"),
    VERIFIED("TNMC Verified", "Imeidhinishwa na TNMC"),
    REJECTED("Verification Failed", "Imekataliwa"),
    SUSPENDED("Suspended", "Imesitishwa")
}

enum class BookingStatus(val labelEn: String, val labelSw: String) {
    PENDING_ACCEPTANCE("Awaiting Nurse Acceptance", "Inasubiri Kukubaliwa na Muuguzi"),
    ACCEPTED("Accepted by Nurse", "Imekubaliwa na Muuguzi"),
    CONFIRMED("Confirmed & Scheduled", "Imethibitishwa na Kupangwa"),
    IN_PROGRESS("Nurse In Transit / Care Active", "Muuguzi Yupo Njia / Huduma Inaendelea"),
    COMPLETED("Care Completed & Documented", "Huduma Imekamilika na Kunakiliwa"),
    DECLINED("Declined by Nurse", "Imekataliwa na Muuguzi"),
    CANCELLED("Cancelled", "Imeghairiwa")
}

enum class ServiceCategory(val key: String, val titleEn: String, val titleSw: String, val icon: String) {
    HOME_CARE("home_care", "General Home Nursing", "Uuguzi wa Jumla Nyumbani", "🩺"),
    WOUND_DRESSING("wound_dressing", "Wound & Ulcer Dressing", "Kusafisha na Kufunga Vidonda", "🩹"),
    MATERNAL_CHILD("maternal_child", "Maternal & Infant Care", "Huduma ya Mama na Mtoto", "👶"),
    ELDERLY_CARE("elderly_care", "Elderly & Palliative Care", "Uangalizi wa Wazee", "👵"),
    POST_OPERATIVE("post_operative", "Post-Surgical Recovery", "Uangalizi Baada ya Upasuaji", "🏥"),
    MEDICATION_ADMIN("medication_admin", "Medication & Injections", "Utoaji Dawa na Sindano", "💉"),
    TELEHEALTH("telehealth", "Telehealth Video Consultation", "Mashauriano ya Afya Mtandaoni", "📱"),
    VITAL_SIGNS("vital_signs", "Vital Signs & Blood Sugar", "Upimaji Shinikizo na Sukari", "📊")
}

enum class VisitType(val labelEn: String, val labelSw: String) {
    HOME_VISIT("Home Visit", "Uonano Nyumbani"),
    TELEHEALTH_CALL("Telehealth Video/Call", "Simu ya Video / Mtandaoni"),
    WORKPLACE_VISIT("Workplace Visit", "Uonano Kazini")
}

enum class PaymentProvider(val displayName: String, val prefix: String) {
    MPESA("M-Pesa (Vodacom)", "0754"),
    AIRTEL_MONEY("Airtel Money", "0784"),
    TIGOPESA("Tigo Pesa (Mixx by Yas)", "0714")
}

data class NurseProfile(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val cadre: String, // e.g. "Registered Nurse (RN)", "Nurse Midwife (RN-M)"
    val tnmcRegistrationNumber: String,
    val experienceYears: Int,
    val qualifications: List<String>,
    val verificationStatus: VerificationStatus,
    val verificationNotes: String = "",
    val verifiedAt: String = "",
    val serviceRadiusKm: Int = 15,
    val serviceAreas: List<String>, // e.g. ["Kinondoni", "Mikocheni", "Oysterbay"]
    val specialties: List<ServiceCategory>,
    val hourlyRateTZS: Long,
    val rating: Double,
    val reviewCount: Int,
    val isAvailableNow: Boolean,
    val avatarInitials: String = "RN",
    val bio: String = ""
)

data class Booking(
    val id: String,
    val patientId: String,
    val patientName: String,
    val patientPhone: String,
    val nurseId: String,
    val nurseName: String,
    val service: ServiceCategory,
    val visitType: VisitType,
    val scheduledDate: String, // YYYY-MM-DD
    val scheduledTime: String, // HH:mm
    val status: BookingStatus,
    val neighborhood: String,
    val streetAddress: String,
    val recipientName: String,
    val recipientAge: Int,
    val recipientGender: String,
    val clinicalNotes: String = "",
    val isSelf: Boolean = true,
    val feeTZS: Long,
    val platformFeeTZS: Long = 5000L,
    val totalTZS: Long,
    val paymentStatus: String = "PAID_ESCROW",
    val paymentMethod: PaymentProvider = PaymentProvider.MPESA,
    val paymentReference: String,
    val createdAt: String,
    val clinicalEncounterNote: ClinicalEncounterNote? = null
)

data class ClinicalEncounterNote(
    val bloodPressure: String = "", // e.g. 120/80 mmHg
    val heartRate: String = "",     // e.g. 74 bpm
    val spo2: String = "",          // e.g. 98%
    val bloodGlucose: String = "",  // e.g. 5.6 mmol/L
    val temperature: String = "",   // e.g. 36.8 °C
    val woundAssessment: String = "",
    val proceduresPerformed: String = "",
    val patientInstructions: String = "",
    val signedBy: String = "",
    val signedAt: String = ""
)

data class HealthArticle(
    val id: String,
    val titleEn: String,
    val titleSw: String,
    val category: String,
    val summaryEn: String,
    val summarySw: String,
    val contentEn: String,
    val contentSw: String,
    val reviewedBy: String,
    val reviewDate: String,
    val readTimeMinutes: Int
)

data class MaternalChildRecord(
    val id: String,
    val motherOrChildName: String,
    val stage: String, // e.g. "28 Weeks Gestation", "Baby: 6 Weeks"
    val recordType: String, // "Antenatal", "Immunization", "Postnatal"
    val details: String,
    val dueDate: String,
    val isCompleted: Boolean = false
)

data class MedicationItem(
    val id: String,
    val medicationName: String,
    val dosage: String,
    val scheduleTime: String,
    val instructions: String,
    val isTakenToday: Boolean = false
)

data class EmergencyProtocol(
    val id: String,
    val titleEn: String,
    val titleSw: String,
    val category: String,
    val keyWarningEn: String,
    val keyWarningSw: String,
    val stepsEn: List<String>,
    val stepsSw: List<String>
)

data class PatientProfile(
    val id: String,
    val name: String,
    val nidaId: String,
    val phone: String,
    val email: String,
    val age: Int,
    val gender: String,
    val city: String = "Dar es Salaam",
    val neighborhood: String,
    val streetAddress: String,
    val emergencyContactPhone: String,
    val registeredAt: String
)

enum class WalletTransactionType(val labelEn: String, val labelSw: String) {
    ESCROW_DEPOSIT("Escrow Deposit", "Dhamana Iliyowekwa"),
    ESCROW_RELEASE("Escrow Payout", "Malipo ya Huduma"),
    PAYOUT_WITHDRAWAL("Mobile Money Withdrawal", "Kutoa Pesa kwa Simu"),
    REFUND("Escrow Refund", "Kurejeshewa Pesa")
}

data class WalletTransaction(
    val id: String,
    val userId: String,
    val type: WalletTransactionType,
    val amountTZS: Long,
    val provider: PaymentProvider,
    val phoneNumber: String,
    val reference: String,
    val timestamp: String,
    val status: String = "COMPLETED"
)

data class NotificationItem(
    val id: String,
    val recipientRole: UserRole,
    val title: String,
    val message: String,
    val timestamp: String,
    val isRead: Boolean = false,
    val actionRoute: String = ""
)

data class BusinessAnalytics(
    val totalGrossBookingValueTZS: Long,
    val platformRevenueTZS: Long,
    val totalBookingsCount: Int,
    val completedBookingsCount: Int,
    val activePractitionersCount: Int,
    val neighborhoodDemand: Map<String, Int>
)

