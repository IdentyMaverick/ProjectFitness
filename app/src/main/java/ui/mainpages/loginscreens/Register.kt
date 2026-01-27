package ui.mainpages.loginscreens

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
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
import data.remote.Database
import ui.mainpages.navigation.Screens
import viewmodel.AuthViewModel
import viewmodel.RegisterUiState
import viewmodel.ViewModelProfile

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun RegisterScreen(navController: NavController , authViewModel: AuthViewModel) {
        val context = LocalContext.current
        val state = authViewModel.registerState.collectAsState().value

        Box(
            Modifier
                .fillMaxSize()
                .background(color = Color(0xFF181F26))
        ) {
            Image(
                painter = painterResource(id = R.drawable.login),
                contentDescription = null,
                alpha = 0.09f,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.size(800.dp)
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier
                    .size(100.dp))
            Image(
                painter = painterResource(id = R.drawable.projectfitnesslogologin) ,
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
            )
                Spacer(modifier = Modifier
                    .height(16.dp))
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                    var maxLengthName = 35
                    var maxLength = 35
                    var name = remember { mutableStateOf("") }
                    Text(
                        text = "Full Name",
                        fontFamily = FontFamily(Font(R.font.lexendregular)),
                        modifier = Modifier.padding(top = 30.dp, end = 150.dp),
                        color = Color(0xFFD9D9D9),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    BasicTextField(
                        value = name.value,
                        onValueChange = { newValue ->
                            val limited = newValue.take(maxLengthName)
                                        if (limited != name.value) name.value = limited},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .height(40.dp)
                            .width(300.dp)
                            .background(Color(0xFF2C3E50), shape = RoundedCornerShape(10.dp)),
                        maxLines = 1,
                        textStyle = TextStyle(
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(R.font.lexendregular)),
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
                        fontFamily = FontFamily(Font(R.font.lexendregular)),
                        modifier = Modifier.padding(top = 5.dp, end = 150.dp),
                        color = Color(0xFFD9D9D9),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    BasicTextField(
                        value = nickName.value,
                        onValueChange = { newValue ->
                            val limited = newValue.take(maxLength)
                            if (limited != nickName.value) nickName.value = limited},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .height(40.dp)
                            .width(300.dp)
                            .background(Color(0xFF2C3E50), shape = RoundedCornerShape(10.dp)),
                        maxLines = 1,
                        textStyle = TextStyle(
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(R.font.lexendregular)),
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
                        fontFamily = FontFamily(Font(R.font.lexendregular)),
                        modifier = Modifier.padding(top = 5.dp, end = 120.dp),
                        color = Color(0xFFD9D9D9),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.size(5.dp))
                    BasicTextField(
                        value = emailText.value,
                        onValueChange = { newValue ->
                            val limited = newValue.take(maxLength)
                            if (limited != emailText.value) emailText.value = limited},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .height(40.dp)
                            .width(300.dp)
                            .background(Color(0xFF2C3E50), shape = RoundedCornerShape(10.dp)),
                        maxLines = 1,
                        textStyle = TextStyle(
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(R.font.lexendregular)),
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
                        text = "Password", fontFamily = FontFamily(Font(R.font.lexendregular)),
                        modifier = Modifier.padding(top = 5.dp, end = 150.dp),
                        color = Color(0xFFD9D9D9),
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.size(5.dp))
                    BasicTextField(
                        value = password.value,
                        onValueChange = { newValue ->
                            val limited = newValue.take(maxLength)
                            if (limited != password.value) password.value = limited},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .height(40.dp)
                            .width(300.dp)
                            .background(Color(0xFF2C3E50), shape = RoundedCornerShape(10.dp)),
                        maxLines = 1,
                        textStyle = TextStyle(
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(R.font.lexendregular)),
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
                                    imageVector = Icons.Default.Lock,
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
                                authViewModel.register(
                                    fullName = name.value,
                                    nickname = nickName.value,
                                    email = emailText.value,
                                    password = password.value
                                )
                            }

                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2C3E50),
                            contentColor = Color(0xFFF1C40F)
                        ), shape = RoundedCornerShape(5.dp)
                    ) {
                        Text(
                            text = "Sign-up",
                            fontFamily = FontFamily(Font(R.font.lexendbold)),
                            fontSize = 15.sp,
                            letterSpacing = 0.sp
                        )
                    }
                    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top) {
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
                            text = "OR SIGN-UP WITH",
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
                            .size(15.dp))
                        val annotedText = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color(0xFFD9D9D9), fontFamily = FontFamily(Font(
                            R.font.lexendregular
                        )), fontSize = 15.sp, letterSpacing = 0.sp)){
                            append("Do you already have an account ?")
                        }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold,color = Color(0xFFF1C40F),fontFamily = FontFamily(Font(
                            R.font.lexendbold
                        )), fontSize = 15.sp, letterSpacing = 0.sp)) {
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
        when(state) {
            is RegisterUiState.Loading -> {
                // istersen butonu disable et / loader göster
            }
            is RegisterUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                authViewModel.resetRegisterState()
            }
            is RegisterUiState.Success -> {
                androidx.compose.runtime.LaunchedEffect(Unit) {
                    navController.navigate(Screens.LoginScreen.route) {
                        popUpTo(Screens.LoginScreen.RegisterScreen.route) { inclusive = true }
                    }
                    authViewModel.resetRegisterState()
                }
            }
            else -> Unit
        }
    }