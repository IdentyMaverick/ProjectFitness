-keepattributes Signature, Annotation, InnerClasses, EnclosingMethod
-dontwarn androidx.compose.**
-dontwarn androidx.compose.runtime.ParcelableSnapshotMutationPolicy
-keep class androidx.compose.runtime.ParcelableSnapshotMutationPolicy { *; }

-keep class com.grozzbear.data.models.** { *; }
-keep class com.google.gson.** { *; }

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

-keepnames class com.grozzbear.models.** { *; }

-keepattributes Signature
-keepattributes *Annotation*

-keep class com.google.firebase.** { *; }