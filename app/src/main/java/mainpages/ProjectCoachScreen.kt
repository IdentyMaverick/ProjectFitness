
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.projectfitness.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import database.Exercise
import database.ProjectFitnessContainer
import database.ProjectFitnessExerciseEntity
import database.ProjectFitnessWorkoutEntity
import database.SetRep
import kotlinx.coroutines.launch
import viewmodel.ViewModelSave


@Composable
fun ProjectCoachScreen(navController: NavController , viewModelSave: ViewModelSave){

    val db = FirebaseFirestore.getInstance()
    val uid = Firebase.auth.currentUser?.uid

    var configuration = LocalConfiguration.current
    var screenheightDp = configuration.screenHeightDp

    val context = LocalContext.current
    val scopes = rememberCoroutineScope()
    var projectFitnessContainer = ProjectFitnessContainer(context)
    val itemRepo = projectFitnessContainer.itemsRepository

    val sharedPreferences = context.getSharedPreferences("workoutIdNumber", Context.MODE_PRIVATE)
    var workoutIdNumber by remember { mutableStateOf(sharedPreferences.getInt("number",0)) }
    var workoutidNumber by remember { mutableStateOf(sharedPreferences.getInt("number2",1)) }
    Log.d("log2",workoutIdNumber.toString())
    Box(modifier = Modifier
        .background(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFF1C40F), Color(0xFF181F26)),
                start = Offset(0f, 0f),
                end = Offset(0f, screenheightDp.toFloat())
            )
        )
        .fillMaxSize()){


        Column(Modifier.align(Alignment.TopCenter)) {

            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Icon(painter = painterResource(id = R.drawable.projectfitnessprevious),
                    contentDescription = null ,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .size(25.dp)
                        .align(Alignment.CenterVertically)
                        .clickable(onClick = {navController.navigate("activity")}))
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "COACH SELECTIONS",
                    color = Color(0xFF181F26),
                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                    style = TextStyle(fontSize = 20.sp, letterSpacing = 10.sp , textAlign = TextAlign.Center),
                    modifier = Modifier
                        .padding(top = 10.dp , end = 50.dp)
                        .align(Alignment.CenterVertically)
                )
            }



            Spacer(modifier = Modifier
                .size(15.dp)
                .align(Alignment.CenterHorizontally))
            Text(
                text = "Difficulty Level",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                style = TextStyle(fontSize = 20.sp, letterSpacing = 10.sp , textAlign = TextAlign.Center),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier
                .size(10.dp)
                .align(Alignment.CenterHorizontally))
            Row( modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                val challengeDifficulty = viewModelSave.coachesSelectedDifficulty.value
                val totalIcons = 5

                for (i in 1..totalIcons) {
                    val iconColor = if (i <= challengeDifficulty) Color(0xFFF1C40F) else Color.White

                    Icon(
                        painter = painterResource(id = R.drawable.skull),
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp),
                        tint = iconColor
                    )

                    if (i < totalIcons) {
                        Spacer(modifier = Modifier.size(20.dp))
                    }
                }
            }
            Spacer(modifier = Modifier
                .size(10.dp)
                .align(Alignment.CenterHorizontally))
            Text(
                text = "Exercise Detail",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                style = TextStyle(fontSize = 20.sp, letterSpacing = 10.sp , textAlign = TextAlign.Center),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier
                .size(10.dp)
                .align(Alignment.CenterHorizontally))
            Text(
                text = "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
                color = Color(0xFF181F26),
                fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                style = TextStyle(fontSize = 15.sp, letterSpacing = 5.sp , textAlign = TextAlign.Center),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(start = 5.dp, end = 5.dp)
            )
        }

        Column(modifier = Modifier
            .align(Alignment.BottomCenter)
            .background(Color.Transparent)
            .fillMaxWidth()
            .height(420.dp)) {

            Text(
                text = viewModelSave.coachesSelectedName.value,
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                style = TextStyle(fontSize = 30.sp, letterSpacing = 3.sp , textAlign = TextAlign.Center),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
            if (viewModelSave.coachesSelectedName.value == "Back Finisher"){viewModelSave.coachProjectMain = viewModelSave.readyBackFinisher} else {}
            LazyColumn(modifier = Modifier
                .padding(top = 30.dp)
                .fillMaxWidth()
                .height(280.dp) ,
                state = LazyListState()
            ) {
                itemsIndexed(viewModelSave.coachProjectMain){ index , item ->
                    Box(
                        modifier = Modifier
                            .padding(start = 10.dp, end = 10.dp)
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .background(Color.Transparent, shape = RoundedCornerShape(20.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(start = 0.dp)
                                .width(90.dp)
                                .height(100.dp)
                                .clip(shape = RoundedCornerShape(20.dp))
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color(0xFF181F26)  // End color
                                        ),
                                        start = Offset.Infinite,
                                        end = Offset(100.0f, 0.0f)
                                    ),
                                    shape = RoundedCornerShape(20.dp)
                                )

                        ) {
                            if (index == 0) {
                                Image(
                                    painterResource(id = R.drawable.secondinfo),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    alpha = 0.7f,
                                    modifier = Modifier
                                        .fillMaxSize(),
                                )
                            } else if (index == 1) {
                                Image(
                                    painterResource(id = R.drawable.login),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    alpha = 0.7f,
                                    modifier = Modifier
                                        .fillMaxSize(),
                                )
                            }
                            else if (index == 2) {
                                Image(
                                    painterResource(id = R.drawable.gym),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    alpha = 0.7f,
                                    modifier = Modifier
                                        .fillMaxSize(),
                                )
                            }
                            else if (index == 3) {
                                Image(
                                    painterResource(id = R.drawable.gymroomwith),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    alpha = 0.7f,
                                    modifier = Modifier
                                        .fillMaxSize(),
                                )
                            }
                            else if (index == 4) {
                                Image(
                                    painterResource(id = R.drawable.gymroomwithgym),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    alpha = 0.7f,
                                    modifier = Modifier
                                        .fillMaxSize(),
                                )
                            }
                            else if (index == 5) {
                                Image(
                                    painterResource(id = R.drawable.gymroomgym),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    alpha = 0.7f,
                                    modifier = Modifier
                                        .fillMaxSize(),
                                )
                            }
                            else if (index == 6) {
                                Image(
                                    painterResource(id = R.drawable.login),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    alpha = 0.7f,
                                    modifier = Modifier
                                        .fillMaxSize(),
                                )
                            }
                        }
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(end = 40.dp)
                        ) {
                            Row(modifier = Modifier) {
                                Text(
                                    text = item,
                                    modifier = Modifier
                                        .padding(start = 110.dp , top = 20.dp),
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                    textAlign = TextAlign.Left,
                                    color = Color(0xFFD9D9D9),
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(painter = painterResource(id = R.drawable.question),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier
                                        .padding(top = 30.dp)
                                        .size(30.dp)
                                        .clickable {
                                            viewModelSave.flagA.value = false
                                            viewModelSave.flagB.value = false
                                            viewModelSave.flagC.value = false
                                            viewModelSave.flagD.value = true
                                            viewModelSave.selectedWorkoutName.value = item
                                            viewModelSave.updateSelectedItemName(item)
                                            navController.navigate("workoutsettingdetails")
                                        }
                                )
                            }
                        }

                    }
                    Spacer(modifier = Modifier.size(30.dp))
                }
            }
            Button(onClick = {
                val editor = sharedPreferences.edit()
                scopes.launch {
                    itemRepo.insertItem(
                        ProjectFitnessWorkoutEntity(
                            workoutName = viewModelSave.coachesSelectedName.value,
                            exercises = mutableStateListOf(Exercise("", 0, 0))
                        )
                    )

                    if (uid != null) {
                        val docRef = db.collection("Workouts").document("$uid").collection("HasWorkout").document("${workoutidNumber}")
                        val hashMapOf2 = hashMapOf(
                            "workoutid" to workoutIdNumber ,
                            "workoutname" to viewModelSave.coachesSelectedName.value ,
                            "exercises" to mutableStateListOf(Exercise("", 0, 0))
                        )
                        docRef.set(hashMapOf2)
                    }

                    viewModelSave.setrepCoach.add(SetRep("Set 1" , 12 , false , 0f))
                    itemRepo.insertItems(
                        ProjectFitnessExerciseEntity(
                            exerciseId = (workoutidNumber) + 1,
                            exercisesName = "Lat Pulldown",
                            exercisesRep = 12,
                            exercisesSet = 3,
                            setrepList = viewModelSave.setrepCoach
                        )
                    )

                    itemRepo.insertItems(
                        ProjectFitnessExerciseEntity(
                            exerciseId = (workoutidNumber) + 1,
                            exercisesName = "Lat Pulldown 2",
                            exercisesRep = 12,
                            exercisesSet = 3,
                            setrepList = viewModelSave.setrepCoach
                        )
                    )
                    itemRepo.insertItems(
                        ProjectFitnessExerciseEntity(
                            exerciseId = (workoutidNumber) + 1,
                            exercisesName = "Lat Pulldown 3",
                            exercisesRep = 12,
                            exercisesSet = 3,
                            setrepList = viewModelSave.setrepCoach
                        )
                    )
                }
                editor.putInt("number",workoutIdNumber+1)
                editor.putInt("number2",workoutidNumber+1)
                editor.apply()

                navController.navigate("home")
            } , modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 20.dp)
                .width(250.dp)
                .height(40.dp),
                colors = androidx.compose.material.ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF1C40F)),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "ADD TO YOUR EXERCISE LIST",
                    color = Color.Black,
                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                    style = TextStyle(fontSize = 20.sp, letterSpacing = 0.sp , textAlign = TextAlign.Center),
                )
            }
        }
    }
}

@Preview(name = "phone", device = "spec:shape=Normal,width=360,height=1020,unit=dp,dpi=402")
@Composable
fun PreviewCoachScreen(){
    ProjectCoachScreen(navController = rememberNavController() , viewModelSave = ViewModelSave())
}