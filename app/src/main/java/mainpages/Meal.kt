package mainpages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.projectfitness.R
import navigation.NavigationBar

@Composable
fun Meal(navController: NavController) {
    var flagggg by remember { mutableStateOf(false) }
    var flagggg2 by remember { mutableStateOf(false) }
    var flagggg3 by remember { mutableStateOf(false) }
    var flagggg4 by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF181F26))
    )
    {
        Text(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 5.dp),
            text = "PROJECT FITNESS",
            fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
            color = Color(0xFFF1C40F),
            style = TextStyle(fontSize = 20.sp, letterSpacing = 10.sp)
        )

        Text(
            text = "SOON",
            modifier = Modifier.align(Alignment.Center),
            color = Color.White,
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                fontSize = 100.sp
            )
        )
        Text(
            text = "MEAL TIME",
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 150.dp),
            color = Color.White,
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                fontSize = 25.sp,
                letterSpacing = 10.sp
            )
        )
    }
    var indexs = 3
    NavigationBar(navController = navController, indexs, flagggg, flagggg2, flagggg3, flagggg4)
}

@Preview(showSystemUi = true)
@Composable
fun PreviewMeal() {
    Meal(navController = rememberNavController())
}