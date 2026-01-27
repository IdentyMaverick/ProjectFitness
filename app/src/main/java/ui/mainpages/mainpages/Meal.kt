package ui.mainpages.mainpages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.projectfitness.R
import ui.mainpages.navigation.NavigationBar

@Composable
fun Meal(navController: NavController) {
    var flagggg by remember { mutableStateOf(false) }
    var flagggg2 by remember { mutableStateOf(false) }
    var flagggg3 by remember { mutableStateOf(false) }
    var flagggg4 by remember { mutableStateOf(true) }
    var clickedProfile by remember { mutableStateOf(false) }
    var showMenuSheet by remember { mutableStateOf(false) }


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
            NavigationBar(navController = navController, indexs, flagggg, flagggg2, flagggg3, flagggg4)
        },
        floatingActionButton = {
            ExtendedStartButton {
                //Start function
            }
        },
        floatingActionButtonPosition = FabPosition.EndOverlay,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(
                modifier = Modifier
                    .padding(top = 30.dp)
            )
            Text(
                "TOO SOON",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.lexendbold)),
                fontSize = 30.sp
            )
        }
    }
    /*Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF181F26))
    )
    {
        Text(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 5.dp),
            text = "PROJECT FITNESS",
            fontFamily = FontFamily(Font(R.font.lexendextralight)),
            color = Color(0xFFF1C40F),
            style = TextStyle(fontSize = 20.sp, letterSpacing = 10.sp)
        )

        Text(
            text = "SOON",
            modifier = Modifier.align(Alignment.Center),
            color = Color.White,
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.lexendextralight)),
                fontSize = 100.sp
            )
        )
        Text(
            text = "MEAL TIME",
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 150.dp),
            color = Color.White,
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.lexendextralight)),
                fontSize = 25.sp,
                letterSpacing = 10.sp
            )
        )
    }*/
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