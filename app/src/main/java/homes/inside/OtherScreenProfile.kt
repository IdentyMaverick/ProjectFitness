
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.projectfitness.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage


data class WorkoutCompleteUser(
    var workoutname : String ,
    var workoutdate : String ,
    var durationminutes : String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherScreenProfile(navController: NavController, socialViewModel: SocialViewModel, nickname: String) {
    val db = FirebaseFirestore.getInstance()
    val uid = Firebase.auth.currentUser?.uid
    val config = LocalConfiguration.current
    val screenHeightDp = config.screenHeightDp.dp
    val storageRef = Firebase.storage.reference
    val user by socialViewModel.getUserByNicknameLive(nickname).observeAsState()
    val getFollowers by socialViewModel.getFollowers(uid ?: "").observeAsState(initial = emptyList())
    val getFollowing by socialViewModel.getFollowing(uid ?: "").observeAsState(initial = emptyList())
    val getFollowersOther by socialViewModel.getFollowers(user?.id ?: "").observeAsState(initial = emptyList())
    val getFollowingOther by socialViewModel.getFollowing(user?.id ?: "").observeAsState(initial = emptyList())
    var numberOfFollows = getFollowingOther.size
    var numberOfFollowers = getFollowersOther.size
    var userCompletedWorkout : CollectionReference
    var userCompletedW = mutableListOf("")
    //Firebase Data Variables
    var workoutList by remember { mutableStateOf<MutableList<WorkoutCompleteUser>>(mutableListOf()) }
    //

    if (user != null) {
        userCompletedWorkout = db.collection("completedWorkouts").document(user?.id ?: "").collection("completedWorkouts")
        userCompletedWorkout.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val workout = WorkoutCompleteUser(
                        document.getString("completedWorkoutName").orEmpty(),
                        document.getString("completionDate").orEmpty(),
                        document.getString("durationMinutes").orEmpty()
                    )
                    workoutList.add(workout)
                }
                // Güncellenmiş workoutList'i kullanabiliriz
                Log.d("Datas", workoutList.toString())
            }
            .addOnFailureListener { documents ->
                Log.w("document", documents.toString())
            }
    }
    Log.d("Datas", workoutList.toString())


    val profileRef by remember(user) {
        derivedStateOf {
            user?.id?.let { userId ->
                storageRef.child("gs://projectfitness-ddfeb.appspot.com/profile_photos/$userId/profile.jpg")
            }
        }
    }

    var imageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(profileRef) {
        profileRef?.downloadUrl?.addOnSuccessListener { uri ->
            imageUrl = uri.toString()
            Log.d("profileRef3", imageUrl.toString())
            Log.d("profileRef4", profileRef.toString())
            Log.d("Firebase", "Success to Download image URL")
        }?.addOnFailureListener { exception ->
            Log.d("profileRef3", imageUrl.toString())
            Log.d("profileRef4", profileRef.toString())
            Log.e("Firebase", "Failed to download image URL", exception)
        }
    }

    user?.let {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFF1C40F), Color(0xFF181F26)),
                        start = Offset(0f, 0f),
                        end = Offset(0f, (screenHeightDp * 1.5f).value)
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
            ) {
                IconButton(
                    onClick = { navController.navigate("projectfollowscreen") },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.projectfitnessprevious),
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.CenterVertically),
                        tint = Color.Black
                    )
                }
                Text(
                    text = "PROJECT FITNESS",
                    style = TextStyle(
                        fontSize = 35.sp,
                        fontFamily = FontFamily(
                            Font(R.font.postnobillscolombosemibold)
                        ),
                        letterSpacing = 3.sp
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 10.dp)
                )
                IconButton(onClick = { }, modifier = Modifier.align(Alignment.CenterVertically)) {
                    Icon(
                        painter = painterResource(id = R.drawable.circlenotifications),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(30.dp),
                        tint = Color.Black
                    )
                }
            }
            Column(
                modifier = Modifier
                    .padding(top = 100.dp)
                    .fillMaxWidth()
                    .height(500.dp)
                    .background(Color.Transparent)
            ) {
                Image(
                    painter = if (imageUrl != null) {
                        rememberAsyncImagePainter(model = imageUrl, onError = { error ->
                            Log.e("Image Loading Error", "Error: $error")
                        })
                    } else {
                        painterResource(id = R.drawable.secondinfo)
                    },
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(BorderStroke(1.dp, Color.White), CircleShape)
                        .align(Alignment.CenterHorizontally)
                )
                Text(text = user?.first.toString() , style = TextStyle(fontSize = 20.sp ,
                    letterSpacing = 3.sp ,
                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold
                    )) ,
                    color = Color.White
                ) ,
                    modifier = Modifier.align(Alignment.CenterHorizontally))
                Text(text = "@" + user?.nickname.toString() , style = TextStyle(fontSize = 20.sp ,
                    letterSpacing = 3.sp ,
                    fontFamily = FontFamily(Font(R.font.postnobillscolombolight
                    ))
                ) ,
                    modifier = Modifier.align(Alignment.CenterHorizontally) ,
                    color = Color.White)
                Spacer(modifier = Modifier.size(20.dp))
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)) {
                    Text(
                        text = "Followers $numberOfFollowers",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(bottom = 10.dp, start = 40.dp)
                            .clickable(onClick = {}),
                        color = Color(0xFFD9D9D9),
                        fontFamily = FontFamily(Font(R.font.postnobillscolombolight)),
                        fontSize = 20.sp,
                        style = TextStyle(letterSpacing = 3.sp) ,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "Follows $numberOfFollows" ,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(bottom = 30.dp, end = 40.dp)
                            .clickable(onClick = {}),
                        color = Color(0xFFD9D9D9),
                        fontFamily = FontFamily(Font(R.font.postnobillscolombolight)),
                        fontSize = 20.sp,
                        style = TextStyle(letterSpacing = 3.sp)
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
                Button(onClick = {if (getFollowing.contains(user?.id)){socialViewModel.unfollowUser(uid ?: "" , user?.id ?: "")} else {socialViewModel.followUser(uid ?: "" ,  user?.id ?: "")}
                                 }  ,
                    modifier = Modifier
                        .width(137.dp)
                        .height(25.dp)
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF181F26)) ,
                    contentPadding = PaddingValues(0.dp) ,
                    shape = RoundedCornerShape(5.dp) ,
                    border = BorderStroke(0.5.dp , Color.White)) {
                    Text(text = if (getFollowing.contains(user?.id)){"Unfollow"} else{"Follow"}
                        , style = TextStyle(
                        fontSize = 15.sp ,
                        letterSpacing = 3.sp ,
                        fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold))))
                }
                var tabTitles = listOf("Activity","Profile")
                var selectedTabIndex by remember { mutableStateOf(0) }
                Spacer(modifier = Modifier.size(20.dp))
                SecondaryTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color(0xFF181F26),
                    contentColor = Color.White,
                    indicator = {
                        // TabRowDefaults.SecondaryIndicator(color = Color.Cyan)
                        TabRowDefaults.SecondaryIndicator(color = Color(0xFFF1C40F), height = 1.5f.dp, modifier = Modifier
                            .tabIndicatorOffset(selectedTabIndex))
                    }) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                if (selectedTabIndex == index){
                                    Text(
                                        text = title,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        style = TextStyle(fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                            fontSize = 15.sp,
                                            letterSpacing = 3.sp),
                                        color = Color(0xFFF1C40F))
                                }
                                else {
                                    Text(
                                        text = title,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        style = TextStyle(fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                            fontSize = 15.sp,
                                            letterSpacing = 3.sp),
                                        color = Color.White)
                                }
                            }
                        )
                    }
                }

                if (selectedTabIndex == 0) {

                    if (workoutList.isEmpty()){
                        Text(
                            text = "User hasn't workout yet.",
                            fontFamily = FontFamily(Font(R.font.postnobillscolombo)),
                            fontWeight = FontWeight.Bold,
                            style = TextStyle(letterSpacing = 1.sp, fontSize = 15.sp),
                            color = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier
                                .padding(top = 100.dp)
                                .align(Alignment.CenterHorizontally))}

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 30.dp),
                        state = LazyListState()
                    ) {
                        itemsIndexed(workoutList) { index, item ->
                            Box(
                                modifier = Modifier
                                    .padding(start = 15.dp, end = 15.dp)
                                    .height(93.dp)
                                    .fillMaxHeight()
                                    .border(
                                        1.dp,
                                        shape = RoundedCornerShape(20.dp),
                                        color = Color(0xFF202B36)
                                    )
                            )
                            {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .padding(start = 5.dp)
                                        .width(80.dp)
                                        .height(80.dp)
                                        .clip(shape = RoundedCornerShape(20.dp))
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color(0xFF181F26)  // End color
                                                ),
                                                start = Offset.Infinite,
                                                end = Offset(100.0f, 0.0f)
                                            ),
                                            shape = RoundedCornerShape(20.dp)
                                        )

                                )
                                {
                                    if (index == 0) {
                                        Image(
                                            painterResource(id = R.drawable.secondinfo),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            alpha = 0.7f,
                                            modifier = Modifier
                                                .fillMaxSize(),
                                        )
                                    } else if (index == 1) {
                                        Image(
                                            painterResource(id = R.drawable.login),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            alpha = 0.7f,
                                            modifier = Modifier
                                                .fillMaxSize(),
                                        )
                                    }
                                    else if (index == 2) {
                                        Image(
                                            painterResource(id = R.drawable.gym),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            alpha = 0.7f,
                                            modifier = Modifier
                                                .fillMaxSize(),
                                        )
                                    }
                                    else if (index == 3) {
                                        Image(
                                            painterResource(id = R.drawable.gymroomwith),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            alpha = 0.7f,
                                            modifier = Modifier
                                                .fillMaxSize(),
                                        )
                                    }
                                    else if (index == 4) {
                                        Image(
                                            painterResource(id = R.drawable.gymroomwithgym),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            alpha = 0.7f,
                                            modifier = Modifier
                                                .fillMaxSize(),
                                        )
                                    }
                                    else if (index == 5) {
                                        Image(
                                            painterResource(id = R.drawable.gymroomgym),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            alpha = 0.7f,
                                            modifier = Modifier
                                                .fillMaxSize(),
                                        )
                                    }
                                    else if (index == 6) {
                                        Image(
                                            painterResource(id = R.drawable.login),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            alpha = 0.7f,
                                            modifier = Modifier
                                                .fillMaxSize(),
                                        )
                                    }
                                }
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(end = 15.dp)
                                ) {
                                    Row(modifier = Modifier) {
                                        Text(
                                            text = item.durationminutes,
                                            modifier = Modifier
                                                .padding(start = 100.dp , top = 7.dp),
                                            fontSize = 15.sp,
                                            fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                            textAlign = TextAlign.Left,
                                            color = Color(0xFFD9D9D9),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Row(modifier = Modifier) {
                                        Text(
                                            text = item.workoutname.uppercase(),
                                            modifier = Modifier
                                                .padding(start = 100.dp , top = 0.dp),
                                            fontSize = 20.sp,
                                            fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                            textAlign = TextAlign.Left,
                                            color = Color(0xFFD9D9D9),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Icon(painter = painterResource(id = R.drawable.keyboarddoublearrowright),
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier
                                                .padding(top = 0.dp)
                                                .size(25.dp)
                                                .clickable {}
                                        )
                                    }
                                    Row(modifier = Modifier.padding(bottom = 10.dp)) {
                                        Text(
                                            text = "3 exercise",
                                            modifier = Modifier
                                                .padding(start = 100.dp),
                                            fontSize = 15.sp,
                                            fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                            textAlign = TextAlign.Left,
                                            color = Color(0xFFD9D9D9),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Text(
                                            text = "Completed in " + item.durationminutes,
                                            modifier = Modifier
                                                .padding(start = 0.dp),
                                            fontSize = 15.sp,
                                            fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                            textAlign = TextAlign.Left,
                                            color = Color(0xFFD9D9D9),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                            }
                        }
                    }

                }

            }

        }
    }
}
