package com.example.projectfitness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projectfitness.ui.theme.ProjectFitnessTheme

class InfoScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Info()
        }
    }
}
@Composable
fun Info(){
    Box(
        Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.projectfitnessblue)), ) {
        Image(painter = painterResource(id = R.drawable.info), contentDescription =null, alpha = 0.3f, contentScale = ContentScale.FillHeight, modifier = Modifier.size(800.dp) )
        Text(text = "TRACK YOUR WORKOUT",color = colorResource(id = R.color.projectfitnessyellow), modifier = Modifier
            .align(
                Alignment.Center
            )
            .padding(top = 125.dp, start = 70.dp, end = 70.dp), fontSize = 40.sp, fontFamily = FontFamily(Font(R.font.poppinsregulartext)), textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.size(10.dp))
        Text(text = "Track and save your all exercises in this app",color = Color.White, modifier = Modifier
            .align(
                Alignment.Center
            )
            .padding(top = 330.dp, start = 55.dp, end = 55.dp), fontSize = 24.sp, fontFamily = FontFamily(Font(R.font.poppinsregulartext)), textAlign = TextAlign.Center)
    }
}
@Preview(showSystemUi = true)
@Composable
fun PreviewInfo(){
    Info()
}