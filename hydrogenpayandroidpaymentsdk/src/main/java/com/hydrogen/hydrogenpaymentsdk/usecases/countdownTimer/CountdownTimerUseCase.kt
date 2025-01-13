package com.hydrogen.hydrogenpaymentsdk.usecases.countdownTimer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CountdownTimerUseCase {

    private val _timeLeft = MutableStateFlow(0) // Time left in seconds
    val timeLeft: StateFlow<Int> = _timeLeft.asStateFlow()

    private var job: Job? = null
    private var isPaused = false

    fun start(minutes: Int) {
        reset()
        val totalSeconds = minutes * 60
        _timeLeft.value = totalSeconds
        resume()
    }

    fun resume() {
        if (job != null || isPaused) return
        isPaused = false
        job = CoroutineScope(Dispatchers.Default).launch {
            while (_timeLeft.value > 0) {
                delay(1000L)
                _timeLeft.update { (_timeLeft.value - 1) }
            }
            pause() // Auto-pause when the timer reaches zero
        }
    }

    fun pause() {
        isPaused = true
        job?.cancel()
        job = null
    }

    fun reset() {
        pause()
        _timeLeft.update { 0 }
    }
}