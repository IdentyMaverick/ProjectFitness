package com.example.projectfitness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    lateinit var navController: NavHostController
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            val systemUiController = rememberSystemUiController()
            navController = rememberNavController()
            Main()
            SideEffect {
                systemUiController.setStatusBarColor(Color.Black)
                systemUiController.setSystemBarsColor(Color.Black)
            }

        }
    }
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun Main(){

        val pageCount = 3
        val pagerState = rememberPagerState()
        HorizontalPager(state = pagerState, pageCount = pageCount){ index ->

            if (index == 0){
                Info()
                }
            else if (index == 1){
                SecondInfo()}
            else if (index == 2){
                ThirdInfo(navController)

                }
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(100.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.Bottom)
        { repeat(pageCount){ iteration -> val color = if (pagerState.currentPage == iteration) Color(0xFFF1C40F) else Color.White
        Box(modifier = Modifier
            .padding(2.dp)
            .clip(CircleShape)
            .background(color)
            .size(10.dp))} }
            ProjectFitnessHost(navController = navController,pagerState)
    }
    }
    @Preview(name = "phone", device = "spec:shape=Normal,width=360,height=720,unit=dp,dpi=402")
    @Composable
    fun PreviewMain(){
        Main()
    }
}