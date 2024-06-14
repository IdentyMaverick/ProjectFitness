package viewmodel

import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import database.Exercises
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProjectFitnessViewModel : ViewModel() {
    val firestoreData = MutableLiveData<String>()
    val firestoreExercisesData = MutableLiveData<List<Exercises>>() // Egzersiz verileri için MutableLiveData güncellendi
    private val firestore = Firebase.firestore
    private val itemsCollection = firestore.collection("exercises")
    val firestoreItems: MutableLiveData<List<Exercises>> = MutableLiveData() // Egzersiz verileri için MutableLiveData güncellendi

    init {
        loadDataFromFirestore()
        loadExercisesDataFromFirestore()
    }

    fun loadDataFromFirestore() {
        val db = Firebase.firestore
        val auth = Firebase.auth
        val currentUserInfo = auth.currentUser?.email
        val docRef = db.collection("users")
        val query = docRef.whereEqualTo("email", currentUserInfo)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val snapshot = query.get().await()
                for (document in snapshot) {
                    val nameofUser = document.data["first"].toString()
                    firestoreData.value = nameofUser
                    Log.d(TAG, "Username is $nameofUser")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading user data: ${e.message}")
            }
        }
    }

    private fun loadExercisesDataFromFirestore() {
        itemsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Error listening to exercises collection: ${error.message}")
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                try {
                    val itemList = snapshot.documents.map { document ->
                        val data = document.data
                        val name = data?.get("name") as? String?
                        val bodypart = data?.get("bodypart") as? String
                        val index = data?.get("index") as? String
                        val secondarymuscles = data?.get("secondary muscles") as? String
                        Exercises(name, bodypart, index, secondarymuscles)
                    }
                    firestoreItems.value = itemList
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing exercises data: ${e.message}")
                }
            }
        }
    }
}
