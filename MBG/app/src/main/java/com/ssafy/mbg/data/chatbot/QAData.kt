package com.ssafy.mbg.data.chatbot

import com.google.gson.annotations.SerializedName

data class QAData(
    @SerializedName("common_qa")
    val commonQa: List<QA>,
    @SerializedName("cultural_sites")
    val culturalSites: List<CulturalSite>,
    @SerializedName("random_facts")
    val randomFacts: List<String>
)

data class QA(
    val keywords : List<String>,
    val response : String
)

data class CulturalSite(
    val name : String,
    val keywords: List<String>,
    @SerializedName("basic_info") val basicInfo: String,
    @SerializedName("fun_fact") val funFact: String,
    @SerializedName("did_you_know") val didYouKnow: String
)
