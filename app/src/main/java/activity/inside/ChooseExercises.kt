package com.grozzbear.projectfitness.activity.inside

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grozzbear.R
import com.grozzbear.projectfitness.data.local.viewmodel.WorkoutSettingViewModel
import com.grozzbear.ui.theme.GrozzOnBackground
import com.grozzbear.ui.theme.GrozzRadiusChip
import com.grozzbear.ui.theme.GrozzRadiusPanel
import com.grozzbear.ui.theme.GrozzSurface
import com.grozzbear.ui.theme.GrozzSystemBar
import com.grozzbear.ui.theme.GrozzYellow
import com.grozzbear.ui.theme.Lexend
import com.grozzbear.ui.theme.Oswald
import data.local.viewmodel.ActivityInsideViewModel
import data.local.viewmodel.CreateWorkoutViewModel
import data.local.viewmodel.OldWorkoutDetailsViewModel
import kotlinx.coroutines.flow.flowOf
import ui.mainpages.navigation.Screens
import viewmodel.ViewModelSave
import viewmodel.WorkoutinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseExercises(
    navController: NavController,
    viewModelSave: ViewModelSave,
    workoutinViewModel: WorkoutinViewModel,
    createWorkoutViewModel: CreateWorkoutViewModel?,
    workoutSettingViewModel: WorkoutSettingViewModel?,
    oldWorkoutDetailsViewModel: OldWorkoutDetailsViewModel?,
    activityInsideViewModel: ActivityInsideViewModel,
    targetWorkoutId: String?,
) {
    val searchText = rememberSaveable { mutableStateOf("") }
    var selectedMuscleGroup by rememberSaveable { mutableStateOf("All") }
    var selectedEquipment by rememberSaveable { mutableStateOf("All") }
    var expandableExercise by remember { mutableStateOf(false) }
    var expandableEquipment by remember { mutableStateOf(false) }
    val muscleGroups =
        remember {
            listOf(
                "All",
                "Chest",
                "Back",
                "Quads",
                "Biceps",
                "Triceps",
                "Shoulders",
                "Abs",
                "Calves",
                "Abductors",
                "Adductors",
                "Forearms",
                "Glutes",
                "Hamstrings",
                "Traps",
            )
        }
    val equipment =
        remember { listOf("All", "Cable", "Barbell", "Bodyweight", "Dumbbell", "Machine", "Plate") }

    val isEdit = Screens.ChooseExercises.isEditMode(targetWorkoutId)
    val isHistory = Screens.ChooseExercises.isHistoryMode(targetWorkoutId)
    val isAddingToExisting = isEdit || isHistory

    val catalogFromEdit by (workoutSettingViewModel?.catalogExercises ?: flowOf(emptyList()))
        .collectAsState(initial = emptyList())
    val catalogFromCreate by (createWorkoutViewModel?.catalogWorkoutList ?: flowOf(emptyList()))
        .collectAsState(initial = emptyList())
    val catalogFromHistory by (oldWorkoutDetailsViewModel?.catalogExercises ?: flowOf(emptyList()))
        .collectAsState(initial = emptyList())
    val catalogExercisesList =
        when {
            isHistory -> catalogFromHistory
            isEdit -> catalogFromEdit
            else -> catalogFromCreate
        }

    val selectedIdsByCreateVm by (createWorkoutViewModel?.selectedExerciseIds ?: flowOf(emptySet()))
        .collectAsState(initial = emptySet())

    var editSelectedIds by remember { mutableStateOf(setOf<String>()) }

    val workoutFull by (workoutSettingViewModel?.workoutFlow ?: flowOf(null))
        .collectAsState(initial = null)
    val historyDraft by (oldWorkoutDetailsViewModel?.draft ?: flowOf(null))
        .collectAsState(initial = null)

    val existingCatalogIds =
        remember(workoutFull) {
            workoutFull
                ?.exercises
                ?.mapNotNull { it.exercise.catalogExerciseId }
                ?.toSet()
                ?: emptySet()
        }
    val existingHistoryNames =
        remember(historyDraft) {
            historyDraft
                ?.exerciseWithSets
                ?.map { it.exerciseLog.exerciseName.lowercase() }
                ?.toSet()
                ?: emptySet()
        }

    val selectedIds = if (isAddingToExisting) editSelectedIds else selectedIdsByCreateVm
    val exerciseCounter = selectedIds.size

    val filteredExercises =
        remember(catalogExercisesList, selectedMuscleGroup, selectedEquipment, searchText.value) {
            val query = searchText.value.trim()
            catalogExercisesList.filter { item ->
                val muscleOk =
                    selectedMuscleGroup == "All" ||
                        item.bodyPart.equals(
                            selectedMuscleGroup,
                            ignoreCase = true,
                        )

                val equipmentOk =
                    selectedEquipment == "All" ||
                        item.equipment.equals(
                            selectedEquipment,
                            ignoreCase = true,
                        )

                val searchOk = query.isBlank() || item.name.contains(query, ignoreCase = true)

                muscleOk && equipmentOk && searchOk
            }
        }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { HomeTopBarCreateWorkout(navController) },
        containerColor = GrozzSystemBar,
        floatingActionButton = {
            ExtendedStartButtonCreateWorkout(
                onConfirmClick = {
                    if (isHistory) {
                        val selected = catalogExercisesList.filter { it.id in selectedIds }
                        oldWorkoutDetailsViewModel?.addDraftExercisesFromCatalog(selected)
                        navController.popBackStack()
                    } else if (isEdit) {
                        workoutSettingViewModel?.addExercisesFromCatalog(selectedIds)
                        navController.popBackStack()
                    } else {
                        createWorkoutViewModel?.onConfirmSelection()
                        val returnedToEditor =
                            navController.popBackStack(
                                route = Screens.CreateWorkout.route,
                                inclusive = false,
                            )
                        if (!returnedToEditor) {
                            navController.navigate(Screens.CreateWorkout.route)
                        }
                    }
                },
                totalSelectedExercise = exerciseCounter.toString(),
            )
        },
        floatingActionButtonPosition = FabPosition.EndOverlay,
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(20.dp))

            Row {
                FilterDropdownCreateWorkout(
                    text = selectedMuscleGroup,
                    expanded = expandableExercise,
                    onExpandChange = { expandableExercise = it },
                    items = muscleGroups,
                    onItemSelected = { selectedMuscleGroup = it },
                )
                Spacer(Modifier.width(10.dp))
                FilterDropdownCreateWorkout(
                    text = selectedEquipment,
                    expanded = expandableEquipment,
                    onExpandChange = { expandableEquipment = it },
                    items = equipment,
                    onItemSelected = { selectedEquipment = it },
                )
            }

            Spacer(Modifier.height(20.dp))

            SearchBox(searchText)

            Spacer(Modifier.height(20.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp),
            ) {
                itemsIndexed(filteredExercises) { index, item ->
                    val alreadyInWorkout =
                        when {
                            isEdit -> item.id in existingCatalogIds
                            isHistory -> item.name.lowercase() in existingHistoryNames
                            else -> false
                        }
                    val clicked = item.id in selectedIds

                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .padding(horizontal = 20.dp, vertical = 8.dp)
                                .clip(RoundedCornerShape(GrozzRadiusChip))
                                .background(GrozzSurface)
                                .clickable(enabled = !alreadyInWorkout) {
                                    if (isAddingToExisting) {
                                        editSelectedIds =
                                            if (!clicked) {
                                                editSelectedIds + item.id
                                            } else {
                                                editSelectedIds - item.id
                                            }
                                    } else {
                                        if (!clicked) {
                                            createWorkoutViewModel?.addExercise(item.id)
                                        } else {
                                            createWorkoutViewModel?.removeExercise(item.id)
                                        }
                                    }
                                }.border(
                                    width =
                                        when {
                                            alreadyInWorkout -> 1.dp
                                            clicked -> 2.dp
                                            else -> 0.dp
                                        },
                                    color =
                                        when {
                                            alreadyInWorkout -> Color.White.copy(alpha = 0.2f)
                                            clicked -> GrozzYellow
                                            else -> Color.Transparent
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            modifier = Modifier.width(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "${index + 1}",
                                color = Color.White.copy(alpha = 0.6f),
                                fontFamily = Lexend,
                                fontSize = 12.sp,
                            )
                        }

                        Spacer(Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.name,
                                color = Color.White,
                                fontFamily = Lexend,
                                fontSize = 16.sp,
                                maxLines = 1,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "${item.bodyPart} • ${item.equipment}",
                                color = GrozzYellow,
                                fontFamily = Lexend,
                                fontSize = 12.sp,
                            )
                        }

                        IconButton(
                            onClick = {
                                activityInsideViewModel.selectCatalog(item)
                                navController.navigate("activityinside")
                            },
                            modifier = Modifier.padding(end = 8.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info",
                                modifier = Modifier.size(25.dp),
                                tint = Color.White,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterDropdownCreateWorkout(
    text: String,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    items: List<String>,
    onItemSelected: (String) -> Unit,
) {
    Box {
        Button(
            onClick = { onExpandChange(true) },
            modifier =
                Modifier
                    .border(1.dp, GrozzYellow, RoundedCornerShape(8.dp))
                    .width(130.dp)
                    .height(40.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21282F)),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontFamily = Lexend,
                )
                Icon(Icons.Filled.ArrowDropDown, null, tint = GrozzYellow)
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandChange(false) },
            modifier =
                Modifier
                    .background(Color(0xFF21282F))
                    .width(130.dp)
                    .height(300.dp),
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item, color = Color.White) },
                    onClick = {
                        onItemSelected(item)
                        onExpandChange(false)
                    },
                )
            }
        }
    }
}

@Composable
fun SearchBox(text: MutableState<String>) {
    BasicTextField(
        value = text.value,
        onValueChange = { text.value = it },
        modifier =
            Modifier
                .height(45.dp)
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .background(Color(0xFF21282F), shape = RoundedCornerShape(12.dp)),
        textStyle =
            TextStyle(
                fontSize = 14.sp,
                fontFamily = Lexend,
                color = Color.White,
            ),
        singleLine = true,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.padding(horizontal = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFFD9D9D9))
                Spacer(modifier = Modifier.width(10.dp))
                if (text.value.isEmpty()) {
                    Text("Search exercises...", color = Color.Gray, fontSize = 14.sp)
                }
                innerTextField()
            }
        },
    )
}

@Composable
fun HomeTopBarCreateWorkout(navController: NavController) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                painter = painterResource(R.drawable.left),
                contentDescription = "Back",
                modifier = Modifier.size(24.dp),
                tint = GrozzOnBackground,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "ADD",
            color = GrozzOnBackground,
            fontSize = 20.sp,
            fontFamily = Oswald,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "EXERCISES",
            color = GrozzYellow,
            fontSize = 20.sp,
            fontFamily = Oswald,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(48.dp))
    }
}

@Composable
fun ExtendedStartButtonCreateWorkout(onConfirmClick: () -> Unit, totalSelectedExercise: String) {
    FloatingActionButton(
        onClick = onConfirmClick,
        containerColor = GrozzYellow,
        shape = RoundedCornerShape(GrozzRadiusPanel),
    ) {
        if (totalSelectedExercise.toInt() <= 0) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Confirm",
                tint = Color.Black,
                modifier = Modifier.size(24.dp),
            )
        } else {
            Text(
                text = totalSelectedExercise,
                color = Color.Black,
                fontSize = 20.sp,
            )
        }
    }
}
