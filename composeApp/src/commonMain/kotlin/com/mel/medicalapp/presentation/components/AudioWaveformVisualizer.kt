package com.mel.medicalapp.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mel.medicalapp.presentation.theme.MelColors

@Composable
fun AudioWaveformVisualizer(
    isRecording: Boolean,
    amplitude: Float,
    barCount: Int = 18
) {
    val infiniteTransition = rememberInfiniteTransition()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until barCount) {
            val animProgress by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 350 + (i * 35), easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            val dynamicHeight = if (isRecording) {
                val factor = ((i % 5) + 1) * 0.18f
                (8.dp + (60.dp * amplitude.coerceAtLeast(0.15f) * animProgress * factor)).coerceIn(8.dp, 75.dp)
            } else {
                6.dp
            }

            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(dynamicHeight)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (isRecording) {
                            if (i % 2 == 0) MelColors.AquaGreen else MelColors.SkyBlue
                        } else {
                            MelColors.BorderSubtle
                        }
                    )
            )
        }
    }
}

