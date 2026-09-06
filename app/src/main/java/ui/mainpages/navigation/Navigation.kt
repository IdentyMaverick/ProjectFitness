package ui.mainpages.navigation

import activity.inside.ActivityInside
import activity.inside.CreateWorkout
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.grozzbear.R
import com.grozzbear.projectfitness.activity.inside.ChooseExercises
import com.grozzbear.projectfitness.data.local.repository.WorkoutRepository
import com.grozzbear.projectfitness.data.local.viewmodel.ActivityViewModel
import com.grozzbear.projectfitness.data.local.viewmodel.HomesViewModel
import com.grozzbear.projectfitness.data.local.viewmodel.WorkoutSettingViewModel
import com.grozzbear.projectfitness.viewmodel.WorkoutViewModelFactory
import com.grozzbear.ui.theme.GrozzMuted
import com.grozzbear.ui.theme.GrozzOnBackground
import com.grozzbear.ui.theme.GrozzOnPrimary
import com.grozzbear.ui.theme.GrozzSurface
import com.grozzbear.ui.theme.GrozzSystemBar
import com.grozzbear.ui.theme.GrozzTextSecondary
import com.grozzbear.ui.theme.GrozzYellow
import com.grozzbear.ui.theme.Lexend
import data.local.viewmodel.ActivityInsideViewModel
import data.local.viewmodel.ChooseExercisesViewModel
import data.local.viewmodel.CreateWorkoutViewModel
import data.local.viewmodel.FaqcontactfeedbackScreenViewModel
import data.local.viewmodel.LeaderboardViewModel
import data.local.viewmodel.OldWorkoutDetailsViewModel
import data.local.viewmodel.PersonalInformationsScreenViewModel
import data.local.viewmodel.WorkoutCompleteAnalysisScreenViewModel
import data.local.viewmodel.WorkoutCompleteScreenViewModel
import data.local.viewmodel.WorkoutLogViewModel
import data.remote.AuthRepository
import data.remote.FirestoreRepository
import data.remote.LeaderboardEntry
import data.remote.StorageRepository
import data.remote.UserRepository
import data.remote.WorkoutinRepository
import ui.mainpages.inside.FaqcontactfeedbackScreen
import ui.mainpages.inside.FindUsersScreen
import ui.mainpages.inside.HomesSettings
import ui.mainpages.inside.NotificationScreen
import ui.mainpages.inside.OldWorkoutDetails
import ui.mainpages.inside.OtherScreenProfile
import ui.mainpages.inside.PersonalInformationsScreen
import ui.mainpages.inside.Profile
import ui.mainpages.inside.ProjectFollowScreen
import ui.mainpages.inside.ProjectFollowersScreen
import ui.mainpages.inside.WorkoutCompleteAnalysisScreen
import ui.mainpages.inside.WorkoutCompleteScreen
import ui.mainpages.inside.WorkoutLog
import ui.mainpages.inside.WorkoutSettingScreen
import ui.mainpages.loginscreens.ForgetPasswordScreen
import ui.mainpages.loginscreens.LoginScreen
import ui.mainpages.loginscreens.RegisterScreen
import ui.mainpages.mainpages.Activity
import ui.mainpages.mainpages.AllWorkouts
import ui.mainpages.mainpages.Home
import ui.mainpages.mainpages.LeaderBoard
import ui.mainpages.mainpages.Meal
import ui.mainpages.openscreen.InfoHorizontalScreen
import viewmodel.AuthViewModel
import viewmodel.ProfileViewModel
import viewmodel.SocialViewModel
import viewmodel.ViewModelProfile
import viewmodel.ViewModelSave
import viewmodel.WorkoutinViewModel

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Navigation(workoutRepository: WorkoutRepository) {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val isBoardingCompleted = sharedPref.getBoolean("is_boarding_completed", false)
    val isUserLoggedIn = FirebaseAuth.getInstance().currentUser != null
    val navController = rememberNavController()
    val currentUser: FirebaseAuth = FirebaseAuth.getInstance()
    val infoDialog = remember { mutableStateOf(false) }

    val userRepository = remember { UserRepository() }
    val firestoreRepository = remember { FirestoreRepository() }
    val storageRepository = remember { StorageRepository() }
    val workoutinRepository = remember { WorkoutinRepository() }
    val authRepository = remember { AuthRepository() }

    val viewModel: ViewModelSave = viewModel()
    val viewModelProfile: ViewModelProfile = viewModel()
    val chooseExercisesViewModel: ChooseExercisesViewModel = viewModel()

    val coreFactory =
        remember {
            WorkoutViewModelFactory(
                repository = workoutRepository,
                auth = currentUser,
                userRepository = userRepository,
                firestoreRepository = firestoreRepository,
                storageRepository = storageRepository,
                workoutinRepository = workoutinRepository,
                authRepository = authRepository,
            )
        }
    val authViewModel: AuthViewModel = viewModel(factory = coreFactory)
    val profileViewModel: ProfileViewModel = viewModel(factory = coreFactory)
    val socialViewModel: SocialViewModel = viewModel(factory = coreFactory)
    val workoutinModel: WorkoutinViewModel = viewModel(factory = coreFactory)
    val activityInsideViewModel: ActivityInsideViewModel = viewModel(factory = coreFactory)

    val featureFactory =
        remember(authViewModel, profileViewModel) {
            WorkoutViewModelFactory(
                repository = workoutRepository,
                auth = currentUser,
                userRepository = userRepository,
                profileViewModel = profileViewModel,
                authViewModel = authViewModel,
                firestoreRepository = firestoreRepository,
                storageRepository = storageRepository,
                workoutinRepository = workoutinRepository,
                authRepository = authRepository,
            )
        }
    val faqcontactfeedbackScreenViewModel: FaqcontactfeedbackScreenViewModel =
        viewModel(factory = featureFactory)
    val personalInformationsScreenViewModel: PersonalInformationsScreenViewModel =
        viewModel(factory = featureFactory)
    val homesViewModel: HomesViewModel = viewModel(factory = featureFactory)
    val activityViewModel: ActivityViewModel = viewModel(factory = featureFactory)
    val workoutCompleteScreenViewModel: WorkoutCompleteScreenViewModel =
        viewModel(factory = featureFactory)
    val workoutCompleteAnalysisScreenViewModel: WorkoutCompleteAnalysisScreenViewModel =
        viewModel(factory = featureFactory)
    val oldWorkoutDetailsViewModel: OldWorkoutDetailsViewModel =
        viewModel(factory = featureFactory)
    val leaderboardViewModel: LeaderboardViewModel = viewModel(factory = featureFactory)
    val workoutSettingViewModel: WorkoutSettingViewModel = viewModel(factory = featureFactory)
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(GrozzSystemBar),
    ) {
        NavHost(
            navController = navController,
            startDestination =
                when {
                    !isBoardingCompleted -> Screens.InfoHorizontalScreen.route
                    !isUserLoggedIn -> Screens.LoginScreen.route
                    else -> Screens.Home.route
                },
            enterTransition = {
                slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn()
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut()
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn()
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut()
            },
        ) {
            navigation(
                route = "create_workout_graph",
                startDestination = Screens.CreateWorkout.route,
            ) {
                composable(
                    Screens.CreateWorkout.route,
                    enterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { 1000 },
                            animationSpec =
                                spring(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessLow,
                                ),
                        ) + fadeIn() + scaleIn(initialScale = 0.95f)
                    },
                    exitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { 1000 },
                            animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy),
                        ) + fadeOut() + scaleOut(targetScale = 0.95f)
                    },
                    popEnterTransition = {
                        slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                    },
                    popExitTransition = {
                        slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                    },
                ) { backStackEntry ->

                    val parentEntry =
                        remember(backStackEntry) {
                            navController.getBackStackEntry("create_workout_graph")
                        }

                    val createWorkoutViewModel: CreateWorkoutViewModel =
                        viewModel(
                            parentEntry,
                            factory = featureFactory,
                        )

                    CreateWorkout(
                        navController = navController,
                        createWorkoutViewModel = createWorkoutViewModel,
                        chooseExercisesViewModel = chooseExercisesViewModel,
                    )
                }

                composable(
                    Screens.LeaderBoard.route,
                    enterTransition = { fadeIn(animationSpec = tween(400)) },
                    exitTransition = { fadeOut(animationSpec = tween(400)) },
                ) { backStackEntry ->

                    val parentEntry =
                        remember(backStackEntry) {
                            navController.getBackStackEntry("create_workout_graph")
                        }

                    val leaderboardViewModel: LeaderboardViewModel =
                        viewModel(
                            parentEntry,
                            factory = featureFactory,
                        )

                    LeaderBoard(
                        navController = navController,
                        authViewModel = authViewModel,
                        leaderboardViewModel = leaderboardViewModel,
                        profileViewModel = profileViewModel,
                    )
                }
            }
            composable(
                route = Screens.HomesSettings.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec =
                            spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow,
                            ),
                    ) + fadeIn() + scaleIn(initialScale = 0.95f)
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy),
                    ) + fadeOut() + scaleOut(targetScale = 0.95f)
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                },
            ) {
                HomesSettings(
                    navController = navController,
                    authViewModel = authViewModel,
                )
            }
            composable(route = Screens.InfoHorizontalScreen.route) {
                InfoHorizontalScreen(navController)
            }
            composable(route = Screens.LoginScreen.route) {
                LoginScreen(navController = navController, authViewModel = authViewModel)
            }
            composable(route = Screens.LoginScreen.ForgetPasswordScreen.route) {
                ForgetPasswordScreen(navController = navController, authViewModel = authViewModel)
            }
            composable(route = Screens.LoginScreen.RegisterScreen.route) {
                RegisterScreen(navController = navController, authViewModel = authViewModel)
            }
            composable(
                route = Screens.Home.route,
                enterTransition = { fadeIn(animationSpec = tween(400)) },
                exitTransition = { fadeOut(animationSpec = tween(400)) },
            ) {
                Home(
                    navController = navController,
                    viewModelProfile = viewModelProfile,
                    authViewModel = authViewModel,
                    homesViewModel = homesViewModel,
                    socialViewModel = socialViewModel,
                    workoutSettingViewModel = workoutSettingViewModel,
                )
            }
            composable(
                route = Screens.Activity.route,
                enterTransition = { fadeIn(animationSpec = tween(400)) },
                exitTransition = { fadeOut(animationSpec = tween(400)) },
            ) {
                Activity(
                    navController = navController,
                    activityViewModel = activityViewModel,
                    authViewModel = authViewModel,
                    homesViewModel = homesViewModel,
                )
            }
            composable(
                route = Screens.Home.Profile.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec =
                            spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow,
                            ),
                    ) + fadeIn() + scaleIn(initialScale = 0.95f)
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy),
                    ) + fadeOut() + scaleOut(targetScale = 0.95f)
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                },
            ) {
                Profile(
                    navController = navController,
                    viewModelProfile = viewModelProfile,
                    socialViewModel = socialViewModel,
                    authViewModel = authViewModel,
                    profileViewModel = profileViewModel,
                    workoutScreenCompleteScreenViewModel = workoutCompleteScreenViewModel,
                    oldWorkoutDetailsViewModel = oldWorkoutDetailsViewModel,
                )
            }
            composable(
                route = Screens.WorkoutSettingScreen.ROUTE_WITH_ARG,
                arguments = listOf(navArgument("workoutId") { type = NavType.StringType }),
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec =
                            spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow,
                            ),
                    ) + fadeIn() + scaleIn(initialScale = 0.95f)
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy),
                    ) + fadeOut() + scaleOut(targetScale = 0.95f)
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                },
            ) { backStackEntry ->
                val workoutId = backStackEntry.arguments?.getString("workoutId").toString()

                val workoutSettingViewModel: WorkoutSettingViewModel =
                    viewModel(
                        key = "WorkoutSettingViewModel_$workoutId",
                        factory =
                            remember {
                                object : androidx.lifecycle.ViewModelProvider.Factory {
                                    @Suppress("UNCHECKED_CAST")
                                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T =
                                        WorkoutSettingViewModel(workoutRepository, workoutId) as T
                                }
                            },
                    )

                WorkoutSettingScreen(
                    navController = navController,
                    viewModelSave = viewModel,
                    workoutSettingViewModel = workoutSettingViewModel,
                )
            }

            composable(
                route = Screens.ChooseExercises.route,
                arguments =
                    listOf(
                        navArgument(Screens.ChooseExercises.ARG_WORKOUT_ID) {
                            type = NavType.StringType
                            defaultValue = Screens.ChooseExercises.MODE_CREATE
                        },
                    ),
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec =
                            spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow,
                            ),
                    ) + fadeIn() + scaleIn(initialScale = 0.95f)
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy),
                    ) + fadeOut() + scaleOut(targetScale = 0.95f)
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                },
            ) { backStackEntry ->
                val workoutIdArg =
                    backStackEntry.arguments
                        ?.getString(Screens.ChooseExercises.ARG_WORKOUT_ID)
                val isEdit = Screens.ChooseExercises.isEditMode(workoutIdArg)
                val isHistory = Screens.ChooseExercises.isHistoryMode(workoutIdArg)

                val createWorkoutViewModel: CreateWorkoutViewModel? =
                    if (!isEdit && !isHistory) {
                        val parentEntry =
                            remember(backStackEntry) {
                                runCatching { navController.getBackStackEntry("create_workout_graph") }
                                    .getOrNull()
                            }
                        viewModel(
                            parentEntry ?: backStackEntry,
                            factory = featureFactory,
                        )
                    } else {
                        null
                    }

                val workoutSettingViewModel: WorkoutSettingViewModel? =
                    if (isEdit && !workoutIdArg.isNullOrBlank()) {
                        viewModel(
                            key = "WorkoutSettingViewModel_$workoutIdArg",
                            factory =
                                remember(workoutIdArg) {
                                    object : androidx.lifecycle.ViewModelProvider.Factory {
                                        @Suppress("UNCHECKED_CAST")
                                        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T =
                                            WorkoutSettingViewModel(workoutRepository, workoutIdArg) as T
                                    }
                                },
                        )
                    } else {
                        null
                    }

                ChooseExercises(
                    navController = navController,
                    viewModelSave = viewModel,
                    workoutinViewModel = workoutinModel,
                    createWorkoutViewModel = createWorkoutViewModel,
                    workoutSettingViewModel = workoutSettingViewModel,
                    oldWorkoutDetailsViewModel = if (isHistory) oldWorkoutDetailsViewModel else null,
                    activityInsideViewModel = activityInsideViewModel,
                    targetWorkoutId = workoutIdArg,
                )
            }

            composable(
                route = Screens.WorkoutLog.ROUTE_WITH_ARG,
                arguments = listOf(navArgument("workoutId") { type = NavType.StringType }),
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec =
                            spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow,
                            ),
                    ) + fadeIn() + scaleIn(initialScale = 0.95f)
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy),
                    ) + fadeOut() + scaleOut(targetScale = 0.95f)
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                },
            ) { backStackEntry ->
                val workoutId = backStackEntry.arguments?.getString("workoutId").toString()

                val workoutLogViewModel: WorkoutLogViewModel =
                    viewModel(
                        key = "WorkoutLogViewModel_$workoutId",
                        factory =
                            remember {
                                object : androidx.lifecycle.ViewModelProvider.Factory {
                                    @Suppress("UNCHECKED_CAST")
                                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T =
                                        WorkoutLogViewModel(
                                            workoutRepository,
                                            workoutId,
                                            workoutCompleteScreenViewModel,
                                            workoutCompleteAnalysisScreenViewModel,
                                        ) as T
                                }
                            },
                    )

                WorkoutLog(
                    navController = navController,
                    workoutLogViewModel = workoutLogViewModel,
                    workoutCompleteScreenViewModel,
                )
            }
            composable(
                route = Screens.PersonalInformationsScreen.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec =
                            spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow,
                            ),
                    ) + fadeIn() + scaleIn(initialScale = 0.95f)
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy),
                    ) + fadeOut() + scaleOut(targetScale = 0.95f)
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                },
            ) {
                PersonalInformationsScreen(
                    navController = navController,
                    personalInformationsScreenViewModel = personalInformationsScreenViewModel,
                )
            }
            composable(
                route = Screens.Meal.route,
                enterTransition = { fadeIn(animationSpec = tween(400)) },
                exitTransition = { fadeOut(animationSpec = tween(400)) },
            ) {
                Meal(navController = navController, authViewModel = authViewModel)
            }
            composable(
                route = Screens.WorkoutCompleteScreen.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec =
                            spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow,
                            ),
                    ) + fadeIn() + scaleIn(initialScale = 0.95f)
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy),
                    ) + fadeOut() + scaleOut(targetScale = 0.95f)
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                },
            ) {
                WorkoutCompleteScreen(
                    navController = navController,
                    workoutCompleteScreenViewModel = workoutCompleteScreenViewModel,
                    workoutCompleteAnalysisScreenViewModel,
                    leaderboardViewModel,
                )
            }
            composable(
                route = Screens.ProjectFollowersScreen.route,
                arguments = listOf(navArgument("nickname") { type = NavType.StringType }),
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec =
                            spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow,
                            ),
                    ) + fadeIn() + scaleIn(initialScale = 0.95f)
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy),
                    ) + fadeOut() + scaleOut(targetScale = 0.95f)
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                },
            ) {
                val listOwnerNickname = it.arguments?.getString("nickname").orEmpty()
                ProjectFollowersScreen(
                    navController = navController,
                    socialViewModel = socialViewModel,
                    authViewModel = authViewModel,
                    listOwnerNickname = listOwnerNickname,
                )
            }
            composable(
                route = Screens.ProjectFollowScreen.route,
                arguments = listOf(navArgument("nickname") { type = NavType.StringType }),
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec =
                            spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow,
                            ),
                    ) + fadeIn() + scaleIn(initialScale = 0.95f)
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy),
                    ) + fadeOut() + scaleOut(targetScale = 0.95f)
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                },
            ) {
                val listOwnerNickname = it.arguments?.getString("nickname").orEmpty()
                ProjectFollowScreen(
                    navController = navController,
                    socialViewModel = socialViewModel,
                    authViewModel = authViewModel,
                    listOwnerNickname = listOwnerNickname,
                )
            }
            composable(
                route = Screens.FindUsersScreen.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec =
                            spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow,
                            ),
                    ) + fadeIn() + scaleIn(initialScale = 0.95f)
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy),
                    ) + fadeOut() + scaleOut(targetScale = 0.95f)
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                },
            ) {
                FindUsersScreen(
                    navController = navController,
                    socialViewModel = socialViewModel,
                    authViewModel = authViewModel,
                )
            }
            composable(
                route = Screens.AllWorkouts.route,
                arguments =
                    listOf(
                        navArgument(Screens.AllWorkouts.ARG_FILTER) { type = NavType.StringType },
                    ),
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec =
                            spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow,
                            ),
                    ) + fadeIn() + scaleIn(initialScale = 0.95f)
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy),
                    ) + fadeOut() + scaleOut(targetScale = 0.95f)
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                },
            ) {
                val filter =
                    it.arguments?.getString(Screens.AllWorkouts.ARG_FILTER)
                        ?: Screens.AllWorkouts.FILTER_ALL
                AllWorkouts(
                    navController = navController,
                    homesViewModel = homesViewModel,
                    filter = filter,
                )
            }
            composable(
                route = Screens.OtherScreenProfile.route,
                arguments = listOf(navArgument("nickname") { type = NavType.StringType }),
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec =
                            spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow,
                            ),
                    ) + fadeIn() + scaleIn(initialScale = 0.95f)
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy),
                    ) + fadeOut() + scaleOut(targetScale = 0.95f)
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                },
            ) {
                val nickname = it.arguments?.getString("nickname")
                if (nickname != null) {
                    OtherScreenProfile(
                        navController = navController,
                        socialViewModel,
                        nickname,
                        profileViewModel,
                        oldWorkoutDetailsViewModel,
                        authViewModel,
                    )
                }
            }
            composable(
                route = Screens.WorkoutCompleteAnalysisScreen.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec =
                            spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow,
                            ),
                    ) + fadeIn() + scaleIn(initialScale = 0.95f)
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy),
                    ) + fadeOut() + scaleOut(targetScale = 0.95f)
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                },
            ) {
                WorkoutCompleteAnalysisScreen(
                    navController = navController,
                    workoutCompleteAnalysisScreenViewModel,
                )
            }
            composable(
                route = Screens.FaqcontactfeedbackScreen.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec =
                            spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow,
                            ),
                    ) + fadeIn() + scaleIn(initialScale = 0.95f)
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy),
                    ) + fadeOut() + scaleOut(targetScale = 0.95f)
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                },
            ) {
                FaqcontactfeedbackScreen(
                    navController = navController,
                    faqcontactfeedbackScreenViewModel = faqcontactfeedbackScreenViewModel,
                )
            }
            composable(
                route = Screens.OldWorkoutDetails.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec =
                            spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow,
                            ),
                    ) + fadeIn() + scaleIn(initialScale = 0.95f)
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy),
                    ) + fadeOut() + scaleOut(targetScale = 0.95f)
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                },
            ) {
                OldWorkoutDetails(
                    navController = navController,
                    oldWorkoutDetailsViewModel = oldWorkoutDetailsViewModel,
                    workoutCompleteScreenViewModel = workoutCompleteScreenViewModel,
                )
            }
            composable(
                route = Screens.NotificationScreen.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec =
                            spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow,
                            ),
                    ) + fadeIn() + scaleIn(initialScale = 0.95f)
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy),
                    ) + fadeOut() + scaleOut(targetScale = 0.95f)
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                },
            ) {
                NotificationScreen(navController = navController, socialViewModel = socialViewModel)
            }
            composable(
                route = Screens.ActivityInside.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec =
                            spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow,
                            ),
                    ) + fadeIn() + scaleIn(initialScale = 0.95f)
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy),
                    ) + fadeOut() + scaleOut(targetScale = 0.95f)
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                },
            ) {
                ActivityInside(
                    navController = navController,
                    activityInsideViewModel = activityInsideViewModel,
                )
            }
        }
    }
    if (infoDialog.value) {
        Dialog(onDismissRequest = { infoDialog.value = false }) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(color = Color.Transparent, shape = RoundedCornerShape(20.dp))
                        .clickable { infoDialog.value = false },
                contentAlignment = Alignment.Center,
            ) {
                AnimatedVisibility(
                    visible = infoDialog.value,
                    enter = fadeIn() + scaleIn(initialScale = 0.8f),
                    exit = fadeOut() + scaleOut(targetScale = 0.8f),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(vertical = 200.dp)
                                .background(color = GrozzSystemBar, shape = RoundedCornerShape(20.dp))
                                .clickable { infoDialog.value = false },
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 20.dp, horizontal = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Image(
                                painter = painterResource(R.drawable.grozzlogo),
                                contentDescription = null,
                                modifier =
                                    Modifier.size(150.dp).graphicsLayer(
                                        translationY = -150f,
                                    ),
                            )
                        }
                        Column(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 20.dp, horizontal = 20.dp)
                                    .graphicsLayer(
                                        translationY = 130f,
                                    ),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Text(
                                    text = "PR",
                                    color = Color.White,
                                    fontFamily = Lexend,
                                    fontSize = 20.sp,
                                )
                                Spacer(modifier = Modifier.width(1.dp))
                                Text(
                                    text = "RANKINGS",
                                    color = GrozzYellow,
                                    fontFamily = Lexend,
                                    fontSize = 20.sp,
                                )
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Row(
                                modifier = Modifier,
                                horizontalArrangement = Arrangement.spacedBy(20.dp),
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.checkcircleicon128),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp),
                                )
                                Spacer(Modifier.weight(1f))
                                Text(
                                    text = "Not Verified",
                                    color = Color.White,
                                    fontFamily = Lexend,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier,
                                horizontalArrangement = Arrangement.spacedBy(20.dp),
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.checkcircleicon128),
                                    contentDescription = null,
                                    tint = Color.Yellow,
                                    modifier = Modifier.size(20.dp),
                                )
                                Spacer(Modifier.weight(1f))
                                Text(
                                    text = "Waiting for Verification Lifting",
                                    color = Color.White,
                                    fontFamily = Lexend,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier,
                                horizontalArrangement = Arrangement.spacedBy(20.dp),
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.checkcircleicon128),
                                    contentDescription = null,
                                    tint = Color.Blue,
                                    modifier = Modifier.size(20.dp),
                                )
                                Spacer(Modifier.weight(1f))
                                Text(
                                    text = "Verified Lifting",
                                    color = Color.White,
                                    fontFamily = Lexend,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                            Spacer(modifier = Modifier.height(50.dp))
                            Row(
                                modifier = Modifier,
                                horizontalArrangement = Arrangement.spacedBy(20.dp),
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.arrowuploadprogress128icon),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp),
                                )
                                Spacer(Modifier.weight(1f))
                                Text(
                                    text = "Upload Your PR Lifting for Verification",
                                    color = Color.White,
                                    fontFamily = Lexend,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier,
                                horizontalArrangement = Arrangement.spacedBy(20.dp),
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.videocallfilledicon128),
                                    contentDescription = null,
                                    tint = GrozzYellow,
                                    modifier = Modifier.size(20.dp),
                                )
                                Spacer(Modifier.weight(1f))
                                Text(
                                    text = "Verified Lifting Video",
                                    color = Color.White,
                                    fontFamily = Lexend,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "* Verification controls updates every 6 hours.",
                                color = Color.White,
                                fontFamily = Lexend,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NavigationBar(
    navController: NavController,
    indexs: Int,
    flag: Boolean,
    flag2: Boolean,
    flag3: Boolean,
    flag4: Boolean,
) {
    val items = listOf("Home", "Activity", "LeaderBoard", "Meal")
    // Scaffold already owns layout slots. Disable NavigationBar's default
    // windowInsets and apply navigationBarsPadding once so insets aren't nested.
    val navItemColors =
        NavigationBarItemDefaults.colors(
            indicatorColor = GrozzSystemBar,
            selectedIconColor = GrozzYellow,
            unselectedIconColor = GrozzMuted,
        )
    NavigationBar(
        modifier =
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .background(GrozzSystemBar)
                .height(64.dp),
        containerColor = GrozzSystemBar,
        windowInsets = WindowInsets(0, 0, 0, 0),
    ) {
        var flag = flag
        var flag2 = flag2
        var flag3 = flag3
        var flag4 = flag4

        items.forEachIndexed { index, item ->
            if (index == 0) {
                NavigationBarItem(
                    selected = flag,
                    onClick = {
                        if (indexs == 0) {
                            flag = false
                        } else if (indexs == 1) {
                            flag = true
                            flag2 = false
                            flag3 = false
                            flag4 = false
                            navController.navigate(Screens.Home.route)
                        } else if (indexs == 2) {
                            flag = true
                            flag2 = false
                            flag3 = false
                            flag4 = false
                            navController.navigate(Screens.Home.route)
                        } else if (indexs == 3) {
                            flag = true
                            flag2 = false
                            flag3 = false
                            flag4 = false
                            navController.navigate(Screens.Home.route)
                        }
                    },
                    icon = {
                        GrozzBottomNavIcon(
                            selected = flag,
                            outlinedRes = R.drawable.home,
                            filledRes = R.drawable.home_filled,
                            contentDescription = "Home",
                        )
                    },
                    colors = navItemColors,
                )
            } else if (index == 1) {
                NavigationBarItem(
                    selected = flag2,
                    onClick = {
                        if (indexs == 0) {
                            flag = false
                            flag2 = true
                            flag3 = false
                            flag4 = false
                            navController.navigate(Screens.Activity.route)
                        } else if (indexs == 1) {
                        } else if (indexs == 2) {
                            flag = false
                            flag2 = true
                            flag3 = false
                            flag4 = false
                            navController.navigate(Screens.Activity.route)
                        } else if (indexs == 3) {
                            flag = false
                            flag2 = true
                            flag3 = false
                            flag4 = false
                            navController.navigate(Screens.Activity.route)
                        }
                    },
                    icon = {
                        GrozzBottomNavIcon(
                            selected = flag2,
                            outlinedRes = R.drawable.fitness_outline,
                            filledRes = R.drawable.fitness_filled,
                            contentDescription = "Activity",
                        )
                    },
                    colors = navItemColors,
                )
            } else if (index == 2) {
                NavigationBarItem(
                    selected = flag3,
                    onClick = {
                        if (indexs == 0) {
                            navController.navigate(Screens.LeaderBoard.route)
                            flag = false
                            flag2 = false
                            flag3 = true
                            flag4 = false
                        } else if (indexs == 1) {
                            flag = false
                            flag2 = false
                            flag3 = true
                            flag4 = false
                            navController.navigate(Screens.LeaderBoard.route)
                        } else if (indexs == 2) {
                        } else if (indexs == 3) {
                            flag = false
                            flag2 = false
                            flag3 = true
                            flag4 = false
                            navController.navigate(Screens.LeaderBoard.route)
                        }
                    },
                    icon = {
                        GrozzBottomNavIcon(
                            selected = flag3,
                            outlinedRes = R.drawable.leaderboard,
                            filledRes = R.drawable.leaderboard_filled,
                            contentDescription = "LeaderBoard",
                        )
                    },
                    colors = navItemColors,
                )
            } else if (index == 3) {
                NavigationBarItem(
                    selected = flag4,
                    onClick = {
                        if (indexs == 0) {
                            navController.navigate(Screens.Meal.route)
                            flag = false
                            flag2 = false
                            flag3 = false
                            flag4 = true
                        } else if (indexs == 1) {
                            navController.navigate(Screens.Meal.route)
                            flag = false
                            flag2 = false
                            flag3 = false
                            flag4 = true
                        } else if (indexs == 2) {
                            navController.navigate(Screens.Meal.route)
                            flag = false
                            flag2 = false
                            flag3 = false
                            flag4 = true
                        } else if (indexs == 3) {
                        }
                    },
                    icon = {
                        GrozzBottomNavIcon(
                            selected = flag4,
                            outlinedRes = R.drawable.meal,
                            filledRes = R.drawable.meal_filled,
                            contentDescription = "Meal",
                        )
                    },
                    colors = navItemColors,
                )
            }
        }
    }
}

@Composable
fun NavigationBarLeaderboard(
    navController: NavController,
    indexs: Int,
    flag: Boolean,
    flag2: Boolean,
    flag3: Boolean,
    flag4: Boolean,
    rankInfo: Pair<Int, LeaderboardEntry>? = null,
    leaderboardViewModel: LeaderboardViewModel,
    infoDialog: (Boolean) -> Unit,
) {
    val items = listOf("Home", "Activity", "LeaderBoard", "Meal")
    val isUploadProofClicked = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Bottom,
    ) {
        if (rankInfo != null) {
            val (rank, userEntry) = rankInfo
            val rankColor =
                when (rank) {
                    1 -> GrozzYellow
                    2 -> Color(0xFFC0C0C0)
                    3 -> Color(0xFF88540B)
                    else -> GrozzMuted
                }
            val statusTint =
                when (userEntry.verificationStatus) {
                    "verified" -> Color(0xFF5B9BD5)
                    "pendent" -> GrozzYellow
                    else -> GrozzOnBackground
                }
            val photoModel = userEntry.userPhotoUri.takeIf { it?.isNotBlank() == true } ?: R.drawable.grozzlogo

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(GrozzSystemBar)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(GrozzSurface.copy(alpha = 0.95f))
                            .border(1.dp, GrozzYellow.copy(alpha = 0.55f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = rank.toString(),
                        color = rankColor,
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = Lexend,
                        modifier = Modifier.width(28.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    AsyncImage(
                        model = photoModel,
                        contentDescription = null,
                        modifier =
                            Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = userEntry.userName,
                                color = GrozzOnBackground,
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = Lexend,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false),
                            )
                            if (userEntry.hasPro) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier =
                                        Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(GrozzYellow)
                                            .padding(horizontal = 8.dp, vertical = 2.dp),
                                ) {
                                    Text(
                                        text = "PRO",
                                        color = GrozzOnPrimary,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                        Text(
                            text = userEntry.exerciseName,
                            color = GrozzTextSecondary,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = Lexend,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${userEntry.weight.toInt()} KG",
                            color = GrozzOnBackground,
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = Lexend,
                        )
                        Text(
                            text = "1RM",
                            color = GrozzYellow,
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = Lexend,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    when (userEntry.verificationStatus) {
                        "verified", "pendent", "notVerified" -> {
                            Icon(
                                painter = painterResource(R.drawable.checkcircleicon128),
                                contentDescription = "Verification status",
                                tint = statusTint,
                                modifier =
                                    Modifier
                                        .size(20.dp)
                                        .clickable(onClick = { infoDialog(true) }),
                            )
                        }
                    }
                    if (userEntry.verificationStatus == "notVerified" && !isUploadProofClicked.value) {
                        Spacer(modifier = Modifier.width(10.dp))
                        ProofUploadSectionLeaderboard(
                            onUriSelected = { uri ->
                                val uid = FirebaseAuth.getInstance().currentUser?.uid
                                if (!uid.isNullOrBlank()) {
                                    leaderboardViewModel.uploadPrProof(
                                        uri,
                                        uid,
                                        userEntry.exerciseName,
                                        userEntry.weight,
                                        userEntry.userName,
                                    )
                                }
                            },
                            isUploadedClick = { isUploadProofClicked.value = true },
                        )
                    }
                }
            }
        }
        val navItemColors =
            NavigationBarItemDefaults.colors(
                indicatorColor = GrozzSystemBar,
                selectedIconColor = GrozzYellow,
                unselectedIconColor = GrozzMuted,
            )
        NavigationBar(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .background(GrozzSystemBar)
                    .height(64.dp),
            containerColor = GrozzSystemBar,
            windowInsets = WindowInsets(0, 0, 0, 0),
        ) {
            var flag = flag
            var flag2 = flag2
            var flag3 = flag3
            var flag4 = flag4

            items.forEachIndexed { index, item ->
                if (index == 0) {
                    NavigationBarItem(
                        selected = flag,
                        onClick = {
                            if (indexs == 0) {
                                flag = false
                            } else if (indexs == 1) {
                                flag = true
                                flag2 = false
                                flag3 = false
                                flag4 = false
                                navController.navigate(Screens.Home.route)
                            } else if (indexs == 2) {
                                flag = true
                                flag2 = false
                                flag3 = false
                                flag4 = false
                                navController.navigate(Screens.Home.route)
                            } else if (indexs == 3) {
                                flag = true
                                flag2 = false
                                flag3 = false
                                flag4 = false
                                navController.navigate(Screens.Home.route)
                            }
                        },
                        icon = {
                            GrozzBottomNavIcon(
                                selected = flag,
                                outlinedRes = R.drawable.home,
                                filledRes = R.drawable.home_filled,
                                contentDescription = "Home",
                            )
                        },
                        colors = navItemColors,
                    )
                } else if (index == 1) {
                    NavigationBarItem(
                        selected = flag2,
                        onClick = {
                            if (indexs == 0) {
                                flag = false
                                flag2 = true
                                flag3 = false
                                flag4 = false
                                navController.navigate(Screens.Activity.route)
                            } else if (indexs == 1) {
                            } else if (indexs == 2) {
                                flag = false
                                flag2 = true
                                flag3 = false
                                flag4 = false
                                navController.navigate(Screens.Activity.route)
                            } else if (indexs == 3) {
                                flag = false
                                flag2 = true
                                flag3 = false
                                flag4 = false
                                navController.navigate(Screens.Activity.route)
                            }
                        },
                        icon = {
                            GrozzBottomNavIcon(
                                selected = flag2,
                                outlinedRes = R.drawable.fitness_outline,
                                filledRes = R.drawable.fitness_filled,
                                contentDescription = "Activity",
                            )
                        },
                        colors = navItemColors,
                    )
                } else if (index == 2) {
                    NavigationBarItem(
                        selected = flag3,
                        onClick = {
                            if (indexs == 0) {
                                navController.navigate(Screens.LeaderBoard.route)
                                flag = false
                                flag2 = false
                                flag3 = true
                                flag4 = false
                            } else if (indexs == 1) {
                                flag = false
                                flag2 = false
                                flag3 = true
                                flag4 = false
                                navController.navigate(Screens.LeaderBoard.route)
                            } else if (indexs == 2) {
                            } else if (indexs == 3) {
                                flag = false
                                flag2 = false
                                flag3 = true
                                flag4 = false
                                navController.navigate(Screens.LeaderBoard.route)
                            }
                        },
                        icon = {
                            GrozzBottomNavIcon(
                                selected = flag3,
                                outlinedRes = R.drawable.leaderboard,
                                filledRes = R.drawable.leaderboard_filled,
                                contentDescription = "LeaderBoard",
                            )
                        },
                        colors = navItemColors,
                    )
                } else if (index == 3) {
                    NavigationBarItem(
                        selected = flag4,
                        onClick = {
                            if (indexs == 0) {
                                navController.navigate(Screens.Meal.route)
                                flag = false
                                flag2 = false
                                flag3 = false
                                flag4 = true
                            } else if (indexs == 1) {
                                navController.navigate(Screens.Meal.route)
                                flag = false
                                flag2 = false
                                flag3 = false
                                flag4 = true
                            } else if (indexs == 2) {
                                navController.navigate(Screens.Meal.route)
                                flag = false
                                flag2 = false
                                flag3 = false
                                flag4 = true
                            } else if (indexs == 3) {
                            }
                        },
                        icon = {
                            GrozzBottomNavIcon(
                                selected = flag4,
                                outlinedRes = R.drawable.meal,
                                filledRes = R.drawable.meal_filled,
                                contentDescription = "Meal",
                            )
                        },
                        colors = navItemColors,
                    )
                }
            }
        }
    }
}

@Composable
private fun GrozzBottomNavIcon(selected: Boolean, outlinedRes: Int, filledRes: Int, contentDescription: String) {
    Icon(
        painter = painterResource(id = if (selected) filledRes else outlinedRes),
        contentDescription = contentDescription,
        modifier = Modifier.size(28.dp),
    )
}

@Composable
fun ProofUploadSectionLeaderboard(onUriSelected: (android.net.Uri) -> Unit, isUploadedClick: () -> Unit) {
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
        ) { video: Uri? ->
            video?.let { video ->
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(context, video)
                val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                val durationInMs = time?.toLong() ?: 0

                if (durationInMs <= 15_000) {
                    onUriSelected(video)
                    isUploadedClick()
                } else {
                    Toast
                        .makeText(
                            context,
                            "Video must be under 15 seconds.",
                            Toast.LENGTH_SHORT,
                        ).show()
                }
            }
        }
    Icon(
        painter = painterResource(id = R.drawable.arrowuploadprogress128icon),
        contentDescription = null,
        tint = GrozzYellow,
        modifier =
            Modifier.size(20.dp).clickable {
                launcher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly),
                )
            },
    )
}
