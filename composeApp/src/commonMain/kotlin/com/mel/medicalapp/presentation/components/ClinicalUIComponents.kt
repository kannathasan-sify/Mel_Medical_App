package com.mel.medicalapp.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mel.medicalapp.presentation.theme.MelColors

/**
 * Ambient, calming glowing halo for active audio recording.
 * Avoids aggressive flashing red lights, using a soothing Aqua Green / Sky Blue breathing pulse.
 */
@Composable
fun AmbientRecordingHalo(
    isRecording: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    if (!isRecording) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            content()
        }
        return
    }

    val infiniteTransition = rememberInfiniteTransition()
    val haloScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val haloAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // Outer Breathing Halo
        Box(
            modifier = Modifier
                .size(110.dp * haloScale)
                .clip(CircleShape)
                .background(MelColors.AquaGreen.copy(alpha = haloAlpha))
        )
        // Inner Glow Ring
        Box(
            modifier = Modifier
                .size(92.dp)
                .clip(CircleShape)
                .background(MelColors.AquaGreen.copy(alpha = 0.15f))
                .border(1.5.dp, MelColors.AquaGreen.copy(alpha = 0.5f), CircleShape)
        )
        content()
    }
}

/**
 * Pinned safety banner directing patients to 911 / 112 with zero delay
 */
@Composable
fun EmergencySafetyAnchor(
    emergencyNumber: String = "911",
    onEmergencyCallClicked: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🚨", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Experiencing a medical emergency?",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF991B1B)
                    )
                    Text(
                        text = "Call emergency services immediately.",
                        fontSize = 11.sp,
                        color = Color(0xFF7F1D1D)
                    )
                }
            }
            Button(
                onClick = onEmergencyCallClicked,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Text(
                    text = "Call $emergencyNumber",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color.White
                )
            }
        }
    }
}

