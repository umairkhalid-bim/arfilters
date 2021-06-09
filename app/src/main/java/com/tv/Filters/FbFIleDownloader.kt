package com.tv.Filters

import android.app.Application
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.lang.Exception


/**
 *
 * Kotlin
 *
 * @author Umair Khalid (umair.khalid786@outlook.com)
 * @package no.realitylab.arface
 */

class FbFIleDownloader {

    private var mStorageRef: StorageReference? = null

    init {
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    fun download(onSuccess : (File) -> Unit?, onProgress : (Int) -> Unit?, onFailure : (Exception) -> Unit?) {
        val localFile: File = File.createTempFile("head_2", "glb")

        mStorageRef?.child("head_2.glb")?.getFile(localFile)
            ?.addOnSuccessListener { onSuccess(localFile) }
            ?.addOnProgressListener {
                onProgress(((it.bytesTransferred / it.totalByteCount) * 100).toInt())
            }
            ?.addOnFailureListener { onFailure(it) }
    }


    fun onClear() {
        mStorageRef = null
    }
}

