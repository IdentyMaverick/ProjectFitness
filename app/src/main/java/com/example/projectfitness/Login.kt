import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
fun LoginScreen(navController: NavController) {

    val scaleMultiplier = 0.5f
    Box(
        Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.projectfitnessblue))
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
                .padding(bottom = 200.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.6f)), shape = RoundedCornerShape(10.dp),
                onClick = { /*TODO*/ }
            ) {
                Text(
                    text = "Login with Google account",
                    color = Color(0xFF2C3E50),
                    modifier = Modifier.padding(end=5.dp),
                    fontSize = 17.sp,
                    fontFamily = FontFamily(
                        Font(R.font.poppinslighttext)
                    ),
                    textAlign = TextAlign.Center
                )
                Image(painterResource(id = R.drawable.google), contentDescription = null)
            }
        }
        Spacer(modifier = Modifier.size(20.dp))
        Box(
            modifier = Modifier
                .size(400.dp)
                .padding(bottom = 50.dp, start = 30.dp, end = 30.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.6f))
                .align(Alignment.BottomCenter)
        ) {

            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                var emailText = remember { mutableStateOf("") }
                Text(
                    text = "E-Mail Address",
                    fontFamily = FontFamily(Font(R.font.poppinsregulartext)),
                    modifier = Modifier.padding(top = 30.dp, end = 100.dp)
                )
                Spacer(modifier = Modifier.size(10.dp))
                OutlinedTextField(
                    value = emailText.value, onValueChange = { emailText.value = it }, maxLines = 1,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(
                            0xFF2C3E50
                        ), unfocusedContainerColor = Color(0xFF2C3E50)
                    ),
                    modifier = Modifier, shape = RoundedCornerShape(10.dp)
                )

                var password = remember { mutableStateOf("") }
                Text(
                    text = "Password", fontFamily = FontFamily(Font(R.font.poppinsregulartext)),
                    modifier = Modifier.padding(top = 5.dp, end = 135.dp)
                )
                Spacer(modifier = Modifier.size(5.dp))
                OutlinedTextField(
                    value = password.value, onValueChange = { password.value = it }, maxLines = 1,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(
                            0xFF2C3E50
                        ),
                        unfocusedContainerColor = Color(0xFF2C3E50),
                        cursorColor = Color(0xFFF1C40F)
                    ),
                    modifier = Modifier, shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.size(15.dp))
                //Checkbox(checked = false, onCheckedChange = { check(true) }, modifier = Modifier.scale(scaleMultiplier).padding(end = 320.dp))

                Text(text = "Forget Password ?",
                    fontSize = 13.sp,
                    textDecoration = TextDecoration.Underline,
                    fontFamily = FontFamily(Font(R.font.poppinsregulartext)),
                    modifier = Modifier
                        .padding(start = 100.dp)
                        .clickable { navController.navigate(Screens.LoginScreen.ForgetPasswordScreen.route) })
                Spacer(modifier = Modifier.size(20.dp))
                Button(
                    onClick = { navController.navigate(Screens.Home.route)},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2C3E50),
                        contentColor = Color(0xFFF1C40F)
                    ), shape = RoundedCornerShape(5.dp)
                ) {
                    Text(text = "Sign-in")
                }
                Spacer(modifier = Modifier.size(7.dp))
                val annotedText = buildAnnotatedString {
                    append("Don’t have an account yet ?")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(" Sign-up")
                    }
                }
                //Text(text = annotedText, modifier = Modifier.clickable { })
                ClickableText(text = annotedText, onClick = { offset ->
                    if (offset in 27..34) {
                        navController.navigate(Screens.LoginScreen.RegisterScreen.route)
                    }
                })

            }
        }

    }
}

@Preview(name = "phone", device = "spec:shape=Normal,width=360,height=720,unit=dp,dpi=402")
@Composable
fun PreviewLoginScreen() {
    LoginScreen(rememberNavController())
}