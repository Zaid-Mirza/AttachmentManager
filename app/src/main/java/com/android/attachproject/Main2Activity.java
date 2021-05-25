package com.android.attachproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mirza.attachmentmanager.managers.AttachmentManager;
import com.mirza.attachmentmanager.managers.HideOption;
import com.mirza.attachmentmanager.models.AttachmentDetail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.Toast;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {


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
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        attachmentManager = new AttachmentManager.AttachmentBuilder(this) // must pass Context
                .fragment(null) // pass fragment reference if you are in fragment
                .setUiTitle("Choose File") // title of dialog or bottom sheet
                .allowMultiple(false) // set true if you want make multiple selection, default is false
                .asBottomSheet(true) // set true if you need to show selection as bottom sheet, default is as Dialog
                // You can hide any option do you want
                .setMaxPhotoSize(200000) // Set max  photo size in bytes
                .galleryMimeTypes(gallery) // mime types for gallery
                .filesMimeTypes(files) // mime types for files
                .build(); // Hide any of the three options

        Toast.makeText(this, "", Toast.LENGTH_LONG).show();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> attachmentManager.openSelection());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<AttachmentDetail> list = attachmentManager.manipulateAttachments(getApplicationContext(), requestCode, resultCode, data);


        if (list != null && list.size() > 0) {
            //  openFileInBrowser(list.get(0).getUri());
        }
        Toast.makeText(this, (list != null ? list.size() : 0) + "", Toast.LENGTH_LONG).show();
    }

    private void openFileInBrowser(Uri url) {
        if (url != null) {
            Intent browserIntent = null;
            try {
                browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setDataAndType(url, "application/pdf");
            } catch (Exception e) {
                e.printStackTrace();
            }
            startActivity(browserIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        attachmentManager.handlePermissionResponse(requestCode, permissions, grantResults);
    }
}
