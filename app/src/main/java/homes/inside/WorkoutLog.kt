package homes.inside

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.chargemap.compose.numberpicker.NumberPicker
import com.example.projectfitness.R
import database.ProjectFitnessContainer
import database.ProjectFitnessExerciseEntity
import database.ProjectFitnessWorkoutWithExercises
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import viewmodel.ViewModelSave

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SuspiciousIndentation", "MutableCollectionMutableState")
@Composable
fun WorkoutLog(navController: NavController, viewModelSave: ViewModelSave) {

    //Database Creation*************************************************************************************************************************************************************
    val context = LocalContext.current
    var projectFitnessContainer = ProjectFitnessContainer(context)
    val itemRepos = projectFitnessContainer.itemsRepository
    var itemRepoList by remember {
        mutableStateOf<List<ProjectFitnessWorkoutWithExercises?>>(
            emptyList()
        )
    }
    var itemrepolist by remember { mutableStateOf(0) }
    var index by remember { mutableStateOf(0) }
    var setRepIndex by remember { mutableStateOf(0) }
    var exerciseList by remember { mutableStateOf<List<ProjectFitnessExerciseEntity?>>(emptyList()) }
    var getWorkoutId by remember { mutableStateOf(0) }
    var setrepId by remember { mutableStateOf(0) }
    val scopes = rememberCoroutineScope()
    var weight by remember { mutableFloatStateOf(0f) }
    var exerciseListLoaded by remember { mutableStateOf(false) }
    var setrepList by remember { mutableStateOf<List<ProjectFitnessExerciseEntity>>(emptyList()) }
    var currentExercise by remember { mutableStateOf("") }

    LaunchedEffect(key1 = null) {
        CoroutineScope(Dispatchers.IO).launch {

            projectFitnessContainer = ProjectFitnessContainer(context)
            var itemRepo = itemRepos

            itemRepoList = itemRepo.getWorkoutWithExercises(viewModelSave.selectedWorkoutName.value)
            getWorkoutId =
                itemRepo.getWorkoutId(viewModelSave.selectedWorkoutName.value)[0]?.workoutId!!
            exerciseList = itemRepo.getExerciseList(getWorkoutId)
            setrepList = itemRepo.getSetRepList(getWorkoutId)




            Log.d("SETREP", "is $setrepList")

            itemrepolist = itemRepoList.size
            selectedexerciseNameFunction(itemRepoList, index)

            exerciseListLoaded = true
        }
    }

    //******************************************************************************************************************************************************************************

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showBottomSheetSetting by remember { mutableStateOf(false) }

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
        )
        {
            Row( // Üst Bar için Row -> üst bardaki içerikleri tutuyor
                Modifier
                    .align(Alignment.CenterStart)
                    .background(Color.Transparent)
            ) {
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
                    text = "Workout Time",
                    color = Color(0xFFF1C40F),
                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                    style = TextStyle(fontSize = 15.sp, letterSpacing = 2.sp),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 90.dp),
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
                            Text(
                                text = item.exercisesName,
                                modifier = Modifier.padding(top = 10.dp, start = 20.dp),
                                color = Color(0xFFF1C40F),
                                fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                style = TextStyle(fontSize = 25.sp),
                            )
                        }
                        Spacer(modifier = Modifier.size(5.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                        ) {
                            Text(
                                text = "Prev Result",
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
                            Spacer(modifier = Modifier.weight(1f))
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
                            Spacer(modifier = Modifier.size(5.dp))
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
                            .height(129.dp)
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

                                            val annotatedString = buildAnnotatedString {
                                                append("${i.setNumber.last()}            ")
                                                withStyle(style = SpanStyle(color = Color.White.copy(alpha = 0.5f))) {
                                                    append("${i.weight}")
                                                }

                                                append("                                         ${i.weight}                    ")
                                                append("${i.setRep}")
                                            }

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
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(50.dp))
                }

            }

        }
    }

    if (showBottomSheetSetting) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheetSetting = false },
            sheetState = sheetState,
            containerColor = Color(0xFF283747)
        ) {
            LaunchedEffect(Unit) {
                scopes.launch { sheetState.expand() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showBottomSheetSetting = false
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
                        value = 1,
                        onValueChange = {/*
                            //setrepList[setRepIndex].weight = it.toFloat()
                            scopes.launch {
                                itemRepos.updateSetRep(
                                    ProjectFitnessSetRepEntity(setrepId = 2, setrepList)
                                )
                            }*/
                        },
                        range = 0..500,
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
                        value = 0,
                        onValueChange = {
                        },
                        range = 0..50,
                        dividersColor = Color(0xFFF1C40F),
                        textStyle = TextStyle(color = Color.White),

                        )
                }
                Row(modifier = Modifier.align(Alignment.BottomEnd)) {
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
                            .clickable {}
                            .width(50.dp)
                            .height(25.dp)
                            .background(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF181F26)
                            )

                    )
                }
            }
        }
    }

}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun timerWorkout(): String {
    var second by remember { mutableStateOf(0) }
    var minute by remember { mutableStateOf(0) }
    LaunchedEffect(key1 = second)
    {
        while (second >= 0) {
            delay(1000L)
            second++
            if (second.equals(60)) {
                minute++
                second = 0
            }
        }
    }
    return "%02d:%02d".format(minute, second)
}

@Composable
fun RestTimer(): String {
    var second by remember { mutableStateOf(0) }
    var minute by remember { mutableStateOf(0) }
    LaunchedEffect(key1 = second)
    {
        while (second >= 0) {
            delay(1000L)
            second++
            if (second.equals(0)) {
                minute--
                second = 59
            }
        }
    }
    return "%02d:%02d".format(minute, second)
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

@Preview(showSystemUi = false)
@Composable
fun WorkoutLogPreview() {
    WorkoutLog(navController = rememberNavController(), viewModel())
}