@file:SuppressLint(
    "StateFlowValueCalledInComposition",
    "UnusedMaterial3ScaffoldPaddingParameter"
)

package ui.mainpages.mainpages

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.projectfitness.R
import com.example.projectfitness.data.local.viewmodel.HomesViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ui.mainpages.navigation.NavigationBar
import ui.mainpages.navigation.Screens
import viewmodel.AuthViewModel
import viewmodel.ProjectFitnessViewModel
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
    homesViewModel: HomesViewModel
) {
    // =========================================================
    // Essentials
    // =========================================================
    val context = LocalContext.current
    val scopes = rememberCoroutineScope()
    val db = remember { FirebaseFirestore.getInstance() }
    val uid = remember { Firebase.auth.currentUser?.uid }

    val configuration = LocalConfiguration.current
    val screenHeightDp = configuration.screenHeightDp

    // =========================================================
    // Repository / Local DB (senin mevcut yapın)
    // =========================================================

    var lazyListState: LazyListState = rememberLazyListState()
    var lazyListState2: LazyListState = rememberLazyListState()

    // =========================================================
    // SharedPreferences (senin mevcut)
    // =========================================================
    val sharedPreferences =
        remember { context.getSharedPreferences("rememberbuttonStatus", Context.MODE_PRIVATE) }
    val sharedPreferences2 =
        remember { context.getSharedPreferences("workoutIdNumber", Context.MODE_PRIVATE) }

    val workoutIdNumberState = remember {
        mutableIntStateOf(sharedPreferences2.getInt("number", 2))
    }
    var rememberMeBoo by remember {
        mutableStateOf(sharedPreferences.getBoolean("bool", false))
    }

    // =========================================================
    // UI States
    // =========================================================
    var clickedAdd by remember { mutableStateOf(false) }
    var clickedProfile by remember { mutableStateOf(false) }

    var showMenuSheet by remember { mutableStateOf(false) }
    var showWorkoutRemoveSheet by remember { mutableStateOf(false) }

    val menuSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val removeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Long press state
    var isPressed by remember { mutableStateOf(false) }

    // =========================================================
    // Firebase: profile image url
    // =========================================================
    val storageRef = remember { Firebase.storage.reference }
    val profileRef = remember(uid) {
        storageRef.child("gs://projectfitness-ddfeb.appspot.com/profile_photos/$uid/profile.jpg")
    }

    val workouts by homesViewModel.workoutsFlow.collectAsState(initial = emptyList())
    val challangesWorkouts = workouts.filter { it.workout.workoutType.contains("challange", ignoreCase = true) }
    val coachWorkouts = workouts.filter { it.workout.workoutType.contains("coach", ignoreCase = true) }

    LaunchedEffect(uid) {
        profileRef.downloadUrl
            .addOnSuccessListener { uri ->
                viewModelProfile.selectedImageUri.value = uri.toString()
                Log.d("Firebase", "Success profile url")
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Failed profile url", exception)
            }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            HomeTopBar(
                clickedProfile = clickedProfile,
                onProfileClick = {
                    clickedProfile = true
                    navController.navigate("profile")
                },
                onMenuClick = { showMenuSheet = true }
            )
        },
        containerColor = Color(0xFF121417),
        bottomBar = {
            // senin NavigationBar fonksiyonun zaten hazır
            val indexs = 0
            val flag = true
            val flag2 = false
            val flag3 = false
            val flag4 = false
            NavigationBar(navController = navController, indexs, flag, flag2, flag3, flag4)
        },
        floatingActionButton = {
            ExtendedStartButton {
                //Start function
            }
        },
        floatingActionButtonPosition = FabPosition.EndOverlay,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier
                .padding(top = 30.dp))

                Text("Start your workout",
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                    fontSize = 25.sp)
                Spacer(modifier = Modifier.size(10.dp))
            Spacer(modifier = Modifier
                .padding(top = 30.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Challenges Catalogue",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(R.font.lexendbold))
                    ),
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {navController.navigate(route = Screens.AllWorkouts.route)},
                    shape = RoundedCornerShape(3.dp),
                    modifier = Modifier
                        .height(30.dp)
                        .width(100.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1C40F))
                ) {
                    Text("See All",
                        style = TextStyle(fontFamily = FontFamily(Font(R.font.lexendbold)),
                            color = Color(0xFF121417))
                    )
                }
            }
            Spacer(modifier = Modifier
                .size(30.dp))
            LazyRow(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ){
                itemsIndexed(challangesWorkouts) {index, item ->
                    val totalIcons = 5
                    val challengeDifficulty = item.component1().workoutRating
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .height(200.dp)
                            .width(250.dp)
                            .background(
                                Color(0xFF1C2126),
                                shape = RoundedCornerShape(0.dp)
                            )
                            .clickable(onClick = {
                                navController.navigate("workoutsettingscreen/${item.workout.workoutId}") {
                                    popUpTo(Screens.Home.route)
                                }
                            }),
                        contentAlignment = Alignment.Center
                    ) {

                        if (index == 0) {
                            Image(
                                painterResource(id = R.drawable.secondinfo),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                alpha = 0.5f,
                                modifier = Modifier
                                    .fillMaxSize(),
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,       // Üst kısım şeffaf
                                                Color.Black.copy(alpha = 0.7f) // Alt kısım %70 siyah
                                            ),
                                            startY = 100f // Efektin başlama noktası (Ayarlayabilirsin)
                                        )
                                    )
                            )
                            Text(
                                text = item.component1().workoutName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    letterSpacing = 0.sp,
                                    textAlign = TextAlign.Center
                                ),
                                color = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(top = 30.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 30.dp)
                            ) {
                                for (i in 1..totalIcons) {
                                    val iconColor =
                                        if (i <= challengeDifficulty) Color(0xFFF1C40F) else Color.White

                                    Icon(
                                        painter = painterResource(id = R.drawable.skull),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(20.dp),
                                        tint = iconColor
                                    )

                                    if (i < totalIcons) {
                                        Spacer(modifier = Modifier.size(10.dp))
                                    }
                                }
                            }


                        } else if (index == 1) {
                            Image(
                                painterResource(id = R.drawable.login),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                alpha = 0.7f,
                                modifier = Modifier
                                    .fillMaxSize(),
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,       // Üst kısım şeffaf
                                                Color.Black.copy(alpha = 0.7f) // Alt kısım %70 siyah
                                            ),
                                            startY = 100f // Efektin başlama noktası (Ayarlayabilirsin)
                                        )
                                    )
                            )
                            Text(
                                text = item.component1().workoutName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    letterSpacing = 0.sp,
                                    textAlign = TextAlign.Center
                                ),
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(top = 30.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 30.dp)
                            ) {
                                for (i in 1..totalIcons) {
                                    val iconColor =
                                        if (i <= challengeDifficulty) Color(0xFFF1C40F) else Color.White

                                    Icon(
                                        painter = painterResource(id = R.drawable.skull),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(20.dp),
                                        tint = iconColor
                                    )

                                    if (i < totalIcons) {
                                        Spacer(modifier = Modifier.size(10.dp))
                                    }
                                }
                            }
                        } else if (index == 2) {
                            Image(
                                painterResource(id = R.drawable.gym),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                alpha = 0.7f,
                                modifier = Modifier
                                    .fillMaxSize(),
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,       // Üst kısım şeffaf
                                                Color.Black.copy(alpha = 0.7f) // Alt kısım %70 siyah
                                            ),
                                            startY = 100f // Efektin başlama noktası (Ayarlayabilirsin)
                                        )
                                    )
                            )
                            Text(
                                text = item.component1().workoutName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    letterSpacing = 0.sp,
                                    textAlign = TextAlign.Center
                                ),
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(top = 30.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 30.dp)
                            ) {
                                for (i in 1..totalIcons) {
                                    val iconColor =
                                        if (i <= challengeDifficulty) Color(0xFFF1C40F) else Color.White

                                    Icon(
                                        painter = painterResource(id = R.drawable.skull),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(20.dp),
                                        tint = iconColor
                                    )

                                    if (i < totalIcons) {
                                        Spacer(modifier = Modifier.size(10.dp))
                                    }
                                }
                            }
                        } else if (index == 3) {
                            Image(
                                painterResource(id = R.drawable.gymroomwith),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                alpha = 1f,
                                modifier = Modifier
                                    .fillMaxSize(),
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,       // Üst kısım şeffaf
                                                Color.Black.copy(alpha = 0.7f) // Alt kısım %70 siyah
                                            ),
                                            startY = 100f // Efektin başlama noktası (Ayarlayabilirsin)
                                        )
                                    )
                            )
                            Text(
                                text = item.component1().workoutName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    letterSpacing = 0.sp,
                                    textAlign = TextAlign.Center
                                ),
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(top = 30.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 30.dp)
                            ) {
                                for (i in 1..totalIcons) {
                                    val iconColor =
                                        if (i <= challengeDifficulty) Color(0xFFF1C40F) else Color.White

                                    Icon(
                                        painter = painterResource(id = R.drawable.skull),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(20.dp),
                                        tint = iconColor
                                    )

                                    if (i < totalIcons) {
                                        Spacer(modifier = Modifier.size(10.dp))
                                    }
                                }
                            }
                        } else if (index == 4) {
                            Image(
                                painterResource(id = R.drawable.gymroomwithgym),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                alpha = 0.7f,
                                modifier = Modifier
                                    .fillMaxSize(),
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,       // Üst kısım şeffaf
                                                Color.Black.copy(alpha = 0.7f) // Alt kısım %70 siyah
                                            ),
                                            startY = 100f // Efektin başlama noktası (Ayarlayabilirsin)
                                        )
                                    )
                            )
                            Text(
                                text = item.component1().workoutName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    letterSpacing = 0.sp,
                                    textAlign = TextAlign.Center
                                ),
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(bottom = 50.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 30.dp)
                            ) {
                                for (i in 1..totalIcons) {
                                    val iconColor =
                                        if (i <= challengeDifficulty) Color(0xFFF1C40F) else Color.White

                                    Icon(
                                        painter = painterResource(id = R.drawable.skull),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(20.dp),
                                        tint = iconColor
                                    )

                                    if (i < totalIcons) {
                                        Spacer(modifier = Modifier.size(10.dp))
                                    }
                                }
                            }
                        } else if (index == 5) {
                            Image(
                                painterResource(id = R.drawable.gymroomgym),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                alpha = 0.7f,
                                modifier = Modifier
                                    .fillMaxSize(),
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,       // Üst kısım şeffaf
                                                Color.Black.copy(alpha = 0.7f) // Alt kısım %70 siyah
                                            ),
                                            startY = 100f // Efektin başlama noktası (Ayarlayabilirsin)
                                        )
                                    )
                            )
                            Text(
                                text = item.component1().workoutName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    letterSpacing = 0.sp,
                                    textAlign = TextAlign.Center
                                ),
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(bottom = 50.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 30.dp)
                            ) {
                                for (i in 1..totalIcons) {
                                    val iconColor =
                                        if (i <= challengeDifficulty) Color(0xFFF1C40F) else Color.White

                                    Icon(
                                        painter = painterResource(id = R.drawable.skull),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(20.dp),
                                        tint = iconColor
                                    )

                                    if (i < totalIcons) {
                                        Spacer(modifier = Modifier.size(10.dp))
                                    }
                                }
                            }
                        } else if (index == 6) {
                            Image(
                                painterResource(id = R.drawable.login),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                alpha = 0.7f,
                                modifier = Modifier
                                    .fillMaxSize(),
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,       // Üst kısım şeffaf
                                                Color.Black.copy(alpha = 0.7f) // Alt kısım %70 siyah
                                            ),
                                            startY = 100f // Efektin başlama noktası (Ayarlayabilirsin)
                                        )
                                    )
                            )
                            Text(
                                text = item.component1().workoutName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    letterSpacing = 0.sp,
                                    textAlign = TextAlign.Center
                                ),
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(bottom = 50.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 30.dp)
                            ) {
                                for (i in 1..totalIcons) {
                                    val iconColor =
                                        if (i <= challengeDifficulty) Color(0xFFF1C40F) else Color.White

                                    Icon(
                                        painter = painterResource(id = R.drawable.skull),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(20.dp),
                                        tint = iconColor
                                    )

                                    if (i < totalIcons) {
                                        Spacer(modifier = Modifier.size(10.dp))
                                    }
                                }
                            }
                        }
                    }
                        Spacer(Modifier.size(30.dp))
                    }
                }
            Spacer(Modifier.size(50.dp))
            Text(
                text = "Coach's Pick",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.lexendbold))
                ),
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(horizontal = 30.dp)
            )
            Spacer(modifier = Modifier
                .size(30.dp))
            LazyRow(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ){
                itemsIndexed(coachWorkouts) {index, item ->
                    val totalIcons = 5
                    val challengeDifficulty = item.component1().workoutRating
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .height(200.dp)
                            .width(250.dp)
                            .background(
                                Color(0xFF1C2126),
                                shape = RoundedCornerShape(0.dp)
                            )
                            .clickable(onClick = { navController.navigate("workoutsettingscreen/${item.workout.workoutId}") }),
                        contentAlignment = Alignment.Center
                    ) {

                        if (index == 0) {
                            Image(
                                painterResource(id = R.drawable.secondinfo),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                alpha = 0.5f,
                                modifier = Modifier
                                    .fillMaxSize(),
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,       // Üst kısım şeffaf
                                                Color.Black.copy(alpha = 0.7f) // Alt kısım %70 siyah
                                            ),
                                            startY = 100f // Efektin başlama noktası (Ayarlayabilirsin)
                                        )
                                    )
                            )
                            Text(
                                text = item.component1().workoutName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    letterSpacing = 0.sp,
                                    textAlign = TextAlign.Center
                                ),
                                color = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(top = 30.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 30.dp)
                            ) {
                                for (i in 1..totalIcons) {
                                    val iconColor =
                                        if (i <= challengeDifficulty) Color(0xFFF1C40F) else Color.White

                                    Icon(
                                        painter = painterResource(id = R.drawable.skull),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(20.dp),
                                        tint = iconColor
                                    )

                                    if (i < totalIcons) {
                                        Spacer(modifier = Modifier.size(10.dp))
                                    }
                                }
                            }


                        } else if (index == 1) {
                            Image(
                                painterResource(id = R.drawable.login),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                alpha = 0.7f,
                                modifier = Modifier
                                    .fillMaxSize(),
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,       // Üst kısım şeffaf
                                                Color.Black.copy(alpha = 0.7f) // Alt kısım %70 siyah
                                            ),
                                            startY = 100f // Efektin başlama noktası (Ayarlayabilirsin)
                                        )
                                    )
                            )
                            Text(
                                text = item.component1().workoutName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    letterSpacing = 0.sp,
                                    textAlign = TextAlign.Center
                                ),
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(bottom = 50.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 30.dp)
                            ) {
                                for (i in 1..totalIcons) {
                                    val iconColor =
                                        if (i <= challengeDifficulty) Color(0xFFF1C40F) else Color.White

                                    Icon(
                                        painter = painterResource(id = R.drawable.skull),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(20.dp),
                                        tint = iconColor
                                    )

                                    if (i < totalIcons) {
                                        Spacer(modifier = Modifier.size(10.dp))
                                    }
                                }
                            }
                        } else if (index == 2) {
                            Image(
                                painterResource(id = R.drawable.gym),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                alpha = 0.7f,
                                modifier = Modifier
                                    .fillMaxSize(),
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,       // Üst kısım şeffaf
                                                Color.Black.copy(alpha = 0.7f) // Alt kısım %70 siyah
                                            ),
                                            startY = 100f // Efektin başlama noktası (Ayarlayabilirsin)
                                        )
                                    )
                            )
                            Text(
                                text = item.component1().workoutName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    letterSpacing = 0.sp,
                                    textAlign = TextAlign.Center
                                ),
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(bottom = 50.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 30.dp)
                            ) {
                                for (i in 1..totalIcons) {
                                    val iconColor =
                                        if (i <= challengeDifficulty) Color(0xFFF1C40F) else Color.White

                                    Icon(
                                        painter = painterResource(id = R.drawable.skull),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(20.dp),
                                        tint = iconColor
                                    )

                                    if (i < totalIcons) {
                                        Spacer(modifier = Modifier.size(10.dp))
                                    }
                                }
                            }
                        } else if (index == 3) {
                            Image(
                                painterResource(id = R.drawable.gymroomwith),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                alpha = 0.7f,
                                modifier = Modifier
                                    .fillMaxSize(),
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,       // Üst kısım şeffaf
                                                Color.Black.copy(alpha = 0.7f) // Alt kısım %70 siyah
                                            ),
                                            startY = 100f // Efektin başlama noktası (Ayarlayabilirsin)
                                        )
                                    )
                            )
                            Text(
                                text = item.component1().workoutName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    letterSpacing = 0.sp,
                                    textAlign = TextAlign.Center
                                ),
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(bottom = 50.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 30.dp)
                            ) {
                                for (i in 1..totalIcons) {
                                    val iconColor =
                                        if (i <= challengeDifficulty) Color(0xFFF1C40F) else Color.White

                                    Icon(
                                        painter = painterResource(id = R.drawable.skull),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(20.dp),
                                        tint = iconColor
                                    )

                                    if (i < totalIcons) {
                                        Spacer(modifier = Modifier.size(10.dp))
                                    }
                                }
                            }
                        } else if (index == 4) {
                            Image(
                                painterResource(id = R.drawable.gymroomwithgym),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                alpha = 0.7f,
                                modifier = Modifier
                                    .fillMaxSize(),
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,       // Üst kısım şeffaf
                                                Color.Black.copy(alpha = 0.7f) // Alt kısım %70 siyah
                                            ),
                                            startY = 100f // Efektin başlama noktası (Ayarlayabilirsin)
                                        )
                                    )
                            )
                            Text(
                                text = item.component1().workoutName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    letterSpacing = 0.sp,
                                    textAlign = TextAlign.Center
                                ),
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(bottom = 50.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 30.dp)
                            ) {
                                for (i in 1..totalIcons) {
                                    val iconColor =
                                        if (i <= challengeDifficulty) Color(0xFFF1C40F) else Color.White

                                    Icon(
                                        painter = painterResource(id = R.drawable.skull),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(20.dp),
                                        tint = iconColor
                                    )

                                    if (i < totalIcons) {
                                        Spacer(modifier = Modifier.size(10.dp))
                                    }
                                }
                            }
                        } else if (index == 5) {
                            Image(
                                painterResource(id = R.drawable.gymroomgym),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                alpha = 0.7f,
                                modifier = Modifier
                                    .fillMaxSize(),
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,       // Üst kısım şeffaf
                                                Color.Black.copy(alpha = 0.7f) // Alt kısım %70 siyah
                                            ),
                                            startY = 100f // Efektin başlama noktası (Ayarlayabilirsin)
                                        )
                                    )
                            )
                            Text(
                                text = item.component1().workoutName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    letterSpacing = 0.sp,
                                    textAlign = TextAlign.Center
                                ),
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(bottom = 50.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 30.dp)
                            ) {
                                for (i in 1..totalIcons) {
                                    val iconColor =
                                        if (i <= challengeDifficulty) Color(0xFFF1C40F) else Color.White

                                    Icon(
                                        painter = painterResource(id = R.drawable.skull),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(20.dp),
                                        tint = iconColor
                                    )

                                    if (i < totalIcons) {
                                        Spacer(modifier = Modifier.size(10.dp))
                                    }
                                }
                            }
                        } else if (index == 6) {
                            Image(
                                painterResource(id = R.drawable.login),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                alpha = 0.7f,
                                modifier = Modifier
                                    .fillMaxSize(),
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,       // Üst kısım şeffaf
                                                Color.Black.copy(alpha = 0.7f) // Alt kısım %70 siyah
                                            ),
                                            startY = 100f // Efektin başlama noktası (Ayarlayabilirsin)
                                        )
                                    )
                            )
                            Text(
                                text = item.component1().workoutName,
                                style = TextStyle(
                                    fontSize = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    letterSpacing = 0.sp,
                                    textAlign = TextAlign.Center
                                ),
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(bottom = 50.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 30.dp)
                            ) {
                                for (i in 1..totalIcons) {
                                    val iconColor =
                                        if (i <= challengeDifficulty) Color(0xFFF1C40F) else Color.White

                                    Icon(
                                        painter = painterResource(id = R.drawable.skull),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(20.dp),
                                        tint = iconColor
                                    )

                                    if (i < totalIcons) {
                                        Spacer(modifier = Modifier.size(10.dp))
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.size(30.dp))
                }
            }
            }

    if (showMenuSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMenuSheet = false },
            sheetState = menuSheetState,
            containerColor = Color(0xFF1C2126),
            modifier = Modifier.height(350.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 40.dp) // Alt boşluk önemli
            ) {
                // Başlık (Opsiyonel)
                Text(
                    text = "Menu",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 20.sp,
                        fontFamily = FontFamily(Font(R.font.lexendbold))
                    ),
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Seçenek: Profil (Eğer sol üstteki yetmezse buraya da koyabilirsin)
                MenuItemRow(
                    iconRes = R.drawable.accountcircle, // Kendi ikonun
                    text = "View Profile",
                    onClick = {navController.navigate(Screens.Home.Profile.route)}
                )

                // 2. Seçenek: Ayarlar (Genelde burası için gereklidir)
                MenuItemRow(
                    iconRes = R.drawable.settings, // Ayarlar ikonu eklemelisin
                    text = "Settings",
                    onClick = {}
                )

                // Ayırıcı Çizgi
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 0.5.dp,
                    color = Color.Gray.copy(alpha = 0.3f)
                )

                // 3. Seçenek: Çıkış Yap (Kırmızı Renk Detayı)
                MenuItemRow(
                    iconRes = R.drawable.logouticon128, // Çıkış ikonu
                    text = "Log Out",
                    textColor = Color(0xFFFF4444), // Hafif yumuşak bir kırmızı
                    onClick = {authViewModel.logout()}
                )
            }
            }
        }
    }
}

// =========================================================
// TOP BAR
// =========================================================
@Composable
private fun HomeTopBar(
    clickedProfile: Boolean,
    onProfileClick: () -> Unit,
    onMenuClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = onProfileClick) {
            Icon(
                painter = painterResource(
                    id = if (clickedProfile) R.drawable.accountcirclefilled
                    else R.drawable.accountcircle
                ),
                contentDescription = null,
                modifier = Modifier.size(25.dp),
                tint = Color.White
            )
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = "PROJECT FITNESS",
            color = Color(0xFFF1C40F),
            fontSize = 14.sp,
            letterSpacing = 6.sp,
            fontFamily = FontFamily(Font(R.font.lexendregular))
        )

        Spacer(Modifier.weight(1f))

        IconButton(onClick = onMenuClick) {
            Icon(
                painter = painterResource(R.drawable.projectfitnesspointheavy),
                contentDescription = null,
                modifier = Modifier.size(25.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
fun ExtendedStartButton(onConfirmClick: () -> Unit) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    ExtendedFloatingActionButton(
        onClick = {
            if (!expanded) {
                expanded = true
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    onConfirmClick()
                    delay(500)
                    expanded = false
                }
            }
        },
        icon = {
            Icon(painter = painterResource(R.drawable.localfiredepartmenticon128), "Extended workout start button", Modifier.size(40.dp))
               },
        text = {
            Text("Start Workout", style = TextStyle(fontFamily = FontFamily(Font(R.font.lexendbold)), fontSize = 18.sp))
        },
        containerColor = Color(0xFFF1C40F),
        expanded = expanded,
        modifier = Modifier
            .padding(bottom = 50.dp)
    )
}

@Composable
fun MenuItemRow(
    iconRes: Int,
    text: String,
    textColor: Color = Color.White,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp), // Tıklama alanı geniş olsun
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
            style = TextStyle(
                color = textColor,
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.lexendregular))
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        // Sağa küçük bir ok işareti (Opsiyonel, şık durur)
        Icon(
            painter = painterResource(id = R.drawable.keyboarddoublearrowright), // > işareti
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(16.dp)
        )
    }
}