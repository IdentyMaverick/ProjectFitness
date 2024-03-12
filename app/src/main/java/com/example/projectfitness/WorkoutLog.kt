package com.example.projectfitness

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

@SuppressLint("SuspiciousIndentation")
@Composable
fun WorkoutLog(navController: NavController,viewModelSave: ViewModelSave){


    val WorkoutList = viewModelSave.workouts()
    val selectedIndexWorkoutinList = viewModelSave.selectedIndexWorkoutinList
    val selectedworkoutList = WorkoutList[selectedIndexWorkoutinList.value.toInt()]
    val gradientColors = listOf(Color(0xFF000000), Color(0x00000000))
    val Config = LocalConfiguration.current
    val screenwheight = Config.screenHeightDp.dp
    val screenwidth = Config.screenWidthDp.dp
    var counter by remember { mutableStateOf(0) }
    val exercisename by rememberUpdatedState(returnNextExerciseName(selectedworkoutList, counter))
    val selectedBackgroundImage by rememberUpdatedState(returnNextExerciseImage(exercisename))
    val color : Color
    var flag = false
    var showDialog by remember{ mutableStateOf(false) }
    var counters = 1
    var text by remember { mutableStateOf("") }


    if (!flag){ color = Color.White }
    else{color = Color(0xFFF1C40F) }

    Box(modifier = Modifier
        .fillMaxSize()
        .paint(painterResource(id = R.drawable.cablecrossover), contentScale = ContentScale.Crop)
        .background(
            brush = Brush.verticalGradient(
                colors = gradientColors,
                startY = 1100f,
                endY = 0f
            ), alpha = 0.8f
        ))
    {

        if (showDialog)
        {
            Dialog(onDismissRequest = { showDialog=false })
            {
                Box(modifier = Modifier
                    .width(300.dp)
                    .height(150.dp)
                    .background(Color(0xFF181F26), shape = RoundedCornerShape(15.dp))
                    .border(
                        width = 5.dp,
                        color = Color(0xFFF1C40F),
                        shape = RoundedCornerShape(15.dp)
                    ))
                {
                    Text(text = "Do you want to finish your workout without saving ? ", style = TextStyle(fontSize = 20.sp, fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)), textAlign = TextAlign.Center, letterSpacing = 1.sp), modifier = Modifier.padding(10.dp), color = Color.White)
                    Button(onClick = {navController.navigate("home") },modifier = Modifier.align(Alignment.BottomStart), colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent))
                    {
                        Text(text = "Yes",color = Color(0xFFF1C40F))
                    }
                }
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(Color(0xFF181F26)),
            horizontalArrangement = Arrangement.SpaceBetween )
        {
            Icon(painter = painterResource(id = R.drawable.left), contentDescription = null, tint = Color.White,modifier = Modifier.clickable (
                onClick = { showDialog = true
                            Log.d("CLICK","CLICKED $showDialog")} ) )
            Box(modifier = Modifier
                .fillMaxWidth()
                .align(CenterVertically))
            {
                Text(text = "Select Exercises", style = TextStyle(fontSize = 25.sp, fontFamily = FontFamily(Font(R.font.postnobillscolombo)), color = Color.White, textAlign = TextAlign.Right, fontWeight = FontWeight.Bold), modifier = Modifier
                    .align(
                        CenterEnd
                    )
                    .padding(end = screenwidth / 7))
                Icon(imageVector = Icons.Default.List, contentDescription = null, tint = Color(0xFFF1C40F), modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.CenterEnd)
                    .padding(end = screenwidth / 20) )
            }

        }

        Row(
            Modifier
                .fillMaxWidth()
                .align(Alignment.Center) ,
                horizontalArrangement = Arrangement.SpaceBetween )
        {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Favorite icon", tint = Color.White, modifier = Modifier
                .padding(start = 15.dp)
                .clickable(onClick = {
                    if (counter > 0) counter--
                    Log.d("MAP2", "$counter")
                }))
            Text(text = ""+ exercisename,
                style = TextStyle(fontSize = 35.sp, color = Color(0xFFF1C40F), fontFamily = FontFamily(Font(R.font.postnobillscolombo)), letterSpacing = 1.sp),
                modifier = Modifier)
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Favorite icon",
                tint = Color.White,
                modifier = Modifier
                    .padding(end = 15.dp)
                    .clickable(onClick = {
                        if (counter + 2 <= selectedworkoutList.exercises.size) {
                            counter++
                            //Log.d("MAP2", "$counter")
                        }
                    })
            )
        }
        val setRepMap = ReturnSetRepNumber(selectedworkoutList.exercises)
        Log.d("MAP","${setRepMap.entries}")
        LazyColumn(state = rememberLazyListState(), modifier = Modifier
            .align(Alignment.Center)
            .padding(top = screenwheight / 2.5f)
        ) {

            itemsIndexed(selectedworkoutList.exercises) { index, item ->
                val setRepList = setRepMap[counter] ?: emptyList()
                var conditionMet by remember { mutableStateOf(false) }

                LaunchedEffect(text,counters) {
                    // Check the condition when counters change
                    if (counters - 1 == (setRepMap[counters] ?: emptyList()).size) {
                        conditionMet = true
                    }
                }

                for (setRep in setRepList) {
                    if (counters - 1 < setRepList.size) {
                        Log.d("COUNTERS", "$counters")
                        Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                            Text(
                                text = "Set  ${counters} ",
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    color = color,
                                    fontFamily = FontFamily(Font(R.font.postnobillscolombo)),
                                    letterSpacing = 1.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(end = 10.dp)
                            )

                            BasicTextField(
                                value = text,
                                onValueChange = {
                                    text = it
                                    counters = 1
                                },
                                modifier = Modifier
                                    .height(35.dp)
                                    .width(270.dp)
                                    .background(
                                        Color.Transparent,
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                textStyle = TextStyle(
                                    fontSize = 15.sp,
                                    fontFamily = FontFamily(Font(R.font.poppinslighttext)),
                                    color = Color(0xFFD9D9D9)
                                ),
                                maxLines = 1,
                                decorationBox = { innerTextField ->
                                    Row(
                                        modifier = Modifier
                                            .padding(horizontal = 20.dp)
                                            .fillMaxWidth()
                                            .background(
                                                color = Color(0xFF21282F),
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .border(
                                                width = 2.dp,
                                                color = Color(0xFF181F26),
                                                shape = RoundedCornerShape(10.dp)
                                            ),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowRight,
                                            contentDescription = "Favorite icon",
                                            tint = Color(0xFFD9D9D9),
                                            modifier = Modifier.padding(start = 5.dp)
                                        )
                                        Spacer(modifier = Modifier.width(width = 10.dp))
                                        innerTextField()
                                    }
                                }
                            )
                        }
                        counters++
                        Spacer(modifier = Modifier.size(10.dp))
                    }
                }
            }
        }
        Text(text = "Finish Workout", style = TextStyle(fontSize = 25.sp, fontFamily = FontFamily(Font(R.font.postnobillscolombo)), color = Color.White, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, letterSpacing = 2.sp),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = screenwheight / 11)
                .clickable(onClick = { }))
        Text(text = "Workout Time " + timerWorkout(), style = TextStyle(fontSize = 20.sp, fontFamily = FontFamily(Font(R.font.postnobillscolombo)), color = Color.White, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = screenwheight / 20))
        Text(text = "Rest " + "x" + " seconds", style = TextStyle(fontSize = 20.sp, fontFamily = FontFamily(Font(R.font.postnobillscolombo)), color = Color.Black, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color(0xFFF1C40F)))
    }

    }

fun returnNextExerciseName(selectedworkoutList : Workout,i : Int) : String
{
    if(i+1<=selectedworkoutList.exercises.size)
    {
        return selectedworkoutList.exercises[i].name
    }
    else
    {
        //NOTHING
    }
    return selectedworkoutList.exercises[selectedworkoutList.exercises.size-1].name
}
fun returnNextExerciseImage(exerciseNames : String) : Int
{
    val match = gymDataClass.find { it.name == exerciseNames }

    return match?.image ?: 0
}
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun timerWorkout() : String
{
    var second by remember { mutableStateOf(0) }
    var minute by remember { mutableStateOf(0) }
    LaunchedEffect(key1 = second)
    {
        while (second >= 0)
        {
            delay(1000L)
            second++
            if (second.equals(60))
            {
                minute++
                second = 0
            }
        }
    }
    return "%02d:%02d".format(minute, second)
}
@Composable
fun RestTimer() : String
{
    var second by remember { mutableStateOf(0) }
    var minute by remember { mutableStateOf(0) }
    LaunchedEffect(key1 = second)
    {
        while (second >= 0)
        {
            delay(1000L)
            second++
            if (second.equals(0))
            {
                minute--
                second = 59
            }
        }
    }
    return "%02d:%02d".format(minute, second)
}

@Composable
fun ReturnSetRepNumber(exercises: List<Exercise>): Map<Int, List<String>> {
    val setRepMap = mutableMapOf<Int, List<String>>()

    exercises.forEachIndexed { index, exercise ->
        val setRepList = mutableListOf<String>()
        for (i in 1..exercise.sets) {
            setRepList.add("${exercise.reps}")
        }
        setRepMap[index] = setRepList
    }

    return setRepMap
}

@Composable
fun Called()
{
             Log.d("CALLED","CALLED")

}


@Preview(showSystemUi = false)
@Composable
fun WorkoutLogPreview(){
    WorkoutLog(navController = rememberNavController(), viewModelSave = viewModel())
}