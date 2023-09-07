package com.sdsol.mediapicker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun AnimatedCircularProgressIndicator(
    currentValue: Float = 0f,
    progressBackgroundColor: Color = Color.Black.copy(0.2f),
    progressIndicatorColor: Color = Color.Black,
    cornerRadius: Dp = 16.dp,
    paddingStart: Dp = 16.dp,
    paddingEnd: Dp = 16.dp,
    paddingTop: Dp = 16.dp,
    paddingBottom: Dp = 16.dp
) {

    val progress by remember(currentValue) {
        mutableFloatStateOf(currentValue/100)
    }

    Dialog(
        properties = DialogProperties(
            dismissOnBackPress = false
        ),
        onDismissRequest = {

        }
    ) {
        Surface(
            shape = RoundedCornerShape(cornerRadius)
        ) {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(start = paddingStart, end = paddingEnd, top = paddingTop),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                CircularProgressIndicator(
                    progress = progress,
                    color = progressIndicatorColor,
                    modifier = Modifier
                        .size(60.dp)
                        .border(
                            8.dp,
                            color = progressBackgroundColor,
                            shape = CircleShape
                        ),
                    strokeWidth = 8.dp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    modifier = Modifier
                        .padding(bottom = paddingBottom),
                    text = "Compressing Video...",
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}

private fun DrawScope.drawCircularProgressIndicator(
    startAngle: Float,
    sweep: Float,
    color: Color,
    stroke: Stroke
) {
    val diameterOffset = stroke.width / 2
    val arcDimen = size.width - 2 * diameterOffset
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweep,
        useCenter = false,
        topLeft = Offset(diameterOffset, diameterOffset),
        size = Size(arcDimen, arcDimen),
        style = stroke
    )
}

@Preview
@Composable
private fun DialogLoadingPreview() {
    AnimatedCircularProgressIndicator(0.5f)
}