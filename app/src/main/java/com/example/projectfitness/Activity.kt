package com.example.projectfitness

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Activity(navController: NavController,viewModelSave: ViewModelSave) {

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

        //val isPressed by interactionSource.collectIsPressedAsState()
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
                style = TextStyle(fontSize = 20.sp,letterSpacing = 10.sp)
            )
            Text(
                text = "WORKOUTS",
                color = Color(0xFFD9D9D9),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 60.dp),
                fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                style = TextStyle(fontSize = 30.sp,letterSpacing = 5.sp)
            )


            Box(  modifier = Modifier.align(Alignment.TopEnd) ){IconButton(
                onClick = { expanded = true }, modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 55.dp)
            ) {
                Icon(
                    painterResource(id = R.drawable.point),
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                        .padding(end = 10.dp),
                    tint = Color(0xFFD9D9D9)
                )

            }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = !expanded },
                    modifier = Modifier
                ) {
                    DropdownMenuItem(text = { Text(text = "Create Workout") }, onClick = { navController.navigate(Screens.ChooseExercises.route)
                        fl.value = true
                        name.value = ""
                        exercises.clear()
                        notworkoutPressed.value = false})
                }
            }

            /*Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .align(Alignment.TopCenter)
            ) {
                drawLine(
                    color = Color(0xFF516273),
                    start = Offset(0f,225f),
                    end =   Offset(1200f,225f)
                )
            }*/
            //val itemsList = (0..2).toList()
            val itemIndexedList = listOf("A", "B", "C", "D")
            val state = rememberLazyListState()
            LazyRow(
                modifier = Modifier
                    .padding(top = 120.dp)
                    .align(Alignment.TopCenter),
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
                                .width(100.dp)
                                .height(30.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = color),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(text = "All", textAlign = TextAlign.Center, style = TextStyle(fontSize = 17.sp,letterSpacing = 1.sp), fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)))
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
                                .width(100.dp)
                                .height(30.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = color2),
                            contentPadding = PaddingValues(0.dp)

                        ) {
                            Text(text = "UpperBody", style = TextStyle(fontSize = 17.sp,letterSpacing = 1.sp), textAlign = TextAlign.Center,fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)))
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
                                .width(100.dp)
                                .height(30.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = color3),
                            contentPadding = PaddingValues(0.dp)

                        ) {
                            Text(text = "LowerBody", style = TextStyle(fontSize = 17.sp,letterSpacing = 1.sp), textAlign = TextAlign.Center,fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)))
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
                                .width(100.dp)
                                .height(30.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = color4),
                            contentPadding = PaddingValues(0.dp)

                        ) {
                            Text(text = "Chest", style = TextStyle(fontSize = 17.sp,letterSpacing = 1.sp), textAlign = TextAlign.Center,fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)))
                        }
                        Spacer(modifier = Modifier.size(10.dp))
                    }
                }
            }
            val indexs = 1
            NavigationBar(navController = navController, indexs, flagg, flagg2,flagg3,flagg4)
        }
    }



@Preview(name = "phone", device = "spec:shape=Normal,width=360,height=720,unit=dp,dpi=402")
@Composable
fun PreviewActivity() {
    Activity(navController = rememberNavController(), viewModelSave = viewModel())
}