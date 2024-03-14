package activity.inside

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import viewmodel.ProjectFitnessViewModel
import com.example.projectfitness.R
import viewmodel.ViewModelSave
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState")
@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun ChooseExercises(navController: NavController, arg: String?, viewModel: ViewModelSave) {

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

    val marginTopDp = if (useDiffrentValue1)
    { 20.dp }
    else if (useDiffrentValue2)
    { 40.dp }
    else if (useDiffrentValue3)
    { 60.dp }
    else
    { 30.dp }

    val marginLazyColumnTopDp = if (useDiffrentValue1)
    { 50.dp } // 750-800 dp
    else if (useDiffrentValue2)
    { 40.dp } // 800-900 dp
    else if (useDiffrentValue3)
    { 60.dp } // >= 900 dp
    else
    { 60.dp } // <= 750 dp

    val marginWidthDp = if (useDiffrentValue4)
    { 500.dp }
    else if (useDiffrentValue5)
    { 10.dp }
    else if (useDiffrentValue6)
    { 10.dp }
    else
    { 200.dp }

    // Set and Reps input placeholder settings
    var reps = viewModel.reps
    var set = viewModel.set
    var count = viewModel.count
    var workout = viewModel.workouts()
    var allowed = viewModel.allowed
    Log.d("NotPressed", "CreateButtonPressed value is ${allowed.value}")

    val viewModels: ProjectFitnessViewModel = viewModel()
    val firestoreItems = viewModels.firestoreItems.value
    var context = LocalContext.current

    var list =
        viewModel.exercises // Create list and init with ViewModelSave for preserving any class
    Log.d("list1", "list is ${list.toList()} and list size is: ${list.size}")
    var flag = viewModel.flag
    Log.d("flag ", "flag is ${flag.value}")
    var text = viewModel.name

    if (arg?.length == 6) {
        Log.d("arg1", arg ?: "arg is null")
    } else {
        var arg2 = arg
        if (arg2 !in list && flag.value) {
            list.add(arg2)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF181F26))
    ) {
        Icon(
            painter = painterResource(id = R.drawable.left),
            contentDescription = null,
            modifier = Modifier
                .clickable(onClick = { navController.navigate("activity") })
                .size(30.dp)
                .padding(top = 5.dp),
            tint = Color(0xFFD9D9D9)
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(screenwidthDp.dp)
                .height(700.dp)
                .padding(top = screenheightDp.dp/3)
                .background(
                    Color(0xFF181F26)
                )
        )
        {
            Row (horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(top = marginTopDp)) {
                Text(text = "Overview", fontSize = 15.sp, fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)), style = TextStyle(letterSpacing = 3.sp), color = Color.White, modifier = Modifier.padding(start = 30.dp))
                Text(text = "Set x Reps", fontSize = 10.sp, fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),style = TextStyle(letterSpacing = 3.sp),color = Color.White,modifier = Modifier.padding(end = 30.dp))
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.TopCenter)
                    .padding(top = marginLazyColumnTopDp, bottom = 20.dp, start = 20.dp, end = 20.dp),
                state = LazyListState()
            )
            {
                itemsIndexed(list) { index, item ->
                    val dismissState = rememberDismissState(confirmValueChange = {
                        if (it == DismissValue.DismissedToStart) {
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(500) // Adjust the delay duration as needed
                                list.remove(item)
                                flag.value = false
                                set[index] = "0"
                                reps[index] = "0"
                            }
                        }
                        true
                    })
                    if (list.size == 0) {
                        Log.d("loooool", "empty")
                    } else {
                        SwipeToDismiss(state = dismissState, background = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Color.Red,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.terminate),
                                    contentDescription = "Terminate",
                                    tint = Color(0xFF212D39),
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .padding(16.dp)
                                )
                            }
                        }, dismissContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .background(
                                        Color(0xFF21282F),
                                        shape = RoundedCornerShape(15.dp)
                                    )

                            ) {
                                Text(
                                    text = "" + item.toString(),
                                    modifier = Modifier
                                        .align(
                                            Alignment.CenterStart
                                        )
                                        .padding(start = 15.dp),
                                    fontSize = 15.sp,
                                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                    color = Color(0xFFD9D9D9),
                                    style = TextStyle(letterSpacing = 1.sp)
                                )
                                /*Canvas(modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 10.dp)) {
                                    drawLine(color = Color(0xFFD9D9D9), start = Offset(60f, 10f), end = Offset(60f, 110f), strokeWidth = 4f)
                                }*/
                                BasicTextField(
                                    value = set[index],
                                    onValueChange = { set[index] = it.filter { it.isDigit() } },
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .padding(end = 110.dp)
                                        .height(30.dp)
                                        .width(37.dp)
                                        .background(
                                            Color(0xFF2C3E50),
                                            shape = RoundedCornerShape(10.dp)
                                        ),
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Number
                                    ),
                                    maxLines = 1,
                                    textStyle = TextStyle(
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily(Font(R.font.poppinslighttext)),
                                        color = Color(0xFFD9D9D9)
                                    ),
                                    decorationBox = { innerTextField ->
                                        Row(
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                                .padding(horizontal = 10.dp)
                                                .fillMaxWidth()
                                                .background(
                                                    color = Color(0xFF2C3E50),
                                                    shape = RoundedCornerShape(10.dp)
                                                )
                                                .border(
                                                    width = 1.dp,
                                                    color = Color(0xFF2C3E50),
                                                    shape = RoundedCornerShape(10.dp)
                                                ),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            innerTextField()
                                        }
                                    },

                                    )
                                Text(
                                    text = "Set",
                                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                    fontSize = 15.sp,
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .padding(end = 85.dp),
                                    color = Color(0xFFD9D9D9)
                                )
                                BasicTextField(
                                    value = reps[index],
                                    onValueChange = { reps[index] = it.filter { it.isDigit() } },
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .padding(end = 45.dp)
                                        .height(30.dp)
                                        .width(37.dp)
                                        .background(
                                            Color(0xFF2C3E50),
                                            shape = RoundedCornerShape(10.dp)
                                        ),
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Number
                                    ),
                                    maxLines = 1,
                                    textStyle = TextStyle(
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily(Font(R.font.poppinslighttext)),
                                        color = Color(0xFFD9D9D9)
                                    ),
                                    decorationBox = { innerTextField ->
                                        Row(
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                                .padding(horizontal = 10.dp)
                                                .fillMaxWidth()
                                                .background(
                                                    color = Color(0xFF2C3E50),
                                                    shape = RoundedCornerShape(10.dp)
                                                )
                                                .border(
                                                    width = 1.dp,
                                                    color = Color(0xFF2C3E50),
                                                    shape = RoundedCornerShape(10.dp)
                                                ),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            innerTextField()
                                        }
                                    },

                                    )
                                Text(
                                    text = "Rep",
                                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                    fontSize = 15.sp,
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .padding(end = 15.dp),
                                    color = Color(0xFFD9D9D9)
                                )
                            }
                        }, directions = setOf(DismissDirection.EndToStart))


                        Spacer(modifier = Modifier.size(10.dp))
                        if (item.toString() == list.last().toString()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                                    .align(Alignment.TopCenter)

                            ) {}

                        }
                    }
                }
            }
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(screenheightDp.dp / 10)
            .paint(
                painter = painterResource(id = R.drawable.cablecrossover),
                contentScale = ContentScale.Crop,
                alpha = 0.3f
            ))
        {
            Text(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 5.dp),
                text = "PROJECT FITNESS",
                fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                color = Color(0xFFF1C40F),
                style = TextStyle(fontSize = 20.sp, letterSpacing = 10.sp)
            )

        }
        Text(
            text = "CREATE WORKOUT",
            fontSize = 30.sp,
            color = Color(0xFFD9D9D9),
            modifier = Modifier
                .align(
                    Alignment.TopCenter
                )
                .padding(top = screenheightDp.dp / 10),
            fontFamily = FontFamily(Font(R.font.postnobillscolombo)),
            textAlign = TextAlign.Center,
            style = TextStyle(letterSpacing = 3.sp),
            fontWeight = FontWeight.Bold
        )

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
        Row {
            Button(onClick = {navController.navigate(route = "createworkout")},
                modifier = Modifier
                    .padding(top = screenheightDp.dp / 3.5f, start = screenwidthDp.dp / 20)
                    .width(220.dp)
                    .height(35.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "ADD EXERCISES",
                    fontFamily = FontFamily(Font(R.font.postnobillscolombo)),
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(letterSpacing = 1.sp, fontSize = 20.sp),
                    color = Color(0xFF21282F))
            }
            Button(onClick = {
                if (list.isEmpty() || text.value.isEmpty()) {
                    Toast.makeText(
                        context,
                        "Add Exercise and Set Name of Workout",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    navController.navigate(route = "home")
                    allowed.value = true
                }
            },
                modifier = Modifier
                    .padding(top = screenheightDp.dp / 3.4f, start = marginWidthDp/ 20)
                    .width(100.dp)
                    .height(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1C40F)),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text(text = "CREATE",
                    fontFamily = FontFamily(Font(R.font.postnobillscolombo)),
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(letterSpacing = 1.sp, fontSize = 10.sp),
                    color = Color(0xFF21282F))
            }
        }


        if (list.isEmpty()) {
            Text(
                text = "Add Exercises",
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = screenheightDp.dp / 2.2f)
                    .clickable { },
                fontSize = 30.sp, color = Color(0xFFD9D9D9),
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