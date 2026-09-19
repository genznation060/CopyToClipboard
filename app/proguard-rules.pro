# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /sdk/tools/proguard/proguard-android.txt

# Keep data classes used with JSON
-keepclassmembers class com.quickcopy.app.data.** {
    <fields>;
}

# Keep Compose related if needed
-dontwarn androidx.compose.**
