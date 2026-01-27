package homes.inside

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.projectfitness.R
import ui.mainpages.navigation.Screens
import viewmodel.ViewModelSave

@Composable
fun ProjectChallangesScreen(
    navController: NavController,
    viewModelSave: ViewModelSave
) {

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = { HomeTopBarProjectChallangesScreen(navController) },
        containerColor = Color(0xFF121417),
        bottomBar = {},
        floatingActionButton = {},
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                text = viewModelSave.challangesSelectedName.value,
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.lexendbold)),
                style = TextStyle(fontSize = 25.sp, textAlign = TextAlign.Center),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(horizontal = 20.dp)
            )

            Spacer(Modifier.size(25.dp))

            Text(
                text = "Difficulty Level",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.lexendbold)),
                style = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center)
            )

            Spacer(Modifier.size(30.dp))

            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                val challengeDifficulty = viewModelSave.challangesSelectedDifficulty.value
                val totalIcons = 5

                for (i in 1..totalIcons) {
                    val iconColor =
                        if (i <= challengeDifficulty) Color(0xFFF1C40F) else Color.White

                    Icon(
                        painter = painterResource(id = R.drawable.skull),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp),
                        tint = iconColor
                    )

                    if (i < totalIcons) Spacer(modifier = Modifier.size(10.dp))
                }
            }

            Spacer(Modifier.size(25.dp))

            Text(
                text = "Challange Detail",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.lexendbold)),
                style = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center)
            )

            Spacer(Modifier.size(25.dp))

            Text(
                text = "' ${viewModelSave.challangesSelectedDetail.value} '",
                color = Color.White.copy(alpha = 0.8f),
                fontFamily = FontFamily(Font(R.font.lexendsemibold)),
                style = TextStyle(fontSize = 15.sp, textAlign = TextAlign.Center),
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(Modifier.size(26.dp))

            // ✅ Liste alanı ekranın kalanını alsın
           /* LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                itemsIndexed(exercises) { index, ex ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF1C2126))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // ⚠️ Alan adını kendi entity’ne göre düzelt:
                        // ex.exerciseName veya ex.name hangisiyse onu kullan.
                        Text(
                            text = "${index+1} -  ${ex.name}", // <-- eğer sende 'name' ise bunu 'ex.name' yap
                            color = Color.White,
                            modifier = Modifier.weight(1f),
                            fontFamily = FontFamily(Font(R.font.lexendbold))
                        )
                        Text(
                            text = "${ex.sets}x${ex.reps}",
                            color = Color.White.copy(alpha = 0.7f),
                            fontFamily = FontFamily(Font(R.font.lexendsemibold))
                        )
                    }
                }
            }*/

            // ✅ Alttaki buton sabit dursun (Spacer(weight) yok)
            Button(
                onClick = { /* Beat challenge action */ },
                modifier = Modifier
                    .padding(bottom = 50.dp)
                    .width(160.dp)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFF1C40F)
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "BEAT CHALLANGE",
                    color = Color.Black,
                    fontFamily = FontFamily(Font(R.font.lexendbold)),
                    style = TextStyle(fontSize = 15.sp, textAlign = TextAlign.Center),
                )
            }
        }
    }
}

@Composable
private fun HomeTopBarProjectChallangesScreen(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navController.navigate(Screens.Home.route) }) {
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
                tint = Color.White
            )
        }
    }
}
