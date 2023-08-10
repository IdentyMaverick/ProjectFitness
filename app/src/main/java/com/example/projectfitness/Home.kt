package com.example.projectfitness

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun Home(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C3E50))
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
        Canvas(
            modifier = Modifier.size(100.dp).clickable(interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = { navController.navigate(Screens.Home.Profile.route) })
        )
        {
            drawCircle(color = Color.Black, radius = 70f, center = Offset(170f, 200f))
        }
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp)
        ) {
            drawLine(
                color = Color(0xFF516273),
                start = Offset(100f, 250f),
                end = Offset(800f, 250f)
            )
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
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.poppinslighttext)),
                fontSize = 17.sp
            )
            Spacer(modifier = Modifier.size(120.dp))
            Text(
                text = "See all",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.poppinslighttext)),
                fontSize = 12.sp
            )
        }
        val indexs = 0
        NavigationBar(navController = navController, indexs)
    }


}

@Preview(name = "phone", device = "spec:shape=Normal,width=360,height=720,unit=dp,dpi=402")
@Composable
fun PreviewHome() {
    Home(navController = rememberNavController())
}