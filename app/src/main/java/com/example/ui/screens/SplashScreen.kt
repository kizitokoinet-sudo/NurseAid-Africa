package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Language
import com.example.ui.theme.*
import kotlinx.coroutines.delay

/**
 * App Loading Screen that establishes parent-brand consistency on startup.
 * NurseAid Africa remains the primary clinical service brand at the center,
 * while the official NurVia Africa logo is presented at the base as the umbrella parent brand.
 */
@Composable
fun SplashScreen(
    language: Language,
    onComplete: () -> Unit
) {
    var loadingStep by remember { mutableStateOf(0) }

    // Sequential loading animation steps for high polish
    LaunchedEffect(Unit) {
        delay(400)
        loadingStep = 1
        delay(500)
        loadingStep = 2
        delay(400)
        loadingStep = 3
        delay(300)
        onComplete()
    }

    val stepLabelsEn = listOf(
        "Initializing Clinical Registry...",
        "Connecting TNMC Verified Practitioners...",
        "Securing Mobile Money Escrow Gateway...",
        "Welcome to NurseAid Africa"
    )

    val stepLabelsSw = listOf(
        "Kuanzisha Mfumo wa Kliniki...",
        "Kuunganisha Wauguzi Walioidhinishwa na TNMC...",
        "Kulinda Mfumo wa Malipo ya Simu...",
        "Karibu NurseAid Africa"
    )

    val activeLabels = if (language == Language.EN) stepLabelsEn else stepLabelsSw

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        NurviaSapphireDark,
                        NurviaRoyalBlue,
                        Color(0xFF031633)
                    )
                )
            )
            .testTag("app_loading_screen")
            .clickable { onComplete() } // Allow tapping to proceed immediately
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color.White.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "Tanzania Clinical Network",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                TextButton(
                    onClick = onComplete,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.7f))
                ) {
                    Text(text = if (language == Language.EN) "Skip ›" else "Ruka ›", fontSize = 12.sp)
                }
            }

            // Center: Primary Clinical Brand (NurseAid Africa)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 20.dp)
            ) {
                // NurseAid Africa Glowing Emblem
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(EmeraldDark, EmeraldPrimary, CalmingTeal)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = "NurseAid Africa Medical Shield",
                        tint = Color.White,
                        modifier = Modifier.size(52.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Brand Title: NurseAid Africa
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "NurseAid ",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Africa",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black,
                        color = EmeraldMint,
                        letterSpacing = (-0.5).sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (language == Language.EN)
                        "Frontline Licensed In-Home Nursing & Clinical Care"
                    else
                        "Huduma za Wauguzi Walioidhinishwa Nyumbani",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.82f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Clinical Founder Attribution
                Surface(
                    color = EmeraldDark.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Founded by Kizito Koinet Kileu, RN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EmeraldMint,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }

                Spacer(modifier = Modifier.height(36.dp))

                // Progress Indicator & Status Text
                LinearProgressIndicator(
                    progress = { (loadingStep + 1) / 4f },
                    modifier = Modifier
                        .width(220.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = EmeraldMint,
                    trackColor = Color.White.copy(alpha = 0.2f),
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = activeLabels[loadingStep.coerceIn(0, activeLabels.size - 1)],
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center
                )
            }

            // Bottom: Parent Brand Consistency (NurVia Africa Official Logo & Badge)
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (language == Language.EN)
                            "PARENT HEALTHCARE ECOSYSTEM"
                        else
                            "MFUMO MAMA WA AFYA BARANI AFRIKA",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0D3B66),
                        letterSpacing = 1.2.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Official NurVia Africa Logo Image Asset
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_nurvia_africa_brand),
                            contentDescription = "Official NurVia Africa Logo Asset",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .height(46.dp)
                                .fillMaxWidth(0.75f)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (language == Language.EN)
                            "Empowering Pan-African Clinical Connectivity & Infrastructure"
                        else
                            "Kuwezesha Miundombinu ya Afya na Kliniki Kidijitali",
                        fontSize = 10.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
