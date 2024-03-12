package com.example.projectfitness

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun Meal(navController: NavController)
{
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF181F26))
    )
    {
        Text(
            text = "SOON",
            modifier = Modifier.align(Alignment.Center),
            color = Color.White,
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                fontSize = 100.sp
            )
        )
        Text(
            text = "MEAL TIME",
            modifier = Modifier.align(Alignment.Center).padding(top = 150.dp),
            color = Color.White,
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                fontSize = 25.sp,
                letterSpacing = 10.sp
            )
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewMeal(){
    Meal(navController = rememberNavController())
}