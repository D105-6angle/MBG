package com.ssafy.tmbg.data.report.dao

import com.ssafy.tmbg.data.report.SatisfactionData

data class ReportData(
    val satisfaction: List<Satisfaction>,
    val comments : List<String>
)
