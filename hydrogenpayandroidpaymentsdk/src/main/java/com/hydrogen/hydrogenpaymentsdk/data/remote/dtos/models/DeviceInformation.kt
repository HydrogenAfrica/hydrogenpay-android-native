package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models

data class DeviceInformation(
    val deviceChannel: String = "en-GB",
    val httpBrowserColorDepth: String,
    val httpBrowserJavaEnabled: Boolean,
    val httpBrowserJavaScriptEnabled: Boolean,
    val httpBrowserLanguage: String,
    val httpBrowserScreenHeight: String,
    val httpBrowserScreenWidth: String,
    val httpBrowserTimeDifference: String,
    val userAgentBrowserValue: String
)