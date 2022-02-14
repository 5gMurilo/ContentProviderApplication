package com.example.contentproviderapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.contentproviderapplication.database.NotesDatabaseHelper
import com.example.contentproviderapplication.database.NotesProvider

class NotesDetailFragment : DialogFragment(), DialogInterface.OnClickListener {

    private lateinit var noteEditTitle: EditText
    private lateinit var noteEditDescription: EditText

    private var id : Long = 0

    companion object {
        private const val EXTRA_ID = "id"
        fun newInstance (id: Long): NotesDetailFragment {
            val bundle = Bundle()
            bundle.putLong(EXTRA_ID, id)

            val notesFragment = NotesDetailFragment()
            notesFragment.arguments = bundle
            return notesFragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity?.layoutInflater?.inflate(R.layout.note_detail, null)

        noteEditTitle = view?.findViewById(R.id.note_edt_title) as EditText
        noteEditDescription = view.findViewById(R.id.note_edt_description) as EditText

        var newNote = true

        if (arguments != null && arguments?.getLong(EXTRA_ID) != 0L) {
            id = arguments?.getLong(EXTRA_ID) as Long
            var uri = Uri.withAppendedPath(NotesProvider.URI_NOTES, id.toString())
            val cursor = activity?.contentResolver?.query(uri, null, null, null,null)

            if (cursor?.moveToNext() as Boolean) {
                newNote = false
                noteEditTitle.setText(cursor.getString(cursor.getColumnIndexOrThrow(NotesDatabaseHelper.TITLE_NOTES)))
                noteEditDescription.setText(cursor.getString(cursor.getColumnIndexOrThrow(NotesDatabaseHelper.DESCRIPTION_NOTES)))
            }
            cursor.close()
        }

        return AlertDialog.Builder(activity as Activity)
            .setTitle(if(newNote) "Nova mensagem" else "Editar mensagem")
            .setView(view)
            .setPositiveButton("Salvar", this)
            .setNegativeButton("cancelar",this)
            .create()
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        val values = ContentValues()
        values.put(NotesDatabaseHelper.TITLE_NOTES, noteEditTitle.text.toString())
        values.put(NotesDatabaseHelper.DESCRIPTION_NOTES, noteEditDescription.text.toString())


        Log.i("values size", "valor -> ${values.size()}")
        Log.i("values size", "valor -> ${values.get("description") == ""}")

        if(id != 0L){
            if(values.size() != 0){
                val uri = Uri.withAppendedPath(NotesProvider.URI_NOTES, id.toString())
                context?.contentResolver?.update(uri, values, null, null)
            }
        }else{
            if(values.get("description") != "" && values.get("title") != ""){
                context?.contentResolver?.insert(NotesProvider.URI_NOTES, values)
            }
        }
    }
}