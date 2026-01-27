package ui.mainpages.inside

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.projectfitness.R
import com.example.projectfitness.data.local.entity.SetEntity
import com.example.projectfitness.data.local.viewmodel.WorkoutSettingViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import viewmodel.ViewModelSave
import kotlin.collections.forEachIndexed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutSettingScreen(navController: NavController, viewModelSave: ViewModelSave, workoutSettingViewModel: WorkoutSettingViewModel) {


    //Database Creation*************************************************************************************************************************************************************
    val context = LocalContext.current
    val lazyListState = rememberLazyListState()
    val selectedWorkout by workoutSettingViewModel.workoutFlow.collectAsState(null)

    val totalIcons = 5

    // UI Coding ****************************************************************************************************************************************************************************

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            HomeTopBarWorkoutSettingScreen(navController)
        },
        containerColor = Color(0xFF121417),
        bottomBar = {},
        floatingActionButton = {ExtendedStartButton { //navigate start workout
        }},
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Spacer(
                modifier = Modifier
                    .padding(top = 30.dp)
            )

            Text(
                "Workout Overview",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.lexendbold)),
                fontSize = 30.sp)
            Spacer(
                modifier = Modifier
                    .padding(top = 30.dp)
            )
            if (selectedWorkout==null){
                Text(
                    "Loading",
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                    fontSize = 30.sp)
            } else {
                Text(
                    selectedWorkout!!.workout.workoutName,
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                    fontSize = 30.sp)
                Spacer(Modifier.size(20.dp))
                Row(
                    modifier = Modifier
                        .padding(bottom = 30.dp)
                ) {
                    for (i in 1..totalIcons) {
                        val iconColor =
                            if (i <= selectedWorkout!!.workout.workoutRating) Color(0xFFF1C40F) else Color.White

                        Icon(
                            painter = painterResource(id = R.drawable.skull),
                            contentDescription = null,
                            modifier = Modifier
                                .size(20.dp),
                            tint = iconColor
                        )

                        if (i < totalIcons) {
                            Spacer(modifier = Modifier.size(10.dp))
                        }
                    }
                }
            }
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
            ){
                if (selectedWorkout?.exercises?.isNotEmpty() == true) {
                    itemsIndexed(selectedWorkout!!.exercises) {index, item ->
                        ExerciseItem(item.exercise.exerciseName, index, item.sets)
                        Spacer(Modifier.size(10.dp))
                    }
                } else {

                }
            }

}
    }
}

@Composable
fun ExtendedStartButton(onConfirmClick: () -> Unit) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    ExtendedFloatingActionButton(
        onClick = {
            if (!expanded) {
                expanded = true
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    onConfirmClick()
                    delay(500)
                    expanded = false
                }
            }
        },
        icon = {
            Icon(painter = painterResource(R.drawable.localfiredepartmenticon128), "Extended workout start button", Modifier.size(40.dp))
        },
        text = {
            Text("Start Workout", style = TextStyle(fontFamily = FontFamily(Font(R.font.lexendbold)), fontSize = 18.sp))
        },
        containerColor = Color(0xFFF1C40F),
        expanded = expanded,
        modifier = Modifier
            .padding(bottom = 50.dp)
    )
}

@Composable
fun ExerciseItem(
    exerciseName: String,
    index: Int,
    exerciseSet: List<SetEntity>
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
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
                    Text(
                        text = "Henüz set yok",
                        color = Color.White.copy(alpha = 0.5f),
                        fontFamily = FontFamily(Font(R.font.lexendregular)),
                        fontSize = 13.sp
                    )
                } else {
                    exerciseSet.forEachIndexed { setIndex, item ->
                        Text(
                            text = "Set ${setIndex + 1} x ${item.reps} reps • ${item.weight} kg",
                            color = Color.White.copy(alpha = 0.65f),
                            fontFamily = FontFamily(Font(R.font.lexendregular)),
                            fontSize = 13.sp
                        )
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}



    @Composable
    private fun HomeTopBarWorkoutSettingScreen(navController: NavController) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = { navController.navigate("home") }) {
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
        }}