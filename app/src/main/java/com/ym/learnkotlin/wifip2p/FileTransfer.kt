package com.ym.learnkotlin.wifip2p

import java.io.Serializable

class FileTransfer :Serializable{
    private var fileName: String? = null

    private var fileLength: Long = 0

    private var md5: String? = null

    fun getFileName(): String? {
        return fileName
    }

    fun setFileName(fileName: String?) {
        this.fileName = fileName
    }

    fun getFileLength(): Long {
        return fileLength
    }

    fun setFileLength(fileLength: Long) {
        this.fileLength = fileLength
    }

    fun getMd5(): String? {
        return md5
    }

    fun setMd5(md5: String?) {
        this.md5 = md5
    }
}