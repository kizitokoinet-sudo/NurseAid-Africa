package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.window.DialogProperties
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun BookingDialog(
    nurse: NurseProfile,
    language: Language,
    isProcessing: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onConfirmBooking: (
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
        phone: String
    ) -> Unit
) {
    var step by remember { mutableStateOf(1) }
    var selectedService by remember { mutableStateOf(nurse.specialties.firstOrNull() ?: ServiceCategory.HOME_CARE) }
    var selectedVisitType by remember { mutableStateOf(VisitType.HOME_VISIT) }
    var scheduledDate by remember { mutableStateOf("2026-10-27") }
    var scheduledTime by remember { mutableStateOf("10:00") }

    // Recipient Details
    var isSelf by remember { mutableStateOf(true) }
    var recipientName by remember { mutableStateOf("Amina Salum") }
    var recipientAgeText by remember { mutableStateOf("34") }
    var recipientGender by remember { mutableStateOf("Female") }
    var selectedNeighborhood by remember { mutableStateOf(nurse.serviceAreas.firstOrNull() ?: "Kinondoni") }
    var streetAddress by remember { mutableStateOf("Mwaya Road, House 14 near St. Peter's") }
    var clinicalNotes by remember { mutableStateOf("") }

    // Payment Details
    var paymentProvider by remember { mutableStateOf(PaymentProvider.MPESA) }
    var phoneNumber by remember { mutableStateOf("0754123456") }
    var localValidationErr by remember { mutableStateOf<String?>(null) }

    val platformFee = 5000L
    val totalAmount = nurse.hourlyRateTZS + platformFee

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .padding(vertical = 16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EmeraldDark)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "STEP $step OF 3",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = EmeraldMint,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = when (step) {
                                    1 -> if (language == Language.EN) "Select Service & Schedule" else "Chagua Huduma na Muda"
                                    2 -> if (language == Language.EN) "Patient & Address (Tanzania)" else "Mpokea Huduma na Mahali"
                                    else -> if (language == Language.EN) "Escrow & Mobile Money" else "Kagua na Lipa Salama"
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                // Error alert
                val displayedError = localValidationErr ?: errorMessage
                if (displayedError != null) {
                    Surface(
                        color = EmergencyRoseLight,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Error, contentDescription = null, tint = EmergencyRose, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = displayedError,
                                fontSize = 11.sp,
                                color = EmergencyRoseDark,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Scrollable Form Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (step == 1) {
                        // Practitioner info
                        Surface(
                            color = SlateBackground,
                            shape = RoundedCornerShape(12.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(EmeraldMint),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = nurse.avatarInitials,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldDark
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = nurse.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = "${nurse.cadre} • ${nurse.tnmcRegistrationNumber}", fontSize = 11.sp, color = CalmingTeal)
                                    Text(
                                        text = "Rate: TZS ${nurse.hourlyRateTZS.toString().reversed().chunked(3).joinToString(",").reversed()}/session",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldDark
                                    )
                                }
                            }
                        }

                        // Service Category Selector
                        Column {
                            Text(
                                text = if (language == Language.EN) "Clinical Service Required" else "Aina ya Huduma ya Uuguzi",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            nurse.specialties.forEach { spec ->
                                val isSelected = spec == selectedService
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) EmeraldMint.copy(alpha = 0.6f) else Color.White,
                                    border = ButtonDefaults.outlinedButtonBorder.copy(
                                        brush = androidx.compose.ui.graphics.SolidColor(
                                            if (isSelected) EmeraldPrimary else SurfaceBorder
                                        )
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                        .clickable { selectedService = spec }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = spec.icon, fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = if (language == Language.EN) spec.titleEn else spec.titleSw,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) EmeraldDark else TextPrimary
                                            )
                                        }
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { selectedService = spec },
                                            colors = RadioButtonDefaults.colors(selectedColor = EmeraldPrimary)
                                        )
                                    }
                                }
                            }
                        }

                        // Visit Type
                        Column {
                            Text(
                                text = if (language == Language.EN) "Mode of Consultation" else "Aina ya Uonano",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(VisitType.HOME_VISIT, VisitType.TELEHEALTH_CALL).forEach { type ->
                                    val isSelected = type == selectedVisitType
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) EmeraldMint.copy(alpha = 0.5f) else Color.White,
                                        border = ButtonDefaults.outlinedButtonBorder.copy(
                                            brush = androidx.compose.ui.graphics.SolidColor(
                                                if (isSelected) EmeraldPrimary else SurfaceBorder
                                            )
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectedVisitType = type }
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(10.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = if (type == VisitType.HOME_VISIT) Icons.Default.Home else Icons.Default.Videocam,
                                                contentDescription = null,
                                                tint = if (isSelected) EmeraldDark else TextSecondary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = if (language == Language.EN) type.labelEn else type.labelSw,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) EmeraldDark else TextPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Date & Time inputs
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = scheduledDate,
                                onValueChange = { scheduledDate = it },
                                label = { Text("Date (YYYY-MM-DD)", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                            )
                            OutlinedTextField(
                                value = scheduledTime,
                                onValueChange = { scheduledTime = it },
                                label = { Text("Time (HH:mm)", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                            )
                        }
                    }

                    if (step == 2) {
                        // Care Recipient
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SlateBackground, RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Checkbox(
                                checked = isSelf,
                                onCheckedChange = {
                                    isSelf = it
                                    if (it) {
                                        recipientName = "Amina Salum"
                                        recipientAgeText = "34"
                                        recipientGender = "Female"
                                    }
                                },
                                colors = CheckboxDefaults.colors(checkedColor = EmeraldPrimary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == Language.EN) "Care is for myself (Amina Salum)" else "Huduma ni kwa ajili yangu mwenyewe",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        if (!isSelf) {
                            OutlinedTextField(
                                value = recipientName,
                                onValueChange = { recipientName = it },
                                label = { Text("Patient Full Name", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = recipientAgeText,
                                    onValueChange = { recipientAgeText = it },
                                    label = { Text("Age (Years)", fontSize = 11.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                                )
                                OutlinedTextField(
                                    value = recipientGender,
                                    onValueChange = { recipientGender = it },
                                    label = { Text("Gender", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f),
                                    textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                                )
                            }
                        }

                        // Neighborhood & Address
                        Column {
                            Text(
                                text = if (language == Language.EN) "Dar es Salaam Neighborhood / Area" else "Eneo la Dar es Salaam",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                nurse.serviceAreas.take(4).forEach { area ->
                                    val isSelected = area == selectedNeighborhood
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) EmeraldDark else SlateSubtle)
                                            .clickable { selectedNeighborhood = area }
                                            .padding(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = area,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else TextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = streetAddress,
                            onValueChange = { streetAddress = it },
                            label = { Text("Street Address & Landmarks", fontSize = 11.sp) },
                            placeholder = { Text("e.g. Mwaya Road, White gate opposite St. Peter's Church") },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                        )

                        OutlinedTextField(
                            value = clinicalNotes,
                            onValueChange = { clinicalNotes = it },
                            label = { Text("Clinical Concerns / Patient History (Optional)", fontSize = 11.sp) },
                            placeholder = { Text("e.g. Post-op wound check, allergies to penicillin, blood pressure history...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                        )
                    }

                    if (step == 3) {
                        // Financial Breakdown
                        Surface(
                            color = SlateBackground,
                            shape = RoundedCornerShape(12.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Nurse Professional Clinical Fee:", fontSize = 12.sp, color = TextSecondary)
                                    Text("TZS ${nurse.hourlyRateTZS.toString().reversed().chunked(3).joinToString(",").reversed()}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Clinical Escrow & Platform Support:", fontSize = 12.sp, color = TextSecondary)
                                    Text("TZS 5,000", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Divider(color = SurfaceBorder, modifier = Modifier.padding(vertical = 4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Total Escrow Protected:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text(
                                        text = "TZS ${totalAmount.toString().reversed().chunked(3).joinToString(",").reversed()}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = EmeraldPrimary
                                    )
                                }
                                Text(
                                    text = "Funds remain safely protected in Escrow until the nurse completes and signs the clinical encounter.",
                                    fontSize = 10.sp,
                                    color = CalmingTeal,
                                    lineHeight = 14.sp
                                )
                            }
                        }

                        // Mobile Money Providers
                        Column {
                            Text(
                                text = "Select Mobile Money Provider (Tanzania):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                PaymentProvider.values().forEach { provider ->
                                    val isSelected = provider == paymentProvider
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) EmeraldMint.copy(alpha = 0.5f) else Color.White,
                                        border = ButtonDefaults.outlinedButtonBorder.copy(
                                            brush = androidx.compose.ui.graphics.SolidColor(
                                                if (isSelected) EmeraldPrimary else SurfaceBorder
                                            )
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { paymentProvider = provider }
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = when (provider) {
                                                    PaymentProvider.MPESA -> "M-Pesa"
                                                    PaymentProvider.AIRTEL_MONEY -> "Airtel"
                                                    PaymentProvider.TIGOPESA -> "Tigo"
                                                },
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Black,
                                                color = if (isSelected) EmeraldDark else TextPrimary
                                            )
                                            Text(
                                                text = provider.prefix + "...",
                                                fontSize = 10.sp,
                                                color = TextMuted
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Mobile Number
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text("Tanzania Registered Mobile Number", fontSize = 11.sp) },
                            placeholder = { Text("e.g. 0754123456 or +255754123456") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("mobile_number_input"),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                        )
                        Text(
                            text = "A simulated sandbox USSD push prompt will initiate upon clicking authorize.",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }

                // Footer Actions
                Surface(
                    color = SlateBackground,
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (step > 1) {
                            TextButton(
                                onClick = {
                                    localValidationErr = null
                                    step -= 1
                                },
                                enabled = !isProcessing
                            ) {
                                Text("Back", fontWeight = FontWeight.Bold, color = TextSecondary)
                            }
                        } else {
                            Spacer(modifier = Modifier.width(1.dp))
                        }

                        if (step < 3) {
                            Button(
                                onClick = {
                                    localValidationErr = null
                                    if (step == 2 && streetAddress.isBlank()) {
                                        localValidationErr = "Please specify street address & landmarks for nurse dispatch."
                                        return@Button
                                    }
                                    step += 1
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Continue", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = {
                                    localValidationErr = null
                                    val age = recipientAgeText.toIntOrNull() ?: 30
                                    onConfirmBooking(
                                        selectedService,
                                        selectedVisitType,
                                        scheduledDate,
                                        scheduledTime,
                                        selectedNeighborhood,
                                        streetAddress,
                                        recipientName,
                                        age,
                                        recipientGender,
                                        clinicalNotes,
                                        isSelf,
                                        paymentProvider,
                                        phoneNumber
                                    )
                                },
                                enabled = !isProcessing,
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("confirm_and_pay_button")
                            ) {
                                if (isProcessing) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Validating Escrow...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                } else {
                                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Authorize TZS ${totalAmount.toString().reversed().chunked(3).joinToString(",").reversed()}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
