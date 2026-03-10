package data.local.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.remote.UserRepository
import kotlinx.coroutines.launch

class FaqcontactfeedbackScreenViewModel(
    private val userRepo: UserRepository
) : ViewModel() {
    fun updateUserIdea(rating: String) {
        viewModelScope.launch {
            userRepo.updateUserIdea(rating)
        }
    }
}