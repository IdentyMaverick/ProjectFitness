package viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ViewModelHomes : ViewModel() {


    var isPressed = mutableStateOf(false)
    var clicked = mutableStateOf(false)

}