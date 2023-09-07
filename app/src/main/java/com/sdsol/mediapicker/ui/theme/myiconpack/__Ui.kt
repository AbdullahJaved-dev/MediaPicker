package com.sdsol.mediapicker.ui.theme.myiconpack

import androidx.compose.ui.graphics.vector.ImageVector
import com.sdsol.mediapicker.ui.theme.MyIconPack
import com.sdsol.mediapicker.ui.theme.myiconpack.ui.AllIcons
import com.sdsol.mediapicker.ui.theme.myiconpack.ui.Theme
import kotlin.collections.List as ____KtList

public object UiGroup

public val MyIconPack.Ui: UiGroup
  get() = UiGroup

private var __AllIcons: ____KtList<ImageVector>? = null

public val UiGroup.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= Theme.AllIcons + listOf()
    return __AllIcons!!
  }
