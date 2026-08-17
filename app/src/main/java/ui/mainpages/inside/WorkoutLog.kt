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
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
                        Box(
                            modifier = Modifier.height(70.dp).fillMaxWidth().padding(horizontal = 20.dp).background(Color.Gray.copy(alpha = 0.1f), shape = RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(modifier = Modifier.fillMaxSize().padding(horizontal = 30.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                    FinalWorkoutTimer()
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    itemsIndexed(
                        items = currentSets,
                        key = { _, item -> item.setId }
                    ) { index, item ->
                        val isDone = item.isClicked
                        var rowWeight by remember(item.setId) { mutableStateOf(if (item.weight > 0) item.weight.toString() else "0") }
                        var rowReps by remember(item.setId) { mutableStateOf(if (item.reps > 0) item.reps.toString() else "0") }
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
                                            .background(Color.Transparent)
                                            .padding(horizontal = 25.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "${index + 1}",
                                            modifier = Modifier.width(30.dp),
                                            color = Color.White,
                                            fontSize = 15.sp
                                        )
                                        Box(modifier = Modifier.weight(1f)) {
                                            SetLogItemWeight(rowWeight, {
                                                rowWeight = it; scope.launch {
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
                                                workoutLogViewModel.toggleSetDone(
                                                    exercise.exercise.exerciseName,
                                                    index,
                                                    false
                                                )
                                            }
                                            }, Modifier.fillMaxWidth(), isDone)
                                        }
                                        Spacer(modifier = Modifier.width(25.dp))
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
                                                .size(18.dp)
                                                .clip(RoundedCornerShape(25))
                                                .background(if (isDone) Color(0xFFF1C40F) else Color.Transparent)
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
                        Spacer(modifier = Modifier.height(20.dp))
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
                            modifier = Modifier.padding(horizontal = 20.dp).background(Color.Gray.copy(alpha = 0.3f), shape = RoundedCornerShape(12.dp)).fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                Text("+", color = Color(0xFFF1C40F), fontSize = 24.sp)
                                Spacer(Modifier.width(10.dp))
                                Text("ADD SET", color = Color.Gray, fontFamily = FontFamily(Font(R.font.lexendextrabold)))
                            }
                        }
                    }
                    item { Spacer(Modifier.height(100.dp)) }
                }
            }

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
                    dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) }
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
                        .padding(bottom = 30.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Button(
                        onClick = {
                            showBottomSheetFinish = true
                        },
                        modifier = Modifier.padding(horizontal = 20.dp).background(Color.Gray.copy(alpha = 0.3f), shape = RoundedCornerShape(12.dp)).fillMaxWidth().height(50.dp),
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
                                fontFamily = FontFamily(Font(R.font.lexendextrabold)),
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
    // String parçalama hatasını önlemek için kontrol (tek kelimelik isimlerde crash olmasın)
    val wordList = exerciseName.split(" ", limit = 2)
    val firstWord = wordList.getOrNull(0) ?: ""
    val secondWord = wordList.getOrNull(1) ?: ""

    val fullUrl = remember(exerciseImage) {
        FirebaseStorageHelper.getImageUrl(exerciseImage ?: "")
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp) // Toplam yükseklik
    ) {
        // 1. En Altta: Görsel
        AsyncImage(
            model = fullUrl,
            contentDescription = exerciseName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(), // Padding'i kaldırdım ki Box'ı tam doldursun
            placeholder = painterResource(id = R.drawable.grozzlogo),
            error = painterResource(id = R.drawable.grozzlogo)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .align(Alignment.TopCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)
                    )
                )
        )

        // Alt Gradient (Yazının okunması için daha koyu)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color(0xFF121417).copy(alpha = 0.9f))
                    )
                )
        )

        // 3. Yazı Katmanı (Şimdi Box içinde olduğu için resmin üstünde!)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart) // Yazıyı sol alta hizalar
                .padding(horizontal = 25.dp, vertical = 20.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = firstWord.uppercase(),
                color = Color.White,
                fontSize = 30.sp,
                fontFamily = FontFamily(Font(R.font.lexendextrabold)),
                lineHeight = 30.sp
            )
            Text(
                text = secondWord.uppercase(),
                color = Color(0xFFF1C40F),
                fontSize = 30.sp,
                fontFamily = FontFamily(Font(R.font.lexendextrabold)),
                lineHeight = 30.sp,
                modifier = Modifier.graphicsLayer(translationY = -10f) // Aradaki boşluğu daraltmak için
            )
        }
    }
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
                    tint = Color.Transparent
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
                    tint = Color.Transparent
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF121417))
            .statusBarsPadding()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        // 1. SOL: Geri Butonu
        Icon(
            imageVector = Icons.Default.KeyboardArrowLeft,
            contentDescription = null,
            tint = Color(0xFFF1C40F),
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.CenterStart)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onBackClick()
                }
        )

        // 2. ORTA: Logo ve Sayaç (Dikey Hizalama)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            // Bu kısım önemli: Spacer yerine Arrangement kullanıyoruz
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Image(
                painter = painterResource(R.drawable.grozzlogo),
                contentDescription = null,
                modifier = Modifier
                    .height(55.dp) // Genişlik yerine yükseklik sabitlemek daha dengeli durur
                    .fillMaxWidth(0.4f), // Logonun ekranın %30'undan fazla yer kaplamasını engeller
                contentScale = ContentScale.Fit
            )

            // Sayaç Barı (1 of 3)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        color = Color.Transparent, // Timer ile aynı arka plan rengi
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .background(Color(0xFFF1C40F), androidx.compose.foundation.shape.CircleShape)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "${pagerState.currentPage + 1} of $totalSegments",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.lexendbold))
                )
            }
        }

        // 3. SAĞ: Zamanlayıcı
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .background(Color(0xFF1C2126), RoundedCornerShape(15.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
//                .clickable { onTimerClick() }
            ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(4.dp).background(Color(0xFFF1C40F), androidx.compose.foundation.shape.CircleShape))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = formattedTime,
                color = Color(0xFFF1C40F),
                fontSize = 11.sp,
                fontFamily = FontFamily(Font(R.font.lexendbold))
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
            IconButton(
                onClick = { onDismiss() },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .background(color = Color(0xFFF1C40F), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(Modifier.height(20.dp))
            Text(
                "End Workout?",
                color = Color.White,
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.lexendextrabold))
            )
            Spacer(Modifier.height(20.dp))
            Text(
                "You will lose all progress for this session. This action cannot be undone.",
                color = Color.White.copy(alpha = 0.3f),
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.lexendregular)),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = { onConfirm() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF1C40F),
                    contentColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth() ) {
                Text(
                    "End",
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.lexendbold))
                )
            }

            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                )
            ) {
                Text(
                    "Cancel",
                    color = Color.Red.copy(alpha = 0.6f),
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.lexendbold))
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
            IconButton(
                onClick = { onDismiss() },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .background(color = Color(0xFFF1C40F), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(Modifier.height(20.dp))
            Text(
                "Complete Workout?",
                color = Color.White,
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.lexendextrabold))
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = { onConfirm() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF1C40F),
                    contentColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth() ) {
                Text(
                    "Complete",
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.lexendbold))
                )
            }
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                )
            ) {
                Text(
                    "Go Back",
                    color = Color.Red.copy(alpha = 0.6f),
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.lexendbold))
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
    modifier: Modifier = Modifier,
    isDone: Boolean
) {
    TextField(
        value = weight,
        onValueChange = { input ->
            val filteredInput = input.filter { it.isDigit() || it == '.' }.replace(" ", "")
            val isValid = filteredInput.length <= 5 &&
                    (filteredInput.isEmpty() || (filteredInput.isNotEmpty() && filteredInput.first() != '.')) &&
                    filteredInput.count { it == '.' } <= 1

            if (isValid) {
                setWeight(filteredInput)
            }
        },
        placeholder = {
            Text(
                "KG",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier
            .width(50.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            cursorColor = Color(0xFFF1C40F),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedIndicatorColor = Color(0xFFF1C40F),
            unfocusedIndicatorColor = if (isDone) Color(0xFFF1C40F) else Color.DarkGray,
        ),
        textStyle = TextStyle(
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
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
                    (filteredInput.isEmpty() || (filteredInput.isNotEmpty() && filteredInput.first() != '.')) &&
                    filteredInput.count { it == '.' } <= 1

            if (isValid) {
                setReps(filteredInput)
            }
        },
        placeholder = {
            Text(
                "0",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier
            .width(50.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            cursorColor = Color(0xFFF1C40F),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedIndicatorColor = Color(0xFFF1C40F),
            unfocusedIndicatorColor = if (isDone) Color(0xFFF1C40F) else Color.White.copy(alpha = 0.3f),
        ),
        textStyle = TextStyle(
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
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
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "REST TIMER",
                color = Color.Gray,
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.lexendbold))
            )

            Text(
                text = String.format("%02d:%02d", timeLeft / 60, timeLeft % 60),
                modifier = Modifier.clickable {
                    if (!isRunning) showEditDialog = true
                },
                color = Color(0xFFF1C40F),
                fontSize = 30.sp,
                fontFamily = FontFamily(Font(R.font.lexendextrabold))
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
            modifier = Modifier,
            colors = IconButtonColors(containerColor = if (isRunning) Color(0xFFE53935) else Color(0xFFF1C40F), contentColor = Color.White, disabledContainerColor = Color.Red, disabledContentColor = Color.White)
        ) {
            Icon(
                painter = painterResource(if (isRunning) R.drawable.pauseicon128 else R.drawable.playicon128),
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier
            )
        }
        Spacer(Modifier.width(10.dp))
        IconButton(
            onClick = {
                timeLeft = selectedSeconds
                isRunning = false
            },
            modifier = Modifier.size(24.dp),
            colors = IconButtonColors(containerColor = Color.Gray.copy(alpha = 0.3f), contentColor = Color.White, disabledContainerColor = Color.Red, disabledContentColor = Color.White)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
            )
        }
//        Button(
//            onClick = {
//                if (timeLeft == 0L) {
//                    timeLeft = selectedSeconds
//                }
//                isRunning = !isRunning
//            },
//            modifier = Modifier
//                .fillMaxWidth(0.85f)
//                .height(56.dp),
//            shape = RoundedCornerShape(14.dp),
//            colors = ButtonDefaults.buttonColors(
//                containerColor = if (isRunning) Color(0xFFE53935) else Color(0xFFF1C40F)
//            )
//        ) {
//            Text(
//                text = if (isRunning) "STOP WORKOUT" else "START TIMER",
//                color = Color.Black,
//                fontWeight = FontWeight.Bold,
//                fontFamily = FontFamily(Font(R.font.lexendbold))
//            )
//        }
    }


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
                        label = { Text("Minutes", color = Color(0xFFF1C40F)) },
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
                        label = { Text("Seconds", color = Color(0xFFF1C40F)) },
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

fun vibratePhone(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(500)
    }
}