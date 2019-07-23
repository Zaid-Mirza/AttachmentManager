# AttachmentManager

[![](https://jitpack.io/v/Zaid-Mirza/AttachmentManager.svg)](https://jitpack.io/#Zaid-Mirza/AttachmentManager)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)
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


# Prerequisite

1. Add permissions and provider in **AndroidManifest.xml**

```xml
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
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


3. Include the library as local library project.
```groovy
allprojects {
   repositories {
      	jcenter()
       	maven { url "https://jitpack.io" }  //Make sure to add this in your project
   }
}
```

```groovy
   implementation 'com.github.Zaid-Mirza:AttachmentManager:1.0.0'
```

# Usage


1. Initiate AttachmentManager object using builder pattern

  **Kotlin**

```kotlin
private var attachmentManager: AttachmentManager? = null

override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        attachmentManager = AttachmentManager.AttachmentBuilder(this) // must pass Context 
                .activity(this) // container activity
                .fragment(null) // pass fragment reference if you are in fragment
                .setUiTitle("Choose File") // title of dialog or bottom sheet
                .allowMultiple(true) // set true if you want make multiple selection, default is false
                .asBottomSheet(true) // set true if you need to show selection as bottom sheet, default is as Dialog
                .build()
       
    }
    
```
**Java**
```java
 private AttachmentManager attachmentManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        attachmentManager = new AttachmentManager.AttachmentBuilder(this) // must pass Context
                .activity(this) // container activity
                .fragment(null) // pass fragment reference if you are in fragment
                .setUiTitle("Choose File") // title of dialog or bottom sheet
                .allowMultiple(true) // set true if you want make multiple selection, default is false
                .asBottomSheet(true) // set true if you need to show selection as bottom sheet, default is as Dialog
                .build();
    }
```


3. Call **openSelection()** method to show selection UI

```kotlin
 attachmentManager?.openSelection()
````

4. Override onActivityResult

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val list = attachmentManager?.manipulateAttachments(requestCode, resultCode, data) // gives you neccessary detail about attachment like uri,name,size,path and mimtype
        Toast.makeText(this, list?.size.toString(), Toast.LENGTH_LONG).show()
    }
```

### Other Usage

1. You can open gallery,camera or file system directly without showing selection UI to user

```kotlin
 attachmentManager?.startCamera()
 // OR
 attachmentManager?.openGallery()
 // OR
 attachmentManager?.openFilSystem()
```
## Note

Any kind of improvements and suggestions are welcomed. Also, if you are using this library in your project then please do provide me your app url. I will list your app here.

