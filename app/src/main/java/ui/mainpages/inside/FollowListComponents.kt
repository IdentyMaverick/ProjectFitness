package ui.mainpages.inside

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.grozzbear.R
import com.grozzbear.ui.theme.GrozzSurface
import com.grozzbear.ui.theme.GrozzYellow
import com.grozzbear.ui.theme.Lexend
import com.grozzbear.ui.theme.Oswald
import data.remote.User
import ui.mainpages.navigation.Screens
import viewmodel.AuthViewModel

private val FollowCardBg = GrozzSurface
private val FollowSearchBg = Color(0xFF21282F)

enum class FollowButtonStyle {
    Follow,
    FollowBack,
    Following
}

@Composable
fun FollowListLoadingState(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = GrozzYellow)
    }
}

@Composable
fun FollowListTopBar(
    title: String,
    navController: NavController,
    topPadding: Dp = if (Build.VERSION.SDK_INT >= 35) 50.dp else 0.dp
) {
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
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = title,
            color = Color.White,
            fontFamily = Oswald,
            fontSize = 20.sp
        )

        Spacer(Modifier.weight(1f))
        Spacer(Modifier.size(48.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(52.dp),
        placeholder = {
            Text(
                text = placeholder,
                color = Color.White.copy(alpha = 0.35f),
                fontFamily = Lexend
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.45f)
            )
        },
        shape = RoundedCornerShape(14.dp),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = FollowSearchBg,
            unfocusedContainerColor = FollowSearchBg,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = GrozzYellow
        ),
        textStyle = TextStyle(
            color = Color.White,
            fontSize = 14.sp,
            fontFamily = Lexend
        )
    )
}

@Composable
fun FollowSectionHeader(
    title: String,
    count: Int? = null,
    accent: Boolean = false,
    modifier: Modifier = Modifier
) {
    val label = if (count != null) "$title · $count" else title
    Text(
        text = label,
        color = if (accent) GrozzYellow else Color.White,
        fontSize = 16.sp,
        fontFamily = Lexend,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

@Composable
fun FollowUserRow(
    user: User,
    buttonStyle: FollowButtonStyle,
    onProfileClick: () -> Unit,
    onFollowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(FollowCardBg)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FollowUserAvatar(
            user = user,
            onClick = onProfileClick
        )

        Spacer(Modifier.width(14.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onProfileClick)
        ) {
            Text(
                text = user.first.ifBlank { user.nickname },
                color = Color.White,
                fontSize = 16.sp,
                fontFamily = Lexend,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "@${user.nickname}",
                color = GrozzYellow,
                fontSize = 13.sp,
                fontFamily = Lexend,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.width(10.dp))

        FollowActionButton(
            style = buttonStyle,
            onClick = onFollowClick
        )
    }
}

@Composable
private fun FollowUserAvatar(
    user: User,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .border(2.dp, GrozzYellow, CircleShape)
            .padding(2.dp)
            .border(2.dp, Color.Black, CircleShape)
            .padding(3.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(
                    user.userPhotoUri.ifEmpty {
                        R.drawable.grozzholdsdumbbellbothhandsnobackgroundxml
                    }
                )
                .crossfade(true)
                .build(),
            contentDescription = "Profile picture",
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .clickable(onClick = onClick),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun FollowActionButton(
    style: FollowButtonStyle,
    onClick: () -> Unit
) {
    val isFollowing = style == FollowButtonStyle.Following
    val label = when (style) {
        FollowButtonStyle.Following -> "Following"
        FollowButtonStyle.FollowBack -> "Follow Back"
        FollowButtonStyle.Follow -> "Follow"
    }

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isFollowing) Color.White.copy(alpha = 0.1f) else GrozzYellow
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.height(34.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp)
    ) {
        Text(
            text = label,
            color = if (isFollowing) Color.White else Color.Black,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Lexend
        )
    }
}

@Composable
fun FollowEmptyState(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(GrozzYellow.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.accountcircle),
                contentDescription = null,
                tint = GrozzYellow.copy(alpha = 0.7f),
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = title,
            color = Color.White,
            fontSize = 20.sp,
            fontFamily = Oswald,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = subtitle,
            color = Color.Gray,
            fontSize = 14.sp,
            fontFamily = Lexend,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}

fun openUserProfile(
    navController: NavController,
    authViewModel: AuthViewModel,
    nickname: String
) {
    if (nickname.isBlank()) return
    authViewModel._totalWorkoutNumber.value = 0
    authViewModel._totalLiftedWeight.value = 0F
    // Replace any existing other-profile (and screens above it) so
    // Profile A → … → Profile B doesn't leave a deep back-stack chain.
    navController.navigate(Screens.OtherScreenProfile.createRoute(nickname)) {
        popUpTo(Screens.OtherScreenProfile.route) {
            inclusive = true
        }
        launchSingleTop = true
    }
}
