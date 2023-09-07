package com.sdsol.mediapicker.ui.theme

import androidx.compose.ui.graphics.vector.ImageVector
import com.sdsol.mediapicker.ui.theme.myiconpack.AllIcons
import com.sdsol.mediapicker.ui.theme.myiconpack.Ui
import com.sdsol.mediapicker.ui.theme.myiconpack.Util
import kotlin.collections.List as ____KtList

public object MyIconPack

private var __AllIcons: ____KtList<ImageVector>? = null

public val MyIconPack.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= Ui.AllIcons + Util.AllIcons + listOf()
    return __AllIcons!!
  }
