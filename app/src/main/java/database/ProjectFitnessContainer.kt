package database

import ItemsRepository
import OfflineItemsRepository
import android.content.Context

class ProjectFitnessContainer(private val context: Context) {
    val itemsRepository: ItemsRepository by lazy {
        OfflineItemsRepository(ProjectFitnessRoom.getDatabase(context).projectFitnessDao())
    }
}