package database

data class Workout(val name : String, var exercises : List<Exercise>)
data class Exercise(var name : String, var reps:Int, var sets:Int)
