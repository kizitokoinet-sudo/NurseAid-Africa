package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.model.Language
import com.example.ui.theme.*

@Composable
fun AboutNurviaDialog(
    language: Language,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .padding(vertical = 16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header with NurVia Africa Parent Brand Presentation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NurviaSapphireDark) // NurVia signature deep sapphire
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            // Official NurVia Africa Logo Asset
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_nurvia_africa_brand),
                                    contentDescription = "NurVia Africa Official Logo",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                        .height(42.dp)
                                )
                            }

                            Text(
                                text = "Parent Ecosystem & Clinical Governance",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NurviaCyanGlow // NurVia Cyan
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

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(18.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Mission overview
                    Surface(
                        color = SlateSubtle,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "UMBRELLA BRAND RELATIONSHIP",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = NurviaRoyalBlue,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (language == Language.EN)
                                    "NurVia Africa is the umbrella parent brand advancing healthcare connectivity, digital health infrastructure, and clinical excellence across the African continent.\n\nNurseAid Africa is its specialized frontline clinical platform in Tanzania, connecting individuals and families with vetted, licensed home care nurses."
                                else
                                    "NurVia Africa ni chapa mama inayoongoza mapinduzi ya huduma za afya na teknolojia ya kidijitali barani Afrika.\n\nNurseAid Africa ni jukwaa la kiutendaji nchini Tanzania linalowaunganisha wananchi na wauguzi walioidhinishwa na Baraza la Wauguzi (TNMC) kwa huduma za nyumbani.",
                                fontSize = 12.sp,
                                color = TextPrimary,
                                lineHeight = 17.sp
                            )
                        }
                    }

                    // Founder Attribution
                    Surface(
                        color = EmeraldMint.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.MedicalServices,
                                contentDescription = null,
                                tint = EmeraldDark,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "NurseAid Africa Clinical Founder",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDark
                                )
                                Text(
                                    text = "Kizito Koinet Kileu, RN",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Registered Nurse • Muhimbili University (MUHAS) Alumnus",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    // Regulatory Standard
                    Text(
                        text = "Statutory Regulatory Standards",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        GovernanceBullet(
                            title = "Tanzania Nurses and Midwives Council (TNMC)",
                            description = "Every nurse on NurseAid Africa is independently audited under Act No. 12 of 2010 to verify active Annual Practicing Licenses (APL)."
                        )
                        GovernanceBullet(
                            title = "Escrow Financial Protection",
                            description = "Patient payments are locked safely in escrow via M-Pesa, Airtel Money, or Tigo Pesa until the nurse signs the clinical encounter note."
                        )
                        GovernanceBullet(
                            title = "Direct Emergency Triage (112 / 115)",
                            description = "Life-threatening acute emergencies are instantly routed to national police and ambulance hotlines with on-device evidence-based first aid."
                        )
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
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D3B66)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Done", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GovernanceBullet(title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SlateBackground, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(text = description, fontSize = 10.sp, color = TextSecondary, lineHeight = 14.sp)
        }
    }
}
