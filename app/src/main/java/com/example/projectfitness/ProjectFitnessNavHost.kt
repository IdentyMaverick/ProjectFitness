package com.example.projectfitness

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProjectFitnessHost(navController:NavHostController,pagerState: PagerState) {
    if (pagerState.currentPage == 2){
        NavHost(
            navController = navController,
            startDestination = ProjectFitnessPageScreen.ThirdInfoPageScreen.route
        ) {
            composable(route = ProjectFitnessPageScreen.ThirdInfoPageScreen.route) {
                ThirdInfo(navController = navController)
            }
            composable(route = ProjectFitnessPageScreen.LoginPageScreen.route) {
                LoginScreen()
            }
        }
    }
}