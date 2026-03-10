package ui.mainpages.inside

import SocialViewModel
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.grozzbear.R
import viewmodel.AuthViewModel

@Composable
fun ProjectFollowScreen(
    navController: NavController,
    socialViewModel: SocialViewModel,
    authViewModel: AuthViewModel
) {
    val user = Firebase.auth.currentUser
    val context = LocalContext.current
    val myNickname by socialViewModel.nickname.collectAsState()

    val getAllUserState by socialViewModel.getAllUsers().observeAsState(initial = emptyList())
    val getFollowing by socialViewModel.getFollowing(myNickname)
        .observeAsState(initial = emptyList())

    val searchedText = remember { mutableStateOf("") }

    // Takip ettiklerini filtrele
    val myFollowingList = getAllUserState.filter {
        it.nickname.isNotEmpty() && getFollowing.contains(it.nickname)
    }.filter {
        it.nickname.contains(searchedText.value, ignoreCase = true) ||
                it.first.contains(searchedText.value, ignoreCase = true)
    }

    // Öneriler (Rastgele 5 kişi, ben değilim ve takip etmiyorum)
    val recommendations = remember(getAllUserState, getFollowing) {
        getAllUserState
            .filter { it.nickname.isNotEmpty() && it.id != user?.uid && !getFollowing.contains(it.nickname) && it.nickname != myNickname }
            .shuffled()
            .take(5)
    }

    Scaffold(
        topBar = { HomeTopBarProfileFollow(navController) },
        containerColor = Color(0xFF121417),
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                SearchBoxFollow(searchedText)
                Spacer(modifier = Modifier.height(20.dp))
            }

            if (myFollowingList.isNotEmpty()) {
                item {
                    Text(
                        "Following",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 25.dp, vertical = 10.dp)
                    )
                }
                items(myFollowingList) { item ->
                    UserRowItem(
                        item,
                        true,
                        myNickname,
                        socialViewModel,
                        navController,
                        context,
                        authViewModel
                    )
                }
            }

            if (searchedText.value.isEmpty() && recommendations.isNotEmpty()) {
                item {
                    Text(
                        "Suggested for you",
                        color = Color(0xFFF1C40F),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 25.dp, vertical = 10.dp)
                    )
                }
                items(recommendations) { item ->
                    UserRowItem(
                        item,
                        false,
                        myNickname,
                        socialViewModel,
                        navController,
                        context,
                        authViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun UserRowItem(
    item: data.remote.User,
    isFollowing: Boolean,
    myNickname: String,
    socialViewModel: SocialViewModel,
    navController: NavController,
    context: android.content.Context,
    authViewModel: AuthViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .border(2.dp, Color(0xFFF1C40F), CircleShape)
                .padding(2.dp)
                .border(2.dp, Color.Black, CircleShape)
                .padding(4.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.userPhotoUri.ifEmpty { R.drawable.grozzholdsdumbbellbothhandsnobackgroundxml })
                    .crossfade(true).build(),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .clickable {
                        authViewModel._totalWorkoutNumber.value = 0
                        authViewModel._totalLiftedWeight.value = 0F
                        navController.navigate("otherscreenprofile/${item.nickname}")
                    },
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.width(15.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.first, color = Color.White, fontSize = 18.sp)
            Text(text = "@${item.nickname}", color = Color(0xFFF1C40F), fontSize = 13.sp)
        }
        Button(
            onClick = {
                if (isFollowing) socialViewModel.unfollowUser(myNickname, item.nickname)
                else socialViewModel.followUser(myNickname, item.nickname)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isFollowing) Color.White.copy(
                    alpha = 0.1f
                ) else Color(0xFFF1C40F)
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.height(35.dp)
        ) {
            Text(
                if (isFollowing) "Following" else "Follow",
                color = if (isFollowing) Color.White else Color.Black,
                fontSize = 11.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBoxFollow(text: MutableState<String>) {
    TextField(
        value = text.value,
        onValueChange = { text.value = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(56.dp),
        placeholder = { Text("Search following...", color = Color.White.copy(alpha = 0.3f)) },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.4f)
            )
        },
        shape = RoundedCornerShape(15.dp),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF21282F),
            unfocusedContainerColor = Color(0xFF21282F),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        textStyle = TextStyle(color = Color.White, fontSize = 14.sp)
    )
}

@Composable
fun HomeTopBarProfileFollow(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                painterResource(R.drawable.left),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }
        Spacer(Modifier.weight(1f))
        Text(
            "FOLLOWING",
            color = Color.White,
            fontFamily = FontFamily(Font(R.font.oswaldbold)),
            fontSize = 20.sp
        )
        Spacer(Modifier.weight(1f))
        Spacer(Modifier.size(48.dp))
    }
}