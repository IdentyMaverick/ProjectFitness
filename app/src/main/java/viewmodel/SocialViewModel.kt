
import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.remote.FirestoreRepository
import data.remote.User
import kotlinx.coroutines.launch

class SocialViewModel(private val repository: FirestoreRepository) : ViewModel() {


    fun followUser(followerId: String, followingId: String) {
        repository.followUser(followerId, followingId)
    }

    fun unfollowUser(followerId: String, followingId: String) {
        repository.unfollowUser(followerId, followingId)
    }

    fun getFollowers(userId: String): LiveData<List<String>> {
        return repository.getFollowers(userId)
    }

    fun getFollowing(userId: String): LiveData<List<String>> {
        return repository.getFollowing(userId)
    }

    @SuppressLint("RestrictedApi")
    fun getUserByNicknameLive(nickname: String): LiveData<User?> {
        val userLiveData = MutableLiveData<User?>()
        viewModelScope.launch {
            val user = repository.getUserByNickname(nickname)
            userLiveData.postValue(user)
        }
        return userLiveData
    }
    @SuppressLint("RestrictedApi")
    fun getUserByIdLive(id: String): LiveData<User?> {
        val userLiveData = MutableLiveData<User?>()
        viewModelScope.launch {
            val user = repository.getUserById(id)
            userLiveData.postValue(user)
        }
        return userLiveData
    }
    @SuppressLint("RestrictedApi")
    fun getAllUsers(): LiveData<List<User>> {
        return repository.getAllUsers()
    }
}
