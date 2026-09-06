package ui.mainpages.mainpages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grozzbear.R
import com.grozzbear.projectfitness.data.local.entity.WorkoutWithExercises
import com.grozzbear.projectfitness.data.local.viewmodel.HomesViewModel
import com.grozzbear.ui.components.GrozzPhotoCard
import com.grozzbear.ui.components.GrozzTopBarLogo
import com.grozzbear.ui.theme.GrozzMuted
import com.grozzbear.ui.theme.GrozzOnBackground
import com.grozzbear.ui.theme.GrozzRadiusChip
import com.grozzbear.ui.theme.GrozzSystemBar
import com.grozzbear.ui.theme.GrozzTextSecondary
import com.grozzbear.ui.theme.GrozzYellow
import com.grozzbear.ui.theme.Lexend
import com.grozzbear.ui.theme.Oswald
import com.grozzbear.ui.util.counted
import com.grozzbear.ui.util.isChallengeType
import com.grozzbear.ui.util.isCoachType
import com.grozzbear.ui.util.safeWorkoutPainter
import com.grozzbear.ui.util.workoutTypeLabel
import ui.mainpages.navigation.Screens

@Composable
fun AllWorkouts(
    navController: NavController,
    homesViewModel: HomesViewModel,
    filter: String = Screens.AllWorkouts.FILTER_ALL
) {
    val allWorkouts by homesViewModel.workoutsFlow.collectAsState(initial = emptyList())
    val filteredWorkouts = remember(allWorkouts, filter) {
        when (filter) {
            Screens.AllWorkouts.FILTER_COACH -> allWorkouts.filter {
                isCoachType(it.workout.workoutType)
            }

            Screens.AllWorkouts.FILTER_CHALLENGE -> allWorkouts.filter {
                isChallengeType(it.workout.workoutType)
            }

            else -> allWorkouts
        }
    }
    val (titleTop, titleBottom, emptyTitle, emptyBody) = remember(filter) {
        when (filter) {
            Screens.AllWorkouts.FILTER_COACH -> WorkoutListCopy(
                titleTop = "COACH'S",
                titleBottom = "PICKS",
                emptyTitle = "No coach picks yet",
                emptyBody = "Coach workouts will show up here when available."
            )

            Screens.AllWorkouts.FILTER_CHALLENGE -> WorkoutListCopy(
                titleTop = "CHALLENGES",
                titleBottom = "CATALOGUE",
                emptyTitle = "No challenges yet",
                emptyBody = "Challenge workouts will show up here when available."
            )

            else -> WorkoutListCopy(
                titleTop = "ALL",
                titleBottom = "WORKOUTS LIST",
                emptyTitle = "No workouts yet",
                emptyBody = "Catalogue workouts will show up here when available."
            )
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            HomeTopBarAllWorkouts(onBack = { navController.popBackStack() })
        },
        containerColor = GrozzSystemBar,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .navigationBarsPadding(),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 8.dp, bottom = 4.dp)
                ) {
                    HorizontalDivider(
                        thickness = 2.dp,
                        color = GrozzYellow,
                        modifier = Modifier.width(28.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = titleTop,
                            color = GrozzOnBackground,
                            fontSize = 20.sp,
                            fontFamily = Oswald,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = titleBottom,
                            color = GrozzYellow,
                            fontSize = 20.sp,
                            fontFamily = Oswald,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = counted(filteredWorkouts.size, "workout"),
                        color = GrozzMuted,
                        fontSize = 13.sp,
                        fontFamily = Lexend,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (filteredWorkouts.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = emptyTitle,
                            color = GrozzOnBackground,
                            fontSize = 16.sp,
                            fontFamily = Lexend,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = emptyBody,
                            color = GrozzMuted,
                            fontSize = 13.sp,
                            fontFamily = Lexend,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(
                    items = filteredWorkouts,
                    key = { it.workout.workoutId }
                ) { item ->
                    AllWorkoutCard(
                        workout = item,
                        onClick = {
                            navController.navigate("workoutsettingscreen/${item.workout.workoutId}")
                        },
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }
        }
    }
}

private data class WorkoutListCopy(
    val titleTop: String,
    val titleBottom: String,
    val emptyTitle: String,
    val emptyBody: String
)

@Composable
private fun AllWorkoutCard(
    workout: WorkoutWithExercises,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val difficulty = workout.workout.workoutRating.coerceIn(0, 5)
    val exerciseCount = workout.exercises.size

    GrozzPhotoCard(
        painter = safeWorkoutPainter(workout.workout.image),
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = workoutTypeLabel(workout.workout.workoutType).uppercase(),
                color = GrozzYellow,
                fontSize = 11.sp,
                fontFamily = Lexend,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = workout.workout.workoutName,
                color = GrozzOnBackground,
                fontFamily = Lexend,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (exerciseCount > 0) {
                WorkoutTag(
                    text = counted(exerciseCount, "exercise"),
                    icon = R.drawable.shutterspeedfilledicon128,
                    textColor = GrozzTextSecondary,
                    iconColor = GrozzTextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                repeat(5) { index ->
                    Icon(
                        painter = painterResource(id = R.drawable.skullicon128),
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (index < difficulty) {
                            GrozzYellow
                        } else {
                            Color.White.copy(alpha = 0.35f)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeTopBarAllWorkouts(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                painter = painterResource(R.drawable.left),
                contentDescription = "Back",
                modifier = Modifier.size(24.dp),
                tint = GrozzOnBackground
            )
        }

        GrozzTopBarLogo(modifier = Modifier.align(Alignment.Center))

        Spacer(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(48.dp)
        )
    }
}

@Composable
fun WorkoutTag(
    text: String,
    icon: Int,
    textColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(GrozzRadiusChip))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            fontFamily = Lexend,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
