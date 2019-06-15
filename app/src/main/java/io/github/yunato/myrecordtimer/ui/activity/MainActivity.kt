package io.github.yunato.myrecordtimer

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.FragmentManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import io.github.yunato.myrecordtimer.ui.activity.SettingsActivity
import io.github.yunato.myrecordtimer.ui.fragment.MainFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        nav_view.setCheckedItem(R.id.nav_measurement)
        onNavigationItemSelected(nav_view.menu.getItem(0))
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        if(item.itemId == R.id.nav_setting){
            SettingsActivity.intent(this).let { startActivity(it) }
        }else{
            supportFragmentManager.beginTransaction().replace(R.id.content, MainFragment.newInstance()).commit()
//        when (item.itemId) {
//            R.id.nav_measurement -> {
//
//            }
//            R.id.nav_view_record -> {
//
//            }
//            R.id.nav_setting -> {
//
//            }
//        }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
