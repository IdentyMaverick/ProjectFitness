package viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel

class ViewModelProfile() : ViewModel() {
    var imageBitmap = mutableStateOf<ImageBitmap?>(null)
    var selectedImageUri = mutableStateOf<String?>(null)
    var rememberMeBool =  mutableStateOf(false)

    var selectedCompletedWorkoutId = mutableStateOf(0)
    var selectedCompletedId = mutableStateOf(0)
    var selectedWorkoutName = mutableStateOf("")

    var nickNameId = mutableStateOf("")


}