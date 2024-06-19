package mainpages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.projectfitness.R
import navigation.NavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderBoard(navController: NavController) {

    var expandable by remember { mutableStateOf(false) }
    var expandable2 by remember { mutableStateOf(false) }
    var muscleList by remember {
        mutableStateOf(
            listOf(
                "Chest",
                "Calf",
                "Quads",
                "Hamstrings",
                "Calves",
                "Glutes",
                "IT Band",
                "Plantar Fascia",
                "Hip Flexors",
                "Abductors",
                "Triceps",
                "Biceps",
                "Forearms",
                "Lats",
                "Abs",
                "Adductors",
                "Lower Back",
                "Upper Back",
                "Neck",
                "Obliques",
                "Traps"
            )
        )
    }
    var exerciseList by remember { mutableStateOf(listOf("Bench Press")) }
    var selectedText by remember { mutableStateOf("Muscle Group") }
    var selectedExercise by remember { mutableStateOf("Exercise") }

    var flag by remember { mutableStateOf(true) }
    var flag2 by remember { mutableStateOf(true) }
    var flag3 by remember { mutableStateOf(true) }
    var flag4 by remember { mutableStateOf(true) }

    var flaggg by remember { mutableStateOf(false) }
    var flaggg2 by remember { mutableStateOf(false) }
    var flaggg3 by remember { mutableStateOf(true) }
    var flaggg4 by remember { mutableStateOf(false) }

    val exercises = arrayOf("Chest Press", "Bench Press")
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionTest by remember { mutableStateOf(exercises[0]) }

    var tabTitles = listOf("1 RP MAX", "WORKOUT TIME")
    var selectedTabIndex by remember { mutableStateOf(0) }

    val configuration = LocalConfiguration.current
    val screenwidthDp = configuration.screenWidthDp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF181F26))
    ) {
        SecondaryTabRow(selectedTabIndex = selectedTabIndex,
            containerColor = Color(0xFF181F26),
            contentColor = Color.White,
            indicator = {
                // TabRowDefaults.SecondaryIndicator(color = Color.Cyan)
                TabRowDefaults.SecondaryIndicator(
                    color = Color(0xFFF1C40F),
                    height = 1.5f.dp,
                    modifier = Modifier.tabIndicatorOffset(selectedTabIndex)
                )
            }) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title, maxLines = 2, overflow = TextOverflow.Ellipsis,
                            style = TextStyle(
                                fontFamily = FontFamily(Font(R.font.poppinsregulartext)),
                                fontSize = 15.sp,
                                letterSpacing = 3.sp
                            )
                        )
                    }
                )
            }
        }
        if (selectedTabIndex == 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp, start = 20.dp, end = 10.dp)
            ) {
                Button(onClick = { expandable = !expandable },
                    modifier = Modifier
                        .height(30.dp)
                        .width(160.dp)
                        .align(Alignment.CenterVertically),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(5.dp),
                    contentPadding = PaddingValues(0.dp)) {
                    Text(
                        text = selectedText, style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.poppinsregulartext)),
                            letterSpacing = 3.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        tint = Color(0xFFF1C40F)
                    )
                }
                Box(modifier = Modifier.offset(y = 35.dp, x = (-100).dp))
                {
                    DropdownMenu(expanded = expandable, onDismissRequest = { expandable = false },
                        modifier = Modifier
                            .background(Color.White)
                            .height(200.dp)
                            .width(100.dp)) {
                        for (i in muscleList) {
                            DropdownMenuItem(
                                text = { Text(i) },
                                onClick = {
                                    selectedText = i
                                    expandable = false
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .height(30.dp)
                                    .width(100.dp)
                            )
                        }
                    }
                }


                Spacer(modifier = Modifier.size(50.dp))

                Button(onClick = { expandable2 = !expandable2 },
                    modifier = Modifier
                        .height(30.dp)
                        .width(160.dp)
                        .align(Alignment.CenterVertically),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(5.dp),
                    contentPadding = PaddingValues(0.dp)) {
                    Text(
                        text = selectedExercise, style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.poppinsregulartext)),
                            letterSpacing = 3.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        tint = Color(0xFFF1C40F)
                    )
                }
                Box(modifier = Modifier.offset(y = 35.dp, x = (-100).dp))
                {
                    DropdownMenu(expanded = expandable2, onDismissRequest = { expandable2 = false },
                        modifier = Modifier
                            .background(Color.White)
                            .height(200.dp)
                            .width(100.dp)) {
                        for (i in exerciseList) {
                            DropdownMenuItem(
                                text = { Text(i) },
                                onClick = {
                                    selectedExercise = i
                                    expandable2 = false
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .height(30.dp)
                                    .width(100.dp)
                            )
                        }
                    }
                }
            }
        }
    }
    val indexs = 2
    NavigationBar(navController = navController, indexs, flaggg, flaggg2, flaggg3, flaggg4)
}