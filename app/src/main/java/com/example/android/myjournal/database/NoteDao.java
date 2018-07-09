package com.example.android.myjournal.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY updated_at")
    List<NoteEntry> loadAllNotes();

    @Insert
    void insertNotes(NoteEntry noteEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateNotes(NoteEntry noteEntry);

    @Delete
    void deleteNotes(NoteEntry noteEntry);
}
