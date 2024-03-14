package homes.inside

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import database.Exercise
import com.example.projectfitness.R
import viewmodel.ViewModelSave
import database.Workout
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@Composable
fun WorkoutSettingScreen(navController: NavController,viewModelSave: ViewModelSave){

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
    Log.d("SCREEN","Screen Width is : $screenwidthDp")

    val marginWorkoutSettingsTextTopDp = if (useDiffrentValue1)
    { 20.dp }
    else if (useDiffrentValue2)
    { 40.dp }
    else if (useDiffrentValue3)
    { 60.dp }
    else
    { 30.dp }


    val marginWorkoutSettingsTextWidthDp = if (useDiffrentValue4)
    { 300.dp }
    else if (useDiffrentValue5)
    { 10.dp }
    else if (useDiffrentValue6)
    { 10.dp }
    else
    { 400.dp }

    var showDialog by remember { mutableStateOf(false) }

    var WorkoutList = viewModelSave.workouts()
    var selectedIndexWorkoutinList = viewModelSave.selectedIndexWorkoutinList
    var selectedworkoutList = WorkoutList[selectedIndexWorkoutinList.value.toInt()]
    var selectedWorkoutName = viewModelSave.selectedWorkoutName


    val data = remember { mutableStateOf(  selectedworkoutList.exercises ) }


    //var selectedListWorkoutIndex = viewModelSave.selectedListWorkoutIndex
    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = Color(0xFF181F26)))
    {
        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color(0xFF181F26))
            .padding(top = screenheightDp.dp/3)
            ) {
            VerticalReorderList(data,viewModelSave,selectedworkoutList)
            Row (horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Overview", fontSize = 15.sp, fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)), style = TextStyle(letterSpacing = 3.sp), color = Color.White, modifier = Modifier.padding(start = 30.dp))
                Text(text = "Set x Reps", fontSize = 10.sp, fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),style = TextStyle(letterSpacing = 3.sp),color = Color.White,modifier = Modifier.padding(end = 30.dp))
            }


        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(screenheightDp.dp / 25)
            .paint(
                painter = painterResource(id = R.drawable.cablecrossover),
                contentScale = ContentScale.Crop,
                alpha = 0.3f
            ))
        Icon(
            painter = painterResource(id = R.drawable.left),
            contentDescription = null,
            modifier = Modifier
                .clickable(onClick = { navController.navigate("home") })
                .size(30.dp)
                .padding(top = 13.dp),
            tint = Color(0xFFD9D9D9)
        )
        Text(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp),
            text = "PROJECT FITNESS",
            fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
            color = Color(0xFFF1C40F),
            style = TextStyle(fontSize = 20.sp,letterSpacing = 10.sp)
        )
            Text(text = ""+selectedWorkoutName.value,
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                style = TextStyle(fontSize = 40.sp,letterSpacing = 3.sp),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = screenheightDp.dp / 10)
            )
        Row {
            Button(onClick = {navController.navigate("workoutlog")},
                modifier = Modifier
                    .padding(top = screenheightDp.dp / 3.9f, start = screenwidthDp.dp / 20)
                    .width(220.dp)
                    .height(35.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1C40F)),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "START WORKOUT",
                    fontFamily = FontFamily(Font(R.font.postnobillscolombo)),
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(letterSpacing = 1.sp, fontSize = 20.sp),
                    color = Color(0xFF21282F))
            }
            Button(onClick = {},
                modifier = Modifier
                    .padding(top = screenheightDp.dp / 3.75f, start = marginWorkoutSettingsTextWidthDp / 20)
                    .width(125.dp)
                    .height(19.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text(text = "WORKOUT SETTINGS",
                    fontFamily = FontFamily(Font(R.font.postnobillscolombo)),
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(letterSpacing = 1.sp, fontSize = 10.sp),
                    color = Color(0xFF21282F))
            }
        }

        if (showDialog)
        {
            popUpScreen (onDismiss = {showDialog = false },viewModelSave,navController)
        }

    }

}

@Composable
fun popUpScreen(onDismiss : () -> Unit, viewModelSave: ViewModelSave, navController: NavController)
{
    Dialog(
        onDismissRequest = {onDismiss()},
        properties = DialogProperties(dismissOnClickOutside = true)
          )
        {
            var workoutname by remember { mutableStateOf("") }
            Box(modifier = Modifier
                .width(270.dp)
                .height(100.dp)
                .background(
                    color = Color(0xFF21282F), shape = RoundedCornerShape(20.dp)
                )
            )
            {
                Text(text = "Change The Name of Workout",
                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                    style = TextStyle(letterSpacing = 1.sp, fontSize = 17.sp),
                    modifier = Modifier
                        .align(Alignment.TopCenter),
                    color = Color(0xFFD9D9D9)
                )
                BasicTextField(value = workoutname, onValueChange = {workoutname = it },modifier = Modifier
                    .align(Alignment.Center)
                    .height(22.dp)
                    .width(215.dp)
                    .background(Color(0xFF181F26), shape = RoundedCornerShape(1.dp)),
                    textStyle = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                        color = Color(0xFFD9D9D9)),
                    maxLines = 1,
                )
                Button(
                    onClick = {
                        onDismiss()

                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .width(60.dp)
                        .height(20.dp)
                        .padding(bottom = 5.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFFD9D9D9)),
                    shape = RoundedCornerShape(5.dp)

                ) {
                    Text("Save",
                        fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                        color = Color.Black)
                }
            }
        }
}
@SuppressLint("SuspiciousIndentation")
@Composable
fun VerticalReorderList(list : MutableState<List<Exercise>>, viewModelSave: ViewModelSave, selectedWorkoutList : Workout) {
    var data = list
    //var selectedReps = selectedWorkoutList.exercises.reps
    //var selectedSets = selectedWorkoutList.exercises[0].sets
    var screenheight = LocalConfiguration.current.screenHeightDp.dp
    var screenwidth = LocalConfiguration.current.screenWidthDp.dp
    val state = rememberReorderableLazyListState(onMove = { from, to ->
        data.value = data.value.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    })
    LazyColumn(
        state = state.listState,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(start = 20.dp,end = 20.dp, top = 30.dp)
            .reorderable(state)
            .detectReorderAfterLongPress(state),

    ) {
        itemsIndexed(data.value) {index,item ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(
                                Color(0xFF21282F), shape = RoundedCornerShape(15.dp)
                            )
                            .clickable(onClick = { /*navController.navigate("workoutsettingscreenworkoutdetails")*/ })
                    )
                    {
                        Text(
                            text = "" + item.name,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 20.dp),
                            fontSize = 20.sp,
                            fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                            textAlign = TextAlign.Center,
                            color = Color(0xFFD9D9D9),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${item.sets} x ${item.reps}",
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 15.dp),
                            fontSize = 20.sp,
                            fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                            textAlign = TextAlign.Center,
                            color = Color(0xFFD9D9D9),
                            fontWeight = FontWeight.Bold
                        )
                    }
            Spacer(modifier = Modifier.size(10.dp))
    }
}
    loopSet(data)
}
fun loopSet(mutableList: MutableState<List<Exercise>>)
{
    for (i in mutableList.value)
    {

    }
}

@Preview
@Composable
fun WorkoutSettingScreenPreview(){
    WorkoutSettingScreen(navController = rememberNavController(),viewModelSave = viewModel())
}