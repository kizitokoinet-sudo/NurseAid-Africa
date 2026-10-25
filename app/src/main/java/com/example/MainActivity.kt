package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.UserRole
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.NurseAidViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                NurseAidApp()
            }
        }
    }
}

@Composable
fun NurseAidApp(viewModel: NurseAidViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val nurses by viewModel.nurses.collectAsStateWithLifecycle()
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()
    val articles by viewModel.articles.collectAsStateWithLifecycle()
    val maternalRecords by viewModel.maternalRecords.collectAsStateWithLifecycle()
    val medications by viewModel.medications.collectAsStateWithLifecycle()
    val emergencyProtocols by viewModel.emergencyProtocols.collectAsStateWithLifecycle()
    val walletTransactions by viewModel.walletTransactions.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()

    var isInitialLoading by remember { mutableStateOf(true) }

    if (isInitialLoading) {
        SplashScreen(
            language = uiState.currentLanguage,
            onComplete = { isInitialLoading = false }
        )
        return
    }

    // Handle back button gracefully across modals & workflows
    BackHandler(
        enabled = uiState.isEmergencyDialogOpen ||
                uiState.isAboutNurviaDialogOpen ||
                uiState.isPatientRegDialogOpen ||
                uiState.isNurseRegDialogOpen ||
                uiState.selectedNurseForBooking != null ||
                uiState.selectedNurseForAudit != null ||
                uiState.selectedBookingForEncounter != null ||
                uiState.selectedArticle != null
    ) {
        if (uiState.isEmergencyDialogOpen) viewModel.openEmergencyDialog(false)
        else if (uiState.isAboutNurviaDialogOpen) viewModel.openAboutNurviaDialog(false)
        else if (uiState.isPatientRegDialogOpen) viewModel.openPatientRegDialog(false)
        else if (uiState.isNurseRegDialogOpen) viewModel.openNurseRegDialog(false)
        else if (uiState.selectedNurseForBooking != null) viewModel.selectNurseForBooking(null)
        else if (uiState.selectedNurseForAudit != null) viewModel.selectNurseForAudit(null)
        else if (uiState.selectedBookingForEncounter != null) viewModel.selectBookingForEncounter(null)
        else if (uiState.selectedArticle != null) viewModel.selectArticle(null)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            NurseAidTopBar(
                currentRole = uiState.currentRole,
                currentLanguage = uiState.currentLanguage,
                onRoleChange = { viewModel.setRole(it) },
                onLanguageChange = { viewModel.setLanguage(it) },
                onOpenEmergency = { viewModel.openEmergencyDialog(true) },
                onOpenAboutNurvia = { viewModel.openAboutNurviaDialog(true) },
                onOpenPatientReg = { viewModel.openPatientRegDialog(true) },
                onOpenNurseReg = { viewModel.openNurseRegDialog(true) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Transient Banner Notification
            AnimatedVisibility(
                visible = uiState.bannerNotice != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                uiState.bannerNotice?.let { notice ->
                    Surface(
                        color = EmeraldDark,
                        shape = RoundedCornerShape(0.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = notice,
                                fontSize = 11.sp,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                color = EmeraldMint,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = EmeraldMint,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { viewModel.clearBanner() }
                            )
                        }
                    }
                }
            }

            // Primary Body based on Role
            Box(modifier = Modifier.weight(1f)) {
                when (uiState.currentRole) {
                    UserRole.PATIENT -> {
                        PatientHomeScreen(
                            nurses = nurses,
                            bookings = bookings,
                            walletTransactions = walletTransactions,
                            maternalRecords = maternalRecords,
                            medications = medications,
                            articles = articles,
                            language = uiState.currentLanguage,
                            activeTab = uiState.patientTab,
                            selectedNeighborhood = uiState.selectedNeighborhood,
                            selectedSpecialty = uiState.selectedSpecialty,
                            onTabChange = { viewModel.setPatientTab(it) },
                            onNeighborhoodChange = { viewModel.setNeighborhoodFilter(it) },
                            onSpecialtyChange = { viewModel.setSpecialtyFilter(it) },
                            onSelectNurse = { viewModel.selectNurseForBooking(it) },
                            onToggleMaternal = { viewModel.toggleMaternal(it) },
                            onToggleMedication = { viewModel.toggleMedication(it) },
                            onAddMedication = { name, dose, time, inst -> viewModel.addMedication(name, dose, time, inst) },
                            onSelectArticle = { viewModel.selectArticle(it) },
                            onOpenUrgentFirstAid = { viewModel.openEmergencyDialog(true) },
                            onOpenPatientReg = { viewModel.openPatientRegDialog(true) },
                            onViewEncounterNote = { viewModel.selectBookingForEncounter(it) }
                        )
                    }

                    UserRole.NURSE -> {
                        // Find active nurse profile (defaulting to Kizito Koinet Kileu, RN)
                        val activeNurse = nurses.find { it.id == "nurse-kizito" } ?: nurses.first()
                        NursePortalScreen(
                            nurse = activeNurse,
                            bookings = bookings,
                            walletTransactions = walletTransactions,
                            notifications = notifications,
                            language = uiState.currentLanguage,
                            activeTab = uiState.nurseTab,
                            onTabChange = { viewModel.setNurseTab(it) },
                            onUpdateStatus = { id, status -> viewModel.updateBookingStatus(id, status) },
                            onOpenEncounter = { viewModel.selectBookingForEncounter(it) },
                            onRequestPayout = { amount, provider, phone ->
                                viewModel.requestNursePayout(activeNurse.id, amount, provider, phone)
                            },
                            onMarkNotificationRead = { viewModel.markNotificationRead(it) },
                            onOpenNurseReg = { viewModel.openNurseRegDialog(true) }
                        )
                    }

                    UserRole.ADMIN -> {
                        AdminPortalScreen(
                            nurses = nurses,
                            bookings = bookings,
                            analytics = viewModel.getBusinessAnalytics(),
                            language = uiState.currentLanguage,
                            activeTab = uiState.adminTab,
                            onTabChange = { viewModel.setAdminTab(it) },
                            onInspectNurse = { viewModel.selectNurseForAudit(it) }
                        )
                    }
                }
            }

            // Persistent Parent-Brand Footer (NurVia Africa)
            NurviaFooter(
                language = uiState.currentLanguage,
                onOpenAboutNurvia = { viewModel.openAboutNurviaDialog(true) }
            )
        }
    }

    // About NurVia Africa Dialog (Official Parent Brand Presentation)
    if (uiState.isAboutNurviaDialogOpen) {
        AboutNurviaDialog(
            language = uiState.currentLanguage,
            onDismiss = { viewModel.openAboutNurviaDialog(false) }
        )
    }

    // Patient Registration Dialog
    if (uiState.isPatientRegDialogOpen) {
        PatientRegistrationDialog(
            language = uiState.currentLanguage,
            onDismiss = { viewModel.openPatientRegDialog(false) },
            onSubmit = { name, nidaId, phone, email, age, gender, neighborhood, streetAddress, emergencyContact ->
                viewModel.registerPatient(
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
            }
        )
    }

    // Nurse Practitioner Credential Registration Dialog
    if (uiState.isNurseRegDialogOpen) {
        NurseRegistrationDialog(
            language = uiState.currentLanguage,
            onDismiss = { viewModel.openNurseRegDialog(false) },
            onSubmit = { name, email, phone, cadre, tnmcReg, expYears, quals, radius, areas, specs, rate, bio ->
                viewModel.registerNurse(
                    name = name,
                    email = email,
                    phone = phone,
                    cadre = cadre,
                    tnmcRegistrationNumber = tnmcReg,
                    experienceYears = expYears,
                    qualifications = quals,
                    serviceRadiusKm = radius,
                    serviceAreas = areas,
                    specialties = specs,
                    hourlyRateTZS = rate,
                    bio = bio
                )
            }
        )
    }

    // Emergency Protocol Dialog
    if (uiState.isEmergencyDialogOpen) {
        EmergencyDialog(
            isOpen = true,
            protocols = emergencyProtocols,
            language = uiState.currentLanguage,
            onDismiss = { viewModel.openEmergencyDialog(false) }
        )
    }

    // Appointment Booking Dialog
    uiState.selectedNurseForBooking?.let { nurse ->
        BookingDialog(
            nurse = nurse,
            language = uiState.currentLanguage,
            isProcessing = uiState.isPaymentProcessing,
            errorMessage = uiState.paymentErrorMessage,
            onDismiss = { viewModel.selectNurseForBooking(null) },
            onConfirmBooking = { service, visitType, date, time, neighborhood, address, name, age, gender, notes, isSelf, provider, phone ->
                viewModel.createBooking(
                    nurse = nurse,
                    service = service,
                    visitType = visitType,
                    date = date,
                    time = time,
                    neighborhood = neighborhood,
                    address = address,
                    recipientName = name,
                    recipientAge = age,
                    recipientGender = gender,
                    clinicalNotes = notes,
                    isSelf = isSelf,
                    provider = provider,
                    phone = phone,
                    onSuccess = { /* handled in viewModel */ }
                )
            }
        )
    }

    // Clinical Encounter Documentation Dialog
    uiState.selectedBookingForEncounter?.let { booking ->
        ClinicalEncounterDialog(
            booking = booking,
            onDismiss = { viewModel.selectBookingForEncounter(null) },
            onSubmit = { bp, hr, spo2, gluc, temp, wound, procs, insts, signedBy ->
                viewModel.submitClinicalEncounter(
                    bookingId = booking.id,
                    bp = bp,
                    hr = hr,
                    spo2 = spo2,
                    glucose = gluc,
                    temp = temp,
                    wound = wound,
                    procedures = procs,
                    instructions = insts,
                    signedBy = signedBy
                )
            }
        )
    }

    // Administrator Credential Audit Dialog
    uiState.selectedNurseForAudit?.let { nurse ->
        NurseAuditDialog(
            nurse = nurse,
            onDismiss = { viewModel.selectNurseForAudit(null) },
            onDecision = { status, notes ->
                viewModel.auditNurse(nurse.id, status, notes)
            }
        )
    }

    // Health Education Article Reader Dialog
    uiState.selectedArticle?.let { article ->
        ArticleDetailDialog(
            article = article,
            language = uiState.currentLanguage,
            onDismiss = { viewModel.selectArticle(null) }
        )
    }
}
