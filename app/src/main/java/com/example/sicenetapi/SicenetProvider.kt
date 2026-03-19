package com.example.sicenetapi // <-- Ajusta a tu paquete real

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri

class SicenetProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.example.sicenetapi.provider"
        const val CARGA_DIR = 1
        const val KARDEX_DIR = 2

        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "carga", CARGA_DIR)
            addURI(AUTHORITY, "kardex", KARDEX_DIR)
        }
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        val context = context ?: return null
        val appContainer = (context.applicationContext as SicenetApplication).container

        return when (uriMatcher.match(uri)) {
            CARGA_DIR -> appContainer.cargaAcademicaDao.getCargaCursor()
            KARDEX_DIR -> appContainer.kardexDao.getKardexCursor()
            else -> throw IllegalArgumentException("URI desconocida: $uri")
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val context = context ?: return 0
        val db = (context.applicationContext as SicenetApplication).container.database

        val tableName = when (uriMatcher.match(uri)) {
            CARGA_DIR -> "carga_academica"
            KARDEX_DIR -> "kardex_alumno"
            else -> throw IllegalArgumentException("URI desconocida: $uri")
        }

        val deletedRows = db.openHelper.writableDatabase.delete(tableName, selection, selectionArgs)
        context.contentResolver.notifyChange(uri, null)
        return deletedRows
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            CARGA_DIR -> "vnd.android.cursor.dir/vnd.$AUTHORITY.carga"
            KARDEX_DIR -> "vnd.android.cursor.dir/vnd.$AUTHORITY.kardex"
            else -> null
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw UnsupportedOperationException("Insertar no soportado en esta práctica")
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        throw UnsupportedOperationException("Actualizar no soportado en esta práctica")
    }
}