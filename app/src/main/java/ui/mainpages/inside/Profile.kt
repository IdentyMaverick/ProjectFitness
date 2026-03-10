package ui.mainpages.inside

import SocialViewModel
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
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
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.grozzbear.R
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import data.local.viewmodel.OldWorkoutDetailsViewModel
import data.local.viewmodel.WorkoutCompleteScreenViewModel
import ui.mainpages.navigation.Screens
import viewmodel.AuthViewModel
import viewmodel.ProfileUiState
import viewmodel.ProfileViewModel
import viewmodel.ViewModelProfile
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(
    navController: NavController,
    viewModelProfile: ViewModelProfile,
    socialViewModel: SocialViewModel,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    workoutScreenCompleteScreenViewModel: WorkoutCompleteScreenViewModel,
    oldWorkoutDetailsViewModel: OldWorkoutDetailsViewModel
) {

    //Firebase *************************************************************************************************************************************************************
    val uid = Firebase.auth.currentUser?.uid ?: return
    val profileState = profileViewModel.profileState.collectAsState().value

    LaunchedEffect(uid) {
        profileViewModel.load(uid)
    }

    val launcherProfile =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) profileViewModel.changePhoto(uid, uri = uri)
        }
    //******************************************************************************************************************************************************************************
    //Database Creation*************************************************************************************************************************************************************
    val context = LocalContext.current

    //Image select options
    val nickname = remember { mutableStateOf("") }
    val _user = com.google.firebase.ktx.Firebase.auth.currentUser
    val getFollowers by socialViewModel.getFollowers(nickname.value)
        .observeAsState(initial = emptyList())
    val getFollowing by socialViewModel.getFollowing(nickname.value)
        .observeAsState(initial = emptyList())
    var numberOfFollows = getFollowing.size
    var numberOfFollowers = getFollowers.size
    val allHistoricalWorkouts by authViewModel.allHistoricalWorkouts.collectAsState(emptyList())
    val scrollState = rememberScrollState()
    val totalWorkout by authViewModel.totalWorkoutNumber.collectAsState()
    val totalLiftedWeight by authViewModel.totalLiftedWeight.collectAsState()
    val getTotalSpentTime by authViewModel.totalSpentTime.collectAsState()
    val consistencyScore = authViewModel.calculateConsistency(allHistoricalWorkouts)
    if (allHistoricalWorkouts.isEmpty()) {
        authViewModel.syncWorkoutsFromFirebase(uid)
    }

    LaunchedEffect(totalWorkout) {
        authViewModel.getTotalWorkoutNumber(uid)
        authViewModel.getTotalLiftedWeight(uid)
        authViewModel.getTotalSpentTime(uid)
    }

    // Profile.kt içinde
    val modelProducer = remember { CartesianChartModelProducer.build() }
    val weeklyData =
        remember(allHistoricalWorkouts) { prepareWeeklyVolumeData(allHistoricalWorkouts) }
    val topPadding = if (android.os.Build.VERSION.SDK_INT >= 35) 50.dp else 0.dp

    LaunchedEffect(weeklyData) {
        if (weeklyData.any { it > 0f }) {
            modelProducer.tryRunTransaction {
                columnSeries { series(weeklyData) }
            }
        } else {
            Log.d("VicoDebug", "Haftalık veri boş veya tümü 0: $weeklyData")
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            HomeTopBarProfile(navController, topPadding = topPadding)
        },
        containerColor = Color(0xFF121417),
        bottomBar = {},
        floatingActionButtonPosition = FabPosition.EndOverlay,
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        var tabTitles = listOf("Stats", "Activity")
        var selectedTabIndex by remember { mutableStateOf(0) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (profileState) {
                is ProfileUiState.Loading -> { /* loading */
                }

                is ProfileUiState.Error -> { /* toast/text */
                }

                is ProfileUiState.Ready -> {
                    val profile = (profileState as ProfileUiState.Ready).profile
                    socialViewModel._nickname.value = profile.nickname
                    nickname.value = profile.nickname

                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .border(4.dp, Color(0xFFF1C40F), CircleShape)
                            .padding(4.dp)
                            .border(2.dp, Color.Black, CircleShape)
                            .padding(4.dp)
                    ) {
                        AsyncImage(
                            model = if (profile.userPhotoUri != "") profile.userPhotoUri else R.drawable.grozzholdsdumbbellbothhandsnobackgroundxml,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .clickable { launcherProfile.launch("image/*") },
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(Modifier.size(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            profile.first,
                            modifier = Modifier
                                .padding(bottom = 0.dp),
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.lexendbold)),
                            fontSize = 20.sp,
                            style = TextStyle(letterSpacing = 0.sp)
                        )
                        Spacer(Modifier.width(5.dp))
                        if (profile.hasPro == true) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF1C40F))
                                    .border(1.dp, Color.Black, RoundedCornerShape(5.dp))
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
                        modifier = Modifier
                            .padding(bottom = 0.dp),
                        color = Color(0xFFF1C40F),
                        fontFamily = FontFamily(Font(R.font.lexendbold)),
                        fontSize = 15.sp,
                        style = TextStyle(letterSpacing = 0.sp)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth() // Tüm genişliği kaplasın
                    .height(80.dp) // Yüksekliği biraz artırdım, tıklama alanı rahat olsun
                    .padding(horizontal = 20.dp), // Kenarlardan boşluk
                horizontalArrangement = Arrangement.SpaceEvenly, // Öğeleri eşit aralıklarla dağıt
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally, // 🔥 İŞTE SİHİRLİ KOD BURASI
                    modifier = Modifier
                        .clickable(onClick = { navController.navigate("projectfollowersscreen") })
                        // Tıklama efektinin (ripple) güzel görünmesi için clip ekleyebilirsin
                        .padding(8.dp) // Tıklama alanı biraz genişlesin
                ) {
                    Text(
                        text = "$numberOfFollowers",
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.lexendbold)), // Sayıyı biraz daha kalın yapabilirsin
                        fontSize = 20.sp,
                        style = TextStyle(letterSpacing = 1.sp),
                        // Padding YOK! Column kendisi ortalayacak.
                    )
                    Text(
                        text = "FOLLOWERS",
                        color = Color.Gray, // Başlığı biraz daha sönük yaparak hiyerarşi kurabilirsin
                        fontFamily = FontFamily(Font(R.font.lexendbold)),
                        fontSize = 11.sp,
                        style = TextStyle(letterSpacing = 0.sp),
                    )
                }

                // Araya dikey çizgi (Divider) istersen:
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp)
                        .background(Color.DarkGray)
                )

                // --- FOLLOWING KISMI ---
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable(onClick = { navController.navigate("projectfollowscreen") })
                        .padding(8.dp)
                ) {
                    Text(
                        text = "$numberOfFollows",
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.lexendbold)),
                        fontSize = 20.sp,
                        style = TextStyle(letterSpacing = 1.sp),
                    )
                    Text(
                        text = "FOLLOWING",
                        color = Color.Gray,
                        fontFamily = FontFamily(Font(R.font.lexendbold)),
                        fontSize = 11.sp,
                        style = TextStyle(letterSpacing = 0.sp),
                    )
                }
            }

            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .fillMaxWidth()
                    .height(30.dp)
                    .clip(RoundedCornerShape(20.dp)) // Dış çerçeve oval yapısı
                    .background(Color.Gray.copy(alpha = 0.1f)) // Arka plan açık gri/mavi tonu
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
                                .padding(horizontal = 0.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .then(
                                    if (isSelected) Modifier.background(Color(0xFFF1C40F)) // Seçili olanın beyaz arka planı
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
                                    color = if (isSelected) Color.Black else Color(0xFF4B5F71) // Seçili Turuncu, seçili değilse Koyu Gri
                                )
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
//            if (selectedTabIndex == 0) {
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        modifier = Modifier.fillMaxSize()
//                    ) {
//                        Row(verticalAlignment = Alignment.CenterVertically,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(horizontal = 20.dp)) {
//                            Text(
//                                text = "Recent Achievements",
//                                textAlign = TextAlign.Start,
//                                fontFamily = FontFamily(Font(R.font.lexendbold)),
//                                fontWeight = FontWeight.Bold,
//                                style = TextStyle(letterSpacing = 0.sp, fontSize = 20.sp),
//                                color = Color.White.copy(alpha = 1f),
//                                modifier = Modifier
//                            )
//                            Spacer(Modifier.weight(1f))
//                            Text(
//                                text = "View All",
//                                textAlign = TextAlign.Start,
//                                fontFamily = FontFamily(Font(R.font.lexendbold)),
//                                fontWeight = FontWeight.Bold,
//                                style = TextStyle(letterSpacing = 0.sp, fontSize = 14.sp),
//                                color = Color(0xFFF1C40F),
//                                modifier = Modifier
//                            )
//                        }
//                        Spacer(modifier = Modifier.height(10.dp))
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.Start
//                        ) {
//                            Column(
//                                verticalArrangement = Arrangement.Center,
//                                horizontalAlignment = Alignment.CenterHorizontally
//                            ) {
//                                Box(
//                                    modifier = Modifier
//                                        .padding(horizontal = 20.dp)
//                                        .height(100.dp)
//                                        .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
//                                    contentAlignment = Alignment.Center
//                                ){
//                                    Box(
//                                        modifier = Modifier
//                                            .padding(horizontal = 25.dp)
//                                            .size(60.dp),
//                                        contentAlignment = Alignment.Center
//                                    ) {
//                                        Box(
//                                            modifier = Modifier
//                                                .background(Color(0xFFF1C40F).copy(alpha = 0.1f), RoundedCornerShape(20.dp))
//                                                .padding(horizontal = 0.dp)
//                                                .size(70.dp),
//                                            contentAlignment = Alignment.Center
//                                        ) {
//                                            Icon(
//                                                painter = painterResource(R.drawable.addcircle),
//                                                contentDescription = null,
//                                                tint = Color(0xFFF1C40F),
//                                                modifier = Modifier
//                                                    .size(30.dp)
//                                            )
//                                        }
//                                    }
//                                }
//                                Spacer(Modifier.height(10.dp))
//                                Text(
//                                    text = "No Achivement",
//                                    textAlign = TextAlign.Center,
//                                    color = Color.Gray.copy(alpha = 1f),
//                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
//                                    fontWeight = FontWeight.Bold
//                                )
//                            }
//
//
//                        }
//                    }
//            }
            if (selectedTabIndex == 0) {
                Spacer(Modifier.height(10.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    Text(
                        text = "Weekly Volume",
                        textAlign = TextAlign.Start,
                        fontFamily = FontFamily(Font(R.font.lexendbold)),
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(letterSpacing = 0.sp, fontSize = 20.sp),
                        color = Color.White.copy(alpha = 1f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 25.dp, vertical = 0.dp)
                    )
                    Spacer(Modifier.height(20.dp))
                    Box(
                        modifier = Modifier
                            .height(180.dp) // Biraz daha yükseklik grafik için iyidir
                            .fillMaxWidth()
                            .padding(horizontal = 25.dp)
                            .background(
                                color = Color(0xFF202B36).copy(alpha = 0.4f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(16.dp) // İçeriden boşluk
                    ) {
                        CartesianChartHost(
                            chart = rememberCartesianChart(
                                rememberColumnCartesianLayer(
                                    columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                                        rememberLineComponent(
                                            color = Color(0xFFF1C40F), // Senin sarı rengin
                                            thickness = 12.dp
                                        )
                                    )
                                ),
                                bottomAxis = rememberBottomAxis(
                                    valueFormatter = { value, _, _ ->
                                        listOf(
                                            "Mon",
                                            "Tue",
                                            "Wed",
                                            "Thu",
                                            "Fri",
                                            "Sat",
                                            "Sun"
                                        )[value.toInt() % 7]
                                    },
                                    label = rememberAxisLabelComponent(
                                        color = Color.Gray,
                                        textSize = 10.sp,
                                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                                    )
                                )
                            ),
                            modelProducer = modelProducer,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = "Lifetime Statistics",
                        textAlign = TextAlign.Start,
                        fontFamily = FontFamily(Font(R.font.lexendbold)),
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(letterSpacing = 0.sp, fontSize = 20.sp),
                        color = Color.White.copy(alpha = 1f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 25.dp)
                    )
                    Spacer(Modifier.height(20.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 25.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color(0xFF121417).copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .size(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = totalWorkout.toString(),
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 48.sp
                                )
                                Text(
                                    text = "WORKOUTS",
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "COMPLETED",
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF1C40F)
                                )
                            }
                        }
                        Spacer(Modifier.width(20.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color(0xFF121417).copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .size(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = totalLiftedWeight.toInt().toString(),
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 40.sp
                                )
                                Text(
                                    text = "KG",
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "WEIGHT",
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "LIFTED",
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF1C40F)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 25.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color(0xFF121417).copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .size(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = getTotalSpentTime.toString(),
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 48.sp
                                )
                                Text(
                                    text = "MINUTES",
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "SPENT FOR",
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "WORKOUTS",
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF1C40F)
                                )
                            }
                        }
                        Spacer(Modifier.width(20.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color(0xFF121417).copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .size(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = consistencyScore.toString(),
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 48.sp
                                )
                                Text(
                                    text = "CONSISTENCY",
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "SCORE",
                                    textAlign = TextAlign.Center,
                                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF1C40F)
                                )
                            }
                        }
                    }
                }
            } else if (selectedTabIndex == 1) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Last Activity",
                    textAlign = TextAlign.Start,
                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(letterSpacing = 0.sp, fontSize = 20.sp),
                    color = Color.White.copy(alpha = 1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                if (!allHistoricalWorkouts.isEmpty()) {
                    LazyColumn() {
                        itemsIndexed(allHistoricalWorkouts) { index, item ->
                            workoutScreenCompleteScreenViewModel.setWorkoutData(
                                item.workoutHistory.dateTimestamp,
                                item.workoutHistory.totalDuration
                            )
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .clickable(
                                            onClick = {
                                                oldWorkoutDetailsViewModel._sessionId.value =
                                                    item.workoutHistory.sessionId
                                                oldWorkoutDetailsViewModel._flag.value = false
                                                navController.navigate("oldworkoutdetails")
                                            }
                                        )
                                        .fillMaxWidth()
                                        .padding(horizontal = 25.dp)
                                        .background(
                                            color = Color(0xFF202B36).copy(alpha = 0.4f),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .height(60.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 20.dp),
                                        horizontalArrangement = Arrangement.Start,
                                        verticalAlignment = Alignment.CenterVertically

                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.dumbbellicon128),
                                            contentDescription = null,
                                            tint = Color(0xFFF1C40F),
                                            modifier = Modifier
                                                .size(30.dp)
                                        )
                                        Spacer(Modifier.width(20.dp))
                                        Column() {
                                            Text(
                                                text = "${item.workoutHistory.workoutName}",
                                                style = TextStyle(
                                                    fontSize = 15.sp,
                                                    fontFamily = FontFamily(
                                                        Font(R.font.lexendbold)
                                                    ),
                                                    color = Color.White
                                                )
                                            )
                                            Text(
                                                text = workoutScreenCompleteScreenViewModel.formattedDate.value,
                                                style = TextStyle(
                                                    fontSize = 10.sp,
                                                    fontFamily = FontFamily(
                                                        Font(R.font.lexendbold)
                                                    ),
                                                    color = Color.White.copy(alpha = 0.5f)
                                                )
                                            )
                                        }
                                        Spacer(Modifier.weight(1f))
                                        Icon(
                                            painter = painterResource(R.drawable.keyboarddoublearrowright),
                                            contentDescription = null,
                                            tint = Color(0xFFF1C40F),
                                            modifier = Modifier
                                                .size(25.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Column(
                        Modifier
                            .padding(horizontal = 30.dp)
                            .fillMaxSize()
                            .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(20.dp)),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(20.dp))
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
                            modifier = Modifier
                                .padding(horizontal = 20.dp, vertical = 20.dp)
                        )
                        Button(
                            onClick = { navController.navigate("home") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF1C40F)
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

    @Composable
    fun CustomDialogScreen(
        onDismiss: () -> Unit,
        launcher: ManagedActivityResultLauncher<String, Uri?>,
    ) {
        var text by remember { mutableStateOf("") }
        Log.d("TAG", "visible")
        Dialog(onDismissRequest = { onDismiss.invoke() }) {
            Box(
                modifier = Modifier
                    .padding(top = 300.dp)
                    .background(
                        Color(0xFFD9D9D9).copy(alpha = 0.4f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .fillMaxWidth()
                    .height(70.dp)
            )
            {
                Button(
                    onClick = { launcher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(Color.Transparent),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                )
                {
                    Text(
                        text = "Open Gallery",
                        fontFamily = FontFamily(Font(R.font.lexendextralight)),
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun HomeTopBarProfile(
    navController: NavController,
    topPadding: Dp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = topPadding)
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

fun prepareWeeklyVolumeData(workouts: List<data.local.entity.WorkoutHistoryFull>): List<Float> {
    val calendar = Calendar.getInstance()

    // 1. Güvenli Pazartesi Hesaplama: Bugünün saatlerini sıfırla ve Pazartesiye geri git
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
        calendar.add(Calendar.DATE, -1)
    }
    val startOfWeek = calendar.timeInMillis
    val endOfWeek = startOfWeek + (7L * 24 * 60 * 60 * 1000) // 7 gün sonrası

    val dailyVolumes = mutableMapOf<Int, Float>()
    val daysOrder = listOf(
        Calendar.MONDAY,
        Calendar.TUESDAY,
        Calendar.WEDNESDAY,
        Calendar.THURSDAY,
        Calendar.FRIDAY,
        Calendar.SATURDAY,
        Calendar.SUNDAY
    )
    daysOrder.forEach { dailyVolumes[it] = 0f }

    workouts.forEach { item ->
        val timestamp = item.workoutHistory.dateTimestamp

        if (timestamp >= startOfWeek && timestamp < endOfWeek) {
            val workoutCal = Calendar.getInstance().apply { timeInMillis = timestamp }
            val day = workoutCal.get(Calendar.DAY_OF_WEEK)

            var workoutVolume = 0f
            Log.d("VicoDebugg", item.workoutHistory.toString()) // Logcat'ten kontrol edebilirsin
            // 3. DÜZELTME: Egzersiz özeti yerine SETLERİ topla
            item.exerciseWithSets.forEach { exerciseWithSets ->
                exerciseWithSets.setLogs.forEach { setLog ->
                    workoutVolume += (setLog.weight * setLog.reps)
                }
            }
            dailyVolumes[day] = (dailyVolumes[day] ?: 0f) + workoutVolume
        }
    }

    val result = daysOrder.map { dailyVolumes[it] ?: 0f }
    Log.d("VicoDebug", "Haftalık Veri: $result") // Logcat'ten kontrol edebilirsin
    return result
}
