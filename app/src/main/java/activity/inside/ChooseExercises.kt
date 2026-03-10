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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grozzbear.R
import data.local.viewmodel.ActivityInsideViewModel
import data.local.viewmodel.CreateWorkoutViewModel
import viewmodel.ViewModelSave
import viewmodel.WorkoutinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseExercises(
    navController: NavController,
    viewModelSave: ViewModelSave,
    workoutinViewModel: WorkoutinViewModel,
    createWorkoutViewModel: CreateWorkoutViewModel,
    activityInsideViewModel: ActivityInsideViewModel
) {
    val searchText = rememberSaveable { mutableStateOf("") }
    var selectedMuscleGroup by rememberSaveable { mutableStateOf("All") }
    var selectedEquipment by rememberSaveable { mutableStateOf("All") }
    var expandableExercise by remember { mutableStateOf(false) }
    var expandableEquipment by remember { mutableStateOf(false) }
    val muscleGroups = remember {
        listOf(
            "All",
            "Chest",
            "Back",
            "Quads",
            "Biceps",
            "Triceps",
            "Shoulders",
            "Abs",
            "Calves"
        )
    }
    val equipment =
        remember { listOf("All", "Cable", "Barbell", "Bodyweight", "Dumbbell", "Machine", "Plate") }

    val catalogExercisesList =
        createWorkoutViewModel.catalogWorkoutList.collectAsState(initial = emptyList()).value
    val selectedIdsByViewModel by createWorkoutViewModel.selectedExerciseIds.collectAsState()

    val filteredExercises =
        remember(catalogExercisesList, selectedMuscleGroup, selectedEquipment, searchText.value) {
            val query = searchText.value.trim()
            catalogExercisesList.filter { item ->
                val muscleOk = selectedMuscleGroup == "All" || item.bodyPart.equals(
                    selectedMuscleGroup,
                    ignoreCase = true
                )

                val equipmentOk = selectedEquipment == "All" || item.equipment.equals(
                    selectedEquipment,
                    ignoreCase = true
                )

                val searchOk = query.isBlank() || item.name.contains(query, ignoreCase = true)

                muscleOk && equipmentOk && searchOk
            }
        }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = { HomeTopBarCreateWorkout(navController) },
        containerColor = Color(0xFF121417),
        floatingActionButton = {
            ExtendedStartButtonCreateWorkout {
                createWorkoutViewModel.onConfirmSelection()
                navController.popBackStack()
            }
        },
        floatingActionButtonPosition = FabPosition.EndOverlay,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(20.dp))

            Row() {
                FilterDropdownCreateWorkout(
                    text = selectedMuscleGroup,
                    expanded = expandableExercise,
                    onExpandChange = { expandableExercise = it },
                    items = muscleGroups,
                    onItemSelected = { selectedMuscleGroup = it }
                )
                Spacer(Modifier.width(10.dp))
                FilterDropdownCreateWorkout(
                    text = selectedEquipment,
                    expanded = expandableEquipment,
                    onExpandChange = { expandableEquipment = it },
                    items = equipment,
                    onItemSelected = { selectedEquipment = it }
                )
            }


            Spacer(Modifier.height(20.dp))

            SearchBox(searchText)

            Spacer(Modifier.height(20.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                itemsIndexed(filteredExercises) { index, item ->
                    val clicked = selectedIdsByViewModel.contains(item.id)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1C2126))
                            .clickable {
                                if (!clicked) {
                                    createWorkoutViewModel.addExercise(item.id)
                                } else {
                                    createWorkoutViewModel.removeExercise(item.id)
                                }
                            }
                            .border(
                                width = if (clicked) 2.dp else 0.dp,
                                color = if (clicked) Color(0xFFF1C40F) else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.width(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${index + 1}",
                                color = Color.White.copy(alpha = 0.6f),
                                fontFamily = FontFamily(Font(R.font.lexendregular)),
                                fontSize = 12.sp
                            )
                        }

                        Spacer(Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.name,
                                color = Color.White,
                                fontFamily = FontFamily(Font(R.font.lexendbold)),
                                fontSize = 16.sp,
                                maxLines = 1
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "${item.bodyPart} • ${item.equipment}",
                                color = Color(0xFFF1C40F),
                                fontFamily = FontFamily(Font(R.font.lexendregular)),
                                fontSize = 12.sp
                            )
                        }

                        IconButton(
                            onClick = {
                                activityInsideViewModel._selectedCatalog.value = item
                                navController.navigate("activityinside")
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.infoicon128),
                                contentDescription = "Info",
                                modifier = Modifier.size(25.dp),
                                tint = Color.White
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
    onItemSelected: (String) -> Unit
) {
    Box {
        Button(
            onClick = { onExpandChange(true) },
            modifier = Modifier
                .border(1.dp, Color(0xFFF1C40F), RoundedCornerShape(8.dp))
                .width(130.dp)
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21282F)),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.lexendextrabold))
                )
                Icon(Icons.Filled.ArrowDropDown, null, tint = Color(0xFFF1C40F))
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandChange(false) },
            modifier = Modifier
                .background(Color(0xFF21282F))
                .width(140.dp)
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item, color = Color.White) },
                    onClick = {
                        onItemSelected(item)
                        onExpandChange(false)
                    }
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
        modifier = Modifier
            .height(45.dp)
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .background(Color(0xFF21282F), shape = RoundedCornerShape(12.dp)),
        textStyle = TextStyle(
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.lexendregular)),
            color = Color.White
        ),
        singleLine = true,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.padding(horizontal = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFFD9D9D9))
                Spacer(modifier = Modifier.width(10.dp))
                if (text.value.isEmpty()) {
                    Text("Search exercises...", color = Color.Gray, fontSize = 14.sp)
                }
                innerTextField()
            }
        }
    )
}

@Composable
fun HomeTopBarCreateWorkout(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                painter = painterResource(R.drawable.left),
                contentDescription = "Back",
                modifier = Modifier.size(24.dp),
                tint = Color.White
            )
        }
        Spacer(Modifier.weight(1f))
        Text(
            text = "ADD",
            color = Color.White,
            fontSize = 20.sp,
            fontFamily = FontFamily(Font(R.font.oswaldbold))
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = "EXERCISES",
            color = Color(0xFFF1C40F),
            fontSize = 20.sp,
            fontFamily = FontFamily(Font(R.font.oswaldbold))
        )
        Spacer(Modifier.weight(1f))
        Spacer(Modifier.width(48.dp))
    }
}

@Composable
fun ExtendedStartButtonCreateWorkout(onConfirmClick: () -> Unit) {
    FloatingActionButton(
        onClick = onConfirmClick,
        containerColor = Color(0xFFF1C40F),
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.projectfitnessplus),
            contentDescription = "Confirm",
            tint = Color.Black,
            modifier = Modifier.size(24.dp)
        )
    }
}