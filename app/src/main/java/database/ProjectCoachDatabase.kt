import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import database.ProjectCoachEntity
import database.ProjectFitnessContainer
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun InsertProjectCoach(){

    val context = LocalContext.current
    val scopes = rememberCoroutineScope()
    var projectFitnessContainer = ProjectFitnessContainer(context)
    val itemRepo = projectFitnessContainer.itemsRepository

    scopes.launch {
        itemRepo.insertProjectCoach(ProjectCoachEntity(coachName = "Back Finisher" , coachPhoto = 0 , coachDifficulty = 5))
        itemRepo.insertProjectCoach(ProjectCoachEntity(coachName = "Back Finisher2" , coachPhoto = 0 , coachDifficulty = 5))
        itemRepo.insertProjectCoach(ProjectCoachEntity(coachName = "Back Finisher3" , coachPhoto = 0 , coachDifficulty = 5))
    }
}