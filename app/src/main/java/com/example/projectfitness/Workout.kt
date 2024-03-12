package com.example.projectfitness

data class Workout(val name : String, val exercises : List<Exercise>)
data class Exercise(val name : String, val reps:Int,val sets:Int)
