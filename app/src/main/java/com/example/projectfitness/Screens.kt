package com.example.projectfitness

sealed class Screens(val route: String) {
    object FirstInfoScreen : Screens("fis")
    object SecondInfoScreen : Screens("sis")
    object ThirdInfoScreen : Screens("tis")
    object LoginScreen : Screens("login")
    object RegisterScreen : Screens("Register")
}
