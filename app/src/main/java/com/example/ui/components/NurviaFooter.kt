package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Language
import com.example.ui.theme.*

/**
 * Persistent Parent-Brand Footer.
 * Displays the official NurVia Africa logo asset, reinforcing the umbrella parent brand
 * relationship while preserving NurseAid Africa's primary clinical role on service screens.
 */
@Composable
fun NurviaFooter(
    language: Language,
    onOpenAboutNurvia: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFFF8FAFC),
        tonalElevation = 1.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("nurvia_footer")
            .border(width = 0.5.dp, color = Color(0xFFE2E8F0))
            .clickable { onOpenAboutNurvia() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Official NurVia Africa Logo Asset + Relationship Copy
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Official transparent PNG rendered with exact aspect ratio
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.size(34.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_nurvia_africa_brand),
                        contentDescription = "Official NurVia Africa Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (language == Language.EN) "POWERED BY " else "CHINI YA ",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "NurVia AFRICA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = NurviaRoyalBlue
                        )
                    }
                    Text(
                        text = if (language == Language.EN)
                            "Pan-African Healthcare Ecosystem & Infrastructure"
                        else
                            "Mfumo Mkuu wa Afya na Maendeleo Barani Afrika",
                        fontSize = 9.5.sp,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right: Interactive button opening parent-brand governance details
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = NurviaRoyalBlue.copy(alpha = 0.08f),
                modifier = Modifier.clip(RoundedCornerShape(6.dp))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = NurviaRoyalBlue,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = if (language == Language.EN) "About" else "Kuhusu",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NurviaRoyalBlue
                    )
                }
            }
        }
    }
}
