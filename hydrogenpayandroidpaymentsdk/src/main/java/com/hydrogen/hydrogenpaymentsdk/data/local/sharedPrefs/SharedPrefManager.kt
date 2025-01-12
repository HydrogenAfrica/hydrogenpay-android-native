package com.hydrogen.hydrogenpaymentsdk.data.local.sharedPrefs

import com.dsofttech.dprefs.utils.DPrefs

class SharedPrefManager() : SharedPrefsManagerContract {

    private val sharedPrefObject: DPrefs = DPrefs
    override fun retrieveStringFromSharedPrefs(key: String, defaultValue: String): String =
        sharedPrefObject.getString(key, defaultValue)

    override fun retrieveIntFromSharedPrefs(key: String, defaultValue: Int?): Int =
        defaultValue?.let { sharedPrefObject.getInt(key, it) }
            ?: run { sharedPrefObject.getInt(key) }

    override fun saveStringToSharedPrefs(key: String, value: String) {
        sharedPrefObject.putString(key, value)
    }

    override fun saveIntToSharedPrefs(key: String, value: Int) {
        sharedPrefObject.putInt(key, value)
    }

    override fun saveBooleanToSharedPrefs(key: String, value: Boolean) {
        sharedPrefObject.putBoolean(key, value)
    }

    override fun saveLongToSharedPrefs(key: String, value: Long) {
        sharedPrefObject.putLong(key, value)
    }

    override fun saveDoubleToSharedPrefs(key: String, value: Double) {
        sharedPrefObject.putDouble(key, value)
    }

    override fun retrieveBooleanFromSharedPrefs(key: String): Boolean =
        sharedPrefObject.getBoolean(key)

    override fun retrieveLongFromSharedPrefs(key: String, defaultValue: Long?): Long =
        defaultValue?.let { sharedPrefObject.getLong(key, it) } ?: run {
            sharedPrefObject.getLong(key)
        }

    override fun retrieveDoubleFromSharedPrefs(key: String, defaultValue: Double?): Double =
        defaultValue?.let { sharedPrefObject.getDouble(key, it) }
            ?: run { sharedPrefObject.getDouble(key) }
}