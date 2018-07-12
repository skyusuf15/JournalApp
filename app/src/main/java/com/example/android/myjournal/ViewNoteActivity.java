package com.example.android.myjournal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.myjournal.database.AppDatabase;
import com.example.android.myjournal.database.NoteEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ViewNoteActivity extends AppCompatActivity {


    // Extra for the note ID to be received in the intent
    public static final String EXTRA_NOTE_ID = "extraNoteId";
    // Extra for the note ID to be received after rotation
    public static final String INSTANCE_NOTE_ID = "instanceNoteId";

    // Constant for default note id to be used when not in update mode
    private static final int DEFAULT_NOTE_ID = -1;
    // Constant for logging
    private static final String TAG = ViewNoteActivity.class.getSimpleName();
    private static final String USERID = FirebaseAuth.getInstance().getUid();

    // Constant for date format
    private static final String DATE_FORMAT = "EEE, MMM d, yyyy";
    // Date formatter
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    //Fields for views
    TextView tv_note, tv_note_date;

    // Firebase Authentication fields
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    private int mNoteId = DEFAULT_NOTE_ID;

    // Member variable for the Database
    private AppDatabase mDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        mDb = AppDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_NOTE_ID)) {
            mNoteId = savedInstanceState.getInt(INSTANCE_NOTE_ID, DEFAULT_NOTE_ID);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_NOTE_ID)) {

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

    private void populateUI(NoteEntry note) {
        tv_note = findViewById(R.id.tv_note);
        tv_note_date = findViewById(R.id.tv_note_date);

        if (note == null) {
            return;
        }
        tv_note.setText(note.getNote());
        tv_note_date.setText(dateFormat.format(note.getUpdatedAt()));
    }

}
