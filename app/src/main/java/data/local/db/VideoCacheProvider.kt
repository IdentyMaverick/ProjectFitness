package data.local.db

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

@UnstableApi
object VideoCacheProvider {
    @Volatile
    private var cache: Cache? = null

    fun get(context: Context): Cache {
        return cache ?: synchronized(this) {
            cache ?: createCache(context).also {
                cache = it
            }
        }
    }

    private fun createCache(context: Context): Cache {
        val cacheDir = File(context.cacheDir, "video_cache")
        val evictor = LeastRecentlyUsedCacheEvictor(200 * 1024 * 1024)
        val databaseProvider = StandaloneDatabaseProvider(context.applicationContext)

        return SimpleCache(cacheDir, evictor, databaseProvider)
    }
}