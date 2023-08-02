package com.dionis.escolinhajdb.presentation.pdf

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.presentation.home.HomeActivity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

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

    private fun getUniqueFileName(directory: File, fileName: String): String {
        val pdf = ".pdf"
        var uniqueFileName = "$fileName$pdf"
        var counter = 1

        if (File(directory, uniqueFileName).exists())
        while (File(directory, uniqueFileName).exists()) {
            counter++
            uniqueFileName = "$fileName ($counter)$pdf"
        } else {
            uniqueFileName = "${fileName}$pdf"
        }

        return uniqueFileName
       // return uniqueFileName
    }

    fun savePdf(
        layout: View,
        fileName: String,
        context: Context
    ) {

        createPdf(layout)

        // Salva o documento em um arquivo PDF
        val directoryName = "Escolinha" // Nome do diretório que você deseja criar
        val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), directoryName)

       // val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)

        if (!directory.exists()) {
            directory.mkdirs() // Cria o diretório se ele não existir
        }

        val uniqueFileName = getUniqueFileName(directory,
            fileName)

        val file = File(directory, uniqueFileName)
        val outputStream = FileOutputStream(
            file)
        document.writeTo(outputStream)
        document.close()
        outputStream.close()

        showNotification(context, fileName, "documento salvo", file)
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

    // Função para mostrar a notificação
    fun showNotification(context: Context, title: String, message: String, arquivo: File) {
        val notificationIntent = Intent(context, HomeActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        )


        // Intent para abrir o arquivo PDF
        val openFileIntent = Intent(Intent.ACTION_VIEW)
        val fileUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            arquivo

        )
        openFileIntent.setDataAndType(fileUri, "application/pdf")
        openFileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        val openFilePendingIntent = PendingIntent.getActivity(
            context,
            0,
            openFileIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // Adicionar a ação à notificação
//        notificationBuilder.addAction(
//            R.drawable.logo_jdb, // Ícone para a ação
//            "Abrir PDF", // Texto da ação
//            openFilePendingIntent // PendingIntent para abrir o arquivo
//        )

        val notificationBuilder = NotificationCompat.Builder(context, "pdf_channel_id")
            .setSmallIcon(R.drawable.logo_jdb)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(openFilePendingIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notificationBuilder.build())





    }




}