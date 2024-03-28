package mainpages

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.projectfitness.R
import database.ProjectFitnessContainer
import kotlinx.coroutines.launch
import navigation.NavigationBar
import navigation.Screens
import viewmodel.ProjectFitnessViewModel
import viewmodel.ViewModelSave

class Homes : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Home(navController: NavController,viewModelSave: ViewModelSave) {
        //Database Creation*************************************************************************************************************************************************************

        val context = LocalContext.current
        val scopes = rememberCoroutineScope()
        var projectFitnessContainer = ProjectFitnessContainer(context)
        val itemRepo = projectFitnessContainer.itemsRepository

        LaunchedEffect(key1 = null) {

        }

        //******************************************************************************************************************************************************************************

        // Variable Initialize *********************************************************************************************************************************************************


        var configuration = LocalConfiguration.current
        var screenwidthDp = configuration.screenWidthDp

        var list = viewModelSave.workouts()
        val viewModel : ProjectFitnessViewModel = viewModel()
        viewModel.loadDataFromFirestore()

        val flag by remember { mutableStateOf(true) }
        val flag2 by remember { mutableStateOf(false) }
        val flag3 by remember { mutableStateOf(false) }
        val flag4 by remember { mutableStateOf(false) }

        val sheetState = rememberModalBottomSheetState()
        val scope = rememberCoroutineScope()
        var showBottomSheet by remember { mutableStateOf(false) }

        var exercises = viewModelSave.exercises
        var name = viewModelSave.name   // Inside of ViewModelSave.kt file , change mutable state of name value for clear name place.
        var fl = viewModelSave.flag
        var notworkoutPressed = viewModelSave.allowed

        var selectedListWorkouts = viewModelSave.selectedListWorkouts

        viewModelSave.exercisesForWorkouts2.clear()

        // UI Coding ****************************************************************************************************************************************************************************

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF181F26))
        ) {

            Box(modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color(0xFFF1C40F)),
            )
            {

                Row(Modifier.align(Alignment.CenterStart)) {

                    Text(
                        text = "Your Workouts",
                        color = Color(0xFF21282F),
                        fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                        style = TextStyle(fontSize = 30.sp),
                        modifier = Modifier
                            .padding(start = 10.dp, top = 5.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = {  navController.navigate(Screens.ChooseExercises.route)
                            fl.value = true
                            name.value = ""
                            exercises.clear()
                            notworkoutPressed.value = false
                                  }, modifier = Modifier

                    ) {
                        Icon(
                            painterResource(id = R.drawable.projectfitnessplus),
                            contentDescription = null,
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp),
                            tint = Color(0xFF21282F)
                        )

                    }

                    IconButton(
                        onClick = { navController.navigate("profile")
                                  }, modifier = Modifier

                    ) {
                        Icon(
                            painterResource(id = R.drawable.projectfitnessprofileheavy),
                            contentDescription = null,
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp),
                            tint = Color(0xFF21282F)
                        )

                    }

                    IconButton(
                        onClick = {  }, modifier = Modifier

                    ) {
                        Icon(
                            painterResource(id = R.drawable.projectfitnesscircleheavy),
                            contentDescription = null,
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp),
                            tint = Color(0xFF21282F)
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

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(screenwidthDp.dp)
                    .fillMaxHeight()
                    .padding(top = 100.dp)


            )
            {
                Column {
                    Text(text = "All Exercises",
                        style = TextStyle(fontSize = 30.sp, fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)), letterSpacing = 4.sp ),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        color = Color.White)

                    Spacer(modifier = Modifier.size(50.dp))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 10.dp, bottom = 20.dp, start = 20.dp, end = 20.dp),
                        state = LazyListState()
                    ) {
                        itemsIndexed(list) { index, item ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(70.dp)
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color(0xFF181F26)  // End color
                                            ),
                                            start = Offset.Infinite,
                                            end = Offset(100.0f, 0.0f)
                                        )
                                    )
                                    .clickable {
                                        viewModelSave.selectedListWorkouts = list[index]
                                        Log.d(
                                            "SLT INDX",
                                            list[index].toString() + " $selectedListWorkouts"
                                        )
                                        navController.navigate("workoutsettingscreen")
                                    }

                            ) {
                                if (index == 0){
                                    Image(painterResource(id = R.drawable.secondinfo),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        alpha = 0.7f,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(shape = RoundedCornerShape(20.dp)),
                                    )
                                }else if (index == 1){
                                    Image(painterResource(id = R.drawable.login),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        alpha = 0.7f,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(shape = RoundedCornerShape(20.dp)),
                                    )
                                }

                                Row(modifier = Modifier.align(Alignment.Center)) {
                                    Text(
                                        text = "" + item.name,
                                        modifier = Modifier
                                            .padding(start = 20.dp),
                                        fontSize = 27.sp,
                                        fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                        textAlign = TextAlign.Left,
                                        color = Color(0xFFD9D9D9),
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                            }
                            Spacer(modifier = Modifier.size(10.dp))

                        }
                    }
                }
            }



            val indexs = 0
            NavigationBar(navController = navController, indexs, flag, flag2,flag3,flag4)
        }


    }

    @Preview(name = "phone", device = "spec:shape=Normal,width=360,height=720,unit=dp,dpi=402")
    @Composable
    fun PreviewHome() {
        Home(navController = rememberNavController(),viewModel())
    }
}