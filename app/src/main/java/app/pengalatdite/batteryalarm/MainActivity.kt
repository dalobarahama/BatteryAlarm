package app.pengalatdite.batteryalarm

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import app.pengalatdite.batteryalarm.SharedPreferences.SEEKBAR_INPUT_VALUE
import app.pengalatdite.batteryalarm.SharedPreferences.SHARED_PREF
import app.pengalatdite.batteryalarm.SharedPreferences.SWITCH_VALUE
import app.pengalatdite.batteryalarm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var presenter: MainActivityPresenter

    companion object {
        const val CHARGING = "Charging"
        const val NOT_CHARGING = "Not Charging"
        const val SWITCH_IS_ON = true
        const val SWITCH_IS_OFF = false
        const val SEEKBAR_INITIAL_VALUE = 90
        const val SEEKBAR_MAX_VALUE = 100
        var SEEKBAR_SAVED_VALUE = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val batteryStatus: Intent? =
            IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { intentFilter ->
                baseContext.registerReceiver(null, intentFilter)
            }

        presenter = MainActivityPresenter(this, batteryStatus)

        binding.batteryStatus.text = getChargingStatus()
        binding.batteryPercentage.text = getBatteryPercentage()

        val sharedPref = this.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        SEEKBAR_SAVED_VALUE =
            sharedPref.getInt(SEEKBAR_INPUT_VALUE, SEEKBAR_INITIAL_VALUE)

        val seek: SeekBar = binding.seekBar

        if (SEEKBAR_SAVED_VALUE != 0) {
            seek.incrementProgressBy(SEEKBAR_SAVED_VALUE)
            binding.seekBarValue.text = SEEKBAR_SAVED_VALUE.toString()
        } else {
            seek.incrementProgressBy(SEEKBAR_INITIAL_VALUE)
            binding.seekBarValue.text = SEEKBAR_INITIAL_VALUE.toString()
        }

        seek.max = SEEKBAR_MAX_VALUE
        seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var barProgress: Int = 0
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.seekBarValue.text = progress.toString()
                barProgress = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                with(sharedPref.edit()) {
                    putInt(SEEKBAR_INPUT_VALUE, barProgress)
                    apply()
                }
            }
        })

        binding.switchOnOff.isChecked = getSwitchStatus()
        binding.switchOnOff.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                with(sharedPref.edit()) {
                    putBoolean(SWITCH_VALUE, binding.switchOnOff.isChecked)
                    putBoolean(SWITCH_VALUE, true)
                    apply()
                }
            } else {
                with(sharedPref.edit()) {
                    putBoolean(SWITCH_VALUE, false)
                    apply()
                }
            }
        }

    }

    private fun getSwitchStatus(): Boolean {
        return if (presenter.switchIsChecked()) {
            SWITCH_IS_ON
        } else {
            SWITCH_IS_OFF
        }
    }

    private fun getChargingStatus(): String {
        return if (presenter.batteryIsCharging()) {
            CHARGING
        } else {
            NOT_CHARGING
        }
    }

    private fun getBatteryPercentage(): String {
        return presenter.getBatteryChargingPercentage()?.toInt().toString()
    }
}