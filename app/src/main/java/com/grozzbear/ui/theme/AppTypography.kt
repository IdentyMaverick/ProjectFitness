package com.grozzbear.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.grozzbear.R

val Lexend = FontFamily(
    Font(R.font.lexendthin, FontWeight.Thin),
    Font(R.font.lexendextralight, FontWeight.ExtraLight),
    Font(R.font.lexendlight, FontWeight.Light),
    Font(R.font.lexendregular, FontWeight.Normal),
    Font(R.font.lexendmedium, FontWeight.Medium),
    Font(R.font.lexendsemibold, FontWeight.SemiBold),
    Font(R.font.lexendbold, FontWeight.Bold),
    Font(R.font.lexendextrabold, FontWeight.ExtraBold),
    Font(R.font.lexendblack, FontWeight.Black)
)

val Oswald = FontFamily(
    Font(R.font.oswaldextralight, FontWeight.ExtraLight),
    Font(R.font.oswaldlight, FontWeight.Light),
    Font(R.font.oswaldregular, FontWeight.Normal),
    Font(R.font.oswaldmedium, FontWeight.Medium),
    Font(R.font.oswaldsemibold, FontWeight.SemiBold),
    Font(R.font.oswaldbold, FontWeight.Bold)
)

val GrozzTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = Oswald,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        lineHeight = 44.sp
    ),
    displayMedium = TextStyle(
        fontFamily = Oswald,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 34.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = Oswald,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Oswald,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 24.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Lexend,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Lexend,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Lexend,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Lexend,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Lexend,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Lexend,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    labelMedium = TextStyle(
        fontFamily = Lexend,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Lexend,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp
    )
)
