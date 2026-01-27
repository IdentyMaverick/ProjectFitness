package ui.mainpages.mainpages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.projectfitness.R
import com.example.projectfitness.data.local.viewmodel.HomesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.checkerframework.checker.units.qual.h
import ui.mainpages.navigation.NavigationBar
import ui.mainpages.navigation.Screens
import viewmodel.AuthViewModel
import viewmodel.ProjectFitnessViewModel
import viewmodel.ViewModelProfile
import viewmodel.ViewModelSave

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllWorkouts(
    navController: NavController,
    homesViewModel: HomesViewModel
) {
    var clickedProfile by remember { mutableStateOf(false) }
    var showMenuSheet by remember { mutableStateOf(false) }

    var lazyListState = rememberLazyListState()
    val allWorkouts by homesViewModel.workoutsFlow.collectAsState(initial = emptyList())

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            HomeTopBarAllWorkouts(
                clickedProfile = clickedProfile,
                onProfileClick = {navController.navigate(Screens.Home.route)},
                onMenuClick = { showMenuSheet = true }
            )
        },
        containerColor = Color(0xFF121417),
        bottomBar = {},
        floatingActionButton = {
            ExtendedStartButtonAllWorkouts {
                navController.navigate(Screens.CreateWorkout.route) {
                    popUpTo(Screens.AllWorkouts.route)
                }
            }
        },
        floatingActionButtonPosition = FabPosition.EndOverlay,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier
                .padding(top = 30.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                Text("All Workouts",
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                    fontSize = 25.sp)
            }
            Spacer(modifier = Modifier
                .padding(top = 50.dp))

            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
            ){
                itemsIndexed(allWorkouts) {index, item ->
                    val totalIcons = 5
                    val challengeDifficulty = item.component1().workoutRating
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .height(100.dp)
                            .width(350.dp)
                            .background(
                                Color(0xFF1C2126),
                                shape = RoundedCornerShape(0.dp)
                            )
                            .clickable(onClick = {navController.navigate("workoutsettingscreen/${item.workout.workoutId}")}),
                        contentAlignment = Alignment.Center
                    ) {

                        if (index == 0) {
                            Image(
                                painterResource(id = R.drawable.secondinfo),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                alpha = 0.5f,
                                modifier = Modifier
                                    .fillMaxSize(),
                            )
                            Text(
                                text = item.component1().workoutName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    letterSpacing = 0.sp,
                                    textAlign = TextAlign.Center
                                ),
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(bottom = 50.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 30.dp)
                            ) {
                                for (i in 1..totalIcons) {
                                    val iconColor =
                                        if (i <= challengeDifficulty) Color(0xFFF1C40F) else Color.White

                                    Icon(
                                        painter = painterResource(id = R.drawable.skull),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(20.dp),
                                        tint = iconColor
                                    )

                                    if (i < totalIcons) {
                                        Spacer(modifier = Modifier.size(10.dp))
                                    }
                                }
                            }


                        } else if (index == 1) {
                            Image(
                                painterResource(id = R.drawable.login),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                alpha = 0.7f,
                                modifier = Modifier
                                    .fillMaxSize(),
                            )
                            Text(
                                text = item.component1().workoutName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    letterSpacing = 0.sp,
                                    textAlign = TextAlign.Center
                                ),
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(bottom = 50.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 30.dp)
                            ) {
                                for (i in 1..totalIcons) {
                                    val iconColor =
                                        if (i <= challengeDifficulty) Color(0xFFF1C40F) else Color.White

                                    Icon(
                                        painter = painterResource(id = R.drawable.skull),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(20.dp),
                                        tint = iconColor
                                    )

                                    if (i < totalIcons) {
                                        Spacer(modifier = Modifier.size(10.dp))
                                    }
                                }
                            }
                        } else if (index == 2) {
                            Image(
                                painterResource(id = R.drawable.gym),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                alpha = 0.7f,
                                modifier = Modifier
                                    .fillMaxSize(),
                            )
                            Text(
                                text = item.component1().workoutName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    letterSpacing = 0.sp,
                                    textAlign = TextAlign.Center
                                ),
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(bottom = 50.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 30.dp)
                            ) {
                                for (i in 1..totalIcons) {
                                    val iconColor =
                                        if (i <= challengeDifficulty) Color(0xFFF1C40F) else Color.White

                                    Icon(
                                        painter = painterResource(id = R.drawable.skull),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(20.dp),
                                        tint = iconColor
                                    )

                                    if (i < totalIcons) {
                                        Spacer(modifier = Modifier.size(10.dp))
                                    }
                                }
                            }
                        } else if (index == 3) {
                            Image(
                                painterResource(id = R.drawable.gymroomwith),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                alpha = 0.7f,
                                modifier = Modifier
                                    .fillMaxSize(),
                            )
                            Text(
                                text = item.component1().workoutName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    letterSpacing = 0.sp,
                                    textAlign = TextAlign.Center
                                ),
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(bottom = 50.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 30.dp)
                            ) {
                                for (i in 1..totalIcons) {
                                    val iconColor =
                                        if (i <= challengeDifficulty) Color(0xFFF1C40F) else Color.White

                                    Icon(
                                        painter = painterResource(id = R.drawable.skull),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(20.dp),
                                        tint = iconColor
                                    )

                                    if (i < totalIcons) {
                                        Spacer(modifier = Modifier.size(10.dp))
                                    }
                                }
                            }
                        } else if (index == 4) {
                            Image(
                                painterResource(id = R.drawable.gymroomwithgym),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                alpha = 0.7f,
                                modifier = Modifier
                                    .fillMaxSize(),
                            )
                            Text(
                                text = item.component1().workoutName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    letterSpacing = 0.sp,
                                    textAlign = TextAlign.Center
                                ),
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(bottom = 50.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 30.dp)
                            ) {
                                for (i in 1..totalIcons) {
                                    val iconColor =
                                        if (i <= challengeDifficulty) Color(0xFFF1C40F) else Color.White

                                    Icon(
                                        painter = painterResource(id = R.drawable.skull),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(20.dp),
                                        tint = iconColor
                                    )

                                    if (i < totalIcons) {
                                        Spacer(modifier = Modifier.size(10.dp))
                                    }
                                }
                            }
                        } else if (index == 5) {
                            Image(
                                painterResource(id = R.drawable.gymroomgym),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                alpha = 0.7f,
                                modifier = Modifier
                                    .fillMaxSize(),
                            )
                            Text(
                                text = item.component1().workoutName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    letterSpacing = 0.sp,
                                    textAlign = TextAlign.Center
                                ),
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(bottom = 50.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 30.dp)
                            ) {
                                for (i in 1..totalIcons) {
                                    val iconColor =
                                        if (i <= challengeDifficulty) Color(0xFFF1C40F) else Color.White

                                    Icon(
                                        painter = painterResource(id = R.drawable.skull),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(20.dp),
                                        tint = iconColor
                                    )

                                    if (i < totalIcons) {
                                        Spacer(modifier = Modifier.size(10.dp))
                                    }
                                }
                            }
                        } else if (index == 6) {
                            Image(
                                painterResource(id = R.drawable.login),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                alpha = 0.7f,
                                modifier = Modifier
                                    .fillMaxSize(),
                            )
                            Text(
                                text = item.component1().workoutName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    letterSpacing = 0.sp,
                                    textAlign = TextAlign.Center
                                ),
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(bottom = 50.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 30.dp)
                            ) {
                                for (i in 1..totalIcons) {
                                    val iconColor =
                                        if (i <= challengeDifficulty) Color(0xFFF1C40F) else Color.White

                                    Icon(
                                        painter = painterResource(id = R.drawable.skull),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(20.dp),
                                        tint = iconColor
                                    )

                                    if (i < totalIcons) {
                                        Spacer(modifier = Modifier.size(10.dp))
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.size(30.dp))
                }
            }
        }}
}

@Composable
fun ExtendedStartButtonAllWorkouts(onConfirmClick: () -> Unit) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    ExtendedFloatingActionButton(
        onClick = {
            if (!expanded) {
                expanded = true
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    onConfirmClick()
                    delay(500)
                    expanded = false
                }
            }
        },
        icon = {
            Icon(painter = painterResource(R.drawable.projectfitnessplus), "Extended create workout button", Modifier.size(40.dp))
        },
        text = {
            Text("Create Workout", style = TextStyle(fontFamily = FontFamily(Font(R.font.lexendbold)), fontSize = 18.sp))
        },
        containerColor = Color(0xFFF1C40F),
        expanded = expanded,
        modifier = Modifier
            .padding(bottom = 50.dp)
    )
}

@Composable
private fun HomeTopBarAllWorkouts(
    clickedProfile: Boolean,
    onProfileClick: () -> Unit,
    onMenuClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = onProfileClick) {
            Icon(
                painter = painterResource(R.drawable.left),
                contentDescription = null,
                modifier = Modifier.size(25.dp),
                tint = Color.White
            )
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = "PROJECT FITNESS",
            color = Color(0xFFF1C40F),
            fontSize = 14.sp,
            letterSpacing = 6.sp,
            fontFamily = FontFamily(Font(R.font.lexendregular))
        )

        Spacer(Modifier.weight(1f))

        IconButton(onClick = onMenuClick) {
            Icon(
                painter = painterResource(R.drawable.projectfitnesspointheavy),
                contentDescription = null,
                modifier = Modifier.size(25.dp),
                tint = Color.Transparent
            )
        }
    }
}