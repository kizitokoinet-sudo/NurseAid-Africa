package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.Booking
import com.example.ui.theme.*

@Composable
fun ClinicalEncounterDialog(
    booking: Booking,
    onDismiss: () -> Unit,
    onSubmit: (
        bp: String,
        hr: String,
        spo2: String,
        glucose: String,
        temp: String,
        wound: String,
        procedures: String,
        instructions: String,
        signedBy: String
    ) -> Unit
) {
    var bp by remember { mutableStateOf("120/80 mmHg") }
    var hr by remember { mutableStateOf("72 bpm") }
    var spo2 by remember { mutableStateOf("98%") }
    var glucose by remember { mutableStateOf("5.4 mmol/L") }
    var temp by remember { mutableStateOf("36.8 °C") }
    var woundAssessment by remember { mutableStateOf("Surgical wound clean, edges well-approximated, no signs of purulent discharge. Sterile hydrocolloid dressing applied.") }
    var proceduresPerformed by remember { mutableStateOf("Aseptic wound cleansing with sterile saline, dressing replacement, and vital signs assessment.") }
    var patientInstructions by remember { mutableStateOf("Keep wound area dry for 48 hours. Continue prescribed oral antibiotic course. Monitor for fever >37.5°C.") }
    var signedBy by remember { mutableStateOf("Kizito Koinet Kileu, RN (TNMC/RN/2018/0491)") }

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
                                text = "NURSING ENCOUNTER RECORD",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = EmeraldMint,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Patient: ${booking.recipientDetailsNameSafe()} • ${booking.service.titleEn}",
                                fontSize = 15.sp,
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

                // Scrollable Form
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "1. Clinical Vital Signs",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDark
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = bp,
                            onValueChange = { bp = it },
                            label = { Text("Blood Pressure", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                        )
                        OutlinedTextField(
                            value = hr,
                            onValueChange = { hr = it },
                            label = { Text("Heart Rate", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = spo2,
                            onValueChange = { spo2 = it },
                            label = { Text("SpO2", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                        )
                        OutlinedTextField(
                            value = glucose,
                            onValueChange = { glucose = it },
                            label = { Text("Blood Glucose", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                        )
                        OutlinedTextField(
                            value = temp,
                            onValueChange = { temp = it },
                            label = { Text("Temp", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                        )
                    }

                    Text(
                        text = "2. Wound Bed & Clinical Assessment",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDark
                    )

                    OutlinedTextField(
                        value = woundAssessment,
                        onValueChange = { woundAssessment = it },
                        label = { Text("Assessment Summary", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                    )

                    Text(
                        text = "3. Procedures & Nursing Interventions",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDark
                    )

                    OutlinedTextField(
                        value = proceduresPerformed,
                        onValueChange = { proceduresPerformed = it },
                        label = { Text("Interventions Executed", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                    )

                    Text(
                        text = "4. Patient Education & Home Care Plan",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDark
                    )

                    OutlinedTextField(
                        value = patientInstructions,
                        onValueChange = { patientInstructions = it },
                        label = { Text("Post-Care Instructions", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                    )

                    Text(
                        text = "5. Professional Verification & TNMC Stamp",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDark
                    )

                    OutlinedTextField(
                        value = signedBy,
                        onValueChange = { signedBy = it },
                        label = { Text("Practitioner Full Name & Registration No.", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                    )
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
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel", color = TextSecondary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                onSubmit(
                                    bp, hr, spo2, glucose, temp,
                                    woundAssessment, proceduresPerformed, patientInstructions,
                                    signedBy
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("sign_and_complete_button")
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sign & Complete Encounter", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

private fun Booking.recipientDetailsNameSafe(): String {
    return if (recipientName.isNotBlank()) recipientName else patientName
}
