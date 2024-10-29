package com.hopeinyang.zeroknowledge.data.services.module

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.hopeinyang.zeroknowledge.data.dao.EncryptionService
import com.hopeinyang.zeroknowledge.data.dao.impl.EncryptionServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

@Module
@InstallIn(SingletonComponent::class)
object EncryptionModule {
    private val keystore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
    private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
    private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
    private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"




    @Provides
    fun providesSecretKey(): SecretKey {
        val existingKey = keystore.getEntry("zero-trust", null) as? KeyStore.SecretKeyEntry
        return  existingKey?.secretKey ?: createKey()
    }

    @Provides
    fun providesCipher(): Cipher {
        return Cipher.getInstance(TRANSFORMATION)
    }

    @Provides
    fun providesCryptoService(key: SecretKey, cipher: Cipher): EncryptionService {
        return EncryptionServiceImpl(key, cipher)
    }



    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(
                    "zero-trust",
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(false)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }
}