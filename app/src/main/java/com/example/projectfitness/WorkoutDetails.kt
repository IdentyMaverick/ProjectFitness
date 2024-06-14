package com.example.projectfitness

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
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
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import database.Exercises
import database.ProjectFitnessContainer
import database.ProjectFitnessExerciseEntity
import database.SetRep
import kotlinx.coroutines.launch
import viewmodel.ProjectFitnessViewModel
import viewmodel.ViewModelSave

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetails(
    navController: NavController,
    projectFitnessViewModel: ProjectFitnessViewModel,
    viewModelSave: ViewModelSave,
) {

    //Database Creation*************************************************************************************************************************************************************
    val db = FirebaseFirestore.getInstance()
    val uid = Firebase.auth.currentUser?.uid
    val context = LocalContext.current
    val scopes = rememberCoroutineScope()
    var projectFitnessContainer = ProjectFitnessContainer(context)
    val itemRepo = projectFitnessContainer.itemsRepository
    val sharedPreferences = context.getSharedPreferences("workoutIdNumber", Context.MODE_PRIVATE)
    var workoutIdNumber by remember { mutableStateOf(sharedPreferences.getInt("number",2)) }
    var exerciseIdNumber by remember { mutableStateOf(sharedPreferences.getInt("exerciseidnumber",2)) }

    //******************************************************************************************************************************************************************************

    // Variable Initialize *********************************************************************************************************************************************************

    var config = LocalConfiguration.current
    var screenwidthDp = config.screenWidthDp.dp
    var screenheightDp = config.screenHeightDp.dp


    val selectedItemName = viewModelSave.selectedItemName.value
    val projectFitnessViewItems = projectFitnessViewModel.firestoreItems.value

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(true) }

    var exerciseSet by remember { mutableStateOf(0) }
    var exerciseRep by remember { mutableStateOf(0) }

    //**************************************************************************************************************************************************************************************

    // UI Coding ****************************************************************************************************************************************************************************
    Log.d("WorkoutDetails / WorkoutIdNumber / Not Clicked ",workoutIdNumber.toString())
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF181F26)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.projectfitnessprevious),
                contentDescription = null,
                modifier = Modifier
                    .clickable(onClick = { navController.navigate("createworkout") })
                    .padding(top = 10.dp)
                    .size(30.dp)
                ,
                tint = Color(0xFFF1C40F)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 50.dp)
                    ,
                text = "PROJECT FITNESS",
                fontFamily = FontFamily(Font(R.font.postnobillscolombobold)),
                color = Color(0xFFF1C40F),
                style = TextStyle(fontSize = 20.sp, letterSpacing = 10.sp)
            )

        }

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
            text = "" + projectFitnessViewItems?.let { returnListMainMuscleItems(it, selectedItemName) },
            fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
            style = TextStyle(fontSize = 15.sp, letterSpacing = 5.sp),
            color = Color(0xFFFF0F00),
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
            text = "" + projectFitnessViewItems?.let { returnListSecondaryMuscleItems(it, selectedItemName) },
            fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
            style = TextStyle(fontSize = 15.sp, letterSpacing = 5.sp),
            color = Color(0xFFF1C40F),
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
                .padding(end = 0.dp, top = screenheightDp / 3f)
                .align(Alignment.TopCenter)
        )
        Button(
            onClick = {
                showBottomSheet = true
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = screenheightDp / 25)
                .width(150.dp)
                .height(30.dp),
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFFF1C40F)),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = "ADD EXERCISES",
                fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                color = Color(0xFF000000)
            )
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(onDismissRequest = { showBottomSheet = false }, sheetState = sheetState, containerColor = Color(0xFF283747)) {
            LaunchedEffect(Unit) {
                scope.launch { sheetState.expand() }.invokeOnCompletion { if (!sheetState.isVisible) {showBottomSheet = false} }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            )
            {
                Row(modifier = Modifier.align(Alignment.TopCenter)) {
                    Text(
                        text = "SET",
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
                        value = exerciseSet,
                        onValueChange = {
                            exerciseSet = it
                            scopes.launch {
                                exerciseSet = it
                            }
                        },
                        range = 0..500,
                        dividersColor = Color(0xFFF1C40F),
                        textStyle = TextStyle(color = Color.White),

                        )
                    Spacer(modifier = Modifier.size(130.dp))
                    NumberPicker(
                        value = exerciseRep,
                        onValueChange = {
                            exerciseRep = it
                            scopes.launch {
                                exerciseRep = it
                            }},
                        range = 0..50,
                        dividersColor = Color(0xFFF1C40F),
                        textStyle = TextStyle(color = Color.White),

                        )
                }
                Row(modifier = Modifier.align(Alignment.BottomEnd)) {
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
                                Log.d("WorkoutDetails / WorkoutIdNumber / Clicked ",workoutIdNumber.toString())
                                setRepDesign(exerciseSet, exerciseRep, viewModelSave)
                                scopes.launch {
                                    itemRepo.insertItems(
                                        ProjectFitnessExerciseEntity(
                                            exerciseId = workoutIdNumber,
                                            exercisesName = selectedItemName,
                                            exercisesRep = exerciseRep,
                                            exercisesSet = exerciseSet,
                                            setrepList = viewModelSave.setrepList
                                        )
                                    )
                                    viewModelSave.idFlag2.value++
                                    viewModelSave.selectedItemUpdatedName.value = selectedItemName
                                }
                                if (uid != null) {
                                    val editor = sharedPreferences.edit()
                                    val docRef = db.collection("Workouts").document("$uid").collection("HasWorkout").document("${workoutIdNumber}").collection("HasExercise")
                                        .document("$exerciseIdNumber")
                                    val hashMapOf = hashMapOf(
                                        "exerciseid" to workoutIdNumber ,
                                        "exercisesname" to selectedItemName ,
                                        "exercisesRep" to exerciseRep ,
                                        "exercisesSet" to exerciseSet ,
                                        "setrepList" to viewModelSave.setrepList
                                    )
                                    docRef.set(hashMapOf)
                                    editor.putInt( "exerciseidnumber" , exerciseIdNumber + 1 )
                                    editor.apply()
                                }

                                viewModelSave.setRepString.add("${exerciseSet} x ${exerciseRep}")
                                navController.navigate("chooseexercises/$selectedItemName")
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
    return when (selectedItemName) {
        "Barbell Bench Press",
        "Cable Crossovers",
        "Chest Dip",
        "Chest Press",
        "Close Grip Dumbbell Press",
        "Decline Bench Press",
        "Dumbbell Bench Press",
        "Dumbbell Flys",
        "Hammer Strength Bench Press",
        "Incline Dumbbell Bench Press",
        "Incline Dumbbell Flys",
        "Pec Dec",
        "Smith Machine Bench Press",
        "Smith Machine Incline Bench Press",
        "Standing Cable Fly" -> R.drawable.colorchestshoulderstriceps

        "Push Up" -> R.drawable.colorchestshoulderstricepsabs

        "Dumbbell Pullover" -> R.drawable.colorlatsshoulderstriceps

        "Leg Press",
        "Front Squat",
        "Dumbbell Bulgarian Split Squat",
        "Box Jump" -> R.drawable.colorquadsabsadductorscalvesgluteshamstringslowerback

        "Squat",
        "Machine Hack Squat",
        "Dumbbell Squat",
        "Dumbbell Split Squat",
        "Plie Squat" -> R.drawable.colorquadscalvesgluteshamstringslowerback

        "Leg Extension",
        "Single Leg Extension" -> R.drawable.colorquads

        "Dumbbell Lunge" -> R.drawable.colorquadscalvesgluteshamstrings

        "Stiff Leg Deadlift",
        "Trap Bar Deadlift",
        "Kettlebell Swing",
        "Sumo Deadlift" -> R.drawable.colorhamstringsabsadductorscalvesgluteslatslowerbackquadstrapsupperback

        "Dumbbell Hamstring Curl" -> R.drawable.colorhamstrings

        "Seated Leg Curl" -> R.drawable.colorhamstringsglutes

        "Nordic Hamstring Curl" -> R.drawable.colorhamstringsabscalvesglutes

        "Seated Calf Raise",
        "Leg Press Calf Raise",
        "Standing Machine Calf Raise" -> R.drawable.colorcalves

        "Hyperextension" -> R.drawable.colorcalveshamstringslowerback

        "Barbell Hip Thrust" -> R.drawable.colorglutesabshamstrings

        "Standing Good Morning" -> R.drawable.colorglutesabshamstringslowerbackupperback

        "IT Band Foam Rolling" -> R.drawable.colorabductor

        "Plantar Fascia Lacrosse Ball" -> R.drawable.colorplantarfascia

        "Kneeling Posterior Hip Capsule Mobilization" -> R.drawable.colorhipflexorsglutes

        "Hip Abduction Machine" -> R.drawable.colorabductor

        "Standing Dumbbell Curl",
        "Standing Barbell Curl" -> R.drawable.colorbiceps

        "Standing Hammer Curl",
        "Incline Dumbbell Curl",
        "Reverse Grip Barbell Curl",
        "Ez Bar Reverse Grip Barbell Curl" -> R.drawable.colorbicepsforearms

        "Seated Barbell Wrist Curl",
        "Behind-The-Back Barbell Wrist Curl",
        "Reverse Grip Barbell Wrist Curl",
        "One-Arm Seated Dumbbell Wrist Curl",
        "Reverse Dumbbell Wrist Curl Over Bench" -> R.drawable.colorforearm

        "Lat Pulldown",
        "Close Grip Lat Pulldown",
        "Wide Grip Pull Up",
        "Straight Arm Lat Pulldown",
        "Chin Up",
        "Reverse Grip Lat Pulldown",
        "Wide Grip Lat Pulldown",
        "Pull Up",
        "Rope Straight Arm Pulldown",
        "V-Bar Pulldown",
        "Shotgun Row",
        "V-Bar Pull Up",
        "Weighted Pull Up",
        "Weighted Chin Up",
        "Close Grip Pull Up",
        "Eccentric Only Pull Up",
        "Underhand Close Grip Lateral Pulldown",
        "Band Assisted Chin Up",
        "Resistance Band Assisted Pull Up",
        "Rack Lat Stretch",
        "Behind Neck Lat Pulldown",
        "Pull Up with Leg Raise",
        "Close Grip Chin Up",
        "Chin Up with Leg Raise",
        "Rope Extension Lat Pulldown" -> R.drawable.colorforearm

        "Standing Overhead Medicine Ball Throw" -> R.drawable.colorforearm

        "Hip Abduction Machine" -> R.drawable.colorforearm

        "Cable Hip Abduction" -> R.drawable.colorforearm

        "Lying Floor Leg Raise" -> R.drawable.colorforearm

        "Cable Crunch" -> R.drawable.colorforearm

        "Weighted Crunch" -> R.drawable.colorforearm

        "Dumbbell Side Bends" -> R.drawable.colorforearm

        "Plank" -> R.drawable.colorforearm

        "Hanging Leg Raise" -> R.drawable.colorforearm

        "Ab Crunch" -> R.drawable.colorforearm

        "Side Plank" -> R.drawable.colorforearm

        "Decline Sit Up" -> R.drawable.colorforearm

        "Abdominal Air Bike" -> R.drawable.colorforearm

        "Abdominal Barbell Rollouts" -> R.drawable.colorforearm

        "Sit Up" -> R.drawable.colorforearm

        "Twisting Hanging Knee Raise" -> R.drawable.colorforearm

        "Exercise Ball Crunch" -> R.drawable.colorforearm

        "Hip Adduction Machine" -> R.drawable.colorforearm

        "Alternating Lateral Lunge" -> R.drawable.colorforearm

        "Deep Squat Prying" -> R.drawable.colorforearm

        "Adductor Foam Rolling" -> R.drawable.colorforearm

        "Lateral Kneeling Adductor" -> R.drawable.colorforearm

        "Rocking Frog Stretch" -> R.drawable.colorforearm

        "Seated Calf Raise" -> R.drawable.colorforearm

        "Seated Dumbbell Calf Raise" -> R.drawable.colorforearm

        "Bodyweight Standing Calf Raise" -> R.drawable.colorforearm

        "Standing One-Leg Calf Raise" -> R.drawable.colorforearm

        "One-Leg Standing Bodyweight Calf Raise" -> R.drawable.colorforearm

        "45 Degree Leg Press Calf Raise" -> R.drawable.colorforearm

        "Standing Machine Calf Raise" -> R.drawable.colorforearm

        "Standing Barbell Calf Raise" -> R.drawable.colorforearm

        "Smith Machine Calf Raise" -> R.drawable.colorforearm

        "Hack Squat Calf Raise" -> R.drawable.colorforearm

        "One-Leg 45 Degree Calf Raise" -> R.drawable.colorforearm

        "Jump Rope" -> R.drawable.colorforearm

        "Dumbbell Deadlift" -> R.drawable.colorforearm

        "Superman" -> R.drawable.colorforearm

        "Smith Machine Deadlift" -> R.drawable.colorforearm

        "90/90 Hip Crossover" -> R.drawable.colorforearm

        "Rollover Into V-Sits" -> R.drawable.colorforearm

        "One Arm Dumbbell Row" -> R.drawable.colorforearm

        "Bent Over Row" -> R.drawable.colorforearm

        "Bent Over Dumbbell Row" -> R.drawable.colorforearm

        "Seated Cable Row" -> R.drawable.colorforearm

        "Incline Bench Two Arm Dumbbell Row" -> R.drawable.colorforearm

        "Neutral Grip Chest Supported Dumbbell Row" -> R.drawable.colorforearm

        "Reverse Grip Bent-Over Dumbbell Row" -> R.drawable.colorforearm

        "Feet Elevated Inverted Row" -> R.drawable.colorforearm

        "T-Bar Row" -> R.drawable.colorforearm

        "Machine T-Bar Row" -> R.drawable.colorforearm

        "Machine Row" -> R.drawable.colorforearm

        "Tripod Dumbbell Row" -> R.drawable.colorforearm

        "Smith Machine Bent-Over Row" -> R.drawable.colorforearm

        "Rope Crossover Seated Row" -> R.drawable.colorforearm

        "One-Arm Seated Cable Row" -> R.drawable.colorforearm

        "Rope Extension Incline Bench Cable Row" -> R.drawable.colorforearm

        "Reverse Grip Incline Bench Cable Row" -> R.drawable.colorforearm

        "Incline Bench Cable Row" -> R.drawable.colorforearm

        "Inverted Row" -> R.drawable.colorforearm

        "Neck Tiger Tail" -> R.drawable.colorforearm

        "Side Plank with Hip Dip" -> R.drawable.colorforearm

        "Side Crunch" -> R.drawable.colorforearm

        "Half Kneeling Cable Lift" -> R.drawable.colorforearm

        "Split Stance Cable Chop" -> R.drawable.colorforearm

        "Tall Kneeling Cable Chop" -> R.drawable.colorforearm

        "Off Bench Oblique Hold" -> R.drawable.colorforearm

        "Landmine Rotation" -> R.drawable.colorforearm

        "Palmar Fascia Lacrosse Ball" -> R.drawable.colorforearm

        "Deep Tissue Foam Roller" -> R.drawable.colorforearm

        "Dumbbell Pullover" -> R.drawable.colorforearm

        "Crunch" ,
        "Dumbbell Lateral Raise" ,
        "Military Press" ,
        "Seated Dumbbell Press",
        "Standing Dumbbell Shoulder Press" ,
        "Bent Over Dumbbell Reverse Fly" ,
        "Smith Machine Shoulder Press" ,
        "Seated Arnold Press" ,
        "Seated Bent Over Dumbbell Reverse Fly" ,
        "Seated Barbell Shoulder Press" ,
        "Seated Dumbbell Lateral Raise" ,
        "Standing Cable Reverse Fly" ,
        "Cable Face Pull" ,
        "Machine Shoulder Press" ,
        "Single Arm Cable Lateral Raise" ,
        "Machine Reverse Fly" ,
        "Cable Lateral Raise" ,
        "Standing Dumbbell Front Raise" ,
        "Seated Behind The Neck Shoulder Press" ,
        "Barbell Front Raise" ,
        "Cable Upright Row" ,
        "Machine Latera Raise" ,
        "Dumbbell Upright Row" ,
        "One-Arm Dumbbell Lateral Raise" ,
        "Smith Machine Upright Row" ,
        "Weight Plate Front Raise" ,
        "Standing Arnold Press" ,
        "Band Pull Apart" ,
        "Bent Over Low Pulley Rear Delt Fly" ,
        "Incline Rear Delt Fly" ,
        "Cable Front Raise" ,
        "Push Press" ,
        "Seated Neutral Grip Dumbbell Shoulder Press" ->R.drawable.colorforearm

        else -> R.drawable.colorforearm
    }
}


fun setRepDesign(exerciseSet : Int , exerciseRep : Int,viewModelSave: ViewModelSave){
    repeat(exerciseSet){index ->
        viewModelSave.setrepList.add(SetRep("Set ${index+1}" , exerciseRep , false , 0f ))
    }
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