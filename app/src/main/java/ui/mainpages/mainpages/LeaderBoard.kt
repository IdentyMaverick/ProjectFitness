package ui.mainpages.mainpages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.projectfitness.R
import ui.mainpages.navigation.NavigationBar
import ui.mainpages.navigation.Screens
import viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderBoard(navController: NavController, authViewModel: AuthViewModel) {

    var expandable by remember { mutableStateOf(false) }
    var expandable2 by remember { mutableStateOf(false) }
    var muscleList by remember {
        mutableStateOf(
            listOf(
                "Chest",
                "Calf",
                "Quads",
                "Hamstrings",
                "Calves",
                "Glutes",
                "IT Band",
                "Plantar Fascia",
                "Hip Flexors",
                "Abductors",
                "Triceps",
                "Biceps",
                "Forearms",
                "Lats",
                "Abs",
                "Adductors",
                "Lower Back",
                "Upper Back",
                "Neck",
                "Obliques",
                "Traps"
            )
        )
    }
    var exerciseList by remember { mutableStateOf(listOf("Bench Press")) }
    var selectedText by remember { mutableStateOf("Muscle Group") }
    var selectedExercise by remember { mutableStateOf("Exercise") }

    var flag by remember { mutableStateOf(true) }
    var flag2 by remember { mutableStateOf(true) }
    var flag3 by remember { mutableStateOf(true) }
    var flag4 by remember { mutableStateOf(true) }

    var flaggg by remember { mutableStateOf(false) }
    var flaggg2 by remember { mutableStateOf(false) }
    var flaggg3 by remember { mutableStateOf(true) }
    var flaggg4 by remember { mutableStateOf(false) }

    val exercises = arrayOf("Chest Press", "Bench Press")
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionTest by remember { mutableStateOf(exercises[0]) }

    var tabTitles = listOf("1 RP MAX", "WORKOUT TIME")
    var selectedTabIndex by remember { mutableStateOf(0) }

    val configuration = LocalConfiguration.current
    val screenwidthDp = configuration.screenWidthDp

    var clickedProfile by remember { mutableStateOf(false) }
    var showMenuSheet by remember { mutableStateOf(false) }

    var showMenuSheetLeaderBoard by remember { mutableStateOf(false) }
    val menuSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            HomeTopBar(
                clickedProfile = clickedProfile,
                onProfileClick = {
                    clickedProfile = true
                    navController.navigate("profile")
                },
                onMenuClick = { showMenuSheetLeaderBoard = true }
            )
        },
        containerColor = Color(0xFF121417),
        bottomBar = {
            // senin NavigationBar fonksiyonun zaten hazır
            val indexs = 2
            val flaggg = false
            val flaggg2 = false
            val flaggg3 = true
            val flaggg4 = false
            NavigationBar(navController = navController, indexs, flaggg, flaggg2, flaggg3, flaggg4)
        },
        floatingActionButton = {
        },
        floatingActionButtonPosition = FabPosition.EndOverlay,
        modifier = Modifier.fillMaxSize()
    )
    { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(
                modifier = Modifier
                    .padding(top = 0.dp)
            )
            Text(
                "Leaderboard",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.lexendbold)),
                fontSize = 35.sp
            )
            Spacer(
                modifier = Modifier
                    .padding(top = 0.dp)
            )

            SecondaryTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color(0xFF121417),
                contentColor = Color.White,
                indicator = {
                    // TabRowDefaults.SecondaryIndicator(color = Color.Cyan)
                    TabRowDefaults.SecondaryIndicator(
                        color = Color(0xFFF1C40F),
                        height = 1.5f.dp,
                        modifier = Modifier.tabIndicatorOffset(selectedTabIndex)
                    )
                }) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title, maxLines = 2, overflow = TextOverflow.Ellipsis,
                                style = TextStyle(
                                    fontFamily = FontFamily(Font(R.font.lexendregular)),
                                    fontSize = 15.sp,
                                    letterSpacing = 1.sp
                                )
                            )
                        }
                    )
                }
            }
            if (selectedTabIndex == 0) {
                Row(
                    modifier = Modifier
                ) {
                    Button(
                        onClick = { expandable = !expandable },
                        modifier = Modifier
                            .height(30.dp)
                            .width(160.dp)
                            .align(Alignment.CenterVertically),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(5.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = selectedText, style = TextStyle(
                                fontFamily = FontFamily(Font(R.font.lexendextralight)),
                                letterSpacing = 3.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.size(26.dp),
                            tint = Color(0xFFF1C40F)
                        )
                    }
                    Box(modifier = Modifier.offset(y = 35.dp, x = (-100).dp))
                    {
                        DropdownMenu(
                            expanded = expandable, onDismissRequest = { expandable = false },
                            modifier = Modifier
                                .background(Color.White)
                                .height(200.dp)
                                .width(100.dp)
                        ) {
                            for (i in muscleList) {
                                DropdownMenuItem(
                                    text = { Text(i) },
                                    onClick = {
                                        selectedText = i
                                        expandable = false
                                    },
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .height(30.dp)
                                        .width(100.dp)
                                )
                            }
                        }
                    }


                    Spacer(modifier = Modifier.size(50.dp))

                    Button(
                        onClick = { expandable2 = !expandable2 },
                        modifier = Modifier
                            .height(30.dp)
                            .width(160.dp)
                            .align(Alignment.CenterVertically),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(5.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = selectedExercise, style = TextStyle(
                                fontFamily = FontFamily(Font(R.font.lexendextralight)),
                                letterSpacing = 3.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.size(26.dp),
                            tint = Color(0xFFF1C40F)
                        )
                    }
                    Box(modifier = Modifier.offset(y = 35.dp, x = (-100).dp))
                    {
                        DropdownMenu(
                            expanded = expandable2, onDismissRequest = { expandable2 = false },
                            modifier = Modifier
                                .background(Color.White)
                                .height(200.dp)
                                .width(100.dp)
                        ) {
                            for (i in exerciseList) {
                                DropdownMenuItem(
                                    text = { Text(i) },
                                    onClick = {
                                        selectedExercise = i
                                        expandable2 = false
                                    },
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .height(30.dp)
                                        .width(100.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        if (showMenuSheetLeaderBoard) {
            ModalBottomSheet(
                onDismissRequest = { showMenuSheetLeaderBoard = false },
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
                        onClick = {  }
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