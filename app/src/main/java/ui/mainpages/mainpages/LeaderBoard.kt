package ui.mainpages.mainpages

import android.util.Log
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.grozzbear.R
import data.local.viewmodel.LeaderboardViewModel
import data.remote.LeaderboardEntry
import ui.mainpages.navigation.NavigationBarLeaderboard
import ui.mainpages.navigation.Screens
import viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderBoard(
    navController: NavController,
    authViewModel: AuthViewModel,
    leaderboardViewModel: LeaderboardViewModel
) {
    // --- State Yönetimi ---
    var expandableMuscle by remember { mutableStateOf(false) }
    var expandableExercise by remember { mutableStateOf(false) }

    val muscleList =
        remember { listOf("Chest", "Back", "Legs", "Shoulders", "Arms") } // Liste uzatılabilir
    val exerciseList =
        remember { listOf("Dumbbell Bench Press", "Standing Barbell Overhead Press") }

    var selectedMuscle by remember { mutableStateOf("Muscle Group") }
    var selectedExercise by remember { mutableStateOf("Dumbbell Bench Press") }

    val topTabTitles = listOf("Global")
    var selectedTopTabIndex by remember { mutableIntStateOf(0) }

    var showMenuSheetLeaderBoard by remember { mutableStateOf(false) }
    val menuSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val leaderboardEntries by leaderboardViewModel.leaderboardData.collectAsState()
    val rankInfo by leaderboardViewModel.currentUserRankInfo.collectAsState()
    val topPadding = if (android.os.Build.VERSION.SDK_INT >= 35) 50.dp else 0.dp

    LaunchedEffect(selectedExercise) {
        if (selectedExercise != "Exercise") {
            leaderboardViewModel.fetchLeaderboard(selectedExercise)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            HomeTopBarLeaderboard(
                onProfileClick = { navController.navigate("profile") },
                onMenuClick = { showMenuSheetLeaderBoard = true },
                topPadding = topPadding
            )
        },
        containerColor = Color(0xFF121417),
        bottomBar = {
            NavigationBarLeaderboard(
                navController = navController,
                indexs = 2,
                flag = false, flag2 = false, flag3 = true, flag4 = false,
                rankInfo = rankInfo
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row() {
                Text(
                    text = "PR",
                    color = Color.White,
                    fontSize = 24.sp,
                    letterSpacing = 0.sp,
                    fontFamily = FontFamily(Font(R.font.oswaldbold))
                )
                Spacer(Modifier.width(5.dp))
                Text(
                    text = "RANKINGS",
                    color = Color(0xFFF1C40F),
                    fontSize = 24.sp,
                    letterSpacing = 0.sp,
                    fontFamily = FontFamily(Font(R.font.oswaldbold))
                )
            }
            Spacer(Modifier.height(10.dp))

            // --- Filtreleme Alanı (Dropdownlar) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterDropdown(
                    text = selectedExercise,
                    expanded = expandableExercise,
                    onExpandChange = { expandableExercise = it },
                    items = exerciseList,
                    onItemSelected = { selectedExercise = it },
                    modifier = Modifier.weight(1f)
                )
            }
            if (selectedTopTabIndex == 0) {
                Leaderboard(leaderboardEntries, navController, leaderboardViewModel)
            }
        }

        // --- Alt Menü (BottomSheet) ---
        if (showMenuSheetLeaderBoard) {
            ModalBottomSheet(
                onDismissRequest = { showMenuSheetLeaderBoard = false },
                sheetState = menuSheetState,
                containerColor = Color(0xFF1C2126)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 40.dp)
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
}

@Composable
fun FilterDropdown(
    text: String,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    items: List<String>,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Button(
            onClick = { onExpandChange(true) },
            modifier = Modifier
                .border(1.dp, Color(0xFFF1C40F), RoundedCornerShape(10.dp))
                .fillMaxWidth()
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(100.dp),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            Text(
                text,
                color = Color(0xFFF1C40F),
                fontSize = 15.sp,
                maxLines = 1,
                fontFamily = FontFamily(Font(R.font.lexendextrabold))
            )
            Icon(Icons.Filled.ArrowDropDown, null, tint = Color(0xFFF1C40F))
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandChange(false) },
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth(0.4f)
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item, color = Color.Black) },
                    onClick = {
                        onItemSelected(item)
                        onExpandChange(false)
                    }
                )
            }
        }
    }
}

@Composable
fun HomeTopBarLeaderboard(onProfileClick: () -> Unit, onMenuClick: () -> Unit, topPadding: Dp) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = topPadding)
            .padding(horizontal = 16.dp, vertical = 8.dp),
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
fun LazyColumnItem(index: Int, item: LeaderboardEntry, navController: NavController) {
    Log.d("allitems", item.toString())
    //item.userPhotoUri = leaderboardViewModel.fetchUserImage(item.userId)
    Box(
        modifier = Modifier
            .clickable(onClick = {
                if (item.userId == FirebaseAuth.getInstance().currentUser?.uid) {
                    navController.navigate(
                        Screens.Home.Profile.route
                    )
                } else {
                    navController.navigate("otherscreenprofile/${item.userName}")
                }
            }
            )
            .background(
                color = if (index == 0) {
                    Color(0xFFF1C40F).copy(alpha = 0.8f)
                } else if (index == 1) {
                    Color(0xFFC0C0C0)
                } else if (index == 2) Color(0xFF88540B) else Color.Gray,
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = (index + 1).toString(),
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.lexendbold))
            )
            Spacer(Modifier.width(10.dp))
            AsyncImage(
                model = item.userPhotoUri,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { },
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.userName,
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.lexendbold))
                )
                Spacer(Modifier.width(5.dp))
                if (item.hasPro) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF1C40F))
                            .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "PRO",
                            color = Color.Black,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = item.weight.toInt().toString() + " KG",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.lexendbold))
            )
            Spacer(modifier = Modifier.width(10.dp))
            if (item.verificationStatus == "verified") {
                Icon(
                    painter = painterResource(R.drawable.checkcircleicon128),
                    contentDescription = null,
                    tint = Color.Blue,
                    modifier = Modifier.size(20.dp)
                )
            } else if (item.verificationStatus == "pendent") {
                Icon(
                    painter = painterResource(R.drawable.checkcircleicon128),
                    contentDescription = null,
                    tint = Color.Yellow,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun Leaderboard(
    leaderboardEntries: List<LeaderboardEntry>,
    navController: NavController,
    leaderboardViewModel: LeaderboardViewModel
) {
//    val topThree = leaderboardEntries.take(3)
//    val firstPlace = leaderboardEntries.getOrNull(0)
//    val secondPlace = leaderboardEntries.getOrNull(1)
//    val thirdPlace = leaderboardEntries.getOrNull(2)
//    val remainingPlace = leaderboardEntries.drop(3)
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(Modifier.height(20.dp))
        Box() {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth() // fillMaxSize yerine fillMaxWidth
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(bottom = 16.dp) // Alttan biraz boşluk
            ) {
                itemsIndexed(leaderboardEntries) { index, item ->
                    LazyColumnItem(index, item, navController)
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.Center
//        ) {
//            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                Spacer(Modifier.height(50.dp))
//                Icon(
//                    painter = painterResource(R.drawable.trophyfilledicon128),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .size(40.dp),
//                    tint = Color.Transparent
//                )
//                AsyncImage(
//                    model = if (secondPlace?.userPhotoUri != null) {
//                        secondPlace.userPhotoUri
//                    } else R.drawable.infodark,
//                    contentDescription = null,
//                    modifier = Modifier
//                        .size(80.dp)
//                        .clip(CircleShape)
//                        .border(BorderStroke(3.dp, Color.Gray), CircleShape)
//                        .clickable { },
//                    contentScale = ContentScale.Crop
//                )
//                Spacer(modifier = Modifier.height(5.dp))
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    Box(
//                        modifier = Modifier
//                            .width(40.dp)
//                            .background(
//                                color = Color.Gray,
//                                shape = RoundedCornerShape(100.dp)
//                            ),
//                        contentAlignment = Alignment.TopCenter
//                    ) {
//                        Text(
//                            text = "2ND",
//                            color = Color.White,
//                            fontFamily = FontFamily(Font(R.font.lexendbold)),
//                        )
//                    }
//                    Text(
//                        text = secondPlace?.userName.toString(),
//                        color = Color.White,
//                        fontFamily = FontFamily(Font(R.font.lexendbold))
//                    )
//                    Text(
//                        text = secondPlace?.weight?.toFloat().toString() + "kg",
//                        color = Color.White,
//                        fontFamily = FontFamily(Font(R.font.lexendbold))
//                    )
//                }
//            }
//            Spacer(Modifier.width(10.dp))
//            Column(
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Icon(
//                    painter = painterResource(R.drawable.trophyfilledicon128),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .size(40.dp),
//                    tint = Color(0xFFF1C40F)
//                )
//                AsyncImage(
//                    model = if (firstPlace?.userPhotoUri != null) {
//                        firstPlace.userPhotoUri
//                    } else R.drawable.infodark,
//                    contentDescription = null,
//                    modifier = Modifier
//                        .size(100.dp)
//                        .clip(CircleShape)
//                        .border(BorderStroke(3.dp, Color(0xFFF1C40F)), CircleShape)
//                        .clickable { },
//                    contentScale = ContentScale.Crop
//                )
//                Spacer(modifier = Modifier.height(5.dp))
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    Box(
//                        modifier = Modifier
//                            .width(40.dp)
//                            .background(
//                                color = Color(0xFFF1C40F),
//                                shape = RoundedCornerShape(100.dp)
//                            ),
//                        contentAlignment = Alignment.TopCenter
//                    ) {
//                        Text(
//                            text = "1ST",
//                            color = Color.Black,
//                            fontFamily = FontFamily(Font(R.font.lexendbold)),
//                        )
//                    }
//                    Text(
//                        text = firstPlace?.userName.toString(),
//                        color = Color(0xFFF1C40F),
//                        fontFamily = FontFamily(Font(R.font.lexendbold))
//                    )
//                    Text(
//                        text = firstPlace?.weight?.toFloat().toString() + "kg",
//                        color = Color(0xFFF1C40F),
//                        fontFamily = FontFamily(Font(R.font.lexendbold))
//                    )
//                }
//            }
//            Spacer(Modifier.width(10.dp))
//            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                Spacer(Modifier.height(90.dp))
//                Icon(
//                    painter = painterResource(R.drawable.trophyfilledicon128),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .size(40.dp),
//                    tint = Color.Transparent
//                )
//                AsyncImage(
//                    model = if (thirdPlace?.userPhotoUri != null) {
//                        thirdPlace.userPhotoUri
//                    } else R.drawable.infodark,
//                    contentDescription = null,
//                    modifier = Modifier
//                        .size(60.dp)
//                        .clip(CircleShape)
//                        .border(BorderStroke(3.dp, Color(0xFFCD7F32)), CircleShape)
//                        .clickable { },
//                    contentScale = ContentScale.Crop
//                )
//                Spacer(Modifier.height(5.dp))
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    Box(
//                        modifier = Modifier
//                            .width(40.dp)
//                            .background(
//                                color = Color(0xFFCD7F32),
//                                shape = RoundedCornerShape(100.dp)
//                            ),
//                        contentAlignment = Alignment.TopCenter
//                    ) {
//                        Text(
//                            text = "3RD",
//                            color = Color.White,
//                            fontFamily = FontFamily(Font(R.font.lexendbold)),
//                        )
//                    }
//                    Text(
//                        text = "Name",
//                        color = Color.White,
//                        fontFamily = FontFamily(Font(R.font.lexendbold))
//                    )
//                    Text(
//                        text = thirdPlace?.weight.toString(),
//                        color = Color.White,
//                        fontFamily = FontFamily(Font(R.font.lexendbold))
//                    )
//                }
//            }
//        }
    }
}