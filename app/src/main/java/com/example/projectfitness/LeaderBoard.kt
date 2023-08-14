package com.example.projectfitness

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderBoard(navController: NavController) {

    var flag by remember { mutableStateOf(true) }
    var flag2 by remember { mutableStateOf(true) }
    var flag3 by remember { mutableStateOf(true) }
    var flag4 by remember { mutableStateOf(true) }

    val exercises = arrayOf("Chest Press", "Bench Press")
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionTest by remember { mutableStateOf(exercises[0]) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF181F26))
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 5.dp),
            text = "Project Fitness",
            fontFamily = FontFamily(Font(R.font.poppinsboldtext)),
            color = Color(0xFFF1C40F),
            fontSize = 20.sp
        )
        Text(
            text = "Leader Board",
            color = Color.White,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 60.dp, end = 170.dp),
            fontSize = 20.sp,
            fontFamily = FontFamily(Font(R.font.poppinslighttext))
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 45.dp, end = 30.dp)
        ) {
            TextField(
                value = selectedOptionTest,
                onValueChange = {},
                Modifier
                    .menuAnchor()
                    .height(60.dp)
                    .width(150.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFF181F26),
                    unfocusedTextColor = Color(0xFFF1C40F),
                    focusedTextColor = (Color(0xFFF1C40F)),
                    unfocusedIndicatorColor = Color(0xFF181F26),
                    focusedIndicatorColor = Color(0xFF181F26)
                ),
                readOnly = true,
                trailingIcon = {
                    Icon(
                        painterResource(id = R.drawable.down),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },

                ) {
                exercises.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(text = selectionOption) },
                        onClick = {
                            selectedOptionTest = selectionOption
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }

            }

        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter)
                .padding(top = 25.dp)
        ) {
            drawLine(
                color = Color(0xFF516273),
                start = Offset(0f,225f),
                end =   Offset(1200f,225f)
            )
        }

        val itemIndexedList = listOf("A", "B", "C", "D")
        LazyRow(
            modifier = Modifier
                .padding(top = 120.dp)
                .align(Alignment.TopCenter)
        ) {
            itemsIndexed(itemIndexedList) { index, item ->
                Spacer(modifier = Modifier.size(10.dp))

                var color = if (flag) {
                    Color(0xFFF1C40F)
                } else {
                    Color(0xFF506172)
                }
                var color2 = if (flag2) {
                    Color(0xFF506172)
                } else Color(0xFFF1C40F)

                var color3 = if (flag3) {
                    Color(0xFF506172)
                } else {
                    Color(0xFFF1C40F)
                }

                var color4 = if (flag4) {
                    Color(0xFF506172)
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
                            .height(25.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = color),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "All Time",
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily(
                                Font(R.font.poppinslighttext)
                            )
                        )
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
                            .height(25.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = color2),
                        contentPadding = PaddingValues(0.dp)

                    ) {
                        Text(text = "6 Months", fontSize = 12.sp, textAlign = TextAlign.Center)
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
                            .height(25.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = color3),
                        contentPadding = PaddingValues(0.dp)

                    ) {
                        Text(text = "3 Months", fontSize = 12.sp, textAlign = TextAlign.Center)
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
                            .height(25.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = color4),
                        contentPadding = PaddingValues(0.dp)

                    ) {
                        Text(text = "1 Months", fontSize = 12.sp, textAlign = TextAlign.Center)
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                }
            }
        }
        Canvas(modifier = Modifier.size(100.dp).align(Alignment.TopStart).padding(top = 170.dp)){
            drawRoundRect(color = Color(0xFFF1C40F), cornerRadius = CornerRadius(40f), size = Size(600f,150f))

        }
    }
    //NavigationBar(navController = navController, indexs = 2, flag = , flag2 = )
}

@Preview(name = "phone", device = "spec:shape=Normal,width=360,height=720,unit=dp,dpi=402")
@Composable
fun PreviewLeaderBoard() {
    LeaderBoard(rememberNavController())
}