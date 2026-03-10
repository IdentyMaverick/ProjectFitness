package ui.mainpages.loginscreens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grozzbear.R
import ui.mainpages.navigation.Screens
import viewmodel.AuthViewModel
import viewmodel.RegisterUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController, authViewModel: AuthViewModel) {
    val context = LocalContext.current
    val state = authViewModel.registerState.collectAsState().value

    val name = remember { mutableStateOf("") }
    val nickName = remember { mutableStateOf("") }
    val emailText = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val maxLength = 35

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF181F26))
    ) {
        Image(
            painter = painterResource(id = R.drawable.registerandforgetpasswordscreenphoto),
            contentDescription = null,
            alpha = 0.9f,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.grozzholdsdumbbellbothhandsnobackgroundxml),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .graphicsLayer(translationY = 100f),
                colorFilter = ColorFilter.tint(Color(0xFFF1C40F))
            )

            Text(
                text = "REGISTER",
                fontFamily = FontFamily(Font(R.font.oswaldbold)),
                color = Color.White,
                fontSize = 60.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = name.value,
                    onValueChange = { if (it.length <= maxLength) name.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    placeholder = {
                        Text(
                            "Full Name",
                            color = Color(0xFF4B5F71),
                            fontFamily = FontFamily(Font(R.font.lexendregular))
                        )
                    },
                    textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors()
                )

                Spacer(modifier = Modifier.height(5.dp))

                OutlinedTextField(
                    value = nickName.value,
                    onValueChange = { if (it.length <= maxLength) nickName.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    placeholder = {
                        Text(
                            "Nickname",
                            color = Color(0xFF4B5F71),
                            fontFamily = FontFamily(Font(R.font.lexendregular))
                        )
                    },
                    textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors()
                )

                Spacer(modifier = Modifier.height(5.dp))

                OutlinedTextField(
                    value = emailText.value,
                    onValueChange = { if (it.length <= maxLength) emailText.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    placeholder = {
                        Text(
                            "E-Mail Address",
                            color = Color(0xFF4B5F71),
                            fontFamily = FontFamily(Font(R.font.lexendregular))
                        )
                    },
                    textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors()
                )

                Spacer(modifier = Modifier.height(5.dp))

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
                    colors = textFieldColors()
                )

                Spacer(modifier = Modifier.height(15.dp))

                Button(
                    onClick = {
                        if (name.value.isEmpty() || emailText.value.isEmpty() || password.value.isEmpty() || nickName.value.isEmpty()) {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            authViewModel.register(
                                name.value,
                                nickName.value,
                                emailText.value,
                                password.value
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1C40F)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        "Sign-up",
                        color = Color.Black,
                        fontFamily = FontFamily(Font(R.font.lexendbold))
                    )
                }

                Row(
                    modifier = Modifier.padding(top = 40.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(
                        Modifier
                            .weight(1f)
                            .padding(start = 40.dp),
                        color = Color.White.copy(0.3f)
                    )
                    Text(
                        "OR SIGN-UP WITH",
                        Modifier.padding(horizontal = 12.dp),
                        color = Color.White.copy(0.6f),
                        fontSize = 12.sp
                    )
                    Divider(
                        Modifier
                            .weight(1f)
                            .padding(end = 40.dp),
                        color = Color.White.copy(0.3f)
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                Button(
                    onClick = { /* TODO */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Image(
                        painterResource(id = R.drawable.google),
                        null,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                val annotatedText = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = Color(0xFFD9D9D9),
                            fontSize = 15.sp
                        )
                    ) { append("Do you already have an account ?") }
                    withStyle(
                        SpanStyle(
                            color = Color(0xFFF1C40F),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    ) { append(" Sign-in") }
                }
                ClickableText(
                    text = annotatedText,
                    onClick = { navController.navigate(Screens.LoginScreen.route) })
            }
        }
    }

    when (state) {
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

@Composable
fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color(0xFF1C2126),
    unfocusedContainerColor = Color(0xFF1C2126),
    focusedBorderColor = Color(0xFF4B5F71),
    unfocusedBorderColor = Color(0xFF2E353D),
    cursorColor = Color(0xFFF1C40F)
)