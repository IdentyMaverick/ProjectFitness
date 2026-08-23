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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.grozzbear.R
import com.grozzbear.ui.components.GrozzComingSoonPanel
import com.grozzbear.ui.components.GrozzTopBarLogo
import com.grozzbear.ui.theme.GrozzBorder
import com.grozzbear.ui.theme.GrozzError
import com.grozzbear.ui.theme.GrozzOnBackground
import com.grozzbear.ui.theme.GrozzSurface
import com.grozzbear.ui.theme.GrozzSystemBar
import com.grozzbear.ui.theme.GrozzYellow
import ui.mainpages.navigation.NavigationBar
import ui.mainpages.navigation.Screens
import ui.mainpages.navigation.navigateToLoginAfterLogout
import viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Meal(navController: NavController, authViewModel: AuthViewModel) {
    var showMenuSheet by remember { mutableStateOf(false) }
    val menuSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            MealTopBar(onMenuClick = { showMenuSheet = true })
        },
        containerColor = GrozzSystemBar,
        bottomBar = {
            NavigationBar(
                navController = navController,
                indexs = 3,
                flag = false,
                flag2 = false,
                flag3 = false,
                flag4 = true
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Image(
                painter = painterResource(R.drawable.mealscreenbackgroundphoto),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                GrozzSystemBar.copy(alpha = 0.55f),
                                GrozzSystemBar.copy(alpha = 0.88f)
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                GrozzComingSoonPanel(
                    title = "Nutrition",
                    accentTitle = "Module",
                    message = "Personalized meal plans and macro tracking powered by AI.",
                    eyebrow = "Phase 2 launch",
                    footer = "Soon"
                )
            }
        }

        if (showMenuSheet) {
            ModalBottomSheet(
                onDismissRequest = { showMenuSheet = false },
                sheetState = menuSheetState,
                containerColor = GrozzSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 40.dp)
                ) {
                    Text(
                        text = "Menu",
                        style = MaterialTheme.typography.titleLarge,
                        color = GrozzOnBackground,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    MenuItemRow(
                        iconRes = R.drawable.accountcircle,
                        text = "View Profile",
                        onClick = {
                            showMenuSheet = false
                            navController.navigate(Screens.Home.Profile.route)
                        }
                    )
                    MenuItemRow(
                        iconRes = R.drawable.settings,
                        text = "Settings",
                        onClick = {
                            showMenuSheet = false
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
                            showMenuSheet = false
                            authViewModel.logout()
                            navController.navigateToLoginAfterLogout()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MealTopBar(onMenuClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onMenuClick) {
            Icon(
                painter = painterResource(R.drawable.accountcircle),
                contentDescription = "Menu",
                modifier = Modifier.size(26.dp),
                tint = GrozzOnBackground
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        GrozzTopBarLogo()
        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(48.dp))
    }
}
