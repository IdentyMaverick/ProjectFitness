import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.projectfitness.R
import com.example.projectfitness.Screens

@Composable
fun ThirdInfo(navController: NavController) {

    Box(
        Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.projectfitnessblue)),
    ) {
        Image(
            painter = painterResource(id = R.drawable.thirdinfo),
            contentDescription = null,
            alpha = 0.3f,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier.size(800.dp)
        )
        Text(
            text = "TRACK YOUR NUTRITION",
            color = colorResource(id = R.color.projectfitnessyellow),
            modifier = Modifier
                .align(
                    Alignment.Center
                )
                .padding(top = 125.dp, start = 70.dp, end = 70.dp),
            fontSize = 35.sp,
            fontFamily = FontFamily(
                Font(R.font.poppinsregulartext)
            ),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Track your nutrition and water intakes",
            color = Color.White,
            modifier = Modifier
                .align(
                    Alignment.Center
                )
                .padding(top = 330.dp, start = 55.dp, end = 55.dp),
            fontSize = 20.sp,
            fontFamily = FontFamily(
                Font(R.font.poppinsregulartext)
            ),
            textAlign = TextAlign.Center
        )
        Button(
            onClick = { navController.navigate(Screens.LoginScreen.route)},
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 40.dp, bottom = 40.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1C40F))
        ) {
            Text(text = "GO", color = Color.Black)
        }
    }
}