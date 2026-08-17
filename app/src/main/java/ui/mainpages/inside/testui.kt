package ui.mainpages.inside

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.grozzbear.R
import data.local.viewmodel.WorkoutCompleteScreenViewModel
import data.local.viewmodel.WorkoutLogViewModel
import data.remote.FirebaseStorageHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ui.mainpages.navigation.Screens

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalInspectionMode

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Preview(showBackground = true, backgroundColor = 0xFF121417)
@Composable
fun WorkoutLogPreview() {
    val fakeExerciseName = "Barbell Bench Press"
    val pagerState = rememberPagerState(pageCount = { 3 })
    val formattedTime = "00:45:12"
    var showTimerSheet by remember { mutableStateOf(false) }
    val wordList = fakeExerciseName.split(" ", limit = 2)
    val firstWord = wordList[0]
    val secondWord = wordList[1]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121417))
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            PreviewTopBar(
                pagerState = pagerState,
                formattedTime = formattedTime,
                onTimerClick = { showTimerSheet = true }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    PreviewExerciseHeader(firstWord, secondWord)
                }

                item {
                    Box(
                        modifier = Modifier.height(70.dp).fillMaxWidth().padding(horizontal = 20.dp).background(Color.Gray.copy(alpha = 0.1f), shape = RoundedCornerShape(10.dp))
,
                        contentAlignment = Alignment.Center
                    ) {
                        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 30.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "REST TIMER",
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily(Font(R.font.lexendbold))
                                )
                                Text(
                                    text = "00:52",
                                    color = Color(0xFFF1C40F),
                                    fontSize = 30.sp,
                                    fontFamily = FontFamily(Font(R.font.lexendextrabold))
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(
                                onClick = { },
                                modifier = Modifier,
                                colors = IconButtonColors(containerColor = Color.Gray.copy(alpha = 0.3f), contentColor = Color.White, disabledContainerColor = Color.Red, disabledContentColor = Color.White)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier
                                )
                            }
                            IconButton(
                                onClick = { },
                                modifier = Modifier,
                                colors = IconButtonColors(containerColor = Color(0xFFF1C40F).copy(alpha = 1f), contentColor = Color.White, disabledContainerColor = Color.Red, disabledContentColor = Color.White)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(Modifier.size(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 25.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.width(30.dp))
                        Text("Weight", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, color = Color.White)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Reps", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, color = Color.White)
                        Spacer(modifier = Modifier.width(47.dp))
                    }
                }

                items(3) { index ->
                    PreviewSetRow(index + 1)
                }

                item {
                    Button(
                        onClick = {},
                        modifier = Modifier.padding(vertical = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Text("+ Add Set", color = Color.White)
                    }
                }
            }
        }

        // Timer BottomSheet Önizlemesi (Opsiyonel: true yaparak görebilirsin)
        if (showTimerSheet) {
            ModalBottomSheet(
                onDismissRequest = { showTimerSheet = false },
                containerColor = Color(0xFF1C2126)
            ) {
                FinalWorkoutTimer()
            }
        }
    }
}

// --- YARDIMCI PREVIEW BİLEŞENLERİ (BAĞIMSIZLAŞTIRILMIŞ) ---

@Composable
fun PreviewExerciseHeader(name: String, nameTwo: String) {
    Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
        Image(
            painter = painterResource(id = R.drawable.loginscreenphoto),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillWidth
        )
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color.Transparent, Color(0xFF121417)))
        ))
    }
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = name,
            color = Color.White,
            fontSize = 40.sp,
            fontFamily = FontFamily(Font(R.font.lexendextrabold)),
            modifier = Modifier.padding(0.dp).fillMaxWidth()
        )
        Text(
            text = nameTwo,
            color = Color(0xFFF1C40F),
            fontSize = 40.sp,
            fontFamily = FontFamily(Font(R.font.lexendextrabold)),
            modifier = Modifier.padding(0.dp).fillMaxWidth().graphicsLayer(
                translationY = -40f
            )
        )
    }
}

@Composable
fun PreviewSetRow(index: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 25.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("$index", modifier = Modifier.width(30.dp), color = Color.White, fontSize = 18.sp)
        SetLogItemWeight("80", {}, Modifier.weight(1f), false)
        Spacer(modifier = Modifier.width(10.dp))
        SetLogItemReps("12", {}, Modifier.weight(1f), true)
        Spacer(modifier = Modifier.width(15.dp))
        Box(
            modifier = Modifier.size(32.dp).border(1.dp, Color.Gray, RoundedCornerShape(50)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Check, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PreviewTopBar(pagerState: PagerState, totalSegments: Int = 3, formattedTime: String, onTimerClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF121417))
            .statusBarsPadding()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        // 1. SOL: Geri Butonu
        Icon(
            imageVector = Icons.Default.KeyboardArrowLeft,
            contentDescription = null,
            tint = Color(0xFFF1C40F),
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.CenterStart)
        )

        // 2. ORTA: Logo ve Sayaç (Dikey Hizalama)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            // Bu kısım önemli: Spacer yerine Arrangement kullanıyoruz
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Image(
                painter = painterResource(R.drawable.grozzlogo),
                contentDescription = null,
                modifier = Modifier
                    .height(55.dp) // Genişlik yerine yükseklik sabitlemek daha dengeli durur
                    .fillMaxWidth(0.4f), // Logonun ekranın %30'undan fazla yer kaplamasını engeller
                contentScale = ContentScale.Fit
            )

            // Sayaç Barı (1 of 3)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        color = Color.Transparent, // Timer ile aynı arka plan rengi
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .background(Color(0xFFF1C40F), androidx.compose.foundation.shape.CircleShape)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "${pagerState.currentPage + 1} of $totalSegments",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.lexendbold))
                )
            }
        }

        // 3. SAĞ: Zamanlayıcı
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .background(Color(0xFF1C2126), RoundedCornerShape(15.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .clickable { onTimerClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(4.dp).background(Color(0xFFF1C40F), androidx.compose.foundation.shape.CircleShape))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = formattedTime,
                color = Color(0xFFF1C40F),
                fontSize = 11.sp,
                fontFamily = FontFamily(Font(R.font.lexendbold))
            )
        }
    }
}