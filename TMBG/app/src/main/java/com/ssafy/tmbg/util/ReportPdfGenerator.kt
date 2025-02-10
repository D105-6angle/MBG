package com.ssafy.tmbg.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.view.View
import androidx.core.content.ContextCompat
import com.ssafy.tmbg.data.report.SatisfactionData
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportPdfGenerator(private val context: Context) {
    private val paint = Paint()
    private val pageWidth = 595 // A4 width in points
    private val pageHeight = 842 // A4 height in points
    private val margin = 50f

    fun generatePdf(
        className: String,
        location: String,
        question1Data: List<SatisfactionData>,
        question2Data: List<SatisfactionData>,
        question3Data: List<SatisfactionData>,
        studentList: List<String>,
        charts: List<View>
    ): File {
        val pdfDocument = PdfDocument()

        // 첫 번째 페이지 생성
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas
        var yPosition = margin

        // 제목
        paint.apply {
            textSize = 24f
            textAlign = Paint.Align.CENTER
            style = Paint.Style.FILL
        }
        canvas.drawText("현장 체험 학습 보고서", pageWidth / 2f, yPosition + 24f, paint)
        yPosition += 50f

        // 기본 정보
        paint.apply {
            textSize = 16f
            textAlign = Paint.Align.LEFT
        }
        canvas.drawText("학급: $className", margin, yPosition + 24f, paint)
        yPosition += 30f
        canvas.drawText("장소: $location", margin, yPosition + 24f, paint)
        yPosition += 30f
        canvas.drawText("날짜: ${getCurrentDate()}", margin, yPosition + 24f, paint)
        yPosition += 50f

        // 만족도 조사 헤더
        paint.textSize = 18f
        canvas.drawText("만족도 조사 결과", margin, yPosition + 24f, paint)
        yPosition += 40f

        // 각 문항 및 차트 그리기
        paint.textSize = 14f

        // 문항 1
        if (yPosition + 300 > pageHeight - margin) { // 새 페이지 필요
            pdfDocument.finishPage(page)
            pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pdfDocument.pages.size + 1).create()
            page = pdfDocument.startPage(pageInfo)
            canvas = page.canvas
            yPosition = margin
        }
        yPosition = drawQuestionWithChart(canvas, "1. 현장 체험 학습이 학생들의 학습에 도움이 되었다고 생각하십니까?",
            question1Data, charts[0], yPosition)
        yPosition += 30f

        // 문항 2
        if (yPosition + 300 > pageHeight - margin) {
            pdfDocument.finishPage(page)
            pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pdfDocument.pages.size + 1).create()
            page = pdfDocument.startPage(pageInfo)
            canvas = page.canvas
            yPosition = margin
        }
        yPosition = drawQuestionWithChart(canvas, "2. 코스 및 활동 프로그램 내용에 대하여 전체적으로 만족하십니까?",
            question2Data, charts[1], yPosition)
        yPosition += 30f

        // 문항 3
        if (yPosition + 300 > pageHeight - margin) {
            pdfDocument.finishPage(page)
            pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pdfDocument.pages.size + 1).create()
            page = pdfDocument.startPage(pageInfo)
            canvas = page.canvas
            yPosition = margin
        }
        yPosition = drawQuestionWithChart(canvas, "3. 안전사고에 대한 교육과 대비는 잘 되었다고 생각하십니까?",
            question3Data, charts[2], yPosition)
        yPosition += 50f

        // 학생 목록
        if (yPosition + (studentList.size / 3 * 25) + 50 > pageHeight - margin) {
            pdfDocument.finishPage(page)
            pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pdfDocument.pages.size + 1).create()
            page = pdfDocument.startPage(pageInfo)
            canvas = page.canvas
            yPosition = margin
        }

        paint.textSize = 18f
        canvas.drawText("참여 학생 목록", margin, yPosition + 24f, paint)
        yPosition += 40f

        paint.textSize = 14f
        studentList.chunked(3).forEach { rowStudents ->
            val row = rowStudents.joinToString("     ")
            canvas.drawText(row, margin, yPosition, paint)
            yPosition += 25f
        }

        pdfDocument.finishPage(page)

        // PDF 저장
        val fileName = "report_${getCurrentDate("yyyyMMdd_HHmmss")}.pdf"
        val file = File(context.getExternalFilesDir(null), fileName)
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()

        return file
    }

    private fun drawQuestionWithChart(
        canvas: Canvas,
        question: String,
        data: List<SatisfactionData>,
        chartView: View,
        startY: Float
    ): Float {
        var yPosition = startY

        // 질문
        canvas.drawText(question, margin, yPosition + 24f, paint)
        yPosition += 40f

        // 차트
        val chartBitmap = getBitmapFromView(chartView)
        val chartSize = 250f
        val scaledBitmap = Bitmap.createScaledBitmap(
            chartBitmap,
            chartSize.toInt(),
            chartSize.toInt(),
            true
        )
        canvas.drawBitmap(scaledBitmap, margin, yPosition, paint)

        // 결과 텍스트
        yPosition += chartSize + 30f
        var xPosition = margin
        data.forEach { satisfaction ->
            val text = "${satisfaction.type.text}: %.1f%%".format(satisfaction.percentage)
            canvas.drawText(text, xPosition, yPosition, paint)
            xPosition += 170f
        }

        return yPosition
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            view.width,
            view.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun getCurrentDate(pattern: String = "yyyy년 MM월 dd일"): String {
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return dateFormat.format(Date())
    }
}