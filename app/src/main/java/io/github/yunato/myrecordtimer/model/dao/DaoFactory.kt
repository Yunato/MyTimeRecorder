package io.github.yunato.myrecordtimer.model.dao

import android.content.Context

class DaoFactory {
    companion object {
        @JvmStatic
        fun getLocalDao(context: Context?): LocalDao = LocalDao.getInstance(context)
    }
}
