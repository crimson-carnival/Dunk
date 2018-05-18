package com.ashusinha.android.dunk;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class Upload extends Fragment implements View.OnClickListener {

    private static final int PICK_PDF_REQUEST = 234;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    private ListView mListView;
    private ArrayList<String> title, subject, url, courses, courseID;
    private StudyMaterialAdapter mAdapter;
    private DatabaseReference mRef;
    private FirebaseUser mUser;
    private String mCourseID;
    private boolean mClickable;
    private FloatingActionButton mFloatingActionButton;
    private TextInputEditText mTitle, mSubject;
    private Button mChoose;
    private Spinner mSpinner;
    private String id;
    private Uri mFilePath, mDownloadUrl;
    private StorageReference mStorageRef;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_academics_share, container, false);
        view = rootView;

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

        } else {
            mFloatingActionButton = rootView.findViewById(R.id.fab);
            mRef = FirebaseDatabase.getInstance().getReference();
            mUser = FirebaseAuth.getInstance().getCurrentUser();
            mListView = rootView.findViewById(R.id.list);
            title = new ArrayList<>();
            subject = new ArrayList<>();
            url = new ArrayList<>();
            mCourseID = "";
            mClickable = false;

            mAdapter = new StudyMaterialAdapter(getContext(), title, subject, url);
            mListView.setAdapter(mAdapter);

            setUpload();

        }
        // Permission has already been granted

        return rootView;
    }

    void setUpload() {
        mTitle = view.findViewById(R.id.title);
        mSubject = view.findViewById(R.id.subject);
        mChoose = view.findViewById(R.id.upload_btn);
        mSpinner = view.findViewById(R.id.course);
        mFloatingActionButton = view.findViewById(R.id.fab);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        view.findViewById(R.id.title_container).setVisibility(View.VISIBLE);
        //view.findViewById(R.id.subject_container).setVisibility(View.VISIBLE);
        mChoose.setVisibility(View.VISIBLE);
        //mSpinner.setVisibility(View.VISIBLE);
        mFloatingActionButton.setVisibility(View.VISIBLE);
        view.findViewById(R.id.header).setVisibility(View.VISIBLE);

        mChoose.setOnClickListener(this);
        mFloatingActionButton.setOnClickListener(this);

        courses = new ArrayList<>();
        courseID = new ArrayList<>();

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, courses);
        mSpinner.setAdapter(adapter);

        mRef.child("courses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                courses.clear();
                courseID.clear();
                courses.add("Select Course Name");
                courseID.add("0");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    courses.add(ds.child("course_name").getValue(String.class));
                    courseID.add(ds.child("id").getValue(String.class));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void fetch() {
        mRef.child("courses").child(mCourseID).child("study_material").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                title.clear();
                subject.clear();
                url.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    mClickable = true;
                    StudyMaterial studyMaterial = ds.getValue(StudyMaterial.class);
                    title.add(studyMaterial.getTitle());
                    subject.add(studyMaterial.getSubject());
                    url.add(studyMaterial.getUrl());
                }
                mAdapter.updateData(title, subject, url);
                activateListener();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void activateListener() {
        if (mClickable) {
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    try {
                        StudyMaterialDownloadDialogue studyMaterialDownloadDialogue = new StudyMaterialDownloadDialogue(getActivity());
                        studyMaterialDownloadDialogue.setTitle(title.get(i));
                        studyMaterialDownloadDialogue.setSubject(subject.get(i));
                        studyMaterialDownloadDialogue.setUrl(url.get(i));
                        studyMaterialDownloadDialogue.show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                try {
                    id = mRef.push().getKey();
                    upload();
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.upload_btn:
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_REQUEST);
            default:
                break;
        }
    }

    private void upload() {
        //Uri file = Uri.fromFile(new File(mFilePath.toString()));
        StorageReference riversRef = mStorageRef.child("study_material/" + DateFormat.getDateTimeInstance().format(new Date()));

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading");
        progressDialog.show();

        riversRef.putFile(mFilePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        mDownloadUrl = taskSnapshot.getDownloadUrl();
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                        /*Calendar now = Calendar.getInstance();
                        String year = Integer.toString(now.get(Calendar.YEAR));
                        String month = Integer.toString(now.get(Calendar.MONTH));
                        if (month.length() == 1) month = 0 + month;
                        String day = Integer.toString(now.get(Calendar.DAY_OF_MONTH));
                        if (day.length() == 1) day = 0 + day;
                        String hour = Integer.toString(now.get(Calendar.HOUR_OF_DAY));
                        if (hour.length() == 1) hour = 0 + hour;
                        String minute = Integer.toString(now.get(Calendar.MINUTE));
                        if (minute.length() == 1) minute = 0 + minute;
                        String second = Integer.toString(now.get(Calendar.SECOND));
                        if (second.length() == 1) second = 0 + second;
                        String millis = Integer.toString(now.get(Calendar.MILLISECOND));
                        if (second.length() == 1) millis = 0 + millis;
                        if (second.length() == 2) millis = 0 + millis;
                        String timestamp = year + "." + month + "." + day + "." + " " + hour + ":" + minute + ":" + second+"."+millis;
                        Radio radio = new Radio(id, title.getText().toString(), currentUser.getDisplayName(), description.getText().toString(), timestamp, mDownloadUrl.toString());
                        mRef.child("radio").child(id).setValue(radio);*/
                        StudyMaterial studyMaterial = new StudyMaterial(mTitle.getText().toString(), mSubject.getText().toString(), mDownloadUrl.toString());
                        int index = mSpinner.getSelectedItemPosition();
                        String id = mRef.child("courses").child(courseID.get(index)).push().getKey();
                        mRef.child("courses").child(courseID.get(index)).child("study_material").child(id).setValue(studyMaterial);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        //calculating progress percentage
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        //displaying percentage in progress dialog
                        progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mFilePath = data.getData();
        }
    }
}
