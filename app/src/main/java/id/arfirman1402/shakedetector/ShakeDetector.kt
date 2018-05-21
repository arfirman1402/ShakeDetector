package id.arfirman1402.shakedetector

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager


class ShakeDetector : SensorEventListener {

    /*
	 * The gForce that is necessary to register as shake.
	 * Must be greater than 1G (one earth gravity unit).
	 * You can install "G-Force", by Blake La Pierre
	 * from the Google Play Store and run it to see how
	 *  many G's it takes to register a shake
	 */
    private val SHAKE_THRESHOLD_GRAVITY = 2.7f
    private val SHAKE_SLOP_TIME_MS = 500
    private val SHAKE_COUNT_RESET_TIME_MS = 3000

    private var mListener: OnShakeListener? = null
    private var mShakeTimestamp: Long = 0
    private var mShakeCount: Int = 0

    fun setOnShakeListener(listener: OnShakeListener) {
        this.mListener = listener
    }

    interface OnShakeListener {
        fun onShake(count: Int)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // ignore
    }

    override fun onSensorChanged(event: SensorEvent) {

        if (mListener != null) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val gX = (x / SensorManager.GRAVITY_EARTH).toDouble()
            val gY = (y / SensorManager.GRAVITY_EARTH).toDouble()
            val gZ = (z / SensorManager.GRAVITY_EARTH).toDouble()

            // gForce will be close to 1 when there is no movement.
            val gForce = Math.sqrt((gX * gX + gY * gY + gZ * gZ)).toFloat()

            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                val now = System.currentTimeMillis()
                // ignore shake events too close to each other (500ms)
                if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                    return
                }

                // reset the shake count after 3 seconds of no shakes
                if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                    mShakeCount = 0
                }

                mShakeTimestamp = now
                mShakeCount++

                mListener!!.onShake(mShakeCount)
            }
        }
    }
}
