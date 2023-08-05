package com.example.projectfitness

sealed class ProjectFitnessPageScreen(val route:String){
    object ThirdInfoPageScreen: ProjectFitnessPageScreen("thirdinfopagescreen")
    object LoginPageScreen: ProjectFitnessPageScreen("loginpagescreen")
}
