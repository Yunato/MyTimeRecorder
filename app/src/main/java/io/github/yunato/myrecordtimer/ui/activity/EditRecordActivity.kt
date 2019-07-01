package io.github.yunato.myrecordtimer.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import io.github.yunato.myrecordtimer.R
import io.github.yunato.myrecordtimer.model.entity.Record
import io.github.yunato.myrecordtimer.ui.fragment.EditRecordFragment
import kotlinx.android.synthetic.main.activity_edit_record.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

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
        val records: ArrayList<Record> = intent.getParcelableArrayListExtra(EXTRA_SUB_RECORD)
        val fragment: EditRecordFragment = EditRecordFragment.newInstance(record, records)
        supportFragmentManager.beginTransaction().replace(R.id.content, fragment).commit()

        save_button.setOnClickListener(fragment.getSaveOnClickListener())
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        if(id == android.R.id.home) {
            AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title_warning)
                .setMessage(R.string.dialog_message_no_save)
                .setPositiveButton(R.string.button_ok){_,_->
                    finish()
                }
                .setNegativeButton(R.string.button_cancel, null)
                .show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    companion object {
        @JvmStatic val EXTRA_RECORD: String = "io.github.yunato.myrecordtimer.ui.activity.EXTRA_RECORD"
        @JvmStatic val EXTRA_SUB_RECORD: String = "io.github.yunato.myrecordtimer.ui.activity.EXTRA_SUB_RECORD"

        fun intent(context: Context, record: Record, records: ArrayList<Record>): Intent {
            return Intent(context, EditRecordActivity::class.java).apply{
                putExtra(EXTRA_RECORD, record)
                putExtra(EXTRA_SUB_RECORD, records)
            }
        }
    }
}
