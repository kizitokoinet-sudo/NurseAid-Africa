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
import com.example.model.NurseProfile
import com.example.model.VerificationStatus
import com.example.ui.theme.*

@Composable
fun NurseAuditDialog(
    nurse: NurseProfile,
    onDismiss: () -> Unit,
    onDecision: (status: VerificationStatus, auditNotes: String) -> Unit
) {
    var auditNotes by remember { mutableStateOf("Verified against the Tanzania Nurses and Midwives Council (TNMC) statutory register. Active Annual Practicing License confirmed valid for current cycle.") }

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
                                text = "TNMC REGULATORY AUDIT & LICENSING",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = EmeraldMint,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = nurse.name,
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

                // Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Verification Overview
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
                                Text("Cadre & Title:", fontSize = 12.sp, color = TextSecondary)
                                Text(nurse.cadre, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("TNMC Registration:", fontSize = 12.sp, color = TextSecondary)
                                Text(nurse.tnmcRegistrationNumber, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CalmingTeal)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Experience:", fontSize = 12.sp, color = TextSecondary)
                                Text("${nurse.experienceYears} Years", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Current Status:", fontSize = 12.sp, color = TextSecondary)
                                Text(
                                    text = nurse.verificationStatus.labelEn,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (nurse.verificationStatus) {
                                        VerificationStatus.VERIFIED -> EmeraldPrimary
                                        VerificationStatus.PENDING -> WarmAmber
                                        else -> EmergencyRose
                                    }
                                )
                            }
                        }
                    }

                    // Uploaded Credential Inspection
                    Text(
                        text = "Document Ledger & Cross-Check",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Surface(
                        color = SlateSubtle,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Annual Practicing License (APL) - Valid through 2026/27", fontSize = 11.sp, color = TextPrimary)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("NIDA National Identification Number Cross-Checked", fontSize = 11.sp, color = TextPrimary)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Academic Degree / Diploma Certificate Authenticated", fontSize = 11.sp, color = TextPrimary)
                            }
                        }
                    }

                    // Audit Notes
                    Text(
                        text = "Compliance Audit Log & Registry Decision",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    OutlinedTextField(
                        value = auditNotes,
                        onValueChange = { auditNotes = it },
                        label = { Text("Audit Ledger Notes", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                    )
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
                        Button(
                            onClick = { onDecision(VerificationStatus.REJECTED, auditNotes) },
                            colors = ButtonDefaults.buttonColors(containerColor = EmergencyRose),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Reject & Flag", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onDecision(VerificationStatus.VERIFIED, auditNotes) },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("approve_nurse_button")
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Approve & Activate", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
