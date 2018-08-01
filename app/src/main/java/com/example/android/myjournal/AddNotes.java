package com.example.android.myjournal;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.myjournal.database.AppDatabase;
import com.example.android.myjournal.database.NoteEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class AddNotes extends AppCompatActivity {

    // Extra for the note ID to be received in the intent
    public static final String EXTRA_NOTE_ID = "extraNoteId";
    // Extra for the note ID to be received after rotation
    public static final String INSTANCE_NOTE_ID = "instanceNoteId";

    // Constant for default note id to be used when not in update mode
    private static final int DEFAULT_NOTE_ID = -1;

    // Constant for logging
    private static final String TAG = AddNotes.class.getSimpleName();
    private String USERID = FirebaseAuth.getInstance().getUid();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    //Fields for views
    EditText mEditText;
    Button mButton;
    // Firebase Authentication fields
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    // Firebase Database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("notes");

    private int mNoteId = DEFAULT_NOTE_ID;
    private Date createdAt;

    // Member variable for the Database
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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



//        database.get

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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
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
        final String date = dateFormat.format(new Date());
        final NoteEntry noteEntry;

        if (mNoteId == DEFAULT_NOTE_ID) {
            noteEntry = new NoteEntry(noteString, USERID, new Date());
        } else {
            noteEntry = new NoteEntry(mNoteId, noteString, USERID, createdAt);
        }

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (mNoteId == DEFAULT_NOTE_ID) {

                    Date lastDate = mDb.noteDao().loadLastDate(USERID);
                    String id = myRef.push().getKey();
                   if(lastDate == null) {
                       mDb.noteDao().insertNotes(noteEntry);
                       myRef.child(id).setValue(noteEntry);
                   } else {
                       String dd = dateFormat.format(lastDate);
                       if (dd.equals(date)) {
                           new Handler(Looper.getMainLooper()).post(new Runnable() {
                               @Override
                               public void run() {
                                   Toast.makeText(AddNotes.this, "You Already have a note for today! Add this to it.", Toast.LENGTH_LONG).show();
                               }
                           });
                       }
                       else {
                           mDb.noteDao().insertNotes(noteEntry);
                           myRef.child(id).setValue(noteEntry);
                       }
                   }

                } else {
                    mDb.noteDao().updateNotes(noteEntry);
                }
                finish();
            }
        });

    }


}
