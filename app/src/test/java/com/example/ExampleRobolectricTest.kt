package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.NurseAidRepository
import com.example.model.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("NurseAid Africa", appName)
    }

    @Test
    fun `repository prevents double booking collision`() {
        val repo = NurseAidRepository()
        val nurse = repo.nurses.value.first()

        val booking1 = Booking(
            id = "BK-TEST-1",
            patientId = "p-1",
            patientName = "Test Patient",
            patientPhone = "+255754111222",
            nurseId = nurse.id,
            nurseName = nurse.name,
            service = ServiceCategory.HOME_CARE,
            visitType = VisitType.HOME_VISIT,
            scheduledDate = "2026-11-10",
            scheduledTime = "09:00",
            status = BookingStatus.CONFIRMED,
            neighborhood = "Kinondoni",
            streetAddress = "Test St 1",
            recipientName = "Test Patient",
            recipientAge = 28,
            recipientGender = "Female",
            feeTZS = nurse.hourlyRateTZS,
            totalTZS = nurse.hourlyRateTZS + 5000L,
            paymentReference = "REF-1",
            createdAt = "2026-10-25 09:00"
        )

        val (saved1, _) = repo.saveBooking(booking1)
        assertTrue(saved1)

        // Try booking the exact same nurse at the exact same date and time
        val booking2 = booking1.copy(id = "BK-TEST-2")
        val (saved2, message) = repo.saveBooking(booking2)
        assertFalse(saved2)
        assertTrue(message.contains("already has a confirmed clinical appointment"))
    }

    @Test
    fun `mobile money payment validates Tanzanian phone numbers`() = runBlocking {
        val repo = NurseAidRepository()

        // Valid phone formats
        val result1 = repo.simulateMobileMoneyPayment(PaymentProvider.MPESA, "0754123456", 40000L)
        assertTrue(result1.isSuccess)
        assertTrue(result1.getOrThrow().startsWith("NAA-MPESA-"))

        val result2 = repo.simulateMobileMoneyPayment(PaymentProvider.AIRTEL_MONEY, "+255784987654", 35000L)
        assertTrue(result2.isSuccess)

        // Invalid phone format
        val resultInvalid = repo.simulateMobileMoneyPayment(PaymentProvider.MPESA, "12345", 40000L)
        assertTrue(resultInvalid.isFailure)
    }

    @Test
    fun `patient registration persists and updates patient list`() {
        val repo = NurseAidRepository()
        val initialCount = repo.patients.value.size

        val newPatient = repo.registerPatient(
            name = "Baraka Juma",
            nidaId = "19920512-14120-00002-18",
            phone = "+255713456789",
            email = "baraka.juma@example.co.tz",
            age = 34,
            gender = "Male",
            neighborhood = "Upanga",
            streetAddress = "Ali Hassan Mwinyi Rd, Plot 14",
            emergencyContact = "+255715987654"
        )

        assertNotNull(newPatient.id)
        assertEquals("Baraka Juma", newPatient.name)
        assertEquals(initialCount + 1, repo.patients.value.size)
    }

    @Test
    fun `nurse registration starts as pending and requires admin verification`() {
        val repo = NurseAidRepository()

        val newNurse = repo.registerNurse(
            name = "Faith Mwakyusa, RN",
            email = "faith.rn@nurseaid.co.tz",
            phone = "+255755333444",
            cadre = "Nurse Midwife (RN-M)",
            tnmcRegistrationNumber = "TNMC/RN/2023/8891",
            experienceYears = 5,
            qualifications = listOf("BSc Nursing - MUHAS", "EmONC Certified"),
            serviceRadiusKm = 12,
            serviceAreas = listOf("Kinondoni", "Mikocheni"),
            specialties = listOf(ServiceCategory.MATERNAL_CHILD),
            hourlyRateTZS = 32000L,
            bio = "Experienced nurse midwife passionate about safe delivery and postpartum care."
        )

        assertEquals(VerificationStatus.PENDING, newNurse.verificationStatus)

        // Public filter should not include unverified nurses
        val verifiedOnly = repo.nurses.value.filter { it.verificationStatus == VerificationStatus.VERIFIED }
        assertFalse(verifiedOnly.any { it.id == newNurse.id })

        // Admin audits and approves the nurse
        repo.verifyNurse(newNurse.id, VerificationStatus.VERIFIED, "TNMC register confirmed valid")
        val updatedNurse = repo.nurses.value.find { it.id == newNurse.id }
        assertNotNull(updatedNurse)
        assertEquals(VerificationStatus.VERIFIED, updatedNurse?.verificationStatus)
    }

    @Test
    fun `business analytics computes gross booking value and completion rate`() {
        val repo = NurseAidRepository()
        val analytics = repo.calculateBusinessAnalytics()

        assertTrue(analytics.totalGrossBookingValueTZS >= 0)
        assertTrue(analytics.platformRevenueTZS >= 0)
        assertTrue(analytics.activePractitionersCount > 0)
        assertTrue(analytics.neighborhoodDemand.isNotEmpty())
    }

    @Test
    fun `nurse payout request records wallet transaction and adjusts settled escrow`() {
        val repo = NurseAidRepository()
        val nurse = repo.nurses.value.first()

        // First register a completed clinical visit to fund the nurse's escrow account
        val completedBooking = Booking(
            id = "BK-TEST-PAYOUT-1",
            patientId = "patient-amina",
            patientName = "Amina Salum",
            patientPhone = "0713554433",
            nurseId = nurse.id,
            nurseName = nurse.name,
            service = ServiceCategory.HOME_CARE,
            visitType = VisitType.HOME_VISIT,
            scheduledDate = "2026-10-24",
            scheduledTime = "14:00",
            status = BookingStatus.COMPLETED,
            neighborhood = "Kinondoni",
            streetAddress = "Mwaya Rd",
            recipientName = "Amina Salum",
            recipientAge = 34,
            recipientGender = "Female",
            feeTZS = 50000L,
            totalTZS = 55000L,
            paymentReference = "REF-COMP-1",
            createdAt = "2026-10-24 10:00"
        )
        repo.saveBooking(completedBooking)

        val initialTxCount = repo.walletTransactions.value.size
        val (success, message) = repo.requestNursePayout(
            nurseId = nurse.id,
            amountTZS = 20000L,
            provider = PaymentProvider.MPESA,
            phone = "0754123456"
        )

        assertTrue(success)
        assertTrue(message.contains("sent to 0754123456 via M-Pesa"))
        assertEquals(initialTxCount + 1, repo.walletTransactions.value.size)

        val latestTx = repo.walletTransactions.value.first()
        assertEquals(WalletTransactionType.PAYOUT_WITHDRAWAL, latestTx.type)
        assertEquals(20000L, latestTx.amountTZS)
    }

    @Test
    fun `official NurVia Africa brand logo drawable is present and loadable`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val drawable = context.resources.getDrawable(R.drawable.ic_nurvia_africa_brand, null)
        assertNotNull("NurVia Africa official logo drawable asset must load without null", drawable)
    }
}
