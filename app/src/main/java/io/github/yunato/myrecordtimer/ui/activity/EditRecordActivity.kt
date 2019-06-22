package io.github.yunato.myrecordtimer.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toolbar
import io.github.yunato.myrecordtimer.R
import kotlinx.android.synthetic.main.activity_edit_record.*

class EditRecordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_record)

        setActionBar(toolbar as Toolbar)
        if(supportActionBar != null){
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_black_24dp)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        if(id == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }
}
