package homes.inside

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.chargemap.compose.numberpicker.NumberPicker
import com.example.projectfitness.R
import com.google.firebase.firestore.FirebaseFirestore
import database.ProjectCompletedExerciseEntity
import database.ProjectCompletedSetting
import database.ProjectCompletedWorkoutEntity
import database.ProjectFitnessContainer
import database.ProjectFitnessExerciseEntity
import database.ProjectFitnessWorkoutWithExercises
import database.SetRep
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import viewmodel.ViewModelSave
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SuspiciousIndentation", "MutableCollectionMutableState")
@Composable
fun WorkoutLog(navController: NavController, viewModelSave: ViewModelSave) {
    viewModelSave.RestTimer()

    //Database Creation*************************************************************************************************************************************************************
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    var projectFitnessContainer = ProjectFitnessContainer(context)
    val itemRepos = projectFitnessContainer.itemsRepository
    var itemRepoList by remember {
        mutableStateOf<List<ProjectFitnessWorkoutWithExercises?>>(
            emptyList()
        )
    }
    val itemsState by itemRepos.getAllCompletedWorkouts().collectAsState(initial = emptyList())
    var itemrepolist by remember { mutableStateOf(0) }
    var index by remember { mutableStateOf(0) }
    var setRepIndex by remember { mutableStateOf(0) }
    var exerciseList by remember { mutableStateOf<List<ProjectFitnessExerciseEntity?>>(emptyList()) }
    var getWorkoutId by remember { mutableStateOf(0) }
    var getExerciseId = viewModelSave.exerciseId.value
    var getExerciseIds = viewModelSave.exerciseIds.value
    var setrepId by remember { mutableStateOf(0) }
    val scopes = rememberCoroutineScope()
    var weight by remember { mutableFloatStateOf(0f) }
    var exerciseListLoaded by remember { mutableStateOf(false) }
    var setrepList by remember { mutableStateOf<List<ProjectFitnessExerciseEntity>>(emptyList()) }
    var currentExercise by remember { mutableStateOf("") }

    var modalBottomBarWeightValue by remember { mutableStateOf(0) }
    var modalBottomBarRepsValue by remember { mutableStateOf(0) }
    var modalBottomBarSetNumber by remember { mutableStateOf("") }
    var modalBottomBarName by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState()
    var scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(true) }

    var changedProjectFitnessExerciseEntityList by remember {
        mutableStateOf(
            ProjectFitnessExerciseEntity(0, 1, "", 1, 1, mutableListOf())
        )
    }

    var isClicked by remember { mutableStateOf(false) }

    var now: LocalDate = LocalDate.now()
    var year = now.year
    var month = now.monthValue
    var day = now.dayOfMonth

    var calculateTotalVolume by remember { mutableStateOf(0) }
    var calculatetotalVolume by remember { mutableStateOf(0) }


    LaunchedEffect(key1 = null) {
        CoroutineScope(Dispatchers.IO).launch {

            projectFitnessContainer = ProjectFitnessContainer(context)
            var itemRepo = itemRepos

            itemRepoList = itemRepo.getWorkoutWithExercises(viewModelSave.selectedWorkoutName.value)
            getWorkoutId =
                itemRepo.getWorkoutId(viewModelSave.selectedWorkoutName.value)[0]?.workoutId!!
            exerciseList = itemRepo.getExerciseList(getWorkoutId)
            setrepList = itemRepo.getSetRepList(getWorkoutId)


            itemrepolist = itemRepoList.size
            selectedexerciseNameFunction(itemRepoList, index)

            exerciseListLoaded = true
        }
    }

    //******************************************************************************************************************************************************************************

    val sheetStateSettingChange = rememberModalBottomSheetState()
    var showBottomSheetSettingChange by remember { mutableStateOf(true) }

    /* Tasarım Kodları *******************************************************************/

    Box( // Tüm ekran box'ı
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color(0xFF181F26))
    )
    {
        Box( // Üst Bar için Box
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color.Transparent)
        )
        {
            Row( // Üst Bar için Row -> üst bardaki içerikleri tutuyor
                Modifier
                    .align(Alignment.CenterStart)
                    .background(Color.Transparent)
            ) {
                Icon(painter = painterResource(id = R.drawable.projectfitnessprevious),
                    contentDescription = null,
                    tint = Color(0xFFF1C40F),
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .size(30.dp)
                        .clickable {
                            viewModelSave.hourInt.value = 0
                            viewModelSave.minuteInt.value = 0
                            viewModelSave.secondInt.value = 0
                            showBottomSheet = true
                            scopes.launch { sheetState.expand() }
                        }
                        .align(Alignment.CenterVertically)
                )

                Text(
                    text = "Workout",
                    color = Color(0xFFF1C40F),
                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                    style = TextStyle(fontSize = 30.sp),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 20.dp),
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = timerWorkout(viewModelSave),
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                    style = TextStyle(fontSize = 15.sp, letterSpacing = 2.sp),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 30.dp),
                )

            }


        }

        Box(
            modifier = Modifier // Lazy column ve geri kalan ögeleri tutan box
                .fillMaxWidth()
                .height(550.dp)
                .align(Alignment.Center)
                .background(Color.Transparent)
        ) {

            LazyColumn(
                state = rememberLazyListState(),
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxHeight()

            ) {
                itemsIndexed(exerciseList) { index, item ->

                    Column(  // Egzersiz ismi ve altındaki 3 lü Row ögelerini taşıyan Column
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        if (item != null) {
                            currentExercise = item.exercisesName
                        }
                        if (item != null) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp)
                            ) {
                                Text(
                                    text = item.exercisesName,
                                    modifier = Modifier.padding(top = 10.dp, start = 20.dp),
                                    color = Color(0xFFF1C40F),
                                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                    style = TextStyle(fontSize = 25.sp),
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(painter = painterResource(id = R.drawable.question),
                                    contentDescription = null,
                                    tint = Color(0xFFF1C40F),
                                    modifier = Modifier
                                        .padding(end = 20.dp)
                                        .size(25.dp)
                                        .align(Alignment.CenterVertically)
                                        .clickable {
                                            viewModelSave.flagA.value = false
                                            viewModelSave.flagB.value = true
                                            viewModelSave.flagC.value = false
                                            viewModelSave.flagD.value = false
                                            navController.navigate("workoutsettingdetails")
                                        }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.size(5.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                        ) {
                            Text(
                                text = "Prev Season",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    letterSpacing = 2.sp,
                                    fontFamily = FontFamily(Font(R.font.postnobillscolombobold)),
                                    color = (Color.White).copy(alpha = 0.5f)
                                ),
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 40.dp)

                            )
                            Spacer(modifier = Modifier.size(10.dp))
                            Text(
                                text = "Weight (kg)",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    letterSpacing = 2.sp,
                                    fontFamily = FontFamily(Font(R.font.postnobillscolombobold)),
                                    color = (Color.White).copy(alpha = 0.5f)
                                ),
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(end = 15.dp)

                            )
                            Spacer(modifier = Modifier.size(0.dp))
                            Text(
                                text = "Reps",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    letterSpacing = 2.sp,
                                    fontFamily = FontFamily(Font(R.font.postnobillscolombobold)),
                                    color = (Color.White).copy(alpha = 0.5f)
                                ),
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(end = 15.dp)

                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .background(
                                Color.Transparent
                            )
                    )
                    {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth()
                                .background(Color.Transparent)
                        ) {
                            if (item != null) {
                                setrepList.forEachIndexed { index, item ->
                                    if (item.exercisesName == currentExercise) {
                                        for (i in item.setrepList) {
                                            /*
                                            val annotatedString = buildAnnotatedString {
                                                append("${i.setNumber.last()}            ")
                                                withStyle(
                                                    style = SpanStyle(
                                                        color = Color.White.copy(
                                                            alpha = 0.5f
                                                        )
                                                    )
                                                ) {
                                                    append("${i.weight}")
                                                }

                                                append("                   ${i.weight.toInt()}                   ")
                                                append("${i.setRep}")

                                            }
                                            */
                                            Row {
                                                Text(
                                                    text = i.setNumber.last().toString(),
                                                    style = TextStyle(
                                                        fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                                        fontSize = 17.sp
                                                    ),
                                                    modifier = Modifier
                                                        .padding(start = 20.dp),
                                                    color = Color.White
                                                )
                                                Spacer(modifier = Modifier.size(30.dp))
                                                Text(
                                                    text = i.weight.toInt().toString() + " kg",
                                                    style = TextStyle(
                                                        fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                                        fontSize = 17.sp
                                                    ),
                                                    modifier = Modifier
                                                        .padding(start = 20.dp),
                                                    color = Color.White
                                                )
                                                Spacer(modifier = Modifier.size(30.dp))
                                                Text(
                                                    text = i.weight.toInt().toString() + " kg",
                                                    style = TextStyle(
                                                        fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                                        fontSize = 17.sp
                                                    ),
                                                    modifier = Modifier
                                                        .padding(start = 20.dp),
                                                    color = Color.White
                                                )
                                                Spacer(modifier = Modifier.size(40.dp))
                                                Text(
                                                    text = i.setRep.toString(),
                                                    style = TextStyle(
                                                        fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                                        fontSize = 17.sp
                                                    ),
                                                    modifier = Modifier
                                                        .padding(start = 20.dp),
                                                    color = Color.White
                                                )
                                                Spacer(modifier = Modifier.size(40.dp))
                                                Icon(
                                                    painter = painterResource(id = R.drawable.projectfitnesssetting),
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .size(20.dp)
                                                        .clickable {
                                                            modalBottomBarWeightValue =
                                                                i.weight.toInt()
                                                            modalBottomBarRepsValue = i.setRep
                                                            modalBottomBarSetNumber =
                                                                i.setNumber
                                                            modalBottomBarName =
                                                                item.exercisesName
                                                            changedProjectFitnessExerciseEntityList =
                                                                item // Seçilen itemin entity bilgilerini çeker
                                                            showBottomSheetSettingChange = true
                                                        },
                                                    tint = Color.White.copy(alpha = 0.8f)
                                                )
                                            }

                                            Spacer(modifier = Modifier.size(20.dp))
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clickable {
                                                    val newSetRep = SetRep(
                                                        setNumber = "${item.setrepList.size + 1}",
                                                        setRep = 12,
                                                        ticked = false,
                                                        weight = 0.0f
                                                    )
                                                    item.setrepList
                                                        .toMutableList()
                                                        .apply { add(newSetRep) }
                                                    scopes.launch { itemrepolist }
                                                    item.setrepList.add(
                                                        SetRep(
                                                            "${item.setrepList.size + 1}",
                                                            12,
                                                            ticked = false,
                                                            0.0f
                                                        )
                                                    )


                                                }
                                                .padding(start = 16.dp, end = 16.dp)
                                                .fillMaxWidth()// İsteğe bağlı: kutuyu etrafında bir boşluk bırakır
                                            , contentAlignment = Alignment.TopCenter
                                        )
                                        {
                                            Text(
                                                text = "Add Set",
                                                style = TextStyle(
                                                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                                    fontSize = 17.sp
                                                ),
                                                modifier = Modifier
                                                    .align(Alignment.Center),
                                                color = Color(0xFFF1C40F)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(50.dp))
                }

            }
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1C40F)),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp)
                    .size(40.dp),
                contentPadding = PaddingValues(0.dp)
            ) {

                Icon(painter = painterResource(id = R.drawable.projectfitnesstick),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            scopes.launch {
                                setrepList.forEachIndexed { index, item ->
                                    item.setrepList.forEachIndexed { index, setRep ->
                                        calculateTotalVolume += (setRep.weight.toInt() * setRep.setRep)
                                    }
                                    // Completed Workout Database Insert
                                    itemRepos.insertCompletedWorkout(
                                        completedWorkout = ProjectCompletedWorkoutEntity(
                                            completedWorkoutId = viewModelSave.completedWorkoutId.value,
                                            workoutId = getWorkoutId,
                                            completionDate = "${day}.${month}.${year}",
                                            durationMinutes = viewModelSave.hour.value + viewModelSave.minutes.value + viewModelSave.seconds.value,
                                            totalSets = 0,
                                            totalReps = 0,
                                            rateOfWorkout = 0,
                                            notesAboutWorkout = "",
                                            completedWorkoutName = viewModelSave.selectedWorkoutName.value,
                                            totalWorkoutVolume = calculateTotalVolume
                                        )
                                    )
                                }
                                itemRepos.insertCompletedSetting(
                                    savedcompletedWorkoutId = ProjectCompletedSetting(
                                        viewModelSave.completedWorkoutId.value
                                    )
                                )
                                // Completed Exercise Database Insert
                                setrepList.forEachIndexed { index, item ->
                                    calculatetotalVolume = 0
                                    item.setrepList.forEachIndexed { index, setRep ->
                                        calculatetotalVolume += (setRep.weight.toInt() * setRep.setRep)
                                    }
                                    itemRepos.insertCompletedExercise(
                                        ProjectCompletedExerciseEntity(
                                            completedWorkoutId = viewModelSave.completedWorkoutId.value,
                                            completedExerciseId = getExerciseId,
                                            exerciseName = item.exercisesName,
                                            exerciseRep = 12,
                                            exerciseSet = 3,
                                            setrepListCompleted = item.setrepList,
                                            totalExerciseVolume = calculatetotalVolume
                                        )
                                    )
                                    getExerciseId += 1
                                    Log.d("KAYIT", "id $getExerciseId")
                                    viewModelSave.exerciseId.value = getExerciseId
                                    // Some features of Workout
                                    viewModelSave.totalSetsOfCompletedWorkout.value += item.setrepList.size
                                    for (i in item.setrepList) {
                                        viewModelSave.totalRepsOfCompletedWorkout.value += i.setRep
                                        viewModelSave.totalWeightOfCompletedWorkout.value += (i.weight.toInt() * i.setRep)
                                    }
                                    Log.d(
                                        "lolsa",
                                        viewModelSave.totalSetsOfCompletedWorkout.value.toString()
                                    )

                                }
                            }
                            navController.navigate(route = "workoutcompletescreen")
                            viewModelSave.completedWorkoutId.value += 1
                        })
            }
        }
    }

    if (showBottomSheetSettingChange) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheetSettingChange = false },
            sheetState = sheetStateSettingChange,
            containerColor = Color(0xFF283747)
        ) {
            LaunchedEffect(Unit) {
                scopes.launch { sheetStateSettingChange.expand() }.invokeOnCompletion {
                    if (!sheetStateSettingChange.isVisible) {
                        showBottomSheetSettingChange = false
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            )
            {
                Row(modifier = Modifier.align(Alignment.TopCenter)) {
                    Text(
                        text = "WEIGHT",
                        style = TextStyle(
                            fontSize = 20.sp,
                            letterSpacing = 2.sp,
                            fontFamily = FontFamily(Font(R.font.postnobillscolombobold)),
                            color = Color.White
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(end = 140.dp)

                    )
                    Text(
                        text = "REP",
                        style = TextStyle(
                            fontSize = 20.sp,
                            letterSpacing = 2.sp,
                            fontFamily = FontFamily(Font(R.font.postnobillscolombobold)),
                            color = Color.White
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(end = 15.dp)

                    )
                }
                Row(modifier = Modifier.align(Alignment.Center)) {
                    NumberPicker(
                        value = modalBottomBarWeightValue,
                        onValueChange = {
                            modalBottomBarWeightValue = it

                            scopes.launch {
                                val newSetRepList = SetRep(
                                    modalBottomBarSetNumber,
                                    modalBottomBarRepsValue,
                                    ticked = false,
                                    modalBottomBarWeightValue.toFloat()
                                )
                                val updatedEntity =
                                    changedProjectFitnessExerciseEntityList // Seçilen egzersiz entity'sini çeker
                                updatedEntity.setrepList.forEachIndexed { index, item ->
                                    if (index == modalBottomBarSetNumber.last().digitToInt() - 1) {
                                        Log.d("values", modalBottomBarSetNumber.last().toString())
                                        item.weight =
                                            modalBottomBarWeightValue.toFloat()
                                    }
                                }
                                // updatedEntity.setrepList.toMutableList().apply { add(newSetRepList) }
                                itemRepos.updateSetRepList(updatedEntity)
                                Log.d("value", "${newSetRepList} and $updatedEntity")
                            }
                        },
                        range = 0..500 step (5), // step fonkisyonu ile 5'er arttırma yapıldı
                        dividersColor = Color(0xFFF1C40F),
                        textStyle = TextStyle(color = Color.White),

                        )
                    Text(
                        text = "KG",
                        style = TextStyle(
                            fontSize = 15.sp,
                            letterSpacing = 2.sp,
                            fontFamily = FontFamily(Font(R.font.postnobillscolombobold)),
                            color = Color.White
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 10.dp, bottom = 7.dp)
                    )
                    Spacer(modifier = Modifier.size(130.dp))
                    NumberPicker(
                        value = modalBottomBarRepsValue,
                        onValueChange = {
                            modalBottomBarRepsValue = it
                            scopes.launch {
                                val newSetRepList = SetRep(
                                    modalBottomBarSetNumber,
                                    modalBottomBarRepsValue,
                                    ticked = false,
                                    modalBottomBarWeightValue.toFloat()
                                )
                                val updatedEntity =
                                    changedProjectFitnessExerciseEntityList // Seçilen egzersiz entity'sini çeker
                                updatedEntity.setrepList.forEachIndexed { index, item ->
                                    if (index == modalBottomBarSetNumber.last().digitToInt() - 1) {
                                        Log.d("values", modalBottomBarSetNumber.last().toString())
                                        item.setRep =
                                            modalBottomBarRepsValue
                                    }
                                }
                                // updatedEntity.setrepList.toMutableList().apply { add(newSetRepList) }
                                itemRepos.updateSetRepList(updatedEntity)
                                Log.d("value", "${newSetRepList} and $updatedEntity")
                            }
                        },
                        range = 0..50,
                        dividersColor = Color(0xFFF1C40F),
                        textStyle = TextStyle(color = Color.White),

                        )
                }
                Row(modifier = Modifier.align(Alignment.BottomEnd)) {
                    Text(text = "Remove Set",
                        style = TextStyle(
                            fontSize = 15.sp,
                            letterSpacing = 2.sp,
                            fontFamily = FontFamily(Font(R.font.postnobillscolombobold)),
                            color = Color.Red.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(end = 15.dp, bottom = 15.dp)
                            .clickable {
                                scopes.launch {
                                    val removedEntity =
                                        changedProjectFitnessExerciseEntityList // elemanı silinecek olan entity
                                    removedEntity.setrepList.forEachIndexed { index, item ->
                                        if (index == modalBottomBarSetNumber
                                                .last()
                                                .digitToInt() - 1
                                        ) {
                                            removedEntity.setrepList.remove(item)
                                        }
                                    }
                                    itemRepos.updateSetRepList(removedEntity)
                                    sheetStateSettingChange.hide()
                                    showBottomSheetSettingChange = false
                                }
                            }
                            .width(100.dp)
                            .height(25.dp)
                            .background(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.Transparent
                            )
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "LOG",
                        style = TextStyle(
                            fontSize = 20.sp,
                            letterSpacing = 2.sp,
                            fontFamily = FontFamily(Font(R.font.postnobillscolombobold)),
                            color = Color.White,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(end = 15.dp, bottom = 15.dp)
                            .clickable {
                                scopes.launch {
                                    sheetStateSettingChange.hide()
                                    showBottomSheetSettingChange = false
                                }
                            }
                            .width(50.dp)
                            .height(25.dp)
                            .background(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.Transparent
                            )

                    )
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = Color(0xFF283747)
        ) {
            LaunchedEffect(Unit) {
                scope.launch { sheetState.expand() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showBottomSheet = false
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
            {
                Column(modifier = Modifier.align(Alignment.TopCenter))
                {
                    Text(
                        text = "Do you want to leave workout ?",
                        style = TextStyle(
                            fontSize = 25.sp,
                            fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold))
                        ),
                        color = Color(0xFFF1C40F),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .align(Alignment.CenterHorizontally)
                            .background(Color.Transparent)
                            .padding(top = 20.dp)
                    ) {
                        Text(text = "Yes",
                            style = TextStyle(fontFamily = FontFamily(Font(R.font.postnobillscolomboregular))),
                            fontSize = 25.sp,
                            modifier = Modifier
                                .padding(start = 100.dp)
                                .clickable { navController.navigate("home") },
                            color = Color(0xFFF1C40F)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "No",
                            style = TextStyle(fontFamily = FontFamily(Font(R.font.postnobillscolomboregular))),
                            fontSize = 25.sp,
                            modifier = Modifier
                                .padding(end = 100.dp)
                                .clickable {
                                    showBottomSheet = false
                                    scopes.launch { sheetState.hide() }
                                },
                            color = Color(0xFFF1C40F)
                        )
                    }
                }

            }
        }
    }

}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun timerWorkout(viewModelSave: ViewModelSave): String {
    var hour by remember { mutableStateOf(viewModelSave.hourInt) }
    var second by remember { mutableStateOf(viewModelSave.secondInt) }
    var minute by remember { mutableStateOf(viewModelSave.minuteInt) }
    LaunchedEffect(key1 = second)
    {
        while (hour.value >= 0) {
            delay(1000L)
            second.value++
            if (minute.value == 60) {
                hour.value++
                minute.value = 0
            } else if (second.value == 60) {
                minute.value++
                second.value = 0
            }
        }
    }
    return " %2d h : %2d m : %2d s ".format(hour.value, minute.value, second.value)
}


fun selectedexerciseNameFunction(
    itemRepoList: List<ProjectFitnessWorkoutWithExercises?>,
    index: Int,
): String {

    return if (itemRepoList.isEmpty()) {
        ""
    } else
        itemRepoList[index]?.exercises2?.get(index)?.exercisesName.toString()

}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showSystemUi = false)
@Composable
fun WorkoutLogPreview() {
    WorkoutLog(navController = rememberNavController(), viewModel())
}

/*
Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth()
                                .background(Color.Transparent)
                        ) {
                            if (item != null) {
                                setrepList.forEachIndexed { index, item ->
                                    if (item.exercisesName == currentExercise) {
                                            for (i in item.setrepList) {
                                                val annotatedString = buildAnnotatedString {
                                                    append("${i.setNumber.last()}            ")
                                                    withStyle(
                                                        style = SpanStyle(
                                                            color = Color.White.copy(
                                                                alpha = 0.5f
                                                            )
                                                        )
                                                    ) {
                                                        append("${i.weight}")
                                                    }

                                                    append("                   ${i.weight.toInt()}                   ")
                                                    append("${i.setRep}")

                                                }
                                                Row {
                                                    Text(
                                                        text = annotatedString,
                                                        style = TextStyle(
                                                            fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                                            fontSize = 17.sp
                                                        ),
                                                        modifier = Modifier
                                                            .padding(start = 20.dp),
                                                        color = Color.White
                                                    )
                                                    Spacer(modifier = Modifier.size(40.dp))
                                                    Icon(painter = painterResource(id = R.drawable.projectfitnesssetting),
                                                        contentDescription = null,
                                                        modifier = Modifier
                                                            .size(20.dp)
                                                            .clickable {
                                                                modalBottomBarWeightValue =
                                                                    i.weight.toInt()
                                                                modalBottomBarRepsValue = i.setRep
                                                                modalBottomBarSetNumber =
                                                                    i.setNumber
                                                                modalBottomBarName =
                                                                    item.exercisesName
                                                                changedProjectFitnessExerciseEntityList =
                                                                    item // Seçilen itemin entity bilgilerini çeker
                                                                showBottomSheetSettingChange = true
                                                            },
                                                        tint = Color.White.copy(alpha = 0.8f))
                                                }

                                                Spacer(modifier = Modifier.size(20.dp))
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .clickable {
                                                        val newSetRep = SetRep(
                                                            setNumber = "${item.setrepList.size + 1}",
                                                            setRep = 12,
                                                            ticked = false,
                                                            weight = 0.0f
                                                        )
                                                        item.setrepList
                                                            .toMutableList()
                                                            .apply { add(newSetRep) }
                                                        scopes.launch { itemrepolist }
                                                        item.setrepList.add(
                                                            SetRep(
                                                                "${item.setrepList.size + 1}",
                                                                12,
                                                                ticked = false,
                                                                0.0f
                                                            )
                                                        )


                                                    }
                                                    .padding(start = 16.dp, end = 16.dp)
                                                    .fillMaxWidth()// İsteğe bağlı: kutuyu etrafında bir boşluk bırakır
                                                , contentAlignment = Alignment.TopCenter
                                            )
                                            {
                                                Text(
                                                    text = "Add Set",
                                                    style = TextStyle(
                                                        fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                                        fontSize = 17.sp
                                                    ),
                                                    modifier = Modifier
                                                        .align(Alignment.Center),
                                                    color = Color(0xFFF1C40F)
                                                )
                                            }
                                    }
                                }
                            }
                        }
 */