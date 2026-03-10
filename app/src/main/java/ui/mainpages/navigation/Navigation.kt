package ui.mainpages.navigation

import OtherScreenProfile
import SocialViewModel
import WorkoutCompleteScreen
import activity.inside.ActivityInside
import activity.inside.CreateWorkout
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import ui.mainpages.inside.HomesSettings
import ui.mainpages.inside.NotificationScreen
import ui.mainpages.inside.OldWorkoutDetails
import ui.mainpages.inside.PersonalInformationsScreen
import ui.mainpages.inside.Profile
import ui.mainpages.inside.ProjectFollowScreen
import ui.mainpages.inside.ProjectFollowersScreen
import ui.mainpages.inside.WorkoutCompleteAnalysisScreen
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
import viewmodel.ProjectFitnessViewModel
import viewmodel.ViewModelProfile
import viewmodel.ViewModelSave
import viewmodel.WorkoutinViewModel

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Navigation(workoutRepository: WorkoutRepository) {
    val context = androidx.compose.ui.platform.LocalContext.current


    val sharedPref = context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
    val isBoardingCompleted = sharedPref.getBoolean("is_boarding_completed", false)
    val isUserLoggedIn = FirebaseAuth.getInstance().currentUser != null
    val viewModel: ViewModelSave = viewModel()
    val projectFitnessViewModel: ProjectFitnessViewModel = ProjectFitnessViewModel()
    val viewModelProfile: ViewModelProfile = ViewModelProfile()
    val firestoreRepository: FirestoreRepository = FirestoreRepository()
    val socialViewModel: SocialViewModel = SocialViewModel(repository = firestoreRepository)
    val navController = rememberNavController()
    val authViewModel: AuthViewModel =
        AuthViewModel(AuthRepository(), UserRepository(), workoutRepository)
    val profileViewModel: ProfileViewModel =
        ProfileViewModel(UserRepository(), workoutRepository, StorageRepository())
    val workoutinModel: WorkoutinViewModel = WorkoutinViewModel(WorkoutinRepository())
    val currentUser: FirebaseAuth = FirebaseAuth.getInstance()
    val chooseExercisesViewModel: ChooseExercisesViewModel = ChooseExercisesViewModel()
    val userRepository: UserRepository = UserRepository()
    val activityInsideViewModel: ActivityInsideViewModel =
        ActivityInsideViewModel(workoutRepository)
    val faqcontactfeedbackScreenViewModel: FaqcontactfeedbackScreenViewModel = viewModel(
        factory = remember {
            WorkoutViewModelFactory(
                workoutRepository,
                currentUser,
                userRepository,
                profileViewModel,
                authViewModel
            )
        }
    )
    val personalInformationsScreenViewModel: PersonalInformationsScreenViewModel = viewModel(
        factory = remember {
            WorkoutViewModelFactory(
                workoutRepository,
                currentUser,
                userRepository,
                profileViewModel,
                authViewModel
            )
        }
    )
    val homesViewModel: HomesViewModel = viewModel(
        factory = remember {
            WorkoutViewModelFactory(
                workoutRepository,
                currentUser,
                userRepository,
                profileViewModel,
                authViewModel
            )
        }
    )
    val activityViewModel: ActivityViewModel = viewModel(
        factory = remember {
            WorkoutViewModelFactory(
                repository = workoutRepository,
                auth = currentUser, userRepository, profileViewModel, authViewModel
            )
        },
    )
    val workoutCompleteScreenViewModel: WorkoutCompleteScreenViewModel = viewModel(
        factory = remember {
            WorkoutViewModelFactory(
                repository = workoutRepository,
                auth = currentUser, userRepository, profileViewModel, authViewModel
            )
        },
    )
    val workoutCompleteAnalysisScreenViewModel: WorkoutCompleteAnalysisScreenViewModel = viewModel(
        factory = remember {
            WorkoutViewModelFactory(
                repository = workoutRepository,
                auth = currentUser, userRepository, profileViewModel, authViewModel
            )
        }
    )
    val oldWorkoutDetailsViewModel: OldWorkoutDetailsViewModel = viewModel(
        factory = remember {
            WorkoutViewModelFactory(
                repository = workoutRepository,
                auth = currentUser, userRepository, profileViewModel, authViewModel
            )
        }
    )
    val leaderboardViewModel: LeaderboardViewModel = viewModel(
        factory = remember {
            WorkoutViewModelFactory(
                repository = workoutRepository,
                auth = currentUser, userRepository, profileViewModel, authViewModel
            )
        }
    )
    val workoutSettingViewModel: WorkoutSettingViewModel = viewModel(
        factory = remember {
            WorkoutViewModelFactory(
                repository = workoutRepository,
                auth = currentUser, userRepository, profileViewModel, authViewModel
            )
        }
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121417))
    )
    {
        NavHost(
            navController = navController, startDestination = when {
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
            }
        ) {

            navigation(
                route = "create_workout_graph",
                startDestination = Screens.ChooseExercises.route
            ) {

                composable(
                    Screens.ChooseExercises.route,
                    enterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { 1000 },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ) + fadeIn() + scaleIn(initialScale = 0.95f)
                    },
                    exitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { 1000 },
                            animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy)
                        ) + fadeOut() + scaleOut(targetScale = 0.95f)
                    },
                    popEnterTransition = {

                        slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                    },
                    popExitTransition = {

                        slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                    }

                ) { backStackEntry ->

                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry("create_workout_graph")
                    }

                    val createWorkoutViewModel: CreateWorkoutViewModel = viewModel(
                        parentEntry,
                        factory = WorkoutViewModelFactory(
                            workoutRepository,
                            currentUser,
                            userRepository,
                            profileViewModel,
                            authViewModel
                        )
                    )

                    CreateWorkout(
                        navController = navController,
                        createWorkoutViewModel = createWorkoutViewModel,
                        chooseExercisesViewModel = chooseExercisesViewModel
                    )
                }

                composable(
                    Screens.CreateWorkout.route,
                    enterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { 1000 },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ) + fadeIn() + scaleIn(initialScale = 0.95f)
                    },
                    exitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { 1000 },
                            animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy)
                        ) + fadeOut() + scaleOut(targetScale = 0.95f)
                    },
                    popEnterTransition = {

                        slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                    },
                    popExitTransition = {

                        slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                    }) { backStackEntry ->

                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry("create_workout_graph")
                    }

                    val createWorkoutViewModel: CreateWorkoutViewModel = viewModel(
                        parentEntry,
                        factory = WorkoutViewModelFactory(
                            workoutRepository,
                            currentUser,
                            userRepository,
                            profileViewModel,
                            authViewModel
                        )
                    )

                    ChooseExercises(
                        navController = navController,
                        viewModelSave = viewModel,
                        workoutinViewModel = workoutinModel,
                        createWorkoutViewModel = createWorkoutViewModel,
                        activityInsideViewModel = activityInsideViewModel
                    )
                }

                composable(
                    Screens.LeaderBoard.route,
                    enterTransition = { fadeIn(animationSpec = tween(400)) },
                    exitTransition = { fadeOut(animationSpec = tween(400)) }
                ) { backStackEntry ->

                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry("create_workout_graph")
                    }

                    val leaderboardViewModel: LeaderboardViewModel = viewModel(
                        parentEntry,
                        factory = WorkoutViewModelFactory(
                            workoutRepository,
                            currentUser,
                            userRepository,
                            profileViewModel,
                            authViewModel
                        )
                    )

                    LeaderBoard(
                        navController = navController,
                        authViewModel = authViewModel,
                        leaderboardViewModel = leaderboardViewModel
                    )
                }
            }
            composable(
                route = Screens.HomesSettings.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn() + scaleIn(initialScale = 0.95f)
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy)
                    ) + fadeOut() + scaleOut(targetScale = 0.95f)
                },
                popEnterTransition = {

                    slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                },
                popExitTransition = {

                    slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                }) {
                HomesSettings(
                    navController = navController,
                    viewModel,
                    projectFitnessViewModel,
                    viewModelProfile,
                    authViewModel
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
                exitTransition = { fadeOut(animationSpec = tween(400)) }

            ) {
                Home(
                    navController = navController,
                    viewModel,
                    projectFitnessViewModel,
                    viewModelProfile = viewModelProfile,
                    authViewModel = authViewModel,
                    homesViewModel = homesViewModel,
                    socialViewModel = socialViewModel,
                    workoutSettingViewModel = workoutSettingViewModel
                )
            }
            composable(
                route = Screens.Activity.route,
                enterTransition = { fadeIn(animationSpec = tween(400)) },
                exitTransition = { fadeOut(animationSpec = tween(400)) }) {
                Activity(
                    navController = navController,
                    activityViewModel = activityViewModel,
                    authViewModel = authViewModel,
                    homesViewModel = homesViewModel
                )
            }
            composable(
                route = Screens.Home.Profile.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn() + scaleIn(initialScale = 0.95f)
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy)
                    ) + fadeOut() + scaleOut(targetScale = 0.95f)
                },
                popEnterTransition = {

                    slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                },
                popExitTransition = {

                    slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                }
            ) {
                Profile(
                    navController = navController,
                    viewModelProfile = viewModelProfile,
                    socialViewModel = socialViewModel,
                    authViewModel = authViewModel,
                    profileViewModel = profileViewModel,
                    workoutScreenCompleteScreenViewModel = workoutCompleteScreenViewModel,
                    oldWorkoutDetailsViewModel = oldWorkoutDetailsViewModel
                )
            }
            composable(
                route = Screens.WorkoutSettingScreen.routeWithArg,
                arguments = listOf(navArgument("workoutId") { type = NavType.StringType }),
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn() + scaleIn(initialScale = 0.95f)
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy)
                    ) + fadeOut() + scaleOut(targetScale = 0.95f)
                },
                popEnterTransition = {

                    slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                },
                popExitTransition = {

                    slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                }
            ) { backStackEntry ->
                val workoutId = backStackEntry.arguments?.getString("workoutId").toString()

                val workoutSettingViewModel: WorkoutSettingViewModel = viewModel(
                    key = "WorkoutSettingViewModel_$workoutId",
                    factory = remember {
                        object : androidx.lifecycle.ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                                return WorkoutSettingViewModel(workoutRepository, workoutId) as T
                            }
                        }
                    }
                )

                WorkoutSettingScreen(
                    navController = navController,
                    viewModelSave = viewModel,
                    workoutSettingViewModel = workoutSettingViewModel
                )
            }

            composable(
                route = Screens.WorkoutLog.routeWithArg,
                arguments = listOf(navArgument("workoutId") { type = NavType.StringType }),
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn() + scaleIn(initialScale = 0.95f)
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy)
                    ) + fadeOut() + scaleOut(targetScale = 0.95f)
                },
                popEnterTransition = {

                    slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                },
                popExitTransition = {

                    slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                }
            )
            { backStackEntry ->
                val workoutId = backStackEntry.arguments?.getString("workoutId").toString()

                val workoutLogViewModel: WorkoutLogViewModel = viewModel(
                    key = "WorkoutLogViewModel_$workoutId",
                    factory = remember {
                        object : androidx.lifecycle.ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                                return WorkoutLogViewModel(
                                    workoutRepository,
                                    workoutId,
                                    workoutCompleteScreenViewModel,
                                    workoutCompleteAnalysisScreenViewModel
                                ) as T
                            }
                        }
                    }
                )

                WorkoutLog(
                    navController = navController,
                    workoutLogViewModel = workoutLogViewModel,
                    workoutCompleteScreenViewModel
                )
            }
            composable(
                route = Screens.PersonalInformationsScreen.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn() + scaleIn(initialScale = 0.95f)
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy)
                    ) + fadeOut() + scaleOut(targetScale = 0.95f)
                },
                popEnterTransition = {

                    slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                },
                popExitTransition = {

                    slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                }) {
                PersonalInformationsScreen(
                    navController = navController,
                    personalInformationsScreenViewModel = personalInformationsScreenViewModel
                )
            }
            composable(
                route = Screens.Meal.route,
                enterTransition = { fadeIn(animationSpec = tween(400)) },
                exitTransition = { fadeOut(animationSpec = tween(400)) })
            {
                Meal(navController = navController, authViewModel = authViewModel)
            }
            composable(
                route = Screens.WorkoutCompleteScreen.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn() + scaleIn(initialScale = 0.95f)
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy)
                    ) + fadeOut() + scaleOut(targetScale = 0.95f)
                },
                popEnterTransition = {

                    slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                },
                popExitTransition = {

                    slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                }) {

                WorkoutCompleteScreen(
                    navController = navController,
                    workoutCompleteScreenViewModel = workoutCompleteScreenViewModel,
                    workoutCompleteAnalysisScreenViewModel,
                    leaderboardViewModel
                )
            }
            composable(
                route = Screens.ProjectFollowersScreen.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn() + scaleIn(initialScale = 0.95f)
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy)
                    ) + fadeOut() + scaleOut(targetScale = 0.95f)
                },
                popEnterTransition = {

                    slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                },
                popExitTransition = {

                    slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                })
            {
                ProjectFollowersScreen(
                    navController = navController,
                    socialViewModel = socialViewModel,
                    authViewModel = authViewModel
                )
            }
            composable(
                route = Screens.ProjectFollowScreen.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn() + scaleIn(initialScale = 0.95f)
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy)
                    ) + fadeOut() + scaleOut(targetScale = 0.95f)
                },
                popEnterTransition = {

                    slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                },
                popExitTransition = {

                    slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                })
            {
                ProjectFollowScreen(
                    navController = navController,
                    socialViewModel = socialViewModel,
                    authViewModel = authViewModel
                )
            }
            composable(
                route = Screens.AllWorkouts.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn() + scaleIn(initialScale = 0.95f)
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy)
                    ) + fadeOut() + scaleOut(targetScale = 0.95f)
                },
                popEnterTransition = {

                    slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                },
                popExitTransition = {

                    slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                })
            {
                AllWorkouts(navController = navController, homesViewModel = homesViewModel)
            }
            composable(
                route = Screens.OtherScreenProfile.route,
                arguments = listOf(navArgument("nickname") { type = NavType.StringType }),
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn() + scaleIn(initialScale = 0.95f)
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy)
                    ) + fadeOut() + scaleOut(targetScale = 0.95f)
                },
                popEnterTransition = {

                    slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                },
                popExitTransition = {

                    slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                }
            ) {
                val nickname = it.arguments?.getString("nickname")
                if (nickname != null) {
                    Log.d("live?", nickname)
                    OtherScreenProfile(
                        navController = navController,
                        socialViewModel,
                        nickname,
                        profileViewModel,
                        oldWorkoutDetailsViewModel,
                        authViewModel
                    )
                } else {
                    Log.d("seeees", "looool")
                }
            }
            composable(
                route = Screens.WorkoutCompleteAnalysisScreen.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn() + scaleIn(initialScale = 0.95f)
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy)
                    ) + fadeOut() + scaleOut(targetScale = 0.95f)
                },
                popEnterTransition = {

                    slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                },
                popExitTransition = {

                    slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                }) {
                WorkoutCompleteAnalysisScreen(
                    navController = navController,
                    workoutCompleteAnalysisScreenViewModel
                )
            }
            composable(
                route = Screens.FaqcontactfeedbackScreen.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn() + scaleIn(initialScale = 0.95f)
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy)
                    ) + fadeOut() + scaleOut(targetScale = 0.95f)
                },
                popEnterTransition = {

                    slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                },
                popExitTransition = {

                    slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                }) {
                FaqcontactfeedbackScreen(
                    navController = navController,
                    faqcontactfeedbackScreenViewModel = faqcontactfeedbackScreenViewModel
                )
            }
            composable(
                route = Screens.OldWorkoutDetails.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn() + scaleIn(initialScale = 0.95f)
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy)
                    ) + fadeOut() + scaleOut(targetScale = 0.95f)
                },
                popEnterTransition = {

                    slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                },
                popExitTransition = {

                    slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                }
            ) {
                OldWorkoutDetails(
                    navController = navController,
                    oldWorkoutDetailsViewModel = oldWorkoutDetailsViewModel,
                    workoutCompleteScreenViewModel = workoutCompleteScreenViewModel
                )
            }
            composable(
                route = Screens.NotificationScreen.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn() + scaleIn(initialScale = 0.95f)
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy)
                    ) + fadeOut() + scaleOut(targetScale = 0.95f)
                },
                popEnterTransition = {

                    slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                },
                popExitTransition = {

                    slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                }
            ) {
                NotificationScreen(navController = navController, socialViewModel = socialViewModel)
            }
            composable(
                route = Screens.ActivityInside.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn() + scaleIn(initialScale = 0.95f)
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy)
                    ) + fadeOut() + scaleOut(targetScale = 0.95f)
                },
                popEnterTransition = {

                    slideInHorizontally(initialOffsetX = { -1000 }) + scaleIn(initialScale = 0.9f) + fadeIn()
                },
                popExitTransition = {

                    slideOutHorizontally(targetOffsetX = { 1000 }) + scaleOut(targetScale = 0.9f) + fadeOut()
                }
            ) {
                ActivityInside(
                    navController = navController,
                    activityInsideViewModel = activityInsideViewModel
                )
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
    flag4: Boolean
) {

    val items = listOf("Home", "Activity", "LeaderBoard", "Meal")
    NavigationBar(
        Modifier
            .height(64.dp)
            .fillMaxWidth()
            .background(Color(0xFF121417)),
        containerColor = Color(0xFF121417)
    ) {

        var flag = flag
        var flag2 = flag2
        var flag3 = flag3
        var flag4 = flag4


        var color = if (flag) {
            Color(0xFFF1C40F)
        } else Color(0xFFB5B5B5)

        var color2 = if (flag2) {
            Color(0xFFF1C40F)
        } else Color(0xFFB5B5B5)
        var color3 = if (flag3) {
            Color(0xFFF1C40F)
        } else Color(0xFFB5B5B5)
        var color4 = if (flag4) {
            Color(0xFFF1C40F)
        } else Color(0xFFB5B5B5)

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
                        Icon(
                            painterResource(id = R.drawable.home),
                            contentDescription = null,
                            Modifier
                                .size(30.dp)
                        )
                    },
                    Modifier.background(Color(0xFF121417)),
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color(0xFF121417),
                        unselectedIconColor = color,
                        selectedIconColor = color
                    ),
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
                        Icon(
                            painterResource(id = R.drawable.dumbbellicon128),
                            contentDescription = null,
                            Modifier
                                .size(30.dp)
                        )
                    },
                    Modifier.background(Color(0xFF121417)),
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color(0xFF121417),
                        unselectedIconColor = color2,
                        selectedIconColor = color2
                    ),
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
                        Icon(
                            painterResource(id = R.drawable.leaderboard),
                            contentDescription = null,
                            Modifier
                                .size(30.dp)
                        )
                    },
                    Modifier.background(Color(0xFF121417)),
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color(0xFF121417),
                        unselectedIconColor = color3,
                        selectedIconColor = color3
                    ),
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
                        Icon(
                            painterResource(id = R.drawable.meal),
                            contentDescription = null,
                            Modifier
                                .size(30.dp)
                        )
                    },
                    Modifier.background(Color(0xFF121417)),
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color(0xFF121417),
                        unselectedIconColor = color4,
                        selectedIconColor = color4
                    ),
                )
            }

        }
    }
}

@Composable
fun NavigationBarLeaderboard(
    navController: NavController,
    indexs: Int,
    flag: Boolean, flag2: Boolean, flag3: Boolean, flag4: Boolean,
    rankInfo: Pair<Int, LeaderboardEntry>? = null
) {
    val items = listOf("Home", "Activity", "LeaderBoard", "Meal")
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Bottom
    ) {
        if (rankInfo != null) {
            val (rank, userEntry) = rankInfo

            Row(modifier = Modifier.padding(horizontal = 30.dp, vertical = 8.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1C40F).copy(alpha = 0.8f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$rank.",
                            color = Color.Black,
                            fontFamily = FontFamily(Font(R.font.lexendbold)),
                            fontSize = 16.sp
                        )
                        Spacer(Modifier.width(12.dp))
                        AsyncImage(
                            model = if (userEntry.userPhotoUri != "") userEntry.userPhotoUri else R.drawable.grozzholdsdumbbellbothhandsnobackgroundxml,
                            contentDescription = null,
                            modifier = Modifier
                                .size(35.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.width(12.dp))
                        Column() {
                            Text(
                                text = "YOU",
                                color = Color.Black,
                                fontFamily = FontFamily(Font(R.font.lexendbold))
                            )
                            Text(
                                text = "PERSONAL RECORD",
                                color = Color.Gray,
                                fontFamily = FontFamily(Font(R.font.lexendbold)),
                                fontSize = 10.sp
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "${userEntry.weight.toInt()} kg",
                            color = Color.Black,
                            fontFamily = FontFamily(Font(R.font.lexendbold))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        if (userEntry.verificationStatus == "verified") {
                            Icon(
                                painter = painterResource(R.drawable.checkcircleicon128),
                                contentDescription = null,
                                tint = Color.Blue,
                                modifier = Modifier.size(20.dp)
                            )
                        } else if (userEntry.verificationStatus == "pendent") {
                            Icon(
                                painter = painterResource(R.drawable.checkcircleicon128),
                                contentDescription = null,
                                tint = Color.Yellow,
                                modifier = Modifier.size(20.dp)
                            )
                        } else if (userEntry.verificationStatus == "notVerified") {
                            IconButton(onClick = {}) {
                                Icon(
                                    painter = painterResource(R.drawable.arrowuploadprogress128icon),
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        NavigationBar(
            Modifier
                .height(60.dp)
                .fillMaxWidth()
                .background(Color(0xFF121417)),
            containerColor = Color(0xFF121417)
        ) {

            var flag = flag
            var flag2 = flag2
            var flag3 = flag3
            var flag4 = flag4


            var color = if (flag) {
                Color(0xFFF1C40F)
            } else Color(0xFFB5B5B5)

            var color2 = if (flag2) {
                Color(0xFFF1C40F)
            } else Color(0xFFB5B5B5)
            var color3 = if (flag3) {
                Color(0xFFF1C40F)
            } else Color(0xFFB5B5B5)
            var color4 = if (flag4) {
                Color(0xFFF1C40F)
            } else Color(0xFFB5B5B5)

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
                            Icon(
                                painterResource(id = R.drawable.home),
                                contentDescription = null,
                                Modifier
                                    .size(30.dp)
                            )
                        },
                        Modifier.background(Color(0xFF121417)),
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color(0xFF121417),
                            unselectedIconColor = color,
                            selectedIconColor = color
                        ),
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
                            Icon(
                                painterResource(id = R.drawable.dumbbellicon128),
                                contentDescription = null,
                                Modifier
                                    .size(30.dp)
                            )
                        },
                        Modifier.background(Color(0xFF121417)),
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color(0xFF121417),
                            unselectedIconColor = color2,
                            selectedIconColor = color2
                        ),
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
                            Icon(
                                painterResource(id = R.drawable.leaderboard),
                                contentDescription = null,
                                Modifier
                                    .size(30.dp)
                            )
                        },
                        Modifier.background(Color(0xFF121417)),
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color(0xFF121417),
                            unselectedIconColor = color3,
                            selectedIconColor = color3
                        ),
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
                            Icon(
                                painterResource(id = R.drawable.meal),
                                contentDescription = null,
                                Modifier
                                    .size(30.dp)
                            )
                        },
                        Modifier.background(Color(0xFF121417)),
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color(0xFF121417),
                            unselectedIconColor = color4,
                            selectedIconColor = color4
                        ),
                    )
                }
            }
        }
    }
}