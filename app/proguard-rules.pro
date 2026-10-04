# ProGuard rules for mod3case2
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.example.mod3case2.data.model.** { *; }
