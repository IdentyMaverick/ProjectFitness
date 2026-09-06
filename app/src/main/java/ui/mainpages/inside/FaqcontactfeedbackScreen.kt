package ui.mainpages.inside

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grozzbear.R
import com.grozzbear.ui.theme.GrozzError
import com.grozzbear.ui.theme.GrozzRadiusButton
import com.grozzbear.ui.theme.GrozzRadiusChip
import com.grozzbear.ui.theme.GrozzRadiusPanel
import com.grozzbear.ui.theme.GrozzSurface
import com.grozzbear.ui.theme.GrozzSystemBar
import com.grozzbear.ui.theme.GrozzYellow
import com.grozzbear.ui.theme.Lexend
import data.local.viewmodel.FaqcontactfeedbackScreenViewModel

private val SupportCardBg = GrozzSurface

private data class FaqItem(val question: String, val answer: String)

private data class FeedbackMood(val label: String, val iconRes: Int, val color: Color)

@Composable
fun FaqcontactfeedbackScreen(
    navController: NavController,
    faqcontactfeedbackScreenViewModel: FaqcontactfeedbackScreenViewModel,
) {
    val context = LocalContext.current
    var selectedMood by remember { mutableStateOf("Neutral") }
    var feedbackSent by remember { mutableStateOf(false) }
    var expandedFaqIndex by remember { mutableStateOf<Int?>(null) }

    val moods =
        remember {
            listOf(
                FeedbackMood("Poor", R.drawable.sentimentextremelydissatisfied, GrozzError),
                FeedbackMood("Ok", R.drawable.sentimentsadicon128, Color(0xFFFF8A3D)),
                FeedbackMood("Neutral", R.drawable.sentimentneutralicon128, Color(0xFF8E9AAB)),
                FeedbackMood("Good", R.drawable.sentimentsatisfiedicon128, GrozzYellow),
                FeedbackMood("Perfect", R.drawable.sentimentsatisfiedicon128, Color(0xFF2ECC71)),
            )
        }

    val faqItems =
        remember {
            listOf(
                FaqItem(
                    question = "How do I start a workout?",
                    answer =
                        "Open Home, pick a workout from Challenges or Coach plans, " +
                            "then tap Start Training to begin logging sets.",
                ),
                FaqItem(
                    question = "Where can I see my progress?",
                    answer =
                        "Open your Profile and check Lifetime Statistics for workouts completed, " +
                            "total weight lifted, time spent, and consistency score.",
                ),
                FaqItem(
                    question = "How do I change my profile photo?",
                    answer =
                        "Go to Profile, tap your avatar or the camera badge, " +
                            "then choose a new photo from your gallery.",
                ),
                FaqItem(
                    question = "How do followers work?",
                    answer =
                        "Open Profile → Followers or Following to manage your fitness circle. " +
                            "You can follow back, unfollow, or visit another athlete’s profile.",
                ),
                FaqItem(
                    question = "I found a bug. What should I do?",
                    answer =
                        "Use Email Support below and include what you were doing, " +
                            "your device model, and a short description of the issue.",
                ),
            )
        }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            SettingsFlowTopBar(
                title = "SUPPORT",
                onBack = { navController.popBackStack() },
            )
        },
        containerColor = GrozzSystemBar,
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 28.dp),
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Help & ",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontFamily = Lexend,
                )
                Text(
                    text = "Feedback",
                    color = GrozzYellow,
                    fontSize = 18.sp,
                    fontFamily = Lexend,
                )
            }
            Text(
                text = "Get answers fast, or tell us how we’re doing.",
                color = Color.White.copy(alpha = 0.45f),
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp),
                fontFamily = Lexend,
            )

            Spacer(modifier = Modifier.height(20.dp))

            SectionTitle("Talk to us")

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                SupportContactCard(
                    title = "Email Support",
                    subtitle = "24h response",
                    iconRes = R.drawable.alternateemailicon128,
                    enabled = true,
                    modifier = Modifier.weight(1f),
                    onClick = { launchEmailIntent(context) },
                )
                SupportContactCard(
                    title = "Live Chat",
                    subtitle = "Coming soon",
                    iconRes = R.drawable.chatbubbleicon128,
                    enabled = false,
                    modifier = Modifier.weight(1f),
                    onClick = {},
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            SectionTitle("FAQ")

            faqItems.forEachIndexed { index, item ->
                FaqAccordionItem(
                    item = item,
                    expanded = expandedFaqIndex == index,
                    onClick = {
                        expandedFaqIndex = if (expandedFaqIndex == index) null else index
                    },
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 5.dp),
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            SectionTitle("Your feedback")

            Box(
                modifier =
                    Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(GrozzRadiusPanel))
                        .background(SupportCardBg)
                        .border(1.dp, GrozzYellow.copy(alpha = 0.35f), RoundedCornerShape(GrozzRadiusPanel))
                        .padding(vertical = 22.dp, horizontal = 16.dp),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Tell us what you think",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontFamily = Lexend,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "How was your workout experience today?",
                        color = Color.White.copy(alpha = 0.45f),
                        fontSize = 13.sp,
                        fontFamily = Lexend,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        moods.forEach { mood ->
                            val selected = selectedMood == mood.label
                            val moodColor = if (selected) mood.color else mood.color.copy(alpha = 0.35f)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier =
                                    Modifier
                                        .clip(RoundedCornerShape(GrozzRadiusChip))
                                        .clickable(enabled = !feedbackSent) {
                                            selectedMood = mood.label
                                        }.padding(horizontal = 4.dp, vertical = 4.dp),
                            ) {
                                Icon(
                                    painter = painterResource(mood.iconRes),
                                    contentDescription = mood.label,
                                    tint = moodColor,
                                    modifier = Modifier.size(24.dp),
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = mood.label,
                                    color = moodColor,
                                    fontSize = 10.sp,
                                    fontFamily = Lexend,
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    Button(
                        onClick = {
                            faqcontactfeedbackScreenViewModel.updateUserIdea(selectedMood)
                            Toast
                                .makeText(
                                    context,
                                    "Thank you for your feedback!",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            feedbackSent = true
                        },
                        enabled = !feedbackSent,
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = GrozzYellow,
                                disabledContainerColor = Color.Gray,
                                contentColor = Color.Black,
                                disabledContentColor = Color.Black.copy(alpha = 0.7f),
                            ),
                        shape = RoundedCornerShape(GrozzRadiusButton),
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                    ) {
                        Text(
                            text = if (feedbackSent) "Feedback Sent" else "Send Feedback",
                            fontSize = 15.sp,
                            fontFamily = Lexend,
                        )
                        if (!feedbackSent) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                painter = painterResource(R.drawable.sendicon128),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = Color.White,
        fontSize = 16.sp,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
        fontFamily = Lexend,
    )
}

@Composable
private fun SupportContactCard(
    title: String,
    subtitle: String,
    iconRes: Int,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val borderColor = if (enabled) GrozzYellow.copy(alpha = 0.6f) else Color.Gray.copy(alpha = 0.35f)
    val iconBg = if (enabled) GrozzYellow else Color.Gray

    Box(
        modifier =
            modifier
                .height(120.dp)
                .clip(RoundedCornerShape(GrozzRadiusPanel))
                .background(SupportCardBg)
                .border(1.dp, borderColor, RoundedCornerShape(GrozzRadiusPanel))
                .clickable(enabled = enabled, onClick = onClick)
                .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier =
                    Modifier
                        .size(48.dp)
                        .background(iconBg, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = if (enabled) Color.Black else Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontFamily = Lexend,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 12.sp,
                fontFamily = Lexend,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun FaqAccordionItem(item: FaqItem, expanded: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SupportCardBg)
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = item.question,
                color = Color.White,
                fontSize = 14.sp,
                fontFamily = Lexend,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = GrozzYellow,
                modifier = Modifier.size(20.dp),
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            Text(
                text = item.answer,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 13.sp,
                lineHeight = 19.sp,
                fontFamily = Lexend,
                modifier = Modifier.padding(top = 10.dp),
            )
        }
    }
}

fun launchEmailIntent(context: Context) {
    val intent =
        Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("osmandenizsavasapple@hotmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Support Request: Grozz Fitness")
        }
    try {
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, "Email app not found", Toast.LENGTH_SHORT).show()
    }
}
