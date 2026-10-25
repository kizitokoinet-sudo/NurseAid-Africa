package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.R
import com.example.model.Language
import com.example.model.UserRole
import com.example.ui.theme.*

@Composable
fun NurseAidTopBar(
    currentRole: UserRole,
    currentLanguage: Language,
    onRoleChange: (UserRole) -> Unit,
    onLanguageChange: (Language) -> Unit,
    onOpenEmergency: () -> Unit,
    onOpenAboutNurvia: () -> Unit = {},
    onOpenPatientReg: () -> Unit = {},
    onOpenNurseReg: () -> Unit = {}
) {
    var roleMenuExpanded by remember { mutableStateOf(false) }

    // Emergency pulsing animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Surface(
        color = Color.White,
        shadowElevation = 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            // Parent Brand & Service Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Logo & Founder Identity + NurVia Africa Parent Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Official NurVia Africa Logo on Top-Left
                    Surface(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onOpenAboutNurvia() },
                        color = Color.White,
                        shadowElevation = 1.dp
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_nurvia_africa_brand),
                            contentDescription = "NurVia Africa Logo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(1.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(9.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "NurseAid ",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = EmeraldDark
                            )
                            Text(
                                text = "Africa",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = CalmingTeal
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // NurVia Africa Parent Pill
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldPrimary.copy(alpha = 0.12f),
                                modifier = Modifier.clickable { onOpenAboutNurvia() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(EmeraldLight)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "NurVia",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldDark
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Founded by Kizito Koinet Kileu, RN",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary,
                            maxLines = 1
                        )
                    }
                }

                // Action Controls: Emergency, Language, Role
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Emergency Button (Pulse)
                    Button(
                        onClick = onOpenEmergency,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmergencyRose.copy(alpha = pulseAlpha)
                        ),
                        contentPadding = PaddingValues(horizontal = 9.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .testTag("emergency_button")
                            .height(34.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (currentLanguage == Language.EN) "112 Alert" else "Dharura 112",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Language Toggle
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SlateSubtle)
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (currentLanguage == Language.EN) Color.White else Color.Transparent)
                                .clickable { onLanguageChange(Language.EN) }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "EN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentLanguage == Language.EN) EmeraldDark else TextMuted
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (currentLanguage == Language.SW) Color.White else Color.Transparent)
                                .clickable { onLanguageChange(Language.SW) }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "SW",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentLanguage == Language.SW) EmeraldDark else TextMuted
                            )
                        }
                    }

                    // Menu & Role Switcher Dropdown
                    Box {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldMint.copy(alpha = 0.7f),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.linearGradient(listOf(EmeraldLight, CalmingTeal))
                            ),
                            modifier = Modifier
                                .clickable { roleMenuExpanded = true }
                                .padding(horizontal = 7.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = when (currentRole) {
                                        UserRole.PATIENT -> Icons.Default.Person
                                        UserRole.NURSE -> Icons.Default.HealthAndSafety
                                        UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                                    },
                                    contentDescription = "Role Icon",
                                    tint = EmeraldDark,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = when (currentRole) {
                                        UserRole.PATIENT -> "Patient"
                                        UserRole.NURSE -> "Nurse"
                                        UserRole.ADMIN -> "Admin"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDark
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    tint = EmeraldDark,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = roleMenuExpanded,
                            onDismissRequest = { roleMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text("Patient: Amina Salum", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("Book home nurses, maternal care, wallet", fontSize = 11.sp, color = TextSecondary)
                                    }
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = EmeraldPrimary)
                                },
                                onClick = {
                                    onRoleChange(UserRole.PATIENT)
                                    roleMenuExpanded = false
                                }
                            )
                            Divider()
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text("Nurse: Kizito Koinet Kileu, RN", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("Assignments, vitals notes, wallet payouts", fontSize = 11.sp, color = TextSecondary)
                                    }
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = CalmingTeal)
                                },
                                onClick = {
                                    onRoleChange(UserRole.NURSE)
                                    roleMenuExpanded = false
                                }
                            )
                            Divider()
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text("Admin: Clinical Operations", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("TNMC licensing queue & business analytics", fontSize = 11.sp, color = TextSecondary)
                                    }
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = WarmAmber)
                                },
                                onClick = {
                                    onRoleChange(UserRole.ADMIN)
                                    roleMenuExpanded = false
                                }
                            )
                            Divider()
                            DropdownMenuItem(
                                text = {
                                    Text("+ Register New Patient Profile", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = EmeraldDark)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = EmeraldPrimary)
                                },
                                onClick = {
                                    roleMenuExpanded = false
                                    onOpenPatientReg()
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text("+ Register as Healthcare Nurse", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CalmingTeal)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.MedicalInformation, contentDescription = null, tint = CalmingTeal)
                                },
                                onClick = {
                                    roleMenuExpanded = false
                                    onOpenNurseReg()
                                }
                            )
                            Divider()
                            DropdownMenuItem(
                                text = {
                                    Text("About NurVia Africa", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0D3B66))
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF0D3B66))
                                },
                                onClick = {
                                    roleMenuExpanded = false
                                    onOpenAboutNurvia()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
