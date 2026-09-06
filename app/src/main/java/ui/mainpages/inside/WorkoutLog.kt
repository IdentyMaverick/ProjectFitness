package ui.mainpages.inside

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.grozzbear.R
import com.grozzbear.ui.components.GrozzPrimaryButton
import com.grozzbear.ui.components.GrozzTopBarLogo
import com.grozzbear.ui.theme.GrozzBorder
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
import data.local.viewmodel.WorkoutCompleteScreenViewModel
import data.local.viewmodel.WorkoutLogViewModel
import data.remote.FirebaseStorageHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ui.mainpages.navigation.Screens

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "DefaultLocale")
@Composable
fun WorkoutLog(
    navController: NavController,
    workoutLogViewModel: WorkoutLogViewModel,
    workoutCompleteScreenViewModel: WorkoutCompleteScreenViewModel
) {
    val workout = workoutLogViewModel.workoutFlow.collectAsState(null).value
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val clickedSetsNumber = remember { mutableStateOf(0) }
    val clickedRepsNumber = remember { mutableStateOf(0) }

    LaunchedEffect(workout?.workout?.workoutId) {
        workout?.let {
            workoutLogViewModel.startWorkout(it.workout.workoutId, it.workout.workoutName)
            workoutLogViewModel.startWorkout()
            workoutLogViewModel.prepareInitialWorkoutData(it)
        }
    }

    if (workout != null) {
        val pagerState = rememberPagerState(pageCount = { workout.exercises.size })
        val seconds by workoutLogViewModel.elapsedTime.collectAsState()
        val formattedTime = remember(seconds) {
            String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60)
        }

        val sheetState = rememberModalBottomSheetState()
        var showBottomSheet by remember { mutableStateOf(false) }
        val sheetStateFinish = rememberModalBottomSheetState()
        var showBottomSheetFinish by remember { mutableStateOf(false) }
        val sheetStateLog = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var showBottomSheetLog by remember { mutableStateOf(false) }
        var showTimerSheet by remember { mutableStateOf(false) }
        val showTimerSheetModalBottom = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        val flag = remember { mutableStateOf(0) }
        val setIndex = remember { mutableIntStateOf(0) }

        BackHandler { showBottomSheet = true }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(GrozzSystemBar)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { focusManager.clearFocus() }
        ) {
            HomeTopBarWorkoutLog(
                onBackClick = { showBottomSheet = true },
                formattedTime = formattedTime
            )

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { pageIndex ->
                    val exercise = workout.exercises[pageIndex]

                    val currentSets = remember(exercise.exercise.exerciseId) {
                        workoutLogViewModel.getOrInitSets(exercise.exercise.exerciseName, exercise.sets)
                    }
                    LaunchedEffect(pagerState.currentPage) {
                        if (pagerState.currentPage == pageIndex) {
                            workoutLogViewModel.addExercise(
                                exercise.exercise.exerciseName,
                                exercise.exercise.bodyPart,
                                exercise.exercise.secondaryMuscles
                            )
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            ExerciseImageHeader(
                                exerciseName = exercise.exercise.exerciseName,
                                exerciseImage = exercise.exercise.exerciseImage,
                                pageLabel = "${pagerState.currentPage + 1} of ${workout.exercises.size}"
                            )
                        }

                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(GrozzSurface)
                                    .padding(horizontal = 20.dp, vertical = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                FinalWorkoutTimer()
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 28.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "SET",
                                    color = GrozzMuted,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = Lexend,
                                    modifier = Modifier.width(30.dp)
                                )
                                Text(
                                    "KG",
                                    color = GrozzMuted,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = Lexend,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    "REPS",
                                    color = GrozzMuted,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = Lexend,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.width(25.dp))
                                Spacer(modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        itemsIndexed(
                            items = currentSets,
                            key = { _, item -> item.setId }
                        ) { index, item ->
                            val isDone = item.isClicked
                            var rowWeight by remember(item.setId) {
                                mutableStateOf(if (item.weight > 0) item.weight.toString() else "0")
                            }
                            var rowReps by remember(item.setId) {
                                mutableStateOf(if (item.reps > 0) item.reps.toString() else "0")
                            }
                            var isDeleting by remember { mutableStateOf(false) }

                            val dismissBoxState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { dismissValue ->
                                    when (dismissValue) {
                                        SwipeToDismissBoxValue.EndToStart -> {
                                            isDeleting = true
                                            false
                                        }

                                        SwipeToDismissBoxValue.StartToEnd -> {
                                            showBottomSheetLog = true
                                            flag.value = 1
                                            setIndex.intValue = index
                                            false
                                        }

                                        else -> false
                                    }
                                }
                            )

                            LaunchedEffect(isDeleting) {
                                if (isDeleting) {
                                    workoutLogViewModel.deleteSet(index, exercise.exercise.exerciseName)
                                    currentSets.remove(item)
                                    isDeleting = false
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateContentSize()
                            ) {
                                SwipeToDismissBox(
                                    state = dismissBoxState,
                                    backgroundContent = {
                                        val color by animateColorAsState(
                                            when (dismissBoxState.targetValue) {
                                                SwipeToDismissBoxValue.EndToStart -> GrozzError
                                                SwipeToDismissBoxValue.StartToEnd -> Color(0xFF4CAF50)
                                                else -> Color.Transparent
                                            },
                                            label = "dismissColor"
                                        )
                                        Box(
                                            Modifier
                                                .fillMaxSize()
                                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(color)
                                        )
                                    },
                                    content = {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(GrozzSurface.copy(alpha = 0.9f))
                                                .padding(horizontal = 12.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${index + 1}",
                                                modifier = Modifier.width(30.dp),
                                                color = if (isDone) GrozzYellow else GrozzOnBackground,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontFamily = Lexend
                                            )
                                            Box(modifier = Modifier.weight(1f)) {
                                                SetLogItemWeight(
                                                    rowWeight,
                                                    {
                                                        rowWeight = it
                                                        scope.launch {
                                                            workoutLogViewModel.toggleSetDone(
                                                                exercise.exercise.exerciseName,
                                                                index,
                                                                false
                                                            )
                                                        }
                                                    },
                                                    Modifier.fillMaxWidth(),
                                                    isDone
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Box(modifier = Modifier.weight(1f)) {
                                                SetLogItemReps(
                                                    rowReps,
                                                    {
                                                        rowReps = it
                                                        scope.launch {
                                                            workoutLogViewModel.toggleSetDone(
                                                                exercise.exercise.exerciseName,
                                                                index,
                                                                false
                                                            )
                                                        }
                                                    },
                                                    Modifier.fillMaxWidth(),
                                                    isDone
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            IconButton(
                                                onClick = {
                                                    scope.launch {
                                                        clickedSetsNumber.value += 1
                                                        clickedRepsNumber.value += rowReps.toIntOrNull() ?: 0
                                                        workoutLogViewModel.saveSetToDb(
                                                            rowReps,
                                                            rowWeight,
                                                            index,
                                                            exercise.exercise.exerciseName
                                                        )
                                                        workoutLogViewModel.toggleSetDone(
                                                            exercise.exercise.exerciseName,
                                                            index,
                                                            !isDone
                                                        )
                                                    }
                                                },
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (isDone) GrozzYellow else GrozzBorder
                                                    )
                                            ) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = if (isDone) "Completed" else "Mark complete",
                                                    tint = if (isDone) GrozzOnPrimary else GrozzMuted,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    scope.launch {
                                        val nextIdx = currentSets.size
                                        workoutLogViewModel.saveSetToDb(
                                            reps = "0",
                                            weight = "0",
                                            setIndex = nextIdx,
                                            exerciseName = exercise.exercise.exerciseName
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .border(1.dp, GrozzYellow, RoundedCornerShape(12.dp)),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                            ) {
                                Text(
                                    text = "+  Add set",
                                    color = GrozzYellow,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontFamily = Lexend
                                )
                            }
                        }
                        item {
                            Spacer(
                                modifier = Modifier.height(
                                    if (!pagerState.canScrollForward) 120.dp else 80.dp
                                )
                            )
                        }
                    }
                }

                if (!pagerState.canScrollForward) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .navigationBarsPadding()
                            .padding(bottom = 16.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        GrozzPrimaryButton(
                            text = "Finish workout",
                            onClick = { showBottomSheetFinish = true },
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .fillMaxWidth()
                        )
                    }
                }
            } // content Box under top bar

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = sheetState,
                    containerColor = GrozzSurface,
                    dragHandle = { BottomSheetDefaults.DragHandle(color = GrozzMuted) }
                ) {
                    WorkoutExitDialog(
                        onConfirm = {
                            workoutLogViewModel.cancelAndExitWorkout {
                                showBottomSheet = false
                                navController.navigate(Screens.Home.route)
                            }
                        },
                        onDismiss = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) showBottomSheet = false
                            }
                        },
                        flag = 0
                    )
                }
            } else if (showBottomSheetLog) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheetLog = false },
                    sheetState = sheetStateLog,
                    containerColor = GrozzSurface,
                    dragHandle = { BottomSheetDefaults.DragHandle(color = GrozzYellow) }
                ) {
                    if (flag.value == 0) {
                        WorkoutLogDialog(
                            onConfirm = {
                                showBottomSheetLog = false
                                workoutLogViewModel.updateExerciseNote(it)
                            },
                            onDismiss = {
                                scope.launch { sheetStateLog.hide() }.invokeOnCompletion {
                                    if (!sheetStateLog.isVisible) showBottomSheetLog = false
                                }
                            },
                            flag = 0
                        )
                    } else if (flag.value == 1) {
                        WorkoutLogDialog(
                            onConfirm = {
                                showBottomSheetLog = false
                                workoutLogViewModel.updateSetNote(it, setIndex.value)
                            },
                            onDismiss = {
                                scope.launch { sheetStateLog.hide() }.invokeOnCompletion {
                                    if (!sheetStateLog.isVisible) showBottomSheetLog = false
                                }
                            },
                            flag = 1
                        )
                    }
                }
            } else if (showBottomSheetFinish) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheetFinish = false },
                    sheetState = sheetStateFinish,
                    containerColor = GrozzSurface,
                    dragHandle = { BottomSheetDefaults.DragHandle(color = GrozzMuted) }
                ) {
                    WorkoutExitDialog(
                        onConfirm = {
                            workoutCompleteScreenViewModel._totalSetsCompleted.value =
                                clickedSetsNumber.value
                            workoutCompleteScreenViewModel._totalRepsCompleted.value =
                                clickedRepsNumber.value
                            workoutLogViewModel.stopWorkout()
                            workoutLogViewModel.finishWorkout(
                                {
                                    showBottomSheetFinish = false
                                    navController.navigate(Screens.WorkoutCompleteScreen.route) {
                                        popUpTo(Screens.WorkoutCompleteScreen.route) {
                                            inclusive = true
                                        }
                                    }
                                }
                            )
                        },
                        onDismiss = {
                            scope.launch { sheetStateFinish.hide() }.invokeOnCompletion {
                                if (!sheetStateFinish.isVisible) showBottomSheetFinish = false
                            }
                        },
                        flag = 1
                    )
                }
            }

        }

        if (showTimerSheet) {
            ModalBottomSheet(
                onDismissRequest = { showTimerSheet = false },
                sheetState = showTimerSheetModalBottom,
                containerColor = GrozzSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .padding(bottom = 24.dp)
                ) {
                    FinalWorkoutTimer()
                }
            }
        }
    }
}

@Composable
fun ExerciseImageHeader(
    exerciseName: String,
    exerciseImage: String? = null,
    pageLabel: String? = null
) {
    val wordList = exerciseName.split(" ", limit = 2)
    val firstWord = wordList.getOrNull(0).orEmpty()
    val secondWord = wordList.getOrNull(1).orEmpty()

    val fullUrl = remember(exerciseImage) {
        FirebaseStorageHelper.getImageUrl(exerciseImage ?: "")
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(196.dp)
    ) {
        AsyncImage(
            model = fullUrl,
            contentDescription = exerciseName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            placeholder = painterResource(id = R.drawable.grozzlogo),
            error = painterResource(id = R.drawable.grozzlogo)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.25f),
                            Color.Transparent,
                            GrozzSystemBar.copy(alpha = 0.95f)
                        )
                    )
                )
        )

        if (!pageLabel.isNullOrBlank()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(GrozzRadiusChip))
                    .background(GrozzSystemBar.copy(alpha = 0.85f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .background(GrozzYellow, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = pageLabel,
                    color = GrozzOnBackground,
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = Lexend,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 24.dp, vertical = 18.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = firstWord.uppercase(),
                color = GrozzOnBackground,
                fontFamily = Oswald,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (secondWord.isNotBlank()) {
                Text(
                    text = secondWord.uppercase(),
                    color = GrozzYellow,
                    fontFamily = Oswald,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun HomeTopBarWorkoutLog(
    onBackClick: () -> Unit,
    formattedTime: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(GrozzSystemBar)
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .height(56.dp)
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Back",
                tint = GrozzYellow,
                modifier = Modifier.size(28.dp)
            )
        }

        GrozzTopBarLogo(
            modifier = Modifier.align(Alignment.Center)
        )

        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clip(RoundedCornerShape(GrozzRadiusChip))
                .background(GrozzSurface)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(GrozzYellow, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = formattedTime,
                color = GrozzYellow,
                style = MaterialTheme.typography.labelMedium,
                fontFamily = Lexend,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun WorkoutExitDialog(onConfirm: () -> Unit, onDismiss: () -> Unit, flag: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 36.dp, start = 24.dp, end = 24.dp, top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(GrozzYellow),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (flag == 0) Icons.Default.Close else Icons.Default.Done,
                contentDescription = null,
                tint = GrozzOnPrimary,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = if (flag == 0) "End workout?" else "Complete workout?",
            color = GrozzOnBackground,
            style = MaterialTheme.typography.titleLarge,
            fontFamily = Lexend
        )
        if (flag == 0) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "You will lose all progress for this session. This cannot be undone.",
                color = GrozzTextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = Lexend,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(28.dp))
        GrozzPrimaryButton(
            text = if (flag == 0) "End" else "Complete",
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onDismiss) {
            Text(
                text = if (flag == 0) "Cancel" else "Go back",
                color = GrozzError.copy(alpha = 0.85f),
                style = MaterialTheme.typography.labelLarge,
                fontFamily = Lexend
            )
        }
    }
}

@Composable
fun WorkoutLogDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    flag: Int
) {
    val logValue = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(bottom = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (flag == 0) "Log about exercise" else "Log about set",
            color = GrozzOnBackground,
            style = MaterialTheme.typography.titleLarge,
            fontFamily = Lexend
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = logValue.value,
            onValueChange = { logValue.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = GrozzSystemBar,
                focusedContainerColor = GrozzSystemBar,
                unfocusedBorderColor = GrozzBorder,
                focusedBorderColor = GrozzYellow,
                cursorColor = GrozzYellow,
                focusedTextColor = GrozzOnBackground,
                unfocusedTextColor = GrozzOnBackground
            ),
            textStyle = TextStyle(
                textAlign = TextAlign.Start,
                fontFamily = Lexend,
                fontSize = 16.sp
            ),
            placeholder = {
                Text(
                    "Write here",
                    color = GrozzMuted,
                    fontFamily = Lexend
                )
            }
        )
        Spacer(modifier = Modifier.height(20.dp))
        GrozzPrimaryButton(
            text = "Save",
            onClick = { onConfirm(logValue.value) },
            modifier = Modifier.fillMaxWidth()
        )
        TextButton(onClick = onDismiss) {
            Text(
                "Cancel",
                color = GrozzMuted,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun SegmentedProgressIndicator(totalSegments: Int, currentSegment: Int) {
    Row(
        modifier = Modifier
            .height(14.dp)
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (i in 0 until totalSegments) {
            val isCompleted = i < currentSegment

            val animatedColor by animateColorAsState(
                targetValue = if (isCompleted) GrozzYellow else GrozzOnBackground.copy(alpha = 0.15f),
                animationSpec = tween(durationMillis = 600),
                label = "colorAnim"
            )

            val glowAlpha by animateFloatAsState(
                targetValue = if (isCompleted) 0.8f else 0f,
                animationSpec = tween(durationMillis = 800),
                label = "glowAnim"
            )

            Box(
                modifier = Modifier
                    .width(50.dp)
                    .height(3.dp)
                    .graphicsLayer {
                        scaleY = if (isCompleted) 1.1f else 1.0f
                    }
                    .clip(RoundedCornerShape(8.dp))
                    .background(animatedColor)
                    .shadow(
                        elevation = if (isCompleted) 10.dp else 0.dp,
                        shape = RoundedCornerShape(8.dp),
                        spotColor = GrozzYellow.copy(alpha = glowAlpha),
                        ambientColor = GrozzYellow.copy(alpha = glowAlpha)
                    )
                    .border(
                        width = 0.5.dp,
                        color = GrozzOnBackground.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    )
            )
        }
    }
}

@Composable
fun SetLogItemWeight(
    weight: String,
    setWeight: (String) -> Unit,
    modifier: Modifier = Modifier,
    isDone: Boolean
) {
    TextField(
        value = weight,
        onValueChange = { input ->
            val filteredInput = input.filter { it.isDigit() || it == '.' }.replace(" ", "")
            val isValid = filteredInput.length <= 5 &&
                (filteredInput.isEmpty() || filteredInput.first() != '.') &&
                filteredInput.count { it == '.' } <= 1

            if (isValid) {
                setWeight(filteredInput)
            }
        },
        placeholder = {
            Text(
                "KG",
                color = GrozzMuted,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier.width(50.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            cursorColor = GrozzYellow,
            focusedTextColor = GrozzOnBackground,
            unfocusedTextColor = GrozzOnBackground,
            focusedIndicatorColor = GrozzYellow,
            unfocusedIndicatorColor = if (isDone) GrozzYellow else GrozzBorder
        ),
        textStyle = TextStyle(
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontFamily = Lexend,
            fontSize = 18.sp
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Composable
fun SetLogItemReps(
    reps: String,
    setReps: (String) -> Unit,
    modifier: Modifier = Modifier,
    isDone: Boolean
) {
    TextField(
        value = reps,
        onValueChange = { input ->
            val filteredInput = input.filter { it.isDigit() || it == '.' }.replace(" ", "")
            val isValid = filteredInput.length <= 5 &&
                (filteredInput.isEmpty() || filteredInput.first() != '.') &&
                filteredInput.count { it == '.' } <= 1

            if (isValid) {
                setReps(filteredInput)
            }
        },
        placeholder = {
            Text(
                "0",
                color = GrozzMuted,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier.width(50.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            cursorColor = GrozzYellow,
            focusedTextColor = GrozzOnBackground,
            unfocusedTextColor = GrozzOnBackground,
            focusedIndicatorColor = GrozzYellow,
            unfocusedIndicatorColor = if (isDone) GrozzYellow else GrozzBorder
        ),
        textStyle = TextStyle(
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontFamily = Lexend,
            fontSize = 18.sp
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Composable
fun FinalWorkoutTimer() {
    val context = LocalContext.current

    var selectedSeconds by remember { mutableLongStateOf(60L) }
    var timeLeft by remember { mutableLongStateOf(60L) }
    var isRunning by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            if (timeLeft == 0L) {
                isRunning = false
                vibratePhone(context)
            }
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = "REST TIMER",
                color = GrozzMuted,
                style = MaterialTheme.typography.labelMedium,
                fontFamily = Lexend
            )
            Text(
                text = String.format("%02d:%02d", timeLeft / 60, timeLeft % 60),
                modifier = Modifier.clickable {
                    if (!isRunning) showEditDialog = true
                },
                color = GrozzYellow,
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = Lexend
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = {
                if (timeLeft == 0L) {
                    timeLeft = selectedSeconds
                }
                isRunning = !isRunning
            },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = if (isRunning) GrozzError else GrozzYellow,
                contentColor = GrozzOnPrimary
            )
        ) {
            Icon(
                painter = painterResource(
                    if (isRunning) R.drawable.pauseicon128 else R.drawable.playicon128
                ),
                contentDescription = if (isRunning) "Pause" else "Start",
                tint = GrozzOnPrimary,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = {
                timeLeft = selectedSeconds
                isRunning = false
            },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = GrozzBorder,
                contentColor = GrozzOnBackground
            )
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Reset",
                tint = GrozzOnBackground
            )
        }
    }

    if (showEditDialog) {
        var tempMinutes by remember { mutableStateOf((selectedSeconds / 60).toString()) }
        var tempSeconds by remember { mutableStateOf((selectedSeconds % 60).toString()) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor = GrozzSurface,
            title = {
                Text(
                    "Set timer",
                    color = GrozzOnBackground,
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = Lexend
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = tempMinutes,
                        onValueChange = { if (it.isDigitsOnly()) tempMinutes = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = TextStyle(color = GrozzOnBackground, fontFamily = Lexend),
                        label = { Text("Minutes", color = GrozzYellow) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GrozzYellow,
                            unfocusedBorderColor = GrozzBorder,
                            cursorColor = GrozzYellow
                        )
                    )
                    OutlinedTextField(
                        value = tempSeconds,
                        onValueChange = { if (it.isDigitsOnly()) tempSeconds = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = TextStyle(color = GrozzOnBackground, fontFamily = Lexend),
                        label = { Text("Seconds", color = GrozzYellow) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GrozzYellow,
                            unfocusedBorderColor = GrozzBorder,
                            cursorColor = GrozzYellow
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val mins = tempMinutes.toLongOrNull() ?: 0L
                        val secs = tempSeconds.toLongOrNull() ?: 0L
                        selectedSeconds = when {
                            mins != 0L && secs == 0L -> mins * 60
                            mins == 0L && secs != 0L -> secs
                            mins != 0L && secs != 0L -> (mins * 60) + secs
                            else -> selectedSeconds
                        }
                        timeLeft = selectedSeconds
                        showEditDialog = false
                    }
                ) {
                    Text("Set", color = GrozzYellow, fontFamily = Lexend)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = GrozzMuted, fontFamily = Lexend)
                }
            }
        )
    }
}

fun vibratePhone(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(500)
    }
}
