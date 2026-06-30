# Keep Gson model fields (snapshot parsing) and Room generated code.
-keep class com.fantasychess.hpt.data.entity.** { *; }
-keepclassmembers class com.fantasychess.hpt.data.entity.** { *; }
-keepattributes Signature
-dontwarn org.jsoup.**
