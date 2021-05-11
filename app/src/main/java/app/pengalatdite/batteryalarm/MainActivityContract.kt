package app.pengalatdite.batteryalarm

interface MainActivityContract {
    fun batteryIsCharging(): Boolean

    fun getBatteryChargingPercentage(): Float?
}