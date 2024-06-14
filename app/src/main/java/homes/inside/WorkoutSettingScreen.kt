package homes.inside

import ItemsRepository
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
import kotlinx.coroutines.launch
import navigation.Screens
import viewmodel.ViewModelSave

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutSettingScreen(navController: NavController, viewModelSave: ViewModelSave) {

    //Database Creation*************************************************************************************************************************************************************
    val context = LocalContext.current
    var projectFitnessContainer = ProjectFitnessContainer(context)
    val itemRepos = projectFitnessContainer.itemsRepository
    var itemRepoList by remember {
        mutableStateOf<List<ProjectFitnessWorkoutWithExercises?>>(
            emptyList()
        )
    }
    var itemRepo by remember { mutableStateOf("") }
    LaunchedEffect(key1 = null) {
        CoroutineScope(Dispatchers.IO).launch {

            projectFitnessContainer = ProjectFitnessContainer(context)
            var itemRepo = itemRepos

            itemRepoList = itemRepo.getWorkoutWithExercises(viewModelSave.selectedWorkoutName.value)

        }
    }

    //******************************************************************************************************************************************************************************

    val screen1 = 750
    val screen2 = 800
    val screen3 = 900
    val screen4 = 380
    val screen5 = 400
    val screen6 = 420

    var configuration = LocalConfiguration.current
    var screenheightDp = configuration.screenHeightDp
    var screenwidthDp = configuration.screenWidthDp

    val useDiffrentValue1 = screenheightDp in screen1..screen2
    val useDiffrentValue2 = screenheightDp in screen2..screen3
    val useDiffrentValue3 = screenheightDp >= screen3


    val useDiffrentValue4 = screenwidthDp in screen4..screen5
    val useDiffrentValue5 = screenwidthDp in screen5..screen6
    val useDiffrentValue6 = screenwidthDp >= screen6
    val useDiffrentValue7 = screenwidthDp <= screen4
    Log.d("SCREEN", "Screen Width is : $screenwidthDp")

    val marginWorkoutSettingsTextTopDp = if (useDiffrentValue1) {
        20.dp
    } else if (useDiffrentValue2) {
        40.dp
    } else if (useDiffrentValue3) {
        60.dp
    } else {
        30.dp
    }


    val marginWorkoutSettingsTextWidthDp = if (useDiffrentValue4) {
        300.dp
    } else if (useDiffrentValue5) {
        10.dp
    } else if (useDiffrentValue6) {
        10.dp
    } else {
        400.dp
    }


    val sheetState = rememberModalBottomSheetState()
    val sheetState2 = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(true) }
    var showBottomSheet2 by remember { mutableStateOf(true) }

    var config = LocalConfiguration.current
    var screenheightDpconfig = configuration.screenHeightDp


    // UI Coding ****************************************************************************************************************************************************************************

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFF1C40F),Color(0xFF181F26)),
                    start = Offset(0f,0f),
                    end = Offset(0f,screenheightDp.toFloat())
                )
            )
    )
    {
            Row(Modifier.align(Alignment.TopCenter)) {
                IconButton(
                    onClick = { navController.navigate("home") }, modifier = Modifier

                ) {
                    Icon(
                        painterResource(id = R.drawable.projectfitnessprevious),
                        contentDescription = null,
                        modifier = Modifier
                            .size(30.dp)
                            .align(Alignment.CenterVertically),
                        tint = Color(0xFF181F26)
                    )

                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = { showBottomSheet = true }, modifier = Modifier

                ) {
                    Icon(
                        painterResource(id = R.drawable.projectfitnesspointheavy),
                        contentDescription = null,
                        modifier = Modifier
                            .width(25.dp)
                            .height(25.dp),
                        tint = Color(0xFF181F26)
                    )

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
                            .height(77.dp)
                    )
                    {
                        Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                            Button(
                                onClick = { navController.navigate(Screens.LoginScreen.route) },
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(bottom = 25.dp)
                                    .fillMaxWidth()
                                    .height(60.dp),
                                shape = RoundedCornerShape(0.dp),
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                            ) {
                                Text(
                                    text = "Logout",
                                    style = TextStyle(
                                        fontSize = 25.sp,
                                        fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold))
                                    ),
                                    color = Color(0xFFF1C40F)
                                )
                            }

                        }
                    }
                }
            }

            Text(
                text = "Workout",
                style = TextStyle(
                    fontSize = 75.sp,
                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                    lineHeight = 55.sp
                ),
                color = Color(0xFF000000),
                modifier = Modifier.padding(start = 25.dp, top = 50.dp),
            )

        Text(
            text = "Overview",
            style = TextStyle(
                fontSize = 75.sp,
                fontFamily = FontFamily(Font(R.font.postnobillscolombolight)),
                lineHeight = 55.sp
            ),
            color = Color(0xFF000000),
            modifier = Modifier.padding(start = 25.dp, top = 105.dp),
        )

        Column(Modifier.background(Color.Transparent).padding(top = (screenheightDp/3).dp )) {
            Text(
                text = viewModelSave.selectedWorkoutName.value.uppercase(),
                style = TextStyle(
                    fontSize = 30.sp,
                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold))
                ),
                color = Color.White,
                modifier = Modifier
                    .padding(start = 30.dp)
            )


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 30.dp),
            state = LazyListState()
        )
        {
            itemsIndexed(itemRepoList) { index, item ->

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .background(
                            Color.Transparent, shape = RoundedCornerShape(15.dp)
                        )
                        .clickable {}
                )
                {
                    Box(
                        modifier = Modifier
                            .padding(start = 20.dp)
                            .width(90.dp)
                            .height(90.dp)
                            .background(
                                Color(0xFF21282F),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clip(shape = RoundedCornerShape(20.dp)),

                        )
                    {}
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .background(Color.Transparent)
                    ) {
                        Row(modifier = Modifier) {
                            Text(
                                text = "" + (item?.exercises2?.get(index)?.exercisesName),
                                modifier = Modifier
                                    .padding(start = 135.dp)
                                    .weight(1f),
                                fontSize = 20.sp,
                                fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                color = Color(0xFFD9D9D9),
                                fontWeight = FontWeight.Bold
                            )
                            Icon(painter = painterResource(id = R.drawable.question),
                                contentDescription = null,
                                tint = Color(0xFFF1C40F),
                                modifier = Modifier
                                    .padding(top = 20.dp, end = 20.dp)
                                    .size(25.dp)
                                    .clickable {
                                        viewModelSave.flagA.value = true
                                        viewModelSave.flagB.value = false
                                        viewModelSave.flagC.value = false
                                        viewModelSave.flagD.value = false
                                        item?.exercises2?.get(index)?.exercisesName?.let {
                                            viewModelSave.updateSelectedItemName(
                                                it
                                            )
                                        }
                                        //
                                        navController.navigate(route = "workoutsettingdetails")

                                    }
                            )
                            /*
                            Icon(painter = painterResource(id = R.drawable.settings),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(top = 20.dp, end = 20.dp)
                                    .size(25.dp)
                                    .clickable { showBottomSheet2 = true })
                            */

                        }
                        if (item != null) {
                            Text(
                                text = "${item.exercises2[index].exercisesSet} x ${item.exercises2[index].exercisesRep}",
                                fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                fontSize = 15.sp,
                                modifier = Modifier
                                    .padding(start = 135.dp),
                                color = Color(0xFFD9D9D9)
                            )
                        }

                    }






                    if (showBottomSheet2) {
                        ModalBottomSheet(
                            onDismissRequest = {
                                showBottomSheet2 = false
                            }, sheetState = sheetState2,
                            containerColor = Color(0xFF283747)
                        )
                        {
                            LaunchedEffect(Unit) {
                                scope.launch { sheetState2.expand() }
                                    .invokeOnCompletion {
                                        if (!sheetState2.isVisible) {
                                            showBottomSheet2 = false
                                        }
                                    }
                            }
                            Box(
                                modifier = Modifier
                                    .height(200.dp)
                                    .width(400.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                            {

                                Column(modifier = Modifier.padding(start = 120.dp)) {
                                    Text(
                                        text = "Set",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.CenterHorizontally)
                                            .padding(start = 10.dp),
                                        color = Color(0xFFF1C40F),
                                        style = TextStyle(fontSize = 20.sp)
                                    )
                                    if (item != null) {
                                        NumberPicker(
                                            value = item.exercises2[index].exercisesSet,
                                            onValueChange = {

                                                updateItemRepoSet(itemRepos, item, index, it)
                                                item.exercises2[index].exercisesSet = it

                                            },
                                            range = 0..20,
                                            dividersColor = Color(0xFFF1C40F),
                                            textStyle = TextStyle(color = Color.White)
                                        )
                                    }

                                }
                                Column(
                                    Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(end = 120.dp)
                                ) {
                                    Text(
                                        text = "Rep",
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally),
                                        color = Color(0xFFF1C40F),
                                        style = TextStyle(fontSize = 20.sp)
                                    )
                                    if (item != null) {
                                        NumberPicker(
                                            value = item.exercises2[index].exercisesRep,
                                            onValueChange = {

                                                updateItemRepoRep(itemRepos, item, index, it)
                                                item.exercises2[index].exercisesRep = it

                                            },
                                            range = 0..20,
                                            dividersColor = Color(0xFFF1C40F),
                                            textStyle = TextStyle(color = Color.White)
                                        )
                                    }

                                }

                                Button(
                                    onClick = { showBottomSheet2 = false },
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .width(70.dp)
                                        .height(40.dp)
                                        .padding(end = 10.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFF1C40F)
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = "Change",
                                        style = TextStyle(
                                            fontFamily = FontFamily(
                                                Font(R.font.postnobillscolombosemibold)
                                            )
                                        ),
                                        color = Color(0xFF21282F)
                                    )

                                }

                            }
                        }

                    }

                }
                Spacer(modifier = Modifier.size(20.dp))
            }
        }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
        ) {
            Button(
                onClick = { navController.navigate("workoutlog") },
                modifier = Modifier
                    .padding(top = screenheightDp.dp / 3.9f, start = screenwidthDp.dp / 20)
                    .width(220.dp)
                    .height(35.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1C40F)),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "START WORKOUT",
                    fontFamily = FontFamily(Font(R.font.postnobillscolombo)),
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(letterSpacing = 1.sp, fontSize = 20.sp),
                    color = Color(0xFF21282F)
                )
            }
        }
    }
}

fun updateItemRepoSet(
    itemRepos: ItemsRepository,
    item: ProjectFitnessWorkoutWithExercises,
    index: Int,
    i: Int,
) {
    CoroutineScope(Dispatchers.IO).launch {
        itemRepos.updatesItem(
            ProjectFitnessExerciseEntity
                (
                ids = item.exercises2[index].ids,
                exerciseId = item.exercises2[index].exerciseId,
                exercisesName = item.exercises2[index].exercisesName,
                exercisesRep = item.exercises2[index].exercisesRep,
                exercisesSet = i,
                setrepList = ViewModelSave().setrepList
            )
        )
    }

}

fun updateItemRepoRep(
    itemRepos: ItemsRepository,
    item: ProjectFitnessWorkoutWithExercises,
    index: Int,
    i: Int,
) {
    CoroutineScope(Dispatchers.IO).launch {
        itemRepos.updatesItem(
            ProjectFitnessExerciseEntity
                (
                ids = item.exercises2[index].ids,
                exerciseId = item.exercises2[index].exerciseId,
                exercisesName = item.exercises2[index].exercisesName,
                exercisesRep = i,
                exercisesSet = item.exercises2[index].exercisesSet,
                setrepList = ViewModelSave().setrepList
            ),
        )
    }

}

@Preview
@Composable
fun WorkoutSettingScreenPreview() {
    WorkoutSettingScreen(navController = rememberNavController(), viewModel())
}