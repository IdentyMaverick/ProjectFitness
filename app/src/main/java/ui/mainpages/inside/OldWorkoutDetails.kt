package ui.mainpages.inside

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grozzbear.R
import data.local.entity.ExerciseLogWithSets
import data.local.entity.WorkoutHistoryFull
import data.local.viewmodel.OldWorkoutDetailsViewModel
import data.local.viewmodel.WorkoutCompleteScreenViewModel
import ui.mainpages.mainpages.MenuItemRow

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OldWorkoutDetails(
    navController: NavController,
    oldWorkoutDetailsViewModel: OldWorkoutDetailsViewModel,
    workoutCompleteScreenViewModel: WorkoutCompleteScreenViewModel
) {
    val flag = oldWorkoutDetailsViewModel._flag.collectAsState(initial = false)
    val observedWorkoutState =
        oldWorkoutDetailsViewModel.workoutDetails.collectAsState(initial = null)
    val workout = observedWorkoutState.value
    val showMenuSheet = remember { mutableStateOf(false) }
    val menuSheetState = rememberModalBottomSheetState()


    LaunchedEffect(workout, observedWorkoutState) {
        workout?.let {
            workoutCompleteScreenViewModel.setWorkoutData(
                it.workoutHistory.dateTimestamp,
                it.workoutHistory.totalDuration
            )
        }
    }

    Scaffold(
        topBar = {
            OldWorkoutDetailsTopBar(
                workout,
                navController,
                showMenuSheet,
                flag.value,
                oldWorkoutDetailsViewModel
            )
        },
        containerColor = Color(0xFF121417) // Tam siyah arka plan
    ) { paddingValues ->
        if (workout == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    color = Color(0xFFF1C40F),
                    strokeWidth = 2.dp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(Modifier.height(8.dp))
                    SummaryCardsRow(workout)
                }

                // 2. Egzersiz Listesi
                itemsIndexed(workout.exerciseWithSets) { _, exerciseData ->
                    ExerciseExpandableCard(exerciseData)
                }

                item { Spacer(Modifier.height(24.dp)) }
            }
        }
        if (showMenuSheet.value) {
            ModalBottomSheet(
                onDismissRequest = { showMenuSheet.value = false },
                sheetState = menuSheetState,
                containerColor = Color(0xFF1C2126),
                modifier = Modifier.height(200.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 40.dp)
                ) {
                    MenuItemRow(
                        iconRes = R.drawable.terminate,
                        text = "Delete",
                        textColor = Color(0xFFFF4444),
                        onClick = {
                            oldWorkoutDetailsViewModel.deleteHistorcialWorkoutById()
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryCardsRow(workout: WorkoutHistoryFull) {
    val totalVolume = workout.exerciseWithSets.sumOf { exercise ->
        exercise.setLogs.sumOf { set ->
            (set.weight * set.reps).toDouble()
        }
    }.toInt()

    val totalSets = workout.exerciseWithSets.sumOf { it.setLogs.size }
    val durationText = formatDuration(workout.workoutHistory.totalDuration)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            "DURATION",
            durationText,
            R.drawable.shutterspeedfilledicon128,
            Modifier.weight(1f)
        )

        SummaryCard(
            "VOLUME",
            String.format("%,d", totalVolume),
            R.drawable.dumbbellicon128,
            Modifier.weight(1f)
        )

        SummaryCard("SETS", totalSets.toString(), R.drawable.timer10icon128, Modifier.weight(1f))
    }
}

@Composable
fun SummaryCard(label: String, value: String, iconRes: Int, modifier: Modifier) {
    Box(
        modifier = modifier
            .height(120.dp)
            .background(Color(0xFF121212), RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFF1C40F).copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painterResource(iconRes),
                null,
                tint = Color(0xFFF1C40F).copy(alpha = 0.8f),
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(label, color = Color(0xFF757575), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(
                value,
                color = Color(0xFFF1C40F),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun ExerciseExpandableCard(exerciseData: ExerciseLogWithSets) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF121212), RoundedCornerShape(12.dp))
            .clickable { expanded = !expanded }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(44.dp)
                    .background(Color(0xFFF1C40F), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painterResource(R.drawable.dumbbellicon128),
                    null,
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = exerciseData.exerciseLog.exerciseName.uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "${exerciseData.exerciseLog.bodyPart.uppercase()} • EQUIPMENT",
                    color = Color(0xFF757575),
                    fontSize = 12.sp
                )
            }

            if (!expanded) {
                var isClickedNumber = 0
                exerciseData.setLogs.forEachIndexed { index, entity ->
                    if (entity.clicked) {
                        isClickedNumber += 1
                    }
                }
                Text(
                    "${isClickedNumber} SETS",
                    color = Color(0xFFF1C40F),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Icon(
                painter = painterResource(if (expanded) R.drawable.down else R.drawable.down),
                contentDescription = null,
                tint = Color(0xFFF1C40F),
                modifier = Modifier
                    .size(20.dp)
                    .padding(start = 8.dp)
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column {
                Spacer(Modifier.height(24.dp))
                Row(Modifier.fillMaxWidth()) {
                    Text(
                        "SET",
                        color = Color(0xFF757575),
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "KG",
                        color = Color(0xFF757575),
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "REPS",
                        color = Color(0xFF757575),
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(12.dp))

                exerciseData.setLogs.forEachIndexed { index, set ->
                    if (set.clicked) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "${index + 1}",
                                color = Color.White,
                                fontSize = 15.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                "${set.weight}",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "${set.reps}",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )

                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OldWorkoutDetailsTopBar(
    workout: WorkoutHistoryFull?,
    navController: NavController,
    showMenuSheet: MutableState<Boolean>,
    flag: Boolean = false,
    oldWorkoutDetailsViewModel: OldWorkoutDetailsViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            oldWorkoutDetailsViewModel.clearTargetUser()
            navController.popBackStack()
        }
        ) {
            Icon(
                painter = painterResource(R.drawable.left),
                contentDescription = null,
                modifier = Modifier.size(30.dp),
                tint = Color.White
            )
        }
        Spacer(Modifier.weight(1f))
        Text(
            workout?.workoutHistory?.workoutName?.uppercase() ?: "WORKOUT",
            color = Color.White,
            fontFamily = FontFamily(Font(R.font.oswaldbold)),
            fontSize = 20.sp
        )
        Spacer(Modifier.weight(1f))
        IconButton(onClick = { showMenuSheet.value = true }, enabled = if (flag) true else false) {
            Icon(
                painter = painterResource(R.drawable.projectfitnesspointheavy),
                contentDescription = null,
                modifier = Modifier.size(25.dp),
                tint = if (flag) Color.White else Color.Transparent
            )
        }
    }
}

fun formatDuration(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    return if (h > 0) "${h}h ${m}m" else "${m}m"
}