package ui.mainpages.loginscreens

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.grozzbear.R
import ui.mainpages.navigation.Screens
import viewmodel.AuthViewModel
import viewmodel.LoginUiState

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel) {
    val context = LocalContext.current
    val loginState by authViewModel.loginState.collectAsState()

    val emailText = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val maxLength = 35

    // Durum takibi: Başarı veya Hata durumunda yapılacak işlemler
    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginUiState.Success -> {
                navController.navigate(Screens.Home.route) {
                    popUpTo(Screens.LoginScreen.route) { inclusive = true }
                    launchSingleTop = true
                }
                authViewModel.resetLoginState()
            }

            is LoginUiState.Error -> {
                val errorMsg = (loginState as LoginUiState.Error).message
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                authViewModel.resetLoginState()
            }

            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF181F26))
    ) {
        // --- ARKA PLAN GÖRSELİ ---
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.drawable.loginscreenphoto),
            contentDescription = null,
            alpha = 0.9f,
            contentScale = ContentScale.Crop // Boşlukları kapatır ve tam kaplar
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.grozzholdsdumbbellbothhandsnobackgroundxml),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .graphicsLayer(translationY = 100f),
                colorFilter = ColorFilter.tint(Color(0xFFF1C40F))
            )

            Text(
                text = "WELCOME",
                fontFamily = FontFamily(Font(R.font.oswaldbold)),
                color = Color.White,
                fontSize = 60.sp
            )
            Text(
                text = "BACK",
                fontFamily = FontFamily(Font(R.font.oswaldbold)),
                color = Color(0xFFF1C40F),
                fontSize = 60.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // E-Mail Field
            OutlinedTextField(
                value = emailText.value,
                onValueChange = { if (it.length <= maxLength) emailText.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = {
                    Text(
                        "E-Mail",
                        color = Color(0xFF4B5F71),
                        fontFamily = FontFamily(Font(R.font.lexendregular))
                    )
                },
                textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = commonTextFieldColors()
            )

            Spacer(modifier = Modifier.height(5.dp))

            // Password Field
            OutlinedTextField(
                value = password.value,
                onValueChange = { if (it.length <= maxLength) password.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = {
                    Text(
                        "Password",
                        color = Color(0xFF4B5F71),
                        fontFamily = FontFamily(Font(R.font.lexendregular))
                    )
                },
                textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                visualTransformation = PasswordVisualTransformation(),
                colors = commonTextFieldColors()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Forgot Password
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 50.dp)
            ) {
                Text(
                    text = "Forget Password ?",
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        navController.navigate(Screens.LoginScreen.ForgetPasswordScreen.route)
                    },
                    color = Color(0xFFD9D9D9),
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            // Sign-in Button
            Button(
                onClick = {
                    if (emailText.value.isEmpty() || password.value.isEmpty()) {
                        Toast.makeText(context, "E-Mail or Password empty", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        authViewModel.login(emailText.value, password.value)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1C40F)),
                shape = RoundedCornerShape(20.dp),
                enabled = loginState !is LoginUiState.Loading
            ) {
                if (loginState is LoginUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black)
                } else {
                    Text(text = "Sign-in", fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }

            // Alt Kısım (Divider ve Google Login)
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Divider(
                        Modifier
                            .weight(1f)
                            .padding(start = 40.dp),
                        color = Color.White.copy(0.3f),
                        thickness = 1.dp
                    )
                    Text(
                        "OR LOGIN WITH",
                        Modifier.padding(horizontal = 12.dp),
                        color = Color.White.copy(0.6f),
                        fontSize = 12.sp
                    )
                    Divider(
                        Modifier
                            .weight(1f)
                            .padding(end = 40.dp),
                        color = Color.White.copy(0.3f),
                        thickness = 1.dp
                    )
                }

                GoogleSignInButton(authViewModel)

                Spacer(modifier = Modifier.height(30.dp))

                // Sign-up Navigation
                val annotatedText = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = Color(0xFFD9D9D9),
                            fontSize = 15.sp
                        )
                    ) { append("Don’t have an account yet ?") }
                    withStyle(
                        SpanStyle(
                            color = Color(0xFFF1C40F),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    ) { append(" Sign-up") }
                }
                ClickableText(
                    text = annotatedText,
                    modifier = Modifier.padding(bottom = 50.dp),
                    onClick = { offset ->
                        // "Sign-up" kelimesinin aralığına tıklandığında tetiklenir
                        if (offset >= 27) {
                            navController.navigate(Screens.LoginScreen.RegisterScreen.route)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun GoogleSignInButton(authViewModel: AuthViewModel) {
    val context = LocalContext.current
    val auth = Firebase.auth
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) Log.d("Auth", "Successfully Signed In")
                else Log.e("Auth", "Firebase Error")
            }
        } catch (e: Exception) {
            Log.e("Auth", "Error: ${e.message}")
        }
    }

    Button(
        onClick = {
            val signInClient = authViewModel.getGoogleSignInClient(context)
            launcher.launch(signInClient.signInIntent)
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
    ) {
        Image(
            painterResource(R.drawable.google),
            contentDescription = "Google Sign In",
            modifier = Modifier.size(35.dp)
        )
    }
}

@Composable
fun commonTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color(0xFF1C2126),
    unfocusedContainerColor = Color(0xFF1C2126),
    focusedBorderColor = Color(0xFF4B5F71),
    unfocusedBorderColor = Color(0xFF2E353D),
    cursorColor = Color(0xFFF1C40F),
    focusedLabelColor = Color.Transparent,
    unfocusedLabelColor = Color.Transparent
)