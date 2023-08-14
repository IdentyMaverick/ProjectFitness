import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.projectfitness.R
import com.example.projectfitness.Screens

@Composable
fun SecondInfo(navController: NavController){
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF181F26)), ) {
        Image(painter = painterResource(id = R.drawable.secondinfo), contentDescription =null, alpha = 0.3f, contentScale = ContentScale.FillHeight, modifier = Modifier.size(800.dp) )
        Text(text = "SHOW YOUR STRENGTH",color = colorResource(id = R.color.projectfitnessyellow), modifier = Modifier
            .align(
                Alignment.Center
            )
            .padding(top = 125.dp, start = 70.dp, end = 70.dp), fontSize = 35.sp, fontFamily = FontFamily(
            Font(R.font.poppinsregulartext)
        ), textAlign = TextAlign.Center)
        Text(text = "Compete other users",color = Color.White, modifier = Modifier
            .align(
                Alignment.Center
            )
            .padding(top = 330.dp, start = 55.dp, end = 55.dp), fontSize = 20.sp, fontFamily = FontFamily(
            Font(R.font.poppinsregulartext)
        ), textAlign = TextAlign.Center)
        Canvas(modifier = Modifier.fillMaxSize().align(Alignment.BottomCenter)) {
            drawCircle(color = Color.White, radius = 10f,center = Offset(size.width/2-40,size.height-220))
            drawCircle(color = Color(0xFFF1C40F), radius = 10f,center = Offset(size.width/2,size.height-220))
            drawCircle(color = Color.White, radius = 10f,center = Offset(size.width/2+40,size.height-220))
        }
        Text(
            text = "Next ->",
            Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 45.dp, end = 40.dp)
                .clickable { navController.navigate(Screens.ThirdInfoScreen.route) },
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            color = Color(0xFFF1C40F)
        )
    }
}
@Preview(name = "phone", device = "spec:shape=Normal,width=360,height=720,unit=dp,dpi=402")
@Composable
fun PreviewSecondInfoScreen() {
    SecondInfo(navController = rememberNavController())
}