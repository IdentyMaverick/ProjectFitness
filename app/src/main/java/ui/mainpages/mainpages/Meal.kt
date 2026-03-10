package ui.mainpages.mainpages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grozzbear.R
import ui.mainpages.navigation.NavigationBar
import ui.mainpages.navigation.Screens
import viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Meal(navController: NavController, authViewModel: AuthViewModel) {
    var flagggg by remember { mutableStateOf(false) }
    var flagggg2 by remember { mutableStateOf(false) }
    var flagggg3 by remember { mutableStateOf(false) }
    var flagggg4 by remember { mutableStateOf(true) }
    var clickedProfile by remember { mutableStateOf(false) }
    var showMenuSheet by remember { mutableStateOf(false) }
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
                onMenuClick = { showMenuSheet = true }
            )
        },
        containerColor = Color(0xFF121417),
        bottomBar = {
            // senin NavigationBar fonksiyonun zaten hazır
            var indexs = 3
            val flagggg = false
            val flagggg2 = false
            val flagggg3 = false
            val flagggg4 = true
            NavigationBar(
                navController = navController,
                indexs,
                flagggg,
                flagggg2,
                flagggg3,
                flagggg4
            )
        },
        floatingActionButton = {

        },
        floatingActionButtonPosition = FabPosition.EndOverlay,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box() {
            Image(
                painterResource(R.drawable.mealscreenbackgroundphoto),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .background(color = Color(0xFF181F26), shape = RoundedCornerShape(40.dp)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(40.dp))
                Box(
                    modifier = Modifier
                        .background(
                            color = Color(0xFFF1C40F).copy(alpha = 0.3f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .width(110.dp)
                        .height(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "PHASE 2 LAUNCH",
                        color = Color(0xFFF1C40F),
                        fontSize = 10.sp,
                        fontFamily = FontFamily(Font(R.font.lexendbold))
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "NUTRITION",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontFamily = FontFamily(Font(R.font.lexendextrabold))

                )
                Text(
                    text = "MODULE",
                    color = Color(0xFFF1C40F),
                    fontSize = 30.sp,
                    fontFamily = FontFamily(Font(R.font.lexendextrabold))
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    textAlign = TextAlign.Center,
                    text = "Personalized meal plans and macro tracking powered by AI.",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.lexendregular))
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    textAlign = TextAlign.Center,
                    text = "Soon",
                    color = Color(0xFFF1C40F),
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.lexendregular))
                )
            }
        }
        if (showMenuSheet == true) {
            ModalBottomSheet(
                onDismissRequest = { showMenuSheet = false },
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