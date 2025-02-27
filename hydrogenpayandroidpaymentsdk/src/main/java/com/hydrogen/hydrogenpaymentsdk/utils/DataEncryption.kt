package com.hydrogen.hydrogenpaymentsdk.utils

// import com.google.android.gms.common.util.Hex
import android.util.Base64
import com.hydrogen.hydrogenpaymentsdk.domain.repository.DataEncryptionContract
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class DataEncryption(
    private val secretKey: String,
    private val iv: String,
) : DataEncryptionContract {
    override fun encrypt(text: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val secretKey = SecretKeySpec(Base64.decode(secretKey, Base64.DEFAULT), "AES")
        val ivSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
        val encryptedBytes = cipher.doFinal(text.toByteArray(Charsets.UTF_8))

        return Base64.encodeToString(
            encryptedBytes,
            Base64.DEFAULT
        )
    }

    override fun decrypt(encryptedText: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

        val secretKey = SecretKeySpec(Base64.decode(secretKey, Base64.DEFAULT), "AES")
        val ivSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)

        val decryptedBytes = cipher.doFinal(Base64.decode(encryptedText, Base64.DEFAULT))

        return String(decryptedBytes, Charsets.UTF_8)
    }

}