package com.hydrogen.hydrogenpaymentsdk.utils

import android.content.Context
import android.util.Base64
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models.DeviceInformation
import com.hydrogen.hydrogenpaymentsdk.domain.enums.CardType
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.STRING_CARD_EXPIRY_DATE_SPACER
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.STRING_DEVICE_LANGUAGE
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.STRING_DEVICE_TYPE
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Calendar
import java.util.Collections
import java.util.TimeZone
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

internal object CardPaymentUtil {
    fun encryptText(text: String, key: String, iv: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val secretKey = SecretKeySpec(Base64.decode(key, Base64.DEFAULT), "AES")
        val ivSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
        val encryptedBytes = cipher.doFinal(text.toByteArray(Charsets.UTF_8))

        return Base64.encodeToString(
            encryptedBytes,
            Base64.DEFAULT
        )
    }

    fun decryptText(encryptedText: String, key: String, iv: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

        val secretKey = SecretKeySpec(Base64.decode(key, Base64.DEFAULT), "AES")
        val ivSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)

        val decryptedBytes = cipher.doFinal(Base64.decode(encryptedText, Base64.DEFAULT))

        return String(decryptedBytes, Charsets.UTF_8)
    }

    fun getCardType(cardNumber: String): CardType {
        val visa = Regex("^4[0-9]{12}(?:[0-9]{3})?$")
        val mastercard = Regex("^5[1-5][0-9]{14}$")
        val mastercard2 = Regex("^2[2-7][0-9]{14}$")
        val verve = Regex("^(?:50[067][180]|6500)(?:[0-9]{15})$")

        return when {
            visa.matches(cardNumber) -> CardType.VISA
            mastercard.matches(cardNumber) || mastercard2.matches(cardNumber) -> CardType.MASTERCARD
            verve.matches(cardNumber) -> CardType.VERVE
            else -> CardType.UNKNOWN
        }
    }

    fun checkSumCardValidation(value: String): Boolean {
        var nCheck = 0
        if (value.isNotEmpty() && value.matches(Regex("[0-9-\\s]+"))) {
            val digits = value.replace("\\D".toRegex(), "") // Remove non-digit characters

            digits.forEachIndexed { n, char ->
                var nDigit = char.digitToInt()
                if ((digits.length + n) % 2 == 0) {
                    nDigit *= 2
                    if (nDigit > 9) nDigit -= 9
                }
                nCheck += nDigit
            }
        }
        return nCheck % 10 == 0
    }

    fun checkCardExpiryDate(expiryDate: String): Boolean {
        val dateToday = Calendar.getInstance()
        val currentYear = dateToday.get(Calendar.YEAR)
        val currentMonth = dateToday.get(Calendar.MONTH) + 1 // Months are 0-based in Calendar

        val parts = expiryDate.split(STRING_CARD_EXPIRY_DATE_SPACER)
        if (parts.size == 2 && parts[0].length == 2 && parts[1].length == 2) {
            val month = parts[0].toIntOrNull() ?: return false
            val year = (parts[1].toIntOrNull() ?: return false) + 2000

            return !(year < currentYear || (year == currentYear && month < currentMonth))
        }
        return false
    }

    fun getDeviceInformation(context: Context): DeviceInformation {
        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels.toString()
        val screenHeight = displayMetrics.heightPixels.toString()
        val colorDepth = "24" // Android does not provide direct color depth access

        val systemProperties = System.getProperty("http.agent") ?: "Unknown"

        return DeviceInformation(
            STRING_DEVICE_TYPE, colorDepth, httpBrowserJavaEnabled = false, true,
            STRING_DEVICE_LANGUAGE, screenHeight, screenWidth, getTimezoneOffset(), systemProperties
        )
    }

    private fun getTimezoneOffset(): String {
        val offsetMillis = TimeZone.getDefault().getOffset(System.currentTimeMillis())
        val offsetMinutes = offsetMillis / (60 * 1000) // Convert milliseconds to minutes
        return offsetMinutes.toString()
    }

    fun getLocalIpAddress(): String? =
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (networkInterface in interfaces) {
                val addresses = Collections.list(networkInterface.inetAddresses)
                for (address in addresses) {
                    if (!address.isLoopbackAddress && address is InetAddress) {
                        val ip = address.hostAddress
                        address.hostAddress?.let {
                            if (it.contains(":").not()) it
                        }
                    }
                }
            }
            null
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    fun getCardTypeFromTypeName(typeName: String): CardType =
        CardType.entries.firstOrNull { it.typeName.contains(typeName, true) } ?: CardType.UNKNOWN
}