package com.ym.learnkotlin.wifip2p.service

import android.app.IntentService
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.ym.learnkotlin.util.LogUtil
import com.ym.learnkotlin.wifip2p.Constants
import com.ym.learnkotlin.wifip2p.FileTransfer
import com.ym.learnkotlin.wifip2p.OnProgressChangeListener
import java.io.*
import java.lang.Exception
import java.net.InetSocketAddress
import java.net.ServerSocket

class WifiServerService(name: String?) : IntentService(name) {
    companion object{
        private const val TAG = "WifiServerService"
    }
    private lateinit var serverSocket:ServerSocket
    private lateinit var inputStream:InputStream
    private lateinit var objectInputStream:ObjectInputStream
    private lateinit var fileOutputStream: FileOutputStream
    private lateinit var progressChangeListener: OnProgressChangeListener


    override fun onCreate() {
        super.onCreate()
        LogUtil.d(TAG,"onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        clean()
        LogUtil.d(TAG,"onDestory")
    }

    override fun onHandleIntent(p0: Intent?) {
        clean()
        var file: File? =null
        try {
            serverSocket = ServerSocket()
            serverSocket.reuseAddress = true
            serverSocket.bind(InetSocketAddress(Constants.PORT))
            var client = serverSocket.accept()
            LogUtil.e(TAG,"Client address is ${client.inetAddress.hostAddress}")

            inputStream = client.getInputStream()
            objectInputStream = ObjectInputStream(inputStream)
            var fileTransfer: FileTransfer = objectInputStream.readObject() as FileTransfer
            LogUtil.e(TAG,"Documents to be received is $fileTransfer")

            // store file in cacheDir
            var name = fileTransfer.getFileName()
            file = File(cacheDir,name)
            fileOutputStream = FileOutputStream(file)

            var buf = ByteArray(1024)
            var len:Int
            var total:Int = 0
            var progress:Int

            while (inputStream.read(buf).also { len=it } != -1){
                fileOutputStream.write(buf,0,len)
                total += len
                progress = (total * 100 / fileTransfer.getFileLength()).toInt()
                LogUtil.d(TAG,"progress is $progress")
                if (progressChangeListener !=null){
                    progressChangeListener.onProgressChanged(fileTransfer,progress)
                }
            }

            serverSocket.close()
            inputStream.close()
            objectInputStream.close()
            fileOutputStream.close()
        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            clean()
            if (progressChangeListener!= null){
                if (file != null) {
                    progressChangeListener.onTransferFinished(file)
                }
            }
            // launch service,waiting for the next time bind
            startService(Intent(this, WifiServerService::class.java))
        }
    }


    override fun onBind(intent: Intent?): IBinder? {
        return WifiServerBinder()
    }

    fun setProgressChangListener(progressChangListener: OnProgressChangeListener?) {
        if (progressChangListener != null) {
            progressChangeListener = progressChangListener
        }
    }


    private fun clean() {
        if (serverSocket != null && !serverSocket.isClosed) {
            try {
                serverSocket.close()
            } catch (e: IOException) {
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
        if (objectInputStream != null) {
            try {
                objectInputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        if (fileOutputStream != null) {
            try {
                fileOutputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    class WifiServerBinder : Binder() {
        var service: WifiServerService = WifiServerService(TAG)
            get() = service
    }
}