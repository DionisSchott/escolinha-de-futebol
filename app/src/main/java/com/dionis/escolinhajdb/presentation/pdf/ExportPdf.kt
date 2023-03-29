package com.dionis.escolinhajdb.presentation.pdf

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

class ExportPdf {


    val document = PdfDocument()

    fun createPdf(layout: View) {


        val pageInfo = PdfDocument.PageInfo.Builder(
            layout.measuredWidth,
            layout.measuredHeight,
            1).create()


        val page = document.startPage(pageInfo)


        val canvas = page.canvas
        val paint = layout.background?.let { Paint().apply { color = Color.WHITE } } ?: Paint().apply { color = Color.WHITE }
        canvas.drawPaint(paint)
        layout.draw(canvas)


        document.finishPage(page)

    }

    fun savePdf(
        layout: View,
        nomeArquivo: String,
    ) {

        createPdf(layout)

        // Salva o documento em um arquivo PDF
        val diretorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val arquivo = File(diretorio, "$nomeArquivo.pdf")
        val outputStream = FileOutputStream(arquivo)
        document.writeTo(outputStream)
        document.close()
        outputStream.close()
    }


    fun sharePdf(context: Context, layout: View, fileName: String) {

        createPdf(layout)

        // Cria um ContentResolver para acessar o ContentProvider
        val contentResolver = context.contentResolver

        // Define os metadados do arquivo
        val mimeType = "application/pdf"
        val displayName = "$fileName.pdf"
        val description = "PDF gerado pelo aplicativo"

        // Cria um ContentValues com os metadados do arquivo
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.TITLE, description)
        }

        // Insere o PDF como um novo registro no ContentProvider
        val uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), values)

        // Abre um OutputStream para gravar o conteúdo do PDF no ContentProvider
        uri?.let {
            contentResolver.openOutputStream(it)?.use { outputStream ->
                document.writeTo(outputStream)
                outputStream.close()
            }

            // Cria um Intent para compartilhar o PDF
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = mimeType
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            // Inicia a Activity de compartilhamento
            context.startActivity(Intent.createChooser(shareIntent, "Compartilhar PDF"))
        }

        // Finaliza o documento PDF
        document.close()
    }


    fun sharePdf(context: Context, fileName: String) {


        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "application/pdf"

        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val file = File(directory, "$fileName.pdf")

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

        context.startActivity(Intent.createChooser(shareIntent.putExtra(Intent.EXTRA_STREAM, uri), "Compartilhar PDF"))


    }

}