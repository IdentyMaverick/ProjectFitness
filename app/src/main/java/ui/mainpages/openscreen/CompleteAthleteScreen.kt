package ui.mainpages.openscreen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.grozzbear.ui.components.GrozzPrimaryButton
import com.grozzbear.ui.theme.GrozzBorder
import com.grozzbear.ui.theme.GrozzMuted
import com.grozzbear.ui.theme.GrozzOnBackground
import com.grozzbear.ui.theme.GrozzOnPrimary
import com.grozzbear.ui.theme.GrozzRadiusChip
import com.grozzbear.ui.theme.GrozzSurface
import com.grozzbear.ui.theme.GrozzSystemBar
import com.grozzbear.ui.theme.GrozzYellow
import com.grozzbear.ui.theme.Lexend
import com.grozzbear.ui.theme.Oswald
import data.local.entity.NutritionGoal
import data.local.viewmodel.MealViewModel
import data.local.viewmodel.PersonalInformationsScreenViewModel
import data.remote.UserProfile
import ui.mainpages.inside.BirthDateSelector
import ui.mainpages.inside.GenderSelector
import ui.mainpages.inside.ProfileFieldLabel
import ui.mainpages.inside.ProfileOutlinedField
import ui.mainpages.navigation.BodyStatsPrefs
import ui.mainpages.navigation.Screens
import viewmodel.ProfileUiState

@Composable
fun CompleteAthleteScreen(
    navController: NavController,
    personalInformationsScreenViewModel: PersonalInformationsScreenViewModel,
    mealViewModel: MealViewModel,
) {
    val context = LocalContext.current
    val profileState by personalInformationsScreenViewModel.profileState.collectAsState()

    LaunchedEffect(Unit) {
        personalInformationsScreenViewModel.loadUid()
    }

    fun finishSetup() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        BodyStatsPrefs.markPrompted(context, uid)
        navController.navigate(Screens.Home.route) {
            popUpTo(Screens.CompleteAthleteScreen.route) { inclusive = true }
            launchSingleTop = true
        }
    }

    BackHandler { finishSetup() }

    val profile =
        (profileState as? ProfileUiState.Ready)?.profile ?: UserProfile()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(GrozzSystemBar)
                .windowInsetsPadding(WindowInsets.systemBars)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 28.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "YOUR BODY",
            color = GrozzYellow,
            fontFamily = Lexend,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Text(
                text = "Set up ",
                color = GrozzOnBackground,
                fontFamily = Oswald,
                fontWeight = FontWeight.SemiBold,
                fontSize = 26.sp,
            )
            Text(
                text = "Grozz",
                color = GrozzYellow,
                fontFamily = Oswald,
                fontWeight = FontWeight.SemiBold,
                fontSize = 26.sp,
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Used for calories and water. You can skip and add this later in Profile.",
            color = GrozzMuted,
            fontFamily = Lexend,
            fontSize = 13.sp,
        )

        Spacer(modifier = Modifier.height(28.dp))

        CompleteAthleteForm(
            profile = profile,
            onContinue = { gender, birthDate, height, weight, goal ->
                val first = profile.first.ifBlank { "Sporcu" }
                personalInformationsScreenViewModel.updateUserInformation(
                    first = first,
                    gender = gender,
                    birthDate = birthDate,
                    height = height,
                    weight = weight,
                )
                mealViewModel.setGoal(goal)
                Toast.makeText(context, "You're set. Let's train.", Toast.LENGTH_SHORT).show()
                finishSetup()
            },
            onSkip = { finishSetup() },
        )
    }
}

@Composable
private fun CompleteAthleteForm(
    profile: UserProfile,
    onContinue: (Boolean, String, String, String, NutritionGoal) -> Unit,
    onSkip: () -> Unit,
) {
    var gender by remember(profile.gender) { mutableStateOf(profile.gender) }
    var birthDate by remember(profile.birthDate) { mutableStateOf(profile.birthDate) }
    var height by remember(profile.height) { mutableStateOf(profile.height) }
    var weight by remember(profile.weight) { mutableStateOf(profile.weight) }
    var goal by remember { mutableStateOf(NutritionGoal.MAINTAIN) }
    val canContinue = birthDate.isNotBlank() && height.isNotBlank() && weight.isNotBlank()

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            GenderSelector(
                gender = gender,
                onGenderChange = { gender = it },
                modifier = Modifier.weight(1f),
            )
            BirthDateSelector(
                birthDate = birthDate,
                onBirthDateChange = { birthDate = it },
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                ProfileFieldLabel("Height (cm)", padded = false)
                ProfileOutlinedField(
                    value = height,
                    onValueChange = { height = it.filter { ch -> ch.isDigit() || ch == '.' }.take(6) },
                    placeholder = "Height",
                    keyboardType = KeyboardType.Decimal,
                    horizontalPadding = 0.dp,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                ProfileFieldLabel("Weight (kg)", padded = false)
                ProfileOutlinedField(
                    value = weight,
                    onValueChange = { weight = it.filter { ch -> ch.isDigit() || ch == '.' }.take(6) },
                    placeholder = "Weight",
                    keyboardType = KeyboardType.Decimal,
                    horizontalPadding = 0.dp,
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))
        ProfileFieldLabel("Goal", padded = false)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            NutritionGoal.entries.forEach { option ->
                val selected = option == goal
                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(GrozzRadiusChip))
                            .background(if (selected) GrozzYellow else GrozzSurface)
                            .border(
                                1.dp,
                                if (selected) GrozzYellow else GrozzBorder,
                                RoundedCornerShape(GrozzRadiusChip),
                            ).clickable { goal = option }
                            .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = option.label,
                        color = if (selected) GrozzOnPrimary else GrozzOnBackground,
                        fontFamily = Lexend,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
        GrozzPrimaryButton(
            text = "Continue",
            onClick = { onContinue(gender, birthDate, height.trim(), weight.trim(), goal) },
            enabled = canContinue,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Skip for now",
            color = GrozzYellow,
            fontFamily = Lexend,
            fontSize = 14.sp,
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable(onClick = onSkip)
                    .padding(8.dp),
        )
    }
}
