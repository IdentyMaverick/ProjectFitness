
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.projectfitness.R
import com.example.projectfitness.Screens

@Composable
fun RegisterScreen(navController: NavController) {
    val scaleMultiplier = 0.5f
    Box(
        Modifier
            .fillMaxSize()
            .background(color = Color(0xFF181F26))
    ) {
        Image(
            painter = painterResource(id = R.drawable.login),
            contentDescription = null,
            alpha = 0.3f,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier.size(800.dp)
        )
        Text(
            text = "PROJECT FITNESS",
            color = colorResource(id = R.color.projectfitnessyellow),
            modifier = Modifier
                .align(
                    Alignment.Center
                )
                .padding(bottom = 400.dp, start = 20.dp, end = 20.dp),
            fontSize = 35.sp,
            fontFamily = FontFamily(
                Font(R.font.poppinsboldtext)
            ),
            textAlign = TextAlign.Center
        )
        Row(
            Modifier
                .align(Alignment.Center)
                .padding(bottom = 260.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.6f)), shape = RoundedCornerShape(10.dp),
                onClick = { /*TODO*/ }
            ) {
                Text(
                    text = "Register with Google account",
                    color = Color(0xFF2C3E50),
                    modifier = Modifier.padding(end=5.dp),
                    fontSize = 15.sp,
                    fontFamily = FontFamily(
                        Font(R.font.poppinslighttext)
                    ),
                    textAlign = TextAlign.Center
                )
                Image(painterResource(id = R.drawable.google), contentDescription = null)
            }
        }
        Box(
            modifier = Modifier
                .size(430.dp)
                .padding(bottom = 30.dp, start = 30.dp, end = 30.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.6f))
                .align(Alignment.BottomCenter)
        ) {

            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                var name = remember { mutableStateOf("") }
                Text(
                    text = "Full Name",
                    fontFamily = FontFamily(Font(R.font.poppinsregulartext)),
                    modifier = Modifier.padding(top = 30.dp, end = 150.dp)
                )
                Spacer(modifier = Modifier.size(5.dp))
                BasicTextField(
                    value = name.value,
                    onValueChange = { name.value = it },
                    modifier = Modifier
                        .height(40.dp)
                        .width(270.dp)
                        .background(Color(0xFF2C3E50), shape = RoundedCornerShape(10.dp)),
                    maxLines = 1,
                    textStyle = TextStyle(
                        fontSize = 15.sp,
                        fontFamily = FontFamily(Font(R.font.poppinslighttext)),
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        textDecoration = TextDecoration.LineThrough
                    )

                )

                var emailText = remember { mutableStateOf("") }
                Text(
                    text = "E-Mail Address",
                    fontFamily = FontFamily(Font(R.font.poppinsregulartext)),
                    modifier = Modifier.padding(top = 5.dp, end = 120.dp)
                )
                Spacer(modifier = Modifier.size(5.dp))
                BasicTextField(
                    value = emailText.value,
                    onValueChange = { emailText.value = it },
                    modifier = Modifier
                        .height(40.dp)
                        .width(270.dp)
                        .background(Color(0xFF2C3E50), shape = RoundedCornerShape(10.dp)),
                    maxLines = 1,
                    textStyle = TextStyle(
                        fontSize = 15.sp,
                        fontFamily = FontFamily(Font(R.font.poppinslighttext)),
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        textDecoration = TextDecoration.LineThrough
                    )

                )
                var password = remember { mutableStateOf("") }
                Text(
                    text = "Password", fontFamily = FontFamily(Font(R.font.poppinsregulartext)),
                    modifier = Modifier.padding(top = 5.dp, end = 150.dp)
                )
                Spacer(modifier = Modifier.size(5.dp))
                BasicTextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    modifier = Modifier
                        .height(40.dp)
                        .width(270.dp)
                        .background(Color(0xFF2C3E50), shape = RoundedCornerShape(10.dp)),
                    maxLines = 1,
                    textStyle = TextStyle(
                        fontSize = 15.sp,
                        fontFamily = FontFamily(Font(R.font.poppinslighttext)),
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        textDecoration = TextDecoration.LineThrough
                    )

                )
                Spacer(modifier = Modifier.size(15.dp))
                Button(
                    onClick = { /*TODO*/ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2C3E50),
                        contentColor = Color(0xFFF1C40F)
                    ), shape = RoundedCornerShape(5.dp)
                ) {
                    Text(text = "Sign-up")
                }
                val annotedText = buildAnnotatedString {
                    append("Do you already have an account ?")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(" Sign-in")
                    }
                }
                ClickableText(text = annotedText, onClick = {navController.navigate(Screens.LoginScreen.route)} )
            }
        }

    }
}
@Preview(name = "phone", device = "spec:shape=Normal,width=360,height=720,unit=dp,dpi=402")
@Composable
fun PreviewRegisterScreen(){
    RegisterScreen(navController = rememberNavController())
}