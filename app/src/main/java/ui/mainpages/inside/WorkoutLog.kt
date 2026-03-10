package ui.mainpages.inside

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.grozzbear.R
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

        // Sheet States
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(top = topPadding)
                .background(Color(0xFF121417))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { focusManager.clearFocus() }
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { pageIndex ->
                val exercise = workout.exercises[pageIndex]

                val currentSets = remember(exercise.exercise.exerciseId) {
                    workoutLogViewModel.getOrInitSets(exercise.exercise.exerciseName, exercise.sets)
                }
                Log.d("currentsets", "${currentSets.size}")
                // Sayfa aktif olduğunda ViewModel'deki activeExerciseId'yi güncelle
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
                            exercise.exercise.exerciseName,
                            exercise.exercise.exerciseImage
                        )
                    }

                    item {
                        LogPlace(
                            onLogClick = { showBottomSheetLog = it },
                            flag = { flag.value = it })
                    }

                    item {
                        Spacer(Modifier.size(30.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 25.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.width(30.dp))
                            Text(
                                "Weight",
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Reps",
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                color = Color.White
                            )
                        }
                    }

                    // 3. SET LİSTESİ (ITEMS)
                    itemsIndexed(
                        items = currentSets,
                        key = { _, item -> item.setId }
                    ) { index, item ->
                        val isDone = item.isClicked
                        var rowWeight by remember(item.setId) { mutableStateOf(if (item.weight > 0) item.weight.toString() else "") }
                        var rowReps by remember(item.setId) { mutableStateOf(if (item.reps > 0) item.reps.toString() else "") }
                        var isDeleting by remember { mutableStateOf(false) }

                        val dismissBoxState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { dismissValue ->
                                if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                    isDeleting = true
                                    false
                                } else if (dismissValue == SwipeToDismissBoxValue.StartToEnd) {
                                    showBottomSheetLog = true
                                    flag.value = 1
                                    setIndex.intValue = index
                                    false
                                } else false
                            }
                        )

                        LaunchedEffect(isDeleting) {
                            if (isDeleting) {
                                // ViewModel: DB'den sil ve alttaki indeksleri kaydır
                                workoutLogViewModel.deleteSet(index, exercise.exercise.exerciseName)
                                // UI: Listeden anında kaldır
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
                                            SwipeToDismissBoxValue.EndToStart -> Color(0xFFE53935)
                                            SwipeToDismissBoxValue.StartToEnd -> Color(0xFF4CAF50)
                                            else -> Color.Transparent
                                        }, label = ""
                                    )
                                    Box(
                                        Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 25.dp, vertical = 4.dp)
                                            .background(color, RoundedCornerShape(14.dp))
                                    )
                                },
                                content = {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFF121417))
                                            .padding(horizontal = 25.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "${index + 1}",
                                            modifier = Modifier.width(30.dp),
                                            color = Color.White,
                                            fontSize = 20.sp
                                        )
                                        Box(modifier = Modifier.weight(1f)) {
                                            SetLogItemWeight(rowWeight, {
                                                rowWeight = it; scope.launch {
                                                //workoutLogViewModel.saveSetToDb(rowReps, it, index, exercise.exercise.exerciseName)
                                                workoutLogViewModel.toggleSetDone(
                                                    exercise.exercise.exerciseName,
                                                    index,
                                                    false
                                                )
                                            }
                                            }, Modifier.fillMaxWidth(), isDone)
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Box(modifier = Modifier.weight(1f)) {
                                            SetLogItemReps(rowReps, {
                                                rowReps = it; scope.launch {
                                                //workoutLogViewModel.saveSetToDb(it, rowWeight, index, exercise.exercise.exerciseName)
                                                workoutLogViewModel.toggleSetDone(
                                                    exercise.exercise.exerciseName,
                                                    index,
                                                    false
                                                )
                                            }
                                            }, Modifier.fillMaxWidth(), isDone)
                                        }
                                        Spacer(modifier = Modifier.width(15.dp))
                                        IconButton(
                                            onClick = {
                                                scope.launch {
                                                    clickedSetsNumber.value += 1
                                                    clickedRepsNumber.value += rowReps.toInt()
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
                                                .clip(RoundedCornerShape(50))
                                                .background(if (isDone) Color(0xFFF1C40F) else Color.Transparent)
                                                .border(
                                                    1.dp,
                                                    Color.Gray.copy(0.5f),
                                                    RoundedCornerShape(50)
                                                )
                                        ) {
                                            Icon(
                                                Icons.Default.Check,
                                                null,
                                                tint = if (isDone) Color.Black else Color.Gray.copy(
                                                    0.5f
                                                ),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                scope.launch {
                                    val nextIdx = currentSets.size
                                    Log.d("currentsetsize", "${currentSets.toList()}")
                                    workoutLogViewModel.saveSetToDb(
                                        reps = "0",
                                        weight = "0",
                                        setIndex = nextIdx,
                                        exerciseName = exercise.exercise.exerciseName
                                    )
                                }
                            },
                            modifier = Modifier.padding(vertical = 16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            Text("+ Add Set", color = Color.White)
                        }
                    }
                    item { Spacer(Modifier.height(100.dp)) }
                }
            }

            // 4. ÜST BAR VE BOTTOM SHEETS
            HomeTopBarWorkoutLog(
                pagerState = pagerState,
                totalSegments = workout.exercises.size,
                workoutLogViewModel = workoutLogViewModel,
                onBackClick = { showBottomSheet = true },
                formattedTime = formattedTime,
                showTimerSheet = showTimerSheet,
                setShowTimerSheet = { showTimerSheet = it }
            )

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = sheetState,
                    containerColor = Color(0xFF1C2126),
                    dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) }
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
                    onDismissRequest = { showBottomSheetLog = false; },
                    sheetState = sheetStateLog,
                    containerColor = Color(0xFF1C2126),
                    dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFFF1C40F)) }
                ) {
                    if (flag.value == 0) {
                        WorkoutLogDialog(
                            onConfirm = {
                                showBottomSheetLog = false;
                                workoutLogViewModel.updateExerciseNote(it)
                            },
                            onDismiss = {
                                scope.launch { sheetStateLog.hide() }.invokeOnCompletion {
                                    if (!sheetStateLog.isVisible) showBottomSheetLog = false
                                }
                            },
                            setLog = { },
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
                            setLog = { },
                            flag = 1
                        )
                    }
                }
            } else if (showBottomSheetFinish) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheetFinish = false },
                    sheetState = sheetStateFinish,
                    containerColor = Color(0xFF1C2126),
                    dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFFF1C40F)) }
                ) {
                    WorkoutExitDialog(
                        onConfirm = {
                            workoutCompleteScreenViewModel._totalSetsCompleted.value =
                                clickedSetsNumber.value
                            workoutCompleteScreenViewModel._totalRepsCompleted.value =
                                clickedRepsNumber.value
                            workoutLogViewModel.stopWorkout()
                            workoutLogViewModel.finishWorkout({
                                showBottomSheetFinish = false
                                navController.navigate(Screens.WorkoutCompleteScreen.route) {
                                    popUpTo(Screens.WorkoutCompleteScreen.route) {
                                        inclusive = true
                                    }
                                }
                            })
                        },
                        onDismiss = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) showBottomSheetFinish = false
                            }
                        },
                        flag = 1
                    )
                }
            }
            if (!pagerState.canScrollForward) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 30.dp), // Ekranın en altından biraz yukarıda
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Button(
                        onClick = {
                            showBottomSheetFinish = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .padding(horizontal = 90.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF1C40F),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(8.dp)
                    ) {
                        Text(
                            text = "FINISH WORKOUT",
                            style = TextStyle(
                                fontFamily = FontFamily(Font(R.font.lexendbold)),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
        if (showTimerSheet == true) {
            ModalBottomSheet(
                onDismissRequest = { showTimerSheet = false },
                sheetState = showTimerSheetModalBottom,
                containerColor = Color(0xFF1C2126)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 40.dp)
                ) {
                    FinalWorkoutTimer()
                }
            }
        }
    }
}

@Composable
fun ExerciseImageHeader(exerciseName: String, exerciseImage: String? = null) {
    val fullUrl = remember(exerciseImage)
    {
        FirebaseStorageHelper.getImageUrl(exerciseImage ?: "")
    }
    Log.d("fullUrl", "$fullUrl")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        AsyncImage(
            model = fullUrl,
            contentDescription = exerciseName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 30.dp),
            placeholder = painterResource(id = R.drawable.grozzholdsdumbbellbothhandsnobackgroundxml),
            error = painterResource(id = R.drawable.grozzholdsdumbbellbothhandsnobackgroundxml)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .align(Alignment.TopCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.8f), Color.Transparent)
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color(0xFF121417))
                    )
                )
        )
    }

    Text(
        text = exerciseName,
        color = Color.White.copy(alpha = 0.9f),
        fontSize = 25.sp,
        fontFamily = FontFamily(Font(R.font.lexendbold)),
        modifier = Modifier.padding(16.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
fun LogPlace(
    onLogClick: (Boolean) -> Unit,
    flag: (Int) -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp)
        ) {
            IconButton(onClick = {}, modifier = Modifier.size(20.dp)) {
                Icon(
                    painter = painterResource(R.drawable.historyicon128),
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                "Log your Progress",
                color = Color(0xFFF1C40F),
                fontSize = 15.sp,
                fontFamily = FontFamily(Font(R.font.lexendregular))
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { onLogClick(true); flag(0) }, modifier = Modifier.size(30.dp)) {
                Icon(
                    painter = painterResource(R.drawable.editnote),
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeTopBarWorkoutLog(
    pagerState: PagerState,
    totalSegments: Int,
    workoutLogViewModel: WorkoutLogViewModel,
    onBackClick: () -> Unit,
    formattedTime: String,
    showTimerSheet: Boolean,
    setShowTimerSheet: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                painter = painterResource(R.drawable.left),
                contentDescription = "Back",
                modifier = Modifier.size(25.dp),
                tint = Color.White
            )
        }

        Spacer(Modifier.weight(1f))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "GROZZ",
                    color = Color(0xFFF1C40F),
                    fontSize = 24.sp,
                    letterSpacing = 0.sp,
                    fontFamily = FontFamily(Font(R.font.oswaldbold))
                )

            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = formattedTime,
                color = Color.White,
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.lexendregular))
            )
            Spacer(Modifier.height(16.dp))
            SegmentedProgressIndicator(totalSegments, pagerState.currentPage)
        }

        Spacer(Modifier.weight(1f))

        IconButton(
            onClick = { setShowTimerSheet(true) },
            modifier = Modifier.size(25.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.shutterspeedfilledicon128),
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Composable
fun WorkoutExitDialog(onConfirm: () -> Unit, onDismiss: () -> Unit, flag: Int) {
    if (flag == 0) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 48.dp, start = 24.dp, end = 24.dp, top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Do you want to leave workout?",
                color = Color.White,
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.lexendregular))
            )
            Spacer(Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    "Yes",
                    color = Color.Red,
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                    modifier = Modifier.clickable { onConfirm() }
                )
                Text(
                    "No",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.lexendregular)),
                    modifier = Modifier.clickable { onDismiss() }
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 48.dp, start = 24.dp, end = 24.dp, top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Do you want to finish workout?",
                color = Color.White,
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.lexendregular))
            )
            Spacer(Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    "Yes",
                    color = Color.Red,
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                    modifier = Modifier.clickable { onConfirm() }
                )
                Text(
                    "No",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.lexendregular)),
                    modifier = Modifier.clickable { onDismiss() }
                )
            }
        }
    }
}

@Composable
fun WorkoutLogDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    setLog: (String) -> Unit,
    flag: Int
) {
    val flag = remember { mutableIntStateOf(flag) }
    val initialText = "" // Eğer varsa eski notu buraya ViewModel'den çekebilirsin
    val logValue = remember { mutableStateOf(initialText) }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (flag.value == 0) {
                "Log about Exercise"
            } else {
                "Log about Set"
            },
            color = Color.White,
            fontSize = 18.sp,
            fontFamily = FontFamily(Font(R.font.lexendregular))
        )
        Spacer(Modifier.height(32.dp))
        Text(
            text = logValue.value,
            color = Color.White,
            fontSize = 18.sp,
            fontFamily = FontFamily(Font(R.font.lexendregular))
        )
        Spacer(Modifier.height(32.dp))
        OutlinedTextField(
            value = logValue.value,
            onValueChange = { logValue.value = it },
            modifier = Modifier.height(300.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.White.copy(alpha = 0.2f),
                focusedIndicatorColor = Color(0xFFF1C40F),
                cursorColor = Color(0xFFF1C40F),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            textStyle = TextStyle(
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            placeholder = {
                Text("Write here")
            }
        )
        Spacer(Modifier.height(50.dp))
        Button(
            onClick = { onConfirm(logValue.value) },
            colors = ButtonColors(
                containerColor = Color(0xFFF1C40F),
                contentColor = Color(0xFFF1C40F),
                disabledContainerColor = Color(0xFFF1C40F),
                disabledContentColor = Color(0xFFF1C40F)
            )
        ) {
            Text(
                "Save",
                color = Color(0xFF121417),
                fontSize = 15.sp,
                fontFamily = FontFamily(Font(R.font.lexendbold))
            )
        }
        Spacer(Modifier.height(50.dp))
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
                targetValue = if (isCompleted) Color(0xFFF1C40F) else Color.White.copy(alpha = 0.15f),
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
                        spotColor = Color(0xFFF1C40F).copy(alpha = glowAlpha),
                        ambientColor = Color(0xFFF1C40F).copy(alpha = glowAlpha)
                    )
                    .border(
                        width = 0.5.dp,
                        color = Color.White.copy(alpha = 0.2f),
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
    modifier: Modifier,
    isDone: Boolean
) {
    var weightOutlined = weight
    OutlinedTextField(
        value = weightOutlined,
        onValueChange = { input ->

            val filteredInput = input.filter { it.isDigit() || it == '.' }.replace(" ", "")

            val isValid = filteredInput.length <= 5 &&
                    (filteredInput.isEmpty() || filteredInput.first() != '.' || filteredInput.last() != '.') &&
                    filteredInput.count { it == '.' } <= 1

            if (isValid) {
                weightOutlined = filteredInput
                setWeight(filteredInput)
            }
        },
        placeholder = {
            Text(
                "Type",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier
            .width(100.dp)
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedIndicatorColor = if (isDone) {
                Color(0xFFF1C40F)
            } else {
                Color.White.copy(alpha = 0.2f)
            },
            focusedIndicatorColor = Color(0xFFF1C40F),
            cursorColor = Color(0xFFF1C40F),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        textStyle = TextStyle(
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Composable
fun SetLogItemReps(
    reps: String,
    setReps: (String) -> Unit,
    modifier: Modifier,
    isDone: Boolean
) {
    var repsOutlined = reps
    OutlinedTextField(
        value = repsOutlined,
        onValueChange = { input ->

            val filteredInput = input.filter { it.isDigit() || it == '.' }.replace(" ", "")

            val isValid = filteredInput.length <= 5 &&
                    (filteredInput.isEmpty() || filteredInput.first() != '.' || filteredInput.last() != '.') &&
                    filteredInput.count { it == '.' } <= 1

            if (isValid) {
                repsOutlined = filteredInput
                setReps(filteredInput)
            }
        },
        placeholder = {
            Text(
                "Type",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier
            .width(100.dp)
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedIndicatorColor = if (isDone) {
                Color(0xFFF1C40F)
            } else {
                Color.White.copy(alpha = 0.2f)
            },
            focusedIndicatorColor = Color(0xFFF1C40F),
            cursorColor = Color(0xFFF1C40F),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        textStyle = TextStyle(
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Composable
fun FinalWorkoutTimer() {
    val context = LocalContext.current

    // Durum Yönetimi
    var selectedSeconds by remember { mutableLongStateOf(60L) }
    var timeLeft by remember { mutableLongStateOf(60L) }
    var isRunning by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    // Lottie Ayarları
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.clocktimer))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isRunning,
        iterations = LottieConstants.IterateForever
    )

    // Zamanlayıcı Mantığı
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Lottie Animasyonu
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(220.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 2. Dijital Saat (Tıklanabilir)
        Text(
            text = String.format("%02d:%02d", timeLeft / 60, timeLeft % 60),
            modifier = Modifier.clickable {
                if (!isRunning) showEditDialog = true
            },
            style = TextStyle(
                color = if (!isRunning) Color(0xFFF1C40F) else Color.White,
                fontSize = 54.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.lexendbold))
            )
        )

        if (!isRunning) {
            Text(
                text = "Tap to edit time",
                color = Color.Gray,
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.lexendregular))
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Hızlı Ayar Butonları
        if (!isRunning) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                TimeButton("-30s") {
                    if (timeLeft > 30) {
                        timeLeft -= 30; selectedSeconds -= 30
                    }
                }
                TimeButton("+30s") { timeLeft += 30; selectedSeconds += 30 }
                TimeButton("Reset") { timeLeft = 60; selectedSeconds = 60 }
            }
        }

        // 4. Ana Kontrol Butonu
        Button(
            onClick = {
                if (timeLeft == 0L) {
                    timeLeft = selectedSeconds
                }
                isRunning = !isRunning
            },
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRunning) Color(0xFFE53935) else Color(0xFFF1C40F)
            )
        ) {
            Text(
                text = if (isRunning) "STOP WORKOUT" else "START TIMER",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.lexendbold))
            )
        }
    }

    // --- Manuel Süre Giriş Diyalogu ---
    if (showEditDialog) {
        var tempMinutes by remember { mutableStateOf((selectedSeconds / 60).toString()) }
        var tempSeconds by remember { mutableStateOf((selectedSeconds % 60).toString()) }


        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor = Color(0xFF1C2126),
            title = { Text("Set Timer", color = Color.White) },
            text = {
                Column() {
                    OutlinedTextField(
                        value = tempMinutes,
                        onValueChange = { if (it.isDigitsOnly()) tempMinutes = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = TextStyle(color = Color.White),
                        label = { Text("Minutes") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFF1C40F),
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    OutlinedTextField(
                        value = tempSeconds,
                        onValueChange = { if (it.isDigitsOnly()) tempSeconds = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = TextStyle(color = Color.White),
                        label = { Text("Seconds") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFF1C40F),
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                }

            },
            confirmButton = {
                TextButton(onClick = {
                    val mins = tempMinutes.toLongOrNull() ?: 0L
                    val seconds = tempSeconds.toLongOrNull() ?: 0L
                    if (mins != 0L && seconds == 0L) {
                        selectedSeconds = mins * 60
                    } else if (mins == 0L && seconds != 0L) {
                        selectedSeconds = seconds
                    } else if (mins != 0L && seconds != 0L) {
                        selectedSeconds = (mins * 60) + seconds
                    }
                    timeLeft = selectedSeconds
                    showEditDialog = false
                }) {
                    Text("SET", color = Color(0xFFF1C40F))
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("CANCEL", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun TimeButton(label: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f))
    ) {
        Text(label, color = Color.White, fontSize = 12.sp)
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