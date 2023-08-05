package com.example.projectfitness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

class SecondInfoScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SecondInfo()
        }
    }
}
@Composable
fun SecondInfo(){
    Box(
        Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.projectfitnessblue)), ) {
        Image(painter = painterResource(id = R.drawable.secondinfo), contentDescription =null, alpha = 0.3f, contentScale = ContentScale.FillHeight, modifier = Modifier.size(800.dp) )
        Text(text = "SHOW YOUR STRENGTH",color = colorResource(id = R.color.projectfitnessyellow), modifier = Modifier
            .align(
                Alignment.Center
            )
            .padding(top = 125.dp, start = 70.dp, end = 70.dp), fontSize = 35.sp, fontFamily = FontFamily(
            Font(R.font.poppinsregulartext)
        ), textAlign = TextAlign.Center)
        Text(text = "Compete other users",color = Color.White, modifier = Modifier
            .align(
                Alignment.Center
            )
            .padding(top = 330.dp, start = 55.dp, end = 55.dp), fontSize = 20.sp, fontFamily = FontFamily(
            Font(R.font.poppinsregulartext)
        ), textAlign = TextAlign.Center)
    }
}
@Preview(name = "phone", device = "spec:shape=Normal,width=360,height=720,unit=dp,dpi=402")
@Composable
fun PreviewSecondInfo(){
    SecondInfo()
}