package ui.mainpages.inside

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import viewmodel.AuthViewModel
import viewmodel.SocialViewModel

@Composable
fun FindUsersScreen(
    navController: NavController,
    socialViewModel: SocialViewModel,
    authViewModel: AuthViewModel
) {
    val currentUserId = Firebase.auth.currentUser?.uid
    val myNickname by socialViewModel.nickname.collectAsState()

    val allUsers by remember { socialViewModel.getAllUsers() }
        .collectAsState(initial = emptyList())
    val myFollowing by remember(myNickname) {
        socialViewModel.getFollowing(myNickname)
    }.collectAsState(initial = emptyList())

    var searchQuery by remember { mutableStateOf("") }

    val discoverList = remember(allUsers, myNickname, currentUserId, searchQuery) {
        allUsers
            .filter {
                it.nickname.isNotBlank() &&
                    it.id != currentUserId &&
                    it.nickname != myNickname
            }
            .filter {
                searchQuery.isBlank() ||
                    it.nickname.contains(searchQuery, ignoreCase = true) ||
                    it.first.contains(searchQuery, ignoreCase = true)
            }
            .sortedBy { it.nickname.lowercase() }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            FollowListTopBar(
                title = "FIND PEOPLE",
                navController = navController
            )
        },
        containerColor = Color(0xFF121417),
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        when {
            myNickname.isBlank() -> {
                FollowListLoadingState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(16.dp))

                    FollowSearchField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = "Search people..."
                    )

                    Spacer(Modifier.height(8.dp))

                    if (discoverList.isEmpty()) {
                        FollowEmptyState(
                            title = if (searchQuery.isBlank()) "No people to show" else "No results found",
                            subtitle = if (searchQuery.isBlank()) {
                                "Check back later to find new athletes to follow."
                            } else {
                                "Try a different name or username."
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
                        ) {
                            item(key = "discover_header") {
                                FollowSectionHeader(
                                    title = if (searchQuery.isBlank()) "Suggested for you" else "Results",
                                    count = discoverList.size,
                                    accent = searchQuery.isBlank()
                                )
                            }

                            items(
                                items = discoverList,
                                key = { it.id.ifBlank { it.nickname } }
                            ) { user ->
                                val isFollowingThem = myFollowing.contains(user.nickname)
                                FollowUserRow(
                                    user = user,
                                    buttonStyle = if (isFollowingThem) {
                                        FollowButtonStyle.Following
                                    } else {
                                        FollowButtonStyle.Follow
                                    },
                                    onProfileClick = {
                                        openUserProfile(navController, authViewModel, user.nickname)
                                    },
                                    onFollowClick = {
                                        if (myNickname.isBlank()) return@FollowUserRow
                                        if (isFollowingThem) {
                                            socialViewModel.unfollowUser(myNickname, user.nickname)
                                        } else {
                                            socialViewModel.followUser(myNickname, user.nickname)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
