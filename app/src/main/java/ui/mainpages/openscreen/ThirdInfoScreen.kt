package ui.mainpages.openscreen

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.projectfitness.R
import ui.mainpages.other.Show
import ui.mainpages.navigation.Screens

@Composable
fun ThirdInfo(navController: NavController) {

    val show = Show()
    val context : Context = LocalContext.current
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF181F26)),
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.drawable.thirdinfo),
            contentDescription = null,
            alpha = 0.3f,
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔼 ÜST ALAN
            Image(
                modifier = Modifier
                    .padding(top = 50.dp),
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "TRACK YOUR NUTRITION",
                color = colorResource(id = R.color.projectfitnessyellow),
                fontSize = 35.sp,
                fontFamily = FontFamily(Font(R.font.lexendregular)),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier
                .size(50.dp))
            Text(
                text = "Track your nutrition and water intakes",
                color = Color.White,
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.lexendextralight)),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 15.dp)
            )
            // 🔽 ALT ALAN
            Spacer(modifier = Modifier.weight(1f))
            Canvas(modifier = Modifier) {
            drawCircle(color = Color.White, radius = 10f,center = Offset(size.width/2-40,size.height-220))
            drawCircle(color = Color.White, radius = 10f,center = Offset(size.width/2,size.height-220))
            drawCircle(color = Color(0xFFF1C40F), radius = 10f,center = Offset(size.width/2+40,size.height-220))
        }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "GO",
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .clickable(onClick = {
                        show.setPageShownFlag(context,true)
                        navController.navigate(Screens.LoginScreen.route)}),
                fontSize = 25.sp,
                color = Color(0xFFF1C40F),
                fontFamily = FontFamily(Font(R.font.lexendsemibold)),
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(32.dp))
    }
    }
}