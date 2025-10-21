package com.alarm

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import java.util.Locale

class VoiceRecognitionManager(private val activity: Activity) {
    
    private val speechRecognizer: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity)
    private var isListening = false
    
    fun startListening() {
        if (!isListening) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale("ru", "RU"))
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Скажите 'отключись', 'я встал' или 'я проснулся'")
            }
            
            speechRecognizer.setRecognitionListener(createRecognitionListener())
            speechRecognizer.startListening(intent)
            isListening = true
            
            Toast.makeText(activity, "Слушаю...", Toast.LENGTH_SHORT).show()
        }
    }
    
    fun stopListening() {
        if (isListening) {
            speechRecognizer.stopListening()
            isListening = false
        }
    }
    
    private fun createRecognitionListener(): android.speech.RecognitionListener {
        return object : android.speech.RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                isListening = false
            }
            
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                matches?.let { processVoiceCommand(it[0].toLowerCase()) }
                isListening = false
            }
            
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }
    
    private fun processVoiceCommand(command: String) {
        when {
            command.contains("отключись") || 
            command.contains("отключи") || 
            command.contains("выключи") -> {
                stopAlarm()
            }
            command.contains("я встал") || 
            command.contains("встал") || 
            command.contains("проснулся") -> {
                stopAlarm()
            }
            else -> {
                Toast.makeText(activity, "Команда не распознана. Скажите 'отключись'", Toast.LENGTH_SHORT).show()
                startListening() // Продолжаем слушать
            }
        }
    }
    
    private fun stopAlarm() {
        Toast.makeText(activity, "Будильник отключен! Доброе утро, Сергей!", Toast.LENGTH_LONG).show()
        // Останавливаем сервис будильника
        activity.stopService(Intent(activity, AlarmService::class.java))
    }
}
