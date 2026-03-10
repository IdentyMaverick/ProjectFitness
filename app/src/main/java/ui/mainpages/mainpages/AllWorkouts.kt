package ui.mainpages.mainpages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grozzbear.R
import com.grozzbear.projectfitness.data.local.viewmodel.HomesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllWorkouts(
    navController: NavController,
    homesViewModel: HomesViewModel
) {
    var clickedProfile by remember { mutableStateOf(false) }
    var showMenuSheet by remember { mutableStateOf(false) }

    var lazyListState = rememberLazyListState()
    val allWorkouts by homesViewModel.workoutsFlow.collectAsState(initial = emptyList())

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            HomeTopBarAllWorkouts(
                clickedProfile = clickedProfile,
                onProfileClick = { navController.popBackStack() },
                onMenuClick = { showMenuSheet = true }
            )
        },
        containerColor = Color(0xFF121417),
        bottomBar = {},
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "ALL",
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.oswaldbold)),
                    fontSize = 40.sp, // Görsele uygun olarak biraz büyüttüm
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false // Android'in otomatik eklediği tepe boşluğunu siler
                        ),
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Bottom,
                            trim = LineHeightStyle.Trim.Both
                        )
                    )
                )

                Text(
                    text = "WORKOUTS",
                    color = Color(0xFFF1C40F),
                    fontFamily = FontFamily(Font(R.font.oswaldbold)),
                    fontSize = 40.sp,
                    modifier = Modifier.offset(y = (-8).dp), // İki metni iyice birbirine yaklaştırır
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false // Alt boşluğu siler
                        ),
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Top,
                            trim = LineHeightStyle.Trim.Both
                        )
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Sarı kısa çizgi
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White)
                )
            }
            Spacer(
                modifier = Modifier
                    .padding(top = 20.dp)
            )

            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
            ) {
                itemsIndexed(allWorkouts) { index, item ->
                    val totalIcons = 5
                    val challengeDifficulty = item.component1().workoutRating
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp) // Yüksekliği görsele uygun artırdım
                            .clip(RoundedCornerShape(24.dp)) // Daha yumuşak köşeler
                            .background(
                                Color(0xFF1C2126),
                                shape = RoundedCornerShape(0.dp)
                            )
                            .clickable(onClick = { navController.navigate("workoutsettingscreen/${item.workout.workoutId}") }),
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
                                style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                                fontFamily = FontFamily(Font(R.font.oswaldbold))
                            )
                            Text(
                                text = item.component1().workoutName,
                                color = Color.White,
                                style = TextStyle(
                                    fontSize = 24.sp,
                                    fontFamily = FontFamily(Font(R.font.lexendbold))
                                ),
                                fontFamily = FontFamily(Font(R.font.oswaldbold))
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
                    Spacer(Modifier.size(10.dp))
                }
            }
        }
    }
}

@Composable
private fun HomeTopBarAllWorkouts(
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

        IconButton(onClick = onMenuClick) {
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
fun WorkoutTag(text: String, icon: Int, textColor: Color, iconColor: Color) {
    Row(
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(50.dp))
            .padding(horizontal = 12.dp, vertical = 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, color = textColor, fontSize = 12.sp)
    }
}