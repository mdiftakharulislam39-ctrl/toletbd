package com.pronaycoding.toletapp.ui.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun WelcomeIllustration(
    modifier: Modifier = Modifier,
    primary: Color = MaterialTheme.colorScheme.primary,
    onPrimary: Color = MaterialTheme.colorScheme.onPrimary,
    accent: Color = MaterialTheme.colorScheme.primaryContainer,
) {
    Canvas(modifier = modifier.size(240.dp)) {
        val w = size.width
        val h = size.height
        val stroke = 3.5f

        drawCircle(
            color = onPrimary.copy(alpha = 0.14f),
            radius = w * 0.42f,
            center = Offset(w * 0.5f, h * 0.46f),
        )

        drawCircle(
            color = accent.copy(alpha = 0.55f),
            radius = w * 0.09f,
            center = Offset(w * 0.78f, h * 0.2f),
        )

        val houseLeft = w * 0.22f
        val houseTop = h * 0.34f
        val houseWidth = w * 0.56f
        val houseHeight = h * 0.4f

        val roofPath = Path().apply {
            moveTo(houseLeft - w * 0.04f, houseTop + houseHeight * 0.08f)
            lineTo(houseLeft + houseWidth * 0.5f, houseTop - houseHeight * 0.18f)
            lineTo(houseLeft + houseWidth + w * 0.04f, houseTop + houseHeight * 0.08f)
            close()
        }
        drawPath(roofPath, color = primary)
        drawPath(roofPath, color = onPrimary.copy(alpha = 0.35f), style = Stroke(width = stroke))

        drawRoundRect(
            color = onPrimary.copy(alpha = 0.92f),
            topLeft = Offset(houseLeft, houseTop + houseHeight * 0.06f),
            size = Size(houseWidth, houseHeight),
            cornerRadius = CornerRadius(18f, 18f),
        )
        drawRoundRect(
            color = primary.copy(alpha = 0.25f),
            topLeft = Offset(houseLeft, houseTop + houseHeight * 0.06f),
            size = Size(houseWidth, houseHeight),
            cornerRadius = CornerRadius(18f, 18f),
            style = Stroke(width = stroke),
        )

        val windowSize = houseWidth * 0.22f
        drawRoundRect(
            color = accent.copy(alpha = 0.75f),
            topLeft = Offset(houseLeft + houseWidth * 0.14f, houseTop + houseHeight * 0.2f),
            size = Size(windowSize, windowSize),
            cornerRadius = CornerRadius(8f, 8f),
        )
        drawRoundRect(
            color = accent.copy(alpha = 0.75f),
            topLeft = Offset(houseLeft + houseWidth * 0.64f, houseTop + houseHeight * 0.2f),
            size = Size(windowSize, windowSize),
            cornerRadius = CornerRadius(8f, 8f),
        )

        val doorWidth = houseWidth * 0.24f
        val doorHeight = houseHeight * 0.42f
        drawRoundRect(
            color = primary,
            topLeft = Offset(
                houseLeft + houseWidth * 0.5f - doorWidth * 0.5f,
                houseTop + houseHeight * 0.58f,
            ),
            size = Size(doorWidth, doorHeight),
            cornerRadius = CornerRadius(10f, 10f),
        )

        drawCircle(
            color = onPrimary.copy(alpha = 0.85f),
            radius = 5f,
            center = Offset(
                houseLeft + houseWidth * 0.5f + doorWidth * 0.22f,
                houseTop + houseHeight * 0.78f,
            ),
        )

        val pinX = w * 0.82f
        val pinY = h * 0.62f
        drawCircle(color = primary, radius = w * 0.07f, center = Offset(pinX, pinY))
        drawCircle(color = onPrimary, radius = w * 0.028f, center = Offset(pinX, pinY - w * 0.012f))
        val pinTail = Path().apply {
            moveTo(pinX, pinY + w * 0.04f)
            lineTo(pinX - w * 0.035f, pinY + w * 0.13f)
            lineTo(pinX + w * 0.035f, pinY + w * 0.13f)
            close()
        }
        drawPath(pinTail, color = primary)

        drawRoundRect(
            color = onPrimary.copy(alpha = 0.2f),
            topLeft = Offset(w * 0.08f, h * 0.78f),
            size = Size(w * 0.28f, h * 0.07f),
            cornerRadius = CornerRadius(20f, 20f),
        )
        drawRoundRect(
            color = onPrimary.copy(alpha = 0.14f),
            topLeft = Offset(w * 0.58f, h * 0.84f),
            size = Size(w * 0.3f, h * 0.055f),
            cornerRadius = CornerRadius(20f, 20f),
        )
    }
}

@Composable
fun PhoneIllustration(
    modifier: Modifier = Modifier,
    primary: Color = MaterialTheme.colorScheme.primary,
    onPrimary: Color = MaterialTheme.colorScheme.onPrimary,
    accent: Color = MaterialTheme.colorScheme.primaryContainer,
) {
    Canvas(modifier = modifier.size(180.dp)) {
        val w = size.width
        val h = size.height
        val stroke = 3f
        val phoneLeft = w * 0.28f
        val phoneTop = h * 0.08f
        val phoneW = w * 0.44f
        val phoneH = h * 0.84f

        drawRoundRect(
            color = onPrimary.copy(alpha = 0.15f),
            topLeft = Offset(phoneLeft - 8f, phoneTop - 8f),
            size = Size(phoneW + 16f, phoneH + 16f),
            cornerRadius = CornerRadius(32f, 32f),
        )

        drawRoundRect(
            color = onPrimary.copy(alpha = 0.95f),
            topLeft = Offset(phoneLeft, phoneTop),
            size = Size(phoneW, phoneH),
            cornerRadius = CornerRadius(28f, 28f),
        )
        drawRoundRect(
            color = primary.copy(alpha = 0.3f),
            topLeft = Offset(phoneLeft, phoneTop),
            size = Size(phoneW, phoneH),
            cornerRadius = CornerRadius(28f, 28f),
            style = Stroke(width = stroke),
        )

        drawRoundRect(
            color = accent.copy(alpha = 0.7f),
            topLeft = Offset(phoneLeft + phoneW * 0.18f, phoneTop + phoneH * 0.2f),
            size = Size(phoneW * 0.64f, phoneH * 0.12f),
            cornerRadius = CornerRadius(10f, 10f),
        )

        repeat(3) { index ->
            drawCircle(
                color = primary.copy(alpha = 0.35f + index * 0.12f),
                radius = phoneW * 0.055f,
                center = Offset(
                    phoneLeft + phoneW * (0.32f + index * 0.18f),
                    phoneTop + phoneH * 0.55f,
                ),
            )
        }

        drawRoundRect(
            color = primary,
            topLeft = Offset(phoneLeft + phoneW * 0.28f, phoneTop + phoneH * 0.72f),
            size = Size(phoneW * 0.44f, phoneH * 0.1f),
            cornerRadius = CornerRadius(12f, 12f),
        )
    }
}
