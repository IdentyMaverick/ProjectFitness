package viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import database.Exercise
import database.Workout

// Kotlin class that creates viewmodel and saving create workout list in any class without loss.
class ViewModelSave : ViewModel(){
    val name = mutableStateOf("")
    val exercises = mutableStateListOf<String?>()
    val flag = mutableStateOf(true)
    var allowed = mutableStateOf(false)

    var set   =  mutableStateListOf<String>("0","0","0","0","0","0","0","0","0")
    var reps  =  mutableStateListOf<String>("0","0","0","0","0","0","0","0","0")


    var count = mutableStateOf(0)
    var workoutList = mutableListOf<Workout>()

    var selectedIndexWorkoutinList = mutableStateOf("")
    var selectedItemName = mutableStateOf("")
    var selectedWorkoutName = mutableStateOf("")
    var selectedListWorkoutIndex = mutableStateOf("")
    fun updateSelectedItemName(newName: String) {
        selectedItemName.value = newName
    }
    fun updateSelectedWorkoutName(newName: String) {
        selectedWorkoutName.value = newName
    }
    fun updateSelectedWorkoutIndex (newIndex : String){
        selectedListWorkoutIndex.value = newIndex
    }
    fun workouts(): List<Workout> {

        var exerciseList = exercises.filterNotNull()
        val repsList = reps.map { it.toIntOrNull() ?: 0 }
        val setsList = set.map { it.toIntOrNull() ?: 0 }

        if (exerciseList.isNotEmpty() && allowed.value == true) {
            val exercisesForWorkout = mutableListOf<Exercise>()
            for (i in exerciseList.indices) {
                val exercise = Exercise(
                    name = exerciseList[i],
                    reps = repsList[i],
                    sets = setsList[i]
                )
                exercisesForWorkout.add(exercise)

            }
                val workout = Workout(name = name.value, exercises = exercisesForWorkout)
                if (workout !in workoutList){workoutList.add(workout)}
        }

        return workoutList
    }
}