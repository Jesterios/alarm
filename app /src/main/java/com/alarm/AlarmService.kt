package com.alarm

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.widget.Toast
import java.util.Locale

class AlarmService : Service(), TextToSpeech.OnInitListener {
    
    private lateinit var tts: TextToSpeech
    private var isTtsReady = false
    
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale("ru", "RU"))
            if (result != TextToSpeech.LANG_MISSING_DATA && 
                result != TextToSpeech.LANG_NOT_SUPPORTED) {
                isTtsReady = true
                startIllidanWakeUp()
            }
        }
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        tts = TextToSpeech(this, this)
        return START_STICKY
    }
    
    private fun startIllidanWakeUp() {
        val wakeUpPhrases = listOf(
            "Сергей! Проснись! Я пожертвовал всем, чего добился ты?",
            "Сергей, готов ли ты к жертве? Время пробуждаться!",
            "Сергей! Мы не готовы, но мы должны быть! Подъём!",
            "Сергей, пора вставать! Твои миссии не выполнятся сами!"
        )
        
        // Произносим фразу
        tts.speak(wakeUpPhrases.random(), TextToSpeech.QUEUE_FLUSH, null, "illidan_wakeup")
        
        Toast.makeText(this, "Иллидан будит Сергея!", Toast.LENGTH_LONG).show()
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}
