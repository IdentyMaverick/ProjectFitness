-keepattributes Signature, Annotation, InnerClasses, EnclosingMethod
-dontwarn androidx.compose.**
-dontwarn androidx.compose.runtime.ParcelableSnapshotMutationPolicy
-keep class androidx.compose.runtime.ParcelableSnapshotMutationPolicy { *; }

-keep class com.grozzbear.data.models.** { *; }
-keep class com.google.gson.** { *; }

-keep class dagger.hilt.** { *; }
-keepattributes *Annotation*

-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.**

-keep class com.airbnb.lottie.** { *; }
-keep class coil.** { *; }
-dontwarn coil.**
-keep class com.patrykandpatrick.vico.** { *; }

-keep class com.google.firebase.** { *; }

-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}