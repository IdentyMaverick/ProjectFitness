package ui.mainpages.inside

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grozzbear.R
import com.grozzbear.ui.theme.GrozzBorder
import com.grozzbear.ui.theme.GrozzError
import com.grozzbear.ui.theme.GrozzMuted
import com.grozzbear.ui.theme.GrozzOnBackground
import com.grozzbear.ui.theme.GrozzOnPrimary
import com.grozzbear.ui.theme.GrozzSurface
import com.grozzbear.ui.theme.GrozzSystemBar
import com.grozzbear.ui.theme.GrozzYellow
import com.grozzbear.ui.theme.Lexend
import com.grozzbear.ui.theme.Oswald
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
    val canManage by oldWorkoutDetailsViewModel._flag.collectAsState()
    val workout by oldWorkoutDetailsViewModel.workoutDetails.collectAsState(initial = null)
    val formattedDate by workoutCompleteScreenViewModel.formattedDate.collectAsState()
    val elapsedTime by workoutCompleteScreenViewModel.elapsedTime.collectAsState()
    var showMenuSheet by remember { mutableStateOf(false) }
    val menuSheetState = rememberModalBottomSheetState()

    LaunchedEffect(workout) {
        workout?.let {
            workoutCompleteScreenViewModel.setWorkoutData(
                it.workoutHistory.dateTimestamp,
                it.workoutHistory.totalDuration
            )
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            OldWorkoutDetailsTopBar(
                title = workout?.workoutHistory?.workoutName.orEmpty(),
                canManage = canManage,
                onBack = {
                    oldWorkoutDetailsViewModel.clearTargetUser()
                    navController.popBackStack()
                },
                onMenuClick = { showMenuSheet = true }
            )
        },
        containerColor = GrozzSystemBar,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        val details = workout
        if (details == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = GrozzYellow,
                    strokeWidth = 2.dp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 32.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    val subtitle = listOf(formattedDate, elapsedTime)
                        .filter { it.isNotBlank() }
                        .joinToString(" · ")
                    if (subtitle.isNotBlank()) {
                        Text(
                            text = subtitle,
                            color = GrozzMuted,
                            fontSize = 13.sp,
                            fontFamily = Lexend,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    SummaryCardsRow(details)
                }

                item {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "EXERCISES",
                            color = GrozzOnBackground,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Lexend,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = "${details.exerciseWithSets.size} TOTAL",
                            color = GrozzYellow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Lexend
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(color = GrozzBorder)
                }

                if (details.exerciseWithSets.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No exercises logged",
                                color = GrozzOnBackground,
                                fontSize = 16.sp,
                                fontFamily = Lexend,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "This session doesn’t include exercise details.",
                                color = GrozzMuted,
                                fontSize = 13.sp,
                                fontFamily = Lexend,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(
                        items = details.exerciseWithSets,
                        key = { it.exerciseLog.logId }
                    ) { exerciseData ->
                        ExerciseExpandableCard(exerciseData)
                    }
                }
            }
        }

        if (showMenuSheet) {
            ModalBottomSheet(
                onDismissRequest = { showMenuSheet = false },
                sheetState = menuSheetState,
                containerColor = GrozzSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(bottom = 16.dp)
                ) {
                    MenuItemRow(
                        iconRes = R.drawable.terminate,
                        text = "Delete workout",
                        textColor = GrozzError,
                        onClick = {
                            showMenuSheet = false
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
private fun SummaryCardsRow(workout: WorkoutHistoryFull) {
    val completedSets = workout.exerciseWithSets.flatMap { exercise ->
        val clicked = exercise.setLogs.filter { it.clicked }
        if (clicked.isNotEmpty()) clicked else exercise.setLogs
    }
    val totalVolume = completedSets.sumOf { set ->
        (set.weight * set.reps).toDouble()
    }.toInt()
    val totalSets = completedSets.size
    val durationText = formatDuration(workout.workoutHistory.totalDuration)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SummaryCard(
            label = "DURATION",
            value = durationText,
            iconRes = R.drawable.shutterspeedfilledicon128,
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            label = "VOLUME",
            value = String.format("%,d", totalVolume),
            iconRes = R.drawable.dumbbellicon128,
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            label = "SETS",
            value = totalSets.toString(),
            iconRes = R.drawable.timer10icon128,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SummaryCard(
    label: String,
    value: String,
    iconRes: Int,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .height(112.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(GrozzSurface)
            .border(1.dp, GrozzBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = GrozzYellow,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = label,
            color = GrozzMuted,
            fontSize = 10.sp,
            fontFamily = Lexend,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.4.sp
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            color = GrozzYellow,
            fontSize = 18.sp,
            fontFamily = Lexend,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ExerciseExpandableCard(exerciseData: ExerciseLogWithSets) {
    var expanded by remember { mutableStateOf(false) }
    val completedSets = remember(exerciseData.setLogs) {
        exerciseData.setLogs.filter { it.clicked }
    }
    val bodyPart = exerciseData.exerciseLog.bodyPart.trim()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GrozzSurface)
            .border(1.dp, GrozzBorder, RoundedCornerShape(16.dp))
            .clickable { expanded = !expanded }
            .animateContentSize()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(GrozzYellow),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.dumbbellicon128),
                    contentDescription = null,
                    tint = GrozzOnPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exerciseData.exerciseLog.exerciseName,
                    color = GrozzOnBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = Lexend,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (bodyPart.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = bodyPart.uppercase(),
                        color = GrozzYellow,
                        fontSize = 12.sp,
                        fontFamily = Lexend,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (!expanded) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "${completedSets.size} sets",
                        color = GrozzMuted,
                        fontSize = 12.sp,
                        fontFamily = Lexend
                    )
                }
            }

            Icon(
                imageVector = if (expanded) {
                    Icons.Default.KeyboardArrowUp
                } else {
                    Icons.Default.KeyboardArrowDown
                },
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = GrozzYellow,
                modifier = Modifier.size(24.dp)
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column {
                Spacer(Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    SetHeaderCell("SET", Modifier.weight(1f), TextAlign.Start)
                    SetHeaderCell("KG", Modifier.weight(1f), TextAlign.Center)
                    SetHeaderCell("REPS", Modifier.weight(1f), TextAlign.Center)
                }
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(color = GrozzBorder)
                Spacer(Modifier.height(4.dp))

                if (completedSets.isEmpty()) {
                    Text(
                        text = "No completed sets",
                        color = GrozzMuted,
                        fontSize = 13.sp,
                        fontFamily = Lexend,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    completedSets.forEach { set ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${set.setIndex + 1}",
                                color = GrozzOnBackground,
                                fontSize = 15.sp,
                                fontFamily = Lexend,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = formatWeight(set.weight),
                                color = GrozzOnBackground,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Lexend,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "${set.reps}",
                                color = GrozzOnBackground,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Lexend,
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

@Composable
private fun SetHeaderCell(
    text: String,
    modifier: Modifier = Modifier,
    align: TextAlign
) {
    Text(
        text = text,
        color = GrozzMuted,
        fontSize = 11.sp,
        fontFamily = Lexend,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp,
        modifier = modifier,
        textAlign = align
    )
}

@Composable
private fun OldWorkoutDetailsTopBar(
    title: String,
    canManage: Boolean,
    onBack: () -> Unit,
    onMenuClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .height(56.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                painter = painterResource(R.drawable.left),
                contentDescription = "Back",
                modifier = Modifier.size(24.dp),
                tint = GrozzOnBackground
            )
        }

        Text(
            text = title.ifBlank { "WORKOUT" }.uppercase(),
            color = GrozzOnBackground,
            fontFamily = Oswald,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 56.dp)
                .fillMaxWidth()
        )

        if (canManage) {
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    painter = painterResource(R.drawable.projectfitnesspointheavy),
                    contentDescription = "Menu",
                    modifier = Modifier.size(22.dp),
                    tint = GrozzOnBackground
                )
            }
        } else {
            Spacer(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(48.dp)
            )
        }
    }
}

private fun formatDuration(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    return if (h > 0) "${h}h ${m}m" else "${m}m"
}

private fun formatWeight(weight: Float): String {
    return if (weight % 1f == 0f) {
        weight.toInt().toString()
    } else {
        "%.1f".format(weight)
    }
}
