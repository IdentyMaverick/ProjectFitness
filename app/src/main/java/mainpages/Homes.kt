package mainpages

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
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
import com.example.projectfitness.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage
import database.Exercise
import database.ProjectFitnessContainer
import database.ProjectFitnessExerciseEntity
import database.ProjectFitnessWorkoutEntity
import database.SetRep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import navigation.NavigationBar
import navigation.Screens
import showNotificationChann
import viewmodel.ProjectFitnessViewModel
import viewmodel.ViewModelProfile
import viewmodel.ViewModelSave


@SuppressLint("StateFlowValueCalledInComposition", "CoroutineCreationDuringComposition",
    "UnrememberedMutableState", "UnrememberedMutableInteractionSource"
)
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Home(navController: NavController,viewModelSave: ViewModelSave , viewModel: ProjectFitnessViewModel ,viewModelProfile: ViewModelProfile) {
        Log.d("count","tekrar")
        // Database Creation
        val context = LocalContext.current
        val db = remember { FirebaseFirestore.getInstance() }
        val uid = remember { Firebase.auth.currentUser?.uid }
        // Coroutine Scope
        val scopes = rememberCoroutineScope()
        // ProjectFitnessContainer
        val projectFitnessContainer = remember { ProjectFitnessContainer(context) }
        val itemRepo = projectFitnessContainer.itemsRepository
        // State management
        //val itemsState by itemRepo.getAllItemsStream().collectAsState(initial = emptyList())
        val user = itemRepo.getProjectFitnessUser().collectAsState(initial = emptyList()).value
        // SharedPreferences
        val sharedPreferences = remember { context.getSharedPreferences("rememberbuttonStatus", Context.MODE_PRIVATE) }
        val sharedPreferences2 = remember { context.getSharedPreferences("workoutIdNumber", Context.MODE_PRIVATE) }
        val workoutIdNumber by remember { mutableIntStateOf(sharedPreferences2.getInt("number", 2)) }
        //var exerciseIdNumber by remember { mutableStateOf(sharedPreferences2.getInt("number3", 2)) }
        var rememberMeBoo by remember { mutableStateOf(sharedPreferences.getBoolean("bool", false)) }
        var imageUrl by remember { mutableStateOf<String?>(null) }
        val storageRef = remember { Firebase.storage.reference }
        val profileRef = remember { storageRef.child("gs://projectfitness-ddfeb.appspot.com/profile_photos/$uid/profile.jpg") }


    LaunchedEffect(Unit) {
        profileRef.downloadUrl.addOnSuccessListener { uri ->
            imageUrl = uri.toString()
            viewModelProfile.selectedImageUri.value = imageUrl
            Log.d("Firebase", "Success to download image URL")
        }.addOnFailureListener { exception ->
            Log.e("Firebase", "Failed to download image URL", exception)
        }
    }
    Log.d("Firebases", viewModelProfile.selectedImageUri.value.toString())

        //******************************************************************************************************************************************************************************

        // Variable Initialization
        val configuration = LocalConfiguration.current
        //val screenwidthDp = configuration.screenWidthDp
        val screenheightDp = configuration.screenHeightDp
        viewModel.loadDataFromFirestore()

        // State management for flags and bottom sheet states
        val flag by remember { mutableStateOf(true) }
        val flag2 by remember { mutableStateOf(false) }
        val flag3 by remember { mutableStateOf(false) }
        val flag4 by remember { mutableStateOf(false) }

        val sheetState = rememberModalBottomSheetState()
        val showWorkoutRemoveMenuState = rememberModalBottomSheetState()
        var showBottomSheet by remember { mutableStateOf(true) }
        var showWorkoutRemoveMenu by remember { mutableStateOf(true) }
        val exercises = viewModelSave.exercises
        val name = viewModelSave.name
        val fl = viewModelSave.flag
        val notworkoutPressed = viewModelSave.allowed

        viewModelSave.exercisesForWorkouts2.clear()

        // Interaction states
        var isPressed by remember { mutableStateOf(false) }
        var clicked by remember { mutableStateOf(false) }
        //var clicked2 by remember { mutableStateOf(false) }
        var clicked3 by remember { mutableStateOf(false) }
        var clicked4 by remember { mutableStateOf(false) }
        // UI Coding ****************************************************************************************************************************************************************************

        // Firebase user data
        var firebaseUserWorkoutList by remember { mutableStateOf<List<ProjectFitnessWorkoutEntity>?>(null) }
        var firebaseUserExerciseList by remember { mutableStateOf<List<ProjectFitnessExerciseEntity>?>(null) }

    LaunchedEffect(Unit) {
        val fetchedWorkoutList = mutableListOf<ProjectFitnessWorkoutEntity>()
        val fetchedExerciseList = mutableListOf<ProjectFitnessExerciseEntity>()
        db.collection("Workouts").document(uid ?: "").collection("HasWorkout").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {

                    val exercisesList = document.get("exercises") as? List<Map<String, Any>> ?: emptyList()
                    val exercisese = exercisesList.map { exerciseMap ->
                        Exercise(
                            name = exerciseMap["name"] as? String ?: "",
                            sets = (exerciseMap["sets"] as? Long)?.toInt() ?: 0,
                            reps = (exerciseMap["reps"] as? Long)?.toInt() ?: 0
                        )
                    }.toMutableList()

                    val firebaseuserworkoutlist = ProjectFitnessWorkoutEntity(
                        document.getLong("workoutid")?.toInt() ?: 0,
                        document.getString("workoutname") ?: "",
                        exercises = exercisese
                    )
                    fetchedWorkoutList.add(firebaseuserworkoutlist)
                    val exerciseCollectionRef = db.collection("Workouts").document(uid ?: "")
                        .collection("HasWorkout").document("${document.getLong("workoutid")?.toInt() ?: 0}")
                        .collection("HasExercise")
                    exerciseCollectionRef.get()
                        .addOnSuccessListener { documentse ->
                            Log.d("document list", documents.isEmpty.toString())
                            for (documente in documentse) {
                                val exerciseList = documente.get("setrepList") as? List<Map<String, Any>> ?: emptyList()
                                val exercise = exerciseList.map { exercisesMap ->
                                    SetRep(
                                        setNumber = exercisesMap["setNumber"] as? String ?: "" ,
                                        setRep = exercisesMap["setRep"] as? Int ?: 0 ,
                                        ticked = exercisesMap["ticked"] as? Boolean ?: false ,
                                        weight = exercisesMap["weight"] as? Float ?: 0.0f
                                    )
                                }.toMutableList()
                                val firebaseuserexerciselist = ProjectFitnessExerciseEntity(
                                    exerciseId = documente.getLong("exerciseid")?.toInt() ?: 0 ,
                                    exercisesName = documente.getString("exercisesname") ?: "" ,
                                    exercisesRep = documente.getLong("exercisesRep")?.toInt() ?: 0 ,
                                    exercisesSet = documente.getLong("exercisesSet")?.toInt() ?: 0 ,
                                    setrepList = exercise
                                )
                                scopes.launch {
                                    withContext(Dispatchers.IO) {
                                        val workoutId = document.getLong("workoutid")?.toInt() ?: 0
                                        Log.d("workout id: ", workoutId.toString())
                                        val exerciseList = itemRepo.getExerciseList(workoutId)
                                        Log.d("firebaseuserexerciselist: ", firebaseuserexerciselist.toString())

                                        // Eğer liste boşsa direkt ekleme yap
                                        if (exerciseList.isNullOrEmpty()) {
                                            Log.d("worksss", firebaseuserexerciselist.toString())
                                            itemRepo.insertItems(firebaseuserexerciselist)
                                        } else {
                                            // Liste boş değilse kontrol et
                                            var exists = false
                                            for (item in exerciseList) {
                                                if (item?.setrepList == firebaseuserexerciselist.setrepList) {
                                                    exists = true
                                                    break
                                                }
                                            }

                                            if (!exists) {
                                                Log.d("worksss", firebaseuserexerciselist.toString())
                                                itemRepo.insertItems(firebaseuserexerciselist)
                                            } else {
                                                Log.d("notworksss", firebaseuserexerciselist.toString())
                                            }
                                        }
                                    }
                                }


                                fetchedExerciseList.add(firebaseuserexerciselist)
                            }
                            firebaseUserExerciseList = fetchedExerciseList
                            Log.d("list is" , firebaseUserExerciseList.toString())
                        }.addOnFailureListener { exception ->
                            Log.e("failed", "Failed to fetch exercise list", exception)
                            firebaseUserExerciseList = emptyList() // veya null, hata durumuna göre ayarlayabilirsiniz
                        }
                }
                firebaseUserWorkoutList = fetchedWorkoutList
                Log.d("success 3",  firebaseUserWorkoutList.toString())
            }.addOnFailureListener { exception ->
                Log.e("Firebase", "Failed to fetch workout list", exception)
                firebaseUserWorkoutList = emptyList() // veya null, hata durumuna göre ayarlayabilirsiniz
            }
        //*******************

    }
    Log.d("Homes / WorkoutIdNumber / Not Clicked ",workoutIdNumber.toString())
    Box( // Ana arkaplan
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFF1C40F), Color(0xFF181F26)),
                        start = Offset(0f, 0f),
                        end = Offset(0f, screenheightDp.toFloat())
                    )
                )

        ) {
                showNotificationChann(context)
                Row(Modifier.align(Alignment.TopCenter)) {

                    Text(
                    text = "PROJECT FITNESS ",
                        color = Color(0xFF181F26),
                        fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                        style = TextStyle(fontSize = 25.sp, letterSpacing = 3.sp),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 10.dp, top = 5.dp)
                    )
                    //Believe in yourself, and you will be unstoppable // Slogan

                    IconButton(
                        onClick = {
                            clicked3 = true
                            navController.navigate("profile")
                        }, modifier = Modifier

                    ) {
                        Icon(
                            painterResource(id = if (clicked3){R.drawable.accountcirclefilled}else{R.drawable.accountcircle}),
                            contentDescription = null,
                            modifier = Modifier
                                .width(25.dp)
                                .height(25.dp),
                            tint = Color(0xFF181F26)
                        )

                    }

                    IconButton(
                        onClick = { clicked4 = true
                            navController.navigate("profile")
                                  }, modifier = Modifier

                    ) {
                        Icon(
                            painterResource(id = if (clicked4){R.drawable.circlenotificationsfilled}else{R.drawable.circlenotifications}),
                            contentDescription = null,
                            modifier = Modifier
                                .width(25.dp)
                                .height(25.dp),
                            tint = Color(0xFF181F26)
                        )

                    }

                    IconButton(
                        onClick = {
                            Log.d("CLICKED", "clicked")
                            showBottomSheet = true
                        }, modifier = Modifier

                    ) {
                        Icon(
                            painterResource(id = R.drawable.projectfitnesspointheavy),
                            contentDescription = null,
                            modifier = Modifier
                                .width(25.dp)
                                .height(25.dp),
                            tint = Color(0xFF181F26)
                        )
                    }
                }

                if (showBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showBottomSheet = false },
                        sheetState = sheetState,
                        containerColor = Color(0xFF283747)
                    ) {
                        LaunchedEffect(Unit) {
                            scopes.launch { sheetState.expand() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showBottomSheet = false
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(77.dp)
                        )
                        {
                            Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                                Button(
                                    onClick = {
                                        scopes.launch {
                                            itemRepo.deleteProjectUser(user[0])
                                            rememberMeBoo = false
                                            val editor = sharedPreferences.edit()
                                            editor.putBoolean("bool",rememberMeBoo)
                                            editor.apply()
                                            navController.navigate(Screens.LoginScreen.route)
                                        }
                                         },
                                    modifier = Modifier
                                        .align(Alignment.End)
                                        .padding(bottom = 25.dp)
                                        .fillMaxWidth()
                                        .height(60.dp),
                                    shape = RoundedCornerShape(0.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                                ) {
                                    Text(
                                        text = "Logout",
                                        style = TextStyle(
                                            fontSize = 25.sp,
                                            fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold))
                                        ),
                                        color = Color(0xFFF1C40F)
                                    )
                                }

                            }
                        }
                    }
                }

                if (showWorkoutRemoveMenu) {
                    ModalBottomSheet(
                        onDismissRequest = { showWorkoutRemoveMenu = false },
                        sheetState = showWorkoutRemoveMenuState,
                        containerColor = Color(0xFF283747)
                    ) {
                        LaunchedEffect(Unit) {
                            scopes.launch { showWorkoutRemoveMenuState.expand() }.invokeOnCompletion {
                                if (!showWorkoutRemoveMenuState.isVisible) {
                                    showWorkoutRemoveMenu = false
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(77.dp)
                        )
                        {
                            Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                                Button(
                                    onClick = {
                                        scopes.launch {
                                        itemRepo.deleteItem(viewModelSave.selectedWorkoutItem.value)
                                    }
                                        if(viewModelSave.workoutSize.value == 1){viewModelSave.workoutSize.value = 0}
                                        showWorkoutRemoveMenu = false},
                                    modifier = Modifier
                                        .align(Alignment.End)
                                        .padding(bottom = 25.dp)
                                        .fillMaxWidth()
                                        .height(60.dp),
                                    shape = RoundedCornerShape(0.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                                ) {
                                    Text(
                                        text = "Remove Workout",
                                        style = TextStyle(
                                            fontSize = 25.sp,
                                            fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold))
                                        ),
                                        color = Color(0xFFF1C40F)
                                    )
                                }

                            }
                        }
                    }
                }

                Text(text = "YOUR", style = TextStyle(fontSize = 70.sp, fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)), lineHeight = 55.sp),
                    color = Color(0xFF000000),
                    modifier = Modifier.padding(start = 25.dp, top = 50.dp),
                    )
            Text(text = "STATISTIC", style = TextStyle(fontSize = 70.sp, fontFamily = FontFamily(Font(R.font.postnobillscolombolight)), lineHeight = 55.sp),
                color = Color(0xFF000000),
                modifier = Modifier.padding(start = 25.dp, top = 105.dp),
            )

                Row(
                    Modifier
                        .align(Alignment.Center)
                        .padding(bottom = 270.dp)) {
                    firebaseUserWorkoutList?.let { workouts ->
                        Text(
                            text = workouts.size.toString(),
                            style = TextStyle(
                                fontSize = 50.sp,
                                fontFamily = FontFamily(Font(R.font.postnobillscolombolight)),
                                lineHeight = 14.sp
                            ),
                            color = Color(0xFF000000),
                            modifier = Modifier.padding(start = 25.dp),
                        )
                    }
                    Text(text = "Workouts\nYou Have", style = TextStyle(fontSize = 25.sp, fontFamily = FontFamily(Font(R.font.postnobillscolombolight)), lineHeight = 14.sp),
                        color = Color(0xFF000000),
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .align(Alignment.CenterVertically),
                    )
                    Spacer(modifier = Modifier.size(10.dp))

                    Text(text = viewModelSave.completedWorkoutSize.value.toString(), style = TextStyle(fontSize = 50.sp, fontFamily = FontFamily(Font(R.font.poppinsextralighttext)), lineHeight = 14.sp),
                        color = Color(0xFF000000),
                        modifier = Modifier.padding(start = 25.dp),
                    )
                    Text(text = "Workouts\nCompleted", style = TextStyle(fontSize = 25.sp, fontFamily = FontFamily(Font(R.font.postnobillscolombolight)), lineHeight = 14.sp),
                        color = Color(0xFF000000),
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .align(Alignment.CenterVertically),
                    )

                }

                Column(
                    Modifier
                        .background(Color.Transparent)
                        .padding(top = (screenheightDp / 3).dp)) {

                    Row(modifier = Modifier.background(Color.Transparent)) {
                        Text(
                            text = "Workouts",
                            style = TextStyle(
                                fontSize = 35.sp,
                                fontFamily = FontFamily(Font(R.font.postnobillscolombolight)),
                                letterSpacing = 15.sp
                            ),
                            modifier = Modifier
                                .align(Alignment.Top)
                                .padding(start = 30.dp),
                            color = Color.White.copy(alpha = 0.5f)
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        IconButton(
                            onClick = {
                                Log.d("Homes / WorkoutIdNumber / Clicked ",workoutIdNumber.toString())
                                clicked = true
                                navController.navigate(Screens.ChooseExercises.route)
                                fl.value = true
                                name.value = ""
                                exercises.clear()
                                notworkoutPressed.value = false

                                scopes.launch {
                                    itemRepo.insertItem(
                                        ProjectFitnessWorkoutEntity(
                                            workoutId = workoutIdNumber,
                                            workoutName = "",
                                            exercises = mutableStateListOf(Exercise("", 0, 0))
                                        )
                                    )
                                }

                            }, modifier = Modifier.padding(end = 30.dp)

                        ) {
                            Icon(
                                painterResource(id = if (clicked){R.drawable.addcirclefilled}else{R.drawable.addcircle}),
                                contentDescription = null,
                                modifier = Modifier
                                    .width(25.dp)
                                    .height(25.dp),
                                tint = Color(0xFFFFFFFF)
                            )
                        }
                    }

                        Spacer(modifier = Modifier.size(20.dp))

                    if (firebaseUserWorkoutList?.isEmpty() == true){
                        Text(
                            text = "Press + Button to Create Workout",
                            fontFamily = FontFamily(Font(R.font.postnobillscolombo)),
                            fontWeight = FontWeight.Bold,
                            style = TextStyle(letterSpacing = 1.sp, fontSize = 20.sp),
                            color = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier
                                .padding(top = 100.dp)
                                .align(Alignment.CenterHorizontally)

                        )
                    }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 20.dp, start = 20.dp, end = 20.dp),
                            state = LazyListState()
                        ) {
                            firebaseUserWorkoutList?.let { workouts ->
                                itemsIndexed(workouts) { index, item ->
                                    val editor2 = sharedPreferences2.edit()
                                    editor2.putInt("number",workouts.last().workoutId+1)
                                    editor2.apply()
                                    Log.d("idnumber 2", workoutIdNumber.toString())
                                    scopes.launch { itemRepo.insertItem(item) }
                                    viewModelSave.workoutSize.value = workouts.size
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight()
                                            .background(
                                                if (isPressed) {
                                                    Color.Red
                                                } else {
                                                    Color.Transparent
                                                }, shape = RoundedCornerShape(20.dp)
                                            )
                                            .pointerInput(Unit) {
                                                detectTapGestures(
                                                    onLongPress = {
                                                        isPressed = true
                                                        viewModelSave.selectedWorkoutItem.value =
                                                            item
                                                        try {
                                                            showWorkoutRemoveMenu = true
                                                        } finally {
                                                            isPressed = false
                                                        }
                                                    }
                                                )
                                            }
                                            .border(
                                                1.dp,
                                                shape = RoundedCornerShape(20.dp),
                                                color = Color(0xFF202B36)
                                            )
                                            .clickable(
                                                interactionSource = MutableInteractionSource(),
                                                indication = null,
                                                onClick = {
                                                    viewModelSave.selectedWorkoutName.value =
                                                        item.workoutName
                                                    navController.navigate("workoutsettingscreen")
                                                },
                                            )
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .padding(start = 0.dp)
                                                .width(90.dp)
                                                .height(100.dp)
                                                .clip(shape = RoundedCornerShape(20.dp))
                                                .background(
                                                    brush = Brush.linearGradient(
                                                        colors = listOf(
                                                            Color.Transparent,
                                                            Color(0xFF181F26)  // End color
                                                        ),
                                                        start = Offset.Infinite,
                                                        end = Offset(100.0f, 0.0f)
                                                    ),
                                                    shape = RoundedCornerShape(20.dp)
                                                )

                                        ) {
                                            when (index) {
                                                0 -> {
                                                    Image(
                                                        painterResource(id = R.drawable.secondinfo),
                                                        contentDescription = null,
                                                        contentScale = ContentScale.Crop,
                                                        alpha = 0.7f,
                                                        modifier = Modifier
                                                            .fillMaxSize(),
                                                    )
                                                }
                                                1 -> {
                                                    Image(
                                                        painterResource(id = R.drawable.login),
                                                        contentDescription = null,
                                                        contentScale = ContentScale.Crop,
                                                        alpha = 0.7f,
                                                        modifier = Modifier
                                                            .fillMaxSize(),
                                                    )
                                                }
                                                2 -> {
                                                    Image(
                                                        painterResource(id = R.drawable.gym),
                                                        contentDescription = null,
                                                        contentScale = ContentScale.Crop,
                                                        alpha = 0.7f,
                                                        modifier = Modifier
                                                            .fillMaxSize(),
                                                    )
                                                }
                                                3 -> {
                                                    Image(
                                                        painterResource(id = R.drawable.gymroomwith),
                                                        contentDescription = null,
                                                        contentScale = ContentScale.Crop,
                                                        alpha = 0.7f,
                                                        modifier = Modifier
                                                            .fillMaxSize(),
                                                    )
                                                }
                                                4 -> {
                                                    Image(
                                                        painterResource(id = R.drawable.gymroomwithgym),
                                                        contentDescription = null,
                                                        contentScale = ContentScale.Crop,
                                                        alpha = 0.7f,
                                                        modifier = Modifier
                                                            .fillMaxSize(),
                                                    )
                                                }
                                                5 -> {
                                                    Image(
                                                        painterResource(id = R.drawable.gymroomgym),
                                                        contentDescription = null,
                                                        contentScale = ContentScale.Crop,
                                                        alpha = 0.7f,
                                                        modifier = Modifier
                                                            .fillMaxSize(),
                                                    )
                                                }
                                                6 -> {
                                                    Image(
                                                        painterResource(id = R.drawable.login),
                                                        contentDescription = null,
                                                        contentScale = ContentScale.Crop,
                                                        alpha = 0.7f,
                                                        modifier = Modifier
                                                            .fillMaxSize(),
                                                    )
                                                }
                                            }
                                        }
                                        Column(
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                                .padding(end = 40.dp)
                                        ) {
                                            Row(modifier = Modifier) {
                                                Text(
                                                    text = "" + item.workoutName.uppercase() ,
                                                    modifier = Modifier
                                                        .padding(start = 110.dp, top = 20.dp),
                                                    fontSize = 25.sp,
                                                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                                    textAlign = TextAlign.Left,
                                                    color = Color(0xFFD9D9D9),
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(modifier = Modifier.weight(1f))
                                                Icon(painter = painterResource(id = R.drawable.playcircle),
                                                    contentDescription = null,
                                                    tint = Color(0xFFF1C40F),
                                                    modifier = Modifier
                                                        .padding(top = 30.dp)
                                                        .size(24.dp)
                                                        .clickable {
                                                            viewModelSave.selectedWorkoutName.value =
                                                                item.workoutName

                                                            navController.navigate("workoutsettingscreen")
                                                        }
                                                )
                                            }
                                            Text(
                                                text = item.exercises.size.toString() + " exercise",
                                                modifier = Modifier
                                                    .padding(start = 110.dp),
                                                fontSize = 15.sp,
                                                fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                                                textAlign = TextAlign.Left,
                                                color = Color(0xFFD9D9D9),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                    }
                                    Spacer(modifier = Modifier.size(30.dp))
                                }
                            }
                        }
                }
            val indexs = 0
            NavigationBar(navController = navController, indexs, flag, flag2, flag3, flag4)
        }
    }

