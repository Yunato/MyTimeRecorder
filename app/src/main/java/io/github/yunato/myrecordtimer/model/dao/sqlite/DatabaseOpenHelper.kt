package io.github.yunato.myrecordtimer.model.dao.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseOpenHelper(mContext: Context) :
    SQLiteOpenHelper(mContext, DATABASE_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE $DB_OPE_TABLE_NAME " +
                    "( $FIELD_ID INTEGER PRIMARY KEY AUTOINCREMENT, $FIELD_OP INTEGER, $FIELD_LID INTEGER );")
        db?.execSQL(
            "CREATE TABLE $DB_REL_TABLE_NAME " +
                    "( $FIELD_ID INTEGER PRIMARY KEY AUTOINCREMENT, $FIELD_LID INTEGER, $FIELD_RID TEXT );")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $DB_OPE_TABLE_NAME ;")
        db?.execSQL("DROP TABLE IF EXISTS $DB_REL_TABLE_NAME ;")
        onCreate(db)
    }

    companion object {
        @JvmStatic val DATABASE_NAME: String = "record.db"
        @JvmStatic val DB_VERSION: Int = 1
        @JvmStatic val DB_OPE_TABLE_NAME: String = "operation"
        @JvmStatic val DB_REL_TABLE_NAME: String = "relation"

        @JvmStatic val FIELD_ID: String = "_id"
        @JvmStatic val FIELD_OP: String = "operation"
        @JvmStatic val FIELD_LID: String = "local_calendar_id"
        @JvmStatic val FIELD_RID: String = "remote_calendar_id"

        @JvmStatic val OPE_ADD: Int = 0
        @JvmStatic val OPE_DELETE: Int = 1
    }
}
