package app.pengalatdite.batteryalarm

import android.content.Intent
import android.os.BatteryManager

class MainActivityPresenter(status: Intent?) : MainActivityContract {
    private val batteryStatus: Intent? = status

    override fun batteryIsCharging(): Boolean {
        val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1

        return status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL
    }

    override fun getBatteryChargingPercentage(): Float? {
        return batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale.toFloat()
        }
    }
}