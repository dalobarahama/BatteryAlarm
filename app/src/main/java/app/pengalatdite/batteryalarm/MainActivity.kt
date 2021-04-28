package app.pengalatdite.batteryalarm

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.pengalatdite.batteryalarm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var presenter: MainActivityPresenter

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

        presenter = MainActivityPresenter(batteryStatus)

        if (presenter.batteryIsCharging()) {
            binding.batteryStatus.text = CHARGING
        } else {
            binding.batteryStatus.text = NOT_CHARGING
        }

        binding.batteryPercentage.text =
            presenter.getBatteryChargingPercentage()?.toInt().toString()

        setContentView(binding.root)
    }
}