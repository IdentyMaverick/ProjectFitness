package mainpages

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.projectfitness.R
import database.ProjectFitnessContainer
import navigation.NavigationBar
import viewmodel.ViewModelSave


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Activity(navController: NavController, viewModelSave: ViewModelSave) {

    //Database Creation*************************************************************************************************************************************************************

    val context = LocalContext.current
    val scopes = rememberCoroutineScope()
    var projectFitnessContainer = ProjectFitnessContainer(context)
    val itemRepo = projectFitnessContainer.itemsRepository

    val itemsState by itemRepo.getProjectFitnessChallanges().collectAsState(initial = emptyList())
    val itemsState2 by itemRepo.getProjectFitnessCoach().collectAsState(initial = emptyList())

    //******************************************************************************************************************************************************************************

    // Variable Initiliaze -------------------------------------------------------------------------
    val configuration = LocalConfiguration.current
    val screenheightDp = configuration.screenHeightDp
    val screenwidthDp = configuration.screenWidthDp

    var lazyListState: LazyListState = rememberLazyListState()
    var lazyListState2: LazyListState = rememberLazyListState()

    // UI Codes ------------------------------------------------------------------------------------
    Box( // Ana arkaplan
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFF1C40F), Color(0xFF181F26)),
                    start = Offset(0f, 0f),
                    end = Offset(0f, screenheightDp.toFloat() - 150f)
                )
            )
    )
    {
        // Top Bar Design --------------------------------------------------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, top = 10.dp)
        ) {
            Text(
                text = "PROJECT FITNESS",
                fontSize = 25.sp,
                fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                letterSpacing = 3.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            IconButton(onClick = { /*TODO Profile section nav */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.accountcircle),
                    contentDescription = null,
                    Modifier
                        .size(25.dp)
                        .align(Alignment.CenterVertically)
                )
            }

            IconButton(onClick = { /*TODO Profile section nav */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.circlenotifications),
                    contentDescription = null,
                    Modifier
                        .size(25.dp)
                        .align(Alignment.CenterVertically)
                )
            }

            IconButton(onClick = { /*TODO Profile section nav */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.point),
                    contentDescription = null,
                    Modifier
                        .size(25.dp)
                        .align(Alignment.CenterVertically)
                )
            }

        }
        // ---------------------------------------------------------------------------------------------

        Text(
            text = "PF",
            style = TextStyle(
                fontSize = 70.sp,
                fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                lineHeight = 70.sp
            ),
            color = Color(0xFF000000),
            modifier = Modifier.padding(start = 25.dp, top = 50.dp),
        )
        Text(
            text = "WORKOUTS",
            style = TextStyle(
                fontSize = 60.sp,
                fontFamily = FontFamily(Font(R.font.postnobillscolombolight)),
                lineHeight = 70.sp
            ),
            color = Color(0xFF000000),
            modifier = Modifier.padding(start = 25.dp, top = 110.dp),
        )
        /*
            Text(text = "Special Exercises Created By Project Fitness Coach", style = TextStyle(fontSize = 17.sp, fontFamily = FontFamily(Font(R.font.postnobillscolombolight)), lineHeight = 70.sp ,
                letterSpacing = 1.sp),
                color = Color.White.copy(alpha = 0.3f),
                modifier = Modifier
                    .padding(top = 190.dp)
                    .align(Alignment.TopCenter),
            )
        */
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, bottom = 150.dp)
        ) {
            Text(
                text = "CHALLANGES",
                style = TextStyle(
                    fontSize = 25.sp,
                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                    lineHeight = 70.sp,
                    letterSpacing = 20.sp
                ),
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier
                    .padding(top = 190.dp)
                    .align(Alignment.CenterHorizontally),
            )

            LazyRow(
                modifier = Modifier
                    .background(Color.Transparent)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(top = 20.dp, start = 30.dp, end = 30.dp),
                state = lazyListState
            ) {
                itemsIndexed(itemsState) { index, item ->
                    Box(
                        modifier = Modifier
                            .padding(start = 0.dp)
                            .width(327.dp)
                            .height(127.dp)
                            .clip(shape = RoundedCornerShape(20.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0xFF181F26)  // End color
                                    ),
                                    start = Offset.Infinite,
                                    end = Offset(100.0f, 0.0f)
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable(onClick = {
                                viewModelSave.challangesSelectedName.value = item.challangeName
                                viewModelSave.challangesSelectedDifficulty.value =
                                    item.challangeDifficulty
                                navController.navigate("projectchallangesscreen")
                            })

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
                                text = item.challangeName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                    letterSpacing = 5.sp
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
                                val challengeDifficulty = item.challangeDifficulty
                                val totalIcons = 5

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
                                        Spacer(modifier = Modifier.size(20.dp))
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
                                text = item.challangeName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                    letterSpacing = 5.sp
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
                                val challengeDifficulty = item.challangeDifficulty
                                val totalIcons = 5

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
                                        Spacer(modifier = Modifier.size(20.dp))
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
                                text = item.challangeName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                    letterSpacing = 5.sp
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
                                val challengeDifficulty = item.challangeDifficulty
                                val totalIcons = 5

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
                                        Spacer(modifier = Modifier.size(20.dp))
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
                                text = item.challangeName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                    letterSpacing = 5.sp
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
                                val challengeDifficulty = item.challangeDifficulty
                                val totalIcons = 5

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
                                        Spacer(modifier = Modifier.size(20.dp))
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
                                text = item.challangeName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                    letterSpacing = 5.sp
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
                                val challengeDifficulty = item.challangeDifficulty
                                val totalIcons = 5

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
                                        Spacer(modifier = Modifier.size(20.dp))
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
                                text = item.challangeName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                    letterSpacing = 5.sp
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
                                val challengeDifficulty = item.challangeDifficulty
                                val totalIcons = 5

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
                                        Spacer(modifier = Modifier.size(20.dp))
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
                                text = item.challangeName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                    letterSpacing = 5.sp
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
                                val challengeDifficulty = item.challangeDifficulty
                                val totalIcons = 5

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
                                        Spacer(modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        }
                    }
                    Canvas(
                        modifier = Modifier
                            .padding(bottom = 30.dp)
                            .fillMaxHeight()
                            .align(Alignment.CenterHorizontally)
                    ) {
                        val crcRadius = 10f
                        val crcSpacing = 40f

                        for (i in 0 until itemsState.size) {
                            val crcX =
                                size.width / 1 + i * crcSpacing - (itemsState.size + 8) * crcSpacing / 1
                            val crcColor = if (i == index) Color(0xFFF1C40F) else Color.White
                            drawCircle(crcColor, crcRadius, Offset(crcX, size.height - 320))
                        }
                    }
                    Spacer(modifier = Modifier.size(70.dp))
                }

            }
        }
        Column(
            modifier = Modifier
                .padding(top = 400.dp, bottom = 0.dp)
                .align(Alignment.Center)
                .height(200.dp)
                .fillMaxWidth()
                .background(Color.Transparent)
        ) {
            Text(
                text = "COACH SELECTIONS",
                style = TextStyle(
                    fontSize = 25.sp,
                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                    lineHeight = 70.sp,
                    letterSpacing = 10.sp
                ),
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            LazyRow(
                modifier = Modifier
                    .background(Color.Transparent)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(top = 20.dp, start = 30.dp, end = 30.dp),
                state = lazyListState2
            ) {
                itemsIndexed(itemsState2) { index, item ->
                    Box(
                        modifier = Modifier
                            .padding(start = 0.dp)
                            .width(327.dp)
                            .height(127.dp)
                            .clip(shape = RoundedCornerShape(20.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0xFF181F26)  // End color
                                    ),
                                    start = Offset.Infinite,
                                    end = Offset(100.0f, 0.0f)
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable(onClick = {
                                viewModelSave.coachesSelectedName.value = item.coachName
                                viewModelSave.coachesSelectedDifficulty.value = item.coachDifficulty
                                navController.navigate("projectcoachscreen")
                            })

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
                                text = item.coachName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                    letterSpacing = 5.sp
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
                                val challengeDifficulty = item.coachDifficulty
                                val totalIcons = 5

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
                                        Spacer(modifier = Modifier.size(20.dp))
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
                                text = item.coachName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                    letterSpacing = 5.sp
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
                                val challengeDifficulty = item.coachDifficulty
                                val totalIcons = 5

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
                                        Spacer(modifier = Modifier.size(20.dp))
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
                                text = item.coachName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                    letterSpacing = 5.sp
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
                                val challengeDifficulty = item.coachDifficulty
                                val totalIcons = 5

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
                                        Spacer(modifier = Modifier.size(20.dp))
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
                                text = item.coachName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                    letterSpacing = 5.sp
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
                                val challengeDifficulty = item.coachDifficulty
                                val totalIcons = 5

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
                                        Spacer(modifier = Modifier.size(20.dp))
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
                                text = item.coachName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                    letterSpacing = 5.sp
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
                                val challengeDifficulty = item.coachDifficulty
                                val totalIcons = 5

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
                                        Spacer(modifier = Modifier.size(20.dp))
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
                                text = item.coachName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                    letterSpacing = 5.sp
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
                                val challengeDifficulty = item.coachDifficulty
                                val totalIcons = 5

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
                                        Spacer(modifier = Modifier.size(20.dp))
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
                                text = item.coachName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                    letterSpacing = 5.sp
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
                                val challengeDifficulty = item.coachDifficulty
                                val totalIcons = 5

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
                                        Spacer(modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        }
                    }
                    Canvas(
                        modifier = Modifier
                            .padding(top = 235.dp)
                            .fillMaxHeight()
                            .align(Alignment.CenterHorizontally)
                    ) {
                        val crcRadius = 10f
                        val crcSpacing = 40f

                        for (i in 0 until itemsState.size) {
                            val crcX =
                                size.width / 1 + i * crcSpacing - (itemsState.size + 8) * crcSpacing / 1
                            val crcColor = if (i == index) Color(0xFFF1C40F) else Color.White
                            drawCircle(crcColor, crcRadius, Offset(crcX, size.height - 320))
                        }
                    }
                    Spacer(modifier = Modifier.size(70.dp))
                }

            }


        }
    }


    var flagg by remember { mutableStateOf(false) }
    var flagg2 by remember { mutableStateOf(true) }
    var flagg3 by remember { mutableStateOf(false) }
    var flagg4 by remember { mutableStateOf(false) }


    val indexs = 1
    NavigationBar(navController = navController, indexs, flagg, flagg2, flagg3, flagg4)
}


@Preview(name = "phone", device = "spec:shape=Normal,width=360,height=720,unit=dp,dpi=402")
@Composable
fun PreviewActivity() {
    Activity(navController = rememberNavController(), viewModelSave = ViewModelSave())
}