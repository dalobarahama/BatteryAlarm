package app.pengalatdite.batteryalarm

import android.content.Intent

interface MainActivityContract {
    fun batteryIsCharging(): Boolean

    fun getBatteryChargingPercentage(): Float?
}