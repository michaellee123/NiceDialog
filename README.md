# NiceDialog
A Very Nice Dialog For Android Developer.

# Import

If you can easy connet to JCenter, you can use this to import `NiceDialog` into your project.

```gradle
implementation 'dog.abcd:nicedialog:1.0.0'
```

If not, you can also use this.

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}
//something else...
implementation 'com.github.michaellee123:NiceDialog:1.0.0'
```

# Simple Usage

First at all, you should make your project support databinding, add this to your project's gradle.

 ```gradle
android {
    //something else...
    dataBinding {
        enabled = true
    }
}
 ```
 
 ## Create A Layout XML
 
 You just need make your all code included by `<layout></layout>`, like this.
 
 `dialog_nice.xml`
 
 ```xml
<?xml version="1.0" encoding="utf-8"?>
<layout>

    <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@color/colorPrimary"
        android:gravity="center"
        android:orientation="vertical">

        <TextView
            android:id="@+id/tvMessage"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textColor="#ffffff"
            android:textSize="48sp"
            android:textStyle="bold" />

        <Button
            android:id="@+id/btnConfirm"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content" />

    </LinearLayout>
</layout>
 ```
 
 Then, databinding will auto create a class named `DialogNiceBinding`.
 
 ## Show A Dialog By NiceDialog
 
 Simply call `NiceDialog` at the desired location.
 
