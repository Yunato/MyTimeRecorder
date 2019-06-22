package io.github.yunato.myrecordtimer.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import io.github.yunato.myrecordtimer.R
import io.github.yunato.myrecordtimer.model.entity.Record
import io.github.yunato.myrecordtimer.ui.fragment.EditRecordFragment
import kotlinx.android.synthetic.main.activity_edit_record.*

class EditRecordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_record)

        setSupportActionBar(toolbar as Toolbar)
        if(supportActionBar != null){
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_black_24dp)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        val record = intent.getParcelableExtra<Record>(EXTRA_RECORD)
        val fragment = EditRecordFragment.newInstance(record)
        supportFragmentManager.beginTransaction().replace(R.id.content, fragment).commit()

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        if(id == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    companion object {
        @JvmStatic val EXTRA_RECORD: String = "io.github.yunato.myrecordtimer.ui.activity.EXTRA_RECORD"

        fun intent(context: Context, record: Record): Intent {
            return Intent(context, EditRecordActivity::class.java).apply{
                putExtra(EXTRA_RECORD, record)
            }
        }
    }
}
