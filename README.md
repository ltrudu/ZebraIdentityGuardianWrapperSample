*This is the sample code for the ZebraIdentitiyGuardianWrapper library*
=========================================================

The library is available at : https://github.com/ltrudu/ZebraIdentityGuardianWrapper

*Please be aware that this library / application / sample is provided as a community project without any guarantee of support*
=========================================================

[![](https://jitpack.io/v/ltrudu/ZebraIdentityGuardianWrapper.svg)](https://jitpack.io/#ltrudu/ZebraIdentityGuardianWrapper)
[![](https://jitpack.io/v/ltrudu/ZebraIdentityGuardianWrapper/month.svg)](https://jitpack.io/#ltrudu/ZebraIdentityGuardianWrapper)


# ZebraIdentitiyGuardianWrapper

## Easy implementation of Identity Guardian APIs !!!

Forget about ContentResolvers, StageNow, EMDK, certificates, application signature... complexity....

Just implement Identity Guardian APIs with simple method calls that will:
- Register your app to the requested service using AccessMgr CSP and the EMDK's profile manager.
- Retrieve session data as a cursor.
- Translate the cursor into text message, a key/value pair Map.
- Register to authentication changes with a simple java object.

Have fun with Zebra's devices :)

## Before implementing the API

Please, take the time to read the Idendity Guardian APIs documentation to see what information you can get.

https://techdocs.zebra.com/identityguardian/

## Change Log !!! 

## 1.0 : First release

See sample App for a quick implementation of the library.

## Sample Repository
https://github.com/ltrudu/ZebraIdentityGuardianWrapperSample

Look for "TODO: MANDATORY FOR ZebraIdentitiyGuardianWrapper" section of this Readme to find what you need to add to your AndroidManifest.xml to use this wrapper.

## Important !!
```text
	If you plan to use the AuthenticationStatusObserver, it is advised to implement it inside a foreground service.
	This will ensure that the service will always listen to the Identity Guardian API
	You'll find a snippet of Foreground Service in this repository:
	https://github.com/ltrudu/ForegroundServiceSample
``` 

## Description

A wrapper to easily implement the Identity Guardian APIs.

Identity Guardian API is based on a Content Provider.

The access to this content provider is restricted to registered applications.

Applications need to be **explicitly granted the ability** to do so and use a proprietary API.

To access to this API, you must first register your application using the AccessMgr MX's CSP.

You can do it using StageNow, more details here: https://techdocs.zebra.com/identityguardian/1-6/api/#allowlistapis

Or you can use this wrapper that will automatically register your application if it is necessary.

## Implementation
To use this helper on Zebra Android devices running Android 11 or higher, first declare a new permission in your AndroidManifest.xml

```xml
    <uses-permission android:name="com.zebra.mdna.els.permission.PROVIDER" />
    <uses-permission android:name="com.symbol.emdk.permission.EMDK" />
```

Then add a query element to retrive the data

```xml
    <queries>
        <package android:name="com.symbol.emdk.emdkservice" />
        <package android:name="com.zebra.mdna.els" />
    </queries>
```

Then add the uses-library element to your application 
```xml
        <uses-library android:name="com.symbol.emdk" />
```

Sample AdroidManifest.xml:
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">
    <uses-permission android:name="com.zebra.mdna.els.permission.PROVIDER" />
    <uses-permission android:name="com.symbol.emdk.permission.EMDK" />

    <queries>
        <package android:name="com.symbol.emdk.emdkservice" />
        <package android:name="com.zebra.mdna.els" />
    </queries>

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.ZebraIdentityGuardianWrapperSample"
        tools:targetApi="31">
        <uses-library android:name="com.symbol.emdk" />
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```
Update your project settings.graddle file to add jitpack repository
```text
        maven { url 'https://jitpack.io' }        
```
Sample project settings.gradle
```text
pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}

rootProject.name = "ZebraIdentityGuardianWrapperSample"
include ':ZebraIdentityGuardianWrapperSample'
```
Add ZebraIdentityGuardianWrapper library information to your libs.versions.toml file
```text
[versions]
zebraIdentityGuardianWrapper = "1.0"

[libraries]
zebraIdentityGuardianWrapper = { group = "com.github.ltrudu", name = "ZebraIdentityGuardianWrapper", version.ref = "zebraIdentityGuardianWrapper" }
```

Finally, add ZebraIdentityGuardianWrapper dependency to your application build.graddle file:
```text
    implementation libs.zebraIdentityGuardianWrapper
```

Sample application build.graddle:
```text
dependencies {
    implementation libs.appcompat
    implementation libs.material
    compileOnly libs.emdk
    implementation libs.zebraIdentityGuardianWrapper
    testImplementation libs.junit
    androidTestImplementation libs.ext.junit
    androidTestImplementation libs.espresso.core
}
```

## The implementation provides two functional classes:

- IGCRHelper : A facade static class that provides the main functionalities of the API (force authentication, logout, get current session, get previous session)
- AuthenticationStatusObserver : A class that help to track authentication status.


## Code Snippets

Snippet code to force user authentication:
```java
    private void sendAuthenticationRequest(Context context)
    {
        IGCRHelper.sendAuthenticationRequest(context, IGCRHelper.EAuthenticationScheme.authenticationScheme1, IGCRHelper.EAuthenticationFlag.blocking, new IIGAuthenticationResultCallback() {
            @Override
            public void onSuccess(String message) {
                Log.v(TAG, message);
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, message);
            }

            @Override
            public void onDebugStatus(String message) {
                Log.d(TAG, message);
            }
        });
    }
```

This methods takes two enums as arguments:

- EAuthenticationScheme : Indicates the scheme we want to force for authentication, can be scheme from 1 to 4
- EAuthenticationFlag : Indicates if the authentication has to be blocking or not (blocking means that the notification bar, system bar, home button, and recent button are disabled and cannot be accessed by the user.)

Snippet code to force user logout:
```java
    private void logout(Context context)
    {
        IGCRHelper.logout(context, new IIGAuthenticationResultCallback() {
            @Override
            public void onSuccess(String message) {
                Log.v(TAG, message);
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, message);
            }

            @Override
            public void onDebugStatus(String message) {
                Log.d(TAG, message);
            }
        });
    }
```

Snippet code to use to current session information:
```java
    private void getCurrentSession(Context context)
    {
       IGCRHelper.getCurrentSession(context, new IIGSessionResultCallback() {
            @Override
            public void onSuccess(Cursor result) {
				// Log info as string
                Log.v(TAG, IGCRHelper.cursorToString(result));
				// Get the info as JSONObject
				JSONObject jsonValues = IGCRHelper.cursorToJson(result);
				// Get the info as a key/value map
				Map<String, String> resultMap = IGCRHelper.cursorToMap(result);
				// Or parse the cursor manually
				try {
					while(cursor.moveToNext()) {
						for(int i = 0; i < cursor.getColumnCount(); i++) {
							String columnName = cursor.getColumnName(i);
							String value = cursor.getString(i);
							// Do something here
						}
					}
				}
				catch(Exception e) {
					Log.e(TAG, e.getMessage());
				}
				finally {
					cursor.close();
				}
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, message);
            }

            @Override
            public void onDebugStatus(String message) {
                Log.d(TAG, message);
            }
        });
    }
```

Snippet code to use to previous session information:
```java
    private void getPreviousSession(Context context)
    {
        IGCRHelper.getPreviousSession(context, new IIGSessionResultCallback() {
            @Override
            public void onSuccess(Cursor result) {
				// Log info as string
                Log.v(TAG, IGCRHelper.cursorToString(result));
				// Get the info as JSONObject
				JSONObject jsonValues = IGCRHelper.cursorToJson(result);
				// Get the info as a key/value map
				Map<String, String> resultMap = IGCRHelper.cursorToMap(result);
				// Or parse the cursor manually
				try {
					while(cursor.moveToNext()) {
						for(int i = 0; i < cursor.getColumnCount(); i++) {
							String columnName = cursor.getColumnName(i);
							String value = cursor.getString(i);
							// Do something here						}
					}
				}
				catch(Exception e) {
					Log.e(TAG, e.getMessage());
				}
				finally {
					cursor.close();
				}
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, message);
            }

            @Override
            public void onDebugStatus(String message) {
                Log.d(TAG, message);
            }
        });
    }
```

## Helpers

IGCRHelpers implement some functions to help you to process the sessions data cursor.

You can process the cursor manually with the folowing snippet:

```java
try {
	while(cursor.moveToNext()) {
		for(int i = 0; i < cursor.getColumnCount(); i++) {
			String columnName = cursor.getColumnName(i);
			String value = cursor.getString(i);
			// Do something here						}
	}
}
catch(Exception e) {
	Log.e(TAG, e.getMessage());
}
finally {
	cursor.close();
}
```

You can get all the values as string data for logging purposes:

```java
String cursorAsString = IGCRHelper.cursorToString(cursor)
```
Finally you can translate the cursor as a map, as a JSONObject or as a XML String if it is easier for your process:

```java
JSONObject jsonValues = IGCRHelper.cursorToJson(cusor);
Map<String, String> resultMap = IGCRHelper.cursorToMap(cusor);
String resultXML = IGCRHelper.cursorToXml(cusor);
```

