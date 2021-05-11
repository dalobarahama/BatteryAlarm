package app.pengalatdite.batteryalarm

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import app.pengalatdite.batteryalarm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var presenter: MainActivityPresenter

    companion object {
        const val CHARGING = "Charging"
        const val NOT_CHARGING = "Not Charging"
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

        presenter = MainActivityPresenter(batteryStatus)

        binding.batteryStatus.text = getChargingStatus()
        binding.batteryPercentage.text = getBatteryPercentage()

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        SEEKBAR_SAVED_VALUE =
            sharedPref.getInt(getString(R.string.seekbar_input_value), SEEKBAR_INITIAL_VALUE)

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
                    putInt(getString(R.string.seekbar_input_value), barProgress)
                    apply()
                }
            }
        })
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