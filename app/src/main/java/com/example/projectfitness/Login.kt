package com.example.projectfitness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

class Login : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
        }
    }
}
@Composable
fun LoginScreen(){
    val scaleMultiplier = 0.5f
    Box(
        Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.projectfitnessblue)) ) {
        Image(painter = painterResource(id = R.drawable.login), contentDescription =null, alpha = 0.3f, contentScale = ContentScale.FillHeight, modifier = Modifier.size(800.dp) )
        Text(text = "PROJECT FITNESS",color = colorResource(id = R.color.projectfitnessyellow), modifier = Modifier
            .align(
                Alignment.Center
            )
            .padding(bottom = 400.dp, start = 20.dp, end = 20.dp), fontSize = 35.sp, fontFamily = FontFamily(
            Font(R.font.poppinsboldtext)
        ), textAlign = TextAlign.Center)
        Text(text = "Login with Google account",color = Color.White, modifier = Modifier
            .align(
                Alignment.Center
            )
            .padding(bottom = 230.dp, start = 65.dp, end = 65.dp), fontSize = 25.sp, fontFamily = FontFamily(
            Font(R.font.poppinslighttext)
        ), textAlign = TextAlign.Center)
        Box(modifier = Modifier
            .size(400.dp)
            .padding(bottom = 50.dp, start = 30.dp, end = 30.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.6f))
            .align(Alignment.BottomCenter)){    

            Column(Modifier.fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally) {
                var emailText by remember { mutableStateOf("") }
                Text(text = "E-Mail Address", fontFamily = FontFamily(Font(R.font.poppinsregulartext)),
                    modifier = Modifier.padding(top = 30.dp, end = 100.dp))
                Spacer(modifier = Modifier.size(10.dp))
                OutlinedTextField(value = emailText , onValueChange = { emailText = it  }, maxLines = 1,
                    colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFF2C3E50), unfocusedContainerColor = Color(0xFF2C3E50)),
                    modifier = Modifier
                        .height(20.dp)
                        .width(250.dp), shape = RoundedCornerShape(10.dp)
                )

                var password by remember { mutableStateOf("") }
                Text(text = "Password", fontFamily = FontFamily(Font(R.font.poppinsregulartext)),
                    modifier = Modifier.padding(top = 30.dp, end = 135.dp))
                Spacer(modifier = Modifier.size(10.dp))
                OutlinedTextField(value = emailText , onValueChange = { password = it  }, maxLines = 1,
                    colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFF2C3E50), unfocusedContainerColor = Color(0xFF2C3E50)),
                    modifier = Modifier
                        .height(20.dp)
                        .width(250.dp),shape = RoundedCornerShape(10.dp))
                Spacer(modifier = Modifier.size(15.dp))
                //Checkbox(checked = false, onCheckedChange = { check(true) }, modifier = Modifier.scale(scaleMultiplier).padding(end = 320.dp))

                Text(text = "Forget Password ?", fontSize = 13.sp, textDecoration = TextDecoration.Underline, fontFamily = FontFamily(Font(R.font.poppinsregulartext)), modifier = Modifier
                    .padding(start = 100.dp)
                    .clickable { })
                Spacer(modifier = Modifier.size(20.dp))
                Button(onClick = { /*TODO*/ }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C3E50), contentColor =Color(0xFFF1C40F) ), ) {
                    Text(text = "Sign-in")
                }
                Spacer(modifier = Modifier.size(20.dp))
                val annotedText = buildAnnotatedString {
                    append("Don’t have an account yet ?")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)){
                    append(" Sign-up")
                } }
                Text(text = annotedText, modifier = Modifier.clickable { })

            }
        }

    }
}
@Preview(name = "phone", device = "spec:shape=Normal,width=360,height=720,unit=dp,dpi=402")
@Composable
fun PreviewLoginScreen(){
    LoginScreen()
}