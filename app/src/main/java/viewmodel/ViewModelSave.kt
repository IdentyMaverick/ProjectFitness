package viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import database.Exercise
import database.Workout

class ViewModelSave() : ViewModel(){
    val name = mutableStateOf("")
    val exercises = mutableStateListOf<String?>()
    val flag = mutableStateOf(true)
    var allowed = mutableStateOf(true)
    var set   =  mutableStateListOf<String>("0","0","0","0","0","0","0","0","0")
    var reps  =  mutableStateListOf<String>("0","0","0","0","0","0","0","0","0")
    var count = mutableStateOf(0)
    var workoutList = mutableListOf<Workout>()
    var selectedItemName = mutableStateOf("")   // Create Workout --> Exercise selected --> Workout Details --> ADD EXERCISES --> (selectedItemName = Exercise Name)
    var selectedWorkoutName = mutableStateOf("")
    var selectedListWorkouts: Workout? by mutableStateOf(null)

    var exercisesForWorkouts2 = mutableStateListOf<Exercise>()


    fun updateSelectedItemName(newName: String) {
        selectedItemName.value = newName
    }

    fun workouts(): List<Workout> {
        var exerciseList = exercises.filterNotNull()
        val repsList = reps.map { it.toIntOrNull() ?: 0 }
        val setsList = set.map { it.toIntOrNull() ?: 0 }

        if (exerciseList.isNotEmpty() && allowed.value) {
            var exercisesForWorkout = mutableListOf<Exercise>()
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