package com.example.projectfitness.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.projectfitness.R

val poppins = FontFamily(Font(R.font.poppinsboldtext),Font(R.font.poppinslighttext),Font(R.font.poppinsregulartext))

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.poppinsboldtext)),
        fontWeight = FontWeight.Normal,
        fontSize = 40.sp),
    bodyMedium = TextStyle(
        fontFamily=FontFamily(Font(R.font.poppinsregulartext)),
        fontWeight = FontWeight.Normal,
        fontSize = 40.sp),
    bodySmall = TextStyle(
        fontFamily = FontFamily(Font(R.font.poppinslighttext)),
        fontWeight = FontWeight.Normal,
        fontSize = 40.sp
    )







/*bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
     Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)
