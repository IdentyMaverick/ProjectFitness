package ui.mainpages.openscreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grozzbear.R
import kotlinx.coroutines.launch
import ui.mainpages.navigation.Screens

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InfoHorizontalScreen(navController: NavController) {
    val pages = listOf(
        InfoHorizontal(
            title = "TRACK YOUR PROGRESS",
            description = "Track and save every exercise with precision. A modern hub for your physical growth.",
            verticalText = "WORKOUT",
            imagesRes = R.drawable.infohorizontalscreenfirstphoto
        ),
        InfoHorizontal(
            title = "MONITOR GROWTH",
            description = "Visualize your progress with deep insight and performance charts.",
            verticalText = "ANALYTICS",
            imagesRes = R.drawable.infohorizontalscreensecondphoto
        ),
        InfoHorizontal(
            title = "EAT SMART",
            description = "Complement your hard work with a tailored nutrition plan for maximum results.",
            verticalText = "NUTRITION",
            imagesRes = R.drawable.infohorizontalscreenthirdphoto
        ),
        InfoHorizontal(
            title = "JOIN THE CLUB",
            description = "Share your journey, compete with friends, and stay motivated together.",
            verticalText = "COMMUNITY",
            imagesRes = R.drawable.infohorizontalscreenfourthphoto
        )
    )
    val pagerState =
        androidx.compose.foundation.pager.rememberPagerState(pageCount = { pages.size })
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF181F26))
    ) {
        // PAGER: Kaydırılabilir ana içerik
        androidx.compose.foundation.pager.HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { position ->
            val page = pages[position]

            // Burası senin tasarladığımız sayfa yapısı
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(id = pages[position].imagesRes),
                    contentDescription = null,
                    alpha = 0.5f,
                    contentScale = ContentScale.Crop
                )

                // İçerik (Dikey yazı ve metinler)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .fillMaxWidth(0.65f)
                    ) {
                        HorizontalDivider(
                            thickness = 3.dp,
                            color = Color(0xFFF1C40F),
                            modifier = Modifier.width(45.dp)
                        )
                        Spacer(Modifier.height(30.dp))
                        Text(
                            text = page.title,
                            color = Color.White,
                            fontSize = 45.sp,
                            lineHeight = 45.sp,
                            fontFamily = FontFamily(Font(R.font.oswaldbold))
                        )
                        Spacer(Modifier.height(20.dp))
                        Text(
                            text = page.description,
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(R.font.lexendregular))
                        )
                    }

                    // Dinamik Dikey Yazı
                    Text(
                        text = page.verticalText,
                        color = Color(0xFFF1C40F),
                        fontFamily = FontFamily(Font(R.font.oswaldbold)),
                        fontSize = 70.sp, // Boyutu bir tık daha düşürmek güvenli bir alan yaratır
                        softWrap = false,
                        maxLines = 1,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .graphicsLayer {
                                rotationZ = 90f
                            }
                            .layout { measurable, constraints ->
                                // Constraints.Infinite kullanarak metnin "ekran bitti" uyarısı almasını engelliyoruz
                                val placeable = measurable.measure(
                                    constraints.copy(
                                        minWidth = 0,
                                        maxWidth = Int.MAX_VALUE,
                                        minHeight = 0,
                                        maxHeight = Int.MAX_VALUE
                                    )
                                )

                                layout(placeable.height, placeable.width) {
                                    placeable.place(
                                        x = -(placeable.width / 2 - placeable.height / 2),
                                        y = -(placeable.height / 2 - placeable.width / 2)
                                    )
                                }
                            }
                    )
                }
            }
        }

        // ÜST KATMAN: Sabit durması gereken Logo ve Skip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 0.dp, end = 30.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val context = androidx.compose.ui.platform.LocalContext.current
            Image(
                painter = painterResource(id = R.drawable.grozzholdsdumbbellbothhandsnobackgroundxml),
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                colorFilter = ColorFilter.tint(Color(0xFFF1C40F))
            )
            Text(
                text = "SKIP",
                modifier = Modifier.clickable(
                    onClick = {
                        navController.navigate(Screens.LoginScreen.route)
                        setHorizontalScreen(context)
                    },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ),
                color = Color.White.copy(alpha = 0.5f),
                fontFamily = FontFamily(Font(R.font.lexendregular))
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 30.dp, bottom = 60.dp)
        ) {
            // Dinamik Sayfa İndikatörü
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(pages.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .size(width = if (isSelected) 28.dp else 8.dp, height = 8.dp)
                            .background(
                                color = if (isSelected) Color(0xFFF1C40F) else Color.White.copy(
                                    alpha = 0.3f
                                ),
                                shape = CircleShape
                            )
                    )
                }
            }
            val context = androidx.compose.ui.platform.LocalContext.current
            Spacer(modifier = Modifier.height(height = 40.dp))
            Text(
                text = if (pagerState.currentPage == pages.size - 1) "Get Started" else "Next",
                modifier = Modifier.clickable {
                    if (pagerState.currentPage < pages.size - 1) {
                        // Bir sonraki sayfaya kaydır
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        // DURUMU KAYDET: Bir daha bu ekran gelmesin
                        setHorizontalScreen(context)

                        // Login'e git
                        navController.navigate(Screens.LoginScreen.route) {
                            // Geri tuşuna basınca boarding'e dönmemesi için geçmişi temizle
                            popUpTo(0)
                        }
                    }
                },
                fontSize = 24.sp,
                color = Color(0xFFF1C40F),
                fontFamily = FontFamily(Font(R.font.oswaldbold))
            )
        }
    }
}

// Context üzerinden SharedPreferences'a erişmek için bir yardımcı fonksiyon
fun setHorizontalScreen(context: android.content.Context) {
    val sharedPref = context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putBoolean("is_boarding_completed", true)
        apply()
    }
}