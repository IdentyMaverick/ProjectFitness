package ui.mainpages.inside

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ui.mainpages.navigation.Screens
import viewmodel.AuthViewModel
import viewmodel.SocialViewModel

@Composable
fun ProjectFollowScreen(
    navController: NavController,
    socialViewModel: SocialViewModel,
    authViewModel: AuthViewModel,
    listOwnerNickname: String
) {
    val currentUserId = Firebase.auth.currentUser?.uid
    val myNickname by socialViewModel.nickname.collectAsState()
    val ownerNickname = listOwnerNickname.ifBlank { myNickname }
    val isOwnList = ownerNickname.isNotBlank() && ownerNickname == myNickname

    val allUsers by remember { socialViewModel.getAllUsers() }
        .collectAsState(initial = emptyList())
    val followingNicknames by remember(ownerNickname) {
        socialViewModel.getFollowing(ownerNickname)
    }.collectAsState(initial = emptyList())
    val myFollowing by remember(myNickname) {
        socialViewModel.getFollowing(myNickname)
    }.collectAsState(initial = emptyList())

    var searchQuery by remember { mutableStateOf("") }

    val followingList = remember(allUsers, followingNicknames, searchQuery) {
        allUsers
            .filter { it.nickname.isNotBlank() && followingNicknames.contains(it.nickname) }
            .filter {
                searchQuery.isBlank() ||
                    it.nickname.contains(searchQuery, ignoreCase = true) ||
                    it.first.contains(searchQuery, ignoreCase = true)
            }
    }

    val recommendations = remember(allUsers, myFollowing, myNickname, currentUserId, isOwnList) {
        if (!isOwnList) emptyList()
        else allUsers
            .filter {
                it.nickname.isNotBlank() &&
                    it.id != currentUserId &&
                    it.nickname != myNickname &&
                    !myFollowing.contains(it.nickname)
            }
            .shuffled()
            .take(5)
    }

    val showRecommendations = isOwnList && searchQuery.isBlank() && recommendations.isNotEmpty()
    val showEmptyState = followingList.isEmpty() && !showRecommendations

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            FollowListTopBar(
                title = "FOLLOWING",
                navController = navController
            )
        },
        containerColor = Color(0xFF121417),
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        when {
            ownerNickname.isBlank() -> {
                FollowListLoadingState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            showEmptyState -> {
                ColumnWithSearch(
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    FollowEmptyState(
                        title = if (searchQuery.isBlank()) "Not following anyone yet" else "No results found",
                        subtitle = if (searchQuery.isBlank()) {
                            "Accounts this person follows will show up here."
                        } else {
                            "Try a different name or username."
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    item(key = "search") {
                        Spacer(Modifier.height(16.dp))
                        FollowSearchField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = "Search following..."
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    if (followingList.isNotEmpty()) {
                        item(key = "following_header") {
                            FollowSectionHeader(
                                title = "Following",
                                count = followingList.size
                            )
                        }

                        items(
                            items = followingList,
                            key = { it.id.ifBlank { it.nickname } }
                        ) { user ->
                            val isSelf = user.nickname == myNickname
                            val isFollowingThem = myFollowing.contains(user.nickname)
                            FollowUserRow(
                                user = user,
                                buttonStyle = when {
                                    isSelf -> FollowButtonStyle.Following
                                    isOwnList || isFollowingThem -> FollowButtonStyle.Following
                                    else -> FollowButtonStyle.Follow
                                },
                                onProfileClick = {
                                    if (isSelf) {
                                        navController.navigate(Screens.Home.Profile.route) {
                                            launchSingleTop = true
                                        }
                                    } else {
                                        openUserProfile(navController, authViewModel, user.nickname)
                                    }
                                },
                                onFollowClick = {
                                    if (isSelf || myNickname.isBlank()) return@FollowUserRow
                                    if (isOwnList || isFollowingThem) {
                                        socialViewModel.unfollowUser(myNickname, user.nickname)
                                    } else {
                                        socialViewModel.followUser(myNickname, user.nickname)
                                    }
                                }
                            )
                        }
                    } else if (searchQuery.isNotBlank()) {
                        item(key = "following_empty_search") {
                            FollowEmptyState(
                                title = "No results found",
                                subtitle = "Try a different name or username.",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 32.dp)
                            )
                        }
                    }

                    if (showRecommendations) {
                        item(key = "suggested_header") {
                            FollowSectionHeader(
                                title = "Suggested for you",
                                accent = true,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        items(
                            items = recommendations,
                            key = { "suggested_${it.id.ifBlank { it.nickname }}" }
                        ) { user ->
                            FollowUserRow(
                                user = user,
                                buttonStyle = FollowButtonStyle.Follow,
                                onProfileClick = {
                                    openUserProfile(navController, authViewModel, user.nickname)
                                },
                                onFollowClick = {
                                    socialViewModel.followUser(myNickname, user.nickname)
                                }
                            )
                        }
                    }

                    item(key = "bottom_spacer") {
                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ColumnWithSearch(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.Column(modifier = modifier) {
        Spacer(modifier.height(16.dp))
        FollowSearchField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = "Search following..."
        )
        Spacer(modifier.height(8.dp))
        content()
    }
}
