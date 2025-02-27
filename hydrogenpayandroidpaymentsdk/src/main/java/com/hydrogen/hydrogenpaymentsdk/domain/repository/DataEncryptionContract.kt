package com.hydrogen.hydrogenpaymentsdk.domain.repository

interface DataEncryptionContract {
    /**
     * Encrypts the plain text, [data], provided.
     *
     * @param [text] The string that is to be encrypted
     * @return [String] The encrypted data resulting from the encryption process
     * */
    fun encrypt(text: String): String

    /**
     * Decrypts the encrypted string, [encryptedData]
     *
     * @param [encryptedText] the String to be decrypted.
     * @return [String] The decrypted string.
     * */
    fun decrypt(encryptedText: String): String
}