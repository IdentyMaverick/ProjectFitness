package loginscreens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.projectfitness.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import navigation.Screens

@Composable
fun ForgetPasswordScreen(navController: NavController) {
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
        Image(
            painter = painterResource(id = R.drawable.projectfitnesslogologin) ,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 420.dp, end = 235.dp)
                .size(50.dp)
        )
        Text(
            text = "PROJECT FITNESS",
            color = colorResource(id = R.color.projectfitnessyellow),
            modifier = Modifier
                .align(
                    Alignment.Center
                )
                .padding(bottom = 400.dp, start = 70.dp, end = 20.dp),
            fontFamily = FontFamily(
                Font(R.font.postnobillscolombosemibold)
            ),
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 25.sp, letterSpacing = 5.sp)
        )
        Text(
            text = "Forget Password ?",
            color = Color.White,
            modifier = Modifier
                .align(
                    Alignment.Center
                )
                .padding(bottom = 280.dp, start = 65.dp, end = 65.dp),
            fontSize = 25.sp,
            fontFamily = FontFamily(
                Font(R.font.postnobillscolombosemibold)
            ),
            textAlign = TextAlign.Center,
            style = TextStyle(letterSpacing = 3.sp)
        )
        val annotedText = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold,fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)), color = Color(0xFFF1C40F)),) {
                append("No worries,")
            }
            withStyle(style = SpanStyle(fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold))) )
            {
                append(" we’ll send you reset instruction")
            }

        }
        Text(
            text = annotedText, color = Color.White,
            modifier = Modifier
                .align(
                    Alignment.Center
                )
                .padding(bottom = 180.dp, start = 55.dp, end = 55.dp)
                , fontSize = 15.sp, fontFamily = FontFamily(Font(R.font.poppinslighttext)),
                textAlign = TextAlign.Center
        )
        Box(
            modifier = Modifier
                .size(400.dp)
                .padding(bottom = 150.dp, start = 30.dp, end = 30.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF182129).copy(alpha = 0.4f))
                .align(Alignment.BottomCenter)
        ) {

            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                var emailText = remember { mutableStateOf("") }
                Text(
                    text = "E-Mail Address",
                    fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                    modifier = Modifier.padding(top = 30.dp, end = 150.dp),
                    color = Color(0xFFD9D9D9),
                    style = TextStyle(letterSpacing = 3.sp),
                )
                Spacer(modifier = Modifier.size(10.dp))
                BasicTextField(
                    value = emailText.value,
                    onValueChange = { emailText.value = it },
                    modifier = Modifier
                        .height(40.dp)
                        .width(270.dp)
                        .background(Color(0xFF2C3E50), shape = RoundedCornerShape(10.dp)),
                    maxLines = 1,
                    textStyle = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.poppinslighttext)),
                        color = Color.White
                    ),
                    decorationBox = { innerTextField ->
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .fillMaxWidth()
                                .background(
                                    color = Color(0xFF2C3E50),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .border(
                                    width = 2.dp,
                                    color = Color(0xFF2C3E50),
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Favorite icon",
                                tint = Color.Black
                            )
                            Spacer(modifier = Modifier.width(width = 10.dp))
                            innerTextField()

                        }
                    }

                )
                Spacer(modifier = Modifier.size(15.dp))
                //Checkbox(checked = false, onCheckedChange = { check(true) }, modifier = Modifier.scale(scaleMultiplier).padding(end = 320.dp))
                Button(
                    onClick = { PasswordReset(email = emailText.value)
                                navController.navigate(Screens.LoginScreen.route)},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2C3E50),
                        contentColor = Color(0xFFF1C40F)
                    ), shape = RoundedCornerShape(5.dp)
                ) {
                    Text(text = "Reset Password",fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)))
                }
                Spacer(modifier = Modifier.size(14.dp))
                val annotedText = buildAnnotatedString{
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFFD9D9D9),fontFamily = FontFamily(Font(
                        R.font.postnobillscolombosemibold
                    )))) {
                        append("<- Back to ")
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFFF1C40F), fontFamily = FontFamily(Font(
                        R.font.postnobillscolombosemibold
                    )))) {
                        append("Login ")
                    }
                }
                //Text(text = annotedText, modifier = Modifier.clickable { })
                ClickableText(text = annotedText, onClick = { offset ->
                        navController.navigate(Screens.LoginScreen.route)

                })

            }
        }

    }
}


fun PasswordReset(email:String){
    val emails = email

    Firebase.auth.sendPasswordResetEmail(emails).addOnCompleteListener({task -> if (task.isSuccessful){ Log.d("Reset","Reset email sent") }  })

}

@Preview(name = "phone", device = "spec:shape=Normal,width=360,height=720,unit=dp,dpi=402")
@Composable
fun PreviewForgetPasswordScreen() {
    ForgetPasswordScreen(rememberNavController())
}