package com.example.contentproviderapplication.database

import android.annotation.SuppressLint
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.media.UnsupportedSchemeException
import android.net.Uri
import android.provider.BaseColumns
import java.net.URI

class NotesProvider : ContentProvider() {

    private lateinit var mUriMatcher: UriMatcher
    private lateinit var dbHelper: NotesDatabaseHelper

    companion object {
        const val AUTHORITY = "com.example.contentproviderapplication.provider"
        const val NOTES = 1
        const val NOTES_BY_ID = 2
        val BASE_URI = Uri.parse("content://$AUTHORITY")
        val URI_NOTES = Uri.withAppendedPath(BASE_URI, "notes")
    }

    override fun onCreate(): Boolean {
        mUriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        mUriMatcher.addURI(AUTHORITY, "notes", NOTES)
        mUriMatcher.addURI(AUTHORITY, "notes/#", NOTES_BY_ID)

        if (context != null) {
            dbHelper = NotesDatabaseHelper(context as Context)
        }
        return true
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        if (mUriMatcher.match(uri) == NOTES_BY_ID) {
            val db: SQLiteDatabase = dbHelper.writableDatabase
            val linesAffect = db.delete(
                NotesDatabaseHelper.TABLE_NOTES,
                "${BaseColumns._ID} = ?",
                arrayOf(uri.lastPathSegment)
            )
            db.close()
            context?.contentResolver?.notifyChange(uri, null)
            return linesAffect
        } else {
            throw UnsupportedSchemeException("Uri inválida para exclusão")
        }
    }

    override fun getType(uri: Uri): String? = throw UnsupportedSchemeException("")

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (mUriMatcher.match(uri) == NOTES) {
            val db: SQLiteDatabase = dbHelper.writableDatabase
            val id = db.insert(NotesDatabaseHelper.TABLE_NOTES, null, values)
            val insertURI = Uri.withAppendedPath(BASE_URI, id.toString())
            db.close()
            context?.contentResolver?.notifyChange(uri, null)
            return insertURI
        } else {
            throw UnsupportedSchemeException("Uri inválida para inserção")
        }
    }

    @SuppressLint("Recycle")
    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        return when {
            mUriMatcher.match(uri) == NOTES -> {
                val db: SQLiteDatabase = dbHelper.writableDatabase
                val cursor: Cursor = db.query(
                    NotesDatabaseHelper.TABLE_NOTES,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                )
                cursor.setNotificationUri(context?.contentResolver, uri)
                cursor
            }

            mUriMatcher.match(uri) == NOTES_BY_ID -> {
                val db: SQLiteDatabase = dbHelper.writableDatabase
                val cursor = db.query(
                    NotesDatabaseHelper.TABLE_NOTES,
                    projection,
                    "${BaseColumns._ID} = ?",
                    arrayOf(uri.lastPathSegment),
                    null,
                    null,
                    sortOrder
                )
                cursor.setNotificationUri(context?.contentResolver, uri)
                cursor
            }

            else -> {
                throw UnsupportedSchemeException("Uri não implementada.")
            }
        }
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        if (mUriMatcher.match(uri) == NOTES_BY_ID) {
            val db: SQLiteDatabase = dbHelper.writableDatabase
            val lineAffect = db.update(
                NotesDatabaseHelper.TABLE_NOTES,
                values,
                "${BaseColumns._ID} = ?",
                arrayOf(uri.lastPathSegment)
            )
            db.close()
            context?.contentResolver?.notifyChange(uri, null)
            return lineAffect
        } else {
            throw UnsupportedSchemeException("Uri não implementada")
        }
    }
}