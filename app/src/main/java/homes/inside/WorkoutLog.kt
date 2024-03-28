package homes.inside

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.example.projectfitness.R
import database.ProjectFitnessContainer
import kotlinx.coroutines.delay
import viewmodel.ViewModelSave

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun WorkoutLog(navController: NavController){

    val context = LocalContext.current
    val viewModelSave: ViewModelSave = viewModel()
    val container = ProjectFitnessContainer(context)


    val selectedListWorkouts = viewModelSave.selectedListWorkouts

    var ticked by remember { mutableStateOf(false) }
    var tickColor = if (ticked) {Color(0xFFF1C40F) } else {Color(0xFFD9D9D9)}

    var currentIndex by remember{ mutableStateOf(0) }

    /* Tasarım Kodları *******************************************************************/
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color(0xFF181F26))
            .paint(
                painter = painterResource(id = R.drawable.chestpro),
                contentScale = ContentScale.Crop,
                alpha = 0.05f
            ),
        contentAlignment = Alignment.TopCenter
    )
    {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        )
        {
            Row(Modifier.align(Alignment.CenterStart)) {

                Button(
                    onClick = { },
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 20.dp)
                        .border(2.dp, Color.White, shape = MaterialTheme.shapes.small)
                        .width(30.dp)
                        .height(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF181F26)),
                    contentPadding = PaddingValues(0.dp)
                )
                {
                    Icon(
                        painterResource(id = R.drawable.projectfitnessprevious),
                        contentDescription = null,
                        modifier = Modifier
                            .width(25.dp)
                            .height(25.dp)
                            .clickable(onClick = {
                                if (currentIndex == 0)
                                {
                                    // do nothing because out of bounds
                                }
                                else{currentIndex--}
                            }),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.weight(4f))

                if (selectedListWorkouts != null) {
                    Text(
                        text = ""+ selectedListWorkouts.exercises[currentIndex].name ,
                        color = Color(0xFFF1C40F),
                        fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                        style = TextStyle(fontSize = 30.sp),
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                    )
                }
                Spacer(modifier = Modifier.weight(4f))


                Button(
                    onClick = { },
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 20.dp)
                        .border(2.dp, Color.White, shape = MaterialTheme.shapes.small)
                        .width(30.dp)
                        .height(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF181F26)),
                    contentPadding = PaddingValues(0.dp)
                )
                {
                    Icon(
                        painterResource(id = R.drawable.projectfitnessgo),
                        contentDescription = null,
                        modifier = Modifier
                            .width(25.dp)
                            .height(25.dp)
                            .clickable(onClick = {
                                if (selectedListWorkouts != null) {
                                    if (currentIndex >=selectedListWorkouts.exercises.size-1 ) {
                                        // do nothing because out of bounds
                                    }
                                    else{currentIndex++}
                                }
                            }),
                        tint = Color.White
                    )
                }
            }
        }

        //Content

        Box(modifier = Modifier.align(Alignment.Center)) {
            LazyColumn(
                state = rememberLazyListState(),
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxHeight()
                    .padding(top = 100.dp, bottom = 100.dp)
            ) {

                if (selectedListWorkouts != null) {
                    itemsIndexed(selectedListWorkouts.exercises) { index, item ->

                        Box(
                            modifier = Modifier
                                .width(291.dp)
                                .height(29.dp)
                                .background(
                                    tickColor.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(5.dp)
                                )
                        )
                        {
                            Icon(painter = painterResource(id = R.drawable.projectfitnesstick),
                                contentDescription = null,
                                Modifier
                                    .align(Alignment.CenterEnd)
                                    .width(32.dp)
                                    .height(29.dp)
                                    .background(
                                        color = tickColor
                                            .copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(5.dp)
                                    )
                                    .clickable(onClick = {
                                        if (ticked) {
                                            ticked = false
                                        } else {
                                            ticked = true
                                        }
                                    })
                            )

                            Text(
                                text = "SET  ",
                                style = TextStyle(
                                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                    fontSize = 20.sp,
                                    letterSpacing = 2.sp
                                ),
                                modifier = Modifier.align(Alignment.Center),
                                color = Color.White
                            )

                            Icon(
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                contentDescription = null,
                                Modifier
                                    .align(Alignment.CenterStart)
                                    .width(32.dp)
                                    .height(29.dp)
                                    .background(
                                        color = Color(0xFFD9D9D9)
                                            .copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(5.dp)
                                    )
                                    .clickable(onClick = { })


                            )
                        }
                        Spacer(modifier = Modifier.size(10.dp))
                    }
                }
            }
        }
    }
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

@Preview(showSystemUi = false)
@Composable
fun WorkoutLogPreview(){
    WorkoutLog(navController = rememberNavController())
}