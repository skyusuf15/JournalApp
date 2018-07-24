package com.example.android.myjournal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
    private static final String USERID = FirebaseAuth.getInstance().getUid();

    //Fields for views
    EditText mEditText;
    Button mButton;
    // Firebase Authentication fields
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    private int mNoteId = DEFAULT_NOTE_ID;
    private Date createdAt;

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
                mNoteId = intent.getIntExtra(EXTRA_NOTE_ID, DEFAULT_NOTE_ID);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        final NoteEntry note = mDb.noteDao().loadNoteById(mNoteId);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                populateUI(note);
                            }
                        });
                    }
                });
            }
        }


        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged: USER= " + user);
                    Log.d(TAG, "\nonAuthStateChanged: Uid= " + USERID);
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
        if (noteEntry == null) {
            return;
        }
        mEditText.setText(noteEntry.getNote());
        createdAt = noteEntry.getUpdatedAt();
    }

    private void onSaveButtonClicked() {
        String noteString = mEditText.getText().toString().trim();
        final Date date = new Date();
        final NoteEntry noteEntry;


        if (mNoteId == DEFAULT_NOTE_ID) {
            noteEntry = new NoteEntry(noteString, USERID, date);
        } else {
            noteEntry = new NoteEntry(mNoteId, noteString, USERID, createdAt);
        }


        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (mNoteId == DEFAULT_NOTE_ID) {

                    NoteEntry dd = mDb.noteDao().loadNoteByDate(date);
                   if( dd != null) {
                       Log.d(TAG, "run: Date existed!####  " + dd.getUpdatedAt());
                       Toast.makeText(AddNotes.this, "You Already have a note for today! Add this to it.", Toast.LENGTH_LONG).show();
                   } else {
                       Log.d(TAG, "run: No Date Found! ####  ");
                       mDb.noteDao().insertNotes(noteEntry);
                      }

                } else {
                    mDb.noteDao().updateNotes(noteEntry);
                }
                finish();
            }
        });

    }


}
