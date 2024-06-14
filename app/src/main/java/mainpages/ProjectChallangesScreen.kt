
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.projectfitness.R
import database.ProjectFitnessContainer
import viewmodel.ViewModelSave


@Composable
fun ProjectChallangesScreen(navController: NavController , viewModelSave: ViewModelSave){
    var configuration = LocalConfiguration.current
    var screenheightDp = configuration.screenHeightDp

    //Database Creation*************************************************************************************************************************************************************

    val context = LocalContext.current
    val scopes = rememberCoroutineScope()
    var projectFitnessContainer = ProjectFitnessContainer(context)
    val itemRepo = projectFitnessContainer.itemsRepository

    //******************************************************************************************************************************************************************************

    Box(modifier = Modifier
        .background(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFF1C40F), Color(0xFF181F26)),
                start = Offset(0f, 0f),
                end = Offset(0f, screenheightDp.toFloat())
            )
        )
        .fillMaxSize()){


        Column(Modifier.align(Alignment.TopCenter)) {

            Text(
                text = "CHALLANGES",
                color = Color(0xFF181F26),
                fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                style = TextStyle(fontSize = 25.sp, letterSpacing = 20.sp , textAlign = TextAlign.Center),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier
                .size(15.dp)
                .align(Alignment.CenterHorizontally))
            Text(
                text = "Difficulty Level",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                style = TextStyle(fontSize = 20.sp, letterSpacing = 10.sp , textAlign = TextAlign.Center),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier
                .size(10.dp)
                .align(Alignment.CenterHorizontally))
            Row( modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                val challengeDifficulty = viewModelSave.challangesSelectedDifficulty.value
                val totalIcons = 5

                for (i in 1..totalIcons) {
                    val iconColor = if (i <= challengeDifficulty) Color(0xFFF1C40F) else Color.White

                    Icon(
                        painter = painterResource(id = R.drawable.skull),
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp),
                        tint = iconColor
                    )

                    if (i < totalIcons) {
                        Spacer(modifier = Modifier.size(20.dp))
                    }
                }
            }
            Spacer(modifier = Modifier
                .size(10.dp)
                .align(Alignment.CenterHorizontally))
            Text(
                text = "Challange Detail",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                style = TextStyle(fontSize = 20.sp, letterSpacing = 10.sp , textAlign = TextAlign.Center),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier
                .size(10.dp)
                .align(Alignment.CenterHorizontally))
            Text(
                text = "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
                color = Color(0xFF181F26),
                fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                style = TextStyle(fontSize = 15.sp, letterSpacing = 5.sp , textAlign = TextAlign.Center),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(start = 5.dp, end = 5.dp)
            )
        }

        Column(modifier = Modifier
            .align(Alignment.BottomCenter)
            .background(Color.Transparent)
            .fillMaxWidth()
            .height(420.dp)) {

            Text(
                text = viewModelSave.challangesSelectedName.value,
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                style = TextStyle(fontSize = 25.sp, letterSpacing = 3.sp , textAlign = TextAlign.Center),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
            // Lazy Column Place
            Button(onClick = {  } , modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 320.dp)
                .width(160.dp)
                .height(40.dp),
                colors = androidx.compose.material.ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF10F0F)),
                contentPadding = PaddingValues(0.dp)
                ) {
                Text(
                    text = "BEAT CHALLANGE",
                    color = Color.Black,
                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                    style = TextStyle(fontSize = 20.sp, letterSpacing = 0.sp , textAlign = TextAlign.Center),
                )
            }
        }
    }
}

@Preview(name = "phone", device = "spec:shape=Normal,width=360,height=720,unit=dp,dpi=402")
@Composable
fun PreviewChallangesScreen(){
    ProjectChallangesScreen(navController = rememberNavController() , viewModelSave = ViewModelSave())
}