package com.william.bizflow.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.william.bizflow.models.Sale
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object ExportUtils {

    fun exportSalesToCSV(context: Context, sales: List<Sale>) {
        if (sales.isEmpty()) {
            Toast.makeText(context, "No sales to export", Toast.LENGTH_SHORT).show()
            return
        }

        val filename = "Bizflow_Sales_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.csv"
        val csvHeader = "Product Name,Quantity,Total Amount,Profit,Date\n"
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        try {
            val file = File(context.cacheDir, filename)
            val out = FileOutputStream(file)
            out.write(csvHeader.toByteArray())

            for (sale in sales) {
                val date = sdf.format(Date(sale.timestamp))
                val line = "${sale.productName},${sale.quantity},${sale.totalAmount},${sale.profit},$date\n"
                out.write(line.toByteArray())
            }
            out.close()

            shareFile(context, file, "text/csv")

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun exportProductsToCSV(context: Context, products: List<com.william.bizflow.models.Product>) {
        if (products.isEmpty()) {
            Toast.makeText(context, "No products to export", Toast.LENGTH_SHORT).show()
            return
        }

        val filename = "Bizflow_Inventory_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.csv"
        val csvHeader = "Product Name,Buying Price,Selling Price,Stock Count\n"

        try {
            val file = File(context.cacheDir, filename)
            val out = FileOutputStream(file)
            out.write(csvHeader.toByteArray())

            for (product in products) {
                val line = "${product.name},${product.buyingPrice},${product.sellingPrice},${product.stockCount}\n"
                out.write(line.toByteArray())
            }
            out.close()

            shareFile(context, file, "text/csv")

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun exportSalesToPDF(context: Context, sales: List<Sale>) {
        if (sales.isEmpty()) {
            Toast.makeText(context, "No sales to export", Toast.LENGTH_SHORT).show()
            return
        }

        val pdfDocument = PdfDocument()
        val paint = Paint()
        val titlePaint = Paint()
        val linePaint = Paint().apply {
            color = android.graphics.Color.LTGRAY
            strokeWidth = 1f
        }

        // Page info: A4 size is roughly 595 x 842 points
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        titlePaint.textSize = 20f
        canvas.drawText("Bizflow Sales Report", 40f, 50f, titlePaint)

        paint.textSize = 10f
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        canvas.drawText("Generated on: ${sdf.format(Date())}", 40f, 75f, paint)

        var y = 110f
        val lineSpacing = 25f

        // Draw Table Header Background
        val headerPaint = Paint().apply { color = android.graphics.Color.rgb(240, 240, 240) }
        canvas.drawRect(35f, y - 15f, 560f, y + 10f, headerPaint)

        // Table Headers
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Product", 40f, y, paint)
        canvas.drawText("Qty", 220f, y, paint)
        canvas.drawText("Total (Ksh)", 280f, y, paint)
        canvas.drawText("Profit (Ksh)", 380f, y, paint)
        canvas.drawText("Date", 480f, y, paint)
        
        y += lineSpacing
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        for (sale in sales) {
            if (y > 800) { // Start new page if current page is full
                pdfDocument.finishPage(page)
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                y = 50f
                
                // Redraw headers on new page
                canvas.drawRect(35f, y - 15f, 560f, y + 10f, headerPaint)
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText("Product", 40f, y, paint)
                canvas.drawText("Qty", 220f, y, paint)
                canvas.drawText("Total (Ksh)", 280f, y, paint)
                canvas.drawText("Profit (Ksh)", 380f, y, paint)
                canvas.drawText("Date", 480f, y, paint)
                y += lineSpacing
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            }

            canvas.drawText(sale.productName.take(25), 40f, y, paint)
            canvas.drawText(sale.quantity, 220f, y, paint)
            canvas.drawText(sale.totalAmount, 280f, y, paint)
            canvas.drawText(sale.profit, 380f, y, paint)
            canvas.drawText(sdf.format(Date(sale.timestamp)).split(" ")[0], 480f, y, paint)
            
            // Draw a subtle line between rows
            canvas.drawLine(35f, y + 5f, 560f, y + 5f, linePaint)
            
            y += lineSpacing
        }

        pdfDocument.finishPage(page)

        val filename = "Bizflow_Report_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"
        val file = File(context.cacheDir, filename)

        try {
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()
            shareFile(context, file, "application/pdf")
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "PDF Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun exportFinancialSummaryToPDF(
        context: Context,
        actualRevenue: Double,
        actualProfit: Double,
        totalProducts: Int,
        totalStockValue: Double,
        potentialProfit: Double
    ) {
        val pdfDocument = PdfDocument()
        val paint = Paint()
        val titlePaint = Paint()
        val linePaint = Paint().apply {
            color = android.graphics.Color.BLACK
            strokeWidth = 2f
        }

        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        titlePaint.textSize = 26f
        canvas.drawText("Bizflow Business Summary", 40f, 60f, titlePaint)
        canvas.drawLine(40f, 75f, 555f, 75f, linePaint)

        paint.textSize = 12f
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        canvas.drawText("Report Period: All Time", 40f, 100f, paint)
        canvas.drawText("Generated on: ${sdf.format(Date())}", 40f, 120f, paint)

        paint.textSize = 16f
        var y = 180f
        val lineSpacing = 45f

        // Draw background for summary cards
        val cardPaint = Paint().apply { color = android.graphics.Color.rgb(250, 250, 250) }
        
        fun drawMetric(label: String, value: String, currentY: Float) {
            canvas.drawRect(40f, currentY - 30f, 555f, currentY + 10f, cardPaint)
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText(label, 50f, currentY, paint)
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(value, 380f, currentY, paint)
        }

        drawMetric("Total Sales Revenue:", "Ksh $actualRevenue", y); y += lineSpacing
        drawMetric("Actual Profit:", "Ksh $actualProfit", y); y += lineSpacing
        drawMetric("Inventory Items:", totalProducts.toString(), y); y += lineSpacing
        drawMetric("Total Stock Value:", "Ksh $totalStockValue", y); y += lineSpacing
        drawMetric("Potential Remaining Profit:", "Ksh $potentialProfit", y)
        
        y += 100f
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Business Insight:", 40f, y, paint)
        
        y += 25f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        val summaryText = "Current business performance shows a profit of Ksh $actualProfit realized from Ksh $actualRevenue revenue. " +
                "The remaining stock in inventory holds a potential additional profit of Ksh $potentialProfit."
        
        val words = summaryText.split(" ")
        var line = ""
        for (word in words) {
            if (paint.measureText("$line $word") < 500) {
                line += "$word "
            } else {
                canvas.drawText(line, 40f, y, paint)
                y += 20f
                line = "$word "
            }
        }
        canvas.drawText(line, 40f, y, paint)

        pdfDocument.finishPage(page)

        val filename = "Bizflow_Summary_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"
        val file = File(context.cacheDir, filename)

        try {
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()
            shareFile(context, file, "application/pdf")
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Summary Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareFile(context: Context, file: File, mimeType: String) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_SUBJECT, "Bizflow Report")
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Share Report"))
    }
}
