package database
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class ProjectUserConverter {
    @TypeConverter
    fun fromImageBitmap(imageBitmap: ImageBitmap?): ByteArray? {
        if (imageBitmap == null) return null
        val stream = ByteArrayOutputStream()
        val bitmap = imageBitmap.asAndroidBitmap()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    @TypeConverter
    fun toImageBitmap(bytes: ByteArray?): ImageBitmap? {
        if (bytes == null) return null
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        return bitmap.asImageBitmap()
    }
}
