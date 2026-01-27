package ui.mainpages.loginscreens

import android.annotation.SuppressLint
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.projectfitness.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ui.mainpages.navigation.Screens
import viewmodel.AuthViewModel
import viewmodel.LoginUiState
import viewmodel.ViewModelProfile

    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    fun LoginScreen(navController: NavController, authViewModel: AuthViewModel) {
        var contextmi = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        val loginState = authViewModel.loginState.collectAsState().value

        Box(
            Modifier
                .fillMaxSize()
                .background(color = Color(0xFF181F26))
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = R.drawable.login),
                contentDescription = null,
                alpha = 0.09f,
                contentScale = ContentScale.FillHeight
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var maxLength = 35
                var emailText = remember { mutableStateOf("") }
                Spacer(modifier = Modifier
                    .size(100.dp))
                Image(
                    painter = painterResource(id = R.drawable.projectfitnesslogologin),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                )
                Spacer(modifier = Modifier
                    .height(16.dp))
                Text(
                    text = "E-Mail Address",
                    fontFamily = FontFamily(Font(R.font.lexendregular)),
                    modifier = Modifier.padding(top = 30.dp, end = 150.dp),
                    style = TextStyle(letterSpacing = 1.sp),
                    color = Color(0xFFD9D9D9)
                )
                Spacer(modifier = Modifier.size(10.dp))
                BasicTextField(
                    value = emailText.value,
                    onValueChange = { newValue ->
                        val limited = newValue.take(maxLength)
                        if (limited != emailText.value) emailText.value = limited},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .height(40.dp)
                        .width(300.dp)
                        .background(Color(0xFF2C3E50), shape = RoundedCornerShape(10.dp)),
                    maxLines = 1,
                    textStyle = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.lexendregular)),
                        color = Color.White,
                        letterSpacing = 1.sp,
                        textDecoration = TextDecoration.None
                    ),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart // Metni sol ortalamak için
                        ) {
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
                    }

                )
                var password = remember { mutableStateOf("") }
                Text(
                    text = "Password",
                    fontFamily = FontFamily(Font(R.font.lexendregular)),
                    modifier = Modifier.padding(top = 5.dp, end = 185.dp),
                    style = TextStyle(letterSpacing = 1.sp),
                    color = Color(0xFFD9D9D9)

                )
                Spacer(modifier = Modifier.size(5.dp))
                BasicTextField(
                    value = password.value,
                    onValueChange = { newValue ->
                        val limited = newValue.take(maxLength)
                        if (limited != password.value) password.value = limited},
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier
                        .height(40.dp)
                        .width(300.dp)
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
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Favorite icon",
                                tint = Color.Black
                            )
                            Spacer(modifier = Modifier.width(width = 10.dp))
                            innerTextField()
                        }
                    }

                )
                Spacer(modifier = Modifier.size(20.dp))
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 50.dp)) {
                    Text(
                        text = "Forget Password ?",
                        textDecoration = TextDecoration.Underline,
                        fontFamily = FontFamily(Font(R.font.lexendregular)),
                        modifier = Modifier
                            .clickable { navController.navigate(Screens.LoginScreen.ForgetPasswordScreen.route) }
                            .align(Alignment.CenterVertically),
                        color = Color(0xFFD9D9D9),
                        style = TextStyle(letterSpacing = 0.sp, fontSize = 15.sp)
                    )
                }
                Spacer(modifier = Modifier.size(50.dp))
                Button(
                    onClick = {
                        if (emailText.value.isNullOrEmpty() || password.value.isNullOrEmpty()) {
                            Toast.makeText(
                                contextmi,
                                "E-Mail or Password empty, try again",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Log.d("Login state is", "Login state is: $loginState")
                            authViewModel.login(emailText.value, password.value)
                            if (loginState is LoginUiState.Success) {
                                coroutineScope.launch {
                                        navController.navigate(Screens.Home.route) {
                                            popUpTo(Screens.LoginScreen.route) {inclusive = true}
                                            launchSingleTop = true
                                        }
                                }
                                authViewModel.resetLoginState()
                            }
                            else if (loginState is LoginUiState.Error) {
                                coroutineScope.launch {
                                    Log.d("Login Error", "Login error")
                                }
                            }
                        }

                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2C3E50),
                        contentColor = Color(0xFFF1C40F)
                    ), shape = RoundedCornerShape(5.dp)
                ) {
                    Text(
                        text = "Sign-in",
                        fontFamily = FontFamily(Font(R.font.lexendbold)),
                        fontSize = 15.sp,
                        letterSpacing = 0.sp
                    )
                }
                Spacer(modifier = Modifier.size(7.dp))
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {

                    Row(
                        modifier = Modifier
                            .padding(top = 40.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(
                            modifier = Modifier.weight(1f)
                                .padding(start = 40.dp),
                            color = Color.White.copy(alpha = 0.3f),
                            thickness = 1.dp
                        )
                        Text(
                            text = "OR LOGIN WITH",
                            modifier = Modifier.padding(horizontal = 12.dp),
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )

                        Divider(
                            modifier = Modifier.weight(1f)
                                .padding(end = 40.dp),
                            color = Color.White.copy(alpha = 0.3f),
                            thickness = 1.dp
                        )

                    }

                    Spacer(modifier = Modifier
                        .size(15.dp))

                    Row(
                        Modifier,
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF181F26).copy(
                                alpha = 0f
                            )
                        ),
                            shape = RoundedCornerShape(10.dp),
                            onClick = { /*TODO*/ }
                        ) {
                            Image(painterResource(id = R.drawable.google), contentDescription = null, modifier = Modifier
                                .size(30.dp))
                        }
                    }

                    Spacer(modifier = Modifier
                        .weight(1f))
                    val annotedText = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = Color(0xFFD9D9D9),
                                fontFamily = FontFamily(Font(R.font.lexendregular)),
                                fontSize = 15.sp,
                                letterSpacing = 0.sp
                            )
                        ) {
                            append("Don’t have an account yet ?")
                        }
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF1C40F),
                                fontFamily = FontFamily(Font(R.font.lexendbold)),
                                fontSize = 15.sp,
                                letterSpacing = 0.sp
                            )
                        ) {
                            append(" Sign-up")
                        }
                    }
                    ClickableText(text = annotedText, onClick = { offset ->
                        if (offset in 27..34) {
                            navController.navigate(Screens.LoginScreen.RegisterScreen.route)
                        }
                    },
                        modifier = Modifier
                            .padding(bottom = 50.dp))
                }
            }

        }
        }
/*}
/*fun LoginFirebase(
    email: String,
    password: String,
    navController: NavController,
    context: Context,
    viewModelProfile: ViewModelProfile
    ) {
    var projectFitnessContainer = ProjectFitnessContainer(context)
    val itemRepo = projectFitnessContainer.itemsRepository
    val db = Firebase.firestore
    val docRef = db.collection("users")
    var sharedPreferences = context.getSharedPreferences("rememberbuttonStatus", MODE_PRIVATE)
    auth = Firebase.auth
    var uid = ""
    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
        if (task.isSuccessful) {
            val query = docRef.whereEqualTo("email", email)
            CoroutineScope(Dispatchers.Main).launch {
                query.get().addOnSuccessListener { document ->
                    for (document in document) {
                        var usernameinfo = document.data.get("first").toString()
                        var nickName = document.data.get("nickname")
                        viewModelProfile.nickNameId.value = nickName.toString()
                        this@Login.usernames = usernameinfo
                    }
                }.await()
                itemRepo.insertProjectUser(ProjectFitnessUser
                    (uid, usernames , email , password ,
                    "gs://projectfitness-ddfeb.appspot.com/gs:/projectfitness-ddfeb.appspot.com/profile_photos/${com.google.firebase.Firebase.auth.currentUser?.uid}/profile.jpg" , sharedPreferences.getBoolean("bool",false) , viewModelProfile.nickNameId.value))
                    //"gs://projectfitness-ddfeb.appspot.com/gs:/projectfitness-ddfeb.appspot.com/profile_photos/VgUOk57HqnSH1tUAD3ZI/profile.jpg"
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
fun getCurrentUser(navController: NavController) {
    try {
        val user = Firebase.auth.currentUser
        if (user != null) {
            navController.navigate(Screens.Home.route)
        } else {
            // Login
        }
    } catch (e: Exception) {
        Log.d("LOOOL", e.message.toString())
    }
}
@Composable
fun IndeterminateCircularIndicator() {
    CircularProgressIndicator(
        modifier = Modifier
            .width(40.dp)
            .fillMaxSize()
            .padding(top = 500.dp),
        color = Color(0xFF181F26),
        trackColor = Color(0xFFF1C40F),
    )
}*/