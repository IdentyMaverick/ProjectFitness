package ui.mainpages.inside

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grozzbear.R
import com.grozzbear.projectfitness.data.local.entity.SetEntity
import com.grozzbear.projectfitness.data.local.entity.WorkoutExerciseEntity
import com.grozzbear.projectfitness.data.local.viewmodel.WorkoutSettingViewModel
import ui.mainpages.mainpages.WorkoutTag
import viewmodel.ViewModelSave
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutSettingScreen(
    navController: NavController,
    viewModelSave: ViewModelSave,
    workoutSettingViewModel: WorkoutSettingViewModel
) {
    val lazyListState = rememberLazyListState()
    val selectedWorkout by workoutSettingViewModel.workoutFlow.collectAsState(null)
    val image = selectedWorkout.let { workout -> workout?.workout?.image }
    val sheetState = rememberModalBottomSheetState()

    var showBottomSheet by remember { mutableStateOf(false) }
    var editingSet by remember { mutableStateOf<SetEntity?>(null) }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            HomeTopBarWorkoutSettingScreen(if (image != null) image else 0, navController)
        },
        containerColor = Color(0xFF121417),
        bottomBar = {
            FixedStartButton {
                selectedWorkout?.workout?.let { workout ->
                    navController.navigate("workoutlog/${workout.workoutId}")
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            if (selectedWorkout == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFF1C40F))
                }
            } else {
                // Workout Başlık
                Column(modifier = Modifier.padding(horizontal = 30.dp)) {
                    Text(
                        text = selectedWorkout!!.workout.workoutName,
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.lexendbold)),
                        fontSize = 30.sp
                    )
                    Spacer(Modifier.height(5.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        WorkoutTag(
                            text = selectedWorkout!!.workout.workoutType,
                            icon = R.drawable.dumbbellicon128,
                            textColor = Color(0xFFF1C40F),
                            iconColor = Color(0xFFF1C40F)
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))
                Box(
                    Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFFF1C40F))
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(20.dp))

                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    if (selectedWorkout?.exercises?.isNotEmpty() == true) {
                        itemsIndexed(selectedWorkout!!.exercises) { _, item ->
                            ExerciseExpandableCardWorkoutSettingScreen(
                                exerciseDraft = item.exercise,
                                exerciseSet = item.sets,
                                onEditClick = { set ->
                                    editingSet = set
                                    showBottomSheet = true
                                },
                                onDeleteSetClick = { set ->
                                    workoutSettingViewModel.deleteSet(set)
                                },
                                onAddSetClick = {
                                    workoutSettingViewModel.addSet(
                                        UUID.randomUUID().toString(),
                                        item.exercise.exerciseId
                                    )
                                },
                            )
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                }
            }
        }

        if (showBottomSheet && editingSet != null) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color(0xFF1C2126),
            ) {
                EditSetBottomSheetContent(
                    set = editingSet!!,
                    onSave = { updatedReps, updatedWeight ->
                        workoutSettingViewModel.updateSet(
                            editingSet!!.setId,
                            editingSet!!.exerciseOwnerId,
                            updatedReps,
                            updatedWeight
                        )
                        showBottomSheet = false
                    },
                    onDelete = {
                        workoutSettingViewModel.deleteSet(editingSet!!)
                        showBottomSheet = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExerciseExpandableCardWorkoutSettingScreen(
    exerciseDraft: WorkoutExerciseEntity,
    exerciseSet: List<SetEntity>,
    onEditClick: (SetEntity) -> Unit,
    onDeleteSetClick: (SetEntity) -> Unit,
    onAddSetClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1C2126))
            .combinedClickable(
                onClick = { expanded = !expanded },
                onLongClick = { }
            )
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
                    text = exerciseDraft.exerciseName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = exerciseDraft.bodyPart.uppercase(),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color(0xFFF1C40F),
                modifier = Modifier.size(24.dp)
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

                exerciseSet.forEachIndexed { index, set ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            String.format("%02d", index + 1),
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
                                contentDescription = "Edit",
                                tint = Color(0xFFF1C40F),
                                modifier = Modifier
                                    .size(22.dp)
                                    .clickable { onEditClick(set) }
                            )
                            Spacer(Modifier.width(12.dp))
                            Icon(
                                painter = painterResource(R.drawable.closeicon128),
                                contentDescription = "Delete",
                                tint = Color.Red,
                                modifier = Modifier
                                    .size(22.dp)
                                    .clickable { onDeleteSetClick(set) }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = { onAddSetClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .border(1.dp, Color(0xFFF1C40F), shape = RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text(
                        "ADD SET",
                        color = Color(0xFFF1C40F),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun EditSetBottomSheetContent(
    set: SetEntity,
    onSave: (Int, Double) -> Unit,
    onDelete: () -> Unit
) {
    var reps by remember { mutableStateOf(set.reps) }
    var weight by remember { mutableStateOf(set.weight) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("EDIT SET", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))

        // Reps Control
        Row(verticalAlignment = Alignment.CenterVertically) {
            AdjustButton("-") { if (reps > 0) reps-- }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(100.dp)
            ) {
                Text("REPS", color = Color.Gray, fontSize = 12.sp)
                Text("$reps", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }
            AdjustButton("+") { reps++ }
        }

        Spacer(Modifier.height(20.dp))

        // Weight Control
        Row(verticalAlignment = Alignment.CenterVertically) {
            AdjustButton("-") { if (weight >= 1f) weight -= 1f }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(100.dp)
            ) {
                Text("WEIGHT", color = Color.Gray, fontSize = 12.sp)
                Text("$weight", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }
            AdjustButton("+") { weight += 1f }
        }

        Spacer(Modifier.height(30.dp))

        Button(
            onClick = { onSave(reps, weight.toDouble()) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1C40F))
        ) {
            Text("SAVE", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AdjustButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.1f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = Color(0xFFF1C40F), fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun HomeTopBarWorkoutSettingScreen(_workoutSetting: Int, navController: NavController) {
    // 1. Box kullanarak üst üste binmeyi sağlıyoruz
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp) // TopBar yüksekliğini sabitliyoruz
    ) {
        // 2. Arka plan görseli (En altta kalır)
        Image(
            painter = if (_workoutSetting != 0) painterResource(_workoutSetting) else painterResource(
                R.drawable.loginscreenphoto
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop, // Tüm alanı kaplaması için
            modifier = Modifier.matchParentSize() // Box'ın boyutuna eşitler
        )

        // 3. İçerik (Görselin üzerinde görünür)
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    painter = painterResource(R.drawable.left),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(25.dp)
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

            // Sağ tarafta denge sağlamak için boş alan
            Box(Modifier.size(25.dp))
        }
    }
}

@Composable
fun FixedStartButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF121417))
            .padding(20.dp)
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1C40F))
        ) {
            Text(
                "START WORKOUT",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}