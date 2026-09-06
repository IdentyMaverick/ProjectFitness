package ui.mainpages.loginscreens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grozzbear.R
import com.grozzbear.ui.components.GrozzPrimaryButton
import com.grozzbear.ui.components.GrozzTextField
import com.grozzbear.ui.theme.GrozzBackground
import com.grozzbear.ui.theme.GrozzOnBackground
import com.grozzbear.ui.theme.GrozzTextSecondary
import com.grozzbear.ui.theme.GrozzYellow
import com.grozzbear.ui.theme.Oswald
import ui.mainpages.navigation.Screens
import viewmodel.AuthViewModel
import viewmodel.ResetUiState

private const val LoginTag = "login"

@Composable
fun ForgetPasswordScreen(navController: NavController, authViewModel: AuthViewModel) {
    val resetState by authViewModel.resetUiState.collectAsState()
    val context = LocalContext.current
    val emailText = remember { mutableStateOf("") }
    val isLoading = resetState is ResetUiState.Loading
    val scrollState = rememberScrollState()

    LaunchedEffect(resetState) {
        when (resetState) {
            is ResetUiState.Success -> {
                Toast.makeText(context, "Reset mail sent to ${emailText.value}", Toast.LENGTH_LONG)
                    .show()
                navController.navigate(Screens.LoginScreen.route) {
                    popUpTo(Screens.LoginScreen.ForgetPasswordScreen.route) { inclusive = true }
                }
                authViewModel.resetState()
            }

            is ResetUiState.Error -> {
                val errorMsg = (resetState as ResetUiState.Error).message
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                authViewModel.resetState()
            }

            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GrozzBackground)
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.drawable.grozzforget),
            contentDescription = null,
            alpha = 0.8f,
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color.Transparent,
                            0.45f to Color.Transparent,
                            1.0f to GrozzBackground.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .verticalScroll(scrollState)
                .padding(horizontal = 8.dp)
                .padding(top = 24.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.grozzlogo),
                contentDescription = "Grozz Logo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 8.dp)
            )

            Row(horizontalArrangement = Arrangement.Center) {
                Text(
                    text = "RESET ",
                    fontFamily = Oswald,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = GrozzOnBackground
                )
                Text(
                    text = "PASSWORD",
                    fontFamily = Oswald,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = GrozzYellow
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = GrozzYellow)) {
                        append("No worries, ")
                    }
                    withStyle(SpanStyle(color = GrozzOnBackground)) {
                        append("we'll send you reset instructions.")
                    }
                },
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            GrozzTextField(
                value = emailText.value,
                onValueChange = { emailText.value = it },
                placeholder = "E-Mail Address",
                enabled = !isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(24.dp))

            GrozzPrimaryButton(
                text = "Reset Password",
                loading = isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .widthIn(max = 420.dp),
                onClick = {
                    if (emailText.value.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Please enter your e-mail address",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        authViewModel.reset(emailText.value)
                    }
                }
            )

            Spacer(modifier = Modifier.height(28.dp))

            val backToLoginText = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        color = GrozzTextSecondary,
                        fontWeight = FontWeight.Normal
                    )
                ) {
                    append("Back to ")
                }
                pushStringAnnotation(tag = LoginTag, annotation = LoginTag)
                withStyle(
                    SpanStyle(
                        color = GrozzYellow,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("Login")
                }
                pop()
            }

            ClickableText(
                text = backToLoginText,
                style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                onClick = { offset ->
                    if (isLoading) return@ClickableText
                    backToLoginText
                        .getStringAnnotations(LoginTag, offset, offset)
                        .firstOrNull()
                        ?.let {
                            navController.navigate(Screens.LoginScreen.route)
                        }
                }
            )
        }
    }
}
