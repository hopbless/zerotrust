package com.hopeinyang.zeroknowledge.common.composables

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import com.google.firebase.storage.FirebaseStorage
import com.hopeinyang.zeroknowledge.data.dto.Response
import timber.log.Timber


@Composable
fun getCardIconFromUri(
    uri: String,
    imageRepository: ImageRepository = ImageRepository()
): State<Response<Bitmap?>> {

    return produceState<Response<Bitmap?>>(initialValue = Response.loading(null), uri, imageRepository) {

        imageRepository.loadImage(uri){bitmap->
            value = Response.success(bitmap)
        }


    }

}


suspend fun loadImage(uri: String, bitmap:(Bitmap?)->Unit){
    val  ONE_MEGABYTE:Long = 1024 *1024
    val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(uri)
    storageRef.getBytes(ONE_MEGABYTE)
        .addOnSuccessListener {
            Timber.e("Successfully retrieved image from storage")
            try {
                val image = BitmapFactory.decodeByteArray(it, 0, it.size)
                if (image != null)
                    bitmap(image)

                Timber.e("Image decoded successfully from storage")

            }catch (e:Exception){
                e.printStackTrace()
                bitmap(null)

            }


        }
        .addOnFailureListener {
            Timber.e("Failed retrieve image from storage")
        }

}




class ImageRepository{

    val  ONE_MEGABYTE:Long = 1024 *1024
    private val storage = FirebaseStorage.getInstance()

    suspend fun loadImage(uri: String, bitmap:(Bitmap?)->Unit){


        val reference = storage.getReferenceFromUrl(uri)
        reference.getBytes(ONE_MEGABYTE)
            .addOnSuccessListener {
                Timber.e("Successfully retrieved image from storage")
                try {
                    val image = BitmapFactory.decodeByteArray(it, 0, it.size)
                    if (image != null)
                        bitmap(image)

                    Timber.e("Image decoded successfully from storage")

                }catch (e:ArrayIndexOutOfBoundsException){
                    e.printStackTrace()
                    bitmap(null)

                }


        }
            .addOnFailureListener {
                Timber.e("Failed retrieve image from storage")
            }

    }
}