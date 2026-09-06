package ui.mainpages.inside

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.pm.PackageInfoCompat
import androidx.navigation.NavController
import com.grozzbear.R
import com.grozzbear.ui.theme.GrozzMuted
import com.grozzbear.ui.theme.GrozzOnPrimary
import com.grozzbear.ui.theme.GrozzSystemBar
import com.grozzbear.ui.theme.GrozzYellow
import com.grozzbear.ui.theme.Lexend
import com.grozzbear.ui.theme.Oswald
import ui.mainpages.navigation.navigateToLoginAfterLogout
import viewmodel.AuthViewModel

@Composable
fun HomesSettings(navController: NavController, authViewModel: AuthViewModel) {
    // Temporary local state — later move these into DataStore / ViewModel.
    var workoutReminders by remember { mutableStateOf(true) }
    var achievementReminders by remember { mutableStateOf(true) }
    var keepScreenOn by remember { mutableStateOf(true) }
    var hapticsEnabled by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val versionLabel =
        remember(context) {
            runCatching {
                val info = context.packageManager.getPackageInfo(context.packageName, 0)
                val name = info.versionName.orEmpty().ifBlank { "?" }
                val code = PackageInfoCompat.getLongVersionCode(info)
                "v$name ($code)"
            }.getOrDefault("—")
        }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            HomeTopBar(navController)
        },
        containerColor = GrozzSystemBar,
        floatingActionButtonPosition = FabPosition.EndOverlay,
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier =
                    Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White),
            )

            SettingSectionTitle("Profile", GrozzYellow)
            SettingRow(
                text = "Personal Informations",
                textColor = Color.White,
                onClick = { navController.navigate("personalinformationsscreen") },
            )

            SettingSectionTitle("App", GrozzYellow)
            SettingSwitchRow(
                text = "Keep screen on",
                checked = keepScreenOn,
                onCheckedChange = { keepScreenOn = it },
                enabled = false,
            )
            SettingSwitchRow(
                text = "Haptics",
                checked = hapticsEnabled,
                onCheckedChange = { hapticsEnabled = it },
                enabled = false,
            )

            SettingSectionTitle("Notifications", GrozzYellow)
            SettingSwitchRow(
                text = "Workout Reminders",
                checked = workoutReminders,
                onCheckedChange = { workoutReminders = it },
                enabled = false,
            )
            SettingSwitchRow(
                text = "Achievement Reminders",
                checked = achievementReminders,
                onCheckedChange = { achievementReminders = it },
                enabled = false,
            )

            SettingSectionTitle("Help & Support", GrozzYellow)
            SettingRow(
                text = "FAQ & Contact & Feedback",
                textColor = Color.White,
                onClick = { navController.navigate("faqcontactfeedbackscreen") },
            )

            SettingSectionTitle("About", GrozzYellow)
            SettingInfoRow(
                label = "Version",
                value = versionLabel,
            )

            SettingRow(
                text = "Log Out",
                textColor = Color.Red,
                type = "logout",
                onClick = {
                    authViewModel.logout()
                    navController.navigateToLoginAfterLogout()
                },
            )
        }
    }
}

@Composable
fun HomeTopBar(navController: NavController) {
    SettingsFlowTopBar(
        title = "SETTINGS",
        onBack = { navController.popBackStack() },
    )
}

/**
 * Shared chrome for Settings + Personal Info + Support.
 * Keep back icon / title size identical across that flow.
 */
@Composable
fun SettingsFlowTopBar(title: String, onBack: () -> Unit) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .height(56.dp),
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart),
        ) {
            Icon(
                painter = painterResource(R.drawable.left),
                contentDescription = "Back",
                modifier = Modifier.size(24.dp),
                tint = Color.White,
            )
        }

        Text(
            text = title,
            color = Color.White,
            fontSize = 20.sp,
            fontFamily = Oswald,
            modifier = Modifier.align(Alignment.Center),
        )

        Spacer(
            modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .size(48.dp),
        )
    }
}

@Composable
fun SettingRow(text: String, onClick: () -> Unit = {}, textColor: Color, type: String = "normal") {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 12.dp, horizontal = 30.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style =
                TextStyle(
                    color = textColor,
                    fontFamily = Lexend,
                    fontSize = 16.sp,
                ),
        )
        Spacer(modifier = Modifier.weight(1f))
        if (type == "normal") {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp),
            )
        } else {
            Icon(
                painter = painterResource(R.drawable.logouticon128),
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

/**
 * Settings row with a Switch on the right (no chevron).
 *
 * checked = current on/off value
 * onCheckedChange = called when user flips the switch → update your state there
 */
@Composable
fun SettingSwitchRow(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, enabled: Boolean = true) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 30.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style =
                TextStyle(
                    color = if (enabled) Color.White else Color.White.copy(alpha = 0.5f),
                    fontFamily = Lexend,
                    fontSize = 16.sp,
                ),
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors =
                SwitchDefaults.colors(
                    checkedThumbColor = GrozzOnPrimary,
                    checkedTrackColor = GrozzYellow,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = GrozzMuted,
                    uncheckedBorderColor = GrozzMuted,
                ),
        )
    }
}

@Composable
fun SettingInfoRow(label: String, value: String) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 30.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style =
                TextStyle(
                    color = Color.White,
                    fontFamily = Lexend,
                    fontSize = 16.sp,
                ),
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            style =
                TextStyle(
                    color = GrozzMuted,
                    fontFamily = Lexend,
                    fontSize = 16.sp,
                ),
        )
    }
}

@Composable
fun SettingSectionTitle(title: String, color: Color) {
    Text(
        text = title,
        style =
            TextStyle(
                color = color,
                fontFamily = Lexend,
                fontSize = 16.sp,
            ),
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = 30.dp, top = 20.dp, bottom = 8.dp),
    )
}
