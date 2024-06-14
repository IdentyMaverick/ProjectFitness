
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.projectfitness.R
import database.ProjectFitnessContainer
import viewmodel.ViewModelProfile


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun HistoryExerciseScreen(navController: NavController , viewModelProfile: ViewModelProfile){

    //Database Creation*************************************************************************************************************************************************************
    val context = LocalContext.current
    val scopes = rememberCoroutineScope()
    var projectFitnessContainer = ProjectFitnessContainer(context)
    val itemRepo = projectFitnessContainer.itemsRepository
    val completionWorkoutString by itemRepo.getCmdWorkoutsByWorkoutId(viewModelProfile.selectedCompletedId.value).collectAsState(initial = emptyList())
    var completionDateString = ""
    val completionExerciseString by itemRepo.getAllItemsCompleted(viewModelProfile.selectedCompletedId.value).collectAsState(initial = emptyList())
    var currentExercise by remember { mutableStateOf("") }
    var setrepList = completionExerciseString
    /*CoroutineScope(Dispatchers.IO).launch {
        setrepList = itemRepo.completedGetSetRep(viewModelProfile.selectedCompletedWorkoutId.value)
    }*/

    //******************************************************************************************************************************************************************************


    Box(modifier = Modifier
        .background(Color(0xFF181F26))
        .fillMaxSize()){
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)) {
            Icon(painter = painterResource(id = R.drawable.projectfitnessprevious),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .padding(start = 5.dp)
                    .width(25.dp)
                    .height(25.dp)
                    .align(Alignment.CenterVertically)
                    .clickable(onClick = {
                        navController.navigate("profile")
                    }))
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "PROJECT FITNESS" ,
                style = TextStyle(fontSize = 30.sp ,
                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)) ,
                    color = Color.White ,
                    letterSpacing = 5.sp
                ) ,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 55.dp)
            )
        }
        Column(modifier = Modifier
            .padding(top = 100.dp)
            .align(Alignment.TopCenter)
            .background(Color.Transparent)
            .fillMaxWidth()
            .height(150.dp)
            .background(Color.Transparent)) {
            completionWorkoutString.forEachIndexed{index , item ->
            Log.d("sees",completionWorkoutString.toString())
            Text(text = item.completedWorkoutName , style = TextStyle(fontSize = 40.sp , color = Color.White , fontFamily = FontFamily(
                Font(R.font.postnobillscolombosemibold)
            ) , letterSpacing = 3.sp) ,
                modifier = Modifier.align(Alignment.CenterHorizontally))
            Text(text = "Date Completed  " + completionDateString , style = TextStyle(fontSize = 15.sp , color = Color.White , fontFamily = FontFamily(
                Font(R.font.postnobillscolombosemibold)
            ) , letterSpacing = 3.sp) ,
                modifier = Modifier.align(Alignment.CenterHorizontally))
            Text(text = "Completed in " + item.durationMinutes , style = TextStyle(fontSize = 15.sp , color = Color.White , fontFamily = FontFamily(
                Font(R.font.postnobillscolombosemibold)
            ) , letterSpacing = 3.sp) ,
                modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.size(20.dp))
            Divider(modifier = Modifier
                .padding(end = 30.dp , start = 30.dp), thickness = 1.dp , color = Color.White)
        }
        }
        LazyColumn(modifier = Modifier
            .background(Color.Transparent)
            .fillMaxWidth()
            .height(450.dp)
            .align(Alignment.BottomCenter),) {
            itemsIndexed(completionExerciseString){index , item ->
                Column(  // Egzersiz ismi ve altındaki 3 lü Row ögelerini taşıyan Column
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    if (item != null) {
                        currentExercise = item.exerciseName
                    }
                    if (item != null) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                        ){
                            androidx.compose.material3.Text(
                                text = item.exerciseName,
                                modifier = Modifier.padding(top = 10.dp, start = 20.dp),
                                color = Color(0xFFF1C40F),
                                fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                style = TextStyle(fontSize = 25.sp),
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(painter = painterResource(id = R.drawable.question), contentDescription = null,
                                tint = Color(0xFFF1C40F), modifier = Modifier
                                    .padding(end = 20.dp)
                                    .size(25.dp)
                                    .align(Alignment.CenterVertically)
                                    .clickable {}
                            )
                        }
                    }
                    Spacer(modifier = Modifier.size(5.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {

                        androidx.compose.material3.Text(
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
                        androidx.compose.material3.Text(
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
                        androidx.compose.material3.Text(
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
                                Log.d("sees",item.exerciseName)
                                if (item.exerciseName == currentExercise) {
                                    for (i in item.setrepListCompleted) {
                                        Row {
                                            androidx.compose.material3.Text(
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
                                            androidx.compose.material3.Text(
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
                                            androidx.compose.material3.Text(
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
                                            androidx.compose.material3.Text(
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
                                            Icon(painter = painterResource(id = R.drawable.projectfitnesssetting),
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clickable {},
                                                tint = Color.White.copy(alpha = 0.8f))
                                        }

                                        Spacer(modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        } else {Log.d("sees","loool")}
                    }
                }
            }
        }
    }


}

@Preview
@Composable
fun PreviewHistoryExerciseScreen(){
    HistoryExerciseScreen(navController = rememberNavController() , ViewModelProfile())
}