package com.ssafy.mbg.ui.chatbot

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ssafy.mbg.data.chatbot.ChatMessage
import com.ssafy.mbg.data.chatbot.QAData
import kotlinx.coroutines.launch

class ChatViewModel(
    private val application: Application
) : AndroidViewModel(application) {
    private val _messages = MutableLiveData<ChatMessage>()
    val messages: LiveData<ChatMessage> = _messages

    private lateinit var qaData: QAData

    init {
        Log.d("ChatViewModel", "ViewModel 초기화 시작")
        loadQAData()
        sendInitialMessage()
    }

    private fun initializeData() {
        loadQAData()
        sendInitialMessage()
    }

    private fun sendInitialMessage() {
        _messages.value = ChatMessage(
            message = "안녕! 나는 꾸미라고 해! 문화재 탐험을 더 재미있게 만들어줄 네 친구야! 궁금한 게 있으면 언제든 물어봐!",
            isUser = false
        )
    }

    private fun loadQAData() {
        try {
            // 동기적으로 데이터 로드
            val jsonString = application.assets.open("qa_data.json").bufferedReader().use { it.readText() }
            qaData = Gson().fromJson(jsonString, QAData::class.java)
            Log.d("ChatViewModel", "QA 데이터 로드 성공: ${qaData.culturalSites.size}개의 문화재 정보")

            // 데이터 확인용 로그
            qaData.culturalSites.forEach { site ->
                Log.d("ChatViewModel", "문화재: ${site.name}, 키워드: ${site.keywords.joinToString()}")
            }
        } catch (e: Exception) {
            Log.e("ChatViewModel", "QA 데이터 로드 실패", e)
            // 에러 발생 시 기본 데이터라도 초기화
            qaData = QAData(emptyList(), emptyList(), emptyList())
        }
    }

    fun sendMessage(message: String) {
        // 사용자 메시지 추가
        _messages.value = ChatMessage(
            message = message,
            isUser = true
        )

        // 챗봇 응답 처리
        viewModelScope.launch {
            val response = when {
                // QA 데이터에서 응답 확인
                checkBasicResponse(message) != null -> {
                    checkBasicResponse(message)!!
                }
                // 앱 관련 질문 확인
                message.contains("앱") || message.contains("어플") ||
                        message.contains("너") || message.contains("꾸미") ||
                        message.contains("이거") || message.contains("뭐하는") ||
                        message.contains("기능") || message.contains("할 수 있") ->
                {
                    getAppInfoResponse(message)
                }
                // 기본 응답
                else -> {
                    "그건 아직 잘 모르겠어... 다른 걸 물어봐줄래? 😅"
                }
            }

            // 봇 응답 추가
            _messages.value = ChatMessage(
                message = response,
                isUser = false
            )
        }
    }

    private fun checkBasicResponse(message: String): String? {
        if (!::qaData.isInitialized) {
            Log.e("ChatViewModel", "QA 데이터가 초기화되지 않았습니다")
            return "죄송해요, 지금은 답변하기 어려워요. 잠시 후에 다시 물어봐주세요! 😅"
        }

        Log.d("ChatViewModel", "메시지 체크 시작: $message")

        // 일반적인 QA 체크 (먼저 체크)
        qaData.commonQa.forEach { qa ->
            if (qa.keywords.any { keyword -> message.contains(keyword) }) {
                Log.d("ChatViewModel", "일반 QA 매칭 성공: ${qa.response}")
                return qa.response
            }
        }

        // 문화재 관련 정보 체크
        qaData.culturalSites.forEach { site ->
            val matchedKeyword = site.keywords.find { keyword -> message.contains(keyword) }
            if (matchedKeyword != null) {
                Log.d("ChatViewModel", "문화재 매칭: ${site.name}, 키워드: $matchedKeyword")
                return when {
                    message.contains("재미") || message.contains("특이") -> site.funFact
                    message.contains("알고") || message.contains("몰랐") -> site.didYouKnow
                    else -> site.basicInfo
                }
            }
        }


        // 랜덤 사실 제공 (5% 확률) - 문화재 체크 루프 밖으로 이동
        if (Math.random() < 0.05 && qaData.randomFacts.isNotEmpty()) {
            val randomFact = qaData.randomFacts.random()
            Log.d("ChatViewModel", "랜덤 팩트 선택: $randomFact")
            return randomFact
        }

        Log.d("ChatViewModel", "매칭되는 응답 없음")
        return null
    }

    private fun getAppInfoResponse(message: String): String {
        return when {
            message.contains("뭐하는") || message.contains("어떤") || message.contains("이거") -> {
                "나는 네가 문화재를 탐험하면서 더 재미있게 배울 수 있도록 도와주는 꾸미야! " +
                        "우리 함께 문화재를 돌아다니면서 퀴즈도 풀고, 특별한 카드도 모을 수 있어.\n\n" +
                        "문화재를 관람하면서 퀴즈를 풀면 멋진 '문화재 카드'를 받을 수 있고, " +
                        "랜덤 퀴즈를 풀면 재미있는 '일화 카드'도 얻을 수 있다구! 👻"
            }
            message.contains("기능") || message.contains("할 수 있") -> {
                "우리 함께 이런 재미있는 것들을 할 수 있어!\n\n" +
                        "🎯 문화재 퀴즈를 풀고 '문화재 카드' 모으기\n" +
                        "🎲 랜덤 퀴즈를 찾아 '일화 카드' 수집하기\n" +
                        "📚 문화재의 역사와 재미있는 이야기 알아보기\n" +
                        "❓ 궁금한 점 언제든지 물어보기\n\n" +
                        "어때? 재미있지 않니? 😊"
            }
            message.contains("너") || message.contains("꾸미") -> {
                "나는 꾸미야! 너의 문화재 탐험 친구지! 🌟\n\n" +
                        "너와 함께 문화재를 돌아다니면서 재미있는 퀴즈도 내고, " +
                        "멋진 카드도 모으면서 문화재에 대해 배우는 게 내 특기란다! " +
                        "우리 함께 신나게 문화재 탐험을 해보자!"
            }
            else -> {
                "나는 네 문화재 탐험 친구 꾸미야! 함께 퀴즈도 풀고 카드도 모으면서 " +
                        "문화재에 대해 배워보자! 궁금한 게 있으면 언제든 물어봐! 😊"
            }
        }
    }
}