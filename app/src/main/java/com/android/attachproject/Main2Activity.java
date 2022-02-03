package com.android.attachproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mirza.attachmentmanager.managers.AttachmentManager;
import com.mirza.attachmentmanager.managers.HideOption;
import com.mirza.attachmentmanager.models.AttachmentDetail;
import com.mirza.attachmentmanager.utils.FileUtil;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {


    private AttachmentManager attachmentManager = null;
    String[] gallery = {"image/*"};
    String[] files  = { "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .ppt & .pptx
            "application/pdf"};

    ActivityResultLauncher<Intent> mLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        ArrayList<AttachmentDetail> list = attachmentManager.manipulateAttachments(this,result.getResultCode(),result.getData());

    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        attachmentManager = new AttachmentManager.AttachmentBuilder(this) // must pass Context
                .fragment(null) // pass fragment reference if you are in fragment
                .setUiTitle("Choose File") // title of dialog or bottom sheet
                .allowMultiple(false) // set true if you want make multiple selection, default is false
                .asBottomSheet(true) // set true if you need to show selection as bottom sheet, default is as Dialog
                .setOptionsTextColor(android.R.color.holo_green_light) // change text color
                .setImagesColor(R.color.colorAccent) // change icon color
                 .hide(HideOption.DOCUMENT)// You can hide any option do you want
                .setMaxPhotoSize(200000) // Set max  photo size in bytes
                .galleryMimeTypes(gallery) // mime types for gallery
                .filesMimeTypes(files) // mime types for files
                .build(); // Hide any of the three options

        Toast.makeText(this, "", Toast.LENGTH_LONG).show();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> attachmentManager.openSelection(mLauncher));

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        attachmentManager.handlePermissionResponse(requestCode, permissions, grantResults,mLauncher);
    }

}
