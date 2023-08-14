package com.example.projectfitness

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun CreateWorkout(navController: NavController) {
    val configuration = LocalConfiguration.current
    val screenwidthDp = configuration.screenWidthDp.dp
    val screenheightDp = configuration.screenHeightDp.dp

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
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 70.dp, start = 40.dp),
            text = "CHOOSE \nEXERCISES",
            fontFamily = FontFamily(Font(R.font.poppinsboldtext)),
            color = Color.White,
            fontSize = 35.sp,
            )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .width(screenwidthDp)
                .height(screenheightDp / 1.4f)
                .background(Color(0xFF506172), shape = RoundedCornerShape(10.dp))
        )
        {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .width(screenwidthDp / 1.1f)
                    .height(screenheightDp / 1.7f)
                    .background(
                        Color(0xFF181F26),
                        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                    )
            )
        }
    }
}

@Preview
@Composable
fun PreviewCreateWorkout() {
    CreateWorkout(rememberNavController())
}