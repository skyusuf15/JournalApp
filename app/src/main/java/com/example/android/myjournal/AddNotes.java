package com.example.android.myjournal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.myjournal.database.AppDatabase;
import com.example.android.myjournal.database.NoteEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

public class AddNotes extends AppCompatActivity {

    // Extra for the note ID to be received in the intent
    public static final String EXTRA_NOTE_ID = "extraNoteId";
    // Extra for the note ID to be received after rotation
    public static final String INSTANCE_NOTE_ID = "instanceNoteId";

    // Constant for default note id to be used when not in update mode
    private static final int DEFAULT_NOTE_ID = -1;
    // Constant for logging
    private static final String TAG = AddNotes.class.getSimpleName();


    //Fields for views
    EditText mEditText;
    Button mButton;
    // Firebase Authentication fields
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    private int mNoteId = DEFAULT_NOTE_ID;
    // Member variable for the Database
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);

        initView();
        mDb = AppDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_NOTE_ID)) {
            mNoteId = savedInstanceState.getInt(INSTANCE_NOTE_ID, DEFAULT_NOTE_ID);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_NOTE_ID)) {
            mButton.setText(R.string.update_button);
            if (mNoteId == DEFAULT_NOTE_ID) {
                // populate the UI
            }
        }


        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                } else {

                }
            }
        };

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_NOTE_ID, mNoteId);
        super.onSaveInstanceState(outState);
    }

    private void initView() {
        mEditText = findViewById(R.id.editTextNoteDescription);
        mButton = findViewById(R.id.saveButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveButtonClicked();
            }
        });
    }

    private void populateUI(NoteEntry noteEntry) {

    }

    private void onSaveButtonClicked() {
        String noteString = mEditText.getText().toString().trim();
        Date date = new Date();

        NoteEntry noteEntry = new NoteEntry(noteString, 1, date);
        mDb.noteDao().insertNotes(noteEntry);
        finish();
    }


}
