package com.example.android.myjournal.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY created_at DESC")
    List<NoteEntry> loadAllNotesByUserId();

    @Insert
    void insertNotes(NoteEntry noteEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateNotes(NoteEntry noteEntry);

    @Delete
    void deleteNotes(NoteEntry noteEntry);

    @Query("SELECT * FROM notes WHERE id = :id")
    NoteEntry loadNoteById(int id);

    @Query("SELECT * FROM notes WHERE created_at = :date")
    NoteEntry loadNoteByDate(Date date);
}
