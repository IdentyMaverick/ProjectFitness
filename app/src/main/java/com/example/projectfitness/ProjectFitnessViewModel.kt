package com.example.projectfitness

import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProjectFitnessViewModel : ViewModel() {
    val firestoreData = MutableLiveData<String>()
    val firestoreExercisesData = MutableLiveData<String>()

    private val firestore = Firebase.firestore
    private val itemsCollection = firestore.collection("exercises")
    val firestoreItems: MutableState<List<Exercises>> = mutableStateOf(emptyList())

    init {
        loadExercisesDataFromFirestore()
    }

    fun loadDataFromFirestore() {
        val db = Firebase.firestore
        val auth = Firebase.auth
        var currentUserInfo = auth.currentUser?.email
        var nameofUser: String = ""
        val docRef = db.collection("users")
        val query = docRef.whereEqualTo("email", currentUserInfo)
        CoroutineScope(Dispatchers.Main).launch {
            query.get().addOnSuccessListener { document ->
                for (document in document) {
                    nameofUser = document.data.get("first").toString()
                    firestoreData.value = nameofUser
                    Log.d(TAG, "Username is " + firestoreData.value)
                }
            }.await()
            Log.d(TAG, "Username2 is " + firestoreData.value)
        }
    }

    fun loadExercisesDataFromFirestore() {
        itemsCollection.addSnapshotListener{snapshot,error ->
            if (error != null){
                return@addSnapshotListener

            }

            else if (snapshot != null && !snapshot.isEmpty){
                try {
                    val itemList = snapshot.documents.map { document ->
                        val data = document.data
                        val name = data?.get("name") as? String?
                        val bodypart = data?.get("bodypart") as? String
                        val index = data?.get("index") as? String
                        val secondarymuscles = data?.get("secondary muscles") as? String
                        Exercises(name, bodypart,index, secondarymuscles)
                    }
                    firestoreItems.value = itemList
            }
                catch (e:Exception){
                    Log.d(TAG,"Error is " + e.message)
                }
            }
        }
    }
}