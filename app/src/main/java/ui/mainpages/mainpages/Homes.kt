@file:SuppressLint(
    "StateFlowValueCalledInComposition",
    "UnusedMaterial3ScaffoldPaddingParameter"
)

package ui.mainpages.mainpages

import SocialViewModel
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage
import com.grozzbear.R
import com.grozzbear.projectfitness.data.local.viewmodel.HomesViewModel
import com.grozzbear.projectfitness.data.local.viewmodel.WorkoutSettingViewModel
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
    homesViewModel: HomesViewModel,
    socialViewModel: SocialViewModel,
    workoutSettingViewModel: WorkoutSettingViewModel
) {
    // =========================================================
    // Essentials
    // =========================================================
    val context = LocalContext.current
    val scopes = rememberCoroutineScope()
    val db = remember { FirebaseFirestore.getInstance() }
    val uid = remember { Firebase.auth.currentUser?.uid }

    // =========================================================
    // Repository / Local DB (senin mevcut yapın)
    // =========================================================

//    val challengeLazyState = rememberLazyListState()
//    val coachLazyState = rememberLazyListState()

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
    val challangesWorkouts =
        workouts.filter { it.workout.workoutType.contains("challange", ignoreCase = true) }
    val coachWorkouts =
        workouts.filter { it.workout.workoutType.contains("coach", ignoreCase = true) }
    val userName by homesViewModel.userName.collectAsState()
    val gradientColors = listOf(
        Color(0xFFFFC107), // Parlak Sarı
        Color(0xFFFF5722)  // Turuncu/Kırmızı
    )
    val brush = Brush.horizontalGradient(colors = gradientColors)
    val selectedWorkout = randomPick()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val cardWidth = screenWidth * 0.85f
    var expanded by rememberSaveable { mutableStateOf(false) }
    val nickname by homesViewModel.nickname.collectAsState()
    val notification by socialViewModel.getNotification(nickname).observeAsState()
    val unReadCount = notification?.count { !it.isRead } ?: 0
    socialViewModel.setNickname(nickname)
    val challengePagerState = rememberPagerState(pageCount = { challangesWorkouts.size })
    val coachPagerState = rememberPagerState(pageCount = { coachWorkouts.size })
    val topPadding = if (android.os.Build.VERSION.SDK_INT >= 35) 50.dp else 0.dp


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

    LaunchedEffect(Unit) {
        Log.d("called", "called")
        homesViewModel.refreshExercises()
        homesViewModel.getUserName(uid.toString())
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            HomeTopBarHomes(
                clickedProfile = clickedProfile,
                onProfileClick = {
                    clickedProfile = true
                    navController.navigate("profile")
                },
                onMenuClick = { showMenuSheet = true },
                navController = navController,
                unreadCount = unReadCount,
                topPadding = topPadding
            )
        },
        containerColor = Color(0xFF121417),
        bottomBar = {
            val indexs = 0
            val flag = true
            val flag2 = false
            val flag3 = false
            val flag4 = false
            NavigationBar(navController = navController, indexs, flag, flag2, flag3, flag4)
        },
        floatingActionButton = {
            ExtendedStartButton({
                navController.navigate("workoutsettingscreen/$selectedWorkout") {
                    popUpTo(Screens.Home.route)
                }
            }, expanded, onExpand = {
                expanded = it
            })
        },
        floatingActionButtonPosition = FabPosition.EndOverlay,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .clickable(
                    onClick = { expanded = false },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GreetingText(userName)
            Spacer(
                modifier = Modifier
                    .padding(top = 30.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(-10.dp)) {
                    HorizontalDivider(
                        thickness = 3.dp,
                        color = Color(0xFFF1C40F),
                        modifier = Modifier.width(45.dp)
                    )
                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = "CHALLANGES",
                        color = Color.White,
                        fontSize = 24.sp,
                        letterSpacing = 0.sp,
                        fontFamily = FontFamily(Font(R.font.oswaldbold))
                    )
                    Text(
                        text = "CATALOGUE",
                        color = Color(0xFFF1C40F),
                        fontSize = 24.sp,
                        letterSpacing = 0.sp,
                        fontFamily = FontFamily(Font(R.font.oswaldbold))
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { navController.navigate(route = Screens.AllWorkouts.route) },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .height(20.dp)
                        .width(55.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text(
                        "See All",
                        style = TextStyle(fontFamily = FontFamily(Font(R.font.lexendextrabold))),
                        color = Color(0xFFF1C40F),
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(
                modifier = Modifier
                    .size(20.dp)
            )
            HorizontalPager(
                state = challengePagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp), // Kartın yüksekliğini buradan kontrol et
                contentPadding = PaddingValues(horizontal = 30.dp), // Yandaki kartların ne kadar görüneceğini belirler
                pageSpacing = 16.dp, // Kartlar arasındaki boşluk
                verticalAlignment = Alignment.CenterVertically
            ) { pageIndex ->
                val item = challangesWorkouts[pageIndex]
                val totalIcons = 5
                val challengeDifficulty = item.component1().workoutRating
                Box(
                    modifier = Modifier
                        .fillMaxWidth() // Pager içinde olduğu için genişliği Pager yönetir
                        .height(150.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF1C2126))
                        .clickable(onClick = {
                            navController.navigate("workoutsettingscreen/${item.workout.workoutId}")
                        }),
                    contentAlignment = Alignment.Center
                ) {

                    Image(
                        painter = painterResource(id = item.workout.image),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.9f)
                                    ),
                                    startY = 100f // Kararmanın başladığı nokta
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.Bottom // İçeriği aşağı yasla
                    ) {
                        Text(
                            text = item.workout.workoutType.uppercase(), // Kategori ismi
                            color = Color(0xFFF1C40F),
                            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = item.component1().workoutName,
                            color = Color.White,
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontFamily = FontFamily(Font(R.font.lexendbold))
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            WorkoutTag(
                                text = "45 mins",
                                icon = R.drawable.shutterspeedfilledicon128,
                                Color.Gray,
                                Color.Gray
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            for (i in 1..totalIcons) {
                                val iconColor =
                                    if (i <= challengeDifficulty) Color(0xFFF1C40F) else Color.White

                                Icon(
                                    painter = painterResource(id = R.drawable.skullicon128),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(15.dp),
                                    tint = iconColor
                                )

                                if (i < totalIcons) {
                                    Spacer(modifier = Modifier.size(2.dp))
                                }
                            }
                        }
                    }
                }
            }
            PageIndicator(challangesWorkouts.size, challengePagerState.currentPage)
            Spacer(Modifier.size(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(-10.dp)) {
                    HorizontalDivider(
                        thickness = 3.dp,
                        color = Color(0xFFF1C40F),
                        modifier = Modifier.width(45.dp)
                    )
                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = "COACH'S",
                        color = Color.White,
                        fontSize = 24.sp,
                        letterSpacing = 0.sp,
                        fontFamily = FontFamily(Font(R.font.oswaldbold))
                    )
                    Text(
                        text = "PICKS",
                        color = Color(0xFFF1C40F),
                        fontSize = 24.sp,
                        letterSpacing = 0.sp,
                        fontFamily = FontFamily(Font(R.font.oswaldbold))
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { navController.navigate(route = Screens.AllWorkouts.route) },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .height(20.dp)
                        .width(55.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text(
                        "See All",
                        style = TextStyle(fontFamily = FontFamily(Font(R.font.lexendextrabold))),
                        color = Color(0xFFF1C40F),
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(
                modifier = Modifier
                    .size(0.dp)
            )
            HorizontalPager(
                state = coachPagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp), // Kartın yüksekliğini buradan kontrol et
                contentPadding = PaddingValues(horizontal = 30.dp), // Yandaki kartların ne kadar görüneceğini belirler
                pageSpacing = 16.dp, // Kartlar arasındaki boşluk
                verticalAlignment = Alignment.CenterVertically
            ) { pageIndex ->
                val item = coachWorkouts[pageIndex]
                val totalIcons = 5
                val challengeDifficulty = item.component1().workoutRating
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .height(150.dp)
                        .width(cardWidth)
                        .background(
                            Color(0xFF1C2126),
                            shape = RoundedCornerShape(0.dp)
                        )
                        .clickable(onClick = { navController.navigate("workoutsettingscreen/${item.workout.workoutId}") }),
                    contentAlignment = Alignment.Center
                ) {

                    Image(
                        painter = painterResource(id = item.workout.image), // Resmini buraya koy
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.9f)
                                    ),
                                    startY = 100f // Kararmanın başladığı nokta
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.Bottom // İçeriği aşağı yasla
                    ) {
                        Text(
                            text = item.workout.workoutType.uppercase(), // Kategori ismi
                            color = Color(0xFFF1C40F),
                            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = item.component1().workoutName,
                            color = Color.White,
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontFamily = FontFamily(Font(R.font.lexendbold))
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            WorkoutTag(
                                text = "45 mins",
                                icon = R.drawable.shutterspeedfilledicon128,
                                Color.Gray,
                                Color.Gray
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            for (i in 1..totalIcons) {
                                val iconColor =
                                    if (i <= challengeDifficulty) Color(0xFFF1C40F) else Color.White

                                Icon(
                                    painter = painterResource(id = R.drawable.skullicon128),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(15.dp),
                                    tint = iconColor
                                )

                                if (i < totalIcons) {
                                    Spacer(modifier = Modifier.size(2.dp))
                                }
                            }
                        }
                    }
                }
            }
            PageIndicator(coachWorkouts.size, coachPagerState.currentPage)
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
                    .padding(top = 16.dp, bottom = 40.dp)
            ) {
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

                MenuItemRow(
                    iconRes = R.drawable.accountcircle, // Kendi ikonun
                    text = "View Profile",
                    onClick = { navController.navigate(Screens.Home.Profile.route) }
                )

                MenuItemRow(
                    iconRes = R.drawable.settings, // Ayarlar ikonu eklemelisin
                    text = "Settings",
                    onClick = { navController.navigate(Screens.HomesSettings.route) }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 0.5.dp,
                    color = Color.Gray.copy(alpha = 0.3f)
                )

                MenuItemRow(
                    iconRes = R.drawable.logouticon128,
                    text = "Log Out",
                    textColor = Color(0xFFFF4444),
                    onClick = {
                        authViewModel.logout()
                        navController.navigate(Screens.LoginScreen.route)
                    }
                )
            }
        }
    }
}

// =========================================================
// TOP BAR
// =========================================================
@Composable
private fun HomeTopBarHomes(
    clickedProfile: Boolean,
    onProfileClick: () -> Unit,
    onMenuClick: () -> Unit,
    navController: NavController,
    unreadCount: Int,
    topPadding: Dp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = topPadding)
            .padding(horizontal = 16.dp, vertical = 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = onProfileClick) {
            Icon(
                painter = painterResource(R.drawable.accountcircle),
                contentDescription = null,
                modifier = Modifier.size(25.dp),
                tint = Color.White
            )
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = "GROZZ",
            color = Color(0xFFF1C40F),
            fontSize = 24.sp,
            letterSpacing = 0.sp,
            fontFamily = FontFamily(Font(R.font.oswaldbold))
        )

        Spacer(Modifier.weight(1f))

        IconButton(
            onClick = { navController.navigate(Screens.NotificationScreen.route) },
            modifier = Modifier.size(50.dp)
        ) {
            BadgedBox(
                badge = {
                    if (unreadCount > 0) {
                        Badge(containerColor = Color.Red) {
                            Text(unreadCount.toString(), color = Color.White)
                        }
                    }
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.circlenotifications), // Kendi ikonunu ekle
                    contentDescription = "Notifications",
                    tint = Color.White,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    }
}

@Composable
fun ExtendedStartButton(
    onConfirmClick: () -> Unit,
    expanded: Boolean,
    onExpand: (Boolean) -> Unit
) {
    ExtendedFloatingActionButton(
        onClick = {
            if (!expanded) {
                onExpand(true)
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    onConfirmClick()
                    delay(500)
                    onExpand(false)
                }
            }
        },
        icon = {
            Icon(
                painter = painterResource(R.drawable.localfiredepartmenticon128),
                "Extended workout start button",
                Modifier.size(40.dp)
            )
        },
        text = {
            Text(
                "Quick Workout",
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                    fontSize = 18.sp
                )
            )
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

@Composable
fun GreetingText(userName: String) {
    val gradientColors = listOf(
        Color(0xFFF1C40F), // Parlak Sarı
        Color(0xFFF1C40F)  // Turuncu/Kırmızı
    )
    val userFirstName = userName.split(" ")[0]
    val annotatedString = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.oswaldbold))
            )
        ) {
            append(userFirstName)
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row() {
            Text(
                text = "Hello, ",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily(Font(R.font.oswaldbold))
                )
            )
            Text(
                text = annotatedString,
                style = TextStyle(
                    brush = Brush.horizontalGradient(colors = gradientColors)
                )
            )
        }
        Text(
            text = "Ready to crush your goals today?",
            style = TextStyle(
                color = Color.Gray.copy(alpha = 0.5f),
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                fontStyle = FontStyle.Italic,
                fontFamily = FontFamily(Font(R.font.lexendregular))
            )
        )
    }
}

@Composable
fun randomPick(): String {
    val workoutList = listOf<String>(
        "push",
        "pull"
    )
    return workoutList.random()

}

@Composable
fun PageIndicator(
    numberOfPages: Int,
    selectedPage: Int,
    modifier: Modifier = Modifier
) {
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
                    .background(if (isSelected) Color(0xFFF1C40F) else Color.Gray.copy(alpha = 0.5f))
                    .size(if (isSelected) 8.dp else 6.dp) // Seçili olanı biraz daha büyük yapabilirsin
                    .animateContentSize() // Geçişlerde yumuşak bir efekt verir
            )
        }
    }
}