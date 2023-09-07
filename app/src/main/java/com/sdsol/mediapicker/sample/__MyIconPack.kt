package com.sdsol.mediapicker.sample

import androidx.compose.ui.graphics.vector.ImageVector
import com.sdsol.mediapicker.sample.myiconpack.`Video-camera-icon`
import kotlin.collections.List as ____KtList

public object MyIconPack

private var __AllIcons: ____KtList<ImageVector>? = null

public val MyIconPack.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= listOf(`Video-camera-icon`)
    return __AllIcons!!
  }
