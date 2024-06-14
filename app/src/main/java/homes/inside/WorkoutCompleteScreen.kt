package homes.inside

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.projectfitness.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.core.cartesian.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.shape.Shape
import database.ProjectFitnessContainer
import kotlinx.coroutines.launch
import viewmodel.ViewModelSave
import java.text.DateFormatSymbols
import java.util.Locale


@Composable
fun WorkoutCompleteScreen(navController: NavController,viewModelSave: ViewModelSave){
    var currentScreen by remember { mutableStateOf("screen1") }
    var totalReps by remember { mutableStateOf(0) }

    //Database Creation*************************************************************************************************************************************************************
    val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser?.uid
    val context = LocalContext.current
    val scopes = rememberCoroutineScope()
    var projectFitnessContainer = ProjectFitnessContainer(context)
    val itemRepo = projectFitnessContainer.itemsRepository
    val itemRepos = projectFitnessContainer.itemsRepository

    val itemsState by itemRepos.getAllCompletedWorkouts().collectAsState(initial = emptyList())

    //******************************************************************************************************************************************************************************

    Log.d("converTime","${viewModelSave.hours.value} "+" " )

    val buttonStates = remember { mutableStateMapOf(
        "screen1" to true,
        "screen2" to false,
        "screen3" to false,
        "screen4" to false,
    )}




    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF1C40F))){
        Row(modifier = Modifier
            .background(Color.Transparent)
            .padding(top = 20.dp)
            .align(Alignment.TopCenter))
        {
            Text(
                text = "Workout \nCompleted",
                style = TextStyle(
                    fontSize = 75.sp,
                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                    lineHeight = 55.sp
                ),
                color = Color(0xFF000000),
                modifier = Modifier.padding(start = 25.dp),
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(painter = painterResource(id = R.drawable.projectfitnessgo), contentDescription = null,
                tint = Color(0xFF181F26), modifier = Modifier
                    .padding(top = 30.dp)
                    .size(40.dp)
                    .clickable {
                        viewModelSave.hours.value = ""
                        viewModelSave.totalSetsOfCompletedWorkout.value = 0
                        viewModelSave.totalRepsOfCompletedWorkout.value = 0
                        navController.navigate("home")
                        scopes.launch {
                            itemRepo.updateCompletedWorkout(rate = viewModelSave.workoutRate.value , notes = viewModelSave.note.value , completedWorkoutId = viewModelSave.completedWorkoutId.value ) }
                        viewModelSave.note.value = ""
                        viewModelSave.totalWeightOfCompletedWorkout.value = 0
                        viewModelSave.hourInt.value = 0
                        viewModelSave.secondInt.value = 0
                        viewModelSave.minuteInt.value = 0
                        for(workout in itemsState) {
                            db.collection("completedWorkouts").document(user ?: "").collection("completedWorkouts")
                                .add(workout)
                                .addOnSuccessListener { documentReference ->
                                    Log.d("success" , documentReference.id)
                                }
                                .addOnFailureListener{documentReference ->
                                    Log.d("fail" , documentReference.message.toString())
                                }
                        }
                    }

            )

        }

        Row(modifier = Modifier
            .background(Color.Transparent)
            .padding(top = 180.dp)
            .align(Alignment.TopCenter)) {
            Button(onClick = { currentScreen = "screen1"
                buttonStates["screen1"] = true
                buttonStates["screen2"] = false
                buttonStates["screen3"] = false
                buttonStates["screen4"] = false
                             }, shape = RoundedCornerShape(5.dp),modifier = Modifier
                .height(74.dp)
                .width(68.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (buttonStates["screen1"] == true) Color(0xFF2A2D31) else Color(0xFF181F26)),
                contentPadding = PaddingValues(0.dp),
                border = BorderStroke(1.dp,Color(0xFF2A2D31))
            ) {

                    Icon(painter = painterResource(id = R.drawable.saat), contentDescription = null,
                        tint = Color(0xFFF1C40F))
            }
            Spacer(modifier = Modifier.size(12.dp))
            Button(onClick = { currentScreen = "screen2"
                buttonStates["screen1"] = false
                buttonStates["screen2"] = true
                buttonStates["screen3"] = false
                buttonStates["screen4"] = false}, shape = RoundedCornerShape(5.dp),modifier = Modifier
                .height(74.dp)
                .width(68.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (buttonStates["screen2"] == true) Color(0xFF2A2D31) else Color(0xFF181F26)),
                contentPadding = PaddingValues(0.dp),
                border = BorderStroke(1.dp,Color(0xFF2A2D31))
            ) {

                Icon(painter = painterResource(id = R.drawable.dumbbellicon), contentDescription = null,
                    tint = Color(0xFFF1C40F),
                    modifier = Modifier.size(45.dp))
            }
            Spacer(modifier = Modifier.size(12.dp))
            Button(onClick = { currentScreen = "screen3"
                buttonStates["screen1"] = false
                buttonStates["screen2"] = false
                buttonStates["screen3"] = true
                buttonStates["screen4"] = false}, shape = RoundedCornerShape(5.dp),modifier = Modifier
                .height(74.dp)
                .width(68.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (buttonStates["screen3"] == true) Color(0xFF2A2D31) else Color(0xFF181F26)),
                contentPadding = PaddingValues(0.dp),
                border = BorderStroke(1.dp,Color(0xFF2A2D31))
            ) {

                Icon(painter = painterResource(id = R.drawable.editnote), contentDescription = null,
                    tint = Color(0xFFF1C40F))
            }
            Spacer(modifier = Modifier.size(12.dp))
            Button(onClick = { currentScreen = "screen4"
                buttonStates["screen1"] = false
                buttonStates["screen2"] = false
                buttonStates["screen3"] = false
                buttonStates["screen4"] = true}, shape = RoundedCornerShape(5.dp),modifier = Modifier
                .height(74.dp)
                .width(68.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (buttonStates["screen4"] == true) Color(0xFF2A2D31) else Color(0xFF181F26)),
                contentPadding = PaddingValues(0.dp),
                border = BorderStroke(1.dp,Color(0xFF2A2D31))
            ) {

                Icon(painter = painterResource(id = R.drawable.projectfitnesstick), contentDescription = null,
                    tint = Color(0xFFF1C40F))
            }
        }

        Column(modifier = Modifier
            .padding(top = 70.dp)
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.Transparent))
        {
            DisplayScreen(screen = currentScreen,viewModelSave)
        }
    }

}

@Composable
fun DisplayScreen(screen: String,viewModelSave: ViewModelSave) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    )
    {
        if (screen == "screen1") {
            Screen1Content(viewModelSave = viewModelSave)
        } else if (screen == "screen2") {
            Screen2Content(viewModelSave = viewModelSave)
        } else if (screen == "screen3") {
            Screen3Content(viewModelSave = viewModelSave)
        }
        else if (screen == "screen4") {
            Screen4Content()
        }
    }
}

@Composable
fun Screen1Content(viewModelSave: ViewModelSave){

    //--------------------------------------------------------
    val context = LocalContext.current
    var projectFitnessContainer = ProjectFitnessContainer(context)
    val itemRepos = projectFitnessContainer.itemsRepository
    var completedWorkoutSize = itemRepos.getAllCompletedWorkouts().collectAsState(initial = emptyList()).value.size
    //--------------------------------------------------------
    viewModelSave.completedWorkoutSize.value = completedWorkoutSize

    val dayNames = DateFormatSymbols.getInstance(Locale.US).shortWeekdays
    val bottomAxisValueFormatter = CartesianValueFormatter { x, _, _ -> "${dayNames[x.toInt() % 12]} ’${20 + x.toInt() / 12}" }

    Box(modifier = Modifier
        .fillMaxHeight(),){
        Column (modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top = 200.dp, start = 30.dp, end = 30.dp)
            .background(Color.Transparent)
            .verticalScroll(rememberScrollState())) {


            Row(Modifier.background(Color.Transparent)) {
                    Text(text = "Workout Time",
                        style = TextStyle(fontFamily = FontFamily(Font(R.font.postnobillscolombobold)),
                            textAlign = TextAlign.Center, fontSize = 23.sp)
                        , color = Color(0xFF000000),
                        modifier = Modifier)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "Completed Workouts",
                    style = TextStyle(fontFamily = FontFamily(Font(R.font.postnobillscolombobold)),
                        textAlign = TextAlign.Center, fontSize = 23.sp)
                    , color = Color(0xFF000000),
                    modifier = Modifier)
            }
                    // time info
                    Row(
                        Modifier
                            .background(Color.Transparent)
                            .fillMaxWidth()
                            ) {
                        Text(text = viewModelSave.hour.value + viewModelSave.minutes.value + viewModelSave.seconds.value,
                            style = TextStyle(fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                textAlign = TextAlign.Center, fontSize = 20.sp, letterSpacing = 1.sp)
                            , color = Color(0xFF000000),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 20.dp))
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = completedWorkoutSize.toString(),
                            style = TextStyle(fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                textAlign = TextAlign.Center, fontSize = 35.sp, letterSpacing = 1.sp)
                            , color = Color(0xFF000000),
                            modifier = Modifier
                                .padding(end = 0.dp))
                        Text(text = "workouts" ,
                            style = TextStyle(fontFamily = FontFamily(Font(R.font.postnobillscolomboregular)),
                                textAlign = TextAlign.Center, fontSize = 15.sp)
                            , color = Color(0xFF000000),
                            modifier = Modifier
                                .padding(top = 20.dp,end = 50.dp))
                    }

            Spacer(modifier = Modifier.size(30.dp))

                Text(text = "Hour / Days Graph",color = Color(0xFF000000) , style = TextStyle(fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                    fontSize = 25.sp), modifier = Modifier
                    .align(Alignment.CenterHorizontally))

                // Graph Drawer
                val scrollState = rememberVicoScrollState()
                val zoomState = rememberVicoZoomState(zoomEnabled = false)
                val modelProducer = remember { CartesianChartModelProducer.build() }
                LaunchedEffect(Unit) { modelProducer.tryRunTransaction { columnSeries { series(x = listOf(1,2,3,4,5,6,7), y = listOf(1,2,1,4,1,2,1)) } } }
            CartesianChartHost(
                chart =
                rememberCartesianChart(
                    rememberColumnCartesianLayer(
                        ColumnCartesianLayer.ColumnProvider.series(
                            rememberLineComponent(
                                color = Color(0xff181F26),
                                thickness = 5.dp,
                                shape = remember { Shape.rounded(allPercent = 15) },
                            ),
                        ),
                    ),
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis(
                        valueFormatter = bottomAxisValueFormatter,
                        itemPlacer = remember {AxisItemPlacer.Horizontal.default(spacing = 1, addExtremeLabelPadding = false)}
                    ),
                ),
                modelProducer = modelProducer,
            )
        }

    }

}

@Composable
fun Screen2Content(viewModelSave: ViewModelSave){

    Box(modifier = Modifier
        .fillMaxHeight()
        .padding() , contentAlignment = Alignment.Center) {
        Column (modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top = 200.dp)
            .align(Alignment.Center)
            .background(Color.Transparent)) {

            Row(Modifier.background(Color.Transparent)) {
                Text(text = "Total Sets",
                    style = TextStyle(fontFamily = FontFamily(Font(R.font.postnobillscolombobold)),
                        textAlign = TextAlign.Center, fontSize = 23.sp)
                    , color = Color(0xFF000000),
                    modifier = Modifier
                        .padding(start = 50.dp))
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "Total Reps",
                    style = TextStyle(fontFamily = FontFamily(Font(R.font.postnobillscolombobold)),
                        textAlign = TextAlign.Center, fontSize = 23.sp)
                    , color = Color(0xFF000000),
                    modifier = Modifier
                        .padding(end = 50.dp))
            }
            Row(
                Modifier
                    .background(Color.Transparent)
                    .fillMaxWidth()
            ) {
                Text(text = viewModelSave.totalSetsOfCompletedWorkout.value.toString(),
                    style = TextStyle(fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                        textAlign = TextAlign.Center, fontSize = 35.sp, letterSpacing = 1.sp)
                    , color = Color(0xFF000000),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 80.dp))
                Text(text = "sets" ,
                    style = TextStyle(fontFamily = FontFamily(Font(R.font.postnobillscolomboregular)),
                        textAlign = TextAlign.Center, fontSize = 15.sp)
                    , color = Color(0XFF000000),
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .align(Alignment.CenterVertically))


                Spacer(modifier = Modifier.weight(1f))


                Text(text = viewModelSave.totalRepsOfCompletedWorkout.value.toString(),
                    style = TextStyle(fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                        textAlign = TextAlign.Center, fontSize = 35.sp, letterSpacing = 1.sp)
                    , color = Color(0xFF000000),
                    modifier = Modifier
                        .padding(end = 0.dp))
                Text(text = "reps" ,
                    style = TextStyle(fontFamily = FontFamily(Font(R.font.postnobillscolomboregular)),
                        textAlign = TextAlign.Center, fontSize = 15.sp)
                    , color = Color(0xFF000000),
                    modifier = Modifier
                        .padding(top = 20.dp,end = 50.dp))
            }
            Text(text = "Total Weight Lifted",
                style = TextStyle(fontFamily = FontFamily(Font(R.font.postnobillscolombobold)),
                    textAlign = TextAlign.Center, fontSize = 23.sp)
                , color = Color(0xFF000000),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally))
            Row(
                Modifier
                    .background(Color.Transparent)
                    .align(Alignment.CenterHorizontally)
            ){
                Text(text = viewModelSave.totalWeightOfCompletedWorkout.value.toString(),
                    style = TextStyle(fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                        textAlign = TextAlign.Center, fontSize = 35.sp, letterSpacing = 1.sp)
                    , color = Color(0xFF000000),
                    modifier = Modifier
                        .padding(end = 0.dp))
                Text(text = "kg" ,
                    style = TextStyle(fontFamily = FontFamily(Font(R.font.postnobillscolomboregular)),
                        textAlign = TextAlign.Center, fontSize = 15.sp)
                    , color = Color(0xFF000000),
                    modifier = Modifier
                        .padding(top = 20.dp))
            }
        }

    }

}

@Composable
fun Screen3Content(viewModelSave: ViewModelSave){

    //--------------------------------------------------------
    val context = LocalContext.current
    var projectFitnessContainer = ProjectFitnessContainer(context)
    val itemRepos = projectFitnessContainer.itemsRepository
    var completedWorkoutSize = itemRepos.getAllCompletedWorkouts().collectAsState(initial = emptyList()).value.size
    val scopes = rememberCoroutineScope()
    //--------------------------------------------------------

    var note by remember { mutableStateOf(viewModelSave.note.value) }
    var workoutRate by remember { mutableStateOf(viewModelSave.workoutRate.value) }
    Log.d("LOL","${viewModelSave.workoutRate.value}")
    var rateWorkoutText by remember { mutableStateOf("") }
    var clicked by remember { mutableStateOf(false) }
    var clicked2 by remember { mutableStateOf(false) }
    var clicked3 by remember { mutableStateOf(false) }
    var clicked4 by remember { mutableStateOf(false) }
    var clicked5 by remember { mutableStateOf(false) }

    var color =  Color.White
    var color2 = Color.White
    var color3 = Color.White
    var color4 = Color.White
    var color5 = Color.White

    if (clicked || viewModelSave.workoutRate.value == 1) {
        color = Color(0xFF000000)
        color2 = Color.White
        color3 = Color.White
        color4 = Color.White
        color5 = Color.White
        clicked2 = false
        clicked3 = false
        clicked4 = false
        clicked5 = false
        }
    else if (clicked2 || viewModelSave.workoutRate.value == 2){
        color = Color(0xFF000000)
        color2 = Color(0xFF000000)
        color3 = Color.White
        color4 = Color.White
        color5 = Color.White
        clicked = false
        clicked3 = false
        clicked4 = false
        clicked5 = false
        }
    else if (clicked3 || viewModelSave.workoutRate.value == 3)
    {
        color = Color(0xFF000000)
        color2 = Color(0xFF000000)
        color3 = Color(0xFF000000)
        color4 = Color.White
        color5 = Color.White
        clicked = false
        clicked2 = false
        clicked4 = false
        clicked5 = false
    }
    else if (clicked4 || viewModelSave.workoutRate.value == 4)
    {
        color = Color(0xFF000000)
        color2 = Color(0xFF000000)
        color3 = Color(0xFF000000)
        color4 = Color(0xFF000000)
        color5 = Color.White
        clicked = false
        clicked2 = false
        clicked3 = false
        clicked5 = false
    }
    else if (clicked5 || viewModelSave.workoutRate.value == 5)
    {
        color = Color(0xFF000000)
        color2 = Color(0xFF000000)
        color3 = Color(0xFF000000)
        color4 = Color(0xFF000000)
        color5 = Color(0xFF000000)
        clicked = false
        clicked2 = false
        clicked3 = false
        clicked4 = false
    }


    Box(modifier = Modifier
        .fillMaxHeight(), contentAlignment = Alignment.Center) {
        Column (modifier = Modifier
            .align(Alignment.Center)
            .padding(bottom = 100.dp)
            .background(Color.Transparent)) {

            Text(text = "Rate Workout",
                style = TextStyle(fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                    textAlign = TextAlign.Center, fontSize = 35.sp)
                , color = Color(0xFF000000),
                modifier = Modifier)
            Spacer(modifier = Modifier.size(30.dp))

        }
        Row(Modifier) {
            IconButton(
                onClick = { clicked = true
                          viewModelSave.workoutRate.value = 1
                          rateWorkoutText = "Too weak: This exercise was difficult for you and you could not cope."}, modifier = Modifier

            ) {
                Icon(
                    painterResource(id = R.drawable.dumbbellicon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(90.dp),
                    tint = color
                )

            }
            IconButton(
                onClick = {
                            clicked = false
                            clicked2 = true
                    viewModelSave.workoutRate.value = 2
                            rateWorkoutText = " Weak: You've had some difficulties with exercise and need to recover." }, modifier = Modifier

            ) {
                Icon(
                    painterResource(id = R.drawable.dumbbellicon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(90.dp),
                    tint = color2
                )

            }
            IconButton(
                onClick = {
                            clicked = false
                            clicked2 = false
                            clicked3 = true
                    viewModelSave.workoutRate.value = 3
                            rateWorkoutText = "Medium: You have completed the exercise but further effort is needed."}, modifier = Modifier

            ) {
                Icon(
                    painterResource(id = R.drawable.dumbbellicon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(90.dp),
                    tint = color3
                )

            }
            IconButton(
                onClick = {
                            clicked = false
                            clicked2 = false
                            clicked3 = false
                            clicked4 = true
                    viewModelSave.workoutRate.value = 4
                            rateWorkoutText = "Good: You did well in the exercise and can improve further."}, modifier = Modifier

            ) {
                Icon(
                    painterResource(id = R.drawable.dumbbellicon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(90.dp),
                    tint = color4
                )

            }
            IconButton(
                onClick = {
                            clicked = false
                            clicked2 = false
                            clicked3 = false
                            clicked4 = false
                            clicked5 = true
                    viewModelSave.workoutRate.value = 5
                            rateWorkoutText = "Excellent: You did great in training and performed at your peak level."}, modifier = Modifier

            ) {
                Icon(
                    painterResource(id = R.drawable.dumbbellicon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(90.dp),
                    tint = color5
                )

            }


        }

        Column (modifier = Modifier
            .align(Alignment.Center)
            .padding(top = 200.dp)
            .background(Color.Transparent)) {
            Spacer(modifier = Modifier.size(80.dp))
            Text(text = rateWorkoutText,
                style = TextStyle(fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                    textAlign = TextAlign.Center, fontSize = 20.sp)
                , color = Color(0xFF000000),
                modifier = Modifier.padding(start = 10.dp, end = 10.dp))



            Text(text = "Take Note About Workout",
                style = TextStyle(fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                    textAlign = TextAlign.Left, fontSize = 25.sp)
                , color = Color(0xFF000000),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp))

            TextField(value = note, onValueChange  = {
                note = it
                viewModelSave.note.value = it },
                modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(start = 20.dp, end = 20.dp),
                label = { Text(text = "Write Note Here", color = Color(0xFF181F26))},
                maxLines = 5,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
            )

        }


    }

}

@Composable
fun Screen4Content(){

    Box(modifier = Modifier
        .fillMaxHeight()
        .padding() , contentAlignment = Alignment.Center) {
        Column (modifier = Modifier
            .align(Alignment.Center)
            .background(Color.Transparent)) {

            Text(text = "SOON",color = Color(0xFF181F26), style = TextStyle(fontSize = 25.sp, fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold))))

        }

    }

}



@Preview
@Composable
fun PreviewWorkoutCompleteScreen(){
    WorkoutCompleteScreen(navController = rememberNavController(), viewModelSave = ViewModelSave() )
}

// 01:50
// hour = 01
// minute = 50