package com.ashusinha.android.dunk;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class StudyMaterialDownloadDialogue extends Dialog implements
        View.OnClickListener {

    private Activity c;
    TextView mTitle, mSubject;
    private String title, subject, url;

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public StudyMaterialDownloadDialogue(Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialogue_study_material_download);

        Button download;

        download = findViewById(R.id.download);
        mTitle = findViewById(R.id.header);
        mSubject = findViewById(R.id.subject);

        mTitle.setText(title);
        mSubject.setText(subject);

        download.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download:
                downloadFile();
                break;
            default:
                break;
        }
    }

    private void downloadFile() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(url);

        File rootPath = new File(Environment.getExternalStorageDirectory(), "Science Up");
        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }

        File localPath = new File(rootPath, subject);
        if (!localPath.exists()) {
            localPath.mkdirs();
        }

        final File localFile = new File(localPath, title + ".pdf");

        storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.e("firebase ", ";local tem file created  created " + localFile.toString());
                Toast.makeText(c, "File stored to "+localFile.toString(), Toast.LENGTH_SHORT).show();
                dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("firebase ", ";local tem file not created  created " + exception.toString());
                Toast.makeText(c, "An unexpected error occurred. File not downloaded.", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }
}
