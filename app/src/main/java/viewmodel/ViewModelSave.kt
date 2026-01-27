package viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay

class ViewModelSave : ViewModel() {

    // -----------------------------
    // Create Workout
    // -----------------------------
    val name = mutableStateOf("")
    val exercises = mutableStateListOf<String?>()
    val setRepString = mutableStateListOf<String?>()
    val flag = mutableStateOf(true)
    var allowed = mutableStateOf(true)

    var set = mutableStateListOf("0", "0", "0", "0", "0", "0", "0", "0", "0")
    var reps = mutableStateListOf("0", "0", "0", "0", "0", "0", "0", "0", "0")

    var count = mutableStateOf(0)

    // Create Workout --> Exercise selected --> Workout Details --> ADD EXERCISES
    var selectedItemName = mutableStateOf("")
    var selectedItemUpdatedName = mutableStateOf("")

    // -----------------------------
    // Selected workout / completed
    // -----------------------------
    var selectedWorkoutName = mutableStateOf("")
    var selectedCompletedName = mutableStateOf("")


    var idFlag = mutableStateOf(1)
    var idFlag2 = mutableStateOf(3)

    var flagA = mutableStateOf(false)
    var flagB = mutableStateOf(false)
    var flagC = mutableStateOf(false)
    var flagD = mutableStateOf(false)

    var note = mutableStateOf("")
    var workoutRate = mutableStateOf(0)

    var workoutSize = mutableStateOf(0)
    var completedWorkoutId = mutableStateOf(1000)
    var completedWorkoutSize = mutableStateOf(0)

    var exerciseId = mutableStateOf(1)
    var exerciseIds = mutableStateOf(1)

    var totalSetsOfCompletedWorkout = mutableStateOf(0)
    var totalRepsOfCompletedWorkout = mutableStateOf(0)
    var totalWeightOfCompletedWorkout = mutableStateOf(0)

    var completedWorkoutTime = mutableStateOf("")
    var hours = mutableStateOf("")

    // -----------------------------
    // Timer
    // -----------------------------
    var hour = mutableStateOf("")
    var minutes = mutableStateOf("")
    var seconds = mutableStateOf("")

    var hourInt = mutableStateOf(0)
    var secondInt = mutableStateOf(0)
    var minuteInt = mutableStateOf(0)

    // -----------------------------
    // Selected workout item
    // -----------------------------

    var ticked = mutableStateOf(false)

    var readyBackFinisher = mutableListOf("Lat Pulldown", "Lat Pulldown 2", "Lat Pulldown 3")
    var coachProjectMain = mutableListOf("")

    var lastSet = mutableStateOf(0)
    var lastRep = mutableStateOf(0)

    // -----------------------------
    // Challenge selection (Senin mevcut)
    // -----------------------------
    var challangesSelectedIndex = mutableStateOf(0) // artık challengeId gibi kullan
    var challangesSelectedName = mutableStateOf("")
    var challangesSelectedDifficulty = mutableStateOf(0)
    var challangesSelectedDetail = mutableStateOf("")

    // ✅ Challenge detail ekranında kullanmak için:
    // (Room relation ile gelen challenge + exercises)

    // -----------------------------
    // Coach selection
    // -----------------------------
    var coachesSelectedIndex = mutableStateOf(0)
    var coachesSelectedName = mutableStateOf("")
    var coachesSelectedDifficulty = mutableStateOf(0)
    var coachesSelectedDetail = mutableStateOf("")

    // -----------------------------
    // Misc
    // -----------------------------
    var alertDialogSpecificExerciseName = mutableStateOf("")
    var pastExerciseVolume = mutableStateOf("")

    fun updateSelectedItemName(newName: String) {
        selectedItemName.value = newName
    }

    /**
     * ✅ Challenge details ekranında çağır:
     * repo.getChallengeWithExercises(challengeId) Flow'unu collect eder ve
     * selectedChallengeWithExercises state'ini günceller.
     *
     * Kullanım:
     * viewModelSave.CollectSelectedChallenge(repo)
     */

    @Composable
    fun RestTimer() {
        LaunchedEffect(key1 = secondInt.value) {
            while (hourInt.value >= 0) {
                delay(1000L)
                secondInt.value++

                if (minuteInt.value == 60) {
                    hourInt.value++
                    minuteInt.value = 0
                } else if (secondInt.value == 60) {
                    minuteInt.value++
                    secondInt.value = 0
                }
            }
        }

        hour.value = "%2dh ".format(hourInt.value)
        minutes.value = "%2dm ".format(minuteInt.value)
        seconds.value = "%2ds ".format(secondInt.value)
    }
}
