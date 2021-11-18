package com.ym.learnkotlin.wifip2p

import java.io.File

interface OnProgressChangeListener{
    fun onProgressChanged(fileTransfer: FileTransfer,progress:Int)
    fun onTransferFinished(file: File)
}
