package com.pawegio.homebudget.main

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.pawegio.homebudget.R

@Composable
fun RoundedProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = colorResource(R.color.progress),
    backgroundColor: Color = colorResource(R.color.progressBackground),
    strokeWidth: Float = 10f
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(strokeWidth.dp)
    ) {
        drawRoundRect(
            color = backgroundColor,
            size = Size(size.width, size.height),
            cornerRadius = CornerRadius(size.height / 2f)
        )
        drawRoundRect(
            color = color,
            size = Size(size.width * progress, size.height),
            cornerRadius = CornerRadius(size.height / 2f)
        )
    }
}
