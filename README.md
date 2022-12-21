<h1 align="center">CustomTab</h1>
<p align="center">
  <img src="https://jitpack.io/v/degpeg-media/degpeg-video-call-sdk/month.svg"/>
  <img src="https://jitpack.io/v/degpeg-media/degpeg-video-call-sdk.svg"/>
</p>

CustomTab library helps to use Custom Tabs on top of the AndroidX browser support library.

Custom Tabs is a browser feature, introduced by Chrome, that is now supported by most major browsers on Android. It give apps more control over their web experience, and make transitions between native and web content more seamless without having to resort to a WebView.

# Preview
<p float="left">
<img src="https://github.com/degpeg-media/degpeg-video-call-sdk/blob/master/app/Screenshot_1.png" alt="dashboard" width="200" height="400"> 
<img src="https://github.com/degpeg-media/degpeg-video-call-sdk/blob/master/app/Screenshot_2.png" alt="dashboard" width="200" height="400">
</p>

# SDK initialization and setup

1. Add the JitPack repository to your project level build file

 ```groovy
allprojects {
    repositories {
         maven {
            url "https://jitpack.io"
        }
    }
}
```

2. Add the dependency to your app level build file

```groovy
dependencies {
     implementation 'com.github.degpeg-media:degpeg-video-call-sdk:release_version'
}
```

# Usage

* Launch customTab
```kotlin
VideoCallSDKHelper.with(this)
    .launchUrl(url)
```

# Customization
Update the browser theme using the SDK methods
```kotlin
VideoCallSDKHelper.with(this)
    .setToolbarColor(getColor("#000000"))
    .setSecondaryToolbarColor(getColor("#FFFFFF"))
    .setNavigationBarColor(Color.BLACK)
    .setNavigationBarDividerColor(Color.GREEN)
    .launchUrl(url)
```