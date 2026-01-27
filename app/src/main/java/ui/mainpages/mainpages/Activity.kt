package ui.mainpages.mainpages

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.input.pointer.pointerInput
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
import com.example.projectfitness.data.local.viewmodel.ActivityViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ui.mainpages.navigation.NavigationBar
import ui.mainpages.navigation.Screens
import viewmodel.AuthViewModel
import viewmodel.ProjectFitnessViewModel
import viewmodel.ViewModelSave


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Activity(navController: NavController, viewModelSave: ViewModelSave, viewModel: ProjectFitnessViewModel, activityViewModel: ActivityViewModel, authViewModel: AuthViewModel,) {

    //Database Creation*************************************************************************************************************************************************************
    var lazyListState: LazyListState = rememberLazyListState()
    val currentUser = FirebaseAuth.getInstance().currentUser?.uid

    activityViewModel.refreshWorkouts(currentUser.toString())
    val myWorkoutsList = activityViewModel.myWorkoutsFlow.collectAsState(initial = emptyList()).value

    //******************************************************************************************************************************************************************************

    // Variable Initiliaze -------------------------------------------------------------------------
    var clickedProfile by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var expand by remember { mutableStateOf(false) }
    var selectedWorkoutId by remember { mutableStateOf<String?>(null) }
    var showMenuSheetActivity by remember { mutableStateOf(false) }
    val menuSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
            ExtendedActivityButton {
                navController.navigate(Screens.ChooseExercises.route)
            }
        },
        floatingActionButtonPosition = FabPosition.EndOverlay,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Spacer(
                modifier = Modifier
                    .padding(top = 30.dp)
            )
            Text(
                "My Workouts",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.lexendbold)),
                fontSize = 30.sp,
                modifier = Modifier
            )
            Spacer(
                modifier = Modifier
                    .padding(top = 40.dp)
            )
            if (myWorkoutsList.isNotEmpty()) {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                ){
                    itemsIndexed(myWorkoutsList) {index, item ->
                        val totalIcons = 5
                        val challengeDifficulty = item.workoutRating
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .height(200.dp)
                                .width(350.dp)
                                .background(
                                    Color(0xFF1C2126),
                                    shape = RoundedCornerShape(0.dp)
                                )
                                .combinedClickable(onClick = {
                                    navController.navigate("activityworkoutoverview/${item.workoutId}") {
                                        popUpTo(Screens.Activity.route)
                                    }
                                },
                                    onLongClick = {
                                        expand = true
                                        selectedWorkoutId = item.workoutId}),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painterResource(id = R.drawable.secondinfo),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                alpha = 0.5f,
                                modifier = Modifier
                                    .fillMaxSize(),
                            )
                            Text(
                                text = item.workoutName,
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
                        Spacer(Modifier.size(50.dp))
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
            }
            else {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(painter = painterResource(R.drawable.dumbbell), contentDescription = null, tint = Color(0xFF2C3138), modifier = Modifier.size(150.dp))
                    Spacer(Modifier.size(50.dp))
                    Text("No Workouts Yet", color = Color.White, fontFamily = FontFamily(Font(R.font.lexendbold)), fontSize = 22.sp)
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
        }
        if (showMenuSheetActivity) {
            ModalBottomSheet(
                onDismissRequest = { showMenuSheetActivity = false },
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
    }}
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
fun ExtendedActivityButton(onConfirmClick: () -> Unit) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    ExtendedFloatingActionButton(
        onClick = {
            if (!expanded) {
                expanded = true
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    onConfirmClick()
                    delay(500)
                    expanded = false
                }
            }
        },
        icon = {
            Icon(painter = painterResource(R.drawable.projectfitnessplus), "Extended create workout button", Modifier.size(40.dp))
        },
        text = {
            Text("Create Workout", style = TextStyle(fontFamily = FontFamily(Font(R.font.lexendbold)), fontSize = 18.sp))
        },
        containerColor = Color(0xFFF1C40F),
        expanded = expanded,
        modifier = Modifier
            .padding(bottom = 50.dp)
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