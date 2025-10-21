package com.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class AlarmReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        // Здесь будет голос Иллидана
        Toast.makeText(
            context, 
            "Иллидан: 'Проснись, смертный! Ты готов к жертве?'", 
            Toast.LENGTH_LONG
        ).show()
        
        // Запускаем сервис для голосового будильника
        val serviceIntent = Intent(context, AlarmService::class.java)
        context.startService(serviceIntent)
    }
}
