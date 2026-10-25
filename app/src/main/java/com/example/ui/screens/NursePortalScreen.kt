package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun NursePortalScreen(
    nurse: NurseProfile,
    bookings: List<Booking>,
    walletTransactions: List<WalletTransaction> = emptyList(),
    notifications: List<NotificationItem> = emptyList(),
    language: Language,
    activeTab: String,
    onTabChange: (String) -> Unit,
    onUpdateStatus: (bookingId: String, status: BookingStatus) -> Unit,
    onOpenEncounter: (Booking) -> Unit,
    onRequestPayout: (amount: Long, provider: PaymentProvider, phone: String) -> Unit = { _, _, _ -> },
    onMarkNotificationRead: (String) -> Unit = {},
    onOpenNurseReg: () -> Unit = {}
) {
    var isAcceptingCalls by remember { mutableStateOf(nurse.isAvailableNow) }
    var isPayoutDialogOpen by remember { mutableStateOf(false) }

    // Filter bookings belonging to this nurse
    val nurseBookings = bookings.filter { it.nurseId == nurse.id }
    val pendingRequests = nurseBookings.filter { it.status == BookingStatus.PENDING_ACCEPTANCE }
    val activeBookings = nurseBookings.filter {
        it.status in listOf(BookingStatus.ACCEPTED, BookingStatus.CONFIRMED, BookingStatus.IN_PROGRESS)
    }
    val completedBookings = nurseBookings.filter { it.status == BookingStatus.COMPLETED }
    val totalEarnings = completedBookings.sumOf { it.feeTZS }

    // Nurse-relevant notifications
    val nurseAlerts = notifications.filter { it.recipientRole == UserRole.NURSE || it.recipientRole == null }
    val unreadAlertsCount = nurseAlerts.count { !it.isRead }

    // Nurse-relevant wallet transactions
    val nurseTransactions = walletTransactions.filter { it.userId == nurse.id || it.userId == "nurse-kizito" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        // Nurse Profile Header Card
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder)
            ),
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = nurse.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = EmeraldMint,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "TNMC VERIFIED",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDark,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "${nurse.cadre} • Reg: ${nurse.tnmcRegistrationNumber}",
                            fontSize = 11.sp,
                            color = CalmingTeal,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Coverage: ${nurse.serviceAreas.joinToString(", ")}",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }

                    // Earnings Pill
                    Surface(
                        color = EmeraldMint.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text("ESCROW SETTLED", fontSize = 8.sp, fontWeight = FontWeight.Black, color = EmeraldDark)
                            Text(
                                text = "TZS ${totalEarnings.toString().reversed().chunked(3).joinToString(",").reversed()}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = EmeraldDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = SurfaceBorder)
                Spacer(modifier = Modifier.height(8.dp))

                // Availability Switch & Credentials Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isAcceptingCalls) EmeraldPrimary else TextMuted)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAcceptingCalls) "Active on Call & Ready for Visits" else "Currently Offline",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAcceptingCalls) EmeraldDark else TextMuted
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            checked = isAcceptingCalls,
                            onCheckedChange = { isAcceptingCalls = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = EmeraldPrimary,
                                checkedTrackColor = EmeraldMint
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Portal Subtabs (Scrollable if needed or 5 buttons)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SubTabButton(
                title = "Queue (${pendingRequests.size})",
                isSelected = activeTab == "requests",
                modifier = Modifier.weight(1f),
                onClick = { onTabChange("requests") }
            )
            SubTabButton(
                title = "Active (${activeBookings.size})",
                isSelected = activeTab == "active",
                modifier = Modifier.weight(1f),
                onClick = { onTabChange("active") }
            )
            SubTabButton(
                title = "Done (${completedBookings.size})",
                isSelected = activeTab == "completed",
                modifier = Modifier.weight(1f),
                onClick = { onTabChange("completed") }
            )
            SubTabButton(
                title = "Wallet",
                isSelected = activeTab == "wallet",
                modifier = Modifier.weight(1f),
                onClick = { onTabChange("wallet") }
            )
            SubTabButton(
                title = "Alerts${if (unreadAlertsCount > 0) " ($unreadAlertsCount)" else ""}",
                isSelected = activeTab == "notifications",
                modifier = Modifier.weight(1f),
                onClick = { onTabChange("notifications") }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tab Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when (activeTab) {
                "requests" -> {
                    if (pendingRequests.isEmpty()) {
                        item {
                            EmptyPortalCard(
                                message = "No pending assignment requests at this moment.",
                                tip = "Incoming patient requests from Dar es Salaam will appear here."
                            )
                        }
                    } else {
                        items(pendingRequests) { booking ->
                            RequestBookingCard(
                                booking = booking,
                                onAccept = { onUpdateStatus(booking.id, BookingStatus.ACCEPTED) },
                                onDecline = { onUpdateStatus(booking.id, BookingStatus.DECLINED) }
                            )
                        }
                    }
                }
                "active" -> {
                    if (activeBookings.isEmpty()) {
                        item {
                            EmptyPortalCard(
                                message = "No active clinical appointments in progress.",
                                tip = "Accepted assignments appear here for transit and clinical encounter logging."
                            )
                        }
                    } else {
                        items(activeBookings) { booking ->
                            ActiveBookingCard(
                                booking = booking,
                                onRecordEncounter = { onOpenEncounter(booking) }
                            )
                        }
                    }
                }
                "completed" -> {
                    if (completedBookings.isEmpty()) {
                        item {
                            EmptyPortalCard(
                                message = "No completed encounters yet.",
                                tip = "Completed and signed clinical notes will be archived here."
                            )
                        }
                    } else {
                        items(completedBookings) { booking ->
                            CompletedBookingCard(booking = booking)
                        }
                    }
                }
                "wallet" -> {
                    item {
                        NurseWalletCard(
                            totalEarnings = totalEarnings,
                            onRequestPayout = { isPayoutDialogOpen = true }
                        )
                    }

                    item {
                        Text(
                            text = "Escrow Settlements & Payout History",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    if (nurseTransactions.isEmpty()) {
                        item {
                            EmptyPortalCard(
                                message = "No wallet transactions recorded yet.",
                                tip = "Escrow releases and mobile money payouts will appear in this ledger."
                            )
                        }
                    } else {
                        items(nurseTransactions) { tx ->
                            NurseWalletTxRow(transaction = tx)
                        }
                    }
                }
                "notifications" -> {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Practitioner Regulatory & Platform Alerts",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            if (unreadAlertsCount > 0) {
                                Surface(
                                    color = EmeraldMint,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "$unreadAlertsCount NEW",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldDark,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (nurseAlerts.isEmpty()) {
                        item {
                            EmptyPortalCard(
                                message = "No notifications at this time.",
                                tip = "Statutory reminders, CPD notices, and booking updates will appear here."
                            )
                        }
                    } else {
                        items(nurseAlerts) { alert ->
                            NurseNotificationCard(
                                alert = alert,
                                onMarkRead = { onMarkNotificationRead(alert.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Payout Request Dialog
    if (isPayoutDialogOpen) {
        NursePayoutDialog(
            maxBalanceTZS = totalEarnings,
            onDismiss = { isPayoutDialogOpen = false },
            onSubmit = { amount, provider, phone ->
                onRequestPayout(amount, provider, phone)
                isPayoutDialogOpen = false
            }
        )
    }
}

@Composable
private fun NurseWalletCard(
    totalEarnings: Long,
    onRequestPayout: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = EmeraldDark,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "LICENSED NURSE ESCROW ACCOUNT",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = EmeraldMint,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "TZS ${totalEarnings.toString().reversed().chunked(3).joinToString(",").reversed()}",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Text(
                text = "Funds cleared after clinical encounter note signing and verified by automated audit.",
                fontSize = 11.sp,
                color = EmeraldMint.copy(alpha = 0.9f),
                lineHeight = 15.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onRequestPayout,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("request_nurse_payout_button")
            ) {
                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Request Payout to Mobile Money",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun NurseWalletTxRow(transaction: WalletTransaction) {
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
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (transaction.type == WalletTransactionType.PAYOUT_WITHDRAWAL || transaction.type == WalletTransactionType.ESCROW_RELEASE)
                        EmeraldMint else AmberLight,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (transaction.type == WalletTransactionType.PAYOUT_WITHDRAWAL)
                                Icons.Default.CallMade else Icons.Default.CallReceived,
                            contentDescription = null,
                            tint = if (transaction.type == WalletTransactionType.PAYOUT_WITHDRAWAL)
                                WarmAmber else EmeraldDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "${transaction.type.labelEn} (${transaction.provider.displayName})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${transaction.timestamp} • ${transaction.reference}",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (transaction.type == WalletTransactionType.PAYOUT_WITHDRAWAL) "-" else "+"}TZS ${transaction.amountTZS.toString().reversed().chunked(3).joinToString(",").reversed()}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = if (transaction.type == WalletTransactionType.PAYOUT_WITHDRAWAL) EmergencyRose else EmeraldDark
                )
                Surface(
                    color = if (transaction.status == "SETTLED") EmeraldMint else AmberLight,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = transaction.status,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (transaction.status == "SETTLED") EmeraldDark else WarmAmber,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun NurseNotificationCard(
    alert: NotificationItem,
    onMarkRead: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (alert.isRead) SlateBackground else Color.White,
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(if (alert.isRead) SurfaceBorder else EmeraldPrimary.copy(alpha = 0.5f))
        ),
        shadowElevation = if (alert.isRead) 0.dp else 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    if (!alert.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(EmeraldPrimary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = alert.title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Surface(
                    color = SlateSubtle,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = if (alert.title.contains("TNMC")) "REGULATORY" else "SERVICE",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = CalmingTeal,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = alert.message,
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = alert.timestamp,
                    fontSize = 9.sp,
                    color = TextMuted
                )

                if (!alert.isRead) {
                    TextButton(
                        onClick = onMarkRead,
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                    ) {
                        Text("Mark Read", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldDark)
                    }
                }
            }
        }
    }
}

@Composable
private fun NursePayoutDialog(
    maxBalanceTZS: Long,
    onDismiss: () -> Unit,
    onSubmit: (amount: Long, provider: PaymentProvider, phone: String) -> Unit
) {
    var amountText by remember { mutableStateOf(if (maxBalanceTZS > 0) "25000" else "0") }
    var selectedProvider by remember { mutableStateOf(PaymentProvider.MPESA) }
    var phone by remember { mutableStateOf("0754123456") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Request Escrow Payout",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = "Disburse cleared earnings directly to your registered Tanzanian mobile wallet.",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Amount
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it; errorMessage = null },
                    label = { Text("Amount (TZS)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Provider Choice
                Text("Select Mobile Network:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PaymentProvider.values().forEach { provider ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedProvider == provider) EmeraldDark else SlateSubtle,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedProvider = provider }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = provider.displayName,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedProvider == provider) Color.White else TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Mobile Phone
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it; errorMessage = null },
                    label = { Text("Mobile Money Phone Number") },
                    placeholder = { Text("07XXXXXXXX") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                errorMessage?.let {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(it, fontSize = 11.sp, color = EmergencyRose, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val amt = amountText.toLongOrNull() ?: 0L
                            if (amt <= 0) {
                                errorMessage = "Please enter a valid amount."
                            } else if (phone.length < 9) {
                                errorMessage = "Please enter a valid Tanzanian phone number."
                            } else {
                                onSubmit(amt, selectedProvider, phone)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("confirm_payout_button")
                    ) {
                        Text("Submit Payout", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SubTabButton(
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) EmeraldDark else SlateSubtle,
        modifier = modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else TextSecondary,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun EmptyPortalCard(message: String, tip: String) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SlateSubtle,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Inbox, contentDescription = null, tint = TextMuted, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = message, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(text = tip, fontSize = 11.sp, color = TextSecondary)
        }
    }
}

@Composable
private fun RequestBookingCard(
    booking: Booking,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
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
                    color = EmeraldMint,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = booking.service.titleEn.uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDark,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = "TZS ${booking.feeTZS.toString().reversed().chunked(3).joinToString(",").reversed()}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = EmeraldDark
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Patient: ${booking.recipientName} (${booking.recipientAge} yrs, ${booking.recipientGender})",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "📅 ${booking.scheduledDate} at ${booking.scheduledTime} (${booking.visitType.labelEn})",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = CalmingTeal
            )
            Text(
                text = "📍 ${booking.neighborhood}: ${booking.streetAddress}",
                fontSize = 11.sp,
                color = TextSecondary
            )

            if (booking.clinicalNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = AmberLight,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Notes: ${booking.clinicalNotes}",
                        fontSize = 10.sp,
                        color = Color(0xFF78350F),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onDecline,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("Decline", fontSize = 11.sp, color = EmergencyRose, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("accept_assignment_button")
                ) {
                    Text("Accept Assignment", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ActiveBookingCard(
    booking: Booking,
    onRecordEncounter: () -> Unit
) {
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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "In Progress: ${booking.recipientName}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Surface(
                    color = AmberLight,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "ACTIVE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = WarmAmber,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = "${booking.service.titleEn} • ${booking.scheduledDate} ${booking.scheduledTime}",
                fontSize = 11.sp,
                color = CalmingTeal
            )
            Text(
                text = "Address: ${booking.neighborhood}, ${booking.streetAddress}",
                fontSize = 11.sp,
                color = TextSecondary
            )
            Text(
                text = "Phone: ${booking.patientPhone}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onRecordEncounter,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("record_encounter_button")
            ) {
                Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Record Clinical Encounter & Vitals", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CompletedBookingCard(booking: Booking) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SlateBackground,
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${booking.recipientName} - ${booking.service.titleEn}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "TZS ${booking.feeTZS.toString().reversed().chunked(3).joinToString(",").reversed()}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = EmeraldDark
                )
            }

            Text(
                text = "Completed: ${booking.scheduledDate} • Ref: ${booking.paymentReference}",
                fontSize = 10.sp,
                color = TextMuted
            )

            booking.clinicalEncounterNote?.let { note ->
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = "Vitals: BP: ${note.bloodPressure} | HR: ${note.heartRate} | SpO2: ${note.spo2} | Temp: ${note.temperature}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = CalmingTeal
                        )
                        Text(
                            text = "Assessment: ${note.woundAssessment}",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "Signed: ${note.signedBy} on ${note.signedAt}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}
