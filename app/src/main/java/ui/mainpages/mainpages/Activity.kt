package ui.mainpages.mainpages

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.grozzbear.R
import com.grozzbear.projectfitness.data.local.viewmodel.ActivityViewModel
import com.grozzbear.projectfitness.data.local.viewmodel.HomesViewModel
import ui.mainpages.navigation.NavigationBar
import ui.mainpages.navigation.Screens
import viewmodel.AuthViewModel


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Activity(
    navController: NavController,
    activityViewModel: ActivityViewModel,
    authViewModel: AuthViewModel,
    homesViewModel: HomesViewModel
) {

    //Database Creation*************************************************************************************************************************************************************
    var lazyListState: LazyListState = rememberLazyListState()
    val currentUser = FirebaseAuth.getInstance().currentUser?.uid

    activityViewModel.refreshWorkouts(currentUser.toString())
    val myWorkoutsList =
        activityViewModel.myWorkoutsFlow.collectAsState(initial = emptyList()).value

    //******************************************************************************************************************************************************************************

    // Variable Initiliaze -------------------------------------------------------------------------
    var clickedProfile by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var expand by remember { mutableStateOf(false) }
    var selectedWorkoutId by remember { mutableStateOf<String?>(null) }
    var showMenuSheetActivity by remember { mutableStateOf(false) }
    val menuSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var expanded by rememberSaveable { mutableStateOf(false) }
    val workouts by homesViewModel.workoutsFlow.collectAsState(initial = emptyList())
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val cardWidth = screenWidth * 0.85f
    val challengePagerState = rememberPagerState(pageCount = { workouts.size })

    // UI Codes ------------------------------------------------------------------------------------
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            HomeTopBarActivity(
                clickedProfile = clickedProfile,
                onProfileClick = {
                    clickedProfile = true
                    navController.navigate("profile")
                },
                onMenuClick = { showMenuSheetActivity = true }
            )
        },
        containerColor = Color(0xFF121417),
        bottomBar = {
            // senin NavigationBar fonksiyonun zaten hazır
            val indexs = 1
            val flagg = false
            val flagg2 = true
            val flagg3 = false
            val flagg4 = false
            NavigationBar(navController = navController, indexs, flagg, flagg2, flagg3, flagg4)
        },
        floatingActionButton = {
            ExtendedActivityButton(
                onConfirmClick = { navController.navigate(Screens.ChooseExercises.route) },
                expanded = expanded,
                onExpandChange = { expanded = it }
            )
        },
        floatingActionButtonPosition = FabPosition.EndOverlay,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .clickable(
                    enabled = true,
                    onClick = { expanded = false },
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                )
        ) {
            Spacer(Modifier.height(40.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Row() {
                    Column(verticalArrangement = Arrangement.spacedBy(-10.dp)) {
                        HorizontalDivider(
                            thickness = 3.dp,
                            color = Color(0xFFF1C40F),
                            modifier = Modifier.width(45.dp)
                        )
                        Spacer(Modifier.height(20.dp))
                        Text(
                            text = "RECOMMENDED",
                            color = Color.White,
                            fontSize = 24.sp,
                            letterSpacing = 0.sp,
                            fontFamily = FontFamily(Font(R.font.oswaldbold))
                        )
                        Text(
                            text = "FOR YOU",
                            color = Color(0xFFF1C40F),
                            fontSize = 24.sp,
                            letterSpacing = 0.sp,
                            fontFamily = FontFamily(Font(R.font.oswaldbold))
                        )
                    }
                }
                Row() {
                    Text(
                        text = "Based on your focus",
                        color = Color.Gray,
                        fontFamily = FontFamily(Font(R.font.lexendregular)),
                    )
                }
                Spacer(Modifier.height(10.dp))
                HorizontalPager(
                    state = challengePagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp), // Kartın yüksekliğini buradan kontrol et
                    contentPadding = PaddingValues(horizontal = 10.dp), // Yandaki kartların ne kadar görüneceğini belirler
                    pageSpacing = 16.dp, // Kartlar arasındaki boşluk
                    verticalAlignment = Alignment.CenterVertically
                ) { pageIndex ->
                    val item = workouts[pageIndex]
                    val totalIcons = 5
                    val challengeDifficulty = item.component1().workoutRating
                    Box(
                        modifier = Modifier
                            .width(cardWidth)
                            .height(150.dp)
                            .clip(RoundedCornerShape(24.dp)) // Daha yumuşak köşeler
                            .background(
                                Color(0xFF1C2126),
                                shape = RoundedCornerShape(0.dp)
                            )
                            .clickable(onClick = {
                                navController.navigate("workoutsettingscreen/${item.workout.workoutId}") {
                                    popUpTo(Screens.Home.route)
                                }
                            }),
                        //.border(3.dp, Color(0xFFF1C40F), RoundedCornerShape(24.dp)),
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
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                fontFamily = FontFamily(Font(R.font.lexendbold))
                            )
                            Text(
                                text = item.component1().workoutName,
                                color = Color.White,
                                style = TextStyle(
                                    fontSize = 24.sp,
                                    fontFamily = FontFamily(Font(R.font.oswaldbold))
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
            }
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(-10.dp)) {
                    HorizontalDivider(
                        thickness = 3.dp,
                        color = Color(0xFFF1C40F),
                        modifier = Modifier.width(45.dp)
                    )
                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = "YOUR",
                        color = Color.White,
                        fontSize = 24.sp,
                        letterSpacing = 0.sp,
                        fontFamily = FontFamily(Font(R.font.oswaldbold))
                    )
                    Text(
                        text = "WORKOUTS",
                        color = Color(0xFFF1C40F),
                        fontSize = 24.sp,
                        letterSpacing = 0.sp,
                        fontFamily = FontFamily(Font(R.font.oswaldbold))
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            if (myWorkoutsList.isNotEmpty()) {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .padding(horizontal = 30.dp)
                ) {
                    itemsIndexed(myWorkoutsList) { index, item ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    Color(0xFF1C2126),
                                    shape = RoundedCornerShape(0.dp)
                                )
                                .combinedClickable(
                                    onClick = {
                                        navController.navigate("workoutsettingscreen/${item.workoutId}") {
                                            popUpTo(Screens.Activity.route)
                                        }
                                    },
                                    onLongClick = {
                                        expand = true
                                        selectedWorkoutId = item.workoutId
                                    }),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.infohorizontalscreensecondphoto), // Resmini buraya koy
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
                                    text = item.workoutType.uppercase(), // Kategori ismi
                                    color = Color(0xFFF1C40F),
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    fontFamily = FontFamily(Font(R.font.lexendbold))
                                )
                                Text(
                                    text = item.workoutName,
                                    color = Color.White,
                                    style = TextStyle(
                                        fontSize = 24.sp,
                                        fontFamily = FontFamily(Font(R.font.oswaldbold))
                                    )
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    WorkoutTag(
                                        text = item.workoutType,
                                        icon = R.drawable.shutterspeedfilledicon128,
                                        Color.Gray,
                                        Color.Gray
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.size(10.dp))
                    }
                }
                if (selectedWorkoutId != null) {
                    LongClickModalBottom(
                        sheetState = sheetState,
                        onDismiss = {
                            selectedWorkoutId = null
                        },
                        onDeleteClick = {
                            Log.d("Delete", "Silinecek ID: $selectedWorkoutId")
                            activityViewModel.deleteWorkouts(selectedWorkoutId!!)

                            selectedWorkoutId = null
                        }
                    )
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.dumbbell),
                        contentDescription = null,
                        tint = Color(0xFF2C3138),
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(Modifier.size(50.dp))
                    Text(
                        "No Workouts Yet",
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.lexendbold)),
                        fontSize = 22.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Create your first workout plan and start your journey today.",
                        style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.lexendregular)),
                            fontSize = 16.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
            if (showMenuSheetActivity) {
                ModalBottomSheet(
                    onDismissRequest = { showMenuSheetActivity = false },
                    sheetState = menuSheetState,
                    containerColor = Color(0xFF1C2126)
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
                            iconRes = R.drawable.accountcircle,
                            text = "View Profile",
                            onClick = { navController.navigate(Screens.Home.Profile.route) }
                        )

                        MenuItemRow(
                            iconRes = R.drawable.settings,
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
}

@Composable
private fun HomeTopBarActivity(
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
fun ExtendedActivityButton(
    onConfirmClick: () -> Unit,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit
) {
    ExtendedFloatingActionButton(
        onClick = {
            if (!expanded) {
                onExpandChange(true)
            } else {
                onConfirmClick()
                onExpandChange(false)
            }
        },
        icon = {
            Icon(
                painter = painterResource(R.drawable.projectfitnessplus),
                null,
                Modifier.size(30.dp)
            )
        },
        text = {
            Text(
                "Create Workout",
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                    fontSize = 18.sp
                )
            )
        },
        containerColor = Color(0xFFF1C40F),
        expanded = expanded,
        modifier = Modifier.padding(bottom = 50.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LongClickModalBottom(
    sheetState: SheetState,
    onDismiss: () -> Unit,      // Kapatma eylemi
    onDeleteClick: () -> Unit   // Silme eylemi
) {
    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
        // Modifier.height(300.dp) yerine wrapContentHeight daha sağlıklı olabilir ama senin tercihin
        modifier = Modifier.height(300.dp),
        containerColor = Color(0xFF1C2126) // Arka plan rengi (Opsiyonel)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Remove Workout",
                style = TextStyle(
                    fontSize = 20.sp,
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.lexendbold))
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Silme Butonu Örneği
            androidx.compose.material3.Button(
                onClick = onDeleteClick,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Delete", color = Color.White)
            }
        }
    }
}