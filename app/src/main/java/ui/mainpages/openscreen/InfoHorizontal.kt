package ui.mainpages.openscreen

import androidx.annotation.Keep

@Keep
data class InfoHorizontal(
    val title: String,
    val description: String,
    val verticalText: String,
    val imagesRes: Int
)
