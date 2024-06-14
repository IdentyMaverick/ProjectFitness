
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.projectfitness.R


fun showNotificationChann(context : Context){
    val notificationId = 1
    val notification = NotificationCompat.Builder(context, "")
        .setContentTitle("Başlık")
        .setContentText("İçerik")
        .setSmallIcon(R.drawable.projectfitnesslogologin)
        .build()
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {

    }
    else{
    NotificationManagerCompat.from(context).notify(notificationId , notification) }
}