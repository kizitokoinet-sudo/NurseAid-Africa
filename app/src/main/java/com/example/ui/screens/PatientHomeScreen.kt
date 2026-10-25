package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun PatientHomeScreen(
    nurses: List<NurseProfile>,
    bookings: List<Booking>,
    walletTransactions: List<WalletTransaction>,
    maternalRecords: List<MaternalChildRecord>,
    medications: List<MedicationItem>,
    articles: List<HealthArticle>,
    language: Language,
    activeTab: String,
    selectedNeighborhood: String,
    selectedSpecialty: String,
    onTabChange: (String) -> Unit,
    onNeighborhoodChange: (String) -> Unit,
    onSpecialtyChange: (String) -> Unit,
    onSelectNurse: (NurseProfile) -> Unit,
    onToggleMaternal: (String) -> Unit,
    onToggleMedication: (String) -> Unit,
    onAddMedication: (String, String, String, String) -> Unit,
    onSelectArticle: (HealthArticle) -> Unit,
    onOpenUrgentFirstAid: () -> Unit = {},
    onOpenPatientReg: () -> Unit = {},
    onViewEncounterNote: (Booking) -> Unit = {}
) {
    val neighborhoods = listOf("All", "Kinondoni", "Mikocheni", "Oysterbay", "Upanga", "Ilala", "Mwenge")

    // Filter nurses strictly by TNMC verified status and filters
    val filteredNurses = nurses.filter { nurse ->
        val isVerified = nurse.verificationStatus == VerificationStatus.VERIFIED
        val matchesArea = selectedNeighborhood == "All" || nurse.serviceAreas.contains(selectedNeighborhood)
        val matchesSpecialty = selectedSpecialty == "All" || nurse.specialties.any { it.key == selectedSpecialty }
        isVerified && matchesArea && matchesSpecialty
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Sub-Navigation Pills
        Surface(
            color = Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    TabPill(
                        label = if (language == Language.EN) "Find a Nurse" else "Tafuta Muuguzi",
                        icon = Icons.Default.Search,
                        isSelected = activeTab == "find_nurse",
                        onClick = { onTabChange("find_nurse") }
                    )
                }
                item {
                    TabPill(
                        label = if (language == Language.EN) "My Bookings (${bookings.size})" else "Miadi Yangu (${bookings.size})",
                        icon = Icons.Default.EventNote,
                        isSelected = activeTab == "my_bookings",
                        onClick = { onTabChange("my_bookings") }
                    )
                }
                item {
                    TabPill(
                        label = if (language == Language.EN) "Patient Wallet" else "Pochi Yangu",
                        icon = Icons.Default.AccountBalanceWallet,
                        isSelected = activeTab == "wallet",
                        onClick = { onTabChange("wallet") }
                    )
                }
                item {
                    TabPill(
                        label = if (language == Language.EN) "Maternal & Child" else "Mama na Mtoto",
                        icon = Icons.Default.ChildCare,
                        isSelected = activeTab == "maternal",
                        onClick = { onTabChange("maternal") }
                    )
                }
                item {
                    TabPill(
                        label = if (language == Language.EN) "Medications" else "Ratiba ya Dawa",
                        icon = Icons.Default.Medication,
                        isSelected = activeTab == "meds",
                        onClick = { onTabChange("meds") }
                    )
                }
                item {
                    TabPill(
                        label = if (language == Language.EN) "Health Education" else "Elimu ya Afya",
                        icon = Icons.Default.MenuBook,
                        isSelected = activeTab == "education",
                        onClick = { onTabChange("education") }
                    )
                }
            }
        }

        // Body Content based on active tab
        when (activeTab) {
            "my_bookings" -> {
                PatientBookingsSection(
                    bookings = bookings,
                    language = language,
                    onViewEncounter = onViewEncounterNote,
                    onFindNurse = { onTabChange("find_nurse") }
                )
            }
            "wallet" -> {
                PatientWalletSection(
                    transactions = walletTransactions,
                    language = language
                )
            }
            "maternal" -> {
                MaternalChildSection(
                    records = maternalRecords,
                    language = language,
                    onToggleRecord = onToggleMaternal
                )
            }
            "meds" -> {
                MedicationSection(
                    medications = medications,
                    language = language,
                    onToggleMedication = onToggleMedication,
                    onAddMedication = onAddMedication
                )
            }
            "education" -> {
                HealthEducationScreen(
                    articles = articles,
                    language = language,
                    onSelectArticle = onSelectArticle
                )
            }
            else -> {
                // FIND A NURSE VIEW
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(10.dp))

                        // URGENT FIRST AID DISPATCH CALLOUT
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = EmergencyRose,
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenUrgentFirstAid() }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.HealthAndSafety,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = if (language == Language.EN) "Need Urgent First-Aid Triage?" else "Unahitaji Huduma ya Dharura?",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = if (language == Language.EN) "Tap for instant 112/115 dispatch & critical instructions" else "Bonyeza hapa kupata namba na msaada wa haraka",
                                            fontSize = 10.sp,
                                            color = Color.White.copy(alpha = 0.9f)
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Hero Header Card
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = EmeraldDark,
                            shadowElevation = 3.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (language == Language.EN)
                                                "Verified Home Care Nurses"
                                            else
                                                "Wauguzi Walioidhinishwa Nyumbani",
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = if (language == Language.EN)
                                                "Connected with licensed practitioners in Dar es Salaam for home visits, wound dressing, and post-discharge recovery."
                                            else
                                                "Ungana na wauguzi wenye leseni ya TNMC kwa ajili ya huduma za nyumbani na uangalizi wa wagonjwa.",
                                            fontSize = 11.sp,
                                            color = EmeraldMint.copy(alpha = 0.9f),
                                            lineHeight = 15.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Neighborhood filter chips
                                Text(
                                    text = if (language == Language.EN) "Area (Dar es Salaam):" else "Eneo (Dar es Salaam):",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldMint
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(neighborhoods) { area ->
                                        val isSelected = area == selectedNeighborhood
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSelected) Color.White else EmeraldPrimary)
                                                .clickable { onNeighborhoodChange(area) }
                                                .padding(horizontal = 8.dp, vertical = 5.dp)
                                        ) {
                                            Text(
                                                text = area,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) EmeraldDark else Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Results Counter
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (language == Language.EN)
                                    "Available Registered Nurses (${filteredNurses.size})"
                                else
                                    "Wauguzi Waliopo Tayari (${filteredNurses.size})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("TNMC Verified", fontSize = 10.sp, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Empty state or Nurse list
                    if (filteredNurses.isEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = SlateSubtle,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MedicalServices,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "No verified nurses match your filter",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Try switching to 'All' areas to view practitioners nearby.",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    } else {
                        items(filteredNurses) { nurse ->
                            NurseCard(
                                nurse = nurse,
                                language = language,
                                onBook = { onSelectNurse(nurse) }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun PatientBookingsSection(
    bookings: List<Booking>,
    language: Language,
    onViewEncounter: (Booking) -> Unit,
    onFindNurse: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (language == Language.EN) "Active & Past Healthcare Appointments" else "Miadi ya Huduma za Afya",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Button(
                onClick = onFindNurse,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("+ Book Visit", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (bookings.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SlateSubtle,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.EventBusy, contentDescription = null, tint = TextMuted, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No appointments booked yet.", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("Choose a verified nurse from the Find a Nurse tab to schedule home care.", fontSize = 11.sp, color = TextSecondary)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(bookings) { b ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder)
                        ),
                        shadowElevation = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = when (b.status) {
                                        BookingStatus.COMPLETED -> EmeraldMint
                                        BookingStatus.PENDING_ACCEPTANCE -> AmberLight
                                        else -> TealLight
                                    },
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = b.status.labelEn.uppercase(),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (b.status) {
                                            BookingStatus.COMPLETED -> EmeraldDark
                                            BookingStatus.PENDING_ACCEPTANCE -> WarmAmber
                                            else -> CalmingTeal
                                        },
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = "TZS ${b.totalTZS.toString().reversed().chunked(3).joinToString(",").reversed()}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = EmeraldDark
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${b.service.titleEn} (${b.visitType.labelEn})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Nurse: ${b.nurseName} • 📅 ${b.scheduledDate} ${b.scheduledTime}",
                                fontSize = 11.sp,
                                color = CalmingTeal,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Recipient: ${b.recipientName} • 📍 ${b.neighborhood}",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )

                            if (b.status == BookingStatus.COMPLETED && b.clinicalEncounterNote != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { onViewEncounter(b) },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("View Clinical Encounter Note & Vitals", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PatientWalletSection(
    transactions: List<WalletTransaction>,
    language: Language
) {
    val totalEscrow = transactions.filter { it.type == WalletTransactionType.ESCROW_DEPOSIT }.sumOf { it.amountTZS }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0D3B66), // NurVia Sapphire
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "PATIENT ESCROW & PAYMENTS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF00A8E8),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "TZS ${totalEscrow.toString().reversed().chunked(3).joinToString(",").reversed()}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "Funds protected safely in Escrow until home clinical care is signed.",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Payment & Escrow Receipts",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(transactions) { tx ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = tx.type.labelEn, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(text = "${tx.provider.displayName} • Ref: ${tx.reference}", fontSize = 10.sp, color = CalmingTeal)
                            Text(text = tx.timestamp, fontSize = 9.sp, color = TextMuted)
                        }
                        Text(
                            text = "TZS ${tx.amountTZS.toString().reversed().chunked(3).joinToString(",").reversed()}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = if (tx.type == WalletTransactionType.ESCROW_DEPOSIT) EmeraldDark else Color(0xFF0D3B66)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TabPill(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) EmeraldDark else SlateSubtle,
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (isSelected) EmeraldDark else SurfaceBorder
            )
        ),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else TextSecondary,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else TextPrimary
            )
        }
    }
}

@Composable
private fun NurseCard(
    nurse: NurseProfile,
    language: Language,
    onBook: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder)
        ),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(listOf(EmeraldMint, TealLight))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = nurse.avatarInitials,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = EmeraldDark
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = nurse.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Surface(
                            color = EmeraldMint,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldDark, modifier = Modifier.size(11.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("TNMC Verified", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = EmeraldDark)
                            }
                        }
                    }

                    Text(
                        text = nurse.cadre,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )

                    Text(
                        text = "Reg: ${nurse.tnmcRegistrationNumber} • ${nurse.experienceYears} Yrs Exp",
                        fontSize = 10.sp,
                        color = CalmingTeal,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (nurse.bio.isNotBlank()) {
                Text(
                    text = nurse.bio,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Specialties chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                nurse.specialties.take(3).forEach { spec ->
                    Surface(
                        color = SlateSubtle,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${spec.icon} ${if (language == Language.EN) spec.titleEn.take(15) else spec.titleSw.take(15)}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = SurfaceBorder)
            Spacer(modifier = Modifier.height(8.dp))

            // Footer with rating and price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = WarmAmber, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${nurse.rating} (${nurse.reviewCount} reviews)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = "TZS ${nurse.hourlyRateTZS.toString().reversed().chunked(3).joinToString(",").reversed()} / visit",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = EmeraldDark
                    )
                }

                Button(
                    onClick = onBook,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("book_nurse_${nurse.id}")
                ) {
                    Text(
                        text = if (language == Language.EN) "Book Nurse" else "Weka Miadi",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
