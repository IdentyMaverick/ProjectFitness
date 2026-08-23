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
import viewmodel.AuthViewModel
import viewmodel.SocialViewModel

@Composable
fun ProjectFollowersScreen(
    navController: NavController,
    socialViewModel: SocialViewModel,
    authViewModel: AuthViewModel
) {
    val myNickname by socialViewModel.nickname.collectAsState()

    val allUsers by remember { socialViewModel.getAllUsers() }
        .collectAsState(initial = emptyList())
    val followers by remember(myNickname) {
        socialViewModel.getFollowers(myNickname)
    }.collectAsState(initial = emptyList())
    val following by remember(myNickname) {
        socialViewModel.getFollowing(myNickname)
    }.collectAsState(initial = emptyList())

    var searchQuery by remember { mutableStateOf("") }

    val followersList = remember(allUsers, followers, searchQuery) {
        allUsers
            .filter { followers.contains(it.nickname) }
            .filter {
                searchQuery.isBlank() ||
                    it.nickname.contains(searchQuery, ignoreCase = true) ||
                    it.first.contains(searchQuery, ignoreCase = true)
            }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            FollowListTopBar(
                title = "FOLLOWERS",
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
                        placeholder = "Search followers..."
                    )

                    Spacer(Modifier.height(8.dp))

                    if (followersList.isEmpty()) {
                        FollowEmptyState(
                            title = if (searchQuery.isBlank()) "No followers yet" else "No results found",
                            subtitle = if (searchQuery.isBlank()) {
                                "When people follow you, they'll appear here."
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
                            item(key = "followers_header") {
                                FollowSectionHeader(
                                    title = "Followers",
                                    count = followersList.size
                                )
                            }

                            items(
                                items = followersList,
                                key = { it.id.ifBlank { it.nickname } }
                            ) { user ->
                                val isFollowingThem = following.contains(user.nickname)
                                FollowUserRow(
                                    user = user,
                                    buttonStyle = if (isFollowingThem) {
                                        FollowButtonStyle.Following
                                    } else {
                                        FollowButtonStyle.FollowBack
                                    },
                                    onProfileClick = {
                                        openUserProfile(navController, authViewModel, user.nickname)
                                    },
                                    onFollowClick = {
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
