package data.remote

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Database : ComponentActivity() {
    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DatabaseReadName()
        }
    }

    fun DatabaseUserCreate(user: HashMap<String, String>) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").add(user)
            .addOnSuccessListener { Log.d(TAG, "Success") }
            .addOnFailureListener { Log.e(TAG, "Fail ") }
    }

    @SuppressLint("UnrememberedMutableState")
    @Composable
    fun DatabaseReadName() {
        val db = Firebase.firestore
        val auth = Firebase.auth
        var currentUserInfo = auth.currentUser?.email
        var courseList = mutableStateListOf<Person?>()
        val docRef = db.collection("users")
        val query = docRef.whereEqualTo("email", currentUserInfo)
        query.get().addOnSuccessListener { document ->
            for (document in document) {
                var p: Person? = document.toObject(Person::class.java)
                courseList.add(p)
            }
        }
        DataPreview(context = this@Database, courseList = courseList)
    }
}

@Composable
fun DataPreview(context: Context, courseList: SnapshotStateList<Person?>) {
    Box(
        modifier = Modifier
            .background(color = Color.White)
            .fillMaxSize()
    ) {
        Text(text = "User is : " + courseList.get(0), color = Color.Black, fontSize = 20.sp)
    }
}