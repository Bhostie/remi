package com.example.appcent_case_study.gesture

import android.content.Context
import android.os.SystemClock
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import java.util.LinkedList
import kotlin.math.abs
import kotlin.math.sqrt

class HandGestureProcessor(
    context: Context,
    private val onGestureDetected: (GestureType) -> Unit
) {
    private val TAG = "HandGestureProcessor"
    private val modelPath = "hand_landmarker.task"
    private val swipeHistory: LinkedList<Float> = LinkedList()
    private val maxHistorySize = 5
    private val swipeThreshold = 0.15f // normalized x-movement
    private val palmThreshold = 0.1f   // simple distance threshold

    private val handLandmarker: HandLandmarker

    init {

        val baseOptionsBuilder = BaseOptions.builder().setModelAssetPath(modelPath)
        val baseOptions = baseOptionsBuilder.build()

        val options = HandLandmarker.HandLandmarkerOptions.builder()
            .setBaseOptions(baseOptions)
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setMinHandDetectionConfidence(0.5f)
            .setMinTrackingConfidence(0.5f)
            .setMinHandPresenceConfidence(0.5f)
            .setResultListener(::onHandResult)
            .build()

        handLandmarker = HandLandmarker.createFromOptions(context, options)
    }


    // palm detection variables
    private var lastOpenPalmStartTime: Long = 0
    private var palmHeld = false
    private val palmHoldDurationMillis = 3000L // 3 seconds


    // Rapid Palm variables
    private enum class PalmState { OPEN, CLOSED, UNKNOWN }
    private var lastPalmState = PalmState.UNKNOWN
    private var gestureSequence: MutableList<Long> = mutableListOf()
    private val gestureWindowMs = 2000L
    private val minPairs = 2


    // Gesture Cooldown
    private val gestureCooldownMs = 2000L  // 2 second cooldown
    private val lastGestureTimes = mutableMapOf<GestureType, Long>()

    fun detectAsync(mpImage: MPImage) {
        handLandmarker.detectAsync(mpImage, SystemClock.uptimeMillis())
    }

    private fun onHandResult(result: HandLandmarkerResult, input: MPImage) {
        if (result.landmarks().isEmpty()) return

        val now = SystemClock.uptimeMillis()
        val landmarks = result.landmarks()[0]
        val x = landmarks[0].x()

        // === GESTURE 1: SWIPE DETECTION ===
        swipeHistory.add(x)
        if (swipeHistory.size > maxHistorySize) swipeHistory.removeFirst()

        val delta = swipeHistory.last - swipeHistory.first
        if (abs(delta) > swipeThreshold) {
            val gesture = if (delta > 0) GestureType.SWIPE_RIGHT else GestureType.SWIPE_LEFT
            triggerGesture(gesture)
            swipeHistory.clear()
            return
        }

        // === Palm Geometry ===
        val dist1 = distance(landmarks[4], landmarks[8])   // thumb to index
        val dist2 = distance(landmarks[8], landmarks[12])  // index to middle
        val isPalmOpen = dist1 > palmThreshold && dist2 > palmThreshold

        // === GESTURE 2: OPEN PALM HOLD (2 sec) ===
        if (isPalmOpen) {
            if (!palmHeld) {
                lastOpenPalmStartTime = now
                palmHeld = true
            } else if (now - lastOpenPalmStartTime >= palmHoldDurationMillis) {
                triggerGesture(GestureType.OPEN_PALM)
                palmHeld = false  // Reset so it doesn't re-trigger continuously
            }
        } else {
            palmHeld = false
        }

        // === GESTURE 3: DOUBLE PALM CLAP (open-close-open-close in time window) ===
        val currentPalmState = when {
            isPalmOpen -> PalmState.OPEN
            else -> PalmState.CLOSED
        }

        if (currentPalmState != lastPalmState) {
            gestureSequence.add(now)

            // Retain only recent transitions within the window
            gestureSequence = gestureSequence.filter { now - it <= gestureWindowMs }.toMutableList()

            if (gestureSequence.size >= minPairs * 2) {
                triggerGesture(GestureType.DOUBLE_PALM_CLAP)
                gestureSequence.clear()
            }

            lastPalmState = currentPalmState
        }

        // Initialize palm state if it was unknown
        if (lastPalmState == PalmState.UNKNOWN) {
            lastPalmState = currentPalmState
        }
    }


    private fun distance(p1: com.google.mediapipe.tasks.components.containers.NormalizedLandmark,
                         p2: com.google.mediapipe.tasks.components.containers.NormalizedLandmark): Float {
        val dx = p1.x() - p2.x()
        val dy = p1.y() - p2.y()
        return sqrt(dx * dx + dy * dy)
    }

    fun close() {
        handLandmarker.close()
    }

    private fun canTrigger(gesture: GestureType): Boolean {
        val now = SystemClock.uptimeMillis()
        val lastTime = lastGestureTimes[gesture] ?: 0L
        return (now - lastTime) >= gestureCooldownMs
    }

    private fun triggerGesture(gesture: GestureType) {
        if (canTrigger(gesture)) {
            lastGestureTimes[gesture] = SystemClock.uptimeMillis()
            onGestureDetected(gesture)
        }
    }
}
