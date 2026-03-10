package ui.mainpages.inside

import SocialViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grozzbear.R

@Composable
fun NotificationScreen(navController: NavController, socialViewModel: SocialViewModel) {
    val nickname by socialViewModel.nickname.collectAsState()
    val notification by socialViewModel.getNotification(nickname).observeAsState()
    val notificationList = notification ?: emptyList()
    LaunchedEffect(notificationList) {
        notificationList.filter { !it.isRead }.forEach {
            socialViewModel.markAllAsRead(nickname)
        }
    }
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = { HomeTopBarNotificationScreen(navController) },
        containerColor = Color(0xFF121417),
        floatingActionButtonPosition = FabPosition.EndOverlay,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .clickable(
                    onClick = {},
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (notificationList.isEmpty()) {
                Spacer(Modifier.height(30.dp))
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No notification",
                        color = Color.White,
                        fontSize = 20.sp,
                        letterSpacing = 0.sp,
                        fontFamily = FontFamily(Font(R.font.oswaldbold))
                    )
                }
            } else {
                Spacer(Modifier.height(50.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(notificationList) { index, item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 40.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (!item.isRead) Color(0xFFF1C40F) // Okunmamışsa SARI (Vurgulu)
                                        else Color.Transparent  // Okunmuşsa GRİ
                                        , shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(10.dp)
                                    .fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(R.drawable.personadd),
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier
                                            .size(50.dp)
                                            .background(
                                                Color(0xFFF1C40F),
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .padding(10.dp)
                                    )
                                    Spacer(Modifier.width(20.dp))
                                    Column() {
                                        Row() {
                                            Text(
                                                text = item.title,
                                                color = Color(0xFFF1C40F),
                                                fontSize = 15.sp,
                                                letterSpacing = 0.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(Modifier.weight(1f))
                                            Text(
                                                text = TimeConvert(item.time),
                                                color = Color.White,
                                                fontSize = 15.sp,
                                                letterSpacing = 0.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(Modifier.height(10.dp))
                                        Text(
                                            text = item.message,
                                            color = Color.White,
                                            fontSize = 15.sp,
                                            letterSpacing = 0.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun HomeTopBarNotificationScreen(navController: NavController) {
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
            text = "NOTIFICATION",
            color = Color.White,
            fontSize = 24.sp,
            letterSpacing = 0.sp,
            fontFamily = FontFamily(Font(R.font.oswaldbold))
        )


        Spacer(Modifier.weight(1f))

        IconButton(onClick = {}) {
            Icon(
                painter = painterResource(R.drawable.circlenotifications),
                contentDescription = null,
                modifier = Modifier.size(25.dp),
                tint = Color.Transparent
            )
        }
    }
}

fun TimeConvert(time: Long): String {
    val sdf = java.text.SimpleDateFormat("dd/MM/yyyy")
    val date = java.util.Date(time)
    return sdf.format(date)
}