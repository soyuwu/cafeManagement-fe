package com.se114.cafe.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.sp
import com.se114.cafe.R

private val vietnam = FontFamily(
    Font(R.font.be_vietnam_pro_regular),
    Font(R.font.be_vietnam_pro_medium, FontWeight.Medium),
    Font(R.font.be_vietnam_pro_semibold, FontWeight.SemiBold),
    Font(R.font.be_vietnam_pro_bold, FontWeight.Bold)
)

private fun style(size: Int, weight: FontWeight) =
    TextStyle(fontFamily = vietnam, fontWeight = weight, fontSize = size.sp, lineHeight = (size + 6).sp)

val Typography = Typography(
    headlineSmall = style(24, FontWeight.Bold), titleLarge = style(20, FontWeight.Bold),
    titleMedium = style(16, FontWeight.SemiBold), bodyLarge = style(16, FontWeight.Normal),
    bodyMedium = style(14, FontWeight.Normal), labelLarge = style(14, FontWeight.SemiBold),
    labelMedium = style(12, FontWeight.SemiBold), bodySmall = style(12, FontWeight.Normal)
)
