package com.example.projectfitness

import Info
import LoginScreen
import RegisterScreen
import SecondInfo
import ThirdInfo
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screens.FirstInfoScreen.route) {
        composable(route = Screens.FirstInfoScreen.route) {
            Info(navController = navController)
        }
        composable(route = Screens.SecondInfoScreen.route) {
            SecondInfo(navController = navController)
        }
        composable(route = Screens.ThirdInfoScreen.route) {
            ThirdInfo(navController = navController)
        }
        composable(route = Screens.LoginScreen.route){
            LoginScreen(navController = navController)
        }
        composable(route = Screens.LoginScreen.ForgetPasswordScreen.route) {
            ForgetPasswordScreen(navController = navController)
        }
        composable(route = Screens.LoginScreen.RegisterScreen.route) {
            RegisterScreen(navController = navController)
        }
    }

}
/*@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalPagers(){
    val pageCount = 3
    val pagerState = rememberPagerState()
    HorizontalPager(state = pagerState, pageCount = pageCount) { index ->
        if (index == 0) {
            Info()
        } else if (index == 1) {
            SecondInfo()
        } else if (index == 2) {
            ThirdInfo(navController = rememberNavController())
        }
        Row(
            Modifier
                .fillMaxSize()
                .padding(100.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        )
        {
            repeat(pageCount) { iteration ->
                val color =
                    if (pagerState.currentPage == iteration) Color(0xFFF1C40F) else Color.White
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(10.dp)
                )
            }
        }
    }
}
*/