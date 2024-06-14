package homes.inside

import SocialViewModel
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.projectfitness.R
import com.google.firebase.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage

@Composable
fun ProjectFollowersScreen(navController: NavController , socialViewModel: SocialViewModel){
    val user = com.google.firebase.ktx.Firebase.auth.currentUser

    val getAllUserState by socialViewModel.getAllUsers().observeAsState(initial = emptyList())
    val getFollowers by socialViewModel.getFollowers(user?.uid ?: "").observeAsState(initial = emptyList())
    val getFollowing by socialViewModel.getFollowing(user?.uid ?: "").observeAsState(initial = emptyList())
    val filterUserState = getAllUserState.filterIndexed{index, item ->getFollowers.contains(item.id)}
    val storageRef = Firebase.storage.reference

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF181F26))){
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.Transparent)) {
            IconButton(onClick = { navController.navigate("profile") }) {
                Icon(painter = painterResource(id = R.drawable.projectfitnessprevious), contentDescription = null ,
                    tint = Color.White , modifier = Modifier.align(Alignment.CenterVertically))
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "PROJECT FITNESS" , style = TextStyle(fontSize = 30.sp , fontFamily = FontFamily(Font(
                R.font.postnobillscolombosemibold)) , letterSpacing = 3.sp , color = Color.White) ,
                modifier = Modifier
                    .padding(end = 50.dp)
                    .align(Alignment.CenterVertically))
        }
        Column(modifier = Modifier
            .padding(top = 100.dp)
            .align(Alignment.TopCenter)) {
            Text(text = "FOLLOWERS" , style = TextStyle(fontSize = 25.sp , fontFamily = FontFamily(Font(
                R.font.postnobillscolombosemibold)) , letterSpacing = 20.sp , color = Color.White.copy(alpha = 0.5f)) ,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.size(30.dp))
            filterUserState.forEachIndexed{index , item ->
                Row(modifier = Modifier.padding(end = 30.dp , start = 30.dp)) {

                    var clicked by remember { mutableStateOf(false) }
                    if (getFollowing.contains(item.id)){clicked = true}
                    val uid = item.id
                    var imageUrl by remember { mutableStateOf<String?>(null) }
                    var profileRef : StorageReference = storageRef.child("gs://projectfitness-ddfeb.appspot.com/profile_photos/$uid/profile.jpg")

                    LaunchedEffect(Unit) {
                        profileRef.downloadUrl.addOnSuccessListener { uri ->
                            imageUrl = uri.toString()
                            Log.d("profileRef3",imageUrl.toString())
                            Log.d("Firebase","Success to Download image URL")
                        }.addOnFailureListener{ exception ->
                            Log.d("profileRef3",imageUrl.toString())
                            Log.e("Firebase", "Failed to download image URL",exception)
                        }
                    }

                    Image(painter = if (imageUrl!=null){
                        rememberAsyncImagePainter(model = imageUrl , onError = { error ->
                            Log.e("Image Loading Error", "Error: $error")
                        })
                    }else{
                        painterResource(id = R.drawable.secondinfo)
                    }, contentDescription = "loll" ,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .border(BorderStroke(1.dp, Color.White), CircleShape)
                            .align(Alignment.CenterVertically)
                            .clickable(onClick = {navController.navigate("otherscreenprofile/${item.nickname}")}))
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(text = "@" + item.nickname ,
                        style = TextStyle(fontSize =25.sp ,
                            color = Color.White ,
                            fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)) ,
                            letterSpacing = 3.sp) ,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .clickable(onClick = {navController.navigate("otherscreenprofile/${item.nickname}")}))
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        if (user != null && clicked == false) {
                            socialViewModel.followUser(user.uid, item.id)
                            clicked = true
                        }
                        else if (user != null && clicked == true){
                            socialViewModel.unfollowUser(user.uid, item.id)
                            clicked = false
                        }
                    }) {
                        Icon(painter = if (!clicked) {painterResource(id = R.drawable.personadd)} else {painterResource(id = R.drawable.personcheck)},
                            contentDescription =null ,
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.CenterVertically),
                            tint = Color.White ,)
                    }
                }

                Spacer(modifier = Modifier.size(30.dp))
            }

        }
    }
}

