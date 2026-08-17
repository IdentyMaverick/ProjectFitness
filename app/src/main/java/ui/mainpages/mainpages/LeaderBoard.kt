package ui.mainpages.mainpages

import android.app.Dialog
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.Transformer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.grozzbear.Application
import com.grozzbear.R
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.qualifiers.ApplicationContext
import data.local.db.VideoCacheProvider
import data.local.viewmodel.LeaderboardViewModel
import data.remote.LeaderboardEntry
import ui.mainpages.navigation.NavigationBarLeaderboard
import ui.mainpages.navigation.Screens
import viewmodel.AuthViewModel
import viewmodel.ProfileUiState
import viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderBoard(
    navController: NavController,
    authViewModel: AuthViewModel,
    leaderboardViewModel: LeaderboardViewModel,
    profileViewModel: ProfileViewModel
) {
    var expandableMuscle by remember { mutableStateOf(false) }
    var expandableExercise by remember { mutableStateOf(false) }
    var expandableModalBottomExercise by remember { mutableStateOf(false) }

    val muscleList =
        remember { listOf("Chest", "Back", "Legs", "Shoulders", "Arms") }
    val exerciseList =
        remember { listOf("Dumbbell Bench Press", "Standing Barbell Overhead Press") }
    val filterOption = remember { listOf("Only Verified", "All") }

    var selectedMuscle by remember { mutableStateOf("Muscle Group") }
    var selectedExercise by remember { mutableStateOf("Dumbbell Bench Press") }
    var selectedModalBottomExercise by remember { mutableStateOf("Dumbbell Bench Press") }

    val topTabTitles = listOf("Global")
    var selectedTopTabIndex by remember { mutableIntStateOf(0) }

    var showMenuSheetLeaderBoard by remember { mutableStateOf(false) }
    val menuSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val leaderboardEntries by leaderboardViewModel.leaderboardData.collectAsState()
    Log.d("leaderboardEntries", leaderboardEntries.toString())
    val verifiedLeaderboardEntries = leaderboardEntries.filter { it.verificationStatus == "verified" }
    val rankInfo by leaderboardViewModel.currentUserRankInfo.collectAsState()
    val topPadding = if (android.os.Build.VERSION.SDK_INT >= 35) 50.dp else 0.dp
    val isLoading = leaderboardEntries.isEmpty()
    val infoDialog = remember { mutableStateOf(false) }
    var tabTitles = listOf("All", "Only Verified")
    var selectedTabIndex by remember { mutableStateOf(0) }
    val isVideoModalBottomSheetVisible = remember { mutableStateOf(false) }
    var showMenuSheetPrAdd by remember { mutableStateOf(false) }
    val showMenuSheetPrAddState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val selectedUri = remember { mutableStateOf<Uri?>(null) }
    val isSelectedVideo = remember { mutableStateOf(false) }
    val weightInMBS = remember { mutableStateOf("0") }
    val volumeInMBS = remember { mutableStateOf("0") }
    val oneRepMax = remember { mutableStateOf(0.0) }
    val userUid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val profileState = profileViewModel.profileState.collectAsState().value
    val username = remember { mutableStateOf("") }
    val videoUrl = remember { mutableStateOf("") }

    LaunchedEffect(userUid) {
        profileViewModel.load(userUid)
    }

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
                topPadding = topPadding,
                infoDialog = { infoDialog.value = it },
                onPlusClick = { showMenuSheetPrAdd = true }
            )
        },
        containerColor = Color(0xFF121417),
        bottomBar = {
            NavigationBarLeaderboard(
                navController = navController,
                indexs = 2,
                flag = false, flag2 = false, flag3 = true, flag4 = false,
                rankInfo = rankInfo,
                leaderboardViewModel = leaderboardViewModel,
                infoDialog = { infoDialog.value = it }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        when(profileState) {
            is ProfileUiState.Ready -> {
                val profile = profileState.profile
                username.value = profile.nickname
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .graphicsLayer(
                    translationY = -50f
                ),
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
            Spacer(modifier = Modifier.height(10.dp))
            SecondaryTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent,
                divider = {},
                indicator = {},
                modifier = Modifier.padding(horizontal = 40.dp)
            ) {
                tabTitles.forEachIndexed { index, string ->
                    val isSelected = selectedTabIndex == index

                    Tab(
                        selected = isSelected,
                        onClick = { selectedTabIndex = index },
                        modifier = Modifier
                            .padding(horizontal = 0.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .then(
                                if (isSelected) Modifier.background(Color(0xFFF1C40F))
                                else Modifier
                            ),
                        text = {
                            Text(
                                text = string,
                                style = TextStyle(
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    fontSize = 15.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected) Color.Black else Color(0xFF4B5F71)
                            )
                        }
                    )
                }
            }
            if (selectedTabIndex == 0) {
                if (isLoading) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Spacer(Modifier.height(10.dp))
                        repeat(5) {
                            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp).height(50.dp).clip(RoundedCornerShape(12.dp)).shimmerEffect())
                        }
                    }
                } else {
                    if (selectedTopTabIndex == 0) {
                        Leaderboard(leaderboardEntries, navController, leaderboardViewModel, onInfoClick = {infoDialog.value = true}, onVideoModalBottomClick = {isVideoModalBottomSheetVisible.value = true}, proofUrl = { videoUrl.value = it })
                    }
                }
            }
            else {
                if (isLoading) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Spacer(Modifier.height(10.dp))
                        repeat(5) {
                            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp).height(50.dp).clip(RoundedCornerShape(12.dp)).shimmerEffect())
                        }
                    }
                } else {
                    if (selectedTopTabIndex == 0) {
                        Leaderboard(verifiedLeaderboardEntries, navController, leaderboardViewModel, onInfoClick = {infoDialog.value = true}, onVideoModalBottomClick = {isVideoModalBottomSheetVisible.value = true}, proofUrl = { videoUrl.value = it })
                    }
                }
            }
        }

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
                        iconRes = R.drawable.accountcircle,
                        text = "Add PR",
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
        if (showMenuSheetPrAdd) {
            ModalBottomSheet(
                onDismissRequest = { showMenuSheetPrAdd = false },
                sheetState = showMenuSheetPrAddState,
                containerColor = Color(0xFF1C2126),
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 40.dp, start = 20.dp, end = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Text(
                            text = "Add PR",
                            style = TextStyle(
                                color = Color(0xFFF1C40F),
                                fontSize = 25.sp,
                                fontFamily = FontFamily(Font(R.font.lexendbold))
                            ),
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }

                    item {
                        Text(
                            text = "SELECT EXERCISE",
                            style = TextStyle(
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 15.sp,
                                fontFamily = FontFamily(Font(R.font.lexendextrabold))
                            ),
                            modifier = Modifier.fillMaxWidth().padding(start = 20.dp,end = 20.dp, top = 16.dp)
                        )
                    }

                    item {
                        FilterDropdownModalBottomPr(
                            text = selectedModalBottomExercise,
                            expanded = expandableModalBottomExercise,
                            onExpandChange = { expandableModalBottomExercise = it },
                            items = exerciseList,
                            onItemSelected = { selectedModalBottomExercise = it }
                        )
                    }

                    item {
                        Text(
                            text = "LIFT WEIGHT",
                            style = TextStyle(
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 15.sp,
                                fontFamily = FontFamily(Font(R.font.lexendextrabold))
                            ),
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                        )
                    }

                    item {
                        WeightInputField(weight = { weightInMBS.value = it }, weightString = weightInMBS.value)
                    }

                    item {
                        Text(
                            text = "VOLUME",
                            style = TextStyle(
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 15.sp,
                                fontFamily = FontFamily(Font(R.font.lexendextrabold))
                            ),
                            modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 16.dp)
                        )
                    }

                    item {
                        VolumeInputField(volume = { volumeInMBS.value = it }, volumeString = volumeInMBS.value)
                    }

                    item {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "YOUR 1 REP MAX IS = ",
                                style = TextStyle(
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 15.sp,
                                    fontFamily = FontFamily(Font(R.font.lexendextrabold))
                                ),
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                            OneRepMax(weightInMBS.value,volumeInMBS.value, rpm = { oneRepMax.value = it })
                        }
                    }

                    item {
                        Text(
                            text = "UPLOAD YOUR LIFTING VIDEO FOR VERIFY",
                            style = TextStyle(
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 12.sp,
                                fontFamily = FontFamily(Font(R.font.lexendextrabold))
                            ),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                        )
                    }

                    item {
                        ProofUploadAddPr(
                            onUriSelected = { selectedUri.value = it },
                            leaderboardViewModel = leaderboardViewModel,
                            selectedUri = selectedUri.value,
                            selectedModalBottomExercise = selectedModalBottomExercise,
                            showMenuSheetPrAdd = { showMenuSheetPrAdd = it },
                            isSelectedVideo = { isSelectedVideo.value = it },
                            isSelectedVideoBoolean = isSelectedVideo.value,
                            weightInMBS = weightInMBS.value,
                            volumeInMBS = volumeInMBS.value,
                            oneRepMax = oneRepMax.value,
                            nickname = username.value
                        )
                    }
                }
            }
        }
        if (infoDialog.value) {
            Dialog(onDismissRequest = { infoDialog.value = false }) {
                Box(
                    modifier = Modifier.fillMaxSize().background(color = Color.Transparent, shape = RoundedCornerShape(20.dp)).clickable { infoDialog.value = false },
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = infoDialog.value,
                        enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.scaleIn(initialScale = 0.8f),
                        exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.scaleOut(targetScale = 0.8f)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(vertical = 200.dp).background(color = Color(0xFF121417), shape = RoundedCornerShape(20.dp)).clickable { infoDialog.value = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize()
                                    .padding(vertical = 20.dp, horizontal = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.grozzlogo),
                                    contentDescription = null,
                                    modifier = Modifier.size(150.dp).graphicsLayer(
                                        translationY = -150f
                                    ),
                                )}
                            Column(
                                modifier = Modifier.fillMaxSize()
                                    .padding(vertical = 20.dp, horizontal = 20.dp).graphicsLayer(
                                        translationY = 130f
                                    ),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "PR",
                                        color = Color.White,
                                        fontFamily = FontFamily(Font(R.font.lexendbold)),
                                        fontSize = 25.sp
                                    )
                                    Spacer(modifier = Modifier.width(1.dp))
                                    Text(
                                        text = "RANKINGS",
                                        color = Color(0xFFF1C40F),
                                        fontFamily = FontFamily(Font(R.font.lexendbold)),
                                        fontSize = 25.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                                Row(
                                    modifier = Modifier,
                                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.checkcircleicon128),
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.weight(1f))
                                    Text(
                                        text = "Not Verified",
                                        color = Color.White,
                                        fontFamily = FontFamily(Font(R.font.lexendbold)),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier,
                                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.checkcircleicon128),
                                        contentDescription = null,
                                        tint = Color.Yellow,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.weight(1f))
                                    Text(
                                        text = "Waiting for Verification Lifting",
                                        color = Color.White,
                                        fontFamily = FontFamily(Font(R.font.lexendbold)),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier,
                                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.checkcircleicon128),
                                        contentDescription = null,
                                        tint = Color.Blue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.weight(1f))
                                    Text(
                                        text = "Verified Lifting",
                                        color = Color.White,
                                        fontFamily = FontFamily(Font(R.font.lexendbold)),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                Spacer(modifier = Modifier.height(50.dp))
                                Row(
                                    modifier = Modifier,
                                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.arrowuploadprogress128icon),
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.weight(1f))
                                    Text(
                                        text = "Upload Your PR Lifting for Verification",
                                        color = Color.White,
                                        fontFamily = FontFamily(Font(R.font.lexendbold)),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier,
                                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.videocallfilledicon128),
                                        contentDescription = null,
                                        tint = Color(0xFFF1C40F),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.weight(1f))
                                    Text(
                                        text = "Verified Lifting Video",
                                        color = Color.White,
                                        fontFamily = FontFamily(Font(R.font.lexendbold)),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                                Text(
                                    text = "* Verification controls updates every 6 hours.",
                                    color = Color.White,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    if (isVideoModalBottomSheetVisible.value) {
        VideoModalBottomSheet(isVideoModalBottomSheetVisible = {isVideoModalBottomSheetVisible.value = it}, videoUrl = videoUrl.value)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdownModalBottomPr(
    text: String,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    items: List<String>,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { onExpandChange(!expanded) },
        modifier = modifier.padding(vertical = 16.dp)
    ) {
        Button(
            onClick = { },
            modifier = Modifier
                .menuAnchor(
                    type = MenuAnchorType.PrimaryEditable,
                )
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
            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandChange(false) },
            modifier = Modifier.background(Color(0xFF121417))
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item, color = Color.White) },
                    onClick = {
                        onItemSelected(item)
                        onExpandChange(false)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@Composable
fun HomeTopBarLeaderboard(onProfileClick: () -> Unit, onMenuClick: () -> Unit, topPadding: Dp, infoDialog: (Boolean) -> Unit, onPlusClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = topPadding)
            .padding(horizontal = 16.dp)
            .height(56.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.align(Alignment.CenterStart),
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
            IconButton(onClick = onProfileClick) {
                Icon(
                    painter = painterResource(R.drawable.addicon128),
                    contentDescription = null,
                    modifier = Modifier.size(25.dp).clickable(
                        onClick = { onPlusClick() }
                    ),
                    tint = Color(0xFFF1C40F)
                )
            }
        }

        // 2. ORTA TARAF (Logo - Her zaman tam merkezde)
        Image(
            painter = painterResource(R.drawable.grozzlogo),
            contentDescription = "Grozz Logo",
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.Center)
        )

        // 3. SAĞ TARAF (Menü ve Yeni Eklenecek Buton)
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { infoDialog(true) }) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color.White
                )
            }

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
}

@Composable
fun LazyColumnItem(index: Int, item: LeaderboardEntry, navController: NavController, onInfoClick: () -> Unit, onVideoModalBottomClick: () -> Unit, getUrl: (String) -> Unit) {
    Box(
        modifier = Modifier
            .background(
                Color.Black.copy(alpha = 0.4f),
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
                color = if (index == 0) {
                    Color(0xFFF1C40F)
                } else if (index == 1) {
                    Color(0xFFC0C0C0)
                } else if (index == 2) Color(0xFF88540B) else Color.Gray,
                fontFamily = FontFamily(Font(R.font.lexendbold))
            )
            Spacer(Modifier.width(10.dp))
            AsyncImage(
                model = item.userPhotoUri ?: R.drawable.grozzlogo,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable {
                        if (item.userId == FirebaseAuth.getInstance().currentUser?.uid) {
                            navController.navigate(
                                Screens.Home.Profile.route
                            )
                        } else {
                            navController.navigate("otherscreenprofile/${item.userName}")
                        }
                    },
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
                    modifier = Modifier.size(20.dp).clickable(
                        onClick = { onInfoClick() }
                    )
                )
                Spacer(Modifier.width(10.dp))
                Icon(
                    painter = painterResource(R.drawable.videocallfilledicon128),
                    contentDescription = null,
                    tint = Color(0xFFF1C40F),
                    modifier = Modifier.size(20.dp).clickable(
                        onClick = {
                            onVideoModalBottomClick()
                            getUrl(item.proofUrl)
                        }
                    )
                )
            } else if (item.verificationStatus == "pendent") {
                Icon(
                    painter = painterResource(R.drawable.checkcircleicon128),
                    contentDescription = null,
                    tint = Color.Yellow,
                    modifier = Modifier.size(20.dp).clickable(
                        onClick = { onInfoClick() }
                    )
                )
            } else if (item.verificationStatus == "notVerified") {
                Icon(
                    painter = painterResource(R.drawable.checkcircleicon128),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp).clickable(
                        onClick = { onInfoClick() }
                    )
                )
            }
        }
    }
}

@Composable
fun Leaderboard(
    leaderboardEntries: List<LeaderboardEntry>,
    navController: NavController,
    leaderboardViewModel: LeaderboardViewModel,
    onInfoClick: () -> Unit,
    onVideoModalBottomClick: () -> Unit,
    proofUrl: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(Modifier.height(20.dp))
        Box() {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                itemsIndexed(leaderboardEntries) { index, item ->
                    LazyColumnItem(index, item, navController, onInfoClick, onVideoModalBottomClick, getUrl = { proofUrl(it) })
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoModalBottomSheet(isVideoModalBottomSheetVisible: (Boolean) -> Unit, videoUrl: String) {
    ModalBottomSheet(
        onDismissRequest = { isVideoModalBottomSheetVisible(false) },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color(0xFF121417),
    ) {
        VideoPlayerSheet( videoUrl = videoUrl, onDismiss = {isVideoModalBottomSheetVisible(false)} )
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun VideoPlayerSheet(videoUrl: String, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val videoCache = VideoCacheProvider.get(context)
    val httpDataSourceFactory = DefaultHttpDataSource.Factory()
    val cacheDataSourceFactory = CacheDataSource.Factory()
        .setCache(videoCache)
        .setUpstreamDataSourceFactory(httpDataSourceFactory)
        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(context)
                    .setDataSourceFactory(cacheDataSourceFactory)
            )
            .build().apply{
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(Unit) {
        onDispose{
            exoPlayer.release()
        }
    }

    Column(modifier = Modifier.fillMaxWidth().height(400.dp)) {
        AndroidView(
            factory = {ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                }
            },
            modifier = Modifier.fillMaxWidth().weight(1f).padding(all = 20.dp)
        )
    }
}

@Composable
fun WeightInputField(weight: (String) -> Unit, weightString: String) {
    var text by remember { mutableStateOf(weightString) }

    TextField(
        value = text,
        onValueChange = {
            text = it
            weight(it)
                        },
        textStyle = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        ),
        suffix = {
            Text(
                text = "KG",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF1C40F)
                )
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            cursorColor = Color.White,
            focusedIndicatorColor = Color.Gray, // The bottom line color when active
            unfocusedIndicatorColor = Color.DarkGray // The bottom line color when inactive
        ),
        placeholder = {
            Text(
                text = "000",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun VolumeInputField(volume: (String) -> Unit, volumeString: String) {
    var text by remember { mutableStateOf(volumeString) }

    TextField(
        value = text,
        onValueChange = {
            text = it
            volume(it)
                        },
        textStyle = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        ),
        suffix = {
            Text(
                text = "REPS",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF1C40F)
                )
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            cursorColor = Color.White,
            focusedIndicatorColor = Color.Gray,
            unfocusedIndicatorColor = Color.DarkGray
        ),
        placeholder = {
            Text(
                text = "0",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun OneRepMax(weight: String, reps: String, rpm: (Double) -> Unit) {
    val weightVal = weight.toDoubleOrNull() ?: 0.0
    val repsVal = reps.toDoubleOrNull() ?: 0.0

    val repsAtMax = repsVal.coerceAtMost(36.0)

    val oneRepMax = if (weightVal > 0 && repsAtMax > 0) {
        weightVal / (1.0278 - (0.0278 * repsAtMax))
    } else 0.0

    rpm(oneRepMax)

    Text(
        text = "${oneRepMax.toInt()} RPM",
        style = TextStyle(
            color = Color(0xFFF1C40F),
            fontSize = 15.sp,
            fontFamily = FontFamily(Font(R.font.lexendextrabold))
        ),
    )
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun ProofUploadAddPr(
    onUriSelected: (android.net.Uri) -> Unit,
    leaderboardViewModel: LeaderboardViewModel,
    selectedUri: android.net.Uri?,
    selectedModalBottomExercise: String,
    showMenuSheetPrAdd: (Boolean) -> Unit,
    isSelectedVideo: (Boolean) -> Unit,
    isSelectedVideoBoolean: Boolean,
    weightInMBS: String,
    volumeInMBS: String,
    oneRepMax: Double,
    nickname: String
) {
    val context = LocalContext.current
    var isCompressing by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { video: Uri? ->
        video?.let { uri ->
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, uri)
            val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val durationInMs = time?.toLong() ?: 0

            if (durationInMs <= 15_000) {
                onUriSelected(uri)
                isSelectedVideo(true)
            } else {
                Toast.makeText(context, "Video must be under 15 seconds.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    if (!isSelectedVideoBoolean){
        Box(
            modifier = Modifier
                .clickable {
                    launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
                }
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(R.drawable.grozzforget),
                contentDescription = null,
                alpha = 0.5f,
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop,
            )
            Icon(
                painter = painterResource(R.drawable.videocallfilledicon128),
                contentDescription = null,
                modifier = Modifier.align(Alignment.Center).padding(bottom = 40.dp),
                tint = Color(0xFFF1C40F)
            )
            Text(
                text = "Upload Media",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.lexendextrabold)),
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 40.dp)
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = "Make sure that video should be under 15 seconds",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.lexendlight)),
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp)
            )
        }
}else {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(R.drawable.grozzforget),
                contentDescription = null,
                alpha = 0.5f,
                modifier = Modifier
                    .fillMaxWidth()
                    .size(100.dp)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop,
            )
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = null,
                modifier = Modifier.padding(bottom = 40.dp).size(40.dp).align(Alignment.Center),
                tint = Color.Green
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier.padding(bottom = 40.dp, top = 10.dp, end = 10.dp).size(20.dp).align(Alignment.TopEnd).clickable(
                    onClick = {
                        isSelectedVideo(false)
                    }
                ),
                tint = Color.Red
            )
            Text(
                text = "Added Successfully",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.lexendextrabold)),
                modifier = Modifier.padding(bottom = 30.dp).align(Alignment.BottomCenter)
            )
        }
}
    Spacer(Modifier.height(20.dp))
    Box(
        modifier = Modifier.fillMaxSize().padding(bottom = 30.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Button(
            onClick = {
                if (selectedUri != null && weightInMBS.isNotEmpty() && volumeInMBS.isNotEmpty()) {
                    isCompressing = true

                    // 1. Sıkıştırılmış dosya için geçici yer oluştur
                    val outputFile = java.io.File.createTempFile("compressed_v", ".mp4", context.cacheDir)
                    val outputPath = outputFile.absolutePath

                    // 2. Transformer Dinleyicisi
                    val transformerListener = object : Transformer.Listener {
                        override fun onCompleted(composition: androidx.media3.transformer.Composition, exportResult: androidx.media3.transformer.ExportResult) {
                            // SIKISTIRMA BITTI -> SIMDI YUKLE
                            val compressedUri = Uri.fromFile(outputFile)

                            leaderboardViewModel.uploadPrProof(
                                compressedUri,
                                FirebaseAuth.getInstance().currentUser?.uid.toString(),
                                selectedModalBottomExercise,
                                oneRepMax,
                                nickname
                            )

                            isCompressing = false
                            showMenuSheetPrAdd(false)
                            isSelectedVideo(false)
                            Toast.makeText(context, "PR added with compressed video!", Toast.LENGTH_SHORT).show()
                        }

                        override fun onError(composition: androidx.media3.transformer.Composition, exportResult: androidx.media3.transformer.ExportResult, exportException: androidx.media3.transformer.ExportException) {
                            isCompressing = false
                            Log.e("TransformerError", exportException.message ?: "Unknown error")
                            Toast.makeText(context, "Compression failed, please try again.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    transformMedia3(
                        context = context,
                        transformerListener = transformerListener,
                        outputPath = outputPath,
                        mediaItem = MediaItem.fromUri(selectedUri)
                    )

                } else {
                    Toast.makeText(context, "Fill all required fields.", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = !isCompressing,
            modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1C40F)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (isCompressing) "COMPRESSING..." else "ADD PR",
                style = TextStyle(fontFamily = FontFamily(Font(R.font.lexendextrabold)), fontSize = 13.sp)
            )
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
fun transformMedia3(
    context: Context,
    transformerListener: Transformer.Listener,
    outputPath: String,
    mediaItem: MediaItem
) {
    val editedMediaItem = EditedMediaItem.Builder(mediaItem)
        .setRemoveAudio(true)
        .build()

    val transformer = Transformer.Builder(context)
        .setVideoMimeType(MimeTypes.VIDEO_H264)
        .addListener(transformerListener)
        .build()

    transformer.start(editedMediaItem, outputPath)
}