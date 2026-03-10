package data.local.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class ChooseExercisesViewModel : ViewModel() {
    val _workoutName: MutableStateFlow<String> = MutableStateFlow("")
    val workoutName = _workoutName

    fun setName(workoutName: String) {
        _workoutName.value = workoutName
    }
}