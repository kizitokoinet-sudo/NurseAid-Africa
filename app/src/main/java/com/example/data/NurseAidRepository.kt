package com.example.data

import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class NurseAidRepository {

    private val _nurses = MutableStateFlow<List<NurseProfile>>(initialNurses())
    val nurses: StateFlow<List<NurseProfile>> = _nurses.asStateFlow()

    private val _bookings = MutableStateFlow<List<Booking>>(initialBookings())
    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

    private val _articles = MutableStateFlow<List<HealthArticle>>(initialArticles())
    val articles: StateFlow<List<HealthArticle>> = _articles.asStateFlow()

    private val _maternalRecords = MutableStateFlow<List<MaternalChildRecord>>(initialMaternalRecords())
    val maternalRecords: StateFlow<List<MaternalChildRecord>> = _maternalRecords.asStateFlow()

    private val _medications = MutableStateFlow<List<MedicationItem>>(initialMedications())
    val medications: StateFlow<List<MedicationItem>> = _medications.asStateFlow()

    private val _emergencyProtocols = MutableStateFlow<List<EmergencyProtocol>>(initialEmergencyProtocols())
    val emergencyProtocols: StateFlow<List<EmergencyProtocol>> = _emergencyProtocols.asStateFlow()

    private val _patients = MutableStateFlow<List<PatientProfile>>(initialPatients())
    val patients: StateFlow<List<PatientProfile>> = _patients.asStateFlow()

    private val _walletTransactions = MutableStateFlow<List<WalletTransaction>>(initialWalletTransactions())
    val walletTransactions: StateFlow<List<WalletTransaction>> = _walletTransactions.asStateFlow()

    private val _notifications = MutableStateFlow<List<NotificationItem>>(initialNotifications())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // Concurrency / Anti-double-booking protected creation
    fun saveBooking(booking: Booking): Pair<Boolean, String> {
        val currentList = _bookings.value
        val hasConflict = currentList.any { existing ->
            existing.nurseId == booking.nurseId &&
                    existing.scheduledDate == booking.scheduledDate &&
                    existing.scheduledTime == booking.scheduledTime &&
                    existing.status !in listOf(BookingStatus.DECLINED, BookingStatus.CANCELLED)
        }

        if (hasConflict) {
            return Pair(
                false,
                "This nurse already has a confirmed clinical appointment at ${booking.scheduledTime} on ${booking.scheduledDate}. Please select another time."
            )
        }

        _bookings.value = listOf(booking) + currentList

        // Record Escrow deposit transaction
        val depositTx = WalletTransaction(
            id = "TX-DEP-${(100000..999999).random()}",
            userId = booking.patientId,
            type = WalletTransactionType.ESCROW_DEPOSIT,
            amountTZS = booking.totalTZS,
            provider = booking.paymentMethod,
            phoneNumber = booking.patientPhone,
            reference = booking.paymentReference,
            timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()),
            status = "COMPLETED"
        )
        _walletTransactions.value = listOf(depositTx) + _walletTransactions.value

        // Notify Nurse of incoming request
        val nurseAlert = NotificationItem(
            id = "notif-${System.currentTimeMillis()}",
            recipientRole = UserRole.NURSE,
            title = "New Assignment Request",
            message = "Booking request from ${booking.recipientName} for ${booking.service.titleEn} on ${booking.scheduledDate} at ${booking.scheduledTime}.",
            timestamp = "Just now",
            isRead = false,
            actionRoute = "requests"
        )
        _notifications.value = listOf(nurseAlert) + _notifications.value

        return Pair(true, "Booking successfully scheduled! Escrow secured.")
    }

    fun updateBookingStatus(
        bookingId: String,
        newStatus: BookingStatus,
        encounterNote: ClinicalEncounterNote? = null
    ) {
        _bookings.value = _bookings.value.map { booking ->
            if (booking.id == bookingId) {
                booking.copy(
                    status = newStatus,
                    clinicalEncounterNote = encounterNote ?: booking.clinicalEncounterNote
                )
            } else {
                booking
            }
        }
    }

    fun verifyNurse(nurseId: String, newStatus: VerificationStatus, auditNotes: String) {
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        _nurses.value = _nurses.value.map { nurse ->
            if (nurse.id == nurseId) {
                nurse.copy(
                    verificationStatus = newStatus,
                    verificationNotes = auditNotes,
                    verifiedAt = if (newStatus == VerificationStatus.VERIFIED) dateStr else nurse.verifiedAt
                )
            } else {
                nurse
            }
        }
    }

    fun toggleMedicationTaken(medId: String) {
        _medications.value = _medications.value.map { med ->
            if (med.id == medId) med.copy(isTakenToday = !med.isTakenToday) else med
        }
    }

    fun addMedication(name: String, dosage: String, time: String, instructions: String) {
        val newItem = MedicationItem(
            id = "med-${System.currentTimeMillis()}",
            medicationName = name,
            dosage = dosage,
            scheduleTime = time,
            instructions = instructions,
            isTakenToday = false
        )
        _medications.value = _medications.value + newItem
    }

    fun toggleMaternalRecord(recordId: String) {
        _maternalRecords.value = _maternalRecords.value.map { rec ->
            if (rec.id == recordId) rec.copy(isCompleted = !rec.isCompleted) else rec
        }
    }

    fun registerPatient(
        name: String,
        nidaId: String,
        phone: String,
        email: String,
        age: Int,
        gender: String,
        city: String = "Dar es Salaam",
        neighborhood: String,
        streetAddress: String,
        emergencyContact: String
    ): PatientProfile {
        val newPatient = PatientProfile(
            id = "patient-${System.currentTimeMillis()}",
            name = name,
            nidaId = nidaId,
            phone = phone,
            email = email,
            age = age,
            gender = gender,
            city = city,
            neighborhood = neighborhood,
            streetAddress = streetAddress,
            emergencyContactPhone = emergencyContact,
            registeredAt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        )
        _patients.value = listOf(newPatient) + _patients.value
        return newPatient
    }

    fun registerNurse(
        name: String,
        email: String,
        phone: String,
        cadre: String,
        tnmcRegistrationNumber: String,
        experienceYears: Int,
        qualifications: List<String>,
        serviceRadiusKm: Int = 15,
        serviceAreas: List<String>,
        specialties: List<ServiceCategory>,
        hourlyRateTZS: Long,
        bio: String
    ): NurseProfile {
        val newNurse = NurseProfile(
            id = "nurse-${System.currentTimeMillis()}",
            name = name,
            email = email,
            phone = phone,
            cadre = cadre,
            tnmcRegistrationNumber = tnmcRegistrationNumber,
            experienceYears = experienceYears,
            qualifications = qualifications,
            verificationStatus = VerificationStatus.PENDING,
            verificationNotes = "Submitted via Nurse Registration Portal. Awaiting TNMC license ledger audit.",
            serviceRadiusKm = serviceRadiusKm,
            serviceAreas = serviceAreas,
            specialties = specialties,
            hourlyRateTZS = hourlyRateTZS,
            rating = 5.0,
            reviewCount = 0,
            isAvailableNow = false,
            avatarInitials = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").ifBlank { "RN" },
            bio = bio
        )
        _nurses.value = listOf(newNurse) + _nurses.value

        // Add admin alert notification
        val adminAlert = NotificationItem(
            id = "notif-${System.currentTimeMillis()}",
            recipientRole = UserRole.ADMIN,
            title = "New Nurse Credential Application",
            message = "New application from $name ($cadre, Reg: $tnmcRegistrationNumber). Pending TNMC compliance review.",
            timestamp = "Just now",
            isRead = false,
            actionRoute = "audit_queue"
        )
        _notifications.value = listOf(adminAlert) + _notifications.value

        return newNurse
    }

    fun requestNursePayout(
        nurseId: String,
        amountTZS: Long,
        provider: PaymentProvider,
        phone: String
    ): Pair<Boolean, String> {
        val completedBookings = _bookings.value.filter { it.nurseId == nurseId && it.status == BookingStatus.COMPLETED }
        val totalEarned = completedBookings.sumOf { it.feeTZS }
        val previousWithdrawals = _walletTransactions.value
            .filter { it.userId == nurseId && it.type == WalletTransactionType.PAYOUT_WITHDRAWAL }
            .sumOf { it.amountTZS }
        val availableBalance = totalEarned - previousWithdrawals

        if (amountTZS <= 0) {
            return Pair(false, "Please enter a valid withdrawal amount greater than TZS 0.")
        }
        if (amountTZS > availableBalance) {
            return Pair(false, "Insufficient balance. Available to withdraw: TZS ${availableBalance.toString().reversed().chunked(3).joinToString(",").reversed()}.")
        }

        val ref = "WD-${provider.name}-${(100000..999999).random()}"
        val tx = WalletTransaction(
            id = "TX-WD-${System.currentTimeMillis()}",
            userId = nurseId,
            type = WalletTransactionType.PAYOUT_WITHDRAWAL,
            amountTZS = amountTZS,
            provider = provider,
            phoneNumber = phone,
            reference = ref,
            timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()),
            status = "COMPLETED"
        )
        _walletTransactions.value = listOf(tx) + _walletTransactions.value

        val notif = NotificationItem(
            id = "notif-${System.currentTimeMillis()}",
            recipientRole = UserRole.NURSE,
            title = "Payout Processed to ${provider.displayName}",
            message = "TZS ${amountTZS.toString().reversed().chunked(3).joinToString(",").reversed()} has been disbursed to $phone (Ref: $ref).",
            timestamp = "Just now",
            isRead = false,
            actionRoute = "wallet"
        )
        _notifications.value = listOf(notif) + _notifications.value

        return Pair(true, "Withdrawal of TZS ${amountTZS.toString().reversed().chunked(3).joinToString(",").reversed()} sent to $phone via ${provider.displayName} (Ref: $ref).")
    }

    fun calculateBusinessAnalytics(): BusinessAnalytics {
        val allBookings = _bookings.value
        val completed = allBookings.filter { it.status == BookingStatus.COMPLETED }
        val totalGross = allBookings.sumOf { it.totalTZS }
        val totalRevenue = allBookings.sumOf { it.platformFeeTZS }
        val activeNurses = _nurses.value.count { it.verificationStatus == VerificationStatus.VERIFIED }

        val demandMap = allBookings.groupBy { it.neighborhood }.mapValues { it.value.size }

        return BusinessAnalytics(
            totalGrossBookingValueTZS = totalGross,
            platformRevenueTZS = totalRevenue,
            totalBookingsCount = allBookings.size,
            completedBookingsCount = completed.size,
            activePractitionersCount = activeNurses,
            neighborhoodDemand = demandMap
        )
    }

    fun markNotificationRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    suspend fun simulateMobileMoneyPayment(
        provider: PaymentProvider,
        phone: String,
        amountTZS: Long
    ): Result<String> {
        val cleanPhone = phone.replace("\\s+".toRegex(), "")
        val regex = "^(?:\\+255|0)[67]\\d{8}\$".toRegex()
        if (!cleanPhone.matches(regex)) {
            return Result.failure(
                IllegalArgumentException("Tafadhali weka nambari sahihi ya simu ya Tanzania (mfano: 0754123456 au +255754123456)")
            )
        }
        delay(1800)
        val randomCode = (100000..999999).random()
        val ref = "NAA-${provider.name}-$randomCode"
        return Result.success(ref)
    }

    companion object {
        private fun initialNurses(): List<NurseProfile> = listOf(
            NurseProfile(
                id = "nurse-kizito",
                name = "Kizito Koinet Kileu, RN",
                email = "kizito@nurseaid.africa",
                phone = "+255 754 112 233",
                cadre = "Registered Nurse (RN) & Founder",
                tnmcRegistrationNumber = "TNMC/RN/2018/0491",
                experienceYears = 8,
                qualifications = listOf(
                    "BSc in Nursing - MUHAS (Dar es Salaam)",
                    "BLS/ACLS Advanced Cardiac Life Support",
                    "Certified Wound Care Specialist"
                ),
                verificationStatus = VerificationStatus.VERIFIED,
                verifiedAt = "2024-01-15 09:00",
                serviceRadiusKm = 25,
                serviceAreas = listOf("Kinondoni", "Mikocheni", "Oysterbay", "Masaki", "Upanga"),
                specialties = listOf(
                    ServiceCategory.HOME_CARE,
                    ServiceCategory.WOUND_DRESSING,
                    ServiceCategory.POST_OPERATIVE,
                    ServiceCategory.TELEHEALTH
                ),
                hourlyRateTZS = 35000L,
                rating = 4.96,
                reviewCount = 42,
                isAvailableNow = true,
                avatarInitials = "KK",
                bio = "Compassionate Registered Nurse with 8+ years hospital & home care experience. Dedicated to bridging accessible clinical support across urban Tanzania."
            ),
            NurseProfile(
                id = "nurse-neema",
                name = "Neema Mwamburi, RN-M",
                email = "neema.m@nurseaid.africa",
                phone = "+255 765 998 877",
                cadre = "Nurse Midwife (RN-M)",
                tnmcRegistrationNumber = "TNMC/MW/2019/1102",
                experienceYears = 6,
                qualifications = listOf(
                    "Diploma in Nursing & Midwifery",
                    "Neonatal Resuscitation Certified",
                    "Lactation & Postpartum Specialist"
                ),
                verificationStatus = VerificationStatus.VERIFIED,
                verifiedAt = "2024-02-10 11:30",
                serviceRadiusKm = 18,
                serviceAreas = listOf("Ilala", "Kariakoo", "Upanga", "Temeke", "Kinondoni"),
                specialties = listOf(
                    ServiceCategory.MATERNAL_CHILD,
                    ServiceCategory.HOME_CARE,
                    ServiceCategory.VITAL_SIGNS
                ),
                hourlyRateTZS = 30000L,
                rating = 4.89,
                reviewCount = 28,
                isAvailableNow = true,
                avatarInitials = "NM",
                bio = "Passionate midwife supporting mothers through prenatal care, newborn bonding, breastfeeding support, and safe postpartum recovery."
            ),
            NurseProfile(
                id = "nurse-baraka",
                name = "Baraka J. Mtui, EN",
                email = "baraka.mtui@gmail.com",
                phone = "+255 712 345 678",
                cadre = "Enrolled Nurse (EN)",
                tnmcRegistrationNumber = "TNMC/EN/2023/9821",
                experienceYears = 3,
                qualifications = listOf(
                    "Certificate in General Nursing",
                    "Geriatric & Palliative Care Skills"
                ),
                verificationStatus = VerificationStatus.PENDING,
                serviceRadiusKm = 12,
                serviceAreas = listOf("Mwenge", "Sinza", "Ubungo", "Mbezi"),
                specialties = listOf(
                    ServiceCategory.ELDERLY_CARE,
                    ServiceCategory.VITAL_SIGNS,
                    ServiceCategory.MEDICATION_ADMIN
                ),
                hourlyRateTZS = 25000L,
                rating = 4.75,
                reviewCount = 12,
                isAvailableNow = false,
                avatarInitials = "BM",
                bio = "Dedicated healthcare assistant focusing on compassionate elderly care, mobility assistance, and medication schedule tracking."
            ),
            NurseProfile(
                id = "nurse-rehema",
                name = "Rehema A. Rashid, BSN",
                email = "rehema.rashid@nurseaid.africa",
                phone = "+255 784 556 778",
                cadre = "Critical Care Nurse Specialist",
                tnmcRegistrationNumber = "TNMC/RN/2020/2290",
                experienceYears = 5,
                qualifications = listOf(
                    "BSN - Catholic University of Health and Allied Sciences (CUHAS)",
                    "Intensive Care Unit (ICU) Specialist Training",
                    "Tracheostomy & Catheter Care Certified"
                ),
                verificationStatus = VerificationStatus.VERIFIED,
                verifiedAt = "2024-03-01 14:00",
                serviceRadiusKm = 20,
                serviceAreas = listOf("Mikocheni", "Oysterbay", "Kawe", "Mbezi Beach"),
                specialties = listOf(
                    ServiceCategory.POST_OPERATIVE,
                    ServiceCategory.WOUND_DRESSING,
                    ServiceCategory.MEDICATION_ADMIN,
                    ServiceCategory.TELEHEALTH
                ),
                hourlyRateTZS = 40000L,
                rating = 4.92,
                reviewCount = 19,
                isAvailableNow = true,
                avatarInitials = "RR",
                bio = "Critical care specialist providing high-acuity in-home management, post-op drainage monitoring, and surgical wound rehabilitation."
            )
        )

        private fun initialBookings(): List<Booking> = listOf(
            Booking(
                id = "BK-829141",
                patientId = "patient-1",
                patientName = "Amina Salum",
                patientPhone = "+255 713 554 433",
                nurseId = "nurse-kizito",
                nurseName = "Kizito Koinet Kileu, RN",
                service = ServiceCategory.WOUND_DRESSING,
                visitType = VisitType.HOME_VISIT,
                scheduledDate = "2026-10-26",
                scheduledTime = "14:00",
                status = BookingStatus.PENDING_ACCEPTANCE,
                neighborhood = "Kinondoni",
                streetAddress = "Mwaya Road, House No. 14, Near St. Peter's",
                recipientName = "Amina Salum",
                recipientAge = 34,
                recipientGender = "Female",
                clinicalNotes = "Post-surgical cesarean dressing renewal. Slight inflammation noticed on peripheral incision line.",
                isSelf = true,
                feeTZS = 35000L,
                platformFeeTZS = 5000L,
                totalTZS = 40000L,
                paymentStatus = "PAID_ESCROW",
                paymentMethod = PaymentProvider.MPESA,
                paymentReference = "NAA-MPESA-849201",
                createdAt = "2026-10-25 08:30"
            ),
            Booking(
                id = "BK-716492",
                patientId = "patient-1",
                patientName = "Amina Salum",
                patientPhone = "+255 713 554 433",
                nurseId = "nurse-neema",
                nurseName = "Neema Mwamburi, RN-M",
                service = ServiceCategory.MATERNAL_CHILD,
                visitType = VisitType.HOME_VISIT,
                scheduledDate = "2026-10-24",
                scheduledTime = "10:30",
                status = BookingStatus.COMPLETED,
                neighborhood = "Ilala",
                streetAddress = "Sharif Shamba, Plot 4B",
                recipientName = "Baby Tariq (Amina's baby)",
                recipientAge = 1,
                recipientGender = "Male",
                clinicalNotes = "4-week postnatal wellness check and umbilical cord hygiene review.",
                isSelf = false,
                feeTZS = 30000L,
                platformFeeTZS = 5000L,
                totalTZS = 35000L,
                paymentStatus = "RELEASED_TO_NURSE",
                paymentMethod = PaymentProvider.AIRTEL_MONEY,
                paymentReference = "NAA-AIRTEL-992104",
                createdAt = "2026-10-23 09:15",
                clinicalEncounterNote = ClinicalEncounterNote(
                    bloodPressure = "N/A (Infant)",
                    heartRate = "132 bpm",
                    spo2 = "99%",
                    bloodGlucose = "Normal",
                    temperature = "36.7 °C",
                    woundAssessment = "Umbilical stump clean, healed with no erythema or discharge.",
                    proceduresPerformed = "Weight measured at 4.2 kg (healthy velocity). Cord evaluated.",
                    patientInstructions = "Continue exclusive breastfeeding on demand. Return for 6-week pentavalent immunizations.",
                    signedBy = "Neema Mwamburi, RN-M (TNMC/MW/2019/1102)",
                    signedAt = "2026-10-24 11:45"
                )
            )
        )

        private fun initialArticles(): List<HealthArticle> = listOf(
            HealthArticle(
                id = "art-1",
                titleEn = "Recognizing Danger Signs in a Newborn Baby",
                titleSw = "Dalili za Hatari kwa Mtoto Mchanga",
                category = "Maternal & Child",
                summaryEn = "Learn when an infant requires immediate hospital emergency pediatric evaluation.",
                summarySw = "Jifunze wakati mtoto anahitaji huduma ya dharura hospitalini bila kuchelewa.",
                contentEn = """
                    Newborns have immature immune and thermoregulatory systems. Watch for these red flags:
                    1. Inability to suckle or feed properly.
                    2. Convulsions or abnormal twitching.
                    3. Fast breathing (>60 breaths/minute) or severe chest in-drawing.
                    4. Hypothermia (<36.5°C) or high fever (>37.5°C).
                    5. Yellow palms or soles within first 48 hours (severe neonatal jaundice).
                    If any of these signs appear, seek immediate hospital attention or emergency transport.
                """.trimIndent(),
                contentSw = """
                    Watoto wachanga wanahitaji uangalizi wa karibu sana. Zingatia dalili hizi kuu za hatari:
                    1. Mtoto kushindwa kunyonya kabisa au kunyonya kinyonge sana.
                    2. Degedege au mtoto kurusha miguu na mikono kwa njia isiyo ya kawaida.
                    3. Kupumua kwa kasi sana (zaidi ya pumzi 60 kwa dakika) au kifua kuingia ndani wakati wa kuvuta pumzi.
                    4. Mwili kuwa wa baridi sana (chini ya 36.5°C) au homa kali (zaidi ya 37.5°C).
                    5. Manjano kwenye macho, viganja vya mikono au nyayo za miguu.
                    Ukiona dalili hizi, nenda hospitalini mara moja au piga namba ya dharura 115.
                """.trimIndent(),
                reviewedBy = "Kizito Koinet Kileu, RN & Pediatric Review Board",
                reviewDate = "October 2026",
                readTimeMinutes = 4
            ),
            HealthArticle(
                id = "art-2",
                titleEn = "Emergency First Aid for Burns and Scalds",
                titleSw = "Huduma ya Kwanza kwa Majeraha ya Moto na Maji ya Moto",
                category = "First Aid",
                summaryEn = "Immediate, evidence-based steps to minimize tissue damage before professional evaluation.",
                summarySw = "Hatua sahihi za kisayansi za kutoa huduma ya kwanza bila kutumia vitu hatarishi.",
                contentEn = """
                    Immediate response rules for thermal burns:
                    1. COOL IMMEDIATELY: Place burn under clean, cool running water for at least 20 minutes.
                    2. DO NOT apply ice directly, butter, raw eggs, engine oil, flour, or toothpaste—these trap heat and introduce severe infections.
                    3. REMOVE tight items (rings, watches, restrictive clothing) before swelling begins, but do NOT peel off clothing stuck to burnt skin.
                    4. COVER loosely with clean cling wrap or sterile non-adherent dressing.
                    5. Seek immediate clinical assessment if the burn is larger than the patient's palm, on face/joints, or in a child.
                """.trimIndent(),
                contentSw = """
                    Miongozo ya haraka ya huduma ya kwanza kwa majeraha ya moto:
                    1. POZA MARA MOJA: Weka jeraha chini ya maji safi ya kawaida yanayotiririka kwa dakika zisizopungua 20.
                    2. USIWEKE barafu moja kwa moja, siagi, mayai mabichi, mafuta ya injini, unga wala dawa ya meno—hivi vinazidisha maambukizi makali na kuzuia joto kutoka.
                    3. VUA vitu vinavyobana (pete, bangili, saa, nguo) kabla eneo halijavimba, lakini USING'OE nguo iliyogandia kwenye jeraha.
                    4. FUNIKA kwa kitambaa safi kikavu kisicho na manyoya au 'cling film' safi bila kukaza.
                    5. Nenda hospitali mara moja ikiwa jeraha ni kubwa kuliko kiganja, liko usoni/viungoni, au limempata mtoto mdogo.
                """.trimIndent(),
                reviewedBy = "Emergency Nursing Committee Tanzania",
                reviewDate = "September 2026",
                readTimeMinutes = 3
            ),
            HealthArticle(
                id = "art-3",
                titleEn = "Managing Hypertension & Preventing Stroke",
                titleSw = "Kudhibiti Shinikizo la Juu la Damu na Kuzuia Kiharusi",
                category = "Chronic Disease",
                summaryEn = "Understanding silent high blood pressure, DASH dietary habits, and medication consistency in urban life.",
                summarySw = "Kuelewa presha inayoua kimya kimya, mlo sahihi na kumeza dawa bila kuacha.",
                contentEn = """
                    High blood pressure is known as the 'silent killer' because it rarely causes symptoms until vital organs suffer damage.
                    - Measure your blood pressure routinely: Target is below 130/80 mmHg for most adults.
                    - Cut down sodium: Reduce table salt, seasoned bouillon cubes, and processed snacks.
                    - Increase potassium & fiber: Eat fresh local vegetables (mchicha, kisamvu), bananas, and whole grains.
                    - NEVER stop antihypertensive medication when you feel healthy—medication is what keeps you feeling well!
                    - Emergency warning (FAST for stroke): Face drooping, Arm weakness, Speech difficulty = Time to call 112/115.
                """.trimIndent(),
                contentSw = """
                    Shinikizo la damu linajulikana kama 'muuaji wa kimya kimya' kwa sababu halina dalili za wazi hadi madhara yatokee.
                    - Pima presha yako mara kwa mara: Lengo ni kuwa chini ya 130/80 mmHg.
                    - Punguza chumvi: Punguza chumvi ya mezani, vipande vya supu vyenye chumvi nyingi, na vyakula vya kusindika.
                    - Ongeza mbogamboga na matunda: Kula mchicha, kisamvu, ndizi, na nafaka zisizokobolewa.
                    - USIACHE dawa za presha unapoanza kujisikia vizuri—dawa ndizo zinazokufanya uwe salama!
                    - Dalili za kiharusi (FAST): Uso kupinda, mkono kupoteza nguvu, kuongea kwa shida = Piga 112 au nenda hospitali haraka.
                """.trimIndent(),
                reviewedBy = "Kizito Koinet Kileu, RN",
                reviewDate = "October 2026",
                readTimeMinutes = 4
            )
        )

        private fun initialMaternalRecords(): List<MaternalChildRecord> = listOf(
            MaternalChildRecord(
                id = "mat-1",
                motherOrChildName = "Amina Salum (Mother)",
                stage = "6 Weeks Postpartum",
                recordType = "Postnatal",
                details = "Maternal wellness review, pelvic floor check, nutritional assessment, and family planning counseling.",
                dueDate = "2026-11-05",
                isCompleted = false
            ),
            MaternalChildRecord(
                id = "mat-2",
                motherOrChildName = "Baby Tariq",
                stage = "6 Weeks Milestone",
                recordType = "Immunization",
                details = "Tanzanian National Vaccine Schedule: Pentavalent-1 (DTP-HepB-Hib), Oral Polio (OPV-1), Pneumococcal (PCV-1), Rotavirus-1.",
                dueDate = "2026-11-05",
                isCompleted = false
            ),
            MaternalChildRecord(
                id = "mat-3",
                motherOrChildName = "Baby Tariq",
                stage = "At Birth (Zero Dose)",
                recordType = "Immunization",
                details = "BCG (Tuberculosis) and Oral Polio Vaccine (OPV-0) administered at delivery.",
                dueDate = "2026-09-24",
                isCompleted = true
            )
        )

        private fun initialMedications(): List<MedicationItem> = listOf(
            MedicationItem(
                id = "med-1",
                medicationName = "Amoxicillin / Clavulanic Acid",
                dosage = "625 mg Tablet",
                scheduleTime = "08:00 & 20:00",
                instructions = "Take with a glass of water after food. Complete full 7-day course.",
                isTakenToday = true
            ),
            MedicationItem(
                id = "med-2",
                medicationName = "Ferrous Sulphate + Folic Acid",
                dosage = "200 mg / 400 mcg",
                scheduleTime = "13:00",
                instructions = "Do not take simultaneously with tea or dairy. Enhances postpartum hemoglobin.",
                isTakenToday = false
            ),
            MedicationItem(
                id = "med-3",
                medicationName = "Paracetamol (Panadol)",
                dosage = "1000 mg",
                scheduleTime = "As Needed (Max 3x/day)",
                instructions = "For moderate surgical incisional tenderness. Maintain 6-hour interval between doses.",
                isTakenToday = false
            )
        )

        private fun initialEmergencyProtocols(): List<EmergencyProtocol> = listOf(
            EmergencyProtocol(
                id = "em-triage",
                titleEn = "Red-Flag Emergency Symptoms",
                titleSw = "Dalili za Hatari Zinazohitaji Hospitali Haraka",
                category = "Triage Assessment",
                keyWarningEn = "Call 112 / 115 immediately. Do not wait for home visit appointment.",
                keyWarningSw = "Piga 112 au 115 mara moja. Usisubiri muuguzi wa nyumbani.",
                stepsEn = listOf(
                    "Loss of consciousness, unresponsiveness, or sudden profound confusion.",
                    "Inability to breathe, audible stridor, or cyanosis (blue lips/fingertips).",
                    "Sudden facial drop, arm weakness, or slurred speech (Signs of Stroke).",
                    "Severe postpartum vaginal bleeding soaking >2 pads within one hour.",
                    "Crushing central chest pain with radiation to left arm, jaw, or neck."
                ),
                stepsSw = listOf(
                    "Mgonjwa kupoteza fahamu, kutojibu, au kuchanganyikiwa ghafla.",
                    "Kushindwa kupumua, kutoa sauti ya kukoroma au midomo kuwa ya bluu/zambarau.",
                    "Uso kupinda ghafla, mkono kupoteza nguvu au kushindwa kuongea (Kiharusi).",
                    "Kutokwa damu nyingi baada ya kujifungua (kulowanisha taulo zaidi ya 2 kwa saa moja).",
                    "Maumivu makali katikati ya kifua yanayosambaa kwenye mkono wa kushoto au taya."
                )
            ),
            EmergencyProtocol(
                id = "em-cpr",
                titleEn = "Hands-Only CPR for Adults",
                titleSw = "Huduma ya Kwanza ya Moyo na Pumzi (CPR)",
                category = "Cardiovascular Emergency",
                keyWarningEn = "Push hard and fast in the center of the chest (100–120 bpm).",
                keyWarningSw = "Kandamiza kifua kwa nguvu na kasi katikati ya kifua (mara 100–120 kwa dakika).",
                stepsEn = listOf(
                    "Check responsiveness: Tap shoulders firmly and shout 'Are you okay?'.",
                    "Dispatch help: Call 112 or 115 and place phone on loudspeaker.",
                    "Position: Place the heel of one hand in the center of the chest; place other hand on top and interlock fingers.",
                    "Compress: Push down at least 5 cm (2 inches) at 100-120 compressions per minute to the tempo of 'Stayin' Alive'.",
                    "Recoil: Allow the chest to come completely up between each compression. Do not stop until medical help takes over."
                ),
                stepsSw = listOf(
                    "Angalia fahamu: Mtikise mgonjwa mabegani na uite kwa sauti: 'Uko sawa?'.",
                    "Piga simu ya dharura: Piga 112 au 115 na uweke sauti ya nje (loudspeaker).",
                    "Mkao: Weka kiganja kimoja katikati ya kifua cha mgonjwa, weka kiganja kingine juu yake na unganisha vidole.",
                    "Kandamiza: Bonyeza chini takriban sentimita 5 kwa kasi ya mara 100 hadi 120 kwa dakika.",
                    "Ruhusu kifua kirudi juu kikamilifu kila baada ya mgandamizo. Usisimame hadi wahudumu wa gari la wagonjwa wafike."
                )
            ),
            EmergencyProtocol(
                id = "em-bleed",
                titleEn = "Severe Hemorrhage Control",
                titleSw = "Kuzuia Kutokwa na Damu Nyingi",
                category = "Trauma & Wounds",
                keyWarningEn = "Continuous direct firm pressure is the single most effective intervention.",
                keyWarningSw = "Mgandamizo thabiti usiolegezwa ndiyo njia kuu ya kuokoa uhai.",
                stepsEn = listOf(
                    "Expose the wound: Cut or remove clothing around the bleeding site.",
                    "Apply direct pressure: Use sterile gauze or the cleanest cloth available. Press firmly with both hands.",
                    "Maintain continuous pressure: DO NOT lift the cloth to check if bleeding stopped. If blood soaks through, add another cloth on top.",
                    "Position: Keep the patient lying down and cover with a blanket to prevent shock and hypothermia.",
                    "Transport: Expedite urgent transfer to the nearest medical emergency center."
                ),
                stepsSw = listOf(
                    "Fichua jeraha: Vua au kata nguo iliyo karibu na jeraha linalovuja damu.",
                    "Kandamiza moja kwa moja: Tumia bandeji safi au kitambaa safi kabisa. Bonyeza kwa nguvu kwa mikono yote miwili.",
                    "Usiache kubonyeza: USINYANYUE kitambaa kuangalia kama damu imeacha. Damu ikitoka nje, ongeza kitambaa kingine juu yake.",
                    "Mkao wa mgonjwa: Mlaze chali na umfunike blanketi ili kumkinga na mshtuko wa mwili (shock).",
                    "Kimbiza hospitali: Mpeleke kituo cha afya kilicho karibu haraka iwezekanavyo."
                )
            ),
            EmergencyProtocol(
                id = "em-choke",
                titleEn = "Choking & Airway Obstruction",
                titleSw = "Mtu Kupaliwa / Njia ya Hewa Kuziba",
                category = "Airway Emergency",
                keyWarningEn = "If the person cannot cough, speak, or breathe, act immediately.",
                keyWarningSw = "Ikiwa mtu hawezi kukohoa, kuongea wala kuvuta pumzi, chukua hatua mara moja.",
                stepsEn = listOf(
                    "Confirm choking: Ask 'Are you choking?'. If they nod without speaking, intervene immediately.",
                    "5 Back blows: Stand behind them, lean them forward, and deliver 5 firm blows between shoulder blades with the heel of your hand.",
                    "5 Abdominal thrusts (Heimlich): Wrap arms around their waist, make a fist above the navel, grasp with other hand, and thrust inward and upward.",
                    "Repeat cycle of 5 back blows and 5 abdominal thrusts until the blockage clears.",
                    "If the person becomes unresponsive, lower gently to the floor and begin CPR compressions immediately."
                ),
                stepsSw = listOf(
                    "Thibitisha kupaliwa: Muulize 'Umepaliwa?'. Akitingisha kichwa bila kuweza kutoa sauti, anza huduma mara moja.",
                    "Vipigo 5 mgongoni: Simama nyuma yake, muinamishe mbele kidogo, piga kwa kiganja katikati ya mabega mara 5 kwa nguvu.",
                    "Migandamizo 5 ya tumbo (Heimlich): Mzungushie mikono kiunoni, fanya ngumi juu ya kitovu, kamata ngumi hiyo na usukume ndani na juu.",
                    "Rudia mtiririko huu (vipigo 5 na migandamizo 5) hadi kitu kilichoziba kitoke.",
                    "Mtu akipoteza fahamu, mlaze chini taratibu na anza CPR mara moja."
                )
            )
        )

        private fun initialPatients(): List<PatientProfile> = listOf(
            PatientProfile(
                id = "patient-amina",
                name = "Amina Salum",
                nidaId = "19900315-11105-00001-24",
                phone = "0713554433",
                email = "amina.salum@gmail.com",
                age = 34,
                gender = "Female",
                city = "Dar es Salaam",
                neighborhood = "Kinondoni",
                streetAddress = "Mwaya Road, House No. 14, Near St. Peter's",
                emergencyContactPhone = "0754998877",
                registeredAt = "2026-09-01 10:00"
            )
        )

        private fun initialWalletTransactions(): List<WalletTransaction> = listOf(
            WalletTransaction(
                id = "TX-DEP-849201",
                userId = "patient-amina",
                type = WalletTransactionType.ESCROW_DEPOSIT,
                amountTZS = 40000L,
                provider = PaymentProvider.MPESA,
                phoneNumber = "0754123456",
                reference = "NAA-MPESA-849201",
                timestamp = "2026-10-25 08:30",
                status = "COMPLETED"
            ),
            WalletTransaction(
                id = "TX-REL-716492",
                userId = "nurse-neema",
                type = WalletTransactionType.ESCROW_RELEASE,
                amountTZS = 30000L,
                provider = PaymentProvider.AIRTEL_MONEY,
                phoneNumber = "0765998877",
                reference = "NAA-AIRTEL-992104",
                timestamp = "2026-10-24 11:45",
                status = "COMPLETED"
            ),
            WalletTransaction(
                id = "TX-WD-991204",
                userId = "nurse-neema",
                type = WalletTransactionType.PAYOUT_WITHDRAWAL,
                amountTZS = 25000L,
                provider = PaymentProvider.AIRTEL_MONEY,
                phoneNumber = "0765998877",
                reference = "WD-AIRTEL-559102",
                timestamp = "2026-10-24 14:00",
                status = "COMPLETED"
            )
        )

        private fun initialNotifications(): List<NotificationItem> = listOf(
            NotificationItem(
                id = "notif-1",
                recipientRole = UserRole.NURSE,
                title = "New Assignment Scheduled",
                message = "Amina Salum has booked a Wound Dressing home visit for 2026-10-26 at 14:00.",
                timestamp = "Today at 08:30",
                isRead = false,
                actionRoute = "requests"
            ),
            NotificationItem(
                id = "notif-2",
                recipientRole = UserRole.NURSE,
                title = "Escrow Payout Credited",
                message = "TZS 30,000 has been credited to your wallet for completed visit with Baby Tariq.",
                timestamp = "Yesterday",
                isRead = true,
                actionRoute = "wallet"
            ),
            NotificationItem(
                id = "notif-3",
                recipientRole = UserRole.ADMIN,
                title = "Pending TNMC License Audit",
                message = "Baraka J. Mtui, EN has submitted credentials for verification.",
                timestamp = "Oct 24",
                isRead = false,
                actionRoute = "audit_queue"
            )
        )
    }
}
