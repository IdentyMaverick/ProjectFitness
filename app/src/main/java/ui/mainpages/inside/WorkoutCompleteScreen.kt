package ui.mainpages.inside

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.grozzbear.R
import com.grozzbear.ui.components.GrozzPrimaryButton
import com.grozzbear.ui.theme.GrozzBorder
import com.grozzbear.ui.theme.GrozzMuted
import com.grozzbear.ui.theme.GrozzOnBackground
import com.grozzbear.ui.theme.GrozzOnPrimary
import com.grozzbear.ui.theme.GrozzRadiusPanel
import com.grozzbear.ui.theme.GrozzSurface
import com.grozzbear.ui.theme.GrozzSystemBar
import com.grozzbear.ui.theme.GrozzTextSecondary
import com.grozzbear.ui.theme.GrozzYellow
import com.grozzbear.ui.theme.Lexend
import com.grozzbear.ui.theme.Oswald
import data.local.viewmodel.LeaderboardViewModel
import data.local.viewmodel.WorkoutCompleteAnalysisScreenViewModel
import data.local.viewmodel.WorkoutCompleteScreenViewModel
import ui.mainpages.navigation.Screens

private val SetsAccent = Color(0xFF00E676)
private val RepsAccent = Color(0xFFF87216)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WorkoutCompleteScreen(
    navController: NavController,
    workoutCompleteScreenViewModel: WorkoutCompleteScreenViewModel,
    workoutCompleteAnalysisScreenViewModel: WorkoutCompleteAnalysisScreenViewModel,
    leaderboardViewModel: LeaderboardViewModel
) {
    val userName by workoutCompleteScreenViewModel.userName.collectAsState()
    val userFirstName = remember(userName) {
        userName.trim().substringBefore(' ').ifBlank { "Athlete" }
    }
    val formattedDate by workoutCompleteScreenViewModel.formattedDate.collectAsState()
    val elapsedTime by workoutCompleteScreenViewModel.elapsedTime.collectAsState()
    val totalSetsCompleted by workoutCompleteScreenViewModel.totalSetsCompleted.collectAsState()
    val totalRepsCompleted by workoutCompleteScreenViewModel.totalRepsCompleted.collectAsState()
    val prExercises by workoutCompleteScreenViewModel.prExercises.collectAsState()
    val scrollState = rememberScrollState()
    var uploadedExercises by remember { mutableStateOf(setOf<String>()) }

    fun goHome() {
        navController.navigate(Screens.Home.route) {
            popUpTo(Screens.Home.route) { inclusive = true }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            HomeTopBarWorkoutCompleteScreen(onClose = ::goHome)
        },
        containerColor = GrozzSystemBar,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        BackHandler(onBack = ::goHome)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            Box(
                modifier = Modifier.size(88.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val circleRadius = size.minDimension / 2
                    drawCircle(
                        color = GrozzYellow.copy(alpha = 0.35f),
                        radius = circleRadius,
                        style = Fill
                    )
                    drawCircle(
                        color = GrozzYellow,
                        radius = circleRadius - 2.dp.toPx(),
                        style = Stroke(width = 3.dp.toPx())
                    )
                }
                Icon(
                    painter = painterResource(R.drawable.trophyfilledicon128),
                    contentDescription = null,
                    tint = GrozzYellow,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Great Job, $userFirstName",
                color = GrozzOnBackground,
                fontSize = 22.sp,
                fontFamily = Lexend,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "You completed your workout",
                color = GrozzYellow,
                fontSize = 16.sp,
                fontFamily = Lexend,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = listOf(formattedDate, elapsedTime)
                    .filter { it.isNotBlank() }
                    .joinToString(" · "),
                color = GrozzMuted,
                fontSize = 14.sp,
                fontFamily = Lexend,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                ProgressCircle(
                    progress = 1f,
                    value = totalSetsCompleted,
                    label = "SETS COMPLETED",
                    color = SetsAccent,
                    iconRes = R.drawable.timer10icon128,
                    iconColor = SetsAccent,
                    modifier = Modifier.weight(1f)
                )
                ProgressCircle(
                    progress = 1f,
                    value = totalRepsCompleted,
                    label = "REPS COMPLETED",
                    color = RepsAccent,
                    iconRes = R.drawable.dumbbellicon128,
                    iconColor = RepsAccent,
                    modifier = Modifier.weight(1f)
                )
            }

            if (prExercises.isNotEmpty()) {
                Spacer(Modifier.height(28.dp))
                Text(
                    text = "PERSONAL RECORDS",
                    color = GrozzMuted,
                    fontSize = 12.sp,
                    fontFamily = Lexend,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                prExercises.forEach { exercise ->
                    WorkoutRecordCard(
                        exerciseName = exercise,
                        leaderboardViewModel = leaderboardViewModel,
                        isUploaded = exercise in uploadedExercises,
                        onUploaded = {
                            uploadedExercises = uploadedExercises + exercise
                        }
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }

            Spacer(Modifier.height(24.dp))

            GrozzPrimaryButton(
                text = "View Full Analysis",
                onClick = {
                    workoutCompleteAnalysisScreenViewModel.setWorkoutList()
                    navController.navigate(Screens.WorkoutCompleteAnalysisScreen.route)
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Done",
                color = GrozzTextSecondary,
                fontSize = 15.sp,
                fontFamily = Lexend,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clickable(onClick = ::goHome)
                    .padding(vertical = 12.dp)
            )
        }
    }
}

@Composable
private fun HomeTopBarWorkoutCompleteScreen(onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .height(56.dp)
    ) {
        IconButton(
            onClick = onClose,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                painter = painterResource(R.drawable.closeicon128),
                contentDescription = "Close",
                modifier = Modifier.size(24.dp),
                tint = GrozzOnBackground
            )
        }

        Row(
            modifier = Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SESSION",
                color = GrozzOnBackground,
                fontSize = 20.sp,
                letterSpacing = 0.sp,
                fontFamily = Oswald,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "SUMMARY",
                color = GrozzYellow,
                fontSize = 20.sp,
                letterSpacing = 0.sp,
                fontFamily = Oswald,
                fontWeight = FontWeight.Bold
            )
        }

        // Balance the close button so the title stays visually centered.
        Spacer(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(48.dp)
        )
    }
}

@Composable
private fun ProgressCircle(
    progress: Float,
    value: Int,
    label: String,
    color: Color,
    iconRes: Int,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(GrozzRadiusPanel))
            .background(GrozzSurface)
            .border(1.dp, GrozzBorder, RoundedCornerShape(GrozzRadiusPanel))
            .padding(vertical = 20.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(84.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 5.dp.toPx()
                val center = Offset(size.width / 2, size.height / 2)
                val radius = (size.minDimension - strokeWidth) / 2

                drawCircle(
                    color = color,
                    radius = radius,
                    center = center,
                    alpha = 0.2f,
                    style = Stroke(width = strokeWidth)
                )
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = 360f * progress.coerceIn(0f, 1f),
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = "$value",
            color = GrozzOnBackground,
            fontSize = 18.sp,
            fontFamily = Lexend,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            color = GrozzMuted,
            fontSize = 11.sp,
            fontFamily = Lexend,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun WorkoutRecordCard(
    exerciseName: String,
    leaderboardViewModel: LeaderboardViewModel,
    isUploaded: Boolean,
    onUploaded: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(GrozzRadiusPanel))
            .background(GrozzSurface)
            .border(1.dp, GrozzYellow.copy(alpha = 0.35f), RoundedCornerShape(GrozzRadiusPanel))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(GrozzYellow),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.workspacepremium128icon),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = GrozzOnPrimary
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "New Personal Record",
                    color = GrozzOnBackground,
                    fontSize = 17.sp,
                    fontFamily = Lexend,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "$exerciseName PR unlocked",
                    color = GrozzTextSecondary,
                    fontSize = 14.sp,
                    fontFamily = Lexend,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        ProofUploadSection(
            isUploaded = isUploaded,
            onUriSelected = { uri ->
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                if (!uid.isNullOrBlank()) {
                    leaderboardViewModel.uploadPrProof(
                        uri,
                        uid,
                        exerciseName,
                        0.0,
                        ""
                    )
                    onUploaded()
                }
            }
        )
    }
}

@Composable
private fun ProofUploadSection(
    onUriSelected: (Uri) -> Unit,
    isUploaded: Boolean
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { video: Uri? ->
        video ?: return@rememberLauncherForActivityResult
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, video)
            val durationInMs = retriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull()
                ?: 0L
            if (durationInMs <= 15_000) {
                onUriSelected(video)
            } else {
                Toast.makeText(context, "Video must be under 15 seconds.", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (_: Exception) {
            Toast.makeText(context, "Could not read that video.", Toast.LENGTH_SHORT).show()
        } finally {
            retriever.release()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(GrozzSystemBar)
            .border(1.dp, GrozzBorder, RoundedCornerShape(12.dp))
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            .clickable(enabled = !isUploaded) {
                launcher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                )
            }
            .padding(vertical = 20.dp, horizontal = 16.dp)
    ) {
        if (!isUploaded) {
            Icon(
                painter = painterResource(id = R.drawable.arrowuploadprogress128icon),
                contentDescription = null,
                tint = GrozzYellow,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Upload video to verify",
                color = GrozzOnBackground,
                fontSize = 15.sp,
                fontFamily = Lexend,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Clips must be under 15 seconds.",
                color = GrozzMuted,
                fontSize = 12.sp,
                fontFamily = Lexend,
                textAlign = TextAlign.Center
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = SetsAccent,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Uploaded successfully",
                    color = GrozzOnBackground,
                    fontSize = 14.sp,
                    fontFamily = Lexend,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
