package activity.inside

//noinspection UsingMaterialAndMaterial3Libraries

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import androidx.compose.ui.draw.alpha
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
import database.Exercise
import database.ProjectFitnessContainer
import database.ProjectFitnessWorkoutEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import navigation.Screens
import viewmodel.ViewModelSave

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@SuppressLint("UnrememberedMutableState")
@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun ChooseExercises(navController: NavController, arg: String?, viewModel: ViewModelSave) {

    //Database Creation *************************************************************************************************************************************************************

    val context = LocalContext.current
    val scopes = rememberCoroutineScope()
    var projectFitnessContainer = ProjectFitnessContainer(context)
    val itemRepos = projectFitnessContainer.itemsRepository

    LaunchedEffect(key1 = null) {
        CoroutineScope(Dispatchers.IO).launch {
            projectFitnessContainer = ProjectFitnessContainer(context)
            var itemRepo = itemRepos
        }
    }

    //******************************************************************************************************************************************************************************

    // Variable Initialize *********************************************************************************************************************************************************

    val sheetState = rememberModalBottomSheetState()
    val sheetStateState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showBottomSheetSetting by remember { mutableStateOf(false) }
    var showBottomSheetSettingSetting by remember { mutableStateOf(false) }
    var allowed = viewModel.allowed
    var list =
        viewModel.exercises // Create list and init with ViewModelSave for preserving any class
    var flag = viewModel.flag
    var text = viewModel.name
    var exercisesForWorkouts2 =
        viewModel.exercisesForWorkouts2 // ViewModelSave classından örneği oluşturulmuş mutablestatelistof<Exercise> liste nesnesi

    var newExercises = Exercise("", 0, 0)
    var arg2 by remember { mutableStateOf("") }

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

    val marginLazyColumnTopDp = if (useDiffrentValue1) {
        70.dp
    } // 750-800 dp
    else if (useDiffrentValue2) {
        60.dp
    } // 800-900 dp
    else if (useDiffrentValue3) {
        70.dp
    } // >= 900 dp
    else {
        70.dp
    } // <= 750 dp

    val marginWidthDp = if (useDiffrentValue4) {
        500.dp
    } else if (useDiffrentValue5) {
        10.dp
    } else if (useDiffrentValue6) {
        10.dp
    } else {
        200.dp
    }


    var idFlags = viewModel.idFlag


    //**************************************************************************************************************************************************************************************

    // UI Coding ****************************************************************************************************************************************************************************

    if (arg?.length == 6) {
        Log.d("arg1", arg ?: "arg is null")
    } else {
        if (arg != null) {
            arg2 = arg
        }
        if (arg2 !in list && flag.value) {
            list.add(arg2)                                  // Workout Details classından gelen seçilmiş egzersizin stringini list ' e ekle
            newExercises = Exercise(
                arg2,
                0,
                0
            ) // Exercise sınıfından örnek oluşturup içine egzersiz ismi , tekrar ve setler ekleniyor
            exercisesForWorkouts2.add(newExercises)        // ViewModelSave sınıfından oluşturulmuş mutablestatelistof<Exercise> nesnesine bir önceki Exercise nesnesi ekleniyor
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF181F26))
    ) {
        Log.d("boolvalue", showBottomSheetSettingSetting.toString())
        Box(
            modifier = Modifier
                .fillMaxWidth()
        )
        {

        }
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(screenwidthDp.dp)
                .height(700.dp)
                .padding(top = screenheightDp.dp / 3.5f)
        )
        {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 0.dp)
            ) {
                Text(
                    text = "Workout",
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                        fontSize = 30.sp,
                        letterSpacing = 3.sp
                    ),
                    color = Color.White
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.TopCenter)
                    .padding(
                        top = marginLazyColumnTopDp,
                        bottom = 20.dp,
                        start = 20.dp,
                        end = 20.dp
                    ),
                state = LazyListState()
            )
            {
                itemsIndexed(list) { index, item ->

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(Color(0xFF21282F), shape = RoundedCornerShape(15.dp))
                    ) {
                        Text(
                            text = "" + item.toString(),
                            modifier = Modifier
                                .align(
                                    Alignment.Center
                                ),
                            fontSize = 20.sp,
                            fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                            color = Color(0xFFD9D9D9),
                            style = TextStyle(letterSpacing = 1.sp)
                        )

                    }
                    Spacer(modifier = Modifier.size(20.dp))


                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
            ) {
                Row {
                    Button(
                        onClick = { navController.navigate(route = "createworkout") },
                        modifier = Modifier
                            .padding(top = screenheightDp.dp / 3.5f, start = screenwidthDp.dp / 20)
                            .width(220.dp)
                            .height(35.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "ADD EXERCISES",
                            fontFamily = FontFamily(Font(R.font.postnobillscolombo)),
                            fontWeight = FontWeight.Bold,
                            style = TextStyle(letterSpacing = 1.sp, fontSize = 20.sp),
                            color = Color(0xFF21282F)
                        )
                    }
                    Button(
                        onClick = {
                            if (list.isEmpty() || text.value.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    "Add Exercise and Set Name of Workout",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                allowed.value = true
                                Log.d(
                                    "exerciseForWorkouts2",
                                    exercisesForWorkouts2.toList().toString()
                                )
                                scopes.launch {
                                    try {
                                        itemRepos.updateItem(
                                            ProjectFitnessWorkoutEntity(
                                                workoutId = viewModel.idFlag.value,
                                                workoutName = text.value,
                                                exercises = exercisesForWorkouts2
                                            )
                                        )
                                        Log.d("UpdateItem", "Update successful")
                                    } catch (e: Exception) {
                                        Log.e("UpdateItem", "Update failed", e)
                                    }
                                }
                                viewModel.idFlag.value++

                                Log.d(
                                    "IDFLAG",
                                    idFlags.value.toString() + " " + text.value + " " + exercisesForWorkouts2.toList()
                                        .toString()
                                )
                                navController.navigate(route = "home")
                            }
                        },
                        modifier = Modifier
                            .padding(top = screenheightDp.dp / 3.4f, start = marginWidthDp / 20)
                            .width(100.dp)
                            .height(25.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1C40F)),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Text(
                            text = "CREATE",
                            fontFamily = FontFamily(Font(R.font.postnobillscolombo)),
                            fontWeight = FontWeight.Bold,
                            style = TextStyle(letterSpacing = 1.sp, fontSize = 10.sp),
                            color = Color(0xFF21282F)
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color.Transparent)
        )
        {

            Row(Modifier.align(Alignment.CenterStart)) {

                Icon(
                    painter = painterResource(id = R.drawable.projectfitnessprevious),
                    contentDescription = null,
                    modifier = Modifier
                        .clickable(onClick = { navController.navigate("home") })
                        .padding(top = 10.dp)
                        .size(30.dp),
                    tint = Color(0xFFF1C40F)
                )

                Text(
                    text = "Create Workout",
                    color = Color(0xFFF1C40F),
                    fontFamily = FontFamily(Font(R.font.postnobillscolomboregular)),
                    style = TextStyle(fontSize = 30.sp),
                    modifier = Modifier
                        .padding(start = 10.dp, top = 5.dp)
                )

                Spacer(modifier = Modifier.weight(1f))


                IconButton(
                    onClick = { showBottomSheetSetting = true }, modifier = Modifier

                ) {
                    Icon(
                        painterResource(id = R.drawable.projectfitnesssetting),
                        contentDescription = null,
                        modifier = Modifier
                            .width(25.dp)
                            .height(25.dp),
                        tint = Color(0xFFF1C40F)
                    )

                }

                IconButton(
                    onClick = { showBottomSheet = true }, modifier = Modifier

                ) {
                    Icon(
                        painterResource(id = R.drawable.projectfitnesspointheavy),
                        contentDescription = null,
                        modifier = Modifier
                            .width(25.dp)
                            .height(25.dp),
                        tint = Color(0xFFF1C40F)
                    )

                }

            }

            if (showBottomSheetSetting) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheetSetting = false },
                    sheetState = sheetState,
                    containerColor = Color(0xFF283747)
                ) {
                    LaunchedEffect(Unit) {
                        scope.launch { sheetState.expand() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheetSetting = false
                            }
                        }
                    }
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(77.dp) )
                    {
                        Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                            Button(onClick = { navController.navigate(Screens.LoginScreen.route) }, modifier = Modifier
                                .align(Alignment.End)
                                .padding(bottom = 25.dp)
                                .fillMaxWidth()
                                .height(60.dp),
                                shape = RoundedCornerShape(0.dp),
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                            ) {
                                Text(text = "Logout",
                                    style = TextStyle(fontSize = 25.sp , fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold))),
                                    color = Color(0xFFF1C40F))
                            }

                        }
                    }
                }
            }

            if (showBottomSheetSettingSetting) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheetSettingSetting = false },
                    sheetState = sheetStateState,
                    containerColor = Color(0xFF283747)
                ) {
                    LaunchedEffect(Unit) {
                        scope.launch { sheetStateState.expand() }.invokeOnCompletion {
                            if (!sheetStateState.isVisible) {
                                showBottomSheetSettingSetting = false
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                    )
                    {
                        Column(modifier = Modifier.align(Alignment.BottomCenter)) {
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
                                        value = 0,
                                        onValueChange = { 0 },
                                        range = 0..500,
                                        dividersColor = Color(0xFFF1C40F),
                                        textStyle = TextStyle(color = Color.White),

                                        )
                                    Spacer(modifier = Modifier.size(130.dp))
                                    NumberPicker(
                                        value = 0,
                                        onValueChange = {
                                        },
                                        range = 0..50,
                                        dividersColor = Color(0xFFF1C40F),
                                        textStyle = TextStyle(color = Color.White),

                                        )
                                }


                                Row(modifier = Modifier.align(Alignment.BottomEnd)) {
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
                                            .clickable {}
                                            .width(50.dp)
                                            .height(25.dp)
                                            .background(
                                                shape = RoundedCornerShape(10.dp),
                                                color = Color(0xFF181F26)
                                            )

                                    )
                                }
                            }
                        }
                    }
                }
            }


        }

        Text(
            text = "Enter Workout Name",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = screenheightDp.dp / 6.5f),
            color = Color(0xFFD9D9D9),
            fontSize = 20.sp,
            fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold))
        )
        BasicTextField(
            value = text.value,
            onValueChange = {
                if (!allowed.value) {
                    text.value = it
                }
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = screenheightDp.dp / 5)
                .height(40.dp)
                .width(270.dp)
                .background(Color.Transparent, shape = RoundedCornerShape(10.dp)),
            textStyle = TextStyle(
                fontSize = 12.sp,
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


        if (list.isEmpty()) {
            Text(
                text = "Add Exercises",
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = screenheightDp.dp / 2.2f)
                    .alpha(0.6f),
                fontSize = 20.sp, color = Color(0xFFD9D9D9),
                fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                style = TextStyle(letterSpacing = 1.sp)
            )
        }
    }


}


@RequiresApi(Build.VERSION_CODES.R)
@Preview(showSystemUi = true)
@Composable
fun PreviewChooseExercises() {
    ChooseExercises(navController = rememberNavController(), arg = "", viewModel = viewModel())
}