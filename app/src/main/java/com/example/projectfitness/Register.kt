
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.projectfitness.R

@Composable
fun RegisterScreen(navController: NavController) {
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
        Text(
            text = "Register with Google account",
            color = Color.White,
            modifier = Modifier
                .align(
                    Alignment.Center
                )
                .padding(bottom = 270.dp, start = 45.dp, end = 45.dp),
            fontSize = 20.sp,
            fontFamily = FontFamily(
                Font(R.font.poppinslighttext)
            ),
            textAlign = TextAlign.Center
        )
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
                Spacer(modifier = Modifier.size(10.dp))
                OutlinedTextField(
                    value = name.value, onValueChange = { name.value = it }, maxLines = 1,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(
                            0xFF2C3E50
                        ), unfocusedContainerColor = Color(0xFF2C3E50)
                    ),
                    modifier = Modifier, shape = RoundedCornerShape(10.dp)
                )

                var emailText = remember { mutableStateOf("") }
                Text(
                    text = "E-Mail Address",
                    fontFamily = FontFamily(Font(R.font.poppinsregulartext)),
                    modifier = Modifier.padding(top = 5.dp, end = 120.dp)
                )
                Spacer(modifier = Modifier.size(10.dp))
                OutlinedTextField(
                    value = emailText.value,
                    onValueChange = { emailText.value = it },
                    maxLines = 1,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(
                            0xFF2C3E50
                        ),
                        unfocusedContainerColor = Color(0xFF2C3E50),
                        cursorColor = Color(0xFFF1C40F)
                    ),
                    modifier = Modifier,
                    shape = RoundedCornerShape(10.dp)
                )
                var password = remember { mutableStateOf("") }
                Text(
                    text = "Password", fontFamily = FontFamily(Font(R.font.poppinsregulartext)),
                    modifier = Modifier.padding(top = 5.dp, end = 150.dp)
                )
                Spacer(modifier = Modifier.size(10.dp))
                OutlinedTextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    maxLines = 1,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(
                            0xFF2C3E50
                        ), unfocusedContainerColor = Color(0xFF2C3E50)
                    ),
                    modifier = Modifier,
                    shape = RoundedCornerShape(10.dp)
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
                Text(text = annotedText, modifier = Modifier.clickable { })
            }
        }

    }
}
@Preview(name = "phone", device = "spec:shape=Normal,width=360,height=720,unit=dp,dpi=402")
@Composable
fun PreviewRegisterScreen(){
    RegisterScreen(navController = rememberNavController())
}