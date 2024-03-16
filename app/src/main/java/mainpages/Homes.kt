package mainpages

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.projectfitness.R
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
    @Composable
    fun Home(navController: NavController,viewModelSave: ViewModelSave) {

        val screen1 = 800
        val screen2 = 900
        var configuration = LocalConfiguration.current
        var screenheightDp = configuration.screenHeightDp
        var screenwidthDp = configuration.screenWidthDp

        val useDiffrentValue1 = screenheightDp in screen1..screen2
        val useDiffrentValue2 = screenheightDp >= screen2

        val marginTopDp = if (useDiffrentValue1)
        { 150.dp }
        else if (useDiffrentValue2)
        { 200.dp }
        else
        {
            100.dp
        }

        var selectedWorkoutName = viewModelSave.selectedWorkoutName
        var backgroundImage : Painter = painterResource(id = R.drawable.secondinfo)

        val marginDp = 16f

        val density = Resources.getSystem().displayMetrics.density
        val marginPx = (marginDp*density).toInt()
        val screenWidthPx = Resources.getSystem().displayMetrics.widthPixels
        val screenHeightPx = Resources.getSystem().displayMetrics.heightPixels


        var list = viewModelSave.workouts()
        var selectedIndexWorkoutinList = viewModelSave.selectedIndexWorkoutinList
        val viewModel : ProjectFitnessViewModel = viewModel()
        val firestoreData = remember {viewModel.firestoreData}
        viewModel.loadDataFromFirestore()


        var expanded by remember { mutableStateOf(false) }
        val flag by remember { mutableStateOf(true) }
        val flag2 by remember { mutableStateOf(false) }
        val flag3 by remember { mutableStateOf(false) }
        val flag4 by remember { mutableStateOf(false) }

        Log.d("List","List size is ${list.size}")



        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF181F26))
        ) {

            Text(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 5.dp),
                text = "PROJECT FITNESS",
                fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                color = Color(0xFFF1C40F),
                fontSize = 20.sp,
                style = TextStyle(fontSize = 20.sp,letterSpacing = 10.sp)
            )
            Canvas(
                modifier = Modifier
                    .size(100.dp)
                    .clickable(interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = { navController.navigate(Screens.Home.Profile.route) })
            )
            {
                drawCircle(color = Color.Black, radius = 70f, center = Offset(170f, 200f))
            }

            Text(
                text = "Welcome " + firestoreData.value ,
                color = Color(0xFFD9D9D9),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = screenheightDp.dp / 12, end = screenwidthDp.dp / 17),
                fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                style = TextStyle(fontSize = 15.sp,letterSpacing = 1.sp)
            )


            Box(modifier = Modifier.align(Alignment.TopEnd)) {
                IconButton(
                    onClick = { expanded = true }, modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 55.dp)
                ) {
                    Icon(
                        painterResource(id = R.drawable.point),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp).padding(end = 10.dp),
                        tint = Color(0xFFD9D9D9)
                    )

                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = !expanded },
                    modifier = Modifier
                ) {
                    DropdownMenuItem(
                        text = { Text(text = "Profile Settings") },
                        onClick = { navController.navigate(route = "profile") })
                    DropdownMenuItem(
                        text = { Text(text = "Logout") },
                        onClick = { navController.navigate(Screens.LoginScreen.route) })
                }
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 380.dp)
            ) {
                Text(
                    text = "Your Workouts",
                    color = Color(0xFFD9D9D9),
                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                    style = TextStyle(fontSize = 25.sp,letterSpacing = 1.sp)
                )
                Spacer(modifier = Modifier.size(120.dp))
                Text(
                    text = "See all",
                    color = Color(0xFFD9D9D9),
                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                    style = TextStyle(fontSize = 15.sp,letterSpacing = 1.sp)
                )
            }
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = marginTopDp)
            ) {
                drawLine(
                    color = Color.White,
                    start = Offset(marginPx.toFloat(), 250f),
                    end = Offset((screenWidthPx-marginPx).toFloat(), 250f)
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .width(screenwidthDp.dp)
                    .height(screenheightDp.dp / 1.5f)
                    .background(
                        Color(0xFF181F26),
                        shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
                    )

            )
            {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
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
                                        end = Offset(100.0f, 0.0f))
                                )
                                .clickable { selectedIndexWorkoutinList.value = index.toString()
                                             viewModelSave.updateSelectedWorkoutName(item.name)
                                             viewModelSave.updateSelectedWorkoutIndex(index.toString())
                                             navController.navigate("workoutsettingscreen")}

                        ) {
                            if (index == 0){
                                Image(painterResource(id = R.drawable.secondinfo),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    alpha = 0.7f,
                                    modifier = Modifier.fillMaxSize()
                                        .clip(shape = RoundedCornerShape(20.dp)),
                                )
                            }else if (index == 1){
                                Image(painterResource(id = R.drawable.login),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    alpha = 0.7f,
                                    modifier = Modifier.fillMaxSize()
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



            val indexs = 0
            NavigationBar(navController = navController, indexs, flag, flag2,flag3,flag4)
        }


    }

    @Preview(name = "phone", device = "spec:shape=Normal,width=360,height=720,unit=dp,dpi=402")
    @Composable
    fun PreviewHome() {
        Home(navController = rememberNavController(), viewModel())
    }
}