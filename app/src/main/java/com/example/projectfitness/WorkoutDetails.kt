package com.example.projectfitness

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import database.Exercises
import database.ProjectFitnessContainer
import database.ProjectFitnessExerciseEntity
import kotlinx.coroutines.launch
import viewmodel.ProjectFitnessViewModel
import viewmodel.ViewModelSave

@Composable
fun WorkoutDetails(
    navController: NavController,
    projectFitnessViewModel: ProjectFitnessViewModel,
    viewModelSave: ViewModelSave,
) {

    //Database Creation*************************************************************************************************************************************************************

    val context = LocalContext.current
    val scopes = rememberCoroutineScope()
    var projectFitnessContainer = ProjectFitnessContainer(context)
    val itemRepo = projectFitnessContainer.itemsRepository

    //******************************************************************************************************************************************************************************

    // Variable Initialize *********************************************************************************************************************************************************

    var config = LocalConfiguration.current
    var screenwidthDp = config.screenWidthDp.dp
    var screenheightDp = config.screenHeightDp.dp


    val selectedItemName = viewModelSave.selectedItemName.value
    val projectFitnessViewItems = projectFitnessViewModel.firestoreItems.value

    //**************************************************************************************************************************************************************************************

    // UI Coding ****************************************************************************************************************************************************************************

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF181F26))
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 5.dp),
            text = "PROJECT FITNESS",
            fontFamily = FontFamily(Font(R.font.postnobillscolombobold)),
            color = Color(0xFFF1C40F),
            style = TextStyle(fontSize = 20.sp, letterSpacing = 10.sp)
        )
        Icon(
            painter = painterResource(id = R.drawable.left),
            contentDescription = null,
            modifier = Modifier
                .clickable(onClick = { navController.navigate("chooseexercises/{name}") })
                .size(30.dp)
                .padding(top = 5.dp),
            tint = Color(0xFFD9D9D9)
        )
        Text(
            text = "" + selectedItemName.uppercase(),
            fontFamily = FontFamily(Font(R.font.postnobillscolombobold)),
            style = TextStyle(fontSize = 20.sp, letterSpacing = 5.sp),
            color = Color(0xFFD9D9D9),
            modifier = Modifier
                .padding(top = screenheightDp / 10)
                .align(Alignment.TopCenter)
        )
        Text(
            text = "MAIN MUSCLE GROUP",
            fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
            style = TextStyle(fontSize = 15.sp, letterSpacing = 7.sp),
            color = Color(0xFF506172),
            modifier = Modifier
                .padding(top = screenheightDp / 6)
                .align(Alignment.TopCenter)
        )
        Text(
            text = "" + returnListMainMuscleItems(projectFitnessViewItems, selectedItemName),
            fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
            style = TextStyle(fontSize = 15.sp, letterSpacing = 5.sp),
            color = Color(0xFFF1C40F),
            modifier = Modifier
                .padding(top = screenheightDp / 5)
                .align(Alignment.TopCenter)
        )
        Text(
            text = "SECONDARY MUSCLE GROUP",
            fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
            style = TextStyle(fontSize = 15.sp, letterSpacing = 7.sp),
            color = Color(0xFF506172),
            modifier = Modifier
                .padding(top = screenheightDp / 4)
                .align(Alignment.TopCenter)
        )
        Text(
            text = "" + returnListSecondaryMuscleItems(projectFitnessViewItems, selectedItemName),
            fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
            style = TextStyle(fontSize = 15.sp, letterSpacing = 5.sp),
            color = Color(0xFFFF0F00),
            modifier = Modifier
                .padding(top = screenheightDp / 3.5f)
                .align(Alignment.TopCenter)
        )
        Row {

        }
        Image(
            painterResource(
                id = returnWorkoutDetail(selectedItemName)
            ),
            contentDescription = null,
            modifier = Modifier
                .padding(top = screenheightDp / 2.5f)
                .align(Alignment.TopCenter)
        )
        Button(
            onClick = {
                navController.navigate("chooseexercises/$selectedItemName")
                scopes.launch {
                    itemRepo.insertItems(
                        ProjectFitnessExerciseEntity(
                            exerciseId = (viewModelSave.idFlag.value) + 1,
                            exercisesName = selectedItemName,
                            exercisesRep = 12,
                            exercisesSet = 3,
                            setrepList = viewModelSave.setrepList
                        )
                    )
                    viewModelSave.idFlag2.value++
                    viewModelSave.selectedItemUpdatedName.value = selectedItemName
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = screenheightDp / 18)
                .background(
                    Color(0xFFD9D9D9),
                    RoundedCornerShape(25.dp)
                )
                .width(130.dp)
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFFD9D9D9)),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = "ADD EXERCISES",
                fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                color = Color(0xFF506172)
            )
        }
    }
}

@Composable
fun returnListMainMuscleItems(
    projectFitnessViewList: List<Exercises>,
    selectedItemName: String?,
): String? {
    for (i in projectFitnessViewList) {
        if (i.name.equals(selectedItemName)) {
            return i.bodypart.toString().uppercase()
        }
    }
    return null
}

@Composable
fun returnListSecondaryMuscleItems(
    projectFitnessViewList: List<Exercises>,
    selectedItemName: String?,
): String? {
    for (i in projectFitnessViewList) {
        if (i.name.equals(selectedItemName)) {
            return i.secondarymuscles.toString().uppercase()
        }
    }
    return null
}

@Composable
fun returnWorkoutDetail(selectedItemName: String?): Int {
    if (selectedItemName == "Barbell Bench Press") {
        return R.drawable.legpress
    } else if (selectedItemName == "Cable Crossovers") {
        return R.drawable.legpress
    } else if (selectedItemName == "Chest Dip") {
        return R.drawable.legpress
    } else if (selectedItemName == "Chest Press") {
        return R.drawable.legpress
    } else if (selectedItemName == "Close Grip Dumbbell Press") {
        return R.drawable.legpress
    } else if (selectedItemName == "Decline Bench Press") {
        return R.drawable.legpress
    } else if (selectedItemName == "Dumbbell Bench Press") {
        return R.drawable.legpress
    } else if (selectedItemName == "Dumbbell Flys") {
        return R.drawable.legpress
    } else if (selectedItemName == "Dumbbell Pullover") {
        return R.drawable.legpress
    } else if (selectedItemName == "Hammer Strength Bench Press") {
        return R.drawable.legpress
    } else if (selectedItemName == "Incline Dumbbell Bench Press") {
        return R.drawable.legpress
    } else if (selectedItemName == "Incline Dumbbell Flys") {
        return R.drawable.legpress
    } else if (selectedItemName == "Leg Press") {
        return R.drawable.legpress
    } else if (selectedItemName == "Pec Dec") {
        return R.drawable.legpress
    } else if (selectedItemName == "Push Up") {
        return R.drawable.legpress
    } else if (selectedItemName == "Smith Machine Bench Press") {
        return R.drawable.legpress
    } else if (selectedItemName == "Smith Machine Incline Bench Press") {
        return R.drawable.legpress
    } else if (selectedItemName == "Standing Cable Fly") {
        return R.drawable.legpress
    } else if (selectedItemName == "Squat") {
        return R.drawable.legpress
    } else if (selectedItemName == "Leg Extension") {
        return R.drawable.legpress
    } else if (selectedItemName == "Machine Hack Squat") {
        return R.drawable.legpress
    } else if (selectedItemName == "Dumbbell Squat") {
        return R.drawable.legpress
    } else if (selectedItemName == "Dumbbell Lunge") {
        return R.drawable.legpress
    } else if (selectedItemName == "Front Squat") {
        return R.drawable.legpress
    } else if (selectedItemName == "Dumbbell Bulgarian Split Squat") {
        return R.drawable.legpress
    } else if (selectedItemName == "Dumbbell Split Squat") {
        return R.drawable.legpress
    } else if (selectedItemName == "Plie Squat") {
        return R.drawable.legpress
    } else if (selectedItemName == "Single Leg Extension") {
        return R.drawable.legpress
    } else if (selectedItemName == "Box Jump") {
        return R.drawable.legpress
    } else if (selectedItemName == "Stiff Leg Deadlift") {
        return R.drawable.legpress
    } else if (selectedItemName == "Dumbbell Hamstring Curl") {
        return R.drawable.legpress
    } else if (selectedItemName == "Trap Bar Deadlift") {
        return R.drawable.legpress
    } else if (selectedItemName == "Seated Leg Curl") {
        return R.drawable.legpress
    } else if (selectedItemName == "Kettlebell Swing") {
        return R.drawable.legpress
    } else if (selectedItemName == "Sumo Deadlift") {
        return R.drawable.legpress
    } else if (selectedItemName == "Nordic Hamstring Curl") {
        return R.drawable.legpress
    } else if (selectedItemName == "Seated Calf Raise") {
        return R.drawable.legpress
    } else if (selectedItemName == "Leg Press Calf Raise") {
        return R.drawable.legpress
    } else if (selectedItemName == "Standing Machine Calf Raise") {
        return R.drawable.legpress
    } else if (selectedItemName == "Hyperextension") {
        return R.drawable.legpress
    } else if (selectedItemName == "Barbell Hip Thrust") {
        return R.drawable.legpress
    } else if (selectedItemName == "Standing Good Morning") {
        return R.drawable.legpress
    } else if (selectedItemName == "IT Band Foam Rolling") {
        return R.drawable.legpress
    } else if (selectedItemName == "Plantar Fascia Lacrosse Ball") {
        return R.drawable.legpress
    } else if (selectedItemName == "Kneeling Posterior Hip Capsule Mobilization") {
        return R.drawable.legpress
    } else if (selectedItemName == "Hip Abduction Machine") {
        return R.drawable.legpress
    }
    return R.drawable.down
}

@Preview(name = "phone", device = "spec:shape=Normal,width=360,height=720,unit=dp,dpi=402")
@Composable
fun WorkoutDetailsPreview() {
    WorkoutDetails(
        navController = rememberNavController(),
        projectFitnessViewModel = viewModel<ProjectFitnessViewModel>(),
        viewModelSave = viewModel()
    )
}