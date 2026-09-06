package ui.mainpages.inside

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grozzbear.R
import com.grozzbear.projectfitness.data.local.entity.SetEntity
import com.grozzbear.projectfitness.data.local.entity.WorkoutExerciseEntity
import com.grozzbear.projectfitness.data.local.viewmodel.WorkoutSettingViewModel
import com.grozzbear.ui.components.GrozzPrimaryButton
import com.grozzbear.ui.components.GrozzTopBarLogo
import com.grozzbear.ui.components.IntPickerColumn
import com.grozzbear.ui.components.WeightFractionOptions
import com.grozzbear.ui.components.WeightWholeKgRange
import com.grozzbear.ui.components.combineWeightKg
import com.grozzbear.ui.components.formatWeightKg
import com.grozzbear.ui.components.splitWeightKg
import com.grozzbear.ui.theme.GrozzError
import com.grozzbear.ui.theme.GrozzMuted
import com.grozzbear.ui.theme.GrozzOnBackground
import com.grozzbear.ui.theme.GrozzOnPrimary
import com.grozzbear.ui.theme.GrozzRadiusChip
import com.grozzbear.ui.theme.GrozzSurface
import com.grozzbear.ui.theme.GrozzSystemBar
import com.grozzbear.ui.theme.GrozzTextSecondary
import com.grozzbear.ui.theme.GrozzYellow
import com.grozzbear.ui.theme.Lexend
import com.grozzbear.ui.theme.Oswald
import com.grozzbear.ui.util.counted
import com.grozzbear.ui.util.safeWorkoutPainter
import com.grozzbear.ui.util.workoutTypeLabel
import java.util.UUID
import ui.mainpages.mainpages.WorkoutTag
import ui.mainpages.navigation.Screens
import viewmodel.ViewModelSave

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutSettingScreen(
    navController: NavController,
    viewModelSave: ViewModelSave,
    workoutSettingViewModel: WorkoutSettingViewModel
) {
    val lazyListState = rememberLazyListState()
    val selectedWorkout by workoutSettingViewModel.workoutFlow.collectAsState(null)
    val image = selectedWorkout?.workout?.image ?: 0
    val sheetState = rememberModalBottomSheetState()

    var showBottomSheet by remember { mutableStateOf(false) }
    var editingSet by remember { mutableStateOf<SetEntity?>(null) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            HomeTopBarWorkoutSettingScreen(
                workoutImageRes = image,
                navController = navController
            )
        },
        containerColor = GrozzSystemBar,
        bottomBar = {
            FixedStartButton {
                selectedWorkout?.workout?.let { workout ->
                    navController.navigate("workoutlog/${workout.workoutId}")
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val workout = selectedWorkout) {
                null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = GrozzYellow)
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = workout.workout.workoutName,
                            color = GrozzOnBackground,
                            fontFamily = Oswald,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        WorkoutTag(
                            text = workoutTypeLabel(workout.workout.workoutType),
                            icon = R.drawable.dumbbellicon128,
                            textColor = GrozzYellow,
                            iconColor = GrozzYellow
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            Modifier
                                .width(40.dp)
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(GrozzYellow)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = counted(workout.exercises.size, "exercise"),
                            color = GrozzTextSecondary,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = Lexend
                        )
                    }

                    if (workout.exercises.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp, vertical = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No exercises yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = GrozzOnBackground,
                                fontFamily = Lexend
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Add movements to this workout before you start.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = GrozzTextSecondary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            GrozzPrimaryButton(
                                text = "Add exercises",
                                onClick = {
                                    navController.navigate(
                                        Screens.ChooseExercises.createRoute(workout.workout.workoutId)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        LazyColumn(
                            state = lazyListState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            contentPadding = PaddingValues(bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val orderedWorkoutList = workout.exercises.sortedBy { it.exercise.orderIndex }
                            itemsIndexed(
                                items = orderedWorkoutList,
                                key = { _, item -> item.exercise.exerciseId }
                            ) { index, item ->
                                Box(
                                    modifier = Modifier
                                        .animateItem()
                                        .padding(vertical = 2.dp)
                                ) {
                                    ExerciseExpandableCardWorkoutSettingScreen(
                                        exerciseDraft = item.exercise,
                                        exerciseSet = item.sets,
                                        onEditClick = { set ->
                                            editingSet = set
                                            showBottomSheet = true
                                        },
                                        onDeleteSetClick = { set ->
                                            workoutSettingViewModel.deleteSet(set)
                                        },
                                        onAddSetClick = {
                                            workoutSettingViewModel.addSet(
                                                UUID.randomUUID().toString(),
                                                item.exercise.exerciseId
                                            )
                                        },
                                        canMoveUp = index > 0,
                                        canMoveDown = index < orderedWorkoutList.lastIndex,
                                        onMoveUp = {
                                            workoutSettingViewModel.moveExercise(index, index - 1)
                                        },
                                        onMoveDown = {
                                            workoutSettingViewModel.moveExercise(index, index + 1)
                                        },
                                        onRemove = {
                                            workoutSettingViewModel.removeExercise(
                                                exerciseId = item.exercise.exerciseId
                                            )
                                        }
                                    )
                                }
                            }
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                GrozzPrimaryButton(
                                    text = "Add exercises",
                                    onClick = {
                                        navController.navigate(
                                            Screens.ChooseExercises.createRoute(workout.workout.workoutId)
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showBottomSheet && editingSet != null) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = GrozzSurface
            ) {
                EditSetBottomSheetContent(
                    set = editingSet!!,
                    onSave = { updatedReps, updatedWeight ->
                        workoutSettingViewModel.updateSet(
                            editingSet!!.setId,
                            editingSet!!.exerciseOwnerId,
                            updatedReps,
                            updatedWeight
                        )
                        showBottomSheet = false
                    },
                    onDelete = {
                        workoutSettingViewModel.deleteSet(editingSet!!)
                        showBottomSheet = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExerciseExpandableCardWorkoutSettingScreen(
    exerciseDraft: WorkoutExerciseEntity,
    exerciseSet: List<SetEntity>,
    onEditClick: (SetEntity) -> Unit,
    onDeleteSetClick: (SetEntity) -> Unit,
    onAddSetClick: () -> Unit,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onRemove: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(GrozzRadiusChip))
            .background(GrozzSurface)
            .combinedClickable(
                onClick = { expanded = !expanded },
                onLongClick = { }
            )
            .animateContentSize()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(44.dp)
                    .background(GrozzYellow, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painterResource(R.drawable.dumbbellicon128),
                    contentDescription = null,
                    tint = GrozzOnPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exerciseDraft.exerciseName,
                    color = GrozzOnBackground,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = Lexend,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = exerciseDraft.bodyPart.uppercase(),
                    color = GrozzYellow,
                    style = MaterialTheme.typography.labelMedium,
                    fontFamily = Lexend,
                    fontWeight = FontWeight.Bold
                )
                if (!expanded && exerciseSet.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = counted(exerciseSet.size, "set"),
                        color = GrozzMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            IconButton(
                onClick = onMoveUp,
                enabled = canMoveUp,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Move up",
                    tint = if (canMoveUp) GrozzYellow else GrozzMuted,
                    modifier = Modifier.size(22.dp)
                )
            }
            IconButton(
                onClick = onMoveDown,
                enabled = canMoveDown,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Move down",
                    tint = if (canMoveDown) GrozzYellow else GrozzMuted,
                    modifier = Modifier.size(22.dp)
                )
            }
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.terminate),
                    contentDescription = "Remove exercise",
                    tint = GrozzError,
                    modifier = Modifier.size(20.dp)
                )
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
                Spacer(modifier = Modifier.height(18.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        "SET",
                        color = GrozzMuted,
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = Lexend,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "KG",
                        color = GrozzMuted,
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = Lexend,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "REPS",
                        color = GrozzMuted,
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = Lexend,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "ACTION",
                        color = GrozzMuted,
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = Lexend,
                        modifier = Modifier.weight(1.5f),
                        textAlign = TextAlign.End
                    )
                }

                if (exerciseSet.isEmpty()) {
                    Text(
                        text = "No sets yet. Add one below.",
                        color = GrozzTextSecondary,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    exerciseSet.forEachIndexed { index, set ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                String.format("%02d", index + 1),
                                color = GrozzOnBackground,
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = Lexend,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                formatWeightKg(set.weight),
                                color = GrozzOnBackground,
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = Lexend,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "${set.reps}",
                                color = GrozzOnBackground,
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = Lexend,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )

                            Row(
                                modifier = Modifier.weight(1.5f),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.editnote),
                                    contentDescription = "Edit",
                                    tint = GrozzYellow,
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clickable { onEditClick(set) }
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Icon(
                                    painter = painterResource(R.drawable.closeicon128),
                                    contentDescription = "Delete",
                                    tint = GrozzError,
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clickable { onDeleteSetClick(set) }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onAddSetClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .border(1.dp, GrozzYellow, RoundedCornerShape(GrozzRadiusChip)),
                    shape = RoundedCornerShape(GrozzRadiusChip),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text(
                        "Add set",
                        color = GrozzYellow,
                        style = MaterialTheme.typography.labelLarge,
                        fontFamily = Lexend
                    )
                }
            }
        }
    }
}

@Composable
fun EditSetBottomSheetContent(
    set: SetEntity,
    onSave: (Int, Double) -> Unit,
    onDelete: () -> Unit
) {
    val (initialWhole, initialFraction) = remember(set.setId, set.weight) {
        splitWeightKg(set.weight)
    }
    var reps by remember(set.setId, set.reps) { mutableIntStateOf(set.reps) }
    var wholeKg by remember(set.setId, set.weight) { mutableIntStateOf(initialWhole) }
    var fractionIndex by remember(set.setId, set.weight) { mutableIntStateOf(initialFraction) }
    val previewWeight = combineWeightKg(wholeKg, fractionIndex)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .padding(bottom = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Edit set",
            color = GrozzOnBackground,
            style = MaterialTheme.typography.titleLarge,
            fontFamily = Lexend
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${formatWeightKg(previewWeight)} KG × $reps",
            color = GrozzYellow,
            fontSize = 16.sp,
            fontFamily = Lexend,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IntPickerColumn(
                label = "KG",
                value = wholeKg,
                range = WeightWholeKgRange,
                onValueChange = { wholeKg = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            IntPickerColumn(
                label = "+",
                value = fractionIndex,
                range = WeightFractionOptions.indices,
                labelForValue = { index ->
                    val f = WeightFractionOptions[index]
                    if (f == 0f) "0" else f.toString()
                },
                onValueChange = { fractionIndex = it }
            )
            Spacer(modifier = Modifier.width(32.dp))
            IntPickerColumn(
                label = "REPS",
                value = reps,
                range = 0..100,
                onValueChange = { reps = it }
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        GrozzPrimaryButton(
            text = "Save",
            onClick = { onSave(reps, combineWeightKg(wholeKg, fractionIndex).toDouble()) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Delete set",
            color = GrozzError,
            style = MaterialTheme.typography.labelLarge,
            fontFamily = Lexend,
            modifier = Modifier
                .clickable(onClick = onDelete)
                .padding(8.dp)
        )
    }
}

@Composable
private fun HomeTopBarWorkoutSettingScreen(
    workoutImageRes: Int,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(72.dp)
    ) {
        Image(
            painter = safeWorkoutPainter(workoutImageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            GrozzSystemBar.copy(alpha = 0.55f),
                            GrozzSystemBar.copy(alpha = 0.85f)
                        )
                    )
                )
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    painter = painterResource(R.drawable.left),
                    contentDescription = "Back",
                    tint = GrozzOnBackground,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            GrozzTopBarLogo()
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.size(48.dp))
        }
    }
}

@Composable
fun FixedStartButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(GrozzSystemBar)
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        GrozzPrimaryButton(
            text = "Start workout",
            onClick = onClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
