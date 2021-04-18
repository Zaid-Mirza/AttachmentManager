package com.android.attachproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mirza.attachmentmanager.managers.AttachmentManager;
import com.mirza.attachmentmanager.managers.HideOption;
import com.mirza.attachmentmanager.models.AttachmentDetail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {


    private AttachmentManager attachmentManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        attachmentManager = new AttachmentManager.AttachmentBuilder(this) // must pass Context
                .fragment(null) // pass fragment reference if you are in fragment
                .setUiTitle("Choose File") // title of dialog or bottom sheet
                .allowMultiple(true) // set true if you want make multiple selection, default is false
                .asBottomSheet(true) // set true if you need to show selection as bottom sheet, default is as Dialog
                .hide(HideOption.DOCUMENT) // You can hide any option do you want
                .setMaxCameraPhotoSize(200000) // Set max camera photo size in bytes
                .build(); // Hide any of the three options

        Toast.makeText(this, "", Toast.LENGTH_LONG).show();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> attachmentManager.openSelection());
    }

    @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            ArrayList<AttachmentDetail> list = attachmentManager.manipulateAttachments(getApplicationContext(),requestCode, resultCode, data);
            Toast.makeText(this, (list != null ? list.size() : 0) + "", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        attachmentManager.handlePermissionResponse(requestCode, permissions, grantResults);
    }
}
