package database

data class Workout(val name : String, val exercises : List<Exercise>)
data class Exercise(val name : String, var reps:Int, var sets:Int)
