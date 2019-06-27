package io.github.yunato.myrecordtimer.model.dao.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import io.github.yunato.myrecordtimer.model.entity.OperationRecord
import io.github.yunato.myrecordtimer.model.entity.RelationRecord

class RecordDBAdapter(mContext: Context) {

    private val db: SQLiteDatabase
    private val helper: DatabaseOpenHelper = DatabaseOpenHelper(mContext)

    init {
        db = helper.writableDatabase
    }

    fun addOperation(ope: Int, calendarId: Long): Long {
        val values = ContentValues()
        values.put(DatabaseOpenHelper.FIELD_OP, ope)
        values.put(DatabaseOpenHelper.FIELD_LID, calendarId)
        return db.insertOrThrow(DatabaseOpenHelper.DB_OPE_TABLE_NAME, null, values)
    }

    fun addOperations(ope: Int, calendarIds: List<Long>): List<Long> {
        val rtnList: MutableList<Long> = mutableListOf()
        for(calendarId in calendarIds){
            rtnList.add(addOperation(ope, calendarId))
        }
        return rtnList
    }

    fun getOperations(): MutableList<OperationRecord> {
        val sql = "SELECT * FROM ${DatabaseOpenHelper.DB_OPE_TABLE_NAME}"
        val result: MutableList<OperationRecord> = mutableListOf()
        db.rawQuery(sql, null).use{
            while (it.moveToNext()) {
                result.add(OperationRecord(
                    it.getLong(it.getColumnIndex(DatabaseOpenHelper.FIELD_ID)),
                    it.getInt(it.getColumnIndex(DatabaseOpenHelper.FIELD_OP)),
                    it.getLong(it.getColumnIndex(DatabaseOpenHelper.FIELD_LID))))
            }
        }
        return result
    }

    fun deleteOperationRecord(id: Long) {
        db.delete(DatabaseOpenHelper.DB_OPE_TABLE_NAME, "${DatabaseOpenHelper.FIELD_ID}=?", arrayOf(id.toString()))
    }

    fun addRelation(localCalendarId: Long, remoteCalendarId: Long): Long {
        val values = ContentValues()
        values.put(DatabaseOpenHelper.FIELD_LID, localCalendarId)
        values.put(DatabaseOpenHelper.FIELD_RID, remoteCalendarId)
        return db.insertOrThrow(DatabaseOpenHelper.DB_REL_TABLE_NAME, null, values)
    }

    fun getRelations(): MutableList<RelationRecord> {
        val sql = "SELECT * FROM ${DatabaseOpenHelper.DB_REL_TABLE_NAME}"
        val result: MutableList<RelationRecord> = mutableListOf()
        db.rawQuery(sql, null).use{
            while (it.moveToNext()) {
                result.add(RelationRecord(
                    it.getLong(it.getColumnIndex(DatabaseOpenHelper.FIELD_ID)),
                    it.getLong(it.getColumnIndex(DatabaseOpenHelper.FIELD_LID)),
                    it.getLong(it.getColumnIndex(DatabaseOpenHelper.FIELD_RID))))
            }
        }
        return result
    }

    fun deleteRelationRecord(id: Long) {
        db.delete(DatabaseOpenHelper.DB_REL_TABLE_NAME, "${DatabaseOpenHelper.FIELD_ID}=?", arrayOf(id.toString()))
    }
}
