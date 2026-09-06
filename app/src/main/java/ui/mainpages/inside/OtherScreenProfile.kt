package ui.mainpages.inside

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.grozzbear.R
import com.grozzbear.ui.theme.GrozzMuted
import com.grozzbear.ui.theme.GrozzSurface
import com.grozzbear.ui.theme.GrozzSystemBar
import com.grozzbear.ui.theme.GrozzYellow
import com.grozzbear.ui.theme.Lexend
import data.local.viewmodel.OldWorkoutDetailsViewModel
import data.remote.User
import ui.mainpages.navigation.Screens
import viewmodel.AuthViewModel
import viewmodel.ProfileViewModel
import viewmodel.SocialViewModel

private val OtherProfileCardBg = GrozzSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherScreenProfile(
    navController: NavController,
    socialViewModel: SocialViewModel,
    nickname: String,
    profileViewModel: ProfileViewModel,
    oldWorkoutDetailsViewModel: OldWorkoutDetailsViewModel,
    authViewModel: AuthViewModel,
) {
    val myNickname by socialViewModel.nickname.collectAsState()
    val user by socialViewModel.getUserByNicknameLive(nickname).observeAsState()

    val otherFollowers by remember(nickname) {
        socialViewModel.getFollowers(nickname)
    }.collectAsState(initial = emptyList())
    val otherFollowing by remember(nickname) {
        socialViewModel.getFollowing(nickname)
    }.collectAsState(initial = emptyList())
    val myFollowingList by remember(myNickname) {
        socialViewModel.getFollowing(myNickname)
    }.collectAsState(initial = emptyList())

    val isFollowing = myFollowingList.contains(nickname)
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("Stats", "Activity")
    val scrollState = rememberScrollState()
    var isPhotoExpanded by remember { mutableStateOf(false) }
    val blurAlpha by animateDpAsState(
        targetValue = if (isPhotoExpanded) 15.dp else 0.dp,
        animationSpec = tween(durationMillis = 50),
        label = "blurAnimation",
    )

    val userHistory by profileViewModel.userHistory.collectAsState()
    val target by authViewModel.target.collectAsState()

    LaunchedEffect(user?.id, user?.nickname) {
        val current = user ?: return@LaunchedEffect
        profileViewModel.setUserId(current.id)
        profileViewModel.loadUserWorkouts(current.nickname)
        authViewModel.loadOtherUserStats(current.id)
    }

    when (val currentUser = user) {
        null -> {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(GrozzSystemBar),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = GrozzYellow)
            }
        }

        else -> {
            OtherProfileContent(
                navController = navController,
                currentUser = currentUser,
                myNickname = myNickname,
                isFollowing = isFollowing,
                followerCount = otherFollowers.size,
                followingCount = otherFollowing.size,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it },
                tabTitles = tabTitles,
                scrollState = scrollState,
                target = target,
                userHistory = userHistory,
                isPhotoExpanded = isPhotoExpanded,
                onPhotoExpandedChange = { isPhotoExpanded = it },
                blurAlpha = blurAlpha,
                socialViewModel = socialViewModel,
                oldWorkoutDetailsViewModel = oldWorkoutDetailsViewModel,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OtherProfileContent(
    navController: NavController,
    currentUser: User,
    myNickname: String,
    isFollowing: Boolean,
    followerCount: Int,
    followingCount: Int,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    tabTitles: List<String>,
    scrollState: androidx.compose.foundation.ScrollState,
    target: AuthViewModel.UserStats,
    userHistory: List<data.local.entity.WorkoutHistoryEntity>,
    isPhotoExpanded: Boolean,
    onPhotoExpandedChange: (Boolean) -> Unit,
    blurAlpha: androidx.compose.ui.unit.Dp,
    socialViewModel: SocialViewModel,
    oldWorkoutDetailsViewModel: OldWorkoutDetailsViewModel,
) {
    val hasPhoto = currentUser.userPhotoUri.isNotBlank()
    val topBarTitle = currentUser.nickname.ifBlank { "PROFILE" }.uppercase()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            FollowListTopBar(
                title = topBarTitle,
                navController = navController,
                topPadding = if (Build.VERSION.SDK_INT >= 35) 50.dp else 0.dp,
            )
        },
        containerColor = GrozzSystemBar,
        modifier =
            Modifier
                .fillMaxSize()
                .blur(blurAlpha),
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier =
                    Modifier
                        .size(110.dp)
                        .border(4.dp, GrozzYellow, CircleShape)
                        .padding(4.dp)
                        .border(2.dp, Color.Black, CircleShape)
                        .padding(4.dp),
            ) {
                AsyncImage(
                    model =
                        if (hasPhoto) {
                            currentUser.userPhotoUri
                        } else {
                            R.drawable.grozzholdsdumbbellbothhandsnobackgroundxml
                        },
                    contentDescription = "Profile picture",
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .clickable(enabled = hasPhoto) {
                                onPhotoExpandedChange(true)
                            },
                    contentScale = ContentScale.Crop,
                )
            }

            Text(
                text = currentUser.first.ifBlank { currentUser.nickname },
                color = Color.White,
                fontSize = 20.sp,
                fontFamily = Lexend,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier =
                    Modifier
                        .padding(top = 12.dp)
                        .padding(horizontal = 24.dp),
            )
            Text(
                text = "@${currentUser.nickname}",
                color = GrozzYellow,
                fontSize = 15.sp,
                fontFamily = Lexend,
            )

            Spacer(Modifier.height(16.dp))

            if (myNickname.isNotBlank() && myNickname != currentUser.nickname) {
                val followShape = RoundedCornerShape(20.dp)
                Button(
                    onClick = {
                        if (isFollowing) {
                            socialViewModel.unfollowUser(myNickname, currentUser.nickname)
                        } else {
                            socialViewModel.followUser(myNickname, currentUser.nickname)
                        }
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                if (isFollowing) {
                                    Color.Transparent
                                } else {
                                    GrozzYellow
                                },
                            contentColor = if (isFollowing) Color.White else Color.Black,
                        ),
                    shape = followShape,
                    border =
                        if (isFollowing) {
                            BorderStroke(
                                1.dp,
                                GrozzYellow.copy(alpha = 0.55f),
                            )
                        } else {
                            null
                        },
                    elevation =
                        ButtonDefaults.buttonElevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 0.dp,
                        ),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp),
                ) {
                    Text(
                        text = if (isFollowing) "Following" else "Follow",
                        style =
                            TextStyle(
                                fontFamily = Lexend,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                            ),
                    )
                }
            }

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatItem(label = "FOLLOWERS", count = followerCount) {
                    navController.navigate(
                        Screens.ProjectFollowersScreen.createRoute(currentUser.nickname),
                    )
                }
                Box(
                    modifier =
                        Modifier
                            .width(1.dp)
                            .height(40.dp)
                            .background(Color.DarkGray),
                )
                StatItem(label = "FOLLOWING", count = followingCount) {
                    navController.navigate(
                        Screens.ProjectFollowScreen.createRoute(currentUser.nickname),
                    )
                }
            }

            Box(
                modifier =
                    Modifier
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .fillMaxWidth()
                        .height(36.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Gray.copy(alpha = 0.1f)),
            ) {
                SecondaryTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Transparent,
                    divider = {},
                    indicator = {},
                    modifier = Modifier.fillMaxSize(),
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        val isSelected = selectedTabIndex == index
                        Tab(
                            selected = isSelected,
                            onClick = { onTabSelected(index) },
                            modifier =
                                Modifier
                                    .clip(RoundedCornerShape(18.dp))
                                    .then(
                                        if (isSelected) {
                                            Modifier.background(GrozzYellow)
                                        } else {
                                            Modifier
                                        },
                                    ),
                            text = {
                                Text(
                                    text = title,
                                    style =
                                        TextStyle(
                                            fontFamily = Lexend,
                                            fontSize = 15.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        ),
                                    color = if (isSelected) Color.Black else GrozzMuted,
                                )
                            },
                        )
                    }
                }
            }

            if (selectedTabIndex == 0) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(bottom = 24.dp),
                ) {
                    Text(
                        text = "Lifetime Statistics",
                        textAlign = TextAlign.Start,
                        fontFamily = Lexend,
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(letterSpacing = 0.sp, fontSize = 20.sp),
                        color = Color.White,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 25.dp, vertical = 12.dp),
                    )

                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 25.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        OtherLifetimeStatCard(
                            value = formatOtherStatNumber(target.count),
                            lines =
                                listOf(
                                    "WORKOUTS" to Color.White,
                                    "COMPLETED" to GrozzYellow,
                                ),
                            modifier = Modifier.weight(1f),
                        )
                        OtherLifetimeStatCard(
                            value = formatOtherStatNumber(target.weight.toLong()),
                            lines =
                                listOf(
                                    "KG" to Color.White,
                                    "WEIGHT" to Color.White,
                                    "LIFTED" to GrozzYellow,
                                ),
                            modifier = Modifier.weight(1f),
                            valueFontSize = 36.sp,
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 25.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        OtherLifetimeStatCard(
                            value = formatOtherStatNumber(target.time),
                            lines =
                                listOf(
                                    "MINUTES" to Color.White,
                                    "SPENT FOR" to Color.White,
                                    "WORKOUTS" to GrozzYellow,
                                ),
                            modifier = Modifier.weight(1f),
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            } else {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(bottom = 16.dp),
                ) {
                    Text(
                        text = "Last Activity",
                        textAlign = TextAlign.Start,
                        fontFamily = Lexend,
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(letterSpacing = 0.sp, fontSize = 20.sp),
                        color = Color.White,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 25.dp, vertical = 8.dp),
                    )

                    if (userHistory.isEmpty()) {
                        Column(
                            Modifier
                                .padding(horizontal = 30.dp)
                                .fillMaxWidth()
                                .background(
                                    Color.Gray.copy(alpha = 0.1f),
                                    RoundedCornerShape(20.dp),
                                ).padding(vertical = 28.dp, horizontal = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.sentimentsadicon128),
                                contentDescription = null,
                                tint = Color.Gray.copy(alpha = 0.5f),
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = "No workout history yet",
                                textAlign = TextAlign.Center,
                                fontFamily = Lexend,
                                fontWeight = FontWeight.Bold,
                                style = TextStyle(letterSpacing = 0.sp, fontSize = 15.sp),
                                color = Color.Gray.copy(alpha = 0.5f),
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 24.dp),
                        ) {
                            items(
                                items = userHistory,
                                key = { it.sessionId },
                            ) { item ->
                                OtherActivityHistoryRow(
                                    workoutName = item.workoutName,
                                    dateLabel = formatNotificationTime(item.dateTimestamp),
                                    onClick = {
                                        oldWorkoutDetailsViewModel.setTargetUserId(currentUser.id)
                                        oldWorkoutDetailsViewModel.setSessionId(item.sessionId)
                                        oldWorkoutDetailsViewModel.setCanManage(false)
                                        navController.navigate("oldworkoutdetails")
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }

        if (isPhotoExpanded && hasPhoto) {
            Dialog(onDismissRequest = { onPhotoExpandedChange(false) }) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .clickable { onPhotoExpandedChange(false) },
                    contentAlignment = Alignment.Center,
                ) {
                    AnimatedVisibility(
                        visible = isPhotoExpanded,
                        enter = fadeIn() + scaleIn(initialScale = 0.8f),
                        exit = fadeOut() + scaleOut(targetScale = 0.8f),
                    ) {
                        AsyncImage(
                            model = currentUser.userPhotoUri,
                            contentDescription = null,
                            modifier =
                                Modifier
                                    .fillMaxWidth(0.9f)
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                                    .background(Color.Black)
                                    .clickable(enabled = false) {},
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OtherLifetimeStatCard(
    value: String,
    lines: List<Pair<String, Color>>,
    modifier: Modifier = Modifier,
    valueFontSize: TextUnit = 40.sp,
) {
    Box(
        modifier =
            modifier
                .background(OtherProfileCardBg, RoundedCornerShape(12.dp))
                .aspectRatio(1f)
                .padding(12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                textAlign = TextAlign.Center,
                fontFamily = Lexend,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = valueFontSize,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            lines.forEach { (label, color) ->
                Text(
                    text = label,
                    textAlign = TextAlign.Center,
                    fontFamily = Lexend,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

@Composable
private fun OtherActivityHistoryRow(workoutName: String, dateLabel: String, onClick: () -> Unit) {
    Box(
        modifier =
            Modifier
                .clickable(onClick = onClick)
                .fillMaxWidth()
                .padding(horizontal = 25.dp, vertical = 6.dp)
                .background(OtherProfileCardBg, RoundedCornerShape(10.dp))
                .height(64.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(R.drawable.dumbbellicon128),
                contentDescription = null,
                tint = GrozzYellow,
                modifier = Modifier.size(30.dp),
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = workoutName,
                    style =
                        TextStyle(
                            fontSize = 15.sp,
                            fontFamily = Lexend,
                            color = Color.White,
                        ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (dateLabel.isNotBlank()) {
                    Text(
                        text = dateLabel,
                        style =
                            TextStyle(
                                fontSize = 11.sp,
                                fontFamily = Lexend,
                                color = Color.White.copy(alpha = 0.5f),
                            ),
                    )
                }
            }
            Icon(
                painter = painterResource(R.drawable.keyboarddoublearrowright),
                contentDescription = null,
                tint = GrozzYellow,
                modifier = Modifier.size(25.dp),
            )
        }
    }
}

private fun formatOtherStatNumber(value: Number): String {
    val n = value.toLong()
    return when {
        n >= 1_000_000 -> String.format("%.1fM", n / 1_000_000.0)
        n >= 10_000 -> String.format("%.1fK", n / 1_000.0)
        else -> n.toString()
    }
}

@Composable
fun StatItem(label: String, count: Int, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            Modifier
                .clickable(onClick = onClick)
                .padding(8.dp),
    ) {
        Text(
            text = "$count",
            color = Color.White,
            fontSize = 20.sp,
            fontFamily = Lexend,
        )
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 11.sp,
            fontFamily = Lexend,
        )
    }
}
