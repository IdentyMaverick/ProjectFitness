package com.example.projectfitness

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import viewmodel.ProjectFitnessViewModel
import viewmodel.ViewModelSave

@Composable
fun WorkoutSettingScreenWorkoutDetails(navController: NavController, projectFitnessViewModel: ProjectFitnessViewModel,viewModelSave: ViewModelSave) {
    val context = LocalContext.current
    //val viewModelSave: ViewModelSave = viewModel()
    val container = ProjectFitnessContainer(context)

    var config = LocalConfiguration.current
    var screenwidthDp = config.screenWidthDp.dp
    var screenheightDp = config.screenHeightDp.dp

    // Access the selectedItemName value from the ViewModelSave
    val selectedItemName = viewModelSave.selectedItemName.value
    val projectFitnessViewItems = projectFitnessViewModel.firestoreItems.value
    var selectedItemMainMuscles : String? = ""
    var selectedItemSecondaryMuscles : String? = ""
    //Log.d("TAG4:","Selected Item.name.equals : ${item.name} in Workout Details")



    Log.d("TAG3:","Selected Item Name is : $selectedItemName in Workout Details")

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF181F26))) {
        Text(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 5.dp),
            text = "PROJECT FITNESS",
            fontFamily = FontFamily(Font(R.font.postnobillscolombobold)),
            color = Color(0xFFF1C40F),
            style = TextStyle(fontSize = 20.sp,letterSpacing = 10.sp)
        )
        Text(
            text = ""+ selectedItemName.uppercase(),
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
            text = ""+ projectFitnessViewItems?.let { returnListMainMuscleItems2(it,selectedItemName) },
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
            text = ""+ projectFitnessViewItems?.let { returnListSecondaryMuscleItems2(it,selectedItemName) },
            fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
            style = TextStyle(fontSize = 15.sp, letterSpacing = 5.sp),
            color = Color(0xFFFF0F00),
            modifier = Modifier
                .padding(top = screenheightDp / 3.5f)
                .align(Alignment.TopCenter)
        )
        Row {

        }
        Image(painterResource(
            id = returnWorkoutDetail2(selectedItemName)),
            contentDescription = null,
            modifier = Modifier
                .padding(top = screenheightDp / 2.5f, end = screenwidthDp / 3 )
                .align(Alignment.TopCenter)
        )
    }
}
@Composable
fun returnListMainMuscleItems2(projectFitnessViewList: List<Exercises>, selectedItemName : String?) : String?{
    for (i in projectFitnessViewList)
    {
        if (i.name.equals(selectedItemName))
        {
            return i.bodypart.toString().uppercase()
        }
    }
    return null
}
@Composable
fun returnListSecondaryMuscleItems2(projectFitnessViewList: List<Exercises>, selectedItemName : String?) : String?{
    for (i in projectFitnessViewList)
    {
        if (i.name.equals(selectedItemName))
        {
            return i.secondarymuscles.toString().uppercase()
        }
    }
    return null
}
@Composable
fun returnWorkoutDetail2(selectedItemName : String?) : Int {
    if (selectedItemName == "Barbell Bench Press") {
        return R.drawable.dumbbellbenchpressdetail
    } else if (selectedItemName == "Cable Crossovers"){
        return R.drawable.pecdecdetail
    }else if (selectedItemName == "Chest Dip"){
        return R.drawable.chestdipdetail
    }else if (selectedItemName == "Chest Press"){
        return R.drawable.dumbbellbenchpressdetail
    }else if (selectedItemName == "Close Grip Dumbbell Press"){
        return R.drawable.dumbbellbenchpressdetail
    }else if (selectedItemName == "Decline Bench Press"){
        return R.drawable.dumbbellbenchpressdetail
    }else if (selectedItemName == "Dumbbell Bench Press"){
        return R.drawable.dumbbellbenchpressdetail
    }else if (selectedItemName == "Dumbbell Flys"){
        return R.drawable.dumbbellbenchpressdetail
    }else if (selectedItemName == "Dumbbell Pullover"){
        return R.drawable.dumbbellpulloverdetail
    }else if (selectedItemName == "Hammer Strength Bench Press"){
        return R.drawable.dumbbellbenchpressdetail
    }else if (selectedItemName == "Incline Dumbbell Bench Press"){
        return R.drawable.dumbbellbenchpressdetail
    }else if (selectedItemName == "Incline Dumbbell Flys"){
        return R.drawable.dumbbellbenchpressdetail
    }else if (selectedItemName == "Leg Press"){
        return R.drawable.pecdecdetail
    }else if (selectedItemName == "Pec Dec"){
        return R.drawable.pecdecdetail
    }else if (selectedItemName == "Push Up"){
        return R.drawable.chestdipdetail
    }else if (selectedItemName == "Smith Machine Bench Press"){
        return R.drawable.dumbbellbenchpressdetail
    }else if (selectedItemName == "Smith Machine Incline Bench Press"){
        return R.drawable.dumbbellbenchpressdetail
    }else if (selectedItemName == "Standing Cable Fly"){
        return R.drawable.dumbbellbenchpressdetail
    }else if (selectedItemName == "Squat"){
        return R.drawable.chestdipdetail
    }else if (selectedItemName == "Leg Extension"){
        return R.drawable.chestdipdetail
    }else if (selectedItemName == "Machine Hack Squat"){
        return R.drawable.chestdipdetail
    }else if (selectedItemName == "Dumbbell Squat"){
        return R.drawable.chestdipdetail
    }else if (selectedItemName == "Dumbbell Lunge"){
        return R.drawable.chestdipdetail
    }else if (selectedItemName == "Front Squat"){
        return R.drawable.chestdipdetail
    }else if (selectedItemName == "Dumbbell Bulgarian Split Squat"){
        return R.drawable.chestdipdetail
    }else if (selectedItemName == "Dumbbell Split Squat"){
        return R.drawable.chestdipdetail
    }else if (selectedItemName == "Plie Squat"){
        return R.drawable.chestdipdetail
    }else if (selectedItemName == "Single Leg Extension"){
        return R.drawable.chestdipdetail
    }else if (selectedItemName == "Box Jump"){
        return R.drawable.chestdipdetail
    }else if (selectedItemName == "Stiff Leg Deadlift"){
        return R.drawable.chestdipdetail
    }else if (selectedItemName == "Dumbbell Hamstring Curl"){
        return R.drawable.chestdipdetail
    }else if (selectedItemName == "Trap Bar Deadlift"){
        return R.drawable.chestdipdetail
    }else if (selectedItemName == "Seated Leg Curl"){
        return R.drawable.chestdipdetail
    }else if (selectedItemName == "Kettlebell Swing"){
        return R.drawable.chestdipdetail
    }else if (selectedItemName == "Sumo Deadlift"){
        return R.drawable.chestdipdetail
    }else if (selectedItemName == "Nordic Hamstring Curl"){
        return R.drawable.chestdipdetail
    }else if (selectedItemName == "Seated Calf Raise"){
        return R.drawable.chestdipdetail
    }else if (selectedItemName == "Leg Press Calf Raise"){
        return R.drawable.chestdipdetail
    }else if (selectedItemName == "Standing Machine Calf Raise"){
        return R.drawable.chestdipdetail
    }else if (selectedItemName == "Hyperextension"){
        return R.drawable.chestdipdetail
    }else if (selectedItemName == "Barbell Hip Thrust"){
        return R.drawable.chestdipdetail
    }else if (selectedItemName == "Standing Good Morning"){
        return R.drawable.chestdipdetail
    }else if (selectedItemName == "IT Band Foam Rolling"){
        return R.drawable.chestdipdetail
    }else if (selectedItemName == "Plantar Fascia Lacrosse Ball"){
        return R.drawable.chestdipdetail
    }else if (selectedItemName == "Kneeling Posterior Hip Capsule Mobilization"){
        return R.drawable.chestdipdetail
    }else if (selectedItemName == "Hip Abduction Machine"){
        return R.drawable.chestdipdetail
    }
    return R.drawable.down
}
@Preview(name = "phone", device = "spec:shape=Normal,width=360,height=720,unit=dp,dpi=402")
@Composable
fun WorkoutSettingScreenWorkoutDetailsPreview()
{
    WorkoutDetails(navController = rememberNavController(), projectFitnessViewModel = viewModel(), viewModelSave = viewModel() )
}