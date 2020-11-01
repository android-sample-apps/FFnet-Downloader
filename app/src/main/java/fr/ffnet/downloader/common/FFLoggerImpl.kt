package fr.ffnet.downloader.common

import com.google.gson.Gson
import timber.log.Timber

class FFLoggerImpl : FFLogger {
    override fun log(throwable: Throwable, additionalInfo: Map<String, String>) =
        Timber.e(throwable)

    override fun log(data: Map<String, Any>) = Timber.i(Gson().toJsonTree(data).toString())

    override fun e(data: Map<String, Any>) = Timber.e(Gson().toJsonTree(data).toString())

    override fun e(tag: String, msg: String) = Timber.tag(tag).e(msg)

    override fun w(tag: String, msg: String) = Timber.tag(tag).w(msg)

    override fun d(tag: String, msg: String) {
        Timber.tag(tag).d(msg)
    }
}
