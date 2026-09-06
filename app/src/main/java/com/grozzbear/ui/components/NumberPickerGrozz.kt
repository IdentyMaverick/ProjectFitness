package com.grozzbear.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberPickerGrozz(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    label: String,
    label2: String,
    label3: String,
    value: Int,
    value2: Int,
    value3: Int,
    range: Iterable<Int>,
    range2: Iterable<Int>,
    range3: Iterable<Int>,
    onValueChange: (Int) -> Unit,
    onValueChange2: (Int) -> Unit,
    onValueChange3: (Int) -> Unit,
    labelForValue: (Int) -> String = { it.toString() },
    labelForValue2: (Int) -> String = { it.toString() },
    labelForValue3: (Int) -> String = { it.toString() },
) {
    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() },
        sheetState = sheetState,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .padding(bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NumberPickerUnit(
                    label = label,
                    value = value,
                    range = range,
                    onValueChange = onValueChange,
                    labelForValue = labelForValue,
                    modifier = Modifier.weight(1f),
                )
                NumberPickerUnit(
                    label = label2,
                    value = value2,
                    range = range2,
                    onValueChange = onValueChange2,
                    labelForValue = labelForValue2,
                    modifier = Modifier.weight(1f),
                )
                NumberPickerUnit(
                    label = label3,
                    value = value3,
                    range = range3,
                    onValueChange = onValueChange3,
                    labelForValue = labelForValue3,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun NumberPickerUnit(
    label: String,
    value: Int,
    range: Iterable<Int>,
    onValueChange: (Int) -> Unit,
    labelForValue: (Int) -> String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label,
            color = GrozzOnBackground,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Lexend,
        )
        Spacer(modifier = Modifier.height(8.dp))
        NumberPicker(
            value = value,
            onValueChange = onValueChange,
            range = range,
            label = labelForValue,
            dividersColor = GrozzYellow,
            textStyle = TextStyle(color = GrozzOnBackground),
        )
    }
}
