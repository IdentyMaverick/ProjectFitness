package ui.mainpages.inside

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grozzbear.R
import com.grozzbear.projectfitness.data.local.entity.SetEntity
import com.grozzbear.ui.components.GrozzPrimaryButton
import com.grozzbear.ui.components.NumberPickerGrozz
import com.grozzbear.ui.components.formatWeightKg
import com.grozzbear.ui.theme.GrozzBorder
import com.grozzbear.ui.theme.GrozzError
import com.grozzbear.ui.theme.GrozzMuted
import com.grozzbear.ui.theme.GrozzOnBackground
import com.grozzbear.ui.theme.GrozzOnPrimary
import com.grozzbear.ui.theme.GrozzRadiusPanel
import com.grozzbear.ui.theme.GrozzSurface
import com.grozzbear.ui.theme.GrozzSystemBar
import com.grozzbear.ui.theme.GrozzTextSecondary
import com.grozzbear.ui.theme.GrozzYellow
import com.grozzbear.ui.theme.Lexend
import com.grozzbear.ui.theme.Oswald
import com.grozzbear.ui.util.counted
import data.local.entity.ExerciseLogWithSets
import data.local.entity.SetLogEntity
import data.local.entity.WorkoutHistoryFull
import data.local.viewmodel.OldWorkoutDetailsViewModel
import data.local.viewmodel.WorkoutCompleteScreenViewModel
import ui.mainpages.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OldWorkoutDetails(
    navController: NavController,
    oldWorkoutDetailsViewModel: OldWorkoutDetailsViewModel,
    workoutCompleteScreenViewModel: WorkoutCompleteScreenViewModel,
) {
    val canManage by oldWorkoutDetailsViewModel.canManage.collectAsState()
    val workout by oldWorkoutDetailsViewModel.workoutDetails.collectAsState(initial = null)
    val formattedDate by workoutCompleteScreenViewModel.formattedDate.collectAsState()
    val elapsedTime by workoutCompleteScreenViewModel.elapsedTime.collectAsState()
    val isEditModeEnabled by oldWorkoutDetailsViewModel.isEditModeEnabled.collectAsState()
    val draft by oldWorkoutDetailsViewModel.draft.collectAsState()
    val displayed = if (isEditModeEnabled) draft else workout

    var showBottomSheet by remember { mutableStateOf(false) }
    var editingSet by remember { mutableStateOf<SetLogEntity?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var durationAdjustFlag = remember { mutableStateOf(false) }
    val durationSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(workout) {
        workout?.let {
            workoutCompleteScreenViewModel.setWorkoutData(
                it.workoutHistory.dateTimestamp,
                it.workoutHistory.totalDuration,
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
                    if (!isEditModeEnabled) {
                        oldWorkoutDetailsViewModel.clearTargetUser()
                        navController.popBackStack()
                    } else {
                        oldWorkoutDetailsViewModel.exitEditMode()
                    }
                },
                onMenuClick = {
                    workout?.let { oldWorkoutDetailsViewModel.enterEditMode(it) }
                },
                onEditClick = { oldWorkoutDetailsViewModel.saveEdits() },
                isEditModeEnabled = isEditModeEnabled,
            )
        },
        containerColor = GrozzSystemBar,
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        val details = displayed
        if (details == null) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    color = GrozzYellow,
                    strokeWidth = 2.dp,
                )
            }
        } else {
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                contentPadding =
                    PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 32.dp,
                    ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    AnimatedVisibility(visible = isEditModeEnabled) {
                        EditModeBanner(
                            modifier = Modifier.padding(bottom = 12.dp),
                        )
                    }
                    val subtitle =
                        listOf(formattedDate, elapsedTime)
                            .filter { it.isNotBlank() }
                            .joinToString(" · ")
                    if (subtitle.isNotBlank()) {
                        Text(
                            text = subtitle,
                            color = GrozzMuted,
                            fontSize = 13.sp,
                            fontFamily = Lexend,
                            fontWeight = FontWeight.Medium,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                            textAlign = TextAlign.Center,
                        )
                    }
                    SummaryCardsRow(
                        details,
                        isEditModeEnabled,
                        durationOnClick = { durationAdjustFlag.value = true },
                        durationText = formatDuration(details.workoutHistory.totalDuration),
                    )
                    if (durationAdjustFlag.value) {
                        val durationParts =
                            formatDuration(details.workoutHistory.totalDuration)
                                .split(':')
                        val hour = durationParts.getOrNull(0)?.toIntOrNull() ?: 0
                        val minute = durationParts.getOrNull(1)?.toIntOrNull() ?: 0
                        val second = durationParts.getOrNull(2)?.toIntOrNull() ?: 0
                        NumberPickerGrozz(
                            sheetState = durationSheetState,
                            onDismissRequest = { durationAdjustFlag.value = false },
                            label = "Hour",
                            value = hour,
                            range = 0..23,
                            onValueChange = { hour ->
                                oldWorkoutDetailsViewModel.updateDraftDuration(hour * 3600L + minute * 60L + second)
                            },
                            label2 = "Minute",
                            value2 = minute,
                            range2 = 0..59,
                            onValueChange2 = { minute ->
                                oldWorkoutDetailsViewModel.updateDraftDuration(hour * 3600L + minute * 60L + second)
                            },
                            label3 = "Second",
                            value3 = durationParts.getOrNull(2)?.toIntOrNull() ?: 0,
                            range3 = 0..59,
                            onValueChange3 = { second ->
                                oldWorkoutDetailsViewModel.updateDraftDuration(hour * 3600L + minute * 60L + second)
                            },
                        )
                    }
                }

                item {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "EXERCISES",
                            color = GrozzOnBackground,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Lexend,
                            letterSpacing = 0.5.sp,
                        )
                        if (isEditModeEnabled) {
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "EDITING",
                                color = GrozzYellow,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Lexend,
                                letterSpacing = 0.6.sp,
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = "${details.exerciseWithSets.size} TOTAL",
                            color = GrozzYellow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Lexend,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(color = GrozzBorder)
                }

                if (details.exerciseWithSets.isEmpty()) {
                    item {
                        Column(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "No exercises logged",
                                color = GrozzOnBackground,
                                fontSize = 16.sp,
                                fontFamily = Lexend,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text =
                                    if (isEditModeEnabled) {
                                        "Add movements to this session."
                                    } else {
                                        "This session doesn’t include exercise details."
                                    },
                                color = GrozzMuted,
                                fontSize = 13.sp,
                                fontFamily = Lexend,
                                textAlign = TextAlign.Center,
                            )
                            if (isEditModeEnabled) {
                                Spacer(Modifier.height(16.dp))
                                GrozzPrimaryButton(
                                    text = "Add Exercise",
                                    onClick = {
                                        navController.navigate(
                                            Screens.ChooseExercises.createRoute(
                                                Screens.ChooseExercises.MODE_HISTORY,
                                            ),
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    }
                } else {
                    itemsIndexed(
                        items = details.exerciseWithSets,
                        key = { _, item -> item.exerciseLog.logId },
                    ) { index, exerciseData ->
                        Box(modifier = Modifier.animateItem()) {
                            ExerciseExpandableCard(
                                exerciseData = exerciseData,
                                isEditMode = isEditModeEnabled,
                                onEditSet = { set ->
                                    editingSet = set
                                    showBottomSheet = true
                                },
                                onAddSet = {
                                    oldWorkoutDetailsViewModel.addDraftSet(exerciseData.exerciseLog.logId)
                                },
                                onDeleteSet = { set -> oldWorkoutDetailsViewModel.deleteDraftSet(set.setId) },
                                canMoveUp = index > 0,
                                canMoveDown = index < details.exerciseWithSets.lastIndex,
                                onMoveUp = { oldWorkoutDetailsViewModel.moveDraftExercises(index, index - 1) },
                                onMoveDown = { oldWorkoutDetailsViewModel.moveDraftExercises(index, index + 1) },
                                onRemove = {},
                            )
                        }
                    }
                }
                if (isEditModeEnabled && details.exerciseWithSets.isNotEmpty()) {
                    item {
                        GrozzPrimaryButton(
                            text = "Add Exercise",
                            onClick = {
                                navController.navigate(
                                    Screens.ChooseExercises.createRoute(
                                        Screens.ChooseExercises.MODE_HISTORY,
                                    ),
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
    if (showBottomSheet) {
        val set = editingSet
        if (set != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                    editingSet = null
                },
                sheetState = sheetState,
                containerColor = GrozzSurface,
                tonalElevation = 0.dp,
            ) {
                EditSetBottomSheetContent(
                    set =
                        SetEntity(
                            setId = set.setId.toString(),
                            exerciseOwnerId = set.logOwnerId.toString(),
                            reps = set.reps,
                            weight = set.weight,
                        ),
                    onSave = { updatedReps, updatedWeight ->
                        oldWorkoutDetailsViewModel.updateDraftSet(
                            set.setId,
                            updatedReps,
                            updatedWeight.toFloat(),
                        )
                        showBottomSheet = false
                        editingSet = null
                    },
                    onDelete = {
                        oldWorkoutDetailsViewModel.deleteDraftSet(set.setId)
                        showBottomSheet = false
                        editingSet = null
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SummaryCardsRow(
    workout: WorkoutHistoryFull,
    isEditMode: Boolean,
    durationOnClick: () -> Unit,
    durationText: String,
) {
    val completedSets =
        workout.exerciseWithSets.flatMap { exercise ->
            if (isEditMode) {
                exercise.setLogs
            } else {
                val clicked = exercise.setLogs.filter { it.clicked }
                if (clicked.isNotEmpty()) clicked else exercise.setLogs
            }
        }
    val totalVolume =
        completedSets
            .sumOf { set ->
                (set.weight * set.reps).toDouble()
            }.toInt()
    val totalSets = completedSets.size

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SummaryCard(
            label = "DURATION",
            value = durationText,
            iconRes = R.drawable.shutterspeedfilledicon128,
            isEditMode = isEditMode,
            modifier = Modifier.weight(1f),
            onClick = { durationOnClick() },
        )
        SummaryCard(
            label = "VOLUME",
            value = String.format("%,d", totalVolume),
            iconRes = R.drawable.dumbbellicon128,
            isEditMode = isEditMode,
            modifier = Modifier.weight(1f),
            onClick = {},
        )
        SummaryCard(
            label = "SETS",
            value = totalSets.toString(),
            iconRes = R.drawable.timer10icon128,
            isEditMode = isEditMode,
            modifier = Modifier.weight(1f),
            onClick = {},
        )
    }
}

@Composable
private fun SummaryCard(
    label: String,
    value: String,
    iconRes: Int,
    isEditMode: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val borderColor by animateColorAsState(
        targetValue = if (isEditMode) GrozzYellow else GrozzBorder,
        label = "summaryBorder",
    )
    Column(
        modifier =
            modifier
                .height(96.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(GrozzSurface)
                .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                .padding(horizontal = 8.dp, vertical = 10.dp)
                .clickable(isEditMode, onClick = { onClick() }),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = GrozzYellow,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = label,
            color = GrozzMuted,
            fontSize = 10.sp,
            fontFamily = Lexend,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.4.sp,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            color = GrozzYellow,
            fontSize = 16.sp,
            fontFamily = Lexend,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun ExerciseExpandableCard(
    exerciseData: ExerciseLogWithSets,
    isEditMode: Boolean,
    onEditSet: (SetLogEntity) -> Unit,
    onAddSet: () -> Unit,
    onDeleteSet: (SetLogEntity) -> Unit,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onRemove: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val visibleSets =
        remember(exerciseData.setLogs, isEditMode) {
            val source =
                if (isEditMode) {
                    exerciseData.setLogs
                } else {
                    val clicked = exerciseData.setLogs.filter { it.clicked }
                    if (clicked.isNotEmpty()) clicked else exerciseData.setLogs
                }
            source.sortedBy { it.setIndex }
        }
    val bodyPart = exerciseData.exerciseLog.bodyPart.trim()
    val borderColor by animateColorAsState(
        targetValue = if (isEditMode) GrozzYellow else GrozzBorder,
        label = "exerciseBorder",
    )

    LaunchedEffect(isEditMode) {
        if (isEditMode) expanded = true
    }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(GrozzRadiusPanel))
                .background(GrozzSurface)
                .border(1.dp, borderColor, RoundedCornerShape(GrozzRadiusPanel))
                .clickable { expanded = !expanded }
                .animateContentSize()
                .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier =
                    Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(GrozzYellow),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.dumbbellicon128),
                    contentDescription = null,
                    tint = GrozzOnPrimary,
                    modifier = Modifier.size(22.dp),
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
                    overflow = TextOverflow.Ellipsis,
                )
                if (bodyPart.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = bodyPart.uppercase(),
                        color = GrozzYellow,
                        fontSize = 12.sp,
                        fontFamily = Lexend,
                        fontWeight = FontWeight.Bold,
                    )
                }
                if (!expanded) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = counted(visibleSets.size, "set"),
                        color = GrozzMuted,
                        fontSize = 12.sp,
                        fontFamily = Lexend,
                    )
                }
            }

            if (isEditMode) {
                IconButton(
                    onClick = onMoveUp,
                    enabled = canMoveUp,
                    modifier = Modifier.size(36.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Move up",
                        tint = if (canMoveUp) GrozzYellow else GrozzMuted,
                        modifier = Modifier.size(22.dp),
                    )
                }
                IconButton(
                    onClick = onMoveDown,
                    enabled = canMoveDown,
                    modifier = Modifier.size(36.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Move down",
                        tint = if (canMoveDown) GrozzYellow else GrozzMuted,
                        modifier = Modifier.size(22.dp),
                    )
                }
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(36.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.terminate),
                        contentDescription = "Remove exercise",
                        tint = GrozzError,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Icon(
                imageVector =
                    if (expanded) {
                        Icons.Default.KeyboardArrowUp
                    } else {
                        Icons.Default.KeyboardArrowDown
                    },
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = GrozzYellow,
                modifier = Modifier.size(24.dp),
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column {
                Spacer(Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    if (isEditMode) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                        ) {
                            Text(
                                "SET",
                                color = GrozzMuted,
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = Lexend,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                "KG",
                                color = GrozzMuted,
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = Lexend,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                            )
                            Text(
                                "REPS",
                                color = GrozzMuted,
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = Lexend,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                            )
                            Text(
                                "ACTION",
                                color = GrozzMuted,
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = Lexend,
                                modifier = Modifier.weight(1.5f),
                                textAlign = TextAlign.End,
                            )
                        }
                    } else {
                        SetHeaderCell("SET", Modifier.weight(1f), TextAlign.Start)
                        SetHeaderCell("KG", Modifier.weight(1f), TextAlign.Center)
                        SetHeaderCell("REPS", Modifier.weight(1f), TextAlign.Center)
                    }
                }
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(color = GrozzBorder)
                Spacer(Modifier.height(4.dp))

                if (visibleSets.isEmpty()) {
                    Text(
                        text = if (isEditMode) "No sets yet" else "No completed sets",
                        color = GrozzMuted,
                        fontSize = 13.sp,
                        fontFamily = Lexend,
                        modifier = Modifier.padding(vertical = 12.dp),
                    )
                    Button(
                        onClick = { onAddSet() },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .border(1.dp, GrozzYellow, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    ) {
                        Text(
                            "Add set",
                            color = GrozzYellow,
                            style = MaterialTheme.typography.labelLarge,
                            fontFamily = Lexend,
                        )
                    }
                } else {
                    visibleSets.forEachIndexed { index, set ->
                        if (isEditMode) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    String.format("%02d", index + 1),
                                    color = GrozzOnBackground,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontFamily = Lexend,
                                    modifier = Modifier.weight(1f),
                                )
                                Text(
                                    formatWeightKg(set.weight),
                                    color = GrozzOnBackground,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontFamily = Lexend,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                )
                                Text(
                                    "${set.reps}",
                                    color = GrozzOnBackground,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontFamily = Lexend,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                )

                                Row(
                                    modifier = Modifier.weight(1.5f),
                                    horizontalArrangement = Arrangement.End,
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.editnote),
                                        contentDescription = "Edit",
                                        tint = GrozzYellow,
                                        modifier =
                                            Modifier
                                                .size(22.dp)
                                                .clickable { onEditSet(set) },
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Icon(
                                        painter = painterResource(R.drawable.closeicon128),
                                        contentDescription = "Delete",
                                        tint = GrozzError,
                                        modifier =
                                            Modifier
                                                .size(22.dp)
                                                .clickable { onDeleteSet(set) },
                                    )
                                }
                            }
                        } else {
                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "${set.setIndex + 1}",
                                    color = GrozzOnBackground,
                                    fontSize = 15.sp,
                                    fontFamily = Lexend,
                                    modifier = Modifier.weight(1f),
                                )
                                Text(
                                    text = formatWeight(set.weight),
                                    color = GrozzOnBackground,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = Lexend,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                )
                                Text(
                                    text = "${set.reps}",
                                    color = GrozzOnBackground,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = Lexend,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    if (isEditMode) {
                        Button(
                            onClick = { onAddSet() },
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .border(1.dp, GrozzYellow, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        ) {
                            Text(
                                "Add set",
                                color = GrozzYellow,
                                style = MaterialTheme.typography.labelLarge,
                                fontFamily = Lexend,
                            )
                        }
                    } else {
                        return@Column
                    }
                }
            }
        }
    }
}

@Composable
private fun EditModeBanner(modifier: Modifier = Modifier) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(GrozzYellow.copy(alpha = 0.12f))
                .border(1.dp, GrozzYellow, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.editnote),
            contentDescription = null,
            tint = GrozzYellow,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Editing session",
                color = GrozzYellow,
                fontSize = 13.sp,
                fontFamily = Lexend,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Tap the checkmark to save. Back discards changes.",
                color = GrozzTextSecondary,
                fontSize = 12.sp,
                fontFamily = Lexend,
            )
        }
    }
}

@Composable
private fun SetHeaderCell(text: String, modifier: Modifier = Modifier, align: TextAlign) {
    Text(
        text = text,
        color = GrozzMuted,
        fontSize = 11.sp,
        fontFamily = Lexend,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp,
        modifier = modifier,
        textAlign = align,
    )
}

@Composable
private fun OldWorkoutDetailsTopBar(
    title: String,
    canManage: Boolean,
    onBack: () -> Unit,
    onMenuClick: () -> Unit,
    onEditClick: () -> Unit,
    isEditModeEnabled: Boolean,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .height(56.dp),
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart),
        ) {
            Icon(
                painter = painterResource(R.drawable.left),
                contentDescription = "Back",
                modifier = Modifier.size(24.dp),
                tint = GrozzOnBackground,
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
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 56.dp)
                    .fillMaxWidth(),
        )

        if (canManage) {
            IconButton(
                onClick = if (isEditModeEnabled) onEditClick else onMenuClick,
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                Icon(
                    painter =
                        if (isEditModeEnabled) {
                            painterResource(R.drawable.checkcircleicon128)
                        } else {
                            painterResource(
                                R.drawable.editnote,
                            )
                        },
                    contentDescription = if (isEditModeEnabled) "Save" else "Edit",
                    modifier = Modifier.size(22.dp),
                    tint = if (isEditModeEnabled) GrozzYellow else GrozzOnBackground,
                )
            }
        } else {
            Spacer(
                modifier =
                    Modifier
                        .align(Alignment.CenterEnd)
                        .size(48.dp),
            )
        }
    }
}

private fun formatDuration(seconds: Long): String {
    val total = seconds.coerceAtLeast(0)
    val h = total / 3600
    val m = (total % 3600) / 60
    val s = total % 60
    return "%d:%02d:%02d".format(h, m, s)
}

private fun formatWeight(weight: Float): String = if (weight % 1f == 0f) {
    weight.toInt().toString()
} else {
    "%.1f".format(weight)
}
