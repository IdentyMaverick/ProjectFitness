package ui.mainpages.mainpages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.grozzbear.R
import com.grozzbear.projectfitness.data.local.entity.WorkoutEntity
import com.grozzbear.projectfitness.data.local.viewmodel.ActivityViewModel
import com.grozzbear.projectfitness.data.local.viewmodel.HomesViewModel
import com.grozzbear.ui.components.GrozzPhotoCard
import com.grozzbear.ui.components.GrozzPrimaryButton
import com.grozzbear.ui.components.GrozzTopBarLogo
import com.grozzbear.ui.theme.GrozzError
import com.grozzbear.ui.theme.GrozzMuted
import com.grozzbear.ui.theme.GrozzOnBackground
import com.grozzbear.ui.theme.GrozzRadiusButton
import com.grozzbear.ui.theme.GrozzSurface
import com.grozzbear.ui.theme.GrozzSystemBar
import com.grozzbear.ui.theme.GrozzTextSecondary
import com.grozzbear.ui.theme.GrozzYellow
import com.grozzbear.ui.theme.Lexend
import com.grozzbear.ui.theme.Oswald
import com.grozzbear.ui.util.safeWorkoutPainter
import com.grozzbear.ui.util.workoutTypeLabel
import ui.mainpages.navigation.NavigationBar
import ui.mainpages.navigation.Screens
import ui.mainpages.navigation.navigateToLoginAfterLogout
import viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Activity(
    navController: NavController,
    activityViewModel: ActivityViewModel,
    authViewModel: AuthViewModel,
    @Suppress("UNUSED_PARAMETER") homesViewModel: HomesViewModel,
) {
    val currentUser = FirebaseAuth.getInstance().currentUser?.uid
    val myWorkouts by activityViewModel.myWorkoutsFlow.collectAsState(initial = emptyList())

    var selectedWorkoutId by remember { mutableStateOf<String?>(null) }
    var showMenuSheet by remember { mutableStateOf(false) }
    val deleteSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val menuSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(currentUser) {
        if (!currentUser.isNullOrBlank()) {
            activityViewModel.refreshWorkouts(currentUser)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            ActivityTopBar(
                onMenuClick = { showMenuSheet = true },
            )
        },
        containerColor = GrozzSystemBar,
        bottomBar = {
            NavigationBar(
                navController = navController,
                indexs = 1,
                flag = false,
                flag2 = true,
                flag3 = false,
                flag4 = false,
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 24.dp),
        ) {
            item {
                ActivityHeader(
                    workoutCount = myWorkouts.size,
                    onCreateClick = { navController.navigate(Screens.CreateWorkout.route) },
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            if (myWorkouts.isEmpty()) {
                item {
                    ActivityEmptyState(
                        onCreateClick = { navController.navigate(Screens.CreateWorkout.route) },
                    )
                }
            } else {
                items(myWorkouts, key = { it.workoutId }) { workout ->
                    MyWorkoutCard(
                        workout = workout,
                        onClick = {
                            navController.navigate("workoutsettingscreen/${workout.workoutId}") {
                                popUpTo(Screens.Activity.route)
                            }
                        },
                        onLongClick = { selectedWorkoutId = workout.workoutId },
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    if (selectedWorkoutId != null) {
        LongClickModalBottom(
            sheetState = deleteSheetState,
            onDismiss = { selectedWorkoutId = null },
            onDeleteClick = {
                val id = selectedWorkoutId
                if (id != null) {
                    activityViewModel.deleteWorkouts(id)
                }
                selectedWorkoutId = null
            },
        )
    }

    if (showMenuSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMenuSheet = false },
            sheetState = menuSheetState,
            containerColor = GrozzSurface,
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 40.dp),
            ) {
                Text(
                    text = "Menu",
                    style = MaterialTheme.typography.titleLarge,
                    color = GrozzOnBackground,
                    modifier = Modifier.padding(horizontal = 24.dp),
                )
                Spacer(modifier = Modifier.height(12.dp))
                MenuItemRow(
                    iconRes = R.drawable.accountcircle,
                    text = "View Profile",
                    onClick = {
                        showMenuSheet = false
                        navController.navigate(Screens.Home.Profile.route)
                    },
                )
                MenuItemRow(
                    iconRes = R.drawable.settings,
                    text = "Settings",
                    onClick = {
                        showMenuSheet = false
                        navController.navigate(Screens.HomesSettings.route)
                    },
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp),
                    thickness = 0.5.dp,
                    color = GrozzTextSecondary.copy(alpha = 0.25f),
                )
                MenuItemRow(
                    iconRes = R.drawable.logouticon128,
                    text = "Log Out",
                    textColor = GrozzError,
                    onClick = {
                        showMenuSheet = false
                        authViewModel.logout()
                        navController.navigateToLoginAfterLogout()
                    },
                )
            }
        }
    }
}

@Composable
private fun ActivityTopBar(onMenuClick: () -> Unit) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onMenuClick) {
            Icon(
                painter = painterResource(R.drawable.accountcircle),
                contentDescription = "Menu",
                modifier = Modifier.size(26.dp),
                tint = GrozzOnBackground,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        GrozzTopBarLogo()

        Spacer(modifier = Modifier.weight(1f))

        // Balance the leading icon so the logo stays centered.
        Spacer(modifier = Modifier.size(48.dp))
    }
}

@Composable
private fun ActivityHeader(workoutCount: Int, onCreateClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalDivider(
            thickness = 2.dp,
            color = GrozzYellow,
            modifier = Modifier.width(28.dp),
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "YOUR",
                fontFamily = Oswald,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = GrozzOnBackground,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "WORKOUTS",
                fontFamily = Oswald,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = GrozzYellow,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text =
                if (workoutCount == 0) {
                    "Build plans you can reuse anytime."
                } else {
                    "$workoutCount custom ${if (workoutCount == 1) "plan" else "plans"}"
                },
            style = MaterialTheme.typography.bodySmall,
            color = GrozzTextSecondary,
        )
        if (workoutCount > 0) {
            Spacer(modifier = Modifier.height(12.dp))
            GrozzPrimaryButton(
                text = "Create workout",
                onClick = onCreateClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ActivityEmptyState(onCreateClick: () -> Unit) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(R.drawable.dumbbell),
            contentDescription = null,
            tint = GrozzMuted,
            modifier = Modifier.size(64.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No workouts yet",
            style = MaterialTheme.typography.titleLarge,
            color = GrozzOnBackground,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Create your first workout plan and start your journey today.",
            style = MaterialTheme.typography.bodyMedium,
            color = GrozzTextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp),
        )
        Spacer(modifier = Modifier.height(24.dp))
        GrozzPrimaryButton(
            text = "Create workout",
            onClick = onCreateClick,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MyWorkoutCard(workout: WorkoutEntity, onClick: () -> Unit, onLongClick: () -> Unit) {
    GrozzPhotoCard(
        painter = safeWorkoutPainter(workout.image),
        modifier =
            Modifier
                .fillMaxWidth()
                .height(88.dp)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick,
                ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(14.dp),
            verticalArrangement = Arrangement.Bottom,
        ) {
            Text(
                text = workoutTypeLabel(workout.workoutType).uppercase(),
                color = GrozzYellow,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                fontFamily = Lexend,
            )
            Text(
                text = workout.workoutName,
                color = GrozzOnBackground,
                fontFamily = Oswald,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LongClickModalBottom(sheetState: SheetState, onDismiss: () -> Unit, onDeleteClick: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = GrozzSurface,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .padding(bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Remove workout",
                style = MaterialTheme.typography.titleLarge,
                color = GrozzOnBackground,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "This deletes the plan from your library.",
                style = MaterialTheme.typography.bodyMedium,
                color = GrozzTextSecondary,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onDeleteClick,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = GrozzError,
                        contentColor = GrozzOnBackground,
                    ),
                shape = RoundedCornerShape(GrozzRadiusButton),
            ) {
                Text(
                    text = "Delete",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Long-press a workout anytime to remove it.",
                style = MaterialTheme.typography.bodySmall,
                color = GrozzTextSecondary.copy(alpha = 0.7f),
            )
        }
    }
}
