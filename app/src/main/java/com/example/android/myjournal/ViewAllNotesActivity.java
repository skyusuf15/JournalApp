package com.example.android.myjournal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.myjournal.database.AppDatabase;
import com.example.android.myjournal.database.NoteEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;
import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class ViewAllNotesActivity extends AppCompatActivity implements GreenAdapter.ItemClickListener {
    // Firebase Authentication fields
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    private GreenAdapter mAdapter;
    private static final String USERID = FirebaseAuth.getInstance().getUid();
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private AppDatabase mDb;
    Button btn_edit_note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_notes);

        mRecyclerView = findViewById(R.id.recyclerViewTasks);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new GreenAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (containsDate(mAdapter.getNoteEntries(), new Date())) {
                    Toast.makeText(ViewAllNotesActivity.this, "You already have a note for today, edit to continue!", Toast.LENGTH_LONG).show();
                }
                else {
                    startActivity(new Intent(ViewAllNotesActivity.this, AddNotes.class));
                }
            }
        });

        mDb = AppDatabase.getInstance((getApplicationContext()));

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
    protected void onResume() {
        super.onResume();
        retriveNotes();
    }

    private void retriveNotes() {

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: USERID# " + USERID);
                if (USERID == null) {
                    Toast.makeText(ViewAllNotesActivity.this, "User Details Not Found!", Toast.LENGTH_LONG);
                    return;
                }
                final List<NoteEntry> notes = mDb.noteDao().loadAllNotesByUserId();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setNotes(notes);
                    }
                });
            }
        });
    }

    public boolean containsDate(final List<NoteEntry> notes, final Date date) {
        // notes.stream().map(NoteEntry::getUpdatedAt).filter(date::equals).findFirst().isPresent();
        return notes.stream().filter(o -> o.getUpdatedAt().equals(date)).findFirst().isPresent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.userLogout) {
            mAuth.signOut();
            finish();
            startActivity(new Intent(ViewAllNotesActivity.this, LoginActivity.class));
        }
        return true;
    }

    @Override
    public void onItemClickListener(int itemId) {
        // Launch ViewNoteActivity adding the itemId as an extra in the intent
        Intent intent = new Intent(ViewAllNotesActivity.this, ViewNoteActivity.class);
        intent.putExtra(ViewNoteActivity.EXTRA_NOTE_ID, itemId);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }
}
