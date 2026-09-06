package activity.inside

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.grozzbear.R
import com.grozzbear.ui.components.GrozzPrimaryButton
import com.grozzbear.ui.components.IntPickerColumn
import com.grozzbear.ui.components.WEIGHT_FRACTION_OPTIONS
import com.grozzbear.ui.components.WEIGHT_WHOLE_KG_RANGE
import com.grozzbear.ui.components.combineWeightKg
import com.grozzbear.ui.components.formatWeightKg
import com.grozzbear.ui.components.splitWeightKg
import com.grozzbear.ui.theme.GrozzBorder
import com.grozzbear.ui.theme.GrozzError
import com.grozzbear.ui.theme.GrozzMuted
import com.grozzbear.ui.theme.GrozzOnBackground
import com.grozzbear.ui.theme.GrozzOnPrimary
import com.grozzbear.ui.theme.GrozzRadiusChip
import com.grozzbear.ui.theme.GrozzSurface
import com.grozzbear.ui.theme.GrozzSystemBar
import com.grozzbear.ui.theme.GrozzTextSecondary
import com.grozzbear.ui.theme.GrozzYellow
import com.grozzbear.ui.theme.Lexend
import com.grozzbear.ui.theme.Oswald
import com.grozzbear.ui.util.counted
import data.local.viewmodel.ChooseExercisesViewModel
import data.local.viewmodel.CreateWorkoutViewModel
import data.local.viewmodel.ExerciseDraft
import java.util.UUID
import kotlinx.coroutines.launch
import ui.mainpages.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun CreateWorkout(
    navController: NavController,
    createWorkoutViewModel: CreateWorkoutViewModel,
    chooseExercisesViewModel: ChooseExercisesViewModel,
) {
    val draft by createWorkoutViewModel.draftExercises.collectAsState()
    val workoutNameInput by chooseExercisesViewModel.workoutName.collectAsState()
    val modalBottomSheetState = rememberModalBottomSheetState()
    var expandBottomSheet by remember { mutableStateOf(false) }

    var editingCatalogId by remember { mutableStateOf("") }
    var editingSetIndex by remember { mutableStateOf(0) }
    var tempReps by remember { mutableIntStateOf(0) }
    // Whole kg + fraction index (not a raw float) — see WEIGHT_FRACTION_OPTIONS.
    var tempWholeKg by remember { mutableIntStateOf(0) }
    var tempFractionIndex by remember { mutableIntStateOf(0) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentUser = Firebase.auth.currentUser
    val canSave =
        currentUser != null && workoutNameInput.isNotBlank() && draft.isNotEmpty()

    fun saveWorkout() {
        val user = currentUser
        if (user == null || workoutNameInput.isBlank() || draft.isEmpty()) {
            Toast
                .makeText(
                    context,
                    "Please enter a name and add exercises.",
                    Toast.LENGTH_SHORT,
                ).show()
            return
        }
        scope.launch {
            createWorkoutViewModel.saveWorkout(
                workoutId = UUID.randomUUID().toString(),
                workoutName = workoutNameInput.trim(),
                workoutType = "User",
                workoutRating = 0,
                ownerUid = user.uid,
                syncState = true,
                // Stable drawable for this build; prefer a name/key long-term.
                image = R.drawable.infohorizontalscreensecondphoto,
                onDone = {
                    Toast.makeText(context, "Workout saved!", Toast.LENGTH_SHORT).show()
                    chooseExercisesViewModel.setName("")
                    navController.navigate(Screens.Activity.route) {
                        popUpTo(Screens.Activity.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onError = { e ->
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                },
            )
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CreateWorkoutTopBar(
                onBack = {
                    chooseExercisesViewModel.setName("")
                    navController.navigate(Screens.Activity.route) {
                        popUpTo(Screens.Activity.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        },
        containerColor = GrozzSystemBar,
        bottomBar = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(GrozzSystemBar)
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .padding(bottom = 8.dp),
            ) {
                GrozzPrimaryButton(
                    text = "Save workout",
                    onClick = { saveWorkout() },
                    enabled = canSave,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    HorizontalDivider(
                        thickness = 2.dp,
                        color = GrozzYellow,
                        modifier = Modifier.width(28.dp),
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "CREATE",
                            fontFamily = Oswald,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = GrozzOnBackground,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "WORKOUT",
                            fontFamily = Oswald,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = GrozzYellow,
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text =
                            if (draft.isEmpty()) {
                                "Name your plan, then add exercises."
                            } else {
                                counted(draft.size, "exercise") + " ready"
                            },
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrozzTextSecondary,
                    )
                }
            }

            item {
                Text(
                    text = "Workout name",
                    style = MaterialTheme.typography.labelLarge,
                    color = GrozzTextSecondary,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = workoutNameInput,
                    onValueChange = { chooseExercisesViewModel.setName(it) },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                    placeholder = {
                        Text(
                            "e.g. Monday Leg Day",
                            color = GrozzMuted,
                            fontFamily = Lexend,
                        )
                    },
                    textStyle =
                        TextStyle(
                            color = GrozzOnBackground,
                            fontSize = 16.sp,
                            fontFamily = Lexend,
                        ),
                    singleLine = true,
                    shape = RoundedCornerShape(GrozzRadiusChip),
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = GrozzSurface,
                            unfocusedContainerColor = GrozzSurface,
                            focusedBorderColor = GrozzMuted,
                            unfocusedBorderColor = GrozzBorder,
                            cursorColor = GrozzYellow,
                        ),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                chooseExercisesViewModel.setName(randomWorkoutName())
                            },
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.casinoicon128),
                                contentDescription = "Random name",
                                tint = GrozzOnBackground,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    },
                )
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(
                    onClick = { navController.navigate(Screens.ChooseExercises.createRoute()) },
                    modifier = Modifier.padding(horizontal = 8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = GrozzYellow,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Add exercises",
                        color = GrozzYellow,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (draft.isEmpty()) {
                item {
                    EmptyExercisesPlaceholder(
                        onAddClick = { navController.navigate(Screens.ChooseExercises.createRoute()) },
                    )
                }
            } else {
                itemsIndexed(
                    items = draft,
                    key = { _, item -> item.catalogId },
                ) { index, item ->
                    Box(
                        modifier =
                            Modifier
                                .animateItem()
                                .padding(horizontal = 20.dp, vertical = 6.dp),
                    ) {
                        ExerciseExpandableCardChooseExercises(
                            onEditClick = { setIndex, weight, reps ->
                                editingCatalogId = item.catalogId
                                editingSetIndex = setIndex
                                val (whole, fractionIndex) = splitWeightKg(weight)
                                tempWholeKg = whole
                                tempFractionIndex = fractionIndex
                                tempReps = reps
                                expandBottomSheet = true
                            },
                            exerciseDraft = item,
                            createWorkoutViewModel = createWorkoutViewModel,
                            catalogId = item.catalogId,
                            canMoveUp = index > 0,
                            canMoveDown = index < draft.lastIndex,
                            onMoveUp = {
                                createWorkoutViewModel.moveDraftExercise(index, index - 1)
                            },
                            onMoveDown = {
                                createWorkoutViewModel.moveDraftExercise(index, index + 1)
                            },
                            onRemove = {
                                createWorkoutViewModel.removeDraftExercise(item.catalogId)
                            },
                        )
                    }
                }
            }
        }

        if (expandBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { expandBottomSheet = false },
                sheetState = modalBottomSheetState,
                containerColor = GrozzSurface,
            ) {
                var currentRepsPicker by remember(tempReps) { mutableIntStateOf(tempReps) }
                var currentWholeKg by remember(tempWholeKg) { mutableIntStateOf(tempWholeKg) }
                var currentFractionIndex by remember(tempFractionIndex) {
                    mutableIntStateOf(tempFractionIndex)
                }
                val previewWeight = combineWeightKg(currentWholeKg, currentFractionIndex)

                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                            .padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Edit set",
                        style = MaterialTheme.typography.titleLarge,
                        color = GrozzOnBackground,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${formatWeightKg(previewWeight)} KG",
                        color = GrozzYellow,
                        fontSize = 16.sp,
                        fontFamily = Lexend,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        // Wheel 1: whole kilograms (Int)
                        IntPickerColumn(
                            label = "KG",
                            value = currentWholeKg,
                            range = WEIGHT_WHOLE_KG_RANGE,
                            onValueChange = { currentWholeKg = it },
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Wheel 2: fraction index → WEIGHT_FRACTION_OPTIONS[index]
                        IntPickerColumn(
                            label = "+",
                            value = currentFractionIndex,
                            range = WEIGHT_FRACTION_OPTIONS.indices,
                            labelForValue = { index ->
                                val f = WEIGHT_FRACTION_OPTIONS[index]
                                if (f == 0f) "0" else f.toString()
                            },
                            onValueChange = { currentFractionIndex = it },
                        )
                        Spacer(modifier = Modifier.width(32.dp))
                        IntPickerColumn(
                            label = "REPS",
                            value = currentRepsPicker,
                            range = 0..100,
                            onValueChange = { currentRepsPicker = it },
                        )
                    }
                    Spacer(modifier = Modifier.height(28.dp))
                    GrozzPrimaryButton(
                        text = "Update set",
                        onClick = {
                            createWorkoutViewModel.updateSet(
                                editingCatalogId,
                                editingSetIndex,
                                currentRepsPicker,
                                combineWeightKg(currentWholeKg, currentFractionIndex),
                            )
                            expandBottomSheet = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
fun ExerciseExpandableCardChooseExercises(
    onEditClick: (Int, Float, Int) -> Unit,
    exerciseDraft: ExerciseDraft,
    createWorkoutViewModel: CreateWorkoutViewModel,
    catalogId: String,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onRemove: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(GrozzRadiusChip))
                .background(GrozzSurface)
                .clickable { expanded = !expanded }
                .animateContentSize()
                .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(44.dp)
                    .background(GrozzYellow, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painterResource(R.drawable.dumbbellicon128),
                    null,
                    tint = GrozzOnPrimary,
                    modifier = Modifier.size(24.dp),
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exerciseDraft.name,
                    color = GrozzOnBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = Lexend,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = exerciseDraft.bodyPart.uppercase(),
                    color = GrozzTextSecondary,
                    fontSize = 12.sp,
                )
            }

            IconButton(
                onClick = onMoveUp,
                enabled = canMoveUp,
                modifier = Modifier.size(36.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Move up",
                    tint = if (canMoveUp) GrozzYellow else GrozzMuted,
                    modifier = Modifier.size(22.dp),
                )
            }
            IconButton(
                onClick = onMoveDown,
                enabled = canMoveDown,
                modifier = Modifier.size(36.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Move down",
                    tint = if (canMoveDown) GrozzYellow else GrozzMuted,
                    modifier = Modifier.size(22.dp),
                )
            }
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(36.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.terminate),
                    contentDescription = "Remove exercise",
                    tint = GrozzError,
                    modifier = Modifier.size(20.dp),
                )
            }
            Icon(
                painter = painterResource(R.drawable.down),
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = GrozzYellow,
                modifier = Modifier.size(20.dp),
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                ) {
                    Text("SET", color = GrozzMuted, fontSize = 11.sp, modifier = Modifier.weight(1f))
                    Text(
                        "KG",
                        color = GrozzMuted,
                        fontSize = 11.sp,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        "REPS",
                        color = GrozzMuted,
                        fontSize = 11.sp,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        "ACTION",
                        color = GrozzMuted,
                        fontSize = 11.sp,
                        modifier = Modifier.weight(1.5f),
                        textAlign = TextAlign.End,
                    )
                }

                exerciseDraft.sets.forEachIndexed { index, set ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "${index + 1}",
                            color = GrozzOnBackground,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            formatWeightKg(set.weight),
                            color = GrozzOnBackground,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            "${set.reps}",
                            color = GrozzOnBackground,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                        )

                        Row(modifier = Modifier.weight(1.5f), horizontalArrangement = Arrangement.End) {
                            Icon(
                                painter = painterResource(R.drawable.editnote),
                                contentDescription = null,
                                tint = GrozzYellow,
                                modifier =
                                    Modifier
                                        .size(22.dp)
                                        .clickable { onEditClick(index, set.weight, set.reps) },
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(
                                painter = painterResource(R.drawable.minusicon128),
                                contentDescription = null,
                                tint = GrozzError,
                                modifier =
                                    Modifier
                                        .size(22.dp)
                                        .clickable {
                                            createWorkoutViewModel.removeSetToExercise(catalogId, set)
                                        },
                            )
                        }
                    }
                }

                IconButton(
                    onClick = { createWorkoutViewModel.addSetToExercise(catalogId) },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add set",
                        tint = GrozzYellow,
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyExercisesPlaceholder(onAddClick: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 40.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.dumbbell),
            contentDescription = null,
            tint = GrozzMuted,
            modifier = Modifier.size(96.dp),
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No exercises yet",
            style = MaterialTheme.typography.titleLarge,
            color = GrozzOnBackground,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Add exercises from the catalogue to build this plan.",
            style = MaterialTheme.typography.bodyMedium,
            color = GrozzTextSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(20.dp))
        GrozzPrimaryButton(
            text = "Add exercises",
            onClick = onAddClick,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun CreateWorkoutTopBar(onBack: () -> Unit) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                painter = painterResource(R.drawable.left),
                contentDescription = "Back",
                modifier = Modifier.size(24.dp),
                tint = GrozzOnBackground,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(R.drawable.grozzlogo),
            contentDescription = "Grozz",
            modifier = Modifier.size(48.dp),
        )
        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.size(48.dp))
    }
}

private fun randomWorkoutName(): String = listOf(
    "Rock it!",
    "Dumbbell Day!",
    "Power Hour",
    "Leg Legend",
    "Push Mode",
    "Pull Session",
    "Core Crusher",
    "Iron Hour",
    "Sweat Check",
    "Beast Mode",
    "No Excuses",
    "Full Send",
    "Gain Day",
    "Lift Heavy",
    "Upper Fire",
    "Lower Burn",
    "Back Attack",
    "Chest Day",
    "Shoulder Pump",
    "Arm Farm",
    "Glute Focus",
    "Cardio Mix",
    "Strength Stack",
    "Volume Day",
    "PR Hunt",
    "Morning Grind",
    "Night Session",
    "Quick Hit",
    "Max Effort",
    "Stay Solid",
).random()
