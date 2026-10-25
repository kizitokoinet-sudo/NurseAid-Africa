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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.model.MaternalChildRecord
import com.example.ui.theme.*

@Composable
fun MaternalChildSection(
    records: List<MaternalChildRecord>,
    language: Language,
    onToggleRecord: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Banner
        Surface(
            color = EmeraldDark,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ChildCare,
                        contentDescription = null,
                        tint = EmeraldMint,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == Language.EN) "Maternal & Child Health Portal" else "Huduma ya Afya ya Mama na Mtoto",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (language == Language.EN)
                        "Track antenatal clinical visits, postnatal milestones, and the official Tanzanian national immunization schedule (EPI)."
                    else
                        "Fuatilia kliniki ya ujauzito, maendeleo ya mtoto mchanga, na ratiba ya chanjo ya kitaifa (EPI).",
                    fontSize = 11.sp,
                    color = EmeraldMint.copy(alpha = 0.9f),
                    lineHeight = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = if (language == Language.EN) "Care Schedule & Milestones" else "Ratiba ya Kliniki na Chanjo",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(records) { rec ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (rec.isCompleted) SlateSubtle else Color.White,
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (rec.isCompleted) EmeraldLight.copy(alpha = 0.4f) else SurfaceBorder
                        )
                    ),
                    shadowElevation = if (rec.isCompleted) 0.dp else 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleRecord(rec.id) }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = rec.isCompleted,
                            onCheckedChange = { onToggleRecord(rec.id) },
                            colors = CheckboxDefaults.colors(checkedColor = EmeraldPrimary)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = rec.motherOrChildName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Surface(
                                    color = if (rec.recordType == "Immunization") TealLight else AmberLight,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = rec.recordType.uppercase(),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (rec.recordType == "Immunization") CalmingTeal else WarmAmber,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = rec.stage,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EmeraldDark
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = rec.details,
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 15.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Due Date: ${rec.dueDate} • Status: ${if (rec.isCompleted) "Completed" else "Pending Visit"}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (rec.isCompleted) EmeraldPrimary else TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}
