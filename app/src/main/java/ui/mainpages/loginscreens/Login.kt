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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.grozzbear.R
import com.grozzbear.ui.components.GrozzPrimaryButton
import com.grozzbear.ui.components.GrozzTextField
import com.grozzbear.ui.theme.GrozzBackground
import com.grozzbear.ui.theme.GrozzTextSecondary
import com.grozzbear.ui.theme.GrozzYellow
import com.grozzbear.ui.theme.Oswald
import ui.mainpages.navigation.Screens
import viewmodel.AuthViewModel
import viewmodel.LoginUiState

private const val SIGN_UP_TAG = "sign_up"

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel) {
    val context = LocalContext.current
    val loginState by authViewModel.loginState.collectAsState()

    val emailText = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val isLoading = loginState is LoginUiState.Loading
    val scrollState = rememberScrollState()

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
        modifier =
            Modifier
                .fillMaxSize()
                .background(GrozzBackground),
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.drawable.grozzlogin),
            contentDescription = null,
            alpha = 0.9f,
            contentScale = ContentScale.Crop,
        )

        // Soft bottom fade so footer text stays readable over the photo
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops =
                                arrayOf(
                                    0.0f to Color.Transparent,
                                    0.45f to Color.Transparent,
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
                        .size(120.dp)
                        .padding(bottom = 8.dp),
            )

            Row(horizontalArrangement = Arrangement.Center) {
                Text(
                    text = "WELCOME ",
                    fontFamily = Oswald,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.White,
                )
                Text(
                    text = "BACK",
                    fontFamily = Oswald,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = GrozzYellow,
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            GrozzTextField(
                value = emailText.value,
                onValueChange = { emailText.value = it },
                placeholder = "E-Mail",
                enabled = !isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            )

            Spacer(modifier = Modifier.height(12.dp))

            GrozzTextField(
                value = password.value,
                onValueChange = { password.value = it },
                placeholder = "Password",
                visualTransformation = PasswordVisualTransformation(),
                enabled = !isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            )

            Text(
                text = "Forgot password?",
                style = MaterialTheme.typography.bodyMedium,
                color = GrozzTextSecondary,
                textDecoration = TextDecoration.Underline,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .clickable(enabled = !isLoading) {
                            navController.navigate(Screens.LoginScreen.ForgetPasswordScreen.route)
                        },
                textAlign = TextAlign.End,
            )

            Spacer(modifier = Modifier.height(20.dp))

            GrozzPrimaryButton(
                text = "Sign in",
                loading = isLoading,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .widthIn(max = 420.dp),
                onClick = {
                    if (emailText.value.isEmpty() || password.value.isEmpty()) {
                        Toast
                            .makeText(context, "E-Mail or Password empty", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        authViewModel.login(emailText.value, password.value)
                    }
                },
            )

            Spacer(modifier = Modifier.height(40.dp))

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
                    text = "OR LOGIN WITH",
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
                enabled = !isLoading,
            )

            Spacer(modifier = Modifier.height(24.dp))

            val signUpText =
                buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = GrozzTextSecondary,
                            fontWeight = FontWeight.Normal,
                        ),
                    ) {
                        append("Don't have an account yet? ")
                    }
                    pushStringAnnotation(tag = SIGN_UP_TAG, annotation = SIGN_UP_TAG)
                    withStyle(
                        SpanStyle(
                            color = GrozzYellow,
                            fontWeight = FontWeight.Bold,
                        ),
                    ) {
                        append("Sign up")
                    }
                    pop()
                }

            ClickableText(
                text = signUpText,
                style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                onClick = { offset ->
                    if (isLoading) return@ClickableText
                    signUpText
                        .getStringAnnotations(SIGN_UP_TAG, offset, offset)
                        .firstOrNull()
                        ?.let {
                            navController.navigate(Screens.LoginScreen.RegisterScreen.route)
                        }
                },
            )
        }
    }
}

@Composable
fun GoogleSignInButton(authViewModel: AuthViewModel, enabled: Boolean = true) {
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken.isNullOrBlank()) {
                    authViewModel.onGoogleSignInFailed("Google ID token is missing")
                    return@rememberLauncherForActivityResult
                }
                authViewModel.loginWithGoogle(
                    idToken = idToken,
                    displayName = account.displayName,
                    email = account.email,
                    photoUrl = account.photoUrl?.toString(),
                )
            } catch (e: ApiException) {
                if (e.statusCode == GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                    return@rememberLauncherForActivityResult
                }
                Log.e("Auth", "Google sign-in failed: ${e.statusCode} ${e.message}")
                authViewModel.onGoogleSignInFailed(googleSignInErrorMessage(e))
            } catch (e: Exception) {
                Log.e("Auth", "Error: ${e.message}")
                authViewModel.onGoogleSignInFailed(e.message ?: "Google sign-in failed")
            }
        }

    Button(
        onClick = {
            val signInClient = authViewModel.getGoogleSignInClient(context)
            launcher.launch(signInClient.signInIntent)
        },
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
    ) {
        Image(
            painterResource(R.drawable.google),
            contentDescription = "Google Sign In",
            modifier = Modifier.size(40.dp),
        )
    }
}

private fun googleSignInErrorMessage(e: ApiException): String = when (e.statusCode) {
    CommonStatusCodes.DEVELOPER_ERROR ->
        "Google Sign-In is not configured for this app. Add the debug SHA-1 in Firebase Console, then download a new google-services.json."

    else -> {
        val codeName = GoogleSignInStatusCodes.getStatusCodeString(e.statusCode)
        "Google sign-in failed ($codeName)"
    }
}
