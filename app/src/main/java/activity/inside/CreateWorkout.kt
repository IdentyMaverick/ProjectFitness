package activity.inside

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.chargemap.compose.numberpicker.NumberPicker
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.grozzbear.R
import data.local.viewmodel.ChooseExercisesViewModel
import data.local.viewmodel.CreateWorkoutViewModel
import data.local.viewmodel.ExerciseDraft
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ui.mainpages.navigation.Screens
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@SuppressLint("UnrememberedMutableState")
@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun CreateWorkout(
    navController: NavController,
    createWorkoutViewModel: CreateWorkoutViewModel,
    chooseExercisesViewModel: ChooseExercisesViewModel
) {
    val draft = createWorkoutViewModel.draftExercises.collectAsState().value
    val modalBottomSheetState = rememberModalBottomSheetState()
    var expandBottomSheet by remember { mutableStateOf(false) }

    var editingCatalogId by remember { mutableStateOf("") }
    var editingSetIndex by remember { mutableStateOf(0) }
    var tempReps by remember { mutableStateOf(0) }
    var tempWeight by remember { mutableStateOf(0) }

    val context = LocalContext.current
    val workoutNameInput = chooseExercisesViewModel.workoutName.collectAsState().value
    val currentUser = Firebase.auth.currentUser
    val verticalScroll = rememberScrollState()

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = { HomeTopBarChooseExercises(navController, chooseExercisesViewModel) },
        containerColor = Color(0xFF121417),
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.verticalScroll(verticalScroll)
            ) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screens.CreateWorkout.route) },
                    containerColor = Color(0xFF1C2126),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }

                ExtendedCreateButtonAddExercise {
                    if (currentUser != null && workoutNameInput.isNotBlank() && draft.isNotEmpty()) {
                        CoroutineScope(Dispatchers.Main).launch {
                            createWorkoutViewModel.saveWorkout(
                                workoutId = UUID.randomUUID().toString(),
                                workoutName = workoutNameInput,
                                workoutType = "User",
                                workoutRating = 0,
                                ownerUid = currentUser.uid,
                                syncState = true,
                                image = 0,
                                onDone = {
                                    Toast.makeText(context, "Workout Saved!", Toast.LENGTH_SHORT)
                                        .show()
                                    navController.navigate(Screens.Activity.route)
                                    chooseExercisesViewModel.setName("")
                                },
                                onError = { e ->
                                    Toast.makeText(
                                        context,
                                        "Error: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            )
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Please enter a name and add exercises.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.EndOverlay
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Create Workout",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.lexendbold)),
                fontSize = 25.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(20.dp)
            )

            Text(
                "Workout Name",
                color = Color.White.copy(alpha = 0.7f),
                fontFamily = FontFamily(Font(R.font.lexendsemibold)),
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = workoutNameInput,
                onValueChange = { chooseExercisesViewModel.setName(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("e.g. Monday Leg Day", color = Color(0xFF4B5F71)) },
                textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF1C2126),
                    unfocusedContainerColor = Color(0xFF1C2126),
                    focusedBorderColor = Color(0xFF4B5F71),
                    unfocusedBorderColor = Color(0xFF2E353D),
                    cursorColor = Color(0xFFF1C40F)
                ),
                trailingIcon = {
                    IconButton(onClick = { chooseExercisesViewModel.setName(RandomName()) }) {
                        Icon(
                            painter = painterResource(R.drawable.casinoicon128),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )

            Spacer(Modifier.height(20.dp))

            if (draft.isNotEmpty()) {
                LazyColumn(modifier = Modifier.padding(horizontal = 20.dp)) {
                    itemsIndexed(draft) { _, item ->
                        ExerciseExpandableCardChooseExercises(
                            onEditClick = { setIndex, weight, reps ->
                                editingCatalogId = item.catalogId
                                editingSetIndex = setIndex
                                tempWeight = weight.toInt()
                                tempReps = reps
                                expandBottomSheet = true
                            },
                            exerciseDraft = item,
                            createWorkoutViewModel = createWorkoutViewModel,
                            catalogId = item.catalogId
                        )
                        Spacer(Modifier.size(10.dp))
                    }
                }
            } else {
                EmptyExercisesPlaceholder()
            }
        }

        if (expandBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { expandBottomSheet = false },
                sheetState = modalBottomSheetState,
                containerColor = Color(0xFF121417)
            ) {
                var currentRepsPicker by remember(tempReps) { mutableIntStateOf(tempReps) }
                var currentWeightPicker by remember(tempWeight) { mutableIntStateOf(tempWeight) }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        PickerComponent("REPS", currentRepsPicker) { currentRepsPicker = it }
                        Spacer(modifier = Modifier.width(40.dp))
                        PickerComponent("KG", currentWeightPicker) { currentWeightPicker = it }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    Button(
                        onClick = {
                            createWorkoutViewModel.updateSet(
                                editingCatalogId,
                                editingSetIndex,
                                currentRepsPicker,
                                currentWeightPicker.toFloat()
                            )
                            expandBottomSheet = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1C2126)),
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Text(
                            "Update Set",
                            fontFamily = FontFamily(Font(R.font.lexendbold)),
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun PickerComponent(label: String, value: Int, onValueChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.width(10.dp))
        NumberPicker(
            value = value,
            onValueChange = onValueChange,
            range = 0..300,
            dividersColor = Color(0xFFF1C40F),
            textStyle = TextStyle(color = Color.White)
        )
    }
}

@Composable
fun ExerciseExpandableCardChooseExercises(
    onEditClick: (Int, Float, Int) -> Unit,
    exerciseDraft: ExerciseDraft,
    createWorkoutViewModel: CreateWorkoutViewModel,
    catalogId: String
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1C2126))
            .clickable { expanded = !expanded }
            .animateContentSize()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(44.dp)
                    .background(Color(0xFFF1C40F), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painterResource(R.drawable.dumbbellicon128),
                    null,
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = exerciseDraft.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "${exerciseDraft.bodyPart.uppercase()}",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            Icon(
                painter = painterResource(if (expanded) R.drawable.down else R.drawable.down),
                contentDescription = null,
                tint = Color(0xFFF1C40F),
                modifier = Modifier.size(20.dp)
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column {
                Spacer(Modifier.height(20.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        "SET",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "KG",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "REPS",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "ACTION",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        modifier = Modifier.weight(1.5f),
                        textAlign = TextAlign.End
                    )
                }

                exerciseDraft.sets.forEachIndexed { index, set ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${index + 1}",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "${set.weight}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "${set.reps}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )

                        Row(Modifier.weight(1.5f), horizontalArrangement = Arrangement.End) {
                            Icon(
                                painter = painterResource(R.drawable.editnote),
                                contentDescription = null,
                                tint = Color(0xFFF1C40F),
                                modifier = Modifier
                                    .size(22.dp)
                                    .clickable { onEditClick(index, set.weight, set.reps) }
                            )
                            Spacer(Modifier.width(12.dp))
                            Icon(
                                painter = painterResource(R.drawable.minusicon128),
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier
                                    .size(22.dp)
                                    .clickable {
                                        createWorkoutViewModel.removeSetToExercise(
                                            catalogId,
                                            set
                                        )
                                    }
                            )
                        }
                    }
                }

                IconButton(
                    onClick = { createWorkoutViewModel.addSetToExercise(catalogId) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Set",
                        tint = Color(0xFFF1C40F)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyExercisesPlaceholder() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 100.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.dumbbell),
            contentDescription = null,
            tint = Color(0xFF2C3138),
            modifier = Modifier.size(150.dp)
        )
        Spacer(Modifier.size(50.dp))
        Text(
            "No Exercise Yet",
            color = Color.White,
            fontFamily = FontFamily(Font(R.font.lexendbold)),
            fontSize = 22.sp
        )
        Text(
            "Add your first exercise right below",
            color = Color.Gray,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun HomeTopBarChooseExercises(
    navController: NavController,
    chooseExercisesViewModel: ChooseExercisesViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            navController.navigate(Screens.Activity.route)
            chooseExercisesViewModel.setName("")
        }) {
            Icon(
                painter = painterResource(R.drawable.left),
                contentDescription = null,
                modifier = Modifier.size(25.dp),
                tint = Color.White
            )
        }
        Spacer(Modifier.weight(1f))
        Text(
            text = "GROZZ",
            color = Color(0xFFF1C40F),
            fontSize = 24.sp,
            fontFamily = FontFamily(Font(R.font.oswaldbold))
        )
        Spacer(Modifier.weight(1f))
        Box(Modifier.size(25.dp))
    }
}

@Composable
fun ExtendedCreateButtonAddExercise(onConfirmClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = onConfirmClick,
        icon = { Icon(Icons.Default.Check, null, Modifier.size(30.dp)) },
        text = {
            Text(
                "Save Workout",
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                    fontSize = 16.sp
                )
            )
        },
        containerColor = Color(0xFFF1C40F),
        modifier = Modifier.padding(bottom = 50.dp)
    )
}

fun RandomName(): String = listOf("Rock it!", "Dumbbell Day!", "Power Hour", "Leg Legend").random()