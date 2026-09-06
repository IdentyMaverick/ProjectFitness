package com.grozzbear.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grozzbear.R
import com.grozzbear.ui.theme.GrozzBorder
import com.grozzbear.ui.theme.GrozzMuted
import com.grozzbear.ui.theme.GrozzOnBackground
import com.grozzbear.ui.theme.GrozzOnPrimary
import com.grozzbear.ui.theme.GrozzRadiusButton
import com.grozzbear.ui.theme.GrozzRadiusChip
import com.grozzbear.ui.theme.GrozzRadiusPanel
import com.grozzbear.ui.theme.GrozzRadiusPhoto
import com.grozzbear.ui.theme.GrozzSurface
import com.grozzbear.ui.theme.GrozzTextSecondary
import com.grozzbear.ui.theme.GrozzYellow
import com.grozzbear.ui.theme.Lexend
import com.grozzbear.ui.theme.Oswald

/** Main-tab top bar logo size — keep Home / Activity / LeaderBoard / Meal aligned. */
val GrozzTopBarLogoSize: Dp = 64.dp

/**
 * Centered Grozz mark for main Scaffold top bars.
 */
@Composable
fun GrozzTopBarLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.grozzlogo),
        contentDescription = "Grozz",
        modifier = modifier.size(GrozzTopBarLogoSize),
    )
}

@Composable
fun GrozzPhotoCard(
    painter: Painter,
    modifier: Modifier = Modifier,
    hero: Boolean = false,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(GrozzRadiusPhoto))
                .background(GrozzSurface),
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        if (hero) {
                            Brush.verticalGradient(
                                colors =
                                    listOf(
                                        Color.Black.copy(alpha = 0.15f),
                                        Color.Black.copy(alpha = 0.85f),
                                    ),
                            )
                        } else {
                            Brush.verticalGradient(
                                colors =
                                    listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.88f),
                                    ),
                                startY = 40f,
                            )
                        },
                    ),
        )
        content()
    }
}

@Composable
fun GrozzPanel(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(GrozzRadiusPanel))
                .background(GrozzSurface)
                .border(1.dp, GrozzBorder, RoundedCornerShape(GrozzRadiusPanel)),
        content = content,
    )
}

@Composable
fun GrozzPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier.height(48.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = GrozzYellow,
                contentColor = GrozzOnPrimary,
                disabledContainerColor = GrozzYellow.copy(alpha = 0.5f),
                disabledContentColor = GrozzOnPrimary.copy(alpha = 0.7f),
            ),
        shape = RoundedCornerShape(GrozzRadiusButton),
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = GrozzOnPrimary,
                strokeWidth = 2.dp,
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = GrozzOnPrimary,
            )
        }
    }
}

/**
 * Shared dark outlined field matching Login/Register styling.
 */
@Composable
fun GrozzTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    maxLength: Int = 35,
    enabled: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = { if (it.length <= maxLength) onValueChange(it) },
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        enabled = enabled,
        placeholder = {
            Text(
                text = placeholder,
                color = GrozzMuted,
                fontFamily = Lexend,
            )
        },
        textStyle =
            TextStyle(
                color = GrozzOnBackground,
                fontSize = 16.sp,
                fontFamily = Lexend,
            ),
        singleLine = singleLine,
        shape = RoundedCornerShape(GrozzRadiusChip),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedContainerColor = GrozzSurface,
                unfocusedContainerColor = GrozzSurface,
                disabledContainerColor = GrozzSurface,
                focusedBorderColor = GrozzMuted,
                unfocusedBorderColor = GrozzBorder,
                disabledBorderColor = GrozzBorder,
                cursorColor = GrozzYellow,
                focusedTextColor = GrozzOnBackground,
                unfocusedTextColor = GrozzOnBackground,
            ),
    )
}

/**
 * Placeholder panel for features that are not shipping yet.
 * Meal uses the richer eyebrow / accent / footer; ActivityInside uses the simple form.
 */
@Composable
fun GrozzComingSoonPanel(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    eyebrow: String? = null,
    accentTitle: String? = null,
    footer: String? = null,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(GrozzRadiusPanel))
                .background(GrozzSurface)
                .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        if (eyebrow != null) {
            Box(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(GrozzYellow.copy(alpha = 0.25f))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = eyebrow.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = GrozzYellow,
                    fontFamily = Lexend,
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Text(
            text = title,
            fontFamily = Oswald,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            color = GrozzOnBackground,
            textAlign = TextAlign.Center,
        )
        if (accentTitle != null) {
            Text(
                text = accentTitle,
                fontFamily = Oswald,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = GrozzYellow,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = GrozzTextSecondary,
            textAlign = TextAlign.Center,
        )

        if (footer != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = footer.uppercase(),
                fontFamily = Lexend,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = GrozzYellow,
                textAlign = TextAlign.Center,
            )
        }
    }
}
