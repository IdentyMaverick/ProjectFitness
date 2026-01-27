package com.example.projectfitness.activity.inside

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.projectfitness.R
import data.local.viewmodel.CreateWorkoutViewModel
import ui.mainpages.navigation.Screens
import viewmodel.ViewModelSave
import viewmodel.WorkoutUiState
import viewmodel.WorkoutinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkout(navController: NavController, viewModelSave: ViewModelSave, workoutinViewModel: WorkoutinViewModel, createWorkoutViewModel: CreateWorkoutViewModel) {

    // Database Initialize *****************************************************************************************************************************************************************

    // Variable Initialize *****************************************************************************************************************************************************************
    var text = remember{ mutableStateOf("") }
    val searchText = rememberSaveable { mutableStateOf("") }
    var selected by rememberSaveable { mutableStateOf(setOf<String>()) }

    // Firebase---------------------------------------
    val catalogExercisesList = createWorkoutViewModel.catalogWorkoutList.collectAsState(initial = emptyList()).value
    val filteredExercises = remember(catalogExercisesList, selected, searchText.value) {
        val query = searchText.value.trim()

        catalogExercisesList.filter { item ->
            val muscleOk =
                selected.isEmpty() || selected.contains(item.bodyPart) // bodyPart ile seçenekler birebir aynı olmalı

            val searchOk =
                query.isBlank() || item.name.contains(query, ignoreCase = true)

            muscleOk && searchOk
        }
    }
    val selectedExerciseIds = createWorkoutViewModel.selectedCatalogExercises.collectAsState().value
    Log.d("drafted1:", selectedExerciseIds.toString())

    // UI Coding ****************************************************************************************************************************************************************************

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            HomeTopBarCreateWorkout(navController)
        },
        containerColor = Color(0xFF121417),
        bottomBar = {},
        floatingActionButton = {ExtendedStartButtonCreateWorkout {
            createWorkoutViewModel.onConfirmSelection()
            navController.navigate(Screens.ChooseExercises.route)
        }},
        floatingActionButtonPosition = FabPosition.EndOverlay,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Add Exercises",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.lexendbold)),
                fontSize = 25.sp)
            Spacer(Modifier.size(20.dp))
            Text("Muscle Group",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.lexendbold)),
                fontSize = 15.sp)
            Spacer(Modifier.size(5.dp))
            MuscleGroupMultiSelect3(selected = selected, onSelectedChange = { selected = it })
            Spacer(Modifier.size(20.dp))
            SearchBox(text)
            Spacer(Modifier.size(20.dp))

                    val selectedIds = remember { mutableStateOf(setOf<String>()) }
            Log.d("selected exercises is", selectedIds.toString())
                    LazyColumn(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    ) {
                        itemsIndexed(filteredExercises) { index, item ->
                            val clicked = selectedIds.value.contains(item.name)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .padding(horizontal = 20.dp, vertical = 8.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF1C2126))
                                    .clickable(true, onClick = {
                                        if (!clicked) {
                                            selectedIds.value += item.name
                                            createWorkoutViewModel.addExercise(item.id)
                                        } else {
                                            selectedIds.value -= item.name
                                            createWorkoutViewModel.removeExercise(item.id)
                                        }
                                    })
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
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
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
                                        color = Color.White.copy(alpha = 0.65f),
                                        fontFamily = FontFamily(Font(R.font.lexendregular)),
                                        fontSize = 12.sp,
                                        maxLines = 1
                                    )

                                    // secondaryMuscles list ise:
                                    val secondary = item.secondaryMuscles
                                        ?.takeIf { it.isNotEmpty() }
                                        ?.joinToString(", ")
                                        ?: "—"

                                    Spacer(Modifier.height(2.dp))

                                    Text(
                                        text = "Secondary: $secondary",
                                        color = Color.White.copy(alpha = 0.45f),
                                        fontFamily = FontFamily(Font(R.font.lexendregular)),
                                        fontSize = 12.sp,
                                        maxLines = 1
                                    )
                                }

                                Spacer(Modifier.width(12.dp))
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(end = 20.dp)
                                ) {
                                    // burada seçili state’in varsa ona göre + / ✓ değiştir
                                    Icon(
                                        painter = painterResource(R.drawable.infoicon128),
                                        contentDescription = null,
                                        modifier = Modifier.size(25.dp).clickable(enabled = true,
                                            onClick = {
                                            navController.navigate("workoutdetails")
                                        }),
                                        tint = Color.White
                                    )
                                }
                            }
                        }
            }
        }
    }


}
/*@Composable
fun imager(projectFitnessViewModel: ProjectFitnessViewModel,item : Exercises) : Int {

     var items = item

    if (items.index == "0")
    {
        return R.drawable.barbellbenchpress
    }
    else if (items.index == "1")
    {
        return R.drawable.cablecrossover
    }
    else if (items.index == "2")
    {
        return R.drawable.calfraises
    }
    else if (items.index == "3")
    {
        return R.drawable.chestdip
    }
    else if (items.index == "4")
    {
        return R.drawable.chestpro
    }
    else if (items.index == "5")
    {
        return R.drawable.cablecrossover
    }
    // QUADRICEPS EXERCISES
    else if (items.index == "15")
    {
        return R.drawable.legpress
    }
    else if (items.index == "21")
    {
        return R.drawable.squat
    }
    else if (items.index == "22")
    {
        return R.drawable.legextension
    }
    else if (items.index == "23")
    {
        return R.drawable.machinehacksquat
    }
    else if (items.index == "24")
    {
        return R.drawable.dumbbellsquat
    }
    else if (items.index == "25")
    {
        return R.drawable.dumbbelllunge
    }
    else if (items.index == "26") {
        return R.drawable.frontsquat
    }
    else if (items.index == "27") {
        return R.drawable.dumbbellbulgariansplitsquat
    }
    else if (items.index == "28") {
        return R.drawable.dumbbellsplitsquat
    }
    else if (items.index == "29") {
        return R.drawable.pliesquat
    }
    else if (items.index == "30") {
        return R.drawable.smithmachinesquat
    }
    else if (items.index == "31") {
        return R.drawable.singlelegextension
    }
    else if (items.index == "32") {
        return R.drawable.boxjump
    }
    //HAMSTRING EXERCISES
    else if (items.index == "33") {
        return R.drawable.stifflegdeadlift
    }
    else if (items.index == "34") {
        return R.drawable.dumbbellhamstringcurl
    }
    else if (items.index == "35") {
        return R.drawable.trapbardeadlift
    }
    else if (items.index == "36") {
        return R.drawable.seatedlegcurl
    }
    else if (items.index == "37") {
        return R.drawable.kettlebellswing
    }
    else if (items.index == "38") {
        return R.drawable.sumodeadlift
    }
    else if (items.index == "39") {
        return R.drawable.lyinglegcurl
    }
    else if (items.index == "40") {
        return R.drawable.nordichamstringcurl
    }
    // CALF EXERCISES
    else if (items.index == "41") {
        return R.drawable.seatedcalfraise
    }
    else if (items.index == "42") {
        return R.drawable.legpresscalfraise
    }
    else if (items.index == "43") {
        return R.drawable.standingmachinecalfraise
    }
    //GLUTES
    else if (items.index == "44") {
        return R.drawable.hyperextension
    }
    else if (items.index == "45") {
        return R.drawable.barbellhipthrust
    }
    else if (items.index == "46") {
        return R.drawable.standinggoodmorning
    }
    //EXTENSION
    else if (items.index == "47") {
        return R.drawable.itbandfoamrolling
    }
    else if (items.index == "48") {
        return R.drawable.plantarfascialacrosseball
    }
    else if (items.index == "49") {
        return R.drawable.kneelingposteriorhipcapsulemobilization
    }
    //ABDUCTORS
    else if (items.index == "50") {
        return R.drawable.hipabductionmachine
    }
    //ADDUCTORS
    else if (items.index == "51") {
        return R.drawable.hipadductionmachine
    }
        return R.drawable.down
}*/

@Composable
fun HomeTopBarCreateWorkout(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = { navController.navigate("chooseexercises") { popUpTo("createworkout") { inclusive = true } } }) {
            Icon(
                painter = painterResource(R.drawable.left),
                contentDescription = null,
                modifier = Modifier.size(25.dp),
                tint = Color.White
            )
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = "PROJECT FITNESS",
            color = Color(0xFFF1C40F),
            fontSize = 14.sp,
            letterSpacing = 6.sp,
            fontFamily = FontFamily(Font(R.font.lexendregular))
        )

        Spacer(Modifier.weight(1f))
        Text("Spacer",
            color = Color.Transparent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuscleGroupMultiSelect() {
    val options = listOf("All", "Popular", "New")
    var selected by rememberSaveable { mutableStateOf(setOf<String>()) }

    MultiChoiceSegmentedButtonRow {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                checked = selected.contains(label),
                onCheckedChange = { isChecked ->
                    selected = if (isChecked) selected + label else selected - label
                },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                icon = {
                    // İstersen seçili ikonu koy:
                    if (selected.contains(label)) {
                        Icon(Icons.Default.Check, contentDescription = null)
                    }
                },
                label = { Text(label) },
                modifier = Modifier.width(80.dp).height(40.dp),
                colors = SegmentedButtonDefaults.colors(activeContainerColor = Color(0xFFF1C40F))
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuscleGroupMultiSelect2() {
    val options = listOf("All", "Cardio", "Strength", "Label")
    var selected by rememberSaveable { mutableStateOf(setOf<String>()) }

    MultiChoiceSegmentedButtonRow {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                checked = selected.contains(label),
                onCheckedChange = { isChecked ->
                    selected = if (isChecked) selected + label else selected - label
                },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                icon = {
                    // İstersen seçili ikonu koy:
                    if (selected.contains(label)) {
                        Icon(Icons.Default.Check, contentDescription = null)
                    }
                },
                label = { Text(label) },
                modifier = Modifier.width(80.dp).height(40.dp),
                colors = SegmentedButtonDefaults.colors(activeContainerColor = Color(0xFFF1C40F))
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuscleGroupMultiSelect3(selected: Set<String>, onSelectedChange: (Set<String>) -> Unit) {
    val options = listOf("Chest", "Back", "Leg", "Arm", "Shoulders")

    MultiChoiceSegmentedButtonRow {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                checked = selected.contains(label),
                onCheckedChange = { isChecked ->
                    val newSet = if (isChecked) selected + label else selected - label
                    onSelectedChange(newSet)
                },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                icon = {
                    if (selected.contains(label)) {
                        Icon(Icons.Default.Check, contentDescription = null)
                    }
                },
                label = { Text(label) },
                modifier = Modifier.width(80.dp).height(40.dp),
                colors = SegmentedButtonDefaults.colors(activeContainerColor = Color(0xFFF1C40F))
            )
        }
    }
}

@Composable
fun SearchBox(text: MutableState<String>) {
    Row() {
        BasicTextField(
            value = text.value,
            onValueChange = { text.value = it },
            modifier = Modifier
                .height(41.dp)
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp)
                .background(Color(0xFF21282F), shape = RoundedCornerShape(15.dp)),
            textStyle = TextStyle(
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.lexendbold)),
                color = Color(0xFFD9D9D9)
            ),
            maxLines = 1,
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFF21282F),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .border(
                            width = 2.dp,
                            color = Color(0xFF21282F),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    verticalAlignment = Alignment.CenterVertically,

                    ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Favorite icon",
                        tint = Color(0xFFD9D9D9)
                    )
                    Spacer(modifier = Modifier.width(width = 10.dp))
                    innerTextField()
                }
            }

        )
    }
}

@Composable
fun ExtendedStartButtonCreateWorkout(onConfirmClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onConfirmClick() },
        containerColor = Color(0xFFF1C40F)
    )
    {
        Icon(painter = painterResource(R.drawable.projectfitnessplus),
            contentDescription = null)
    }
}