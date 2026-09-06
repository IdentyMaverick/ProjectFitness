package ui.mainpages.inside

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grozzbear.R
import com.grozzbear.ui.components.GrozzPrimaryButton
import com.grozzbear.ui.theme.GrozzBorder
import com.grozzbear.ui.theme.GrozzMuted
import com.grozzbear.ui.theme.GrozzOnBackground
import com.grozzbear.ui.theme.GrozzOnPrimary
import com.grozzbear.ui.theme.GrozzRadiusButton
import com.grozzbear.ui.theme.GrozzRadiusPanel
import com.grozzbear.ui.theme.GrozzSurface
import com.grozzbear.ui.theme.GrozzSystemBar
import com.grozzbear.ui.theme.GrozzYellow
import com.grozzbear.ui.theme.Lexend
import com.grozzbear.ui.theme.Oswald
import com.grozzbear.ui.util.counted
import data.local.entity.ExerciseLogWithSets
import data.local.viewmodel.WorkoutCompleteAnalysisScreenViewModel
import kotlin.math.roundToInt
import ui.mainpages.navigation.Screens

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WorkoutCompleteAnalysisScreen(
    navController: NavController,
    workoutCompleteAnalysisScreenViewModel: WorkoutCompleteAnalysisScreenViewModel,
) {
    val exercises by workoutCompleteAnalysisScreenViewModel.exerciseList.collectAsState()
    val ratioDistribution by workoutCompleteAnalysisScreenViewModel.ratioDistribution.collectAsState()
    val sortedMuscles =
        remember(ratioDistribution) {
            ratioDistribution.entries.sortedByDescending { it.value }
        }

    fun goHome() {
        navController.navigate(Screens.Home.route) {
            popUpTo(Screens.Home.route) { inclusive = true }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            HomeTopBarWorkoutCompleteAnalysisScreen(
                onBack = { navController.popBackStack() },
            )
        },
        containerColor = GrozzSystemBar,
        bottomBar = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(GrozzSystemBar)
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                GrozzPrimaryButton(
                    text = "Done",
                    onClick = ::goHome,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (sortedMuscles.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(16.dp))
                    SectionLabel(text = "TARGETED MUSCLES")
                    Spacer(Modifier.height(12.dp))
                    MuscleGroupChips(muscles = sortedMuscles)
                    Spacer(Modifier.height(28.dp))
                }
            } else {
                item { Spacer(Modifier.height(16.dp)) }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "EXERCISES PERFORMED",
                        color = GrozzOnBackground,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Lexend,
                        letterSpacing = 0.5.sp,
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "${exercises.size} TOTAL",
                        color = GrozzYellow,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Lexend,
                    )
                }
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(color = GrozzBorder)
                Spacer(Modifier.height(16.dp))
            }

            if (exercises.isEmpty()) {
                item {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "No exercises logged",
                            color = GrozzOnBackground,
                            fontSize = 16.sp,
                            fontFamily = Lexend,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Completed sets from this session will show up here.",
                            color = GrozzMuted,
                            fontSize = 13.sp,
                            fontFamily = Lexend,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            } else {
                items(exercises, key = { it.exerciseLog.logId }) { item ->
                    AnalysisExerciseCard(item)
                    Spacer(Modifier.height(12.dp))
                }
            }

            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = GrozzMuted,
        fontSize = 12.sp,
        fontFamily = Lexend,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun HomeTopBarWorkoutCompleteAnalysisScreen(onBack: () -> Unit) {
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
                tint = GrozzOnBackground,
            )
        }

        Text(
            text = "WORKOUT ANALYSIS",
            color = GrozzOnBackground,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MuscleGroupChips(muscles: List<Map.Entry<String, Float>>) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        muscles.forEach { (muscle, ratio) ->
            val label =
                buildString {
                    append(muscle)
                    val pct = ratio.roundToInt()
                    if (pct > 0) append(" · $pct%")
                }
            Box(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(GrozzRadiusButton))
                        .background(GrozzYellow)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
            ) {
                Text(
                    text = label,
                    color = GrozzOnPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Lexend,
                )
            }
        }
    }
}

@Composable
private fun AnalysisExerciseCard(exerciseData: ExerciseLogWithSets) {
    var expanded by remember { mutableStateOf(false) }
    val completedSets =
        remember(exerciseData.setLogs) {
            exerciseData.setLogs.filter { it.clicked }
        }
    val bodyPart = exerciseData.exerciseLog.bodyPart.trim()

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(GrozzRadiusPanel))
                .background(GrozzSurface)
                .border(1.dp, GrozzBorder, RoundedCornerShape(GrozzRadiusPanel))
                .clickable { expanded = !expanded }
                .animateContentSize()
                .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier =
                    Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(GrozzYellow),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.dumbbellicon128),
                    contentDescription = null,
                    tint = GrozzOnPrimary,
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exerciseData.exerciseLog.exerciseName,
                    color = GrozzOnBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = Lexend,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (bodyPart.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = bodyPart.uppercase(),
                        color = GrozzYellow,
                        fontSize = 12.sp,
                        fontFamily = Lexend,
                        fontWeight = FontWeight.Bold,
                    )
                }
                if (!expanded) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = counted(completedSets.size, "set"),
                        color = GrozzMuted,
                        fontSize = 12.sp,
                        fontFamily = Lexend,
                    )
                }
            }

            Icon(
                imageVector =
                    if (expanded) {
                        Icons.Default.KeyboardArrowUp
                    } else {
                        Icons.Default.KeyboardArrowDown
                    },
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = GrozzYellow,
                modifier = Modifier.size(24.dp),
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column {
                Spacer(Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    SetHeaderCell("SET", Modifier.weight(1f), TextAlign.Start)
                    SetHeaderCell("KG", Modifier.weight(1f), TextAlign.Center)
                    SetHeaderCell("REPS", Modifier.weight(1f), TextAlign.Center)
                }
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(color = GrozzBorder)
                Spacer(Modifier.height(4.dp))

                if (completedSets.isEmpty()) {
                    Text(
                        text = "No completed sets",
                        color = GrozzMuted,
                        fontSize = 13.sp,
                        fontFamily = Lexend,
                        modifier = Modifier.padding(vertical = 12.dp),
                    )
                } else {
                    completedSets.forEach { set ->
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "${set.setIndex + 1}",
                                color = GrozzOnBackground,
                                fontSize = 15.sp,
                                fontFamily = Lexend,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                text = formatWeight(set.weight),
                                color = GrozzOnBackground,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Lexend,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                            )
                            Text(
                                text = "${set.reps}",
                                color = GrozzOnBackground,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Lexend,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SetHeaderCell(text: String, modifier: Modifier = Modifier, align: TextAlign) {
    Text(
        text = text,
        color = GrozzMuted,
        fontSize = 11.sp,
        fontFamily = Lexend,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp,
        modifier = modifier,
        textAlign = align,
    )
}

private fun formatWeight(weight: Float): String = if (weight % 1f == 0f) {
    weight.toInt().toString()
} else {
    "%.1f".format(weight)
}
