package com.alarm

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.widget.Toast
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

class AlarmService : Service(), TextToSpeech.OnInitListener {
    
    private lateinit var tts: TextToSpeech
    private var isTtsReady = false
    private lateinit var voiceRecognitionManager: VoiceRecognitionManager
    private var timer: Timer? = null
    
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale("ru", "RU"))
            if (result != TextToSpeech.LANG_MISSING_DATA && 
                result != TextToSpeech.LANG_NOT_SUPPORTED) {
                isTtsReady = true
                voiceRecognitionManager = VoiceRecognitionManager(this)
                startIllidanWakeUpSequence()
            }
        }
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        tts = TextToSpeech(this, this)
        return START_STICKY
    }
    
    private fun startIllidanWakeUpSequence() {
        val wakeUpPhrases = listOf(
            "Сергей! Проснись! Я пожертвовал всем, чего добился ты?",
            "Сергей, готов ли ты к жертве? Время пробуждаться!",
            "Сергей, пора вставать! Работа требует работать!",
            "Сергей, подъём! Я пятнадцать тысяч лет уже не сплю. А ты до сих пор спишь. Вставай!"
        )
        
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (isTtsReady) {
                    tts.speak(wakeUpPhrases.random(), TextToSpeech.QUEUE_FLUSH, null, "illidan_wakeup")
                    // Начинаем слушать команду после каждой фразы
                    voiceRecognitionManager.startListening()
                }
            }
        }, 0, 15000) // Повторять каждые 15 секунд
        
        Toast.makeText(this, "Иллидан будит Сергея!", Toast.LENGTH_LONG).show()
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        timer?.cancel()
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}
