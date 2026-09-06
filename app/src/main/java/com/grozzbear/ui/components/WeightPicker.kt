package com.grozzbear.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chargemap.compose.numberpicker.NumberPicker
import com.grozzbear.ui.theme.GrozzOnBackground
import com.grozzbear.ui.theme.GrozzYellow
import com.grozzbear.ui.theme.Lexend
import kotlin.math.abs

/** Discrete plate steps shown on the fraction wheel (index → kg). */
val WeightFractionOptions = listOf(0f, 0.5f, 1f, 1.5f, 2f, 2.5f)

/** Whole-kg wheel steps: 0, 5, 10, … 300 */
const val WeightWholeKgStep = 5
val WeightWholeKgRange: IntProgression = 0..300 step WeightWholeKgStep

/** Split a saved Float into whole kg (5 kg steps) + closest fraction index. */
fun splitWeightKg(weight: Float): Pair<Int, Int> {
    val whole = ((weight.toInt() / WeightWholeKgStep) * WeightWholeKgStep)
        .coerceIn(0, 300)
    val remainder = (weight - whole).coerceIn(0f, 2.5f)
    val fractionIndex = WeightFractionOptions.indices.minByOrNull { index ->
        abs(WeightFractionOptions[index] - remainder)
    } ?: 0
    return whole to fractionIndex
}

fun combineWeightKg(wholeKg: Int, fractionIndex: Int): Float {
    val fraction = WeightFractionOptions.getOrElse(fractionIndex) { 0f }
    return wholeKg + fraction
}

fun formatWeightKg(weight: Float): String {
    return if (weight == weight.toInt().toFloat()) {
        weight.toInt().toString()
    } else {
        weight.toString()
    }
}

@Composable
fun IntPickerColumn(
    label: String,
    value: Int,
    range: Iterable<Int>,
    onValueChange: (Int) -> Unit,
    labelForValue: (Int) -> String = { it.toString() }
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            color = GrozzOnBackground,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Lexend
        )
        Spacer(modifier = Modifier.width(8.dp))
        NumberPicker(
            value = value,
            onValueChange = onValueChange,
            range = range,
            label = labelForValue,
            dividersColor = GrozzYellow,
            textStyle = TextStyle(color = GrozzOnBackground)
        )
    }
}
