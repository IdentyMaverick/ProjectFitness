package ui.mainpages.inside

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grozzbear.R
import data.local.entity.SetLogEntity
import data.local.viewmodel.WorkoutCompleteAnalysisScreenViewModel
import ui.mainpages.navigation.Screens

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WorkoutCompleteAnalysisScreen(
    navController: NavController,
    workoutCompleteAnalysisScreenViewModel: WorkoutCompleteAnalysisScreenViewModel
) {
    val exercises by workoutCompleteAnalysisScreenViewModel.exerciseList.collectAsState()
    val ratioDistrubition by workoutCompleteAnalysisScreenViewModel.ratioDistribution.collectAsState()

    Scaffold(
        topBar = {
            HomeTopBarWorkoutCompleteAnalysisScreen(navController)
        },
        containerColor = Color(0xFF121417),
        bottomBar = {
            // Sabit duran bitirme butonu
            FinishWorkoutButton {
                navController.navigate(Screens.Home.route) {
                    popUpTo(Screens.Home.route) { inclusive = true }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(Modifier.height(30.dp))
                ConsistencyProgressCircle(0.92f, 92, "CONSISTENCY", Color(0xFFF1C40F))
            }

            // 2. Hedeflenen Kas Grupları
            item {
                Spacer(Modifier.height(40.dp))
                Text(
                    text = "TARGETED MUSCLES",
                    color = Color.White.copy(alpha = 0.4f),
                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(16.dp))
                MuscleGroup(ratioDistrubition.keys)
            }

            // 3. Egzersiz Listesi Başlığı
            item {
                Spacer(Modifier.height(40.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "EXERCISES PERFORMED",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.lexendbold))
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "${exercises.size} TOTAL",
                        color = Color(0xFFF1C40F),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.lexendbold))
                    )
                }
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    color = Color.White.copy(alpha = 0.1f)
                )
                Spacer(Modifier.height(16.dp))
            }

            // 4. Dinamik Egzersiz Kartları
            itemsIndexed(exercises) { _, item ->
                ExerciseExpandableCard(item)
                Spacer(Modifier.height(24.dp))
            }

            // Alt boşluk (Butonun üstüne binmemesi için)
            item { Spacer(Modifier.height(20.dp)) }
        }
    }
}

@Composable
private fun HomeTopBarWorkoutCompleteAnalysisScreen(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
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

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "WORKOUT ANALYSIS",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.oswaldbold))
            )
        }

        Spacer(Modifier.weight(1f))

        IconButton(onClick = { /* Paylaşma işlemi */ }) {
            Icon(
                painter = painterResource(R.drawable.shareicon128),
                contentDescription = null,
                modifier = Modifier.size(25.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
fun ConsistencyProgressCircle(progress: Float, value: Int, label: String, color: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
        Canvas(modifier = Modifier.size(170.dp)) {
            val strokeWidth = 10.dp.toPx()
            // Arka plandaki sönük halka
            drawCircle(color = color.copy(alpha = 0.1f), style = Stroke(width = strokeWidth))
            // Ön plandaki ilerleme yayı
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$value%",
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily(Font(R.font.lexendextrabold))
            )
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 12.sp,
                letterSpacing = 1.5.sp,
                fontFamily = FontFamily(Font(R.font.lexendregular))
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MuscleGroup(muscles: Set<String>) {
    FlowRow(
        modifier = Modifier
            .padding(horizontal = 30.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        muscles.forEach { muscle ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFF1C40F))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = muscle,
                    color = Color.Black,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.lexendsemibold))
                )
            }
        }
    }
}

@Composable
fun WorkoutCompleteAnalysisScreenExerciseCard(exerciseName: String, setList: List<SetLogEntity>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = exerciseName,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.lexendbold))
            )
            Spacer(Modifier.weight(1f))
        }
        Spacer(Modifier.height(6.dp))
        Column() {
            setList.forEach { sets ->
                if (sets.clicked)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        DetailText(value = (sets.setIndex + 1).toString(), unit = "Set")
                        DetailText(value = sets.reps.toString(), unit = "Reps")
                        DetailText(value = sets.weight.toString(), unit = "Kg")
                    }
            }
        }
        Spacer(Modifier.height(16.dp))
        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
    }
}

@Composable
fun DetailText(value: String, unit: String) {
    Row(modifier = Modifier.padding(end = 12.dp)) {
        Text(
            text = "$value ",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = unit,
            color = Color.White.copy(alpha = 0.2f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun FinishWorkoutButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .height(56.dp),
        color = Color(0xFFF1C40F),
        shape = RoundedCornerShape(28.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable { onClick() }
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "FINISH WORKOUT",
                color = Color.Black,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                letterSpacing = 1.sp
            )
        }
    }
}