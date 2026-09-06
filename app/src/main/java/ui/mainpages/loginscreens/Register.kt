package ui.mainpages.loginscreens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grozzbear.R
import com.grozzbear.ui.components.GrozzPrimaryButton
import com.grozzbear.ui.components.GrozzTextField
import com.grozzbear.ui.theme.GrozzBackground
import com.grozzbear.ui.theme.GrozzTextSecondary
import com.grozzbear.ui.theme.GrozzYellow
import com.grozzbear.ui.theme.Oswald
import ui.mainpages.navigation.Screens
import ui.mainpages.navigation.navigateAfterAuth
import viewmodel.AuthViewModel
import viewmodel.LoginUiState
import viewmodel.RegisterUiState

private const val SIGN_IN_TAG = "sign_in"

@Composable
fun RegisterScreen(navController: NavController, authViewModel: AuthViewModel) {
    val context = LocalContext.current
    val state by authViewModel.registerState.collectAsState()
    val loginState by authViewModel.loginState.collectAsState()

    val name = remember { mutableStateOf("") }
    val nickName = remember { mutableStateOf("") }
    val emailText = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val isBusy =
        loginState is LoginUiState.Loading || state is RegisterUiState.Loading
    val scrollState = rememberScrollState()

    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginUiState.Success -> {
                navController.navigateAfterAuth(context)
                authViewModel.resetLoginState()
            }

            is LoginUiState.Error -> {
                Toast
                    .makeText(
                        context,
                        (loginState as LoginUiState.Error).message,
                        Toast.LENGTH_SHORT,
                    ).show()
                authViewModel.resetLoginState()
            }

            else -> Unit
        }
    }

    LaunchedEffect(state) {
        when (state) {
            is RegisterUiState.Error -> {
                Toast
                    .makeText(
                        context,
                        (state as RegisterUiState.Error).message,
                        Toast.LENGTH_SHORT,
                    ).show()
                authViewModel.resetRegisterState()
            }

            is RegisterUiState.Success -> {
                navController.navigateAfterAuth(context)
                authViewModel.resetRegisterState()
            }

            else -> Unit
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(GrozzBackground),
    ) {
        Image(
            painter = painterResource(id = R.drawable.grozzregister),
            contentDescription = null,
            alpha = 0.9f,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops =
                                arrayOf(
                                    0.0f to Color.Transparent,
                                    0.40f to Color.Transparent,
                                    1.0f to GrozzBackground.copy(alpha = 0.85f),
                                ),
                        ),
                    ),
        )

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 8.dp)
                    .padding(top = 24.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.grozzlogo),
                contentDescription = "Grozz Logo",
                modifier =
                    Modifier
                        .size(110.dp)
                        .padding(bottom = 8.dp),
            )

            Text(
                text = "REGISTER",
                fontFamily = Oswald,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(24.dp))

            GrozzTextField(
                value = name.value,
                onValueChange = { name.value = it },
                placeholder = "Full Name",
                enabled = !isBusy,
            )

            Spacer(modifier = Modifier.height(12.dp))

            GrozzTextField(
                value = nickName.value,
                onValueChange = { nickName.value = it },
                placeholder = "Nickname",
                enabled = !isBusy,
            )

            Spacer(modifier = Modifier.height(12.dp))

            GrozzTextField(
                value = emailText.value,
                onValueChange = { emailText.value = it },
                placeholder = "E-Mail Address",
                enabled = !isBusy,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            )

            Spacer(modifier = Modifier.height(12.dp))

            GrozzTextField(
                value = password.value,
                onValueChange = { password.value = it },
                placeholder = "Password",
                visualTransformation = PasswordVisualTransformation(),
                enabled = !isBusy,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            )

            Spacer(modifier = Modifier.height(24.dp))

            GrozzPrimaryButton(
                text = "Sign up",
                loading = state is RegisterUiState.Loading,
                enabled = loginState !is LoginUiState.Loading,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .widthIn(max = 420.dp),
                onClick = {
                    if (name.value.isEmpty() ||
                        emailText.value.isEmpty() ||
                        password.value.isEmpty() ||
                        nickName.value.isEmpty()
                    ) {
                        Toast
                            .makeText(context, "Please fill all fields", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        authViewModel.register(
                            name.value,
                            nickName.value,
                            emailText.value,
                            password.value,
                        )
                    }
                },
            )

            Spacer(modifier = Modifier.height(36.dp))

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color.White.copy(alpha = 0.25f),
                )
                Text(
                    text = "OR SIGN-UP WITH",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color.White.copy(alpha = 0.25f),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            GoogleSignInButton(
                authViewModel,
                enabled = !isBusy,
            )

            Spacer(modifier = Modifier.height(24.dp))

            val signInText =
                buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = GrozzTextSecondary,
                            fontWeight = FontWeight.Normal,
                        ),
                    ) {
                        append("Already have an account? ")
                    }
                    pushStringAnnotation(tag = SIGN_IN_TAG, annotation = SIGN_IN_TAG)
                    withStyle(
                        SpanStyle(
                            color = GrozzYellow,
                            fontWeight = FontWeight.Bold,
                        ),
                    ) {
                        append("Sign in")
                    }
                    pop()
                }

            ClickableText(
                text = signInText,
                style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                onClick = { offset ->
                    if (isBusy) return@ClickableText
                    signInText
                        .getStringAnnotations(SIGN_IN_TAG, offset, offset)
                        .firstOrNull()
                        ?.let {
                            navController.navigate(Screens.LoginScreen.route)
                        }
                },
            )
        }
    }
}
