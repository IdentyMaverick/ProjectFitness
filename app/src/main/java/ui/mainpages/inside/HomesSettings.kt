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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grozzbear.R
import ui.mainpages.navigation.Screens
import viewmodel.AuthViewModel
import viewmodel.ProjectFitnessViewModel
import viewmodel.ViewModelProfile
import viewmodel.ViewModelSave

@Composable
fun HomesSettings(
    navController: NavController,
    viewModelSave: ViewModelSave,
    viewModel: ProjectFitnessViewModel,
    viewModelProfile: ViewModelProfile,
    authViewModel: AuthViewModel
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            HomeTopBar(navController)
        },
        containerColor = Color(0xFF121417),
        floatingActionButtonPosition = FabPosition.EndOverlay,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Settings",
                color = Color.White,
                fontSize = 25.sp,
                fontFamily = FontFamily(Font(R.font.lexendbold))
            )
            Spacer(modifier = Modifier.height(30.dp))

            Spacer(modifier = Modifier.height(8.dp))
            // Sarı kısa çizgi
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White)
            )

            // Profile Section
            SettingSectionTitle("Profile", Color(0xFFF1C40F))
            SettingRow(
                "Personal Informations",
                textColor = Color.White,
                onClick = { navController.navigate("personalinformationsscreen") })

            // Notifications Section
            SettingSectionTitle("Notifications", Color(0xFFF1C40F))
            SettingRow("Workout Reminders", textColor = Color.White.copy(alpha = 0.5f))
            SettingRow("Achievement Reminders", textColor = Color.White.copy(alpha = 0.5f))

            // Help Section
            SettingSectionTitle("Help & Support", Color(0xFFF1C40F))
            SettingRow("FAQ & Contact & Feedback", onClick = {
                navController.navigate("faqcontactfeedbackscreen")
            }, textColor = Color.White)

            // Log Out
            SettingRow("Log Out", onClick = {
                authViewModel.logout()
                navController.navigate(Screens.LoginScreen.route)
            }, textColor = Color.Red)

        }
    }
}

@Composable
fun HomeTopBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                painter = painterResource(R.drawable.left),
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

        IconButton(onClick = {}) {
            Icon(
                painter = painterResource(R.drawable.projectfitnesspointheavy),
                contentDescription = null,
                modifier = Modifier.size(25.dp),
                tint = Color.Transparent
            )
        }
    }
}

@Composable
fun SettingRow(
    text: String,
    onClick: () -> Unit = {},
    textColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() } // Tıklama özelliği
            .padding(vertical = 12.dp, horizontal = 30.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = TextStyle(
                color = textColor,
                fontFamily = FontFamily(Font(R.font.lexendregular)),
                fontSize = 18.sp
            )
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.White
        )
    }
}

@Composable
fun SettingSectionTitle(title: String, color: Color) {
    Text(
        text = title,
        style = TextStyle(
            color = color,
            fontFamily = FontFamily(Font(R.font.lexendbold)),
            fontSize = 20.sp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 30.dp, top = 20.dp, bottom = 8.dp)
    )
}