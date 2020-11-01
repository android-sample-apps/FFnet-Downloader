package fr.ffnet.downloader.common

interface FFLogger {

    companion object {

        const val EVENT_KEY = "ffnet-event"
        const val EVENT_MESSAGE = "eventMessage"

        private var instance: FFLogger = VoidLogger()

        fun init(logger: FFLogger) {
            instance = logger
        }

        fun log(throwable: Throwable, additionalInfo: Map<String, String> = emptyMap()) =
            instance.log(throwable, additionalInfo)

        fun log(data: Map<String, Any>) =
            instance.log(data)

        fun e(data: Map<String, Any>) =
            instance.e(data)

        fun e(tag: String, msg: String) =
            instance.e(tag, msg)

        fun w(tag: String, msg: String) =
            instance.w(tag, msg)

        fun d(tag: String, msg: String) =
            instance.d(tag, msg)
    }

    fun log(throwable: Throwable, additionalInfo: Map<String, String> = emptyMap())
    fun log(data: Map<String, Any>)
    fun e(data: Map<String, Any>)
    fun e(tag: String, msg: String)
    fun w(tag: String, msg: String)
    fun d(tag: String, msg: String)
}

private class VoidLogger : FFLogger {

    override fun log(throwable: Throwable, additionalInfo: Map<String, String>) {}

    override fun log(data: Map<String, Any>) {}

    override fun e(data: Map<String, Any>) {}

    override fun e(tag: String, msg: String) {}

    override fun w(tag: String, msg: String) {}

    override fun d(tag: String, msg: String) {}
}
