package com.example.contentproviderapplication

import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.provider.BaseColumns._ID
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.contentproviderapplication.database.NotesDatabaseHelper
import com.example.contentproviderapplication.database.NotesProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URI

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    lateinit var notesRecyclerView: RecyclerView
    lateinit var noteAdd: FloatingActionButton
    lateinit var adapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        noteAdd = note_add
        noteAdd.setOnClickListener{
            NotesDetailFragment().show(supportFragmentManager, "dialog")
        }

        adapter = NotesAdapter(object : NoteClickedListener{
            override fun noteClickedItem(cursor: Cursor) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(_ID))
                val fragment = NotesDetailFragment.newInstance(id)
                fragment.show(supportFragmentManager, "dialog")
            }

            override fun noteRemoveItem(cursor: Cursor?) {
                val id = cursor?.getLong(cursor.getColumnIndexOrThrow(_ID))
                contentResolver.delete(Uri.withAppendedPath(NotesProvider.URI_NOTES, id.toString()), null, null)
            }

        })
        adapter.setHasStableIds(true)

        notesRecyclerView = notes_recycler
        notesRecyclerView.layoutManager = LinearLayoutManager(this)
        notesRecyclerView.adapter = adapter

        LoaderManager.getInstance(this).initLoader(0, null, this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> = CursorLoader(
        this,
        NotesProvider.URI_NOTES,
        null,
        null,
        null,
        NotesDatabaseHelper.TITLE_NOTES
    )


    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        if(data != null) {
            adapter.setCursor(data)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        adapter.setCursor(null)
    }
}