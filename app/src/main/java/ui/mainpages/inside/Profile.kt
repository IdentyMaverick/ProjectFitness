package ui.mainpages.inside

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.grozzbear.R
import data.local.viewmodel.OldWorkoutDetailsViewModel
import data.local.viewmodel.WorkoutCompleteScreenViewModel
import ui.mainpages.navigation.Screens
import viewmodel.AuthViewModel
import viewmodel.ProfileUiState
import viewmodel.ProfileViewModel
import viewmodel.SocialViewModel
import viewmodel.ViewModelProfile

private val ProfileCardBg = Color(0xFF202B36).copy(alpha = 0.55f)
private val ProfileAccent = Color(0xFFF1C40F)
private val ProfileMuted = Color(0xFF4B5F71)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(
    navController: NavController,
    @Suppress("UNUSED_PARAMETER") viewModelProfile: ViewModelProfile,
    socialViewModel: SocialViewModel,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    workoutScreenCompleteScreenViewModel: WorkoutCompleteScreenViewModel,
    oldWorkoutDetailsViewModel: OldWorkoutDetailsViewModel
) {
    val uid = Firebase.auth.currentUser?.uid ?: return
    val profileState by profileViewModel.profileState.collectAsState()

    LaunchedEffect(uid) {
        profileViewModel.load(uid)
        authViewModel.getTotalWorkoutNumber(uid)
        authViewModel.getTotalLiftedWeight(uid)
        authViewModel.getTotalSpentTime(uid)
    }

    val launcherProfile =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) profileViewModel.changePhoto(uid, uri = uri)
        }

    var nickname by remember { mutableStateOf("") }
    val getFollowers by remember(nickname) {
        socialViewModel.getFollowers(nickname)
    }.collectAsState(initial = emptyList())
    val getFollowing by remember(nickname) {
        socialViewModel.getFollowing(nickname)
    }.collectAsState(initial = emptyList())
    val numberOfFollows = getFollowing.size
    val numberOfFollowers = getFollowers.size

    val allHistoricalWorkouts by authViewModel.allHistoricalWorkouts.collectAsState(emptyList())
    val scrollState = rememberScrollState()
    val totalWorkout by authViewModel.totalWorkoutNumber.collectAsState()
    val totalLiftedWeight by authViewModel.totalLiftedWeight.collectAsState()
    val getTotalSpentTime by authViewModel.totalSpentTime.collectAsState()
    val consistencyScore = authViewModel.calculateConsistency(allHistoricalWorkouts)

    LaunchedEffect(allHistoricalWorkouts.isEmpty(), uid) {
        if (allHistoricalWorkouts.isEmpty()) {
            authViewModel.syncWorkoutsFromFirebase(uid)
        }
    }

    LaunchedEffect(nickname) {
        if (nickname.isNotBlank()) {
            socialViewModel._nickname.value = nickname
        }
    }

    var isPhotoExpanded by remember { mutableStateOf(false) }
    val blurAlpha by animateDpAsState(
        targetValue = if (isPhotoExpanded) 15.dp else 0.dp,
        animationSpec = tween(durationMillis = 50),
        label = "blurAnimation"
    )
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("Stats", "Activity")

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            HomeTopBarProfile(navController)
        },
        containerColor = Color(0xFF121417),
        modifier = Modifier
            .fillMaxSize()
            .blur(blurAlpha),
    ) { paddingValues ->
        when (val state = profileState) {
            is ProfileUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ProfileAccent)
                }
            }

            is ProfileUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Couldn't load profile. Pull to refresh or try again later.",
                        color = Color.Gray,
                        fontFamily = FontFamily(Font(R.font.lexendregular)),
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            is ProfileUiState.Ready -> {
                val profile = state.profile
                LaunchedEffect(profile.nickname) {
                    nickname = profile.nickname
                }
                val hasPhoto = profile.userPhotoUri.isNotBlank()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(8.dp))

                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .border(4.dp, ProfileAccent, CircleShape)
                                .padding(4.dp)
                                .border(2.dp, Color.Black, CircleShape)
                                .padding(4.dp)
                        ) {
                            AsyncImage(
                                model = if (hasPhoto) profile.userPhotoUri else R.drawable.grozzlogo,
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .clickable {
                                        if (hasPhoto) {
                                            isPhotoExpanded = true
                                        } else {
                                            launcherProfile.launch("image/*")
                                        }
                                    },
                                contentScale = ContentScale.Crop
                            )
                        }
                        IconButton(
                            onClick = { launcherProfile.launch("image/*") },
                            modifier = Modifier
                                .size(36.dp)
                                .background(ProfileAccent, CircleShape)
                                .border(2.dp, Color(0xFF121417), CircleShape)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.imageicon128),
                                contentDescription = "Change photo",
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(Modifier.size(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            profile.first,
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.lexendbold)),
                            fontSize = 20.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (profile.hasPro) {
                            Spacer(Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ProfileAccent)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "PRO",
                                    color = Color.Black,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Text(
                        "@${profile.nickname}",
                        color = ProfileAccent,
                        fontFamily = FontFamily(Font(R.font.lexendbold)),
                        fontSize = 15.sp
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatItem(label = "FOLLOWERS", count = numberOfFollowers) {
                            navController.navigate("projectfollowersscreen")
                        }
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(40.dp)
                                .background(Color.DarkGray)
                        )
                        StatItem(label = "FOLLOWING", count = numberOfFollows) {
                            navController.navigate("projectfollowscreen")
                        }
                    }

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                            .fillMaxWidth()
                            .height(36.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.Gray.copy(alpha = 0.1f))
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
                                        .clip(RoundedCornerShape(18.dp))
                                        .then(
                                            if (isSelected) Modifier.background(ProfileAccent)
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
                                            color = if (isSelected) Color.Black else ProfileMuted
                                        )
                                    }
                                )
                            }
                        }
                    }

                    if (selectedTabIndex == 0) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(bottom = 24.dp)
                        ) {
                            Text(
                                text = "Lifetime Statistics",
                                textAlign = TextAlign.Start,
                                fontFamily = FontFamily(Font(R.font.lexendbold)),
                                fontWeight = FontWeight.Bold,
                                style = TextStyle(letterSpacing = 0.sp, fontSize = 20.sp),
                                color = Color.White,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 25.dp, vertical = 12.dp)
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 25.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                LifetimeStatCard(
                                    value = formatStatNumber(totalWorkout),
                                    lines = listOf("WORKOUTS" to Color.White, "COMPLETED" to ProfileAccent),
                                    modifier = Modifier.weight(1f)
                                )
                                LifetimeStatCard(
                                    value = formatStatNumber(totalLiftedWeight.toLong()),
                                    lines = listOf(
                                        "KG" to Color.White,
                                        "WEIGHT" to Color.White,
                                        "LIFTED" to ProfileAccent
                                    ),
                                    modifier = Modifier.weight(1f),
                                    valueFontSize = 36.sp
                                )
                            }

                            Spacer(Modifier.height(16.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 25.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                LifetimeStatCard(
                                    value = formatStatNumber(getTotalSpentTime),
                                    lines = listOf(
                                        "MINUTES" to Color.White,
                                        "SPENT FOR" to Color.White,
                                        "WORKOUTS" to ProfileAccent
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                LifetimeStatCard(
                                    value = formatStatNumber(consistencyScore),
                                    lines = listOf(
                                        "CONSISTENCY" to Color.White,
                                        "SCORE" to ProfileAccent
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = "Last Activity",
                                textAlign = TextAlign.Start,
                                fontFamily = FontFamily(Font(R.font.lexendbold)),
                                fontWeight = FontWeight.Bold,
                                style = TextStyle(letterSpacing = 0.sp, fontSize = 20.sp),
                                color = Color.White,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 25.dp, vertical = 8.dp)
                            )

                            if (allHistoricalWorkouts.isNotEmpty()) {
                                LazyColumn(
                                    contentPadding = PaddingValues(bottom = 24.dp)
                                ) {
                                    items(
                                        items = allHistoricalWorkouts,
                                        key = { it.workoutHistory.sessionId }
                                    ) { item ->
                                        val formattedDate = remember(item.workoutHistory.dateTimestamp) {
                                            workoutScreenCompleteScreenViewModel.dateConvert(
                                                item.workoutHistory.dateTimestamp
                                            )
                                        }
                                        ActivityHistoryRow(
                                            workoutName = item.workoutHistory.workoutName,
                                            dateLabel = formattedDate,
                                            onClick = {
                                                oldWorkoutDetailsViewModel._sessionId.value =
                                                    item.workoutHistory.sessionId
                                                oldWorkoutDetailsViewModel._flag.value = false
                                                navController.navigate("oldworkoutdetails")
                                            }
                                        )
                                    }
                                }
                            } else {
                                Column(
                                    Modifier
                                        .padding(horizontal = 30.dp)
                                        .fillMaxWidth()
                                        .background(
                                            Color.Gray.copy(alpha = 0.1f),
                                            RoundedCornerShape(20.dp)
                                        )
                                        .padding(vertical = 24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.sentimentsadicon128),
                                        contentDescription = null,
                                        tint = Color.Gray.copy(alpha = 0.5f)
                                    )
                                    Text(
                                        text = "No workout history yet. Start your first workout today!",
                                        textAlign = TextAlign.Center,
                                        fontFamily = FontFamily(Font(R.font.lexendregular)),
                                        fontWeight = FontWeight.Bold,
                                        style = TextStyle(letterSpacing = 0.sp, fontSize = 15.sp),
                                        color = Color.Gray.copy(alpha = 0.5f),
                                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
                                    )
                                    Button(
                                        onClick = { navController.navigate("home") },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = ProfileAccent
                                        ),
                                        shape = RoundedCornerShape(15.dp),
                                        modifier = Modifier
                                            .height(50.dp)
                                            .fillMaxWidth()
                                            .padding(horizontal = 40.dp),
                                        contentPadding = PaddingValues(0.dp),
                                    ) {
                                        Text(
                                            text = "START TRAINING",
                                            style = TextStyle(
                                                fontSize = 20.sp,
                                                fontFamily = FontFamily(Font(R.font.oswaldbold))
                                            ),
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (isPhotoExpanded) {
                    Dialog(onDismissRequest = { isPhotoExpanded = false }) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { isPhotoExpanded = false },
                            contentAlignment = Alignment.Center
                        ) {
                            AnimatedVisibility(
                                visible = isPhotoExpanded,
                                enter = fadeIn() + scaleIn(initialScale = 0.8f),
                                exit = fadeOut() + scaleOut(targetScale = 0.8f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.9f)
                                        .aspectRatio(1f),
                                    contentAlignment = Alignment.BottomEnd
                                ) {
                                    AsyncImage(
                                        model = profile.userPhotoUri,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(Color.Black)
                                            .clickable(enabled = false) {},
                                        contentScale = ContentScale.Crop
                                    )
                                    IconButton(
                                        onClick = { launcherProfile.launch("image/*") },
                                        modifier = Modifier
                                            .padding(12.dp)
                                            .size(48.dp)
                                            .background(ProfileAccent, CircleShape)
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.imageicon128),
                                            contentDescription = "Change photo",
                                            tint = Color.Black,
                                            modifier = Modifier.size(24.dp)
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

@Composable
private fun LifetimeStatCard(
    value: String,
    lines: List<Pair<String, Color>>,
    modifier: Modifier = Modifier,
    valueFontSize: androidx.compose.ui.unit.TextUnit = 40.sp
) {
    Box(
        modifier = modifier
            .background(ProfileCardBg, RoundedCornerShape(12.dp))
            .aspectRatio(1f)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily(Font(R.font.lexendbold)),
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = valueFontSize,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            lines.forEach { (label, color) ->
                Text(
                    text = label,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                    fontWeight = FontWeight.Bold,
                    color = color,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun ActivityHistoryRow(
    workoutName: String,
    dateLabel: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .padding(horizontal = 25.dp, vertical = 6.dp)
            .background(ProfileCardBg, RoundedCornerShape(10.dp))
            .height(64.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.dumbbellicon128),
                contentDescription = null,
                tint = ProfileAccent,
                modifier = Modifier.size(30.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = workoutName,
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontFamily = FontFamily(Font(R.font.lexendbold)),
                        color = Color.White
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (dateLabel.isNotBlank()) {
                    Text(
                        text = dateLabel,
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontFamily = FontFamily(Font(R.font.lexendregular)),
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    )
                }
            }
            Icon(
                painter = painterResource(R.drawable.keyboarddoublearrowright),
                contentDescription = null,
                tint = ProfileAccent,
                modifier = Modifier.size(25.dp)
            )
        }
    }
}

private fun formatStatNumber(value: Number): String {
    val n = value.toLong()
    return when {
        n >= 1_000_000 -> String.format("%.1fM", n / 1_000_000.0)
        n >= 10_000 -> String.format("%.1fK", n / 1_000.0)
        else -> n.toString()
    }
}

@Composable
fun HomeTopBarProfile(
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                painter = painterResource(R.drawable.left),
                contentDescription = null,
                modifier = Modifier.size(30.dp),
                tint = Color.White
            )
        }

        Spacer(Modifier.weight(1f))

        Text(
            "PROFILE",
            color = Color.White,
            fontFamily = FontFamily(Font(R.font.oswaldbold)),
            fontSize = 20.sp
        )

        Spacer(Modifier.weight(1f))

        IconButton(onClick = { navController.navigate(Screens.HomesSettings.route) }) {
            Icon(
                painter = painterResource(R.drawable.settings),
                contentDescription = null,
                modifier = Modifier.size(25.dp),
                tint = Color.White
            )
        }
    }
}
