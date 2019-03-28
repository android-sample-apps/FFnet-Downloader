package fr.ffnet.downloader.fanfictionutils

import android.content.Context
import android.os.Environment
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.tool.xml.XMLWorkerHelper
import fr.ffnet.downloader.search.Fanfiction
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject


class PdfBuilder @Inject constructor() {

    fun buildPdf(context: Context, fanfiction: Fanfiction): File {

        val fileTitle = "${fanfiction.title}.pdf"
        val file = File(
            File(
                Environment.getExternalStorageDirectory(),
                Environment.DIRECTORY_DOWNLOADS
            ),
            fileTitle
        )
        val document = Document()
        val pdfWriter = PdfWriter.getInstance(document, FileOutputStream(file))

        document.open()
        val worker = XMLWorkerHelper.getInstance()
        fanfiction.chapterList.forEach { chapter ->
//            worker.parseXHtml(pdfWriter, document, chapter.content)
            document.add(Paragraph(chapter.content))
        }
        document.close()
        return file
    }
}
