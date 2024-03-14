package database

import com.example.projectfitness.R

data class GymDataClass(val name:String,val image : Int)

// This class include all application image and text database.

val gymDataClass = listOf(
    GymDataClass("Barbell Bench Press", image = R.drawable.barbellbenchpress),
    GymDataClass("Barbell Hip Thrust", image = R.drawable.barbellhipthrust),
    GymDataClass("Box Jump", image = R.drawable.boxjump),
    GymDataClass("Cable Crossover", image = R.drawable.cablecrossover),
    GymDataClass("Calf Raises", image = R.drawable.calfraises),
    GymDataClass("Chest Dip", image = R.drawable.chestdip),
    GymDataClass("Dumbbell Bulgarian Split Squat", image = R.drawable.dumbbellbulgariansplitsquat),)
