package com.example.projectfitness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.projectfitness.ui.theme.ProjectFitnessTheme
import com.example.projectfitness.ui.theme.poppins

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            Main()
        }
    }
    @Composable
    fun Main(){

        LoginScreen()
    }

    @Composable
    fun LoginScreen(){
        Column (verticalArrangement = Arrangement.Center,horizontalAlignment = Alignment.CenterHorizontally,modifier= Modifier
            .background(color = colorResource(id = R.color.projectfitnessblue))
            .fillMaxSize()) {
            Text(text ="PROJECT FITNESS", fontFamily = FontFamily(Font(R.font.poppinsboldtext)),
                color = colorResource(id = R.color.projectfitnessyellow), fontSize = 35.sp)
        }
    }

    @Preview(showSystemUi = true)
    @Composable
    fun PreviewMain() {
        Main()
    }
}