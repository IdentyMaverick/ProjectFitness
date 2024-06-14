package loginscreens

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.projectfitness.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import database.Database
import navigation.Screens
import viewmodel.ViewModelProfile

class Register : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        //var user = auth.currentUser
        //context = applicationContext
        setContent {}
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show()
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun RegisterScreen(navController: NavController , viewModelProfile: ViewModelProfile) {
        val db = Firebase.firestore
        val context = LocalContext.current
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
            Row(
                Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 260.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF181F26).copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(10.dp),
                    onClick = { /*TODO*/ }
                ) {
                    Text(
                        text = "Register with Google account",
                        color = Color(0xFFD9D9D9),
                        modifier = Modifier.padding(end = 5.dp),
                        fontSize = 17.sp,
                        fontFamily = FontFamily(
                            Font(R.font.postnobillscolombosemibold)
                        ),
                        textAlign = TextAlign.Center,
                        style = TextStyle(letterSpacing = 3.sp),
                    )
                    Image(painterResource(id = R.drawable.google), contentDescription = null)
                }
            }
            Box(
                modifier = Modifier
                    .size(430.dp)
                    .padding(bottom = 30.dp, start = 30.dp, end = 30.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF182129).copy(alpha = 0.4f))
                    .align(Alignment.BottomCenter)
            ) {

                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                    var name = remember { mutableStateOf("") }
                    Text(
                        text = "Full Name",
                        fontFamily = FontFamily(Font(R.font.poppinsregulartext)),
                        modifier = Modifier.padding(top = 30.dp, end = 150.dp),
                        color = Color(0xFFD9D9D9)
                    )
                    Spacer(modifier = Modifier.size(5.dp))
                    BasicTextField(
                        value = name.value,
                        onValueChange = { name.value = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .height(40.dp)
                            .width(270.dp)
                            .background(Color(0xFF2C3E50), shape = RoundedCornerShape(10.dp)),
                        maxLines = 1,
                        textStyle = TextStyle(
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(R.font.poppinslighttext)),
                            color = Color.White,
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
                                    imageVector = Icons.Default.AccountBox,
                                    contentDescription = "Favorite icon",
                                    tint = Color.Black
                                )
                                Spacer(modifier = Modifier.width(width = 10.dp))
                                innerTextField()
                            }
                        }
                    )

                    var nickName = remember { mutableStateOf("") }
                    Text(
                        text = "Nickname",
                        fontFamily = FontFamily(Font(R.font.poppinsregulartext)),
                        modifier = Modifier.padding(top = 30.dp, end = 150.dp),
                        color = Color(0xFFD9D9D9)
                    )
                    Spacer(modifier = Modifier.size(5.dp))
                    BasicTextField(
                        value = nickName.value,
                        onValueChange = {
                            viewModelProfile.nickNameId.value = it
                            nickName.value = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .height(40.dp)
                            .width(270.dp)
                            .background(Color(0xFF2C3E50), shape = RoundedCornerShape(10.dp)),
                        maxLines = 1,
                        textStyle = TextStyle(
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(R.font.poppinslighttext)),
                            color = Color.White,
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
                                    imageVector = Icons.Default.AccountBox,
                                    contentDescription = "Favorite icon",
                                    tint = Color.Black
                                )
                                Spacer(modifier = Modifier.width(width = 10.dp))
                                innerTextField()
                            }
                        }
                    )

                    var emailText = remember { mutableStateOf("") }
                    Text(
                        text = "E-Mail Address",
                        fontFamily = FontFamily(Font(R.font.poppinsregulartext)),
                        modifier = Modifier.padding(top = 5.dp, end = 120.dp),
                        color = Color(0xFFD9D9D9)
                    )
                    Spacer(modifier = Modifier.size(5.dp))
                    BasicTextField(
                        value = emailText.value,
                        onValueChange = { emailText.value = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .height(40.dp)
                            .width(270.dp)
                            .background(Color(0xFF2C3E50), shape = RoundedCornerShape(10.dp)),
                        maxLines = 1,
                        textStyle = TextStyle(
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(R.font.poppinslighttext)),
                            color = Color.White,
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
                    var password = remember { mutableStateOf("") }
                    Text(
                        text = "Password", fontFamily = FontFamily(Font(R.font.poppinsregulartext)),
                        modifier = Modifier.padding(top = 5.dp, end = 150.dp),
                        color = Color(0xFFD9D9D9)
                    )

                    Spacer(modifier = Modifier.size(5.dp))
                    BasicTextField(
                        value = password.value,
                        onValueChange = { password.value = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .height(40.dp)
                            .width(270.dp)
                            .background(Color(0xFF2C3E50), shape = RoundedCornerShape(10.dp)),
                        maxLines = 1,
                        textStyle = TextStyle(
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(R.font.poppinslighttext)),
                            color = Color.White,
                        ),
                        visualTransformation = PasswordVisualTransformation(),
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
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = "Favorite icon",
                                    tint = Color.Black
                                )
                                Spacer(modifier = Modifier.width(width = 10.dp))
                                innerTextField()
                            }
                        }

                    )
                    Spacer(modifier = Modifier.size(15.dp))
                    Button(
                        onClick = {
                            if (name.value.isNullOrEmpty() || emailText.value.isNullOrEmpty() || password.value.isNullOrEmpty() || nickName.value.isNullOrEmpty()) {
                                Toast.makeText(
                                    context,
                                    "Name, Nickname, E-Mail and Password should be filled, try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                RegisterFirebase(
                                    emailText = emailText.value,
                                    password = password.value,
                                    navController = navController,
                                    name = name.value,
                                    nickName = nickName.value
                                )
                            }

                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2C3E50),
                            contentColor = Color(0xFFF1C40F)
                        ), shape = RoundedCornerShape(5.dp)
                    ) {
                        Text(text = "Sign-up")
                    }
                    val annotedText = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color(0xFFD9D9D9), fontFamily = FontFamily(Font(
                            R.font.postnobillscolombosemibold
                        )), fontSize = 15.sp, letterSpacing = 1.sp)){
                            append("Do you already have an account ?")
                        }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold,color = Color(0xFFF1C40F),fontFamily = FontFamily(Font(
                            R.font.postnobillscolombosemibold
                        )), fontSize = 15.sp, letterSpacing = 1.sp)) {
                            append(" Sign-in")
                        }
                    }
                    ClickableText(
                        text = annotedText,
                        onClick = { navController.navigate(Screens.LoginScreen.route) })
                }
            }

        }
    }

    fun RegisterFirebase(
        emailText: String,
        password: String,
        navController: NavController,
        name: String ,
        nickName : String
    ) {
        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(emailText, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = hashMapOf(
                        "first" to name ,
                        "nickname" to nickName ,
                        "email" to emailText ,
                        "password" to password)
                    Database().DatabaseUserCreate(user)
                    navController.navigate(Screens.LoginScreen.route)
                    finish()
                } else {
                    Toast.makeText(this@Register, "" + task.exception, Toast.LENGTH_SHORT).show()
                }
            }
    }

    @Preview(name = "phone", device = "spec:shape=Normal,width=360,height=720,unit=dp,dpi=402")
    @Composable
    fun PreviewRegisterScreen() {
        RegisterScreen(navController = rememberNavController() , ViewModelProfile())
    }
}