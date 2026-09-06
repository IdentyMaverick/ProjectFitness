package com.grozzbear.ui.util

import android.content.Context
import android.content.res.Resources
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.grozzbear.R

/**
 * Room stores workout cover images as [R.drawable] ints.
 * Those IDs are NOT stable across app builds, so an old DB value can crash
 * [painterResource]. Always resolve through this helper.
 */
@DrawableRes
fun Context.safeWorkoutImageRes(
    storedId: Int,
    @DrawableRes fallback: Int = R.drawable.infohorizontalscreensecondphoto,
): Int {
    if (storedId == 0) return fallback
    return try {
        if (resources.getResourceTypeName(storedId) == "drawable") storedId else fallback
    } catch (_: Resources.NotFoundException) {
        fallback
    } catch (_: Exception) {
        fallback
    }
}

@Composable
fun rememberSafeWorkoutImageRes(
    storedId: Int,
    @DrawableRes fallback: Int = R.drawable.infohorizontalscreensecondphoto,
): Int {
    val context = LocalContext.current
    return remember(storedId, fallback) {
        context.safeWorkoutImageRes(storedId, fallback)
    }
}

@Composable
fun safeWorkoutPainter(storedId: Int, @DrawableRes fallback: Int = R.drawable.infohorizontalscreensecondphoto) =
    painterResource(id = rememberSafeWorkoutImageRes(storedId, fallback))
