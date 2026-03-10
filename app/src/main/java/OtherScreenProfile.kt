import android.util.Log
import androidx.annotation.Keep
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import com.grozzbear.R
import data.local.viewmodel.OldWorkoutDetailsViewModel
import ui.mainpages.inside.HomeTopBarProfile
import viewmodel.AuthViewModel
import viewmodel.ProfileViewModel

@Keep
data class WorkoutCompleteUser(
    var workoutname: String,
    var workoutdate: String,
    var durationminutes: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherScreenProfile(
    navController: NavController,
    socialViewModel: SocialViewModel,
    nickname: String,
    profileViewModel: ProfileViewModel,
    oldWorkoutDetailsViewModel: OldWorkoutDetailsViewModel,
    authViewModel: AuthViewModel
) {
    val db = FirebaseFirestore.getInstance()
    val myNickname by socialViewModel.nickname.collectAsState()
    val config = LocalConfiguration.current
    val screenHeightDp = config.screenHeightDp.dp

    val user by socialViewModel.getUserByNicknameLive(nickname).observeAsState()

    // Takip verileri (Kullanıcı nickname'ine göre)
    val otherFollowers by socialViewModel.getFollowers(nickname)
        .observeAsState(initial = emptyList())
    val otherFollowing by socialViewModel.getFollowing(nickname)
        .observeAsState(initial = emptyList())
    val myFollowingList by socialViewModel.getFollowing(myNickname)
        .observeAsState(initial = emptyList())

    val isFollowing = myFollowingList.contains(nickname)
    Log.d("Following list", myFollowingList.toString())
    var workoutList by remember { mutableStateOf<List<WorkoutCompleteUser>>(emptyList()) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("Stats", "Activity")
    val scrollState = rememberScrollState()
    val workoutHistoryFull by profileViewModel.workoutHistoryFull.collectAsState()

    // Antrenmanları çekme (ownerUid filtresiyle)
    LaunchedEffect(user?.id) {
        user?.id?.let { userId ->
            db.collection("googlecloudworkouts")
                .whereEqualTo("ownerUid", userId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    workoutList = querySnapshot.map { doc ->
                        WorkoutCompleteUser(
                            workoutname = doc.getString("workoutName") ?: "Unnamed",
                            workoutdate = doc.getString("workoutdate") ?: "N/A",
                            durationminutes = doc.getString("durationminutes") ?: "0"
                        )
                    }
                }
        }
    }

    if (user == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121417)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFFF1C40F))
        }
    } else {
        user?.let { currentUser ->
            profileViewModel.setUserId(currentUser.id)
            profileViewModel.loadUserWorkouts(currentUser.nickname)
            val userHistory by profileViewModel.userHistory.collectAsState()
            authViewModel.loadOtherUserStats(currentUser.id)
            val target by authViewModel.target.collectAsState()

            Scaffold(
                topBar = { HomeTopBarProfile(navController) },
                containerColor = Color(0xFF121417)
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // --- PROFİL ÜST KISIM ---
                    Spacer(modifier = Modifier.height(20.dp))
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .border(4.dp, Color(0xFFF1C40F), CircleShape)
                            .padding(4.dp)
                            .border(2.dp, Color.Black, CircleShape)
                            .padding(4.dp)
                    ) {
                        AsyncImage(
                            model = currentUser.userPhotoUri.ifEmpty { R.drawable.grozzholdsdumbbellbothhandsnobackgroundxml },
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Text(
                        currentUser.first,
                        color = Color.White,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                    Text("@${currentUser.nickname}", color = Color(0xFFF1C40F), fontSize = 15.sp)
                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = {
                            if (isFollowing) socialViewModel.unfollowUser(
                                myNickname,
                                nickname
                            ) else socialViewModel.followUser(myNickname, nickname)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(15.dp),
                        modifier = Modifier
                            .width(100.dp)
                            .border(1.dp, Color(0xFFF1C40F), RoundedCornerShape(15.dp)),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = if (isFollowing) "UNFOLLOW" else "FOLLOW",
                            style = TextStyle(fontFamily = FontFamily(Font(R.font.lexendbold)))
                        )
                    }

                    // --- TAKİPÇİ SAYILARI ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(label = "FOLLOWERS", count = otherFollowers.size) {
                            navController.navigate("projectfollowersscreen")
                        }
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(40.dp)
                                .background(Color.DarkGray)
                        )
                        StatItem(label = "FOLLOWING", count = otherFollowing.size) {
                            navController.navigate("projectfollowscreen")
                        }
                    }

                    // --- SEKMELER (TAB ROW) ---
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                            .fillMaxWidth()
                            .height(30.dp)
                            .clip(RoundedCornerShape(20.dp)) // Dış çerçeve oval yapısı
                            .background(Color.Gray.copy(alpha = 0.1f)) // Arka plan açık gri/mavi tonu
                    ) {
                        SecondaryTabRow(
                            selectedTabIndex = selectedTabIndex,
                            containerColor = Color.Transparent,
                            divider = {},
                            indicator = {},
                            modifier = Modifier.fillMaxSize()
                        ) {
                            tabTitles.forEachIndexed { index, title ->
                                val isSelected = selectedTabIndex == index

                                Tab(
                                    selected = isSelected,
                                    onClick = { selectedTabIndex = index },
                                    modifier = Modifier
                                        .padding(horizontal = 0.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .then(
                                            if (isSelected) Modifier.background(Color(0xFFF1C40F)) // Seçili olanın beyaz arka planı
                                            else Modifier
                                        ),
                                    text = {
                                        Text(
                                            text = title,
                                            style = TextStyle(
                                                fontFamily = FontFamily(Font(R.font.lexendbold)),
                                                fontSize = 15.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            ),
                                            color = if (isSelected) Color.Black else Color(
                                                0xFF4B5F71
                                            ) // Seçili Turuncu, seçili değilse Koyu Gri
                                        )
                                    }
                                )
                            }
                        }
                    }
//                    if (selectedTabIndex == 0) {
//
//                        Column(
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            modifier = Modifier.fillMaxSize()
//                        ) {
//                            Spacer(Modifier.height(10.dp))
//                            Row(verticalAlignment = Alignment.CenterVertically,
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(horizontal = 20.dp)) {
//                                Text(
//                                    text = "Recent Achievements",
//                                    textAlign = TextAlign.Start,
//                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
//                                    fontWeight = FontWeight.Bold,
//                                    style = TextStyle(letterSpacing = 0.sp, fontSize = 20.sp),
//                                    color = Color.White.copy(alpha = 1f),
//                                    modifier = Modifier
//                                )
//                                Spacer(Modifier.weight(1f))
//                                Text(
//                                    text = "View All",
//                                    textAlign = TextAlign.Start,
//                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
//                                    fontWeight = FontWeight.Bold,
//                                    style = TextStyle(letterSpacing = 0.sp, fontSize = 14.sp),
//                                    color = Color(0xFFF1C40F),
//                                    modifier = Modifier
//                                )
//                            }
//                            Spacer(modifier = Modifier.height(10.dp))
//                            Row(
//                                modifier = Modifier.fillMaxWidth(),
//                                horizontalArrangement = Arrangement.Start
//                            ) {
//                                Column(
//                                    verticalArrangement = Arrangement.Center,
//                                    horizontalAlignment = Alignment.CenterHorizontally
//                                ) {
//                                    Box(
//                                        modifier = Modifier
//                                            .padding(horizontal = 20.dp)
//                                            .height(100.dp)
//                                            .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
//                                        contentAlignment = Alignment.Center
//                                    ){
//                                        Box(
//                                            modifier = Modifier
//                                                .padding(horizontal = 25.dp)
//                                                .size(60.dp),
//                                            contentAlignment = Alignment.Center
//                                        ) {
//                                            Box(
//                                                modifier = Modifier
//                                                    .background(Color(0xFFF1C40F).copy(alpha = 0.1f), RoundedCornerShape(20.dp))
//                                                    .padding(horizontal = 0.dp)
//                                                    .size(70.dp),
//                                                contentAlignment = Alignment.Center
//                                            ) {
//                                                Icon(
//                                                    painter = painterResource(R.drawable.addcircle),
//                                                    contentDescription = null,
//                                                    tint = Color(0xFFF1C40F),
//                                                    modifier = Modifier
//                                                        .size(30.dp)
//                                                )
//                                            }
//                                        }
//                                    }
//                                    Spacer(Modifier.height(10.dp))
//                                    Text(
//                                        text = "No Achivement",
//                                        textAlign = TextAlign.Center,
//                                        color = Color.Gray.copy(alpha = 1f),
//                                        fontFamily = FontFamily(Font(R.font.lexendbold)),
//                                        fontWeight = FontWeight.Bold
//                                    )
//                                }
//
//
//                            }
//                        }
//                    }
                    if (selectedTabIndex == 0) {
                        Spacer(Modifier.height(10.dp))
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                        ) {
                            Text(
                                text = "Weekly Volume",
                                textAlign = TextAlign.Start,
                                fontFamily = FontFamily(Font(R.font.lexendbold)),
                                fontWeight = FontWeight.Bold,
                                style = TextStyle(letterSpacing = 0.sp, fontSize = 20.sp),
                                color = Color.White.copy(alpha = 1f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 25.dp, vertical = 0.dp)
                            )
                            Spacer(Modifier.height(20.dp))
                            Box(
                                modifier = Modifier
                                    .height(150.dp)
                                    .fillMaxWidth()
                                    .padding(horizontal = 25.dp)
                                    .background(
                                        color = Color(0xFF202B36).copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                            ) {

                            }
                            Spacer(Modifier.height(20.dp))
                            Text(
                                text = "Lifetime Statistics",
                                textAlign = TextAlign.Start,
                                fontFamily = FontFamily(Font(R.font.lexendbold)),
                                fontWeight = FontWeight.Bold,
                                style = TextStyle(letterSpacing = 0.sp, fontSize = 20.sp),
                                color = Color.White.copy(alpha = 1f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 25.dp)
                            )
                            Spacer(Modifier.height(20.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 25.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = Color(0xFF121417).copy(alpha = 0.4f),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .size(150.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = target.count.toString(),
                                            textAlign = TextAlign.Center,
                                            fontFamily = FontFamily(Font(R.font.lexendbold)),
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 48.sp
                                        )
                                        Text(
                                            text = "WORKOUTS",
                                            textAlign = TextAlign.Center,
                                            fontFamily = FontFamily(Font(R.font.lexendbold)),
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "COMPLETED",
                                            textAlign = TextAlign.Center,
                                            fontFamily = FontFamily(Font(R.font.lexendbold)),
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFF1C40F)
                                        )
                                    }
                                }
                                Spacer(Modifier.width(20.dp))
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = Color(0xFF121417).copy(alpha = 0.4f),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .size(150.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = target.weight.toString(),
                                            textAlign = TextAlign.Center,
                                            fontFamily = FontFamily(Font(R.font.lexendbold)),
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 40.sp
                                        )
                                        Text(
                                            text = "KG",
                                            textAlign = TextAlign.Center,
                                            fontFamily = FontFamily(Font(R.font.lexendbold)),
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "WEIGHT",
                                            textAlign = TextAlign.Center,
                                            fontFamily = FontFamily(Font(R.font.lexendbold)),
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "LIFTED",
                                            textAlign = TextAlign.Center,
                                            fontFamily = FontFamily(Font(R.font.lexendbold)),
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFF1C40F)
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 25.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = Color(0xFF121417).copy(alpha = 0.4f),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .size(150.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = target.time.toString(),
                                            textAlign = TextAlign.Center,
                                            fontFamily = FontFamily(Font(R.font.lexendbold)),
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 48.sp
                                        )
                                        Text(
                                            text = "MINUTES",
                                            textAlign = TextAlign.Center,
                                            fontFamily = FontFamily(Font(R.font.lexendbold)),
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "SPENT FOR",
                                            textAlign = TextAlign.Center,
                                            fontFamily = FontFamily(Font(R.font.lexendbold)),
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "WORKOUTS",
                                            textAlign = TextAlign.Center,
                                            fontFamily = FontFamily(Font(R.font.lexendbold)),
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFF1C40F)
                                        )
                                    }
                                }
                            }
                        }
                    } else if (selectedTabIndex == 1) {
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = "Last Activity",
                            textAlign = TextAlign.Start,
                            fontFamily = FontFamily(Font(R.font.lexendbold)),
                            fontWeight = FontWeight.Bold,
                            style = TextStyle(letterSpacing = 0.sp, fontSize = 20.sp),
                            color = Color.White.copy(alpha = 1f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 25.dp)
                        )
                    }
                    Spacer(Modifier.height(20.dp))
                    if (userHistory.isEmpty()) {
                        Column(
                            Modifier
                                .padding(horizontal = 30.dp)
                                .fillMaxSize()
                                .background(
                                    Color.Gray.copy(alpha = 0.1f),
                                    RoundedCornerShape(20.dp)
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(Modifier.height(20.dp))
                            Text(
                                text = "No workout history yet",
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily(Font(R.font.lexendregular)),
                                fontWeight = FontWeight.Bold,
                                style = TextStyle(letterSpacing = 0.sp, fontSize = 15.sp),
                                color = Color.Gray.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .padding(horizontal = 20.dp, vertical = 20.dp)
                            )
                        }
                    } else {
                        LazyColumn() {
                            itemsIndexed(userHistory) { index, item ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 25.dp)
                                            .background(
                                                color = Color(0xFF202B36).copy(alpha = 0.4f),
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .height(60.dp)
                                            .clickable(onClick = {
                                                oldWorkoutDetailsViewModel._targetUserId.value =
                                                    currentUser.id
                                                oldWorkoutDetailsViewModel._sessionId.value =
                                                    item.sessionId
                                                oldWorkoutDetailsViewModel._flag.value = true
                                                navController.navigate("oldworkoutdetails")
                                            })
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(horizontal = 20.dp),
                                            horizontalArrangement = Arrangement.Start,
                                            verticalAlignment = Alignment.CenterVertically

                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.dumbbellicon128),
                                                contentDescription = null,
                                                tint = Color(0xFFF1C40F),
                                                modifier = Modifier
                                                    .size(30.dp)
                                            )
                                            Spacer(Modifier.width(20.dp))
                                            Column() {
                                                Text(
                                                    text = "${item.workoutName}",
                                                    style = TextStyle(
                                                        fontSize = 15.sp,
                                                        fontFamily = FontFamily(
                                                            Font(R.font.lexendbold)
                                                        ),
                                                        color = Color.White
                                                    )
                                                )
                                            }
                                            Spacer(Modifier.weight(1f))
                                            Icon(
                                                painter = painterResource(R.drawable.keyboarddoublearrowright),
                                                contentDescription = null,
                                                tint = Color(0xFFF1C40F),
                                                modifier = Modifier
                                                    .size(25.dp)
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
}

@Composable
fun StatItem(label: String, count: Int, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Text(text = "$count", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = Color.Gray, fontSize = 11.sp)
    }
}

@Composable
fun SuccessContent() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Recent Achievements",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))
        // Mevcut başarı tasarımın buraya...
        Text("No Achievements yet.", color = Color.Gray)
    }
}

@Composable
fun StatsContent() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Weekly Volume", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Box(
            modifier = Modifier
                .height(150.dp)
                .fillMaxWidth()
                .background(Color.DarkGray, RoundedCornerShape(10.dp))
        )
    }
}