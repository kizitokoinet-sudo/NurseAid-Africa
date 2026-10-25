package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.EmergencyProtocol
import com.example.model.Language
import com.example.ui.theme.*

@Composable
fun EmergencyDialog(
    isOpen: Boolean,
    protocols: List<EmergencyProtocol>,
    language: Language,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    val context = LocalContext.current
    var selectedProtocolId by remember { mutableStateOf(protocols.firstOrNull()?.id ?: "") }

    fun dialNumber(number: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$number")
        }
        context.startActivity(intent)
    }

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
                // Emergency Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EmergencyRose)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Warning",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (language == Language.EN) "Emergency Clinical Guidance" else "Muongozo wa Dharura ya Kimatibabu",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (language == Language.EN)
                                    "NurseAid Africa is NOT an ambulance dispatch service. For acute life-threatening emergencies, call official services below immediately."
                                else
                                    "NurseAid Africa SI kituo cha magari ya wagonjwa. Kwa dharura inayotishia uhai, piga namba rasmi hapa chini mara moja.",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                lineHeight = 15.sp
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }
                }

                // Official Tanzania Emergency Hotline Buttons
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EmergencyRoseLight.copy(alpha = 0.4f))
                        .padding(14.dp)
                ) {
                    Text(
                        text = if (language == Language.EN) "OFFICIAL TANZANIA EMERGENCY LINES:" else "NAMBA RASMI ZA DHARURA (TANZANIA):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmergencyRoseDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        EmergencyDialCard(
                            label = if (language == Language.EN) "Police / Rescue" else "Polisi & Uokoaji",
                            number = "112",
                            modifier = Modifier.weight(1f),
                            onDial = { dialNumber("112") }
                        )
                        EmergencyDialCard(
                            label = if (language == Language.EN) "Ambulance" else "Gari la Wagonjwa",
                            number = "115",
                            modifier = Modifier.weight(1f),
                            onDial = { dialNumber("115") }
                        )
                        EmergencyDialCard(
                            label = if (language == Language.EN) "Fire & Rescue" else "Zimamoto",
                            number = "114",
                            modifier = Modifier.weight(1f),
                            onDial = { dialNumber("114") }
                        )
                    }
                }

                // Protocol Selector Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    protocols.forEach { proto ->
                        val isSelected = proto.id == selectedProtocolId
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) EmeraldDark else SlateSubtle)
                                .clickable { selectedProtocolId = proto.id }
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = if (language == Language.EN) {
                                    when (proto.id) {
                                        "em-triage" -> "Triage"
                                        "em-cpr" -> "CPR"
                                        "em-bleed" -> "Bleeding"
                                        "em-choke" -> "Choking"
                                        else -> proto.category
                                    }
                                } else {
                                    when (proto.id) {
                                        "em-triage" -> "Dalili"
                                        "em-cpr" -> "CPR"
                                        "em-bleed" -> "Kuvuja Damu"
                                        "em-choke" -> "Kupaliwa"
                                        else -> proto.category
                                    }
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else TextPrimary
                            )
                        }
                    }
                }

                Divider(color = SurfaceBorder)

                // Selected Protocol Content
                val activeProto = protocols.find { it.id == selectedProtocolId } ?: protocols.firstOrNull()

                if (activeProto != null) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text(
                                text = if (language == Language.EN) activeProto.titleEn else activeProto.titleSw,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            // Alert banner
                            Surface(
                                color = AmberLight,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = WarmAmber,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (language == Language.EN) activeProto.keyWarningEn else activeProto.keyWarningSw,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF78350F)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (language == Language.EN) "Step-by-step Emergency Action:" else "Hatua kwa Hatua za Huduma ya Kwanza:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                        }

                        val steps = if (language == Language.EN) activeProto.stepsEn else activeProto.stepsSw
                        items(steps) { stepText ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SlateBackground, RoundedCornerShape(10.dp))
                                    .border(1.dp, SurfaceBorder, RoundedCornerShape(10.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "•",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = EmeraldPrimary,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    text = stepText,
                                    fontSize = 13.sp,
                                    color = TextPrimary,
                                    lineHeight = 18.sp
                                )
                            }
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
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = TextPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = if (language == Language.EN) "Close Guidance" else "Funga Muongozo",
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

@Composable
private fun EmergencyDialCard(
    label: String,
    number: String,
    modifier: Modifier = Modifier,
    onDial: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(EmergencyRose.copy(alpha = 0.5f))
        ),
        shadowElevation = 2.dp,
        modifier = modifier
            .testTag("dial_$number")
            .clickable { onDial() }
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                maxLines = 1
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = EmergencyRose,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = number,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = EmergencyRose
                )
            }
        }
    }
}
