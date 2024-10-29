package com.hopeinyang.zeroknowledge.data.dao

interface EncryptionService {
    suspend fun encryptText(text: String,): String?
    suspend fun decryptText(encryptedText: String): String?
}