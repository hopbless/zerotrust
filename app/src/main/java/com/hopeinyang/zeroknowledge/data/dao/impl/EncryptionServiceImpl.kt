package com.hopeinyang.zeroknowledge.data.dao.impl

import com.hopeinyang.zeroknowledge.data.dao.EncryptionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Arrays
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject

class EncryptionServiceImpl @Inject constructor(
    private val key:SecretKey,
    private val cipher:Cipher
) :EncryptionService{

    private val ivSize = 16
    override suspend fun encryptText(text: String,): String? =
        withContext(Dispatchers.Default){
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val encryptedBytes = try {
                cipher.doFinal(text.toByteArray(Charsets.UTF_8))
            }catch (e: Exception){
                e.printStackTrace()

                return@withContext null
            }

            val iv = cipher.iv

            val combinedIvAndEncryptedData = iv + encryptedBytes

            return@withContext Base64.getEncoder().encodeToString(combinedIvAndEncryptedData)
        }

    override suspend fun decryptText(encryptedText: String,): String? =
        withContext(Dispatchers.Default){

            //Timber.d("EncryptedText is $encryptedText")
            val decodedData = Base64.getDecoder().decode(encryptedText)

            val iv = Arrays.copyOfRange(decodedData, 0, ivSize)

            val encryptedData = Arrays.copyOfRange(decodedData, ivSize, decodedData.size)
            cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
            val decryptedText = try {
                String(cipher.doFinal(encryptedData), Charsets.UTF_8)
            }catch (e:Exception){
                e.printStackTrace()
                return@withContext null

            }

            return@withContext decryptedText
        }
}