package activity.inside

//noinspection UsingMaterialAndMaterial3Libraries

import android.annotation.SuppressLint
import android.content.Context
import android.health.connect.datatypes.units.Length
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.chargemap.compose.numberpicker.NumberPicker
import com.example.projectfitness.R
import com.example.projectfitness.data.local.entity.SetEntity
import com.example.projectfitness.data.local.entity.WorkoutEntity
import com.google.android.play.core.integrity.d
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.patrykandpatrick.vico.compose.common.shader.color
import data.local.viewmodel.ChooseExercisesViewModel
import data.local.viewmodel.CreateWorkoutViewModel
import data.local.viewmodel.SetDraft
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ui.mainpages.mainpages.LongClickModalBottom
import ui.mainpages.navigation.NavigationBar
import ui.mainpages.navigation.Screens
import java.util.UUID
import kotlin.collections.minus
import kotlin.collections.plus

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class
)
@SuppressLint("UnrememberedMutableState", "InvalidColorHexValue")
@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun ChooseExercises(navController: NavController, createWorkoutViewModel: CreateWorkoutViewModel, chooseExercisesViewModel: ChooseExercisesViewModel) {

    //Database Creation *************************************************************************************************************************************************************
    val draft = createWorkoutViewModel.draftExercises.collectAsState().value
    val modalBottomSheetState = rememberModalBottomSheetState()
    var expandBottomSheet by remember { mutableStateOf(false) }

    var editingCatalogId by remember { mutableStateOf("") }
    var editingSetIndex by remember { mutableStateOf(0) }
    var tempReps by remember { mutableStateOf(0) }
    var tempWeight by remember { mutableStateOf(0) }

    val context = LocalContext.current

    var workoutNameInput = chooseExercisesViewModel.workoutName.collectAsState().value
    val currentUser = Firebase.auth.currentUser

    val deleteSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var itemToDeleteId by remember { mutableStateOf<String?>(null) }



    //**************************************************************************************************************************************************************************************

    // UI Coding ****************************************************************************************************************************************************************************
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            HomeTopBarChooseExercises(navController, chooseExercisesViewModel)
        },
        containerColor = Color(0xFF121417),
        bottomBar = {
            // senin NavigationBar fonksiyonun zaten hazır
            val indexs = 1
            val flagg = false
            val flagg2 = true
            val flagg3 = false
            val flagg4 = false
            NavigationBar(navController = navController, indexs, flagg, flagg2, flagg3, flagg4)
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End, // Sağ tarafa yasla
                verticalArrangement = Arrangement.spacedBy(16.dp) // Butonlar arası boşluk
            ) {
                // --- 1. BUTON (Yeni Eklenen Küçük Buton) ---
                FloatingActionButton(
                    onClick = {
                        CoroutineScope(Dispatchers.Main).launch {
                            navController.navigate(Screens.CreateWorkout.route)
                        }
                    },
                    containerColor = Color(0xFF1C2126), // Koyu gri arka plan
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Settings")
                }

                ExtendedCreateButtonAddExercise {
                    if (currentUser != null && workoutNameInput.isNotBlank() && draft.isNotEmpty()) {
                        CoroutineScope(Dispatchers.Main).launch {
                            val uniqueWorkoutId = UUID.randomUUID().toString()
                            createWorkoutViewModel.saveWorkout(
                                workoutId = uniqueWorkoutId, // Yeni kayıt olduğu için boş bırakıyoruz (Repo'da UUID üretiliyorsa)
                                workoutName = workoutNameInput, // TextField'dan gelen isim
                                workoutType = "User", // Burayı sabit verebilirsin veya bir selector ekleyebilirsin
                                workoutRating = 0,
                                ownerUid = currentUser.uid, // Firebase User ID
                                syncState = true,
                                onDone = {
                                    Toast.makeText(context, "Workout Saved!", Toast.LENGTH_SHORT).show()
                                    navController.navigate(Screens.Activity.route)
                                    chooseExercisesViewModel.setName("")
                                },
                                onError = { e ->
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            )
                        }
                    } else {
                        // Hata mesajı: Kullanıcı yoksa veya isim girilmediyse
                        Toast.makeText(context, "Please enter a workout name and add exercises before saving.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.EndOverlay, // EndOverlay yerine End daha stabil olabilir
        modifier = Modifier.fillMaxSize()
    ) { paddingValues->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier
                .padding(top = 30.dp))
            Text("Create Workout",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.lexendbold)),
                fontSize = 30.sp,
                modifier = Modifier)
            Spacer(modifier = Modifier
                .padding(top = 30.dp))
            Text("Type Workout Name",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.lexendsemibold)),
                fontSize = 20.sp,
                modifier = Modifier)
            Spacer(modifier = Modifier
                .padding(top = 30.dp))
            BasicTextField(
                value = workoutNameInput,
                onValueChange = { workoutNameInput = it
                    chooseExercisesViewModel.setName(workoutNameInput)
                                },

                modifier = Modifier
                    .height(40.dp)
                    .padding(horizontal = 30.dp)
                    .background(Color.Transparent, shape = RoundedCornerShape(10.dp)),
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                    color = Color(0xFFD9D9D9)
                ),
                maxLines = 1,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFF283747),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = Color(0xFF283747),
                                shape = RoundedCornerShape(10.dp)
                            ),
                        verticalAlignment = Alignment.CenterVertically,

                        ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Favorite icon",
                            tint = Color(0xFFF1C40F),
                            modifier = Modifier.padding(start = 5.dp)
                        )
                        Spacer(modifier = Modifier.width(width = 10.dp))
                        innerTextField()
                    }
                }
            )
            Spacer(Modifier.size(50.dp))
            if (draft.isNotEmpty()){
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 20.dp),
                state = LazyListState()
            )
            {
                itemsIndexed(draft) { index, item ->
                    ExerciseItem(item.name, index, item.sets, createWorkoutViewModel, item.catalogId,
                        onEditClick = { setIndex, currentReps, currentWeight ->
                            editingCatalogId = item.catalogId
                            editingSetIndex = setIndex
                            tempReps = currentReps.toInt()
                            tempWeight = currentWeight
                            expandBottomSheet = true},
                        onLongClick = {
                            itemToDeleteId = item.catalogId
                        },
                        )
                    Spacer(Modifier.size(10.dp))
                }
                }

            }
            else {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize().padding(bottom = 100.dp)
                ) {
                    Icon(painter = painterResource(R.drawable.dumbbell), contentDescription = null, tint = Color(0xFF2C3138), modifier = Modifier.size(150.dp))
                    Spacer(Modifier.size(50.dp))
                    Text("No Exercise Yet", color = Color.White, fontFamily = FontFamily(Font(R.font.lexendbold)), fontSize = 22.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Add your first exercise right below",
                        style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.lexendregular)),
                            fontSize = 16.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }
        if (expandBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {expandBottomSheet = false},
                sheetState = modalBottomSheetState,
                containerColor = Color(0xFF121417)
            ) {
                var currentRepsPicker by remember(tempReps) { mutableStateOf(tempReps) }
                var currentWeightPicker by remember(tempWeight) { mutableStateOf(tempWeight) }
                Column(modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 0.dp)
                        ) {
                            Text("REP", color = Color.White, fontSize = 20.sp, fontFamily = FontFamily(Font(R.font.lexendbold)))
                            Spacer(Modifier.size(10.dp))
                            NumberPicker(
                                value = currentRepsPicker,
                                onValueChange = {currentRepsPicker = it},
                                range = 0..100,
                                dividersColor = Color(0xFFF1C40F),
                                textStyle = TextStyle(color = Color.White)
                            )
                        }
                        Spacer(modifier = Modifier.size(40.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 0.dp)
                        ) {
                            Text("Weight", color = Color.White, fontSize = 20.sp, fontFamily = FontFamily(Font(R.font.lexendbold)))
                            Spacer(Modifier.size(10.dp))
                            NumberPicker(
                                value = currentWeightPicker,
                                onValueChange = {currentWeightPicker = it},
                                range = 0..100,
                                dividersColor = Color(0xFFF1C40F),
                                textStyle = TextStyle(color = Color.White)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.size(30.dp))
                    Button(onClick = {
                        Log.d("CATALOG", "editing one is $editingCatalogId and set is ${editingSetIndex+1}")
                        createWorkoutViewModel.updateSet(editingCatalogId, editingSetIndex, currentRepsPicker, currentWeightPicker.toFloat())
                        expandBottomSheet = false
                    },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1C2126))) {
                        Text("Change", fontFamily = FontFamily(Font(R.font.lexendbold)))
                    }
                    Spacer(modifier = Modifier.size(20.dp))
                }
            }
        }
        if (itemToDeleteId != null) {
            LongClickModalBottomChooseExercises(
                sheetState = deleteSheetState,
                onDismiss = { itemToDeleteId = null }, // Kapatılınca ID'yi sıfırla
                onDeleteClick = {
                    // ViewModel'den silme işlemini yap
                    createWorkoutViewModel.removeDraftExercise(itemToDeleteId!!)
                    itemToDeleteId = null // İşlem bitince kapat
                }
            )
        }
}
}

@Composable
private fun HomeTopBarChooseExercises(navController: NavController, chooseExercisesViewModel: ChooseExercisesViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = {navController.navigate(Screens.Activity.route)
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
            text = "PROJECT FITNESS",
            color = Color(0xFFF1C40F),
            fontSize = 14.sp,
            letterSpacing = 6.sp,
            fontFamily = FontFamily(Font(R.font.lexendregular))
        )

        Spacer(Modifier.weight(1f))

        IconButton(onClick = {}) {
            Icon(
                painter = painterResource(R.drawable.projectfitnesspointheavy),
                contentDescription = null,
                modifier = Modifier.size(25.dp),
                tint = Color.Transparent
            )
        }
    }
}

@Composable
fun ExtendedCreateButtonAddExercise(onConfirmClick: () -> Unit) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    ExtendedFloatingActionButton(
        onClick = {
            onConfirmClick()
        },
        icon = {
            Icon(imageVector = Icons.Default.Check, "Extended create workout button", Modifier.size(40.dp))
        },
        text = {
            Text("Add Exercise", style = TextStyle(fontFamily = FontFamily(Font(R.font.lexendbold)), fontSize = 18.sp))
        },
        containerColor = Color(0xFFF1C40F),
        expanded = expanded,
        modifier = Modifier
            .padding(bottom = 50.dp)
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun ExerciseItem(
    exerciseName: String,
    index: Int,
    exerciseSet: List<SetDraft>,
    createWorkoutViewModel: CreateWorkoutViewModel,
    catalogId: String,
    onEditClick: (Int, Float, Int) -> Unit,
    onLongClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .combinedClickable(
                onLongClick = { onLongClick() },
                onClick = {  }
            )
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1C2126))
            .animateContentSize() // ✅ daha smooth
            .padding(16.dp)
    ) {

        // ÜST SATIR
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
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

            Text(
                text = exerciseName,
                color = Color.White,
                modifier = Modifier.weight(1f),
                fontFamily = FontFamily(Font(R.font.lexendregular)),
                fontSize = 15.sp,
                maxLines = 1
            )

            // ✅ sadece ok tıklanınca aç/kapa
            Icon(
                imageVector = if (expanded)
                    Icons.Default.Info
                else
                    Icons.Default.Info,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(20.dp)
                    .clickable {  }
            )
            Spacer(Modifier.size(10.dp))
            Icon(
                imageVector = if (expanded)
                    Icons.Default.KeyboardArrowUp
                else
                    Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { expanded = !expanded }
            )
        }

        // AÇILAN ALAN
        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier.padding(top = 12.dp, start = 44.dp)
            ) {
                if (exerciseSet.isEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 70.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.clickable(onClick = {
                                createWorkoutViewModel.addSetToExercise(catalogId)
                            })
                        )
                        Text(
                            text = "Henüz set yok",
                            color = Color.White.copy(alpha = 0.5f),
                            fontFamily = FontFamily(Font(R.font.lexendregular)),
                            fontSize = 13.sp
                        )
                    }
                } else {
                    exerciseSet.forEachIndexed { setIndex, item ->
                        Spacer(modifier = Modifier.size(5.dp))
                        if (setIndex + 1 == exerciseSet.size) {
                            Row() {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp).clickable(onClick = {
                                    onEditClick(setIndex, item.weight, item.weight.toInt())
                                }))
                                Spacer(modifier = Modifier.size(15.dp))
                                Text(
                                    text = "Set ${setIndex + 1} x ${item.reps} reps • ${item.weight} kg",
                                    color = Color.White.copy(alpha = 0.65f),
                                    fontFamily = FontFamily(Font(R.font.lexendregular)),
                                    fontSize = 13.sp
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 70.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.clickable(onClick = {
                                    createWorkoutViewModel.addSetToExercise(catalogId)
                                }))
                                Icon(painter = painterResource(R.drawable.minusicon128), contentDescription = null, tint = Color.Red, modifier = Modifier.clickable(onClick = {
                                    createWorkoutViewModel.removeSetToExercise(catalogId,
                                        SetDraft(item.reps, item.sets, item.weight)
                                    )
                                })
                                    .size(30.dp)) }
                        }else {
                            Row() {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp).clickable(
                                        onClick = {
                                            onEditClick(setIndex, item.weight, item.weight.toInt())
                                        }
                                    )
                                )
                                Spacer(modifier = Modifier.size(15.dp))
                                Text(
                                    text = "Set ${setIndex + 1} x ${item.reps} reps • ${item.weight} kg",
                                    color = Color.White.copy(alpha = 0.65f),
                                    fontFamily = FontFamily(Font(R.font.lexendregular)),
                                    fontSize = 13.sp
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LongClickModalBottomChooseExercises(
    sheetState: SheetState,
    onDismiss: () -> Unit,      // Kapatma eylemi
    onDeleteClick: () -> Unit   // Silme eylemi
) {
    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
        modifier = Modifier.height(250.dp),
        containerColor = Color(0xFF1C2126)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Remove Workout",
                style = TextStyle(
                    fontSize = 20.sp,
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.lexendbold))
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Silme Butonu Örneği
            androidx.compose.material3.Button(
                onClick = onDeleteClick,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Delete", color = Color.White)
            }
        }
    }
}
