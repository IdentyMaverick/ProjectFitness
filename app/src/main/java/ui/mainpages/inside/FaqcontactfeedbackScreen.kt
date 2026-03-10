package ui.mainpages.inside

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grozzbear.R
import data.local.viewmodel.FaqcontactfeedbackScreenViewModel

@Composable
fun FaqcontactfeedbackScreen(
    navController: NavController,
    faqcontactfeedbackScreenViewModel: FaqcontactfeedbackScreenViewModel
) {
    val isClicked = remember { mutableStateOf(false) }
    val context = LocalContext.current
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            HomeTopBarFaqcontactfeedback(navController)
        },
        containerColor = Color(0xFF121417),
        floatingActionButtonPosition = FabPosition.EndOverlay,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(top = 20.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Help &",
                color = Color.White,
                fontSize = 30.sp,
                modifier = Modifier.padding(horizontal = 30.dp),
                fontFamily = FontFamily(Font(R.font.lexendbold))
            )
            Text(
                text = "Feedback Hub",
                color = Color(0xFFF1C40F),
                fontSize = 30.sp,
                modifier = Modifier.padding(horizontal = 30.dp),
                fontFamily = FontFamily(Font(R.font.lexendbold))
            )
            Spacer(modifier = Modifier.height(50.dp))
            Text(
                text = "Talk to us",
                color = Color.White,
                fontSize = 20.sp,
                modifier = Modifier.padding(horizontal = 30.dp),
                fontFamily = FontFamily(Font(R.font.lexendbold))
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 30.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clickable(onClick = { launchEmailIntent(context) })
                        .background(
                            Color.White.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .size(150.dp)
                        .border(1.dp, Color(0xFFF1C40F), shape = RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(R.drawable.alternateemailicon128),
                            contentDescription = null,
                            modifier = Modifier
                                .background(
                                    color = Color(0xFFF1C40F),
                                    shape = RoundedCornerShape(100.dp)
                                )
                                .size(50.dp)
                                .padding(10.dp)
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                        Text(
                            text = "Email Support",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(R.font.lexendregular))
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "24h Response",
                            color = Color.White.copy(alpha = 0.2f),
                            fontSize = 10.sp,
                            fontFamily = FontFamily(Font(R.font.lexendregular))
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .clickable(enabled = false, onClick = {})
                        .background(
                            Color.White.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .size(150.dp)
                        .border(1.dp, Color(0xFFF1C40F), shape = RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(R.drawable.chatbubbleicon128),
                            contentDescription = null,
                            modifier = Modifier
                                .background(
                                    color = Color.Gray,
                                    shape = RoundedCornerShape(100.dp)
                                )
                                .size(50.dp)
                                .padding(10.dp)
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                        Text(
                            text = "Live Chat",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(R.font.lexendregular))
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "Instant Help",
                            color = Color.White.copy(alpha = 0.2f),
                            fontSize = 10.sp,
                            fontFamily = FontFamily(Font(R.font.lexendregular))
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                modifier = Modifier.padding(horizontal = 30.dp)
            ) {
                val isRated = remember { mutableStateOf("Neutral") }
                Box(
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .fillMaxWidth()
                        .height(250.dp)
                        .border(1.dp, Color(0xFFF1C40F), shape = RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Tell us what you think",
                            color = Color.White,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(horizontal = 30.dp),
                            fontFamily = FontFamily(Font(R.font.lexendbold))
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "How was your workout experience today?",
                            color = Color.White.copy(alpha = 0.2f),
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(R.font.lexendregular))
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                        Row(horizontalArrangement = Arrangement.Center) {
                            val rate = listOf<String>(
                                "Poor", "Ok", "Neutral", "Good", "Perfect!"
                            )
                            for (i in rate) {
                                Spacer(modifier = Modifier.width(15.dp))
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    IconButton(
                                        onClick = { isRated.value = i },
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Icon(
                                            painter = if (i == "Poor") {
                                                painterResource(R.drawable.sentimentextremelydissatisfied)
                                            } else if (i == "Ok") {
                                                painterResource(R.drawable.sentimentsadicon128)
                                            } else if (i == "Neutral") {
                                                painterResource(R.drawable.sentimentneutralicon128)
                                            } else if (i == "Good") {
                                                painterResource(R.drawable.sentimentsatisfiedicon128)
                                            } else {
                                                painterResource(R.drawable.sentimentsatisfiedicon128)
                                            },
                                            contentDescription = null,
                                            tint = if (i == isRated.value) {
                                                Color(0xFFF1C40F)
                                            } else {
                                                Color.White.copy(alpha = 0.2f)
                                            }
                                        )
                                    }
                                    Text(
                                        text = i,
                                        color = if (i == isRated.value) {
                                            Color(0xFFF1C40F)
                                        } else {
                                            Color.White.copy(alpha = 0.2f)
                                        },
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily(Font(R.font.lexendregular))
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                        Box(
                            modifier = Modifier
                                .clickable(
                                    onClick = {
                                        faqcontactfeedbackScreenViewModel.updateUserIdea(isRated.value)
                                        Toast.makeText(
                                            navController.context,
                                            "Thank you for your feedback!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        isClicked.value = true
                                    }, enabled = if (isClicked.value == true) false else true,
                                    indication = null,
                                    interactionSource = MutableInteractionSource()
                                )
                                .padding(horizontal = 20.dp)
                                .background(
                                    color = if (isClicked.value == false) Color(0xFFF1C40F) else Color.Gray,
                                    RoundedCornerShape(20.dp)
                                )
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 40.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Send Feedback",
                                    color = Color.Black,
                                    fontSize = 15.sp,
                                    fontFamily = FontFamily(Font(R.font.lexendbold))
                                )
                                Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                                Icon(
                                    painter = painterResource(R.drawable.sendicon128),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeTopBarFaqcontactfeedback(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = topPadding)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                painter = painterResource(R.drawable.left),
                contentDescription = null,
                modifier = Modifier.size(25.dp),
                tint = Color.White
            )
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = "SUPPORT CENTER",
            color = Color.White,
            fontSize = 20.sp,
            letterSpacing = 0.sp,
            fontFamily = FontFamily(Font(R.font.oswaldbold))
        )

        Spacer(Modifier.weight(1f))

        IconButton(onClick = {}) {
            Icon(
                painter = painterResource(R.drawable.projectfitnesspointheavy),
                contentDescription = null,
                modifier = Modifier.size(25.dp),
                tint = Color.Transparent
            )
        }
    }
}

fun launchEmailIntent(context: Context) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf("osmandenizsavasapple@hotmail.com"))
        putExtra(Intent.EXTRA_SUBJECT, "Support Request: [Topic]")
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Email app not found", Toast.LENGTH_SHORT).show()
    }
}
