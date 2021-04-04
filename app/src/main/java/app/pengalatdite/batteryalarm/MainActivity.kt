package app.pengalatdite.batteryalarm

import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.pengalatdite.batteryalarm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        const val CHARGING = "Charging"
        const val NOT_CHARGING = "Not Charging"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)

        val batteryStatus: Intent? =
            IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { intentFilter ->
                baseContext.registerReceiver(null, intentFilter)
            }

        getChargingStatus(binding, batteryStatus)
        getChargingPercentage(binding, batteryStatus)

        setContentView(binding.root)
    }

    private fun getChargingStatus(binding: ActivityMainBinding, batteryStatus: Intent?) {
        val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        if (isCharging)
            binding.batteryStatus.text = CHARGING
        else
            binding.batteryStatus.text = NOT_CHARGING
    }

    private fun getChargingPercentage(binding: ActivityMainBinding, batteryStatus: Intent?) {
        val batteryPercentage: Float? = batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale.toFloat()
        }

        binding.batteryPercentage.text = batteryPercentage.toString()
    }
}