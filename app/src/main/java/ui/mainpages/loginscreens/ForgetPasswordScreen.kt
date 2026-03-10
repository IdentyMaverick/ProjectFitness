package ui.mainpages.loginscreens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grozzbear.R
import ui.mainpages.navigation.Screens
import viewmodel.AuthViewModel
import viewmodel.ResetUiState

@Composable
fun ForgetPasswordScreen(navController: NavController, authViewModel: AuthViewModel) {
    val resetState by authViewModel.resetUiState.collectAsState()
    val context = LocalContext.current
    val emailText = remember { mutableStateOf("") }
    val maxLength = 35

    // Durum Takibi: Şifre sıfırlama başarılı olduğunda yapılacaklar
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
            .background(color = Color(0xFF181F26))
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.drawable.registerandforgetpasswordscreenphoto),
            contentDescription = null,
            alpha = 0.8f, // Metinlerin daha iyi okunması için hafifçe düşürdüm
            contentScale = ContentScale.Crop // Boşlukları kapatır
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars), // Uçtan uca tasarım için güvenli alan
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

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "RESET",
                fontFamily = FontFamily(Font(R.font.oswaldbold)),
                color = Color.White,
                fontSize = 60.sp
            )
            Text(
                text = "PASSWORD",
                fontFamily = FontFamily(Font(R.font.oswaldbold)),
                color = Color(0xFFF1C40F),
                fontSize = 60.sp
            )

            // Bilgilendirme Metni
            val infoText = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFFF1C40F))) {
                    append("No worries, ")
                }
                append("we’ll send you reset instructions.")
            }

            Text(
                text = infoText,
                color = Color.White,
                fontSize = 15.sp,
                fontFamily = FontFamily(Font(R.font.lexendregular)),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 40.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // E-Mail Giriş Alanı
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
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF1C2126),
                    unfocusedContainerColor = Color(0xFF1C2126),
                    focusedBorderColor = Color(0xFF4B5F71),
                    unfocusedBorderColor = Color(0xFF2E353D),
                    cursorColor = Color(0xFFF1C40F),
                    focusedLabelColor = Color.Transparent,
                    unfocusedLabelColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(25.dp))

            // Reset Butonu
            Button(
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
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1C40F)),
                shape = RoundedCornerShape(20.dp),
                enabled = resetState !is ResetUiState.Loading
            ) {
                if (resetState is ResetUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black)
                } else {
                    Text("Reset Password", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Geri Dönüş Linki
            val backToLoginText = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        color = Color(0xFFD9D9D9),
                        fontFamily = FontFamily(Font(R.font.lexendregular))
                    )
                ) {
                    append("Back to ")
                }
                withStyle(
                    SpanStyle(
                        color = Color(0xFFF1C40F),
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.lexendbold))
                    )
                ) {
                    append("Login")
                }
            }

            ClickableText(
                text = backToLoginText,
                onClick = { navController.navigate(Screens.LoginScreen.route) }
            )
        }
    }
}