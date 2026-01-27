package ui.mainpages.openscreen
import android.annotation.SuppressLint
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.projectfitness.R
import com.google.firebase.auth.FirebaseAuth
import ui.mainpages.navigation.Screens
import ui.mainpages.other.Show


@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun Info(navController: NavController) {

    val show = Show()
    val context : Context = LocalContext.current

    if (!show.isPageAlreadyShown(context)){
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xFF181F26)),
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = R.drawable.info),
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
                    text = "TRACK YOUR WORKOUT",
                    color = colorResource(id = R.color.projectfitnessyellow),
                    fontSize = 35.sp,
                    fontFamily = FontFamily(Font(R.font.lexendregular)),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier
                    .size(50.dp))
                Text(
                    text = "Track and save your all exercises in this app",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.lexendextralight)),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 45.dp)
                )

                // 🔽 ALT ALAN
                Spacer(modifier = Modifier.weight(1f))

                Canvas(modifier = Modifier) {
                    drawCircle(color = Color(0xFFF1C40F), radius = 10f,center = Offset(size.width/2-40,size.height-220))
                    drawCircle(color = Color.White, radius = 10f,center = Offset(size.width/2,size.height-220))
                    drawCircle(color = Color.White, radius = 10f,center = Offset(size.width/2+40,size.height-220))
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Next",
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .clickable {
                        navController.navigate(Screens.SecondInfoScreen.route)
                    },
                    fontSize = 20.sp,
                    color = Color(0xFFF1C40F),
                    fontFamily = FontFamily(Font(R.font.lexendsemibold))
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
    else if (show.isPageAlreadyShown(context)){
        LaunchedEffect(Unit) {
            val user = FirebaseAuth.getInstance().currentUser

            if (user != null) {
                navController.navigate(Screens.Home.route) {
                    popUpTo(Screens.FirstInfoScreen.route) {inclusive = true}
                }
            }
            else {
                navController.navigate(Screens.LoginScreen.route)
            }
        }
    }
}