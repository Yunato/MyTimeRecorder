package io.github.yunato.myrecordtimer.ui.activity

import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.NumberPicker
import io.github.yunato.myrecordtimer.R
import io.github.yunato.myrecordtimer.ui.dialog.TimePickerFragment
import kotlinx.android.synthetic.main.dialog_time_picker.view.*

class SettingsActivity : AppCompatPreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()
    }

    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onIsMultiPane(): Boolean {
        return isXLargeTablet(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home){
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    override fun onBuildHeaders(target: List<Header>) {
        loadHeadersFromResource(R.xml.pref_headers, target)
    }

    override fun isValidFragment(fragmentName: String): Boolean {
        return PreferenceFragment::class.java.name == fragmentName
                || UIPreferenceFragment::class.java.name == fragmentName
                || TimerPreferenceFragment::class.java.name == fragmentName
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class UIPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_ui)
            setHasOptionsMenu(true)

            bindPreferenceSummaryToValue(findPreference("list_theme"))
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                activity?.onBackPressed()
                return true
            }
            return super.onOptionsItemSelected(item)
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class TimerPreferenceFragment : PreferenceFragment() {
        private lateinit var sp: SharedPreferences

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_timer)
            setHasOptionsMenu(true)

            sp = PreferenceManager.getDefaultSharedPreferences(activity)
            val myPref = findPreference("button_timer") as Preference
            myPref.setOnPreferenceClickListener {
                val view: View = createPickerView()
                AlertDialog.Builder(activity as Context).apply {
                    setView(view)
                    setTitle(R.string.time_picekr_title)
                    setPositiveButton(android.R.string.ok) { _, _ ->
                        val hour = view.findViewById<NumberPicker>(R.id.picker_hour).value
                        val minute = view.findViewById<NumberPicker>(R.id.picker_minute).value
                        val second = view.findViewById<NumberPicker>(R.id.picker_second).value
                        val editor = sp.edit()
                        editor.putInt(TimePickerFragment.KEY_HOUR, hour)
                        editor.putInt(TimePickerFragment.KEY_MINUTE, minute)
                        editor.putInt(TimePickerFragment.KEY_SECOND, second)
                        editor.apply()
                        setSummary(myPref)
                    }
                    setNegativeButton(android.R.string.cancel) { _, _ ->
                        activity?.finish()
                    }
                }.show()
                true
            }
            setSummary(myPref)
        }

        private fun setSummary(myPref: Preference){
            myPref.setSummary(String.format("%02d:%02d:%02d",
                sp.getInt(TimePickerFragment.KEY_HOUR, 0),
                sp.getInt(TimePickerFragment.KEY_MINUTE, 25),
                sp.getInt(TimePickerFragment.KEY_SECOND, 0)))
        }

        private fun createPickerView(): View {
            return LayoutInflater.from(activity).inflate(R.layout.dialog_time_picker, null).apply{
                fun NumberPicker.setup(max: Int, key: String){
                    minValue = 0
                    maxValue = max
                    value = sp.getInt(key, 0)
                    setFormatter{ String.format("%02d", it) }
                }
                picker_hour.setup(23, TimePickerFragment.KEY_HOUR)
                picker_minute.setup(59, TimePickerFragment.KEY_MINUTE)
                picker_second.setup(59, TimePickerFragment.KEY_SECOND)
            }
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                activity?.onBackPressed()
                return true
            }
            return super.onOptionsItemSelected(item)
        }
    }

    companion object {

        fun intent(context: Context): Intent = Intent(context, SettingsActivity::class.java)

        private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
            val stringValue = value.toString()

            if (preference is ListPreference) {
                val listPreference = preference
                val index = listPreference.findIndexOfValue(stringValue)

                preference.setSummary(
                    if (index >= 0)
                        listPreference.entries[index]
                    else
                        null
                )

            } else {
                preference.summary = stringValue
            }
            true
        }

        private fun isXLargeTablet(context: Context): Boolean {
            return context.resources.configuration.screenLayout and
                    Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_XLARGE
        }

        private fun bindPreferenceSummaryToValue(preference: Preference) {
            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            sBindPreferenceSummaryToValueListener.onPreferenceChange(
                preference,
                PreferenceManager
                    .getDefaultSharedPreferences(preference.context)
                    .getString(preference.key, "")
            )
        }
    }
}
