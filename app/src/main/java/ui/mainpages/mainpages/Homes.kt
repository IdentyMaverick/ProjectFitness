@file:SuppressLint(
    "StateFlowValueCalledInComposition",
    "UnusedMaterial3ScaffoldPaddingParameter"
)

package ui.mainpages.mainpages

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.storage.storage
import com.grozzbear.R
import com.grozzbear.projectfitness.data.local.entity.WorkoutWithExercises
import com.grozzbear.projectfitness.data.local.viewmodel.HomesViewModel
import com.grozzbear.projectfitness.data.local.viewmodel.WorkoutSettingViewModel
import com.grozzbear.ui.components.GrozzPrimaryButton
import com.grozzbear.ui.components.GrozzTopBarLogo
import com.grozzbear.ui.theme.GrozzError
import com.grozzbear.ui.theme.GrozzOnBackground
import com.grozzbear.ui.theme.GrozzOnPrimary
import com.grozzbear.ui.theme.GrozzSurface
import com.grozzbear.ui.theme.GrozzSystemBar
import com.grozzbear.ui.theme.GrozzTextSecondary
import com.grozzbear.ui.theme.GrozzYellow
import java.util.Calendar
import kotlin.random.Random
import com.grozzbear.ui.theme.Lexend
import com.grozzbear.ui.theme.Oswald
import com.grozzbear.ui.util.safeWorkoutPainter
import ui.mainpages.navigation.NavigationBar
import ui.mainpages.navigation.Screens
import ui.mainpages.navigation.navigateToLoginAfterLogout
import viewmodel.AuthViewModel
import viewmodel.ProjectFitnessViewModel
import viewmodel.SocialViewModel
import viewmodel.ViewModelProfile
import viewmodel.ViewModelSave

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navController: NavController,
    viewModelSave: ViewModelSave,
    viewModel: ProjectFitnessViewModel,
    viewModelProfile: ViewModelProfile,
    authViewModel: AuthViewModel,
    homesViewModel: HomesViewModel,
    socialViewModel: SocialViewModel,
    workoutSettingViewModel: WorkoutSettingViewModel
) {
    val uid = Firebase.auth.currentUser?.uid
    var showMenuSheet by remember { mutableStateOf(false) }
    val menuSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val storageRef = remember { Firebase.storage.reference }
    val profileRef = remember(uid) {
        storageRef.child("gs://projectfitness-ddfeb.appspot.com/profile_photos/$uid/profile.jpg")
    }

    val workouts by homesViewModel.workoutsFlow.collectAsState(initial = emptyList())
    val challengeWorkouts =
        workouts.filter { it.workout.workoutType.contains("challange", ignoreCase = true) }
    val coachWorkouts =
        workouts.filter { it.workout.workoutType.contains("coach", ignoreCase = true) }
    val userName by homesViewModel.userName.collectAsState()
    val nickname by homesViewModel.nickname.collectAsState()
    val notification by remember(nickname) {
        socialViewModel.getNotification(nickname)
    }.collectAsState(initial = emptyList())
    val unReadCount = notification.count { !it.isRead }
    socialViewModel.setNickname(nickname)

    val featuredWorkout = remember(workouts) {
        pickTodaysWorkout(workouts)
    }
    val challengePagerState = rememberPagerState(pageCount = { challengeWorkouts.size.coerceAtLeast(1) })
    val isLoading = workouts.isEmpty() || userName.isEmpty() || userName == "Yükleniyor..."

    LaunchedEffect(uid) {
        if (uid.isNullOrBlank()) return@LaunchedEffect
        profileRef.downloadUrl
            .addOnSuccessListener { uri ->
                viewModelProfile.selectedImageUri.value = uri.toString()
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Failed profile url", exception)
            }
    }

    LaunchedEffect(Unit) {
        homesViewModel.refreshExercises()
        if (!uid.isNullOrBlank()) {
            homesViewModel.getUserName(uid)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            HomeTopBar(
                onProfileClick = { showMenuSheet = true },
                onNotificationsClick = {
                    navController.navigate(Screens.NotificationScreen.route)
                },
                unreadCount = unReadCount
            )
        },
        containerColor = GrozzSystemBar,
        bottomBar = {
            NavigationBar(
                navController = navController,
                indexs = 0,
                flag = true,
                flag2 = false,
                flag3 = false,
                flag4 = false
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        if (isLoading) {
            HomeLoadingState(Modifier.padding(paddingValues))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    GreetingSection(userName = userName)
                    Spacer(modifier = Modifier.height(20.dp))
                }

                item {
                    featuredWorkout?.let { featured ->
                        HomeHeroCard(
                            workout = featured,
                            onStartClick = {
                                navController.navigate(
                                    "workoutsettingscreen/${featured.workout.workoutId}"
                                ) {
                                    popUpTo(Screens.Home.route)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(28.dp))
                    }
                }

                item {
                    SectionHeader(
                        titleTop = "CHALLENGES",
                        titleBottom = "CATALOGUE",
                        actionLabel = "See all",
                        onActionClick = {
                            navController.navigate(
                                Screens.AllWorkouts.createRoute(Screens.AllWorkouts.FILTER_CHALLENGE)
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    if (challengeWorkouts.isEmpty()) {
                        EmptyCatalogueHint(
                            text = "No challenges yet. Browse all workouts to get started."
                        )
                    } else {
                        HorizontalPager(
                            state = challengePagerState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(176.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            pageSpacing = 14.dp
                        ) { pageIndex ->
                            val item = challengeWorkouts[pageIndex]
                            WorkoutCatalogueCard(
                                workout = item,
                                onClick = {
                                    navController.navigate(
                                        "workoutsettingscreen/${item.workout.workoutId}"
                                    )
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        PageIndicator(
                            numberOfPages = challengeWorkouts.size,
                            selectedPage = challengePagerState.currentPage
                        )
                    }
                    Spacer(modifier = Modifier.height(28.dp))
                }

                if (coachWorkouts.isNotEmpty()) {
                    item {
                        CoachPicksTeaser(
                            count = coachWorkouts.size,
                            onClick = {
                                navController.navigate(
                                    Screens.AllWorkouts.createRoute(Screens.AllWorkouts.FILTER_COACH)
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    if (showMenuSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMenuSheet = false },
            sheetState = menuSheetState,
            containerColor = GrozzSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 40.dp)
            ) {
                Text(
                    text = "Menu",
                    style = MaterialTheme.typography.titleLarge,
                    color = GrozzOnBackground,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                MenuItemRow(
                    iconRes = R.drawable.accountcircle,
                    text = "View Profile",
                    onClick = {
                        showMenuSheet = false
                        navController.navigate(Screens.Home.Profile.route)
                    }
                )
                MenuItemRow(
                    iconRes = R.drawable.settings,
                    text = "Settings",
                    onClick = {
                        showMenuSheet = false
                        navController.navigate(Screens.HomesSettings.route)
                    }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp),
                    thickness = 0.5.dp,
                    color = GrozzTextSecondary.copy(alpha = 0.25f)
                )
                MenuItemRow(
                    iconRes = R.drawable.logouticon128,
                    text = "Log Out",
                    textColor = GrozzError,
                    onClick = {
                        showMenuSheet = false
                        authViewModel.logout()
                        navController.navigateToLoginAfterLogout()
                    }
                )
            }
        }
    }
}

@Composable
private fun HomeTopBar(
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    unreadCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            // Only status-bar inset here. Scaffold contentWindowInsets are zeroed,
            // and bottom nav applies navigationBarsPadding once by itself.
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onProfileClick) {
            Icon(
                painter = painterResource(R.drawable.accountcircle),
                contentDescription = "Menu",
                modifier = Modifier.size(26.dp),
                tint = GrozzOnBackground
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        GrozzTopBarLogo()

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = onNotificationsClick) {
            BadgedBox(
                badge = {
                    if (unreadCount > 0) {
                        Badge(containerColor = GrozzError) {
                            Text(
                                text = unreadCount.toString(),
                                color = GrozzOnBackground,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.circlenotifications),
                    contentDescription = "Notifications",
                    tint = GrozzOnBackground,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@Composable
private fun GreetingSection(userName: String) {
    val firstName = userName.trim().split(" ").firstOrNull().orEmpty().ifBlank { "Athlete" }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Hello, ",
                style = MaterialTheme.typography.headlineMedium,
                color = GrozzOnBackground
            )
            Text(
                text = firstName,
                style = MaterialTheme.typography.headlineMedium,
                color = GrozzYellow
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Ready to crush your goals today?",
            style = MaterialTheme.typography.bodyMedium,
            color = GrozzTextSecondary.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun HomeHeroCard(
    workout: WorkoutWithExercises,
    onStartClick: () -> Unit
) {
    val exerciseCount = workout.exercises.size

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(210.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(GrozzSurface)
    ) {
        Image(
            painter = safeWorkoutPainter(workout.workout.image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.15f),
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Today's Pick",
                style = MaterialTheme.typography.labelMedium,
                color = GrozzYellow,
                fontFamily = Lexend,
                fontWeight = FontWeight.Bold
            )
            Column {
                Text(
                    text = workout.workout.workoutName,
                    style = MaterialTheme.typography.headlineMedium,
                    color = GrozzOnBackground,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (exerciseCount > 0) {
                        "$exerciseCount exercises · ${workout.workout.workoutType}"
                    } else {
                        workout.workout.workoutType
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = GrozzTextSecondary
                )
                Spacer(modifier = Modifier.height(14.dp))
                GrozzPrimaryButton(
                    text = "Start workout",
                    onClick = onStartClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    titleTop: String,
    titleBottom: String,
    actionLabel: String,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Column {
            HorizontalDivider(
                thickness = 3.dp,
                color = GrozzYellow,
                modifier = Modifier.width(40.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = titleTop,
                fontFamily = Oswald,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = GrozzOnBackground
            )
            Text(
                text = titleBottom,
                fontFamily = Oswald,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = GrozzYellow
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = onActionClick) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelLarge,
                color = GrozzYellow
            )
        }
    }
}

@Composable
private fun WorkoutCatalogueCard(
    workout: WorkoutWithExercises,
    onClick: () -> Unit
) {
    val difficulty = workout.workout.workoutRating.coerceIn(0, 5)
    val exerciseCount = workout.exercises.size

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(GrozzSurface)
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = safeWorkoutPainter(workout.workout.image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f)),
                        startY = 80f
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = workout.workout.workoutType.uppercase(),
                color = GrozzYellow,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = workout.workout.workoutName,
                color = GrozzOnBackground,
                fontFamily = Lexend,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (exerciseCount > 0) {
                    WorkoutTag(
                        text = "$exerciseCount exercises",
                        icon = R.drawable.shutterspeedfilledicon128,
                        textColor = GrozzTextSecondary,
                        iconColor = GrozzTextSecondary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                repeat(5) { index ->
                    Icon(
                        painter = painterResource(id = R.drawable.skullicon128),
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (index < difficulty) GrozzYellow else Color.White.copy(alpha = 0.35f)
                    )
                    if (index < 4) Spacer(modifier = Modifier.size(2.dp))
                }
            }
        }
    }
}

@Composable
private fun CoachPicksTeaser(count: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(GrozzSurface)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Coach's picks",
                style = MaterialTheme.typography.titleMedium,
                color = GrozzOnBackground
            )
            Text(
                text = "$count workouts ready in catalogue",
                style = MaterialTheme.typography.bodySmall,
                color = GrozzTextSecondary
            )
        }
        Text(
            text = "Browse",
            style = MaterialTheme.typography.labelLarge,
            color = GrozzYellow
        )
    }
}

@Composable
private fun EmptyCatalogueHint(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = GrozzTextSecondary,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
    )
}

@Composable
private fun HomeLoadingState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .width(160.dp)
                .height(22.dp)
                .clip(RoundedCornerShape(8.dp))
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .width(220.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(8.dp))
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.height(28.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(24.dp))
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.height(28.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(22.dp))
                .shimmerEffect()
        )
    }
}

@Composable
fun MenuItemRow(
    iconRes: Int,
    text: String,
    textColor: Color = GrozzOnBackground,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = text,
            tint = textColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            color = textColor,
            fontFamily = Lexend,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(id = R.drawable.keyboarddoublearrowright),
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun PageIndicator(
    numberOfPages: Int,
    selectedPage: Int,
    modifier: Modifier = Modifier
) {
    if (numberOfPages <= 1) return
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth()
    ) {
        repeat(numberOfPages) { iteration ->
            val isSelected = iteration == selectedPage
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) GrozzYellow else Color.Gray.copy(alpha = 0.45f))
                    .size(if (isSelected) 8.dp else 6.dp)
                    .animateContentSize()
            )
        }
    }
}

fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition(label = "shimmer")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(animation = tween(1000)),
        label = "shimmerOffset"
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFF22262B),
                Color(0xFF35393F),
                Color(0xFF22262B)
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    ).onGloballyPositioned {
        size = it.size
    }
}

/**
 * Picks one catalogue workout for the local calendar day.
 * Same day → same workout; next day → a new seeded random pick.
 */
private fun pickTodaysWorkout(
    workouts: List<WorkoutWithExercises>
): WorkoutWithExercises? {
    if (workouts.isEmpty()) return null

    val pool = workouts
        .filterNot { it.workout.workoutType.equals("User", ignoreCase = true) }
        .ifEmpty { workouts }
        .sortedBy { it.workout.workoutId }

    val calendar = Calendar.getInstance()
    val daySeed = calendar.get(Calendar.YEAR) * 1_000L +
        calendar.get(Calendar.DAY_OF_YEAR)

    return pool[Random(daySeed).nextInt(pool.size)]
}
