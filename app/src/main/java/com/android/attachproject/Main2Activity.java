package com.android.attachproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mirza.attachmentmanager.managers.AttachmentManager;
import com.mirza.attachmentmanager.models.AttachmentDetail;

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
                .activity(this) // container activity
                .fragment(null) // pass fragment reference if you are in fragment
                .setUiTitle("Choose File") // title of dialog or bottom sheet
                .allowMultiple(true) // set true if you want make multiple selection, default is false
                .asBottomSheet(true) // set true if you need to show selection as bottom sheet, default is as Dialog
                .build();
        Toast.makeText(this, "", Toast.LENGTH_LONG).show();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attachmentManager.openSelection();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       ArrayList<AttachmentDetail> list = attachmentManager.manipulateAttachments(requestCode, resultCode, data);
        Toast.makeText(this, list.size()+"", Toast.LENGTH_LONG).show();
    }
}
