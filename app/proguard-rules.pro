# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/anubhav/Android/Sdk/tools/proguard/proguard-android.txt
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
-libraryjars libs

-dontwarn "com.google.android.gms.**"
-dontwarn "com.facebook.android.**"
-dontwarn "com.mcxiaoke.volley.**"
-dontwarn "com.google.code.gson.**"
-dontwarn "com.google.maps.android.**"
-dontwarn "io.branch.sdk.android.**"
-dontwarn "com.moengage.**"
-dontwarn com.notikum.notifypassive.utils.**

-assumenosideeffects class android.util.Log { *; }

-keepclassmembers class com.oyo.consumer.api.** { *;}

##---------------Begin: proguard configuration common for all Android apps ----------
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
-dump class_files.txt
-printseeds seeds.txt
-printusage unused.txt
-printmapping mapping.txt
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-allowaccessmodification
-keepattributes *Annotation*
-renamesourcefileattribute SourceFile
-repackageclasses ''

-keepattributes SourceFile,LineNumberTable
-keep class com.crashlytics.** { *; }
-keepclassmembers class com.crashlytics.** { *; }

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-dontnote com.android.vending.licensing.ILicensingService

# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn't save them.
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Preserve all native method names and the names of their classes.
-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Preserve static fields of inner classes of R classes that might be accessed
# through introspection.
-keepclassmembers class **.R$* {
  public static <fields>;
}

# Preserve the special static methods that are required in all enumeration classes.
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#-keep public class * {
#    public protected *;
#}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
##---------------End: proguard configuration common for all Android apps ----------

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.oyo.consumer.api.** { *; }
-keep class com.oyo.consumer.webview.** { *; }
-keep class google.place.** { *; }
-keep class com.oyo.consumer.interfaces.OlaMoneyInterface { *; }


-keep class com.google.android.gms.** { *; }
-keep class com.facebook.android.** { *; }
-keep class com.mcxiaoke.volley.** { *; }
-keep class com.google.code.gson.** { *; }
-keep class com.google.maps.android.** { *; }
-keep class com.android.support.** { *; }
-keep class io.branch.sdk.android.** { *; }
-keep class io.branch.sdk.android.** { *; }
-keep class com.moengage.** { *; }

-keep interface com.google.maps.android.** { *; }
-keep interface com.google.android.gms.** { *; }
-keep interface com.android.support.** { *; }

-keep @interface com.google.android.gms.** { *;}
-keep @interface com.google.maps.android.** { *;}


-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keepattributes Exceptions, Signature

##---------------End: proguard configuration for Gson  ----------

-dontwarn com.google.android.gms.location.**
-dontwarn com.google.android.gms.gcm.**
-dontwarn com.google.android.gms.iid.**

-keep class com.google.android.gms.gcm.** { *; }
-keep class com.google.android.gms.iid.** { *; }
-keep class com.google.android.gms.location.** { *; }

-keep class org.apache.http.** { *; }
-keep interface org.apache.http.** { *; }

-dontwarn org.apache.**

-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient
-dontwarn com.google.android.gms.**
-dontwarn com.android.volley.toolbox.**


##---------------Start: proguard configuration for MOE  ----------
-keep class com.moe.pushlibrary.internal.GeoTask
-dontwarn com.moe.pushlibrary.internal.GeoTask

-dontwarn com.moengage.receiver.*
-dontwarn com.moengage.worker.*

-dontwarn com.google.android.gms.location.**
-dontwarn com.google.android.gms.gcm.**
-dontwarn com.google.android.gms.iid.**

-keep class com.google.android.gms.gcm.** { *; }
-keep class com.google.android.gms.iid.** { *; }
-keep class com.google.android.gms.location.** { *; }

-keep class com.moe.** { *; }
-keep class com.moengage.** { *; }

-dontwarn com.moe.pushlibrary.internal.GeoTask
-dontwarn com.moengage.receiver.*
-dontwarn com.moengage.worker.*

-keep class com.delight.**  { *; }

-keepattributes JavascriptInterface

# Apsalar proguard.
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient
-keep class com.apsalar.sdk.** { *; }

# glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
-keep class com.bumptech.glide.integration.volley.VolleyGlideModule


#uninstall io
-keep class com.notikum.notifypassive.** {*;}
-keep public class com.google.android.gms.** {*;}


#localytics
-keep class com.localytics.android.** {*;}

#Crashalytics
-keep class io.fabric.sdk.** {*;}

-keepattributes EnclosingMethod, InnerClasses

-keep class in.juspay.** {*;}

# Hotline SDK Classes
-keep class com.freshdesk.hotline.** { *; }

#------ WIBMO -----
-keep class com.enstage.wibmo.sdk.** { *; }
-keepclassmembers class com.enstage.wibmo.sdk.inapp.pojo.** { *; }
-keep class com.enstage.wibmo.sdk.inapp.InAppBrowserActivity$* {
  *;
}
-keep class com.enstage.wibmo.sdk.inapp.InAppShellJavaScriptInterface {
  *;
}
-keep class com.enstage.wibmo.util.** { *; }
-keep public class org.jsoup.** {
public *;
}
-keep class com.example.anubhav.musicapp.Model.SongsModel {
*;
}
-keep class com.example.anubhav.musicapp.Model.MusicModel {
*;
}
-keep class com.example.anubhav.musicapp.Model.AlbumModel {
*;
}
-keep class com.fasterxml.jackson.** { *; }
-keep class com.wooplr.spotlight.** { *; }
-keep interface com.wooplr.spotlight.**
-keep enum com.wooplr.spotlight.**

-dontwarn com.squareup.okhttp.**
-dontwarn com.fasterxml.jackson.databind.**
-dontwarn okio.**
-dontwarn android.test.**
#------ WIBMO -----