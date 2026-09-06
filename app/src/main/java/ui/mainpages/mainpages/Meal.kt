package ui.mainpages.mainpages

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grozzbear.R
import com.grozzbear.ui.components.GrozzPanel
import com.grozzbear.ui.components.GrozzPrimaryButton
import com.grozzbear.ui.components.GrozzTopBarLogo
import com.grozzbear.ui.theme.GrozzBorder
import com.grozzbear.ui.theme.GrozzError
import com.grozzbear.ui.theme.GrozzMuted
import com.grozzbear.ui.theme.GrozzOnBackground
import com.grozzbear.ui.theme.GrozzOnPrimary
import com.grozzbear.ui.theme.GrozzRadiusChip
import com.grozzbear.ui.theme.GrozzRadiusPanel
import com.grozzbear.ui.theme.GrozzSurface
import com.grozzbear.ui.theme.GrozzSystemBar
import com.grozzbear.ui.theme.GrozzTextSecondary
import com.grozzbear.ui.theme.GrozzYellow
import com.grozzbear.ui.theme.Lexend
import com.grozzbear.ui.theme.Oswald
import data.local.entity.FoodEntry
import data.local.entity.FoodTemplate
import data.local.entity.GROZZ_FOOD_TEMPLATES
import data.local.entity.MealSlot
import data.local.entity.NutritionGoal
import data.local.entity.WaterCupType
import data.local.util.waterCupsFor
import data.local.viewmodel.MealUiState
import data.local.viewmodel.MealViewModel
import ui.mainpages.navigation.NavigationBar
import ui.mainpages.navigation.Screens
import ui.mainpages.navigation.navigateToLoginAfterLogout
import viewmodel.AuthViewModel

private val MacroCarbs = Color(0xFF5DADE2)
private val MacroFat = Color(0xFFFF8A3D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Meal(
    navController: NavController,
    authViewModel: AuthViewModel,
    mealViewModel: MealViewModel,
) {
    var showMenuSheet by remember { mutableStateOf(false) }
    var addingSlot by remember { mutableStateOf<MealSlot?>(null) }
    var showWaterTargetSheet by remember { mutableStateOf(false) }
    var showCupTypeSheet by remember { mutableStateOf(false) }
    val menuSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val addSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val waterTargetSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val cupTypeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val state by mealViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        mealViewModel.refreshProfile()
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            MealTopBar(onMenuClick = { showMenuSheet = true })
        },
        containerColor = GrozzSystemBar,
        bottomBar = {
            NavigationBar(
                navController = navController,
                indexs = 3,
                flag = false,
                flag2 = false,
                flag3 = false,
                flag4 = true,
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = GrozzYellow)
            }
        } else {
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item {
                    MealHeader(dateLabel = state.dateLabel)
                }
                if (state.profileIncomplete) {
                    item {
                        ProfileIncompleteBanner(
                            onClick = {
                                navController.navigate(Screens.PersonalInformationsScreen.route)
                            },
                        )
                    }
                }
                item {
                    GoalSelector(
                        selected = state.goal,
                        onSelect = mealViewModel::setGoal,
                    )
                }
                item {
                    DailyTargetsCard(state = state)
                }
                item {
                    TrainingNoteCard(
                        title = state.trainingTitle,
                        note = state.trainingNote,
                    )
                }
                item {
                    WaterCard(
                        current = state.waterGlasses,
                        target = state.targets.waterGlasses,
                        cupType = state.waterCupType,
                        targetMl = state.targets.waterMl,
                        onSelect = { index ->
                            val next =
                                if (index == state.waterGlasses && state.waterGlasses > 0) {
                                    state.waterGlasses - 1
                                } else {
                                    index
                                }
                            mealViewModel.setWater(next)
                        },
                        onChangeCupType = { showCupTypeSheet = true },
                        hasCustomTarget = state.hasCustomWaterTarget,
                        onChangeTarget = { showWaterTargetSheet = true },
                    )
                }
                item {
                    Text(
                        text = "Today’s meals",
                        color = GrozzOnBackground,
                        fontFamily = Oswald,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                    )
                }
                MealSlot.entries.forEach { slot ->
                    item(key = slot.id) {
                        MealSlotCard(
                            slot = slot,
                            entries = state.entries.filter { it.slot == slot.id },
                            onAdd = { addingSlot = slot },
                            onRemove = mealViewModel::removeFood,
                        )
                    }
                }
                item {
                    Text(
                        text = "Beta — meal plans, barcode scan, and AI logging come later.",
                        color = GrozzMuted,
                        fontFamily = Lexend,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
                    )
                }
            }
        }

        if (showMenuSheet) {
            ModalBottomSheet(
                onDismissRequest = { showMenuSheet = false },
                sheetState = menuSheetState,
                containerColor = GrozzSurface,
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 40.dp),
                ) {
                    Text(
                        text = "Menu",
                        style = MaterialTheme.typography.titleLarge,
                        color = GrozzOnBackground,
                        modifier = Modifier.padding(horizontal = 24.dp),
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    MenuItemRow(
                        iconRes = R.drawable.accountcircle,
                        text = "View Profile",
                        onClick = {
                            showMenuSheet = false
                            navController.navigate(Screens.Home.Profile.route)
                        },
                    )
                    MenuItemRow(
                        iconRes = R.drawable.settings,
                        text = "Settings",
                        onClick = {
                            showMenuSheet = false
                            navController.navigate(Screens.HomesSettings.route)
                        },
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 0.5.dp,
                        color = GrozzBorder,
                    )
                    MenuItemRow(
                        iconRes = R.drawable.logouticon128,
                        text = "Log Out",
                        textColor = GrozzError,
                        onClick = {
                            showMenuSheet = false
                            authViewModel.logout()
                            navController.navigateToLoginAfterLogout()
                        },
                    )
                }
            }
        }

        addingSlot?.let { slot ->
            ModalBottomSheet(
                onDismissRequest = { addingSlot = null },
                sheetState = addSheetState,
                containerColor = GrozzSurface,
            ) {
                AddFoodSheet(
                    slot = slot,
                    onAddCustom = { name, calories, protein, carbs, fat ->
                        mealViewModel.addFood(slot, name, calories, protein, carbs, fat)
                        addingSlot = null
                    },
                    onAddTemplate = { template ->
                        mealViewModel.addTemplate(slot, template)
                        addingSlot = null
                    },
                )
            }
        }

        if (showWaterTargetSheet) {
            ModalBottomSheet(
                onDismissRequest = { showWaterTargetSheet = false },
                sheetState = waterTargetSheetState,
                containerColor = GrozzSurface,
            ) {
                WaterTargetSheet(
                    currentTargetMl = state.targets.waterMl,
                    suggestedMl = state.suggestedWaterMl,
                    cupType = state.waterCupType,
                    hasCustomTarget = state.hasCustomWaterTarget,
                    onSave = { milliliters ->
                        mealViewModel.setCustomWaterTarget(milliliters)
                        showWaterTargetSheet = false
                    },
                    onUseSuggested = {
                        mealViewModel.clearCustomWaterTarget()
                        showWaterTargetSheet = false
                    },
                )
            }
        }

        if (showCupTypeSheet) {
            ModalBottomSheet(
                onDismissRequest = { showCupTypeSheet = false },
                sheetState = cupTypeSheetState,
                containerColor = GrozzSurface,
            ) {
                WaterCupSheet(
                    selected = state.waterCupType,
                    customCups = state.customCups,
                    onSelect = { cupType ->
                        mealViewModel.setCupType(cupType)
                        showCupTypeSheet = false
                    },
                    onAddCustom = mealViewModel::addCustomCup,
                    onRemoveCustom = mealViewModel::removeCustomCup,
                )
            }
        }
    }
}

@Composable
private fun MealHeader(dateLabel: String) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Fuel ",
                color = GrozzOnBackground,
                fontFamily = Oswald,
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
            )
            Text(
                text = "today",
                color = GrozzYellow,
                fontFamily = Oswald,
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = dateLabel,
            color = GrozzTextSecondary,
            fontFamily = Lexend,
            fontSize = 13.sp,
        )
    }
}

@Composable
private fun ProfileIncompleteBanner(onClick: () -> Unit) {
    GrozzPanel(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Personal targets need your stats",
                color = GrozzOnBackground,
                fontFamily = Lexend,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Add height and weight in Profile to replace the default calorie estimate.",
                color = GrozzMuted,
                fontFamily = Lexend,
                fontSize = 13.sp,
            )
            Spacer(modifier = Modifier.height(12.dp))
            GrozzPrimaryButton(
                text = "Add details",
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun GoalSelector(selected: NutritionGoal, onSelect: (NutritionGoal) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        NutritionGoal.entries.forEach { goal ->
            val isSelected = goal == selected
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(GrozzRadiusChip))
                        .background(if (isSelected) GrozzYellow else GrozzSurface)
                        .border(
                            1.dp,
                            if (isSelected) GrozzYellow else GrozzBorder,
                            RoundedCornerShape(GrozzRadiusChip),
                        ).clickable { onSelect(goal) }
                        .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = goal.label,
                    color = if (isSelected) GrozzOnPrimary else GrozzOnBackground,
                    fontFamily = Lexend,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = goal.hint,
                    color = if (isSelected) GrozzOnPrimary.copy(alpha = 0.7f) else GrozzMuted,
                    fontFamily = Lexend,
                    fontSize = 10.sp,
                )
            }
        }
    }
}

@Composable
private fun DailyTargetsCard(state: MealUiState) {
    val remaining = state.targets.calories - state.eatenCalories
    val progress =
        if (state.targets.calories == 0) {
            0f
        } else {
            (state.eatenCalories.toFloat() / state.targets.calories).coerceAtLeast(0f)
        }
    GrozzPanel(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(108.dp)) {
                    CircularProgressIndicator(
                        progress = { progress.coerceAtMost(1f) },
                        modifier = Modifier.size(108.dp),
                        color = if (remaining < 0) GrozzError else GrozzYellow,
                        trackColor = GrozzBorder,
                        strokeWidth = 8.dp,
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = remaining.absoluteCalories(),
                            color = GrozzOnBackground,
                            fontFamily = Oswald,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                        )
                        Text(
                            text = if (remaining >= 0) "kcal left" else "kcal over",
                            color = GrozzMuted,
                            fontFamily = Lexend,
                            fontSize = 11.sp,
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    MacroRow("Protein", state.eatenProtein, state.targets.proteinG, GrozzYellow, "g")
                    MacroRow("Carbs", state.eatenCarbs, state.targets.carbsG, MacroCarbs, "g")
                    MacroRow("Fat", state.eatenFat, state.targets.fatG, MacroFat, "g")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "${state.eatenCalories} / ${state.targets.calories} kcal",
                color = GrozzTextSecondary,
                fontFamily = Lexend,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun MacroRow(label: String, eaten: Int, target: Int, color: Color, unit: String) {
    val ratio = if (target == 0) 0f else (eaten.toFloat() / target).coerceAtLeast(0f)
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = label, color = GrozzOnBackground, fontFamily = Lexend, fontSize = 12.sp)
            Text(
                text = "$eaten / $target$unit",
                color = if (eaten > target) GrozzError else GrozzMuted,
                fontFamily = Lexend,
                fontSize = 12.sp,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { ratio.coerceAtMost(1f) },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(4.dp)),
            color = if (eaten > target) GrozzError else color,
            trackColor = GrozzBorder,
        )
    }
}

@Composable
private fun TrainingNoteCard(title: String, note: String) {
    GrozzPanel(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "AROUND TODAY’S WORKOUT",
                color = GrozzYellow,
                fontFamily = Lexend,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                color = GrozzOnBackground,
                fontFamily = Oswald,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = note,
                color = GrozzTextSecondary,
                fontFamily = Lexend,
                fontSize = 13.sp,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WaterCard(
    current: Int,
    target: Int,
    cupType: WaterCupType,
    targetMl: Int,
    onSelect: (Int) -> Unit,
    onChangeCupType: () -> Unit,
    hasCustomTarget: Boolean,
    onChangeTarget: () -> Unit,
) {
    val currentMl = current * cupType.ml
    GrozzPanel(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Water",
                        color = GrozzOnBackground,
                        fontFamily = Oswald,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                    )
                    if (hasCustomTarget) {
                        Text(
                            text = "Custom target",
                            color = GrozzYellow,
                            fontFamily = Lexend,
                            fontSize = 11.sp,
                        )
                    }
                }
                Text(
                    text = "Change target",
                    color = GrozzYellow,
                    fontFamily = Lexend,
                    fontSize = 12.sp,
                    modifier =
                        Modifier
                            .clip(RoundedCornerShape(GrozzRadiusChip))
                            .border(1.dp, GrozzYellow.copy(alpha = 0.45f), RoundedCornerShape(GrozzRadiusChip))
                            .clickable(onClick = onChangeTarget)
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                WaterCupSelector(
                    selected = cupType,
                    onClick = onChangeCupType,
                )
                Column(horizontalAlignment = Alignment.End) {
                    if (cupType.ml < 1000) {
                        Text(
                            text = "$current / $target ${cupType.unitPlural}",
                            color = GrozzMuted,
                            fontFamily = Lexend,
                            fontSize = 12.sp,
                        )
                        Text(
                            text = "$currentMl / $targetMl ml",
                            color = GrozzMuted.copy(alpha = 0.8f),
                            fontFamily = Lexend,
                            fontSize = 11.sp,
                        )
                    } else {
                        Text(
                            text = "$currentMl / $targetMl ml",
                            color = GrozzMuted.copy(alpha = 0.8f),
                            fontFamily = Lexend,
                            fontSize = 11.sp,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                repeat(target) { index ->
                    val filled = index < current
                    Box(
                        modifier =
                            Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(if (filled) GrozzYellow else Color.Transparent)
                                .border(1.dp, if (filled) GrozzYellow else GrozzBorder, CircleShape)
                                .clickable { onSelect(index + 1) },
                    )
                }
            }
        }
    }
}

@Composable
private fun WaterCupSelector(selected: WaterCupType, onClick: () -> Unit) {
    Row(
        modifier =
            Modifier
                .clip(RoundedCornerShape(GrozzRadiusChip))
                .border(1.dp, GrozzYellow.copy(alpha = 0.45f), RoundedCornerShape(GrozzRadiusChip))
                .clickable(onClick = onClick)
                .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "${selected.label} · ${selected.sizeLabel}",
            color = GrozzYellow,
            fontFamily = Lexend,
            fontSize = 12.sp,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "Select cup size",
            tint = GrozzYellow,
            modifier = Modifier.size(16.dp),
        )
    }
}

private val WaterTargetPresets = listOf(1500, 2000, 2500, 3000, 3500, 4000)
private val CustomCupNameSuggestions = listOf("Cup", "Glass", "Mug", "Bottle", "Shaker")

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WaterCupSheet(
    selected: WaterCupType,
    customCups: List<WaterCupType>,
    onSelect: (WaterCupType) -> Unit,
    onAddCustom: (WaterCupType) -> Unit,
    onRemoveCustom: (String) -> Unit,
) {
    var draft by remember { mutableStateOf(selected) }
    var extraCustom by remember { mutableStateOf<List<WaterCupType>>(emptyList()) }
    var customName by remember { mutableStateOf("") }
    var customMl by remember { mutableStateOf("") }
    val shownCustom =
        remember(customCups, extraCustom) {
            (customCups + extraCustom).distinctBy { it.id }
        }
    val parsedMl = customMl.toIntOrNull()
    val canAdd =
        parsedMl != null &&
            parsedMl in WaterCupType.MIN_CUSTOM_ML..WaterCupType.MAX_CUSTOM_ML &&
            shownCustom.size < 12

    LaunchedEffect(customCups) {
        extraCustom = extraCustom.filter { extra -> customCups.none { it.id == extra.id } }
    }

    fun addCustom() {
        val milliliters = parsedMl ?: return
        val name = customName.trim().ifBlank { "Cup" }
        val existing =
            (WaterCupType.PRESETS + shownCustom).firstOrNull {
                it.ml == milliliters && it.label.equals(name, ignoreCase = true)
            }
        if (existing != null) {
            draft = existing
        } else {
            val cup = WaterCupType.createCustom(name, milliliters)
            extraCustom = extraCustom + cup
            onAddCustom(cup)
            draft = cup
        }
        customName = ""
        customMl = ""
    }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Cup size",
            color = GrozzOnBackground,
            fontFamily = Oswald,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Choose how you log water, or add your own size.",
            color = GrozzMuted,
            fontFamily = Lexend,
            fontSize = 13.sp,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "${draft.label} · ${draft.sizeLabel}",
            color = GrozzYellow,
            fontFamily = Oswald,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
        )
        Text(
            text = "${draft.ml} ml per pour",
            color = GrozzMuted,
            fontFamily = Lexend,
            fontSize = 13.sp,
        )
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = "Presets",
            color = GrozzOnBackground,
            fontFamily = Lexend,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            WaterCupType.PRESETS.forEach { type ->
                WaterTargetChip(
                    label = "${type.label} · ${type.sizeLabel}",
                    selected = draft.id == type.id,
                    onClick = { draft = type },
                )
            }
        }
        if (shownCustom.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Your cups",
                color = GrozzOnBackground,
                fontFamily = Lexend,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                shownCustom.forEach { cup ->
                    CustomCupChip(
                        cup = cup,
                        selected = draft.id == cup.id,
                        onClick = { draft = cup },
                        onRemove = {
                            extraCustom = extraCustom.filterNot { it.id == cup.id }
                            onRemoveCustom(cup.id)
                            if (draft.id == cup.id) draft = WaterCupType.GLASS
                        },
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = "Add custom cup",
            color = GrozzOnBackground,
            fontFamily = Lexend,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CustomCupNameSuggestions.forEach { name ->
                WaterTargetChip(
                    label = name,
                    selected = customName.equals(name, ignoreCase = true),
                    onClick = { customName = name },
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        MealField(
            value = customName,
            onValueChange = { if (it.length <= 20) customName = it },
            placeholder = "Name  e.g. Mug",
        )
        Spacer(modifier = Modifier.height(10.dp))
        MealField(
            value = customMl,
            onValueChange = { customMl = it.filter(Char::isDigit).take(4) },
            placeholder = "Size ml  e.g. 350",
            keyboardType = KeyboardType.Number,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text =
                if (canAdd || customMl.isBlank()) {
                    "Any size between ${WaterCupType.MIN_CUSTOM_ML} and ${WaterCupType.MAX_CUSTOM_ML} ml."
                } else {
                    "Enter a value between ${WaterCupType.MIN_CUSTOM_ML} and ${WaterCupType.MAX_CUSTOM_ML} ml."
                },
            color = if (canAdd || customMl.isBlank()) GrozzMuted else GrozzError,
            fontFamily = Lexend,
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(14.dp))
        GrozzPrimaryButton(
            text = "Add cup",
            onClick = { addCustom() },
            enabled = canAdd,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(12.dp))
        GrozzPrimaryButton(
            text = "Use this cup",
            onClick = { onSelect(draft) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun CustomCupChip(
    cup: WaterCupType,
    selected: Boolean,
    onClick: () -> Unit,
    onRemove: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .clip(RoundedCornerShape(GrozzRadiusChip))
                .background(if (selected) GrozzYellow else Color.Transparent)
                .border(
                    1.dp,
                    GrozzYellow.copy(alpha = if (selected) 1f else 0.45f),
                    RoundedCornerShape(GrozzRadiusChip),
                ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "${cup.label} · ${cup.sizeLabel}",
            color = if (selected) GrozzOnPrimary else GrozzYellow,
            fontFamily = Lexend,
            fontSize = 12.sp,
            modifier =
                Modifier
                    .clickable(onClick = onClick)
                    .padding(start = 10.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
        )
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Remove ${cup.label}",
            tint = if (selected) GrozzOnPrimary else GrozzMuted,
            modifier =
                Modifier
                    .size(28.dp)
                    .clickable(onClick = onRemove)
                    .padding(6.dp),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WaterTargetSheet(
    currentTargetMl: Int,
    suggestedMl: Int,
    cupType: WaterCupType,
    hasCustomTarget: Boolean,
    onSave: (Int) -> Unit,
    onUseSuggested: () -> Unit,
) {
    var draftMl by remember { mutableIntStateOf(currentTargetMl.coerceIn(500, 6000)) }
    var draftText by remember { mutableStateOf(draftMl.toString()) }
    val parsedDraft = draftText.toIntOrNull()
    val previewMl = parsedDraft ?: draftMl
    val cups = waterCupsFor(previewMl.coerceAtLeast(1), cupType.ml)
    val step = cupType.ml.coerceAtLeast(100)
    val canSave = parsedDraft != null && parsedDraft in 500..6000

    fun setDraft(milliliters: Int) {
        val next = milliliters.coerceIn(500, 6000)
        draftMl = next
        draftText = next.toString()
    }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(bottom = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Water target",
            color = GrozzOnBackground,
            fontFamily = Oswald,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Suggested from your profile: $suggestedMl ml",
            color = GrozzMuted,
            fontFamily = Lexend,
            fontSize = 13.sp,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "$previewMl ml",
            color = GrozzYellow,
            fontFamily = Oswald,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
        )
        Text(
            text = String.format("%.1f L · %d %s", previewMl / 1000f, cups, cupType.unitPlural),
            color = GrozzMuted,
            fontFamily = Lexend,
            fontSize = 13.sp,
        )
        Spacer(modifier = Modifier.height(18.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            WaterStepButton(
                label = "−",
                enabled = draftMl > 500,
                onClick = { setDraft(draftMl - step) },
            )
            WaterStepButton(
                label = "+",
                enabled = draftMl < 6000,
                onClick = { setDraft(draftMl + step) },
            )
        }
        Spacer(modifier = Modifier.height(18.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            WaterTargetPresets.forEach { preset ->
                WaterTargetChip(
                    label = "$preset ml",
                    selected = previewMl == preset,
                    onClick = { setDraft(preset) },
                )
            }
            WaterTargetChip(
                label = "Suggested",
                selected = previewMl == suggestedMl,
                onClick = { setDraft(suggestedMl) },
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        MealField(
            value = draftText,
            onValueChange = { raw ->
                val digits = raw.filter(Char::isDigit).take(4)
                draftText = digits
                digits.toIntOrNull()?.let { draftMl = it }
            },
            placeholder = "Custom ml  e.g. 2345",
            keyboardType = KeyboardType.Number,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if (canSave) "Type any amount between 500 and 6000 ml." else "Enter a value between 500 and 6000 ml.",
            color = if (canSave) GrozzMuted else GrozzError,
            fontFamily = Lexend,
            fontSize = 12.sp,
        )
        Spacer(modifier = Modifier.height(22.dp))
        GrozzPrimaryButton(
            text = "Save target",
            onClick = { parsedDraft?.let(onSave) },
            enabled = canSave,
            modifier = Modifier.fillMaxWidth(),
        )
        if (hasCustomTarget) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Use suggested target",
                color = GrozzYellow,
                fontFamily = Lexend,
                fontSize = 13.sp,
                modifier =
                    Modifier
                        .clickable(onClick = onUseSuggested)
                        .padding(8.dp),
            )
        }
    }
}

@Composable
private fun WaterTargetChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = label,
        color = if (selected) GrozzOnPrimary else GrozzYellow,
        fontFamily = Lexend,
        fontSize = 12.sp,
        modifier =
            Modifier
                .clip(RoundedCornerShape(GrozzRadiusChip))
                .background(if (selected) GrozzYellow else Color.Transparent)
                .border(
                    1.dp,
                    GrozzYellow.copy(alpha = if (selected) 1f else 0.45f),
                    RoundedCornerShape(GrozzRadiusChip),
                ).clickable(onClick = onClick)
                .padding(horizontal = 10.dp, vertical = 8.dp),
    )
}

@Composable
private fun WaterStepButton(label: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier =
            Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (enabled) GrozzYellow else GrozzMuted.copy(alpha = 0.35f))
                .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (enabled) GrozzOnPrimary else GrozzOnBackground.copy(alpha = 0.5f),
            fontFamily = Oswald,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
        )
    }
}

@Composable
private fun MealSlotCard(
    slot: MealSlot,
    entries: List<FoodEntry>,
    onAdd: () -> Unit,
    onRemove: (String) -> Unit,
) {
    GrozzPanel(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = slot.label,
                    color = GrozzOnBackground,
                    fontFamily = Oswald,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                )
                Text(
                    text =
                        if (entries.isEmpty()) {
                            "Nothing logged"
                        } else {
                            "${entries.sumOf { it.calories }} kcal"
                        },
                    color = GrozzMuted,
                    fontFamily = Lexend,
                    fontSize = 12.sp,
                )
            }
            IconButton(onClick = onAdd) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add ${slot.label}",
                    tint = GrozzYellow,
                )
            }
        }
        if (entries.isNotEmpty()) {
            HorizontalDivider(thickness = 0.5.dp, color = GrozzBorder)
            entries.forEach { entry ->
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = entry.name,
                            color = GrozzOnBackground,
                            fontFamily = Lexend,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = "${entry.calories} kcal · P ${entry.protein} · C ${entry.carbs} · F ${entry.fat}",
                            color = GrozzMuted,
                            fontFamily = Lexend,
                            fontSize = 11.sp,
                        )
                    }
                    IconButton(onClick = { onRemove(entry.id) }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove ${entry.name}",
                            tint = GrozzMuted,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddFoodSheet(
    slot: MealSlot,
    onAddCustom: (String, Int, Int, Int, Int) -> Unit,
    onAddTemplate: (FoodTemplate) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    val trimmedQuery = query.trim()
    val matches =
        remember(trimmedQuery) {
            if (trimmedQuery.isBlank()) {
                GROZZ_FOOD_TEMPLATES.filter { it.popular }
            } else {
                GROZZ_FOOD_TEMPLATES.filter { template ->
                    template.name.contains(trimmedQuery, ignoreCase = true)
                }
            }
        }
    val searching = trimmedQuery.isNotBlank()

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 28.dp),
    ) {
        Text(
            text = "Add to ${slot.label.lowercase()}",
            color = GrozzOnBackground,
            fontFamily = Oswald,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Search the list, tap a chip, or log your own.",
            color = GrozzMuted,
            fontFamily = Lexend,
            fontSize = 13.sp,
        )
        Spacer(modifier = Modifier.height(16.dp))
        MealField(
            value = query,
            onValueChange = { if (it.length <= 40) query = it },
            placeholder = "Search foods",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = GrozzMuted,
                    modifier = Modifier.size(20.dp),
                )
            },
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = if (searching) "Results" else "Quick add",
            color = GrozzOnBackground,
            fontFamily = Lexend,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (matches.isEmpty()) {
            Text(
                text = "No match. Log it as custom food below.",
                color = GrozzMuted,
                fontFamily = Lexend,
                fontSize = 13.sp,
            )
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                matches.take(12).forEach { template ->
                    Text(
                        text = template.name,
                        color = GrozzYellow,
                        fontFamily = Lexend,
                        fontSize = 12.sp,
                        modifier =
                            Modifier
                                .clip(RoundedCornerShape(GrozzRadiusChip))
                                .border(1.dp, GrozzYellow.copy(alpha = 0.45f), RoundedCornerShape(GrozzRadiusChip))
                                .clickable { onAddTemplate(template) }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                    )
                }
            }
            if (searching) {
                Spacer(modifier = Modifier.height(10.dp))
                matches.forEach { template ->
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(GrozzRadiusChip))
                                .clickable { onAddTemplate(template) }
                                .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = template.name,
                                color = GrozzOnBackground,
                                fontFamily = Lexend,
                                fontSize = 14.sp,
                            )
                            Text(
                                text = "${template.calories} kcal · P ${template.protein} · C ${template.carbs} · F ${template.fat}",
                                color = GrozzMuted,
                                fontFamily = Lexend,
                                fontSize = 11.sp,
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add ${template.name}",
                            tint = GrozzYellow,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Custom food",
            color = GrozzOnBackground,
            fontFamily = Lexend,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
        )
        Spacer(modifier = Modifier.height(8.dp))
        MealField(value = name, onValueChange = { if (it.length <= 40) name = it }, placeholder = "Food name")
        Spacer(modifier = Modifier.height(10.dp))
        MealField(
            value = calories,
            onValueChange = { calories = it.filter(Char::isDigit).take(5) },
            placeholder = "Calories",
            keyboardType = KeyboardType.Number,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                MealField(
                    value = protein,
                    onValueChange = { protein = it.filter(Char::isDigit).take(3) },
                    placeholder = "Protein",
                    keyboardType = KeyboardType.Number,
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                MealField(
                    value = carbs,
                    onValueChange = { carbs = it.filter(Char::isDigit).take(3) },
                    placeholder = "Carbs",
                    keyboardType = KeyboardType.Number,
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                MealField(
                    value = fat,
                    onValueChange = { fat = it.filter(Char::isDigit).take(3) },
                    placeholder = "Fat",
                    keyboardType = KeyboardType.Number,
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        GrozzPrimaryButton(
            text = "Add food",
            onClick = {
                onAddCustom(
                    name,
                    calories.toIntOrNull() ?: 0,
                    protein.toIntOrNull() ?: 0,
                    carbs.toIntOrNull() ?: 0,
                    fat.toIntOrNull() ?: 0,
                )
            },
            enabled = name.isNotBlank() && (calories.toIntOrNull() ?: 0) > 0,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun MealField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(text = placeholder, color = GrozzMuted, fontFamily = Lexend)
        },
        leadingIcon = leadingIcon,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(GrozzRadiusChip),
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedContainerColor = GrozzSystemBar,
                unfocusedContainerColor = GrozzSystemBar,
                focusedBorderColor = GrozzMuted,
                unfocusedBorderColor = GrozzBorder,
                cursorColor = GrozzYellow,
                focusedTextColor = GrozzOnBackground,
                unfocusedTextColor = GrozzOnBackground,
            ),
    )
}

@Composable
private fun MealTopBar(onMenuClick: () -> Unit) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onMenuClick) {
            Icon(
                painter = painterResource(R.drawable.accountcircle),
                contentDescription = "Menu",
                modifier = Modifier.size(26.dp),
                tint = GrozzOnBackground,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        GrozzTopBarLogo()
        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(48.dp))
    }
}

private fun Int.absoluteCalories(): String = kotlin.math.abs(this).toString()
