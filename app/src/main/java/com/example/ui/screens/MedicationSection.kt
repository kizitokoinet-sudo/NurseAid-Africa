package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.model.Language
import com.example.model.MedicationItem
import com.example.ui.theme.*

@Composable
fun MedicationSection(
    medications: List<MedicationItem>,
    language: Language,
    onToggleMedication: (String) -> Unit,
    onAddMedication: (name: String, dosage: String, time: String, instructions: String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var medName by remember { mutableStateOf("") }
    var medDosage by remember { mutableStateOf("") }
    var medTime by remember { mutableStateOf("08:00") }
    var medInstructions by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Banner
        Surface(
            color = CalmingTeal,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Medication,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == Language.EN) "Prescription Adherence Tracker" else "Kifuatiliaji cha Kumeza Dawa",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.25f))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (language == Language.EN)
                        "Manage prescribed dosages and mark daily adherence to support clinical recovery."
                    else
                        "Weka kumbukumbu ya dozi za dawa na weka alama ukishameza kwa usalama wako.",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (language == Language.EN) "Today's Schedule" else "Ratiba ya Leo",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "${medications.count { it.isTakenToday }} of ${medications.size} Taken",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = EmeraldPrimary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(medications) { med ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (med.isTakenToday) SlateSubtle else Color.White,
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (med.isTakenToday) EmeraldLight.copy(alpha = 0.3f) else SurfaceBorder
                        )
                    ),
                    shadowElevation = if (med.isTakenToday) 0.dp else 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleMedication(med.id) }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onToggleMedication(med.id) },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (med.isTakenToday) EmeraldPrimary else SlateSubtle)
                        ) {
                            Icon(
                                imageVector = if (med.isTakenToday) Icons.Default.Check else Icons.Default.Schedule,
                                contentDescription = "Status",
                                tint = if (med.isTakenToday) Color.White else TextMuted
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = med.medicationName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = med.scheduleTime,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDark
                                )
                            }
                            Text(
                                text = "Dosage: ${med.dosage}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CalmingTeal
                            )
                            if (med.instructions.isNotBlank()) {
                                Text(
                                    text = med.instructions,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Medication Reminder", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = medName,
                        onValueChange = { medName = it },
                        label = { Text("Medication Name", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = medDosage,
                        onValueChange = { medDosage = it },
                        label = { Text("Dosage (e.g. 500mg)", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = medTime,
                        onValueChange = { medTime = it },
                        label = { Text("Time (e.g. 08:00 & 20:00)", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = medInstructions,
                        onValueChange = { medInstructions = it },
                        label = { Text("Instructions (e.g. after meals)", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (medName.isNotBlank() && medDosage.isNotBlank()) {
                            onAddMedication(medName, medDosage, medTime, medInstructions)
                            showAddDialog = false
                            medName = ""
                            medDosage = ""
                            medInstructions = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Save Reminder")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
