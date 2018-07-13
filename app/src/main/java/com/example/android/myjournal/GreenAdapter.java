package com.example.android.myjournal;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.myjournal.database.NoteEntry;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class GreenAdapter extends RecyclerView.Adapter<GreenAdapter.NoteViewHolder> {

    // Constant for date format
    private static final String DATE_FORMAT = "EEE, MMM d, yyyy";

    // Member variable to handle item clicks
    final private ItemClickListener mItemClickListener;

    // Class variables for the List that holds task data and the Context
    private List<NoteEntry> mNoteEntries;
    private Context mContext;
    // Date formatter
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());


    public GreenAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new NoteViewHolder that holds the view for each task
     */
    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.content_view_entries, parent, false);

        return new NoteViewHolder(view);
    }


    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(final NoteViewHolder holder, final int position) {
        // Determine the values of the wanted data
        final NoteEntry noteEntry = mNoteEntries.get(position);
        String note = noteEntry.getNote();
        String updatedAt = dateFormat.format(noteEntry.getUpdatedAt());


        //Set values
        holder.taskDescriptionView.setText(note + " ==Note for " + updatedAt);

        holder.btn_edit_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //holder.btn_edit_note.getParent().requestDisallowInterceptTouchEvent(false);
                int itemId = noteEntry.getId();
//                Log.d(TAG, "BTN Item ID# "+ itemId);
                Intent intent = new Intent(mContext, AddNotes.class);
                intent.putExtra(AddNotes.EXTRA_NOTE_ID, itemId);
                mContext.startActivity(intent);
            }
        });
    }

    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mNoteEntries == null) {
            return 0;
        }
        return mNoteEntries.size();
    }

    public List<NoteEntry> getNoteEntries() {
        return mNoteEntries;
    }

    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setNotes(List<NoteEntry> noteEntries) {
        mNoteEntries = noteEntries;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }


    class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        // Class variables for the task description TextViews
        TextView taskDescriptionView;
        Button btn_edit_note;


        public NoteViewHolder(View itemView) {
            super(itemView);

            taskDescriptionView = itemView.findViewById(R.id.taskDescription);
            btn_edit_note = itemView.findViewById(R.id.btn_edit_note);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            int elementId = mNoteEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }

}
