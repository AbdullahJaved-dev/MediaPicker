package com.sdsol.mediapicker.sample.myiconpack

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sdsol.mediapicker.sample.MyIconPack

public val MyIconPack.`Video-camera-icon`: ImageVector
    get() {
        if (`_video-camera-icon` != null) {
            return `_video-camera-icon`!!
        }
        `_video-camera-icon` = Builder(
            name = "Video-camera-icon", defaultWidth = 100.0.dp,
            defaultHeight = 40.213.dp, viewportWidth = 100.0f, viewportHeight = 40.213f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(75.344f, 20.106f)
                lineToRelative(0.0f, 20.107f)
                lineToRelative(-75.344f, 0.0f)
                lineToRelative(0.0f, -40.213f)
                lineToRelative(75.344f, 0.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(75.344f, 20.106f)
                lineToRelative(24.656f, 16.599f)
                lineToRelative(0.0f, -16.599f)
                lineToRelative(0.0f, -16.598f)
                close()
            }
        }
            .build()
        return `_video-camera-icon`!!
    }

private var `_video-camera-icon`: ImageVector? = null

@Preview
@Composable
fun PreviewVideoCameraIcon() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = MyIconPack.`Video-camera-icon`, contentDescription = null)
    }

}