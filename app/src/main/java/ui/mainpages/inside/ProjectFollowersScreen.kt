package ui.mainpages.inside

import SocialViewModel
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import com.grozzbear.R
import viewmodel.AuthViewModel

@Composable
fun ProjectFollowersScreen(
    navController: NavController,
    socialViewModel: SocialViewModel,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val myNickname by socialViewModel.nickname.collectAsState()

    val getAllUserState by socialViewModel.getAllUsers().observeAsState(initial = emptyList())
    val getFollowers by socialViewModel.getFollowers(myNickname)
        .observeAsState(initial = emptyList())
    val getFollowing by socialViewModel.getFollowing(myNickname)
        .observeAsState(initial = emptyList())

    val searchedText = remember { mutableStateOf("") }

    // Takipçi listesini filtrele
    val myFollowersList = remember(getAllUserState, getFollowers, searchedText.value) {
        getAllUserState.filter { userItem ->
            getFollowers.contains(userItem.nickname)
        }.filter {
            it.nickname.contains(searchedText.value, ignoreCase = true) ||
                    it.first.contains(searchedText.value, ignoreCase = true)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { HomeTopBarProfileFollowers(navController) },
        containerColor = Color(0xFF121417),
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            SearchBoxFollowers(searchedText)

            if (myFollowersList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (searchedText.value.isEmpty()) "No followers yet." else "No results found.",
                        color = Color.White.copy(alpha = 0.5f),
                        fontFamily = FontFamily(Font(R.font.lexendregular))
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 20.dp, bottom = 20.dp)
                ) {
                    items(myFollowersList) { item ->
                        val iAmFollowingThem = getFollowing.contains(item.nickname)

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
                                        .crossfade(true)
                                        .build(),
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
                                Text(
                                    text = "@${item.nickname}",
                                    color = Color(0xFFF1C40F),
                                    fontSize = 13.sp
                                )
                            }

                            Button(
                                onClick = {
                                    if (iAmFollowingThem) {
                                        socialViewModel.unfollowUser(myNickname, item.nickname)
                                        Toast.makeText(
                                            context,
                                            "Unfollowed ${item.nickname}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        socialViewModel.followUser(myNickname, item.nickname)
                                        Toast.makeText(
                                            context,
                                            "Following ${item.nickname}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (iAmFollowingThem) Color.White.copy(alpha = 0.1f) else Color(
                                        0xFFF1C40F
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text(
                                    text = if (iAmFollowingThem) "Following" else "Follow Back",
                                    color = if (iAmFollowingThem) Color.White else Color.Black,
                                    fontSize = 11.sp,
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

@Composable
fun HomeTopBarProfileFollowers(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = topPadding)
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
            "FOLLOWERS",
            color = Color.White,
            fontFamily = FontFamily(Font(R.font.oswaldbold)),
            fontSize = 20.sp
        )
        Spacer(Modifier.weight(1f))
        Spacer(Modifier.size(48.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBoxFollowers(text: MutableState<String>) {
    TextField(
        value = text.value,
        onValueChange = { text.value = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(56.dp),
        placeholder = { Text("Search followers...", color = Color.White.copy(alpha = 0.3f)) },
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