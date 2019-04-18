package fr.ffnet.downloader.fanfictionutils

import android.os.Environment
import com.itextpdf.text.Document
import com.itextpdf.text.PageSize
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.tool.xml.XMLWorkerHelper
import fr.ffnet.downloader.search.Chapter
import fr.ffnet.downloader.search.Fanfiction
import org.apache.commons.text.StringEscapeUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.StringReader
import javax.inject.Inject


class PdfBuilder @Inject constructor() {

    companion object {
        private val HTML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE html>\n<html xmlns=\"http://www.w3.org/1999/xhtml\" >\n\n<head>\n" +
            "<meta charset=\"utf-8\" />\n"
        private val HTML_FOOTER = "</body>\n</html>"
    }

    fun buildPdf(fanfiction: Fanfiction): String {
        val fileTitle = "${fanfiction.title}.pdf"
        val file = File(
            File(
                Environment.getExternalStorageDirectory(),
                Environment.DIRECTORY_DOWNLOADS
            ),
            fileTitle
        )

        val outputStream = ByteArrayOutputStream()
        val document = Document(PageSize.A4)
        val pdfWritter = PdfWriter.getInstance(document, outputStream)

        document.open()
        document.addTitle(fanfiction.title)
        val worker = XMLWorkerHelper.getInstance()

        fanfiction.chapterList.forEach { chapter ->
            val chapterHtml = generateHtmlPageFromChapter(chapter)
            worker.parseXHtml(pdfWritter, document, StringReader(chapterHtml))
        }
        document.close()

        val inputStream = ByteArrayInputStream(outputStream.toByteArray())
        inputStream.use { input ->
            file.outputStream().use { input.copyTo(it) }
        }
        return fileTitle
    }

    private fun generateHtmlPageFromChapter(chapter: Chapter): String {
        var htmlPage = chapter.content
        htmlPage = "$HTML_HEADER<title>" + StringEscapeUtils.escapeHtml4(
            chapter.title
        ) + "</title>\n</head>\n<body>\n" + htmlPage + HTML_FOOTER
        htmlPage = htmlPage
            .replace("noshade", "")
            .replace("<br>", "<br/>")
            .replace("<hr size=\"1\" >", "<hr/>")
        return htmlPage
    }
}
