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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import com.grozzbear.R
import com.grozzbear.ui.components.GrozzPrimaryButton
import com.grozzbear.ui.components.GrozzTopBarLogo
import com.grozzbear.ui.theme.GrozzBorder
import com.grozzbear.ui.theme.GrozzError
import com.grozzbear.ui.theme.GrozzMuted
import com.grozzbear.ui.theme.GrozzOnBackground
import com.grozzbear.ui.theme.GrozzOnPrimary
import com.grozzbear.ui.theme.GrozzSurface
import com.grozzbear.ui.theme.GrozzSystemBar
import com.grozzbear.ui.theme.GrozzTextSecondary
import com.grozzbear.ui.theme.GrozzYellow
import com.grozzbear.ui.theme.Lexend
import com.grozzbear.ui.theme.Oswald
import data.local.db.VideoCacheProvider
import data.local.viewmodel.LeaderboardViewModel
import data.remote.LeaderboardEntry
import ui.mainpages.navigation.NavigationBarLeaderboard
import ui.mainpages.navigation.Screens
import ui.mainpages.navigation.navigateToLoginAfterLogout
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
    var expandableExercise by remember { mutableStateOf(false) }
    var expandableModalBottomExercise by remember { mutableStateOf(false) }

    val exerciseList =
        remember { listOf("Dumbbell Bench Press", "Standing Barbell Overhead Press") }

    var selectedExercise by remember { mutableStateOf("Dumbbell Bench Press") }
    var selectedModalBottomExercise by remember { mutableStateOf("Dumbbell Bench Press") }

    var showMenuSheetLeaderBoard by remember { mutableStateOf(false) }
    val menuSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val leaderboardEntries by leaderboardViewModel.leaderboardData.collectAsState()
    val isLoading by leaderboardViewModel.isLoading.collectAsState()
    val verifiedLeaderboardEntries = remember(leaderboardEntries) {
        leaderboardEntries.filter { it.verificationStatus == "verified" }
    }
    val rankInfo by leaderboardViewModel.currentUserRankInfo.collectAsState()
    val infoDialog = remember { mutableStateOf(false) }
    val tabTitles = listOf("All", "Verified")
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val displayEntries = if (selectedTabIndex == 0) leaderboardEntries else verifiedLeaderboardEntries
    val isVideoModalBottomSheetVisible = remember { mutableStateOf(false) }
    var showMenuSheetPrAdd by remember { mutableStateOf(false) }
    val showMenuSheetPrAddState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val selectedUri = remember { mutableStateOf<Uri?>(null) }
    val isSelectedVideo = remember { mutableStateOf(false) }
    val weightInMBS = remember { mutableStateOf("0") }
    val volumeInMBS = remember { mutableStateOf("0") }
    val userUid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val profileState = profileViewModel.profileState.collectAsState().value
    val username = remember { mutableStateOf("") }
    val videoUrl = remember { mutableStateOf("") }

    LaunchedEffect(userUid) {
        if (userUid.isNotBlank()) {
            profileViewModel.load(userUid)
        }
    }

    LaunchedEffect(selectedExercise) {
        leaderboardViewModel.fetchLeaderboard(selectedExercise)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            HomeTopBarLeaderboard(
                onMenuClick = { showMenuSheetLeaderBoard = true },
                onPlusClick = { showMenuSheetPrAdd = true },
                onInfoClick = { infoDialog.value = true }
            )
        },
        containerColor = GrozzSystemBar,
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
        when (profileState) {
            is ProfileUiState.Ready -> {
                username.value = profileState.profile.nickname
            }
            else -> Unit
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                Text(
                    text = "PR",
                    color = GrozzOnBackground,
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = Oswald
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "RANKINGS",
                    color = GrozzYellow,
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = Oswald
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            FilterDropdown(
                text = selectedExercise,
                expanded = expandableExercise,
                onExpandChange = { expandableExercise = it },
                items = exerciseList,
                onItemSelected = { selectedExercise = it },
                modifier = Modifier
                    .fillMaxWidth(0.85F)
                    .padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
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
                            .clip(RoundedCornerShape(10.dp))
                            .then(
                                if (isSelected) Modifier.background(GrozzYellow)
                                else Modifier
                            ),
                        text = {
                            Text(
                                text = string,
                                style = MaterialTheme.typography.labelLarge,
                                fontFamily = Lexend,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) GrozzOnPrimary else GrozzMuted
                            )
                        }
                    )
                }
            }

            when {
                isLoading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Spacer(modifier = Modifier.height(10.dp))
                        repeat(5) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 5.dp)
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .shimmerEffect()
                            )
                        }
                    }
                }
                displayEntries.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp, vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No rankings yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = GrozzOnBackground,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Be the first to add a PR for this lift.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GrozzTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {
                    Leaderboard(
                        displayEntries,
                        navController,
                        leaderboardViewModel,
                        onInfoClick = { infoDialog.value = true },
                        onVideoModalBottomClick = { isVideoModalBottomSheetVisible.value = true },
                        proofUrl = { videoUrl.value = it }
                    )
                }
            }
        }
        if (showMenuSheetLeaderBoard) {
            ModalBottomSheet(
                onDismissRequest = { showMenuSheetLeaderBoard = false },
                sheetState = menuSheetState,
                containerColor = GrozzSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 40.dp)
                ) {
                    Text(
                        text = "Menu",
                        style = MaterialTheme.typography.titleLarge,
                        color = GrozzOnBackground,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MenuItemRow(
                        iconRes = R.drawable.accountcircle,
                        text = "View Profile",
                        onClick = {
                            showMenuSheetLeaderBoard = false
                            navController.navigate(Screens.Home.Profile.route)
                        }
                    )

                    MenuItemRow(
                        iconRes = R.drawable.addicon128,
                        text = "Add PR",
                        onClick = {
                            showMenuSheetLeaderBoard = false
                            showMenuSheetPrAdd = true
                        }
                    )

                    MenuItemRow(
                        iconRes = R.drawable.settings,
                        text = "Settings",
                        onClick = {
                            showMenuSheetLeaderBoard = false
                            navController.navigate(Screens.HomesSettings.route)
                        }
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 0.5.dp,
                        color = GrozzBorder
                    )

                    MenuItemRow(
                        iconRes = R.drawable.logouticon128,
                        text = "Log Out",
                        textColor = GrozzError,
                        onClick = {
                            authViewModel.logout()
                            navController.navigateToLoginAfterLogout()
                        }
                    )
                }
            }
        }
        if (showMenuSheetPrAdd) {
            var isCompressing by remember { mutableStateOf(false) }
            val context = LocalContext.current
            val estimatedOneRepMax = remember(weightInMBS.value, volumeInMBS.value) {
                estimateOneRepMax(weightInMBS.value, volumeInMBS.value)
            }

            ModalBottomSheet(
                onDismissRequest = {
                    if (!isCompressing) showMenuSheetPrAdd = false
                },
                sheetState = showMenuSheetPrAddState,
                containerColor = GrozzSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.92f)
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp)
                ) {
                    Text(
                        text = "Add PR",
                        style = MaterialTheme.typography.headlineMedium,
                        color = GrozzYellow,
                        fontFamily = Lexend,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp),
                        textAlign = TextAlign.Center
                    )

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        contentPadding = PaddingValues(bottom = 12.dp)
                    ) {
                        item { AddPrSectionLabel("Exercise") }
                        item {
                            FilterDropdownModalBottomPr(
                                text = selectedModalBottomExercise,
                                expanded = expandableModalBottomExercise,
                                onExpandChange = { expandableModalBottomExercise = it },
                                items = exerciseList,
                                onItemSelected = { selectedModalBottomExercise = it }
                            )
                        }
                        item { AddPrSectionLabel("Lift weight") }
                        item {
                            WeightInputField(
                                weight = { weightInMBS.value = it },
                                weightString = weightInMBS.value
                            )
                        }
                        item { AddPrSectionLabel("Reps") }
                        item {
                            VolumeInputField(
                                volume = { volumeInMBS.value = it },
                                volumeString = volumeInMBS.value
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Estimated 1RM  ",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = GrozzMuted,
                                    fontFamily = Lexend
                                )
                                Text(
                                    text = "${estimatedOneRepMax.toInt()} KG",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = GrozzYellow,
                                    fontFamily = Lexend
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        item { AddPrSectionLabel("Proof video") }
                        item {
                            Text(
                                text = "Upload a clip under 15 seconds for verification.",
                                style = MaterialTheme.typography.bodySmall,
                                color = GrozzTextSecondary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            )
                        }
                        item {
                            ProofUploadAddPr(
                                onUriSelected = { selectedUri.value = it },
                                isSelectedVideo = {
                                    isSelectedVideo.value = it
                                    if (!it) selectedUri.value = null
                                },
                                isSelectedVideoBoolean = isSelectedVideo.value
                            )
                        }
                    }

                    GrozzPrimaryButton(
                        text = if (isCompressing) "Compressing..." else "Add PR",
                        loading = isCompressing,
                        enabled = !isCompressing,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 20.dp),
                        onClick = {
                            val weightVal = weightInMBS.value.toDoubleOrNull()
                            val repsVal = volumeInMBS.value.toDoubleOrNull()
                            val uri = selectedUri.value

                            when {
                                selectedModalBottomExercise.isBlank() -> {
                                    Toast.makeText(context, "Select an exercise.", Toast.LENGTH_SHORT).show()
                                }
                                weightVal == null || weightVal <= 0.0 -> {
                                    Toast.makeText(context, "Enter a valid lift weight.", Toast.LENGTH_SHORT).show()
                                }
                                repsVal == null || repsVal <= 0.0 -> {
                                    Toast.makeText(context, "Enter valid reps.", Toast.LENGTH_SHORT).show()
                                }
                                uri == null || !isSelectedVideo.value -> {
                                    Toast.makeText(context, "Add a proof video under 15 seconds.", Toast.LENGTH_SHORT).show()
                                }
                                username.value.isBlank() -> {
                                    Toast.makeText(context, "Profile name is still loading. Try again.", Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    isCompressing = true
                                    val outputFile = java.io.File.createTempFile("compressed_v", ".mp4", context.cacheDir)
                                    val outputPath = outputFile.absolutePath
                                    val oneRm = estimateOneRepMax(weightInMBS.value, volumeInMBS.value)

                                    val transformerListener = object : Transformer.Listener {
                                        override fun onCompleted(
                                            composition: androidx.media3.transformer.Composition,
                                            exportResult: androidx.media3.transformer.ExportResult
                                        ) {
                                            val compressedUri = Uri.fromFile(outputFile)
                                            val uid = FirebaseAuth.getInstance().currentUser?.uid
                                            if (!uid.isNullOrBlank()) {
                                                leaderboardViewModel.uploadPrProof(
                                                    compressedUri,
                                                    uid,
                                                    selectedModalBottomExercise,
                                                    oneRm,
                                                    username.value
                                                )
                                            }
                                            isCompressing = false
                                            showMenuSheetPrAdd = false
                                            isSelectedVideo.value = false
                                            selectedUri.value = null
                                            Toast.makeText(context, "PR submitted for verification.", Toast.LENGTH_SHORT).show()
                                        }

                                        override fun onError(
                                            composition: androidx.media3.transformer.Composition,
                                            exportResult: androidx.media3.transformer.ExportResult,
                                            exportException: androidx.media3.transformer.ExportException
                                        ) {
                                            isCompressing = false
                                            Log.e("TransformerError", exportException.message ?: "Unknown error")
                                            Toast.makeText(context, "Compression failed, please try again.", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    transformMedia3(
                                        context = context,
                                        transformerListener = transformerListener,
                                        outputPath = outputPath,
                                        mediaItem = MediaItem.fromUri(uri)
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
        if (infoDialog.value) {
            Dialog(onDismissRequest = { infoDialog.value = false }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(GrozzSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(R.drawable.grozzlogo),
                            contentDescription = null,
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PR",
                                color = GrozzOnBackground,
                                style = MaterialTheme.typography.headlineMedium,
                                fontFamily = Lexend
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "RANKINGS",
                                color = GrozzYellow,
                                style = MaterialTheme.typography.headlineMedium,
                                fontFamily = Lexend
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        LeaderboardInfoRow(
                            iconRes = R.drawable.checkcircleicon128,
                            iconTint = GrozzOnBackground,
                            label = "Not verified"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LeaderboardInfoRow(
                            iconRes = R.drawable.checkcircleicon128,
                            iconTint = GrozzYellow,
                            label = "Waiting for verification"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LeaderboardInfoRow(
                            iconRes = R.drawable.checkcircleicon128,
                            iconTint = Color(0xFF5B9BD5),
                            label = "Verified lifting"
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        LeaderboardInfoRow(
                            iconRes = R.drawable.arrowuploadprogress128icon,
                            iconTint = GrozzOnBackground,
                            label = "Upload your PR for verification"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LeaderboardInfoRow(
                            iconRes = R.drawable.videocallfilledicon128,
                            iconTint = GrozzYellow,
                            label = "Verified lifting video"
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Verification status updates about every 6 hours.",
                            color = GrozzTextSecondary,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
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
private fun LeaderboardInfoRow(
    iconRes: Int,
    iconTint: Color,
    label: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            color = GrozzOnBackground,
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = Lexend,
            modifier = Modifier.weight(1f)
        )
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
    BoxWithConstraints(modifier = modifier) {
        val menuWidth = maxWidth

        Button(
            onClick = { onExpandChange(true) },
            modifier = Modifier
                .border(1.dp, GrozzYellow, RoundedCornerShape(10.dp))
                .fillMaxWidth()
                .height(44.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 14.dp)
        ) {
            Text(
                text = text,
                color = GrozzYellow,
                style = MaterialTheme.typography.labelLarge,
                fontFamily = Lexend,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
                tint = GrozzYellow
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandChange(false) },
            modifier = Modifier
                .width(menuWidth)
                .background(GrozzSurface)
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            item,
                            color = GrozzOnBackground,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
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
        modifier = modifier.padding(vertical = 8.dp)
    ) {
        Button(
            onClick = { },
            modifier = Modifier
                .menuAnchor(type = MenuAnchorType.PrimaryEditable)
                .border(1.dp, GrozzYellow, RoundedCornerShape(10.dp))
                .fillMaxWidth()
                .height(44.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            Text(
                text = text,
                color = GrozzYellow,
                style = MaterialTheme.typography.labelLarge,
                fontFamily = Lexend,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandChange(false) },
            modifier = Modifier.background(GrozzSystemBar)
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            item,
                            color = GrozzOnBackground,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
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
private fun HomeTopBarLeaderboard(
    onMenuClick: () -> Unit,
    onPlusClick: () -> Unit,
    onInfoClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                painter = painterResource(R.drawable.accountcircle),
                contentDescription = "Menu",
                modifier = Modifier.size(26.dp),
                tint = GrozzOnBackground
            )
        }

        GrozzTopBarLogo(modifier = Modifier.align(Alignment.Center))

        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPlusClick) {
                Icon(
                    painter = painterResource(R.drawable.addicon128),
                    contentDescription = "Add PR",
                    modifier = Modifier.size(26.dp),
                    tint = GrozzYellow
                )
            }
            IconButton(onClick = onInfoClick) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    modifier = Modifier.size(26.dp),
                    tint = GrozzOnBackground
                )
            }
        }
    }
}

@Composable
fun LazyColumnItem(index: Int, item: LeaderboardEntry, navController: NavController, onInfoClick: () -> Unit, onVideoModalBottomClick: () -> Unit, getUrl: (String) -> Unit) {
    val rankColor = when (index) {
        0 -> GrozzYellow
        1 -> Color(0xFFC0C0C0)
        2 -> Color(0xFF88540B)
        else -> GrozzMuted
    }
    val statusTint = when (item.verificationStatus) {
        "verified" -> Color(0xFF5B9BD5)
        "pendent" -> GrozzYellow
        else -> GrozzOnBackground
    }

    Box(
        modifier = Modifier
            .background(
                GrozzSurface.copy(alpha = 0.85f),
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = (index + 1).toString(),
                color = rankColor,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = Lexend,
                modifier = Modifier.width(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
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
                            navController.navigate(
                                Screens.OtherScreenProfile.createRoute(item.userName)
                            ) {
                                popUpTo(Screens.OtherScreenProfile.route) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    },
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.userName,
                    color = GrozzOnBackground,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = Lexend,
                    maxLines = 1
                )
                if (item.hasPro) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(GrozzYellow)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "PRO",
                            color = GrozzOnPrimary,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Text(
                text = "${item.weight.toInt()} KG",
                color = GrozzOnBackground,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = Lexend
            )
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                painter = painterResource(R.drawable.checkcircleicon128),
                contentDescription = "Verification status",
                tint = statusTint,
                modifier = Modifier
                    .size(20.dp)
                    .clickable(onClick = { onInfoClick() })
            )
            if (item.verificationStatus == "verified") {
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    painter = painterResource(R.drawable.videocallfilledicon128),
                    contentDescription = "Watch proof",
                    tint = GrozzYellow,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable(
                            onClick = {
                                onVideoModalBottomClick()
                                getUrl(item.proofUrl)
                            }
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
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(leaderboardEntries) { index, item ->
            LazyColumnItem(
                index,
                item,
                navController,
                onInfoClick,
                onVideoModalBottomClick,
                getUrl = { proofUrl(it) }
            )
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
private fun AddPrSectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        color = GrozzMuted,
        fontFamily = Lexend,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 4.dp)
    )
}

fun estimateOneRepMax(weight: String, reps: String): Double {
    val weightVal = weight.toDoubleOrNull() ?: 0.0
    val repsVal = (reps.toDoubleOrNull() ?: 0.0).coerceAtMost(36.0)
    return if (weightVal > 0 && repsVal > 0) {
        weightVal / (1.0278 - (0.0278 * repsVal))
    } else {
        0.0
    }
}

@Composable
fun WeightInputField(weight: (String) -> Unit, weightString: String) {
    var text by remember(weightString) { mutableStateOf(weightString) }

    TextField(
        value = text,
        onValueChange = {
            if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                text = it
                weight(it)
            }
        },
        textStyle = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = GrozzOnBackground,
            fontFamily = Lexend
        ),
        suffix = {
            Text(
                text = "KG",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GrozzYellow,
                    fontFamily = Lexend
                )
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            cursorColor = GrozzYellow,
            focusedIndicatorColor = GrozzYellow,
            unfocusedIndicatorColor = GrozzBorder,
            focusedTextColor = GrozzOnBackground,
            unfocusedTextColor = GrozzOnBackground
        ),
        placeholder = {
            Text(
                text = "0",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = GrozzMuted,
                    fontFamily = Lexend
                )
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun VolumeInputField(volume: (String) -> Unit, volumeString: String) {
    var text by remember(volumeString) { mutableStateOf(volumeString) }

    TextField(
        value = text,
        onValueChange = {
            if (it.isEmpty() || it.matches(Regex("^\\d*$"))) {
                text = it
                volume(it)
            }
        },
        textStyle = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = GrozzOnBackground,
            fontFamily = Lexend
        ),
        suffix = {
            Text(
                text = "REPS",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GrozzYellow,
                    fontFamily = Lexend
                )
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            cursorColor = GrozzYellow,
            focusedIndicatorColor = GrozzYellow,
            unfocusedIndicatorColor = GrozzBorder,
            focusedTextColor = GrozzOnBackground,
            unfocusedTextColor = GrozzOnBackground
        ),
        placeholder = {
            Text(
                text = "0",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = GrozzMuted,
                    fontFamily = Lexend
                )
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun ProofUploadAddPr(
    onUriSelected: (android.net.Uri) -> Unit,
    isSelectedVideo: (Boolean) -> Unit,
    isSelectedVideoBoolean: Boolean
) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { video: Uri? ->
        video?.let { uri ->
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(context, uri)
                val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                val durationInMs = time?.toLong() ?: 0
                if (durationInMs <= 15_000) {
                    onUriSelected(uri)
                    isSelectedVideo(true)
                } else {
                    Toast.makeText(context, "Video must be under 15 seconds.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("ProofUpload", "Failed to read video", e)
                Toast.makeText(context, "Could not read that video.", Toast.LENGTH_SHORT).show()
            } finally {
                retriever.release()
            }
        }
    }

    if (!isSelectedVideoBoolean) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, GrozzBorder, RoundedCornerShape(12.dp))
                .background(GrozzSystemBar)
                .clickable {
                    launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
                },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(R.drawable.videocallfilledicon128),
                    contentDescription = null,
                    tint = GrozzYellow,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Upload video",
                    color = GrozzOnBackground,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = Lexend
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Max 15 seconds",
                    color = GrozzTextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, GrozzYellow.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .background(GrozzSystemBar)
        ) {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.Center),
                tint = Color(0xFF4CAF50)
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove video",
                modifier = Modifier
                    .padding(10.dp)
                    .size(22.dp)
                    .align(Alignment.TopEnd)
                    .clickable { isSelectedVideo(false) },
                tint = GrozzError
            )
            Text(
                text = "Video ready",
                color = GrozzOnBackground,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = Lexend,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
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