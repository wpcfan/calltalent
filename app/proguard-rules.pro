# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\peng\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Google Play services
#-keep class * extends java.util.ListResourceBundle {
#    protected Object[][] getContents();
#}
#-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
#    public static final *** NULL;
#}
#-keepnames @com.google.android.gms.common.annotation.KeepName class *
#-keepclassmembernames class * {
#    @com.google.android.gms.common.annotation.KeepName *;
#}

# Parcelable
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Required by retrolambda
-dontwarn java.lang.invoke.*

# Support library
-dontwarn android.support.v4.**
-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }

# required by picasso
-dontwarn com.squareup.okhttp.**

#Retrofit/OKHttp/Moshi
-dontwarn okio.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keep class com.squareup.moshi.** { *; }
-keepattributes Signature
-keepattributes Exceptions

#RxJava Library
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

#Wilddog
-dontwarn com.wilddog.client.**
-keep class com.wilddog.client.** { *; }
-keep interface com.wilddog.client.** { *; }
-dontwarn com.shaded.fasterxml.**

#icepick
-dontwarn icepick.**
-keep class icepick.** { *; }
-keep class **$$Icepick { *; }
-keepclasseswithmembernames class * {
    @icepick.* <fields>;
}
-keepnames class * { @icepick.State *;}

# ALSO REMEMBER KEEPING YOUR MODEL CLASSES
-keep class com.soulkey.calltalent.domain.** { *; }
