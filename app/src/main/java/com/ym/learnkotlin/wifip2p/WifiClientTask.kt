package com.ym.learnkotlin.wifip2p

import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import com.ym.learnkotlin.util.LogUtil
import com.ym.learnkotlin.wifip2p.util.MD5Util
import java.io.*
import java.lang.Exception
import java.lang.NullPointerException
import java.net.InetSocketAddress
import java.net.Socket
import java.util.*

class WifiClientTask(context: Context?): AsyncTask<Any, Int, Boolean>() {
    companion object{
        private const val TAG = "WifiClientTask"
    }
    private lateinit var progressDialog:ProgressDialog
    private lateinit var mContext:Context

    override fun doInBackground(vararg params: Any): Boolean? {
        var socket: Socket? = null
        var outputStream: OutputStream? = null
        var objectOutputStream: ObjectOutputStream? = null
        var inputStream: InputStream? = null
        try {
            val hostAddress = params[0].toString()
            val imageUri = Uri.parse(params[1].toString())
            val outputFilePath = getOutputFilePath(imageUri)
            val outputFile = File(outputFilePath)
            val fileTransfer = FileTransfer()
            val fileName = outputFile.name
            val fileMa5: String? = MD5Util.getMd5(outputFile)
            val fileLength = outputFile.length()
            fileTransfer.setFileName(fileName)
            fileTransfer.setMd5(fileMa5)
            fileTransfer.setFileLength(fileLength)
            LogUtil.e(TAG, "File's MD5 is ：" + fileTransfer.getMd5())
            socket = Socket()
            socket.bind(null)
            socket.connect(InetSocketAddress(hostAddress, Constants.PORT), 10000)
            outputStream = socket.getOutputStream()
            objectOutputStream = ObjectOutputStream(outputStream)
            objectOutputStream.writeObject(fileTransfer)
            inputStream = FileInputStream(outputFile)
            val fileSize = fileTransfer.getFileLength()
            var total: Long = 0
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } != -1) {
                outputStream.write(buf, 0, len)
                total += len.toLong()
                val progress = (total * 100 / fileSize).toInt()
                publishProgress(progress)
                LogUtil.e(TAG, "Progress：$progress")
            }
            socket.close()
            inputStream.close()
            outputStream.close()
            objectOutputStream.close()
            socket = null
            inputStream = null
            outputStream = null
            objectOutputStream = null
            LogUtil.e(TAG, "Send file success！")
            return true
        } catch (e: Exception) {
            LogUtil.e(TAG, "Send file exception: " + e.message)
        } finally {
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return false
    }


    fun WifiClientTask(context: Context) {
        mContext = context.applicationContext
        progressDialog = ProgressDialog(context)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setTitle("Sending file")
        progressDialog.setMax(100)
    }

    override fun onPreExecute() {
        progressDialog.show()
    }

    override fun onProgressUpdate(vararg values: Int?) {
        progressDialog.progress = values[0]!!
    }

    override fun onPostExecute(result: Boolean?) {
        progressDialog.cancel()
        LogUtil.e(TAG,"onPostExecute $result")
    }

    @Throws(Exception::class)
    private fun getOutputFilePath(fileUri: Uri): String? {
        val outputFilePath: String = mContext.getExternalCacheDir()?.getAbsolutePath() +
                File.separatorChar + Random().nextInt(10000) +
                Random().nextInt(10000).toString() + ".jpg"
        val outputFile = File(outputFilePath)
        if (!outputFile.exists()) {
            outputFile.getParentFile().mkdirs()
            outputFile.createNewFile()
        }
        val outputFileUri: Uri = Uri.fromFile(outputFile)
        copyFile(mContext, fileUri, outputFileUri)
        return outputFilePath
    }

    @Throws(NullPointerException::class, IOException::class)
    private fun copyFile(context: Context, inputUri: Uri, outputUri: Uri) {
        context.contentResolver.openInputStream(inputUri).use { inputStream ->
            FileOutputStream(outputUri.path).use { outputStream ->
                if (inputStream == null) {
                    throw NullPointerException("InputStream for given input Uri is null")
                }
                val buffer = ByteArray(1024)
                var length: Int
                while (inputStream.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }
            }
        }
    }
}