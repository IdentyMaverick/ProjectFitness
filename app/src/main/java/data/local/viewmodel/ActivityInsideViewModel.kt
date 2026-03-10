package data.local.viewmodel

import androidx.lifecycle.ViewModel
import com.grozzbear.projectfitness.data.local.entity.ExerciseCatalogEntity
import com.grozzbear.projectfitness.data.local.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ActivityInsideViewModel(
    repo: WorkoutRepository
) : ViewModel() {
    val _selectedCatalog = MutableStateFlow<ExerciseCatalogEntity>(ExerciseCatalogEntity())
    val selectedCatalog = _selectedCatalog.asStateFlow()


}
