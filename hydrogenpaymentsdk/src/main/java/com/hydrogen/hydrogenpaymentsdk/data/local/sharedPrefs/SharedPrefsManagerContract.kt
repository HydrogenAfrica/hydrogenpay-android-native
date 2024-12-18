package com.hydrogen.hydrogenpaymentsdk.data.local.sharedPrefs

interface SharedPrefsManagerContract {
    fun retrieveStringFromSharedPrefs(
        key: String,
        defaultValue: String = "",
    ): String

    fun retrieveIntFromSharedPrefs(key: String, defaultValue: Int? = null): Int
    fun saveStringToSharedPrefs(key: String, value: String)
    fun saveIntToSharedPrefs(key: String, value: Int)
    fun saveBooleanToSharedPrefs(key: String, value: Boolean)
    fun saveLongToSharedPrefs(key: String, value: Long)
    fun saveDoubleToSharedPrefs(key: String, value: Double)
    fun retrieveBooleanFromSharedPrefs(key: String): Boolean
    fun retrieveLongFromSharedPrefs(key: String, defaultValue: Long? = null): Long
    fun retrieveDoubleFromSharedPrefs(key: String, defaultValue: Double? = null): Double
}