package homes.inside

import android.util.Log
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
import androidx.compose.ui.graphics.Color
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.chargemap.compose.numberpicker.NumberPicker
import com.example.projectfitness.R
import kotlinx.coroutines.launch
import navigation.Screens
import viewmodel.ViewModelSave

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutSettingScreen(navController: NavController,viewModelSave: ViewModelSave){
    val context = LocalContext.current


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



    //val data = remember { mutableStateOf(  selectedworkoutList.exercises ) }

    val sheetState = rememberModalBottomSheetState()
    val sheetState2 = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showBottomSheet2 by remember { mutableStateOf(false) }

    var selectedWorkoutList = viewModelSave.selectedListWorkouts
    Log.d(
        "SLT INDX2",
        "$selectedWorkoutList"
    )

    //Tasarım Kodları */*********************************************
    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = Color(0xFF181F26)))
    {

            Box(modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(60.dp)
                .background(Color(0xFFF1C40F)),
            )
            {

                Row(Modifier.align(Alignment.CenterStart)) {

                    IconButton(
                        onClick = { navController.navigate("home") }, modifier = Modifier

                    ) {
                        Icon(
                            painterResource(id = R.drawable.projectfitnessprevious),
                            contentDescription = null,
                            modifier = Modifier
                                .width(25.dp)
                                .height(25.dp),
                            tint = Color(0xFF21282F)
                        )

                    }

                    Text(
                        text = "Workout Overview",
                        color = Color(0xFF21282F),
                        fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                        style = TextStyle(fontSize = 30.sp),
                        modifier = Modifier
                            .padding(start = 10.dp, top = 5.dp)
                    )

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
                            tint = Color(0xFF21282F)
                        )

                    }

                }

                if (showBottomSheet) {
                    ModalBottomSheet(onDismissRequest = { showBottomSheet = false }, sheetState = sheetState, containerColor = Color(0xFF283747)) {
                        LaunchedEffect(Unit) {
                            scope.launch { sheetState.expand() }.invokeOnCompletion { if (!sheetState.isVisible) {showBottomSheet = false} }
                        }
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp) )
                        {
                            Column(modifier = Modifier.align(Alignment.BottomCenter)) {

                                Button(onClick = { /*TODO*/ }, modifier = Modifier

                                    .padding(bottom = 25.dp)
                                    .fillMaxWidth()
                                    .height(60.dp),
                                    shape = RoundedCornerShape(0.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1C40F))
                                ) {
                                    Text(text = "Profile Settings",
                                        style = TextStyle(fontSize = 30.sp , fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold))),
                                        color = Color(0xFF181F26))
                                }

                                Button(onClick = { navController.navigate(Screens.LoginScreen.route) }, modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(bottom = 25.dp)
                                    .fillMaxWidth()
                                    .height(60.dp),
                                    shape = RoundedCornerShape(0.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1C40F))
                                ) {
                                    Text(text = "Logout",
                                        style = TextStyle(fontSize = 30.sp , fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold))),
                                        color = Color(0xFF181F26))
                                }

                            }
                        }
                    }
                }
            }
            Box(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 100.dp))
            {
                Column(Modifier.fillMaxWidth()) {
                    if (selectedWorkoutList != null) {
                        Text(
                            text = selectedWorkoutList.name,
                            style = TextStyle(
                                fontSize = 30.sp,
                                fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold))
                            ),
                            color = Color.White,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 10.dp, bottom = 20.dp, start = 20.dp, end = 20.dp),
                            state = LazyListState()
                        )
                        {
                            itemsIndexed(selectedWorkoutList.exercises) { index, item ->

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .background(
                                            Color(0xFF21282F), shape = RoundedCornerShape(15.dp)
                                        )
                                        .clickable { showBottomSheet2 = true }
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
                                        fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                        fontSize = 20.sp,
                                        modifier = Modifier
                                            .align(Alignment.CenterEnd)
                                            .padding(end = 35.dp),
                                        color = Color(0xFFD9D9D9)
                                    )


                                    if (showBottomSheet2) {
                                        ModalBottomSheet(onDismissRequest = {
                                            showBottomSheet = false
                                        }, sheetState = sheetState2)
                                        {
                                            LaunchedEffect(Unit) {
                                                scope.launch { sheetState.expand() }
                                                    .invokeOnCompletion {
                                                        if (!sheetState.isVisible) {
                                                            showBottomSheet = false
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
                                                        color = Color(0xFF21282F),
                                                        style = TextStyle(fontSize = 20.sp)
                                                    )
                                                    NumberPicker(
                                                        value = selectedWorkoutList?.exercises?.get(index)?.sets ?: 0,
                                                        onValueChange = {
                                                            selectedWorkoutList.exercises[index].sets = it

                                                        },
                                                        range = 0..20,
                                                        dividersColor = Color(0xFFF1C40F)
                                                    )

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
                                                        color = Color(0xFF21282F),
                                                        style = TextStyle(fontSize = 20.sp)
                                                    )
                                                    NumberPicker(
                                                        value = selectedWorkoutList.exercises[index].reps,
                                                        onValueChange = {
                                                            selectedWorkoutList.exercises[index].reps = it

                                                        },
                                                        range = 0..20,
                                                        dividersColor = Color(0xFFF1C40F)
                                                    )

                                                }

                                                Button(
                                                    onClick = { showBottomSheet2 = false },
                                                    modifier = Modifier
                                                        .align(Alignment.CenterEnd)
                                                        .width(70.dp)
                                                        .height(40.dp)
                                                        .padding(end = 10.dp,),
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
                }

            }
        Row(modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 20.dp)) {
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
    }
}


@Preview
@Composable
fun WorkoutSettingScreenPreview(){
    WorkoutSettingScreen(navController = rememberNavController(), viewModel())
}