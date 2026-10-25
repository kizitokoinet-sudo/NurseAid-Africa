package com.example.ui.components

import androidx.compose.foundation.background
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
import com.example.model.Language
import com.example.model.ServiceCategory
import com.example.ui.theme.*

@Composable
fun NurseRegistrationDialog(
    language: Language,
    onDismiss: () -> Unit,
    onSubmit: (
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
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("07") }
    var cadre by remember { mutableStateOf("Registered Nurse (RN)") }
    var tnmcReg by remember { mutableStateOf("TNMC/RN/") }
    var expYearsText by remember { mutableStateOf("4") }
    var qualificationsText by remember { mutableStateOf("BSc in Nursing - MUHAS, BLS/ACLS Certified") }
    var rateText by remember { mutableStateOf("30000") }
    var bio by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val cadres = listOf("Registered Nurse (RN)", "Nurse Midwife (RN-M)", "Enrolled Nurse (EN)", "Critical Care Specialist")
    val allAreas = listOf("Kinondoni", "Mikocheni", "Oysterbay", "Upanga", "Ilala", "Mwenge", "Sinza")
    var selectedAreas by remember { mutableStateOf(listOf("Kinondoni", "Mikocheni")) }

    val allSpecialties = listOf(
        ServiceCategory.HOME_CARE,
        ServiceCategory.WOUND_DRESSING,
        ServiceCategory.MATERNAL_CHILD,
        ServiceCategory.POST_OPERATIVE,
        ServiceCategory.VITAL_SIGNS
    )
    var selectedSpecialties by remember { mutableStateOf(listOf(ServiceCategory.HOME_CARE, ServiceCategory.WOUND_DRESSING)) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
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
                                text = "HEALTHCARE PRACTITIONER APPLICATION",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = EmeraldMint,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = if (language == Language.EN) "Join NurseAid Africa Clinical Network" else "Jiunge na Mtandao wa Wauguzi",
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

                if (errorMessage != null) {
                    Surface(
                        color = EmergencyRoseLight,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmergencyRoseDark,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                // Form body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Legal Practitioner Name", fontSize = 11.sp) },
                        placeholder = { Text("e.g. Flora M. Kavishe, RN") },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Professional Email", fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.weight(1f),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                        )
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Mobile Phone", fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1f),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                        )
                    }

                    // Cadre
                    Text(text = "Clinical Cadre:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        cadres.forEach { c ->
                            val isSelected = c == cadre
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) EmeraldDark else SlateSubtle)
                                    .clickable { cadre = c }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = c.replace("Specialist", "Spec"),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else TextSecondary
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = tnmcReg,
                            onValueChange = { tnmcReg = it },
                            label = { Text("TNMC Registration Number", fontSize = 11.sp) },
                            placeholder = { Text("TNMC/RN/2022/XXXX") },
                            modifier = Modifier.weight(1.3f),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                        )
                        OutlinedTextField(
                            value = expYearsText,
                            onValueChange = { expYearsText = it },
                            label = { Text("Years Exp", fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(0.7f),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                        )
                    }

                    OutlinedTextField(
                        value = qualificationsText,
                        onValueChange = { qualificationsText = it },
                        label = { Text("Academic Credentials & Certifications", fontSize = 11.sp) },
                        placeholder = { Text("e.g. BSc Nursing MUHAS, BLS/ACLS, Wound Care Cert") },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                    )

                    OutlinedTextField(
                        value = rateText,
                        onValueChange = { rateText = it },
                        label = { Text("Standard Hourly / Visit Rate (TZS)", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                    )

                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Professional Bio & Clinical Focus", fontSize = 11.sp) },
                        placeholder = { Text("Describe your clinical background, hospital affiliations, and patient care approach...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                    )

                    // Statutory Notice
                    Surface(
                        color = AmberLight,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = WarmAmber, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Under TNMC Act No. 12 of 2010, your Annual Practicing License will be independently audited by Clinical Operations before patient dispatch is activated.",
                                fontSize = 10.sp,
                                color = Color(0xFF78350F),
                                lineHeight = 13.sp
                            )
                        }
                    }
                }

                // Footer
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
                        TextButton(onClick = onDismiss) {
                            Text("Cancel", color = TextSecondary)
                        }

                        Button(
                            onClick = {
                                if (name.isBlank() || tnmcReg.length < 8) {
                                    errorMessage = "Please enter full practitioner name and valid TNMC registration number."
                                    return@Button
                                }
                                val exp = expYearsText.toIntOrNull() ?: 3
                                val rate = rateText.toLongOrNull() ?: 30000L
                                val quals = qualificationsText.split(",").map { it.trim() }.filter { it.isNotBlank() }
                                onSubmit(
                                    name, email, phone, cadre, tnmcReg, exp, quals,
                                    15, selectedAreas, selectedSpecialties, rate, bio
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("submit_nurse_reg_button")
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Submit for Verification", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
