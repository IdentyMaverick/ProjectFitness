import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import database.ProjectFitnessChallanges
import database.ProjectFitnessContainer
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun InsertProjectChallange(){

    val context = LocalContext.current
    val scopes = rememberCoroutineScope()
    var projectFitnessContainer = ProjectFitnessContainer(context)
    val itemRepo = projectFitnessContainer.itemsRepository

    scopes.launch {
        itemRepo.insertProjectChallanges(ProjectFitnessChallanges(challangeName = "Pushup Extreme" , challangePhoto = 0 , challangeDifficulty = 3))
        itemRepo.insertProjectChallanges(ProjectFitnessChallanges(challangeName = "Pushup Extreme2" , challangePhoto = 0 , challangeDifficulty = 2))
        itemRepo.insertProjectChallanges(ProjectFitnessChallanges(challangeName = "Pushup Extreme3" , challangePhoto = 0 , challangeDifficulty = 1))
    }
}