package com.example.android.myjournal.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "notes")
public class NoteEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int userId;
    private String note;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;


    @Ignore
    public NoteEntry(String note, int userId, Date updatedAt) {
        this.note = note;
        this.userId = userId;
        this.updatedAt = updatedAt;
    }

    public NoteEntry(int id, String note, int userId, Date updatedAt) {
        this.id = id;
        this.note = note;
        this.userId = userId;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
