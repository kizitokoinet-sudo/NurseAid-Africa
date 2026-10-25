package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun AdminPortalScreen(
    nurses: List<NurseProfile>,
    bookings: List<Booking>,
    analytics: BusinessAnalytics,
    language: Language,
    activeTab: String = "audit_queue",
    onTabChange: (String) -> Unit = {},
    onInspectNurse: (NurseProfile) -> Unit
) {
    val pendingNurses = nurses.filter { it.verificationStatus == VerificationStatus.PENDING }
    val verifiedNurses = nurses.filter { it.verificationStatus == VerificationStatus.VERIFIED }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        // Regulatory Oversight Header
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = EmeraldDark,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = EmeraldMint,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Clinical Governance & Business Operations",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Enforcing statutory practitioner licensing standards under TNMC (Act No. 12 of 2010) and monitoring Dar es Salaam clinical economics.",
                    fontSize = 11.sp,
                    color = EmeraldMint.copy(alpha = 0.9f),
                    lineHeight = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Metric Counters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                title = "TNMC Queue",
                count = pendingNurses.size.toString(),
                accent = WarmAmber,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Verified Active",
                count = verifiedNurses.size.toString(),
                accent = EmeraldPrimary,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Total Sessions",
                count = bookings.size.toString(),
                accent = CalmingTeal,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Admin Subtabs: Audit Queue, Analytics, Registry
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AdminSubTab(
                title = "Audit Queue (${pendingNurses.size})",
                isSelected = activeTab == "audit_queue",
                modifier = Modifier.weight(1f),
                onClick = { onTabChange("audit_queue") }
            )
            AdminSubTab(
                title = "Analytics & GBV",
                isSelected = activeTab == "analytics",
                modifier = Modifier.weight(1f),
                onClick = { onTabChange("analytics") }
            )
            AdminSubTab(
                title = "Registry (${verifiedNurses.size})",
                isSelected = activeTab == "verified_registry",
                modifier = Modifier.weight(1f),
                onClick = { onTabChange("verified_registry") }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when (activeTab) {
                "analytics" -> {
                    item {
                        BusinessAnalyticsCard(analytics = analytics)
                    }
                    item {
                        NeighborhoodDemandCard(demandMap = analytics.neighborhoodDemand)
                    }
                    item {
                        PlatformComplianceCard(analytics = analytics)
                    }
                }
                "verified_registry" -> {
                    item {
                        Text(
                            text = "Active Verified Nurse Registry (${verifiedNurses.size})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    items(verifiedNurses) { nurse ->
                        VerifiedNurseRow(
                            nurse = nurse,
                            onInspect = { onInspectNurse(nurse) }
                        )
                    }
                }
                else -> { // "audit_queue"
                    item {
                        Text(
                            text = "Practitioner Credential Audit Queue (${pendingNurses.size})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    if (pendingNurses.isEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SlateSubtle,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier.padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "All submitted nurse credentials have been evaluated and cleared.",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    } else {
                        items(pendingNurses) { nurse ->
                            PendingNurseAuditCard(
                                nurse = nurse,
                                onInspect = { onInspectNurse(nurse) }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Active Verified Registry (${verifiedNurses.size})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    items(verifiedNurses) { nurse ->
                        VerifiedNurseRow(
                            nurse = nurse,
                            onInspect = { onInspectNurse(nurse) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BusinessAnalyticsCard(analytics: BusinessAnalytics) {
    Surface(
        shape = RoundedCornerShape(16.dp),
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Operational Financial Metrics",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Surface(
                    color = EmeraldMint,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "REAL-TIME",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDark,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val completionRate = if (analytics.totalBookingsCount > 0) {
                (analytics.completedBookingsCount.toDouble() / analytics.totalBookingsCount * 100.0)
            } else 100.0
            val nurseSettled = (analytics.totalGrossBookingValueTZS - analytics.platformRevenueTZS).coerceAtLeast(0L)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FinancialMetricBox(
                    title = "Gross Booking Value (GBV)",
                    value = "TZS ${analytics.totalGrossBookingValueTZS.toString().reversed().chunked(3).joinToString(",").reversed()}",
                    subtitle = "Patient Escrow volume",
                    color = EmeraldDark,
                    modifier = Modifier.weight(1f)
                )
                FinancialMetricBox(
                    title = "Platform Net Revenue",
                    value = "TZS ${analytics.platformRevenueTZS.toString().reversed().chunked(3).joinToString(",").reversed()}",
                    subtitle = "Platform service fee",
                    color = CalmingTeal,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FinancialMetricBox(
                    title = "Nurse Escrow Settled",
                    value = "TZS ${nurseSettled.toString().reversed().chunked(3).joinToString(",").reversed()}",
                    subtitle = "Cleared clinical earnings",
                    color = WarmAmber,
                    modifier = Modifier.weight(1f)
                )
                FinancialMetricBox(
                    title = "Clinical Completion Rate",
                    value = "${String.format("%.1f", completionRate)}%",
                    subtitle = "${analytics.completedBookingsCount} of ${analytics.totalBookingsCount} visits signed",
                    color = EmeraldPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun FinancialMetricBox(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SlateBackground,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(modifier = Modifier.height(3.dp))
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Black, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 9.sp, color = TextMuted)
        }
    }
}

@Composable
private fun NeighborhoodDemandCard(demandMap: Map<String, Int>) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder)
        ),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Urban Dar es Salaam Demand Distribution",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Clinical visit volume geographically mapped across city wards.",
                fontSize = 11.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            val maxCount = (demandMap.values.maxOrNull() ?: 1).coerceAtLeast(1)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                demandMap.forEach { (neighborhood, count) ->
                    val fraction = (count.toFloat() / maxCount).coerceIn(0.1f, 1f)
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = neighborhood, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text(text = "$count visits", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldDark)
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(SlateSubtle)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(EmeraldPrimary)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlatformComplianceCard(analytics: BusinessAnalytics) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SlateBackground,
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = EmeraldDark, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Regulatory Compliance & Audit Health",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "• ${analytics.activePractitionersCount} Practitioners Active & Verified with TNMC license.\n• ${analytics.completedBookingsCount} Clinical encounter notes archived.\n• 100% of escrow payments backed by Tanzanian mobile money M-Pesa / Airtel / Tigo sandboxes.",
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun AdminSubTab(
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
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
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
private fun MetricCard(
    title: String,
    count: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder)
        ),
        shadowElevation = 1.dp,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = count, fontSize = 18.sp, fontWeight = FontWeight.Black, color = accent)
        }
    }
}

@Composable
private fun PendingNurseAuditCard(
    nurse: NurseProfile,
    onInspect: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(WarmAmber.copy(alpha = 0.5f))
        ),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nurse.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "${nurse.cadre} • ${nurse.experienceYears} Yrs Exp",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                Text(
                    text = "Claimed TNMC Reg: ${nurse.tnmcRegistrationNumber}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = CalmingTeal
                )
            }

            Button(
                onClick = onInspect,
                colors = ButtonDefaults.buttonColors(containerColor = WarmAmber),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("inspect_nurse_${nurse.id}")
            ) {
                Text("Inspect & Audit", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun VerifiedNurseRow(
    nurse: NurseProfile,
    onInspect: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = SlateBackground,
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onInspect() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = nurse.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = "${nurse.cadre} • ${nurse.tnmcRegistrationNumber}", fontSize = 10.sp, color = TextSecondary)
            }

            Surface(
                color = EmeraldMint,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "ACTIVE LICENSED",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldDark,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
