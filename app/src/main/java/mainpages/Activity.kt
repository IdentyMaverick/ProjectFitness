package mainpages

import android.content.res.Resources
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.projectfitness.R
import kotlinx.coroutines.launch
import navigation.NavigationBar
import navigation.Screens
import viewmodel.ViewModelSave


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Activity(navController: NavController) {
    val context = LocalContext.current
    val viewModelSave: ViewModelSave = viewModel()
    //val container = ProjectFitnessContainer(context)
    //val viewModelSaves = ViewModelSave(container)

    val screen1 = 800
    val screen2 = 900
    val screen3 = 400
    val screen4 = 450
    val configuration = LocalConfiguration.current
    val screenheightDp = configuration.screenHeightDp
    val screenwidthDp = configuration.screenWidthDp

    val useDiffrentValue1 = screenheightDp in screen1..screen2
    val useDiffrentValue2 = screenheightDp >= screen2

    val useDiffrentValue3 = screenwidthDp in screen3..screen4
    val useDiffrentValue4 = screenwidthDp >= screen4


    val marginTopDpPJWorkoutsText = if (useDiffrentValue1)
    { 140.dp }
    else if (useDiffrentValue2)
    { 140.dp }
    else
    { 120.dp }

    val marginStartDpPJWorkoutsText = if (useDiffrentValue3)
    { 40.dp }
    else if (useDiffrentValue4)
    { 60.dp }
    else
    { 20.dp }

    val marginTopDpCanvas = if (useDiffrentValue1)
    { 20.dp }
    else if (useDiffrentValue2)
    { 30.dp }
    else
    { 95.dp }

    val fontSizePFWorkout = if (useDiffrentValue1)
    { 25.sp }
    else if (useDiffrentValue2)
    { 25.sp }
    else
    { 20.sp }


    val marginTopDp = 16f
    val density = Resources.getSystem().displayMetrics.density
    val marginPx = (marginTopDp*density).toInt()
    val screenWidthPx = Resources.getSystem().displayMetrics.widthPixels

    var expanded2 by remember { mutableStateOf(false) }
    val selectedOptionsText2 by remember { mutableStateOf("Chest") }

        var exercises = viewModelSave.exercises
        var name = viewModelSave.name // Inside of ViewModelSave.kt file , change mutable state of name value for clear name place.
        var fl = viewModelSave.flag // Inside of ViewModelSave.kt file , change mutable state of bool value for clear list.
        var expanded by remember { mutableStateOf(false) }
        val options = listOf("Create Workout")
        val selectedOptionsText by remember { mutableStateOf(options[0]) }
        var notworkoutPressed = viewModelSave.allowed

        var flag by remember { mutableStateOf(true) }
        var flag2 by remember { mutableStateOf(true) }
        var flag3 by remember { mutableStateOf(true) }
        var flag4 by remember { mutableStateOf(true) }

        var flagg by remember { mutableStateOf(false) }
        var flagg2 by remember { mutableStateOf(true) }
        var flagg3 by remember { mutableStateOf(false) }
        var flagg4 by remember { mutableStateOf(false) }

        val sheetState = rememberModalBottomSheetState()
        val scope = rememberCoroutineScope()
        var showBottomSheet by remember { mutableStateOf(false) }


    // TASARIM KODLARI //************************************************************************

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF181F26))
        ) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color(0xFF181F26)),
            )
            {

                Row(Modifier.align(Alignment.CenterStart)) {

                    Text(
                        text = "PF Workout",
                        color = Color(0xFFF1C40F),
                        fontFamily = FontFamily(Font(R.font.postnobillscolomboregular)),
                        style = TextStyle(fontSize = 30.sp),
                        modifier = Modifier
                            .padding(start = 10.dp, top = 5.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = { navController.navigate("profile") }, modifier = Modifier

                    ) {
                        Icon(
                            painterResource(id = R.drawable.projectfitnessprofileheavy),
                            contentDescription = null,
                            modifier = Modifier
                                .width(25.dp)
                                .height(25.dp),
                            tint = Color(0xFFF1C40F)
                        )

                    }

                    IconButton(
                        onClick = {  }, modifier = Modifier

                    ) {
                        Icon(
                            painterResource(id = R.drawable.projectfitnesscircleheavy),
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

                if (showBottomSheet) {
                    ModalBottomSheet(onDismissRequest = { showBottomSheet = false }, sheetState = sheetState, containerColor = Color(0xFF283747)) {
                        LaunchedEffect(Unit) {
                            scope.launch { sheetState.expand() }.invokeOnCompletion { if (!sheetState.isVisible) {showBottomSheet = false} }
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

            }
        }

            val itemIndexedList = listOf("A", "B", "C", "D")
            val state = rememberLazyListState()
            LazyRow(
                modifier = Modifier
                    .padding(top = 80.dp, start = 20.dp, end = 15.dp)
                ,
                state = state
            ) {
                itemsIndexed(itemIndexedList) { index, item ->
                    Spacer(modifier = Modifier.size(10.dp))

                    var color = if (flag) {
                        Color(0xFFF1C40F)
                    } else {
                        Color(0xFF181F26)
                    }
                    var color2 = if (flag2) {
                        Color(0xFF181F26)
                    } else Color(0xFFF1C40F)

                    var color3 = if (flag3) {
                        Color(0xFF181F26)
                    } else {
                        Color(0xFFF1C40F)
                    }

                    var color4 = if (flag4) {
                        Color(0xFF181F26)
                    } else {
                        Color(0xFFF1C40F)
                    }


                    if (index == 0) {
                        Button(
                            onClick = {
                                if (flag) {
                                } else {
                                    flag = true
                                    flag2 = true
                                    flag3 = true
                                    flag4 = true
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .width(70.dp)
                                .height(30.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = color),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(text = "All", textAlign = TextAlign.Center, style = TextStyle(fontSize = 15.sp,letterSpacing = 3.sp), fontFamily = FontFamily(Font(
                                R.font.postnobillscolombosemibold
                            )))
                        }
                        Spacer(modifier = Modifier.size(10.dp))
                    } else if (index == 1) {
                        Button(
                            onClick = {
                                if (flag2) {
                                    flag2 = false
                                    flag = false
                                    flag3 = true
                                    flag4 = true
                                } else {

                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .width(70.dp)
                                .height(30.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = color2),
                            contentPadding = PaddingValues(0.dp)

                        ) {
                            Text(text = "UpperBody", style = TextStyle(fontSize = 15.sp,letterSpacing = 1.sp), textAlign = TextAlign.Center,fontFamily = FontFamily(Font(
                                R.font.postnobillscolombosemibold
                            )))
                        }
                        Spacer(modifier = Modifier.size(10.dp))
                    } else if (index == 2) {
                        Button(
                            onClick = {
                                if (flag3) {
                                    flag3 = false
                                    flag = false
                                    flag2 = true
                                    flag4 = true
                                } else {

                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .width(70.dp)
                                .height(30.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = color3),
                            contentPadding = PaddingValues(0.dp)

                        ) {
                            Text(text = "LowerBody", style = TextStyle(fontSize = 15.sp,letterSpacing = 1.sp), textAlign = TextAlign.Center,fontFamily = FontFamily(Font(
                                R.font.postnobillscolombosemibold
                            )))
                        }
                        Spacer(modifier = Modifier.size(10.dp))
                    } else if (index == 3) {
                        Button(
                            onClick = {
                                if (flag4) {
                                    flag4 = false
                                    flag = false
                                    flag2 = true
                                    flag3 = true
                                } else {

                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .width(70.dp)
                                .height(30.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = color4),
                            contentPadding = PaddingValues(0.dp)

                        ) {
                            Text(text = "Filter", style = TextStyle(fontSize = 15.sp,letterSpacing = 1.sp), textAlign = TextAlign.Center,fontFamily = FontFamily(Font(
                                R.font.postnobillscolombosemibold
                            )))
                        }
                        Spacer(modifier = Modifier.size(10.dp))
                    }
                }
            }

            val indexs = 1
            NavigationBar(navController = navController, indexs, flagg, flagg2,flagg3,flagg4)
        }



@Preview(name = "phone", device = "spec:shape=Normal,width=360,height=720,unit=dp,dpi=402")
@Composable
fun PreviewActivity() {
    Activity(navController = rememberNavController())
}