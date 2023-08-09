package com.example.projectfitness

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun Profile(navController: NavController) {
    val configuration = LocalConfiguration.current
    val screenwidthDp = configuration.screenWidthDp
    val screenheightDp = configuration.screenHeightDp

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
        Icon(
            painterResource(id = R.drawable.left),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null,
                    onClick = { navController.navigate(Screens.Home.route) })
                .padding(top = (screenheightDp / 10).dp)
        )
        Text(
            text = "Profile",
            fontFamily = FontFamily(Font(R.font.poppinslighttext)),
            fontSize = 17.sp,
            color = Color.White,
            modifier = Modifier
                .padding(top = (screenheightDp / 9).dp)
                .align(Alignment.TopCenter)
        )
        Canvas(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 300.dp)
        ) {
            drawCircle(Color.Black, 200f)
        }
        Text(
            text = "Osman Deniz Savaş",
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 70.dp),
            color = Color.White,
            fontFamily = FontFamily(Font(R.font.poppinslighttext)),
            fontSize = 17.sp,
        )
        Text(
            text = "@maverick",
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 20.dp),
            color = Color.White,
            fontFamily = FontFamily(Font(R.font.poppinsextralighttext)),
            fontSize = 15.sp,
        )

        Text(
            text = "Friends \n     0 ",
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 60.dp, end = (screenwidthDp / 2).dp),
            color = Color.White,
            fontFamily = FontFamily(Font(R.font.poppinslighttext)),
            fontSize = 15.sp,
        )
        Canvas(modifier = Modifier.align(Alignment.Center)) {
            drawLine(color = Color.Black, start = Offset(0f, 20f), end = Offset(0f, 100f))
        }
        Text(
            text = "Workouts \n        0 ",
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 60.dp, start = (screenwidthDp / 2).dp),
            color = Color.White,
            fontFamily = FontFamily(Font(R.font.poppinslighttext)),
            fontSize = 15.sp,
        )
        Canvas(modifier = Modifier.align(Alignment.Center)) {
            drawLine(color = Color.Black, start = Offset(-350f, 170f), end = Offset(350f, 170f))
        }
        OutlinedButton(onClick = { /*TODO*/ }, modifier = Modifier.align(Alignment.Center).padding(top = 200.dp), shape = RoundedCornerShape(15.dp)) {
            Text(text = "Edit Profile", color = Color.White, fontFamily = FontFamily(Font(R.font.poppinslighttext)))
        }
    }
}

@Preview(name = "phone", device = "spec:shape=Normal,width=360,height=720,unit=dp,dpi=402")
@Composable
fun PreviewProfile() {
    Profile(navController = rememberNavController())
}