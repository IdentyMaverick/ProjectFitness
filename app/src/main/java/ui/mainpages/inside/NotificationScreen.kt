package ui.mainpages.inside

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grozzbear.R
import com.grozzbear.ui.theme.GrozzSurface
import com.grozzbear.ui.theme.GrozzSystemBar
import com.grozzbear.ui.theme.GrozzYellow
import com.grozzbear.ui.theme.Lexend
import com.grozzbear.ui.theme.Oswald
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import ui.mainpages.navigation.Screens
import viewmodel.NotificationModel
import viewmodel.SocialViewModel

private val NotificationCardBg = GrozzSurface
private val NotificationUnreadBg = GrozzSurface

@Composable
fun NotificationScreen(navController: NavController, socialViewModel: SocialViewModel) {
    val nickname by socialViewModel.nickname.collectAsState()
    val notifications by remember(nickname) {
        socialViewModel.getNotification(nickname)
    }.collectAsState(initial = emptyList())

    // Key on unread state too — nickname alone often fired before the list loaded,
    // so markAllAsRead never ran and the Home badge stayed stuck.
    val hasUnread = notifications.any { !it.isRead }
    LaunchedEffect(nickname, hasUnread) {
        if (nickname.isNotBlank() && hasUnread) {
            socialViewModel.markAllAsRead(nickname)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            HomeTopBarNotificationScreen(navController = navController)
        },
        containerColor = GrozzSystemBar,
        floatingActionButtonPosition = FabPosition.EndOverlay,
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        when {
            nickname.isBlank() -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = GrozzYellow)
                }
            }

            notifications.isEmpty() -> {
                NotificationEmptyState(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                )
            }

            else -> {
                val unreadCount = notifications.count { !it.isRead }
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    contentPadding =
                        androidx.compose.foundation.layout.PaddingValues(
                            horizontal = 20.dp,
                            vertical = 16.dp,
                        ),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (unreadCount > 0) {
                        item(key = "unread_header") {
                            Text(
                                text =
                                    if (unreadCount == 1) {
                                        "1 new notification"
                                    } else {
                                        "$unreadCount new notifications"
                                    },
                                color = GrozzYellow,
                                fontFamily = Lexend,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(bottom = 4.dp),
                            )
                        }
                    }

                    items(
                        items = notifications,
                        key = { it.id.ifBlank { "${it.time}_${it.title}" } },
                    ) { item ->
                        NotificationCard(
                            item = item,
                            onClick = {
                                extractFollowerNickname(item.message)?.let { follower ->
                                    navController.navigate(
                                        Screens.OtherScreenProfile.createRoute(follower),
                                    ) {
                                        popUpTo(Screens.OtherScreenProfile.route) {
                                            inclusive = true
                                        }
                                        launchSingleTop = true
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(item: NotificationModel, onClick: () -> Unit) {
    val cardBackground = if (!item.isRead) NotificationUnreadBg else NotificationCardBg
    val borderColor = if (!item.isRead) GrozzYellow.copy(alpha = 0.35f) else Color.Transparent

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(cardBackground)
                .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                .clickable(onClick = onClick)
                .padding(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        if (!item.isRead) {
            Box(
                modifier =
                    Modifier
                        .padding(top = 6.dp, end = 10.dp)
                        .size(8.dp)
                        .background(GrozzYellow, CircleShape),
            )
        }

        Box(
            modifier =
                Modifier
                    .size(44.dp)
                    .background(GrozzYellow.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(notificationIconFor(item.title)),
                contentDescription = null,
                tint = GrozzYellow,
                modifier = Modifier.size(24.dp),
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = item.title,
                    color = if (!item.isRead) GrozzYellow else Color.White,
                    fontSize = 15.sp,
                    fontFamily = Lexend,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = formatNotificationTime(item.time),
                    color = Color.White.copy(alpha = 0.45f),
                    fontSize = 12.sp,
                    fontFamily = Lexend,
                )
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = item.message,
                color = Color.White.copy(alpha = if (!item.isRead) 0.9f else 0.65f),
                fontSize = 14.sp,
                fontFamily = Lexend,
                lineHeight = 20.sp,
            )
        }
    }
}

@Composable
private fun NotificationEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .size(72.dp)
                    .background(GrozzYellow.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.circlenotifications),
                contentDescription = null,
                tint = GrozzYellow.copy(alpha = 0.7f),
                modifier = Modifier.size(36.dp),
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "No notifications yet",
            color = Color.White,
            fontSize = 20.sp,
            fontFamily = Oswald,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "When someone follows you or you get updates, they'll show up here.",
            color = Color.Gray,
            fontSize = 14.sp,
            fontFamily = Lexend,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
        )
    }
}

@Composable
fun HomeTopBarNotificationScreen(navController: NavController) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                painter = painterResource(R.drawable.left),
                contentDescription = "Back",
                modifier = Modifier.size(30.dp),
                tint = Color.White,
            )
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = "NOTIFICATIONS",
            color = Color.White,
            fontSize = 20.sp,
            fontFamily = Oswald,
            style = TextStyle(letterSpacing = 0.sp),
        )

        Spacer(Modifier.weight(1f))

        Spacer(Modifier.size(48.dp))
    }
}

private fun notificationIconFor(title: String): Int = when (title.lowercase(Locale.getDefault())) {
    "follow" -> R.drawable.personadd
    else -> R.drawable.circlenotifications
}

private fun extractFollowerNickname(message: String): String? {
    val trimmed = message.trim()
    val followMatch = Regex("^(.+?) has followed you\\.?$").find(trimmed)
    return followMatch
        ?.groupValues
        ?.getOrNull(1)
        ?.trim()
        ?.takeIf { it.isNotBlank() }
}

fun formatNotificationTime(timeMillis: Long): String {
    if (timeMillis <= 0L) return ""

    val now = System.currentTimeMillis()
    val diff = (now - timeMillis).coerceAtLeast(0L)

    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
        diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)}m ago"
        diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)}h ago"
        diff < TimeUnit.DAYS.toMillis(2) -> "Yesterday"
        diff < TimeUnit.DAYS.toMillis(7) -> "${TimeUnit.MILLISECONDS.toDays(diff)}d ago"
        else -> SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(timeMillis))
    }
}
