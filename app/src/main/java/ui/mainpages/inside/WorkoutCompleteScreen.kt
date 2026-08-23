import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.grozzbear.R
import data.local.viewmodel.LeaderboardViewModel
import data.local.viewmodel.WorkoutCompleteAnalysisScreenViewModel
import data.local.viewmodel.WorkoutCompleteScreenViewModel
import ui.mainpages.navigation.Screens

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WorkoutCompleteScreen(
    navController: NavController,
    workoutCompleteScreenViewModel: WorkoutCompleteScreenViewModel,
    workoutCompleteAnalysisScreenViewModel: WorkoutCompleteAnalysisScreenViewModel,
    leaderboardViewModel: LeaderboardViewModel
) {
    val userName = workoutCompleteScreenViewModel.userName.collectAsState().value
    val userFirstName = userName.split(" ")[0]
    val formattedDate = workoutCompleteScreenViewModel.formattedDate.collectAsState().value
    val elapsedTime = workoutCompleteScreenViewModel.elapsedTime.collectAsState().value
    val totalSetsCompleted =
        workoutCompleteScreenViewModel.totalSetsCompleted.collectAsState().value
    val totalRepsCompleted =
        workoutCompleteScreenViewModel.totalRepsCompleted.collectAsState().value
    val card by workoutCompleteScreenViewModel.prExercises.collectAsState()
    Log.d("CARDIS", card.toString())
    val scrollState = rememberScrollState()
    val isUploaded = remember { mutableStateOf(false) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            HomeTopBarWorkoutCompleteScreen(navController)
        },
        containerColor = Color(0xFF121417),
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        BackHandler(
            enabled = true
        ) {}
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier.matchParentSize()
                ) {
                    val circleRadius = size.minDimension / 2 // Dairenin yaricapi
                    // Ic dolgu cizimi
                    drawCircle(
                        color = Color(0xFFF1C40F).copy(alpha = 0.4f),
                        radius = circleRadius,
                        style = Fill
                    ) // Icini boyar
                    drawCircle( // Dis kenarlik cizimi
                        color = Color(0xFFF1C40F),
                        radius = circleRadius,
                        style = Stroke(width = 4f)
                    )
                }
                Icon(
                    painter = painterResource(R.drawable.trophyfilledicon128),
                    contentDescription = null,
                    tint = Color(0xFFF1C40F)
                )
            }
            Spacer(Modifier.height(20.dp))
            Text(
                text = "Great Job, $userFirstName",
                color = Color.White,
                fontSize = 30.sp,
                fontFamily = FontFamily(Font(R.font.lexendsemibold))
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "You Completed Workout",
                color = Color(0xFFF1C40F),
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.lexendsemibold))
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "$formattedDate - $elapsedTime",
                color = Color(0xFF8A98AC),
                fontSize = 15.sp,
                fontFamily = FontFamily(Font(R.font.lexendsemibold))
            )
            Spacer(Modifier.height(30.dp))
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                ProgressCircle(
                    1f,
                    totalSetsCompleted,
                    "SETS COMPLETED",
                    color = Color(0xFF00E676),
                    id = R.drawable.timer10icon128,
                    iconColor = Color(0xFF00E676)
                )
                Spacer(Modifier.width(20.dp))
                ProgressCircle(
                    1f,
                    totalRepsCompleted,
                    "REPS COMPLETED",
                    color = Color(0xFFF87216),
                    id = R.drawable.dumbbellicon128,
                    iconColor = Color(0xFFF87216)
                )
            }
            Spacer(Modifier.height(40.dp))
            if (card.isNotEmpty()) {
                card.forEach {
                    WorkoutRecordCard(onNavigate = {}, it, leaderboardViewModel, isUploadedClick = {isUploaded.value = true}, isUploaded = isUploaded.value)
                }
            }
            WorkoutFullAnalysisButton(
                onNavigate = {},
                onNavigateAnalysis = {
                    workoutCompleteAnalysisScreenViewModel.setWorkoutList()
                    workoutCompleteAnalysisScreenViewModel.calculateMuscleDistribution()
                    workoutCompleteAnalysisScreenViewModel.calculateRatioDistribution()
                    navController.navigate(Screens.WorkoutCompleteAnalysisScreen.route)
                }
            )
            Box() {
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}

@Composable
private fun HomeTopBarWorkoutCompleteScreen(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = { navController.navigate(Screens.Home.route) }) {
            Icon(
                painter = painterResource(R.drawable.closeicon128),
                contentDescription = null,
                modifier = Modifier.size(25.dp),
                tint = Color.White
            )
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = "SESSION",
            color = Color.White,
            fontSize = 24.sp,
            letterSpacing = 0.sp,
            fontFamily = FontFamily(Font(R.font.oswaldbold))
        )
        Spacer(Modifier.width(5.dp))
        Text(
            text = "SUMMARY",
            color = Color(0xFFF1C40F),
            fontSize = 24.sp,
            letterSpacing = 0.sp,
            fontFamily = FontFamily(Font(R.font.oswaldbold))
        )

        Spacer(Modifier.weight(1f))

        IconButton(onClick = {}) { // WIP
            Icon(
                painter = painterResource(R.drawable.shareicon128),
                contentDescription = null,
                modifier = Modifier.size(25.dp),
                tint = Color.Transparent
            )
        }
    }
}

@Composable
private fun ProgressCircle(
    progress: Float,
    value: Int,
    label: String,
    color: Color,
    id: Int = 0,
    iconColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(90.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier.matchParentSize()
            ) {
                val strokeWidth = 5.dp.toPx() // Halkanın kalınlığı
                val center = Offset(size.width / 2, size.height / 2)
                val radius = (size.minDimension - strokeWidth) / 2
                val color = color

                drawCircle(
                    color = color,
                    radius = radius,
                    center = center,
                    alpha = 0.2f, // Sönük görünmesi için
                    style = Stroke(width = strokeWidth)
                )

                // 2. İlerleme Yayını (Progress) Çiz
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = 360f * progress, // Yüzdeye göre ne kadar döneceği
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round
                    ) // Uçları yuvarlak yap
                )
            }
            Icon(
                painter = painterResource(id),
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(30.dp)
            )
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text = "$value",
            color = Color.White,
            fontSize = 15.sp,
            fontFamily = FontFamily(Font(R.font.lexendextrabold))
        )
        Spacer(Modifier.height(5.dp))
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 12.sp,
            fontFamily = FontFamily(Font(R.font.lexendmedium))
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WorkoutRecordCard(
    onNavigate: () -> Unit,
    card: String,
    leaderboardViewModel: LeaderboardViewModel,
    isUploadedClick: () -> Unit,
    isUploaded: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Gray.copy(alpha = 0.1f))
            .padding(20.dp)
    ) {
        Column() {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF1C40F)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.workspacepremium128icon),
                        contentDescription = null,
                        modifier = Modifier.size(35.dp),
                        tint = Color.Black
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "New Personal Record!",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "${card} PR record passed",
                        color = Color.White,
                        fontSize = 15.sp,
                        lineHeight = 18.sp
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .border(2.dp, Color.Transparent)
                    .background(Color.Transparent)
                    .height(150.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                    ProofUploadSection(
                        onUriSelected = { uri ->
                            val uid = FirebaseAuth.getInstance().currentUser?.uid
                            if (!uid.isNullOrBlank()) {
                                leaderboardViewModel.uploadPrProof(
                                    uri,
                                    uid,
                                    card,
                                    0.0,
                                    ""
                                )
                            }
                        },
                        isUploadedClick,
                        isUploaded = isUploaded
                    )
            }
        }
    }
}
@Composable
fun WorkoutFullAnalysisButton(onNavigateAnalysis: () -> Unit, onNavigate: () -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GradientSmallButton(
            text = "View Full Analysis",
            modifier = Modifier.height(50.dp).weight(1f),
            onNavigate = onNavigateAnalysis
        )
    }
}

@Composable
fun GradientSmallButton(
    text: String,
    modifier: Modifier = Modifier,
    onNavigate: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onNavigate() }
            .background(Color.Gray.copy(alpha = 0.2f))
            .height(40.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f),
                fontFamily = FontFamily(Font(R.font.lexendextrabold))
            )

            Icon(
                painter = painterResource(id = R.drawable.keyboarddoublearrowright),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ProofUploadSection(onUriSelected: (android.net.Uri) -> Unit, isUploadedClick: () -> Unit, isUploaded: Boolean) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { video: Uri? ->
        video?.let { video ->
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(context, video)
                val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                val durationInMs = time?.toLong() ?: 0

                if (durationInMs <= 15_000) {
                    onUriSelected(video)
                    isUploadedClick()
                } else Toast.makeText(
                    context,
                    "Video must be under 15 seconds.",
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            .clickable(enabled = !isUploaded) {
                launcher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
            }
    ) {
        if (!isUploaded) {
            Icon(
                painter = painterResource(id = R.drawable.arrowuploadprogress128icon),
                contentDescription = null,
                tint = Color(0xFFF1C40F),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Upload Video/Photo to\nVerify Record",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "* Make sure that, your PR videos should be under 15 seconds.",
                color = Color.Gray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.Green,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Uploaded Successfully",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}