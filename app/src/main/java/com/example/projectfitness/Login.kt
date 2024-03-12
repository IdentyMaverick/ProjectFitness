
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class Login : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    var usernames : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContent {
        }
    }

    public override fun onStart() {
        super.onStart()
    }

    @Composable
    fun LoginScreen(navController: NavController) {


        val configuration = LocalConfiguration.current
        val screenwidthDp = configuration.screenWidthDp.dp
        val screenheightDp = configuration.screenHeightDp.dp

        val checkedState = remember { mutableStateOf(true) }
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()

        //getCurrentUser(navController)

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
                fontFamily = FontFamily(
                    Font(R.font.postnobillscolombosemibold)
                ),
                textAlign = TextAlign.Center,
                style = TextStyle(fontSize = 35.sp, letterSpacing = 5.sp)
            )
            Row(
                Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 200.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF181F26).copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(10.dp),
                    onClick = { /*TODO*/ }
                ) {
                    Text(
                        text = "Login with Google account",
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
                    .size(400.dp)
                    .padding(bottom = 50.dp, start = 30.dp, end = 30.dp)
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
                        style = TextStyle(letterSpacing = 3.sp),
                        color = Color(0xFFD9D9D9)
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
                            fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                            color = Color.White,
                            letterSpacing = 1.sp
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
                        text = "Password", fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                        modifier = Modifier.padding(top = 5.dp, end = 185.dp),
                        style = TextStyle(letterSpacing = 3.sp),
                        color = Color(0xFFD9D9D9)

                        )
                    Spacer(modifier = Modifier.size(5.dp))
                    BasicTextField(
                        value = password.value,
                        onValueChange = { password.value = it },
                        modifier = Modifier
                            .height(40.dp)
                            .width(270.dp)
                            .background(Color(0xFF2C3E50), shape = RoundedCornerShape(10.dp)),
                        visualTransformation = PasswordVisualTransformation(),
                        textStyle = TextStyle(
                            color = Color.White,
                            letterSpacing = 1.sp
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
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = "Favorite icon",
                                    tint = Color.Black
                                )
                                Spacer(modifier = Modifier.width(width = 10.dp))
                                innerTextField()
                            }
                        }

                    )
                    Spacer(modifier = Modifier.size(5.dp))



                    Box(Modifier.fillMaxWidth().height(40.dp)){
                        Checkbox(checked = checkedState.value, onCheckedChange = { checkedState.value = it } ,
                            Modifier
                                .align(Alignment.TopEnd)
                                .padding(end = screenwidthDp / 20))
                        Text(text = "Remember Me",
                            Modifier
                                .align(Alignment.TopEnd)
                                .padding(end = screenwidthDp / 6 , top = screenheightDp/50),
                            fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                            color = Color(0xFFD9D9D9),
                            style = TextStyle(letterSpacing = 1.sp, fontSize = 15.sp)
                        )
                    }


                    Text(text = "Forget Password ?",
                        textDecoration = TextDecoration.Underline,
                        fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)),
                        modifier = Modifier
                            .clickable { navController.navigate(Screens.LoginScreen.ForgetPasswordScreen.route) },
                        color = Color(0xFFD9D9D9),
                        style = TextStyle(letterSpacing = 1.sp, fontSize = 15.sp))
                    Spacer(modifier = Modifier.size(10.dp))
                    Button(
                        onClick = {
                            if (emailText.value.isNullOrEmpty() || password.value.isNullOrEmpty()) {
                                Toast.makeText(
                                    context,
                                    "E-Mail or Password empty, try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                coroutineScope.launch {
                                    LoginFirebase(
                                        emailText.value,
                                        password.value,
                                        navController,
                                        context = context
                                    )
                                }

                            }

                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2C3E50),
                            contentColor = Color(0xFFF1C40F)
                        ), shape = RoundedCornerShape(5.dp)
                    ) {
                        Text(text = "Sign-in", fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)), fontSize = 15.sp, letterSpacing = 1.sp)
                    }
                    Spacer(modifier = Modifier.size(7.dp))
                    val annotedText = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color(0xFFD9D9D9), fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)), fontSize = 15.sp, letterSpacing = 1.sp)){
                            append("Don’t have an account yet ?")
                        }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold,color = Color(0xFFF1C40F),fontFamily = FontFamily(Font(R.font.postnobillscolombosemibold)), fontSize = 15.sp, letterSpacing = 1.sp)) {
                            append(" Sign-up")
                        }
                    }
                    //Text(text = annotedText, modifier = Modifier.clickable { })
                    ClickableText(text = annotedText, onClick = { offset ->
                        if (offset in 27..34) {
                            navController.navigate(Screens.LoginScreen.RegisterScreen.route)
                        }//style = TextStyle(letterSpacing = 1.sp, fontSize = 15.sp)
                    })

                }
            }

        }
    }

    suspend fun LoginFirebase(
        email: String,
        password: String,
        navController: NavController,
        context: Context

    ) {
        val db = Firebase.firestore
        val docRef = db.collection("users")
        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val query = docRef.whereEqualTo("email", email)
                CoroutineScope(Dispatchers.Main).launch {
                    query.get().addOnSuccessListener { document ->
                        for (document in document) {
                            var usernameinfo = document.data.get("first").toString()
                            this@Login.usernames = usernameinfo
                        }
                    }.await()
                    Toast.makeText(context, "user is " + this@Login.usernames, Toast.LENGTH_SHORT).show()
                    navController.navigate(Screens.Home.route)
                    finish()
                }
            } else {
                Toast.makeText(
                    context,
                    "E-Mail or Password wrong, try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun getCurrentUser(navController: NavController){
        try {
            val user = Firebase.auth.currentUser
            if (user != null){
                navController.navigate(Screens.Home.route)
            }
            else{
                // Login
            }
        }
        catch (e:Exception){
            Log.d("LOOOL",e.message.toString())}



    }

    @Preview(name = "phone", device = "spec:shape=Normal,width=360,height=720,unit=dp,dpi=402")
    @Composable
    fun PreviewLoginScreen() {
        LoginScreen(rememberNavController())
    }
}