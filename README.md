# AttachmentManager

[![](https://jitpack.io/v/Zaid-Mirza/AttachmentManager.svg)](https://jitpack.io/#Zaid-Mirza/AttachmentManager)
[![API](https://img.shields.io/badge/API-23%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=23)
![Language](https://img.shields.io/badge/language-Kotlin-orange.svg)
<br/>
<div align="center">
  <sub>Developed by
  <a href="https://twitter.com/zaidmirzait">Zaid Mirza</a> and
  <a href="https://github.com/Zaid-Mirza/AttachmentManager/graphs/contributors">
    contributors
  </a>
</div>
<br/>
                                                                         
You can use this light weight library to implement attachment feature (taking picture using camera, picking up file/image from gallery or file system or google drive).
The library helps you to simplify all the process related to picking files without worrying about system permissions

### Language Support

* English
* Arabic

### Warning!

1. This library is build using **AndroidX**.So, I recommend you to migrate your project to **AndroidX** otherwise it may cause problem using both androidx and support libs togather.

2. You might face error ``` Invoke-customs are only supported starting with android 0 --min-api 26  ``` 
.To solve this add below lines in app level **build.gradle** file.

```groovy
compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
```



# Prerequisite

1. Add permissions and provider in **AndroidManifest.xml**

```xml
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE"
    tools:ignore="ScopedStorage" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.CAMERA" />
```
```xml
 <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.attachmentmanager"
            android:exported="false"
            android:grantUriPermissions="true"
            tools:replace="android:authorities">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_provider"
                tools:replace="android:resource" />
  </provider>
```

2. Create **file_provider.xml** in res/xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <external-path
        name="myApp"
        path="Download/" />
    <external-files-path
        name="images"
        path="Pictures" />
</paths>
```

3. If you are targeting Android 11+, you need to add following queries in **AndroidManifest.xml**
```xml
<queries>
        <intent>
            <action android:name="android.intent.action.OPEN_DOCUMENT" />
            <!-- If you don't know the MIME type in advance, set "mimeType" to "*/*". -->
            <data android:mimeType="*/*" />
        </intent>
        <intent>
            <action android:name="android.intent.action.PICK" />
            <!-- If you don't know the MIME type in advance, set "mimeType" to "*/*". -->
            <data android:mimeType="*/*" />
        </intent>
    </queries>
  
  ```

4. Update  project level **build.gradle** file.
```groovy
allprojects {
   repositories {
      	jcenter()
       	maven { url "https://jitpack.io" }  //Make sure to add this in your project
   }
}
```

```groovy
   implementation 'com.github.Zaid-Mirza:AttachmentManager:1.1.8'
```

# Usage


1. Initiate AttachmentManager object using builder pattern

  **Kotlin**

```kotlin
private var attachmentManager: AttachmentManager? = null
var gallery = arrayOf("image/png",
            "image/jpg",
            "image/jpeg")
    var files = arrayOf("application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",  // .ppt & .pptx
            "application/pdf")

override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
      attachmentManager = AttachmentManager.AttachmentBuilder(this) // must pass Context
            .fragment(null) // pass fragment reference if you are in fragment
            .setUiTitle("Choose File") // title of dialog or bottom sheet
            .allowMultiple(false) // set true if you want make multiple selection, default is false
            .asBottomSheet(true) // set true if you need to show selection as bottom sheet, default is as Dialog
            .setOptionsTextColor(android.R.color.holo_green_light) // change text color
            .setImagesColor(R.color.colorAccent) // change icon color
            .hide(HideOption.DOCUMENT) // You can hide any option do you want
            .setMaxPhotoSize(200000) // Set max  photo size in bytes
            .galleryMimeTypes(gallery) // mime types for gallery
            .filesMimeTypes(files) // mime types for files
            .build(); // Hide any of the three options
       
    }
    
```
**Java**
```java
    private AttachmentManager attachmentManager = null;
    String[] gallery = {"image/png",
            "image/jpg",
            "image/jpeg"};
    String[] files  = { "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .ppt & .pptx
            "application/pdf"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       attachmentManager = new AttachmentManager.AttachmentBuilder(this) // must pass Context
                .fragment(null) // pass fragment reference if you are in fragment
                .setUiTitle("Choose File") // title of dialog or bottom sheet
                .allowMultiple(false) // set true if you want make multiple selection, default is false
                .asBottomSheet(true) // set true if you need to show selection as bottom sheet, default is as Dialog
                .setOptionsTextColor(android.R.color.holo_green_light) // change text color
                .setImagesColor(R.color.colorAccent) // change icon color
                .hide(HideOption.DOCUMENT) // You can hide any option do you want
                .setMaxPhotoSize(200000) // Set max  photo size in bytes
                .galleryMimeTypes(gallery) // mime types for gallery
                .filesMimeTypes(files) // mime types for files
                .build(); // Hide any of the three options
    }
```

3. Declare registerForActivityResult

**Kotlin**
```kotlin
 private var mLauncher = registerForActivityResult(StartActivityForResult()) { result ->

    val list =  attachmentManager?.manipulateAttachments(this,result.resultCode,result.data)
    Toast.makeText(this, list?.size.toString(), Toast.LENGTH_LONG).show()
}
````
**Java**
```java
 ActivityResultLauncher<Intent> mLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        ArrayList<AttachmentDetail> list = attachmentManager.manipulateAttachments(this,result.getResultCode(),result.getData());

        });
```
4. Call **openSelection()** method to show selection UI and pass ActivityResultLauncher

**Kotlin**
```kotlin
 attachmentManager?.openSelection(mLauncher)
````
**Java**
```java
attachmentManager.openSelection(mLauncher);
```

5. Override onRequestPermissionsResult (Optional)

**Kotlin**
```kotlin
override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        attachmentManager?.handlePermissionResponse(requestCode, permissions, grantResults)
    }

```
**Java**
```java
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        attachmentManager.handlePermissionResponse(requestCode,permissions,grantResults);
    }
```
### Other Usage

1. You can open gallery,camera or file system directly without showing selection UI to user

***Kotlin***
```kotlin
 attachmentManager?.startCamera(mLauncher)
 // OR
 attachmentManager?.openGallery(mLauncher)
 // OR
 attachmentManager?.openFilSystem(mLauncher)
```

**Java**
```java
 attachmentManager.startCamera(mLauncher);
 // OR
 attachmentManager.openGallery(mLauncher);
 // OR
 attachmentManager.openFilSystem(mLauncher);
```

## Note

Any kind of improvements and suggestions are welcomed. Also, if you are using this library in your project then please do provide me your app url. I will list your app here.

