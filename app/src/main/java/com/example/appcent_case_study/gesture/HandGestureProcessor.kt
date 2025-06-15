package com.example.appcent_case_study.gesture

import android.content.Context
import android.os.SystemClock
import android.util.Log
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
            .setMinHandDetectionConfidence(0.8f)
            .setMinTrackingConfidence(0.8f)
            .setMinHandPresenceConfidence(0.85f)
            .setResultListener(::onHandResult)
            .build()

        handLandmarker = HandLandmarker.createFromOptions(context, options)
    }


    // palm detection variables
    private var lastOpenPalmStartTime: Long = 0
    private var openPalmHeld = false
    private val palmHoldDurationMillis = 3000L // 3 seconds

    // For palm close hold
    private var closePalmHeld = false // is the palm currently held open?

    // Rapid Palm variables
    private enum class PalmState { OPEN, CLOSED, UNKNOWN }
    private var lastPalmState = PalmState.UNKNOWN
    private var gestureSequence: MutableList<Long> = mutableListOf()
    private val gestureWindowMs = 2000L
    private val minPairs = 2



    // Pinch detection variables
    private val pinchThreshold = 0.05f          // normalized distance
    private var pinchStartTime = 0L             // when pinch first detected
    private val pinchHoldDuration = 500L        // ms to hold
    private var pinchActive = false             // are we tracking a pinch?



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
            Log.d(TAG, "Palm is open")
            if (!openPalmHeld) {
                lastOpenPalmStartTime = now
                openPalmHeld = true
            } else if (now - lastOpenPalmStartTime >= palmHoldDurationMillis) {
                triggerGesture(GestureType.OPEN_PALM_HOLD)
                openPalmHeld = false  // Reset so it doesn't re-trigger continuously
            }
        } else {
            openPalmHeld = false
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

        // === GESTURE 4: PINCH DETECTION ===
        // --- Pinch Detection ---
        val thumbTip = landmarks[4]
        val indexTip = landmarks[8]
        val pinchDist = distance(thumbTip, indexTip)
        val isPinching = pinchDist < pinchThreshold

        if (isPinching) {
            val now = SystemClock.uptimeMillis()
            if (!pinchActive) {
                pinchStartTime = now
                pinchActive = true
            } else if (now - pinchStartTime >= pinchHoldDuration) {
                triggerGesture(GestureType.PINCH)
                pinchActive = false
            }
        } else {
            pinchActive = false
        }

        // === GESTURE 5: CLOSE PALM HOLD (3sec) ===
        if (!isPalmOpen) {
            if (closePalmHeld) {
                if (now - lastOpenPalmStartTime >= palmHoldDurationMillis) {
                    triggerGesture(GestureType.CLOSE_PALM_HOLD)
                    closePalmHeld = false  // Reset so it doesn't re-trigger continuously
                }
            } else {
                lastOpenPalmStartTime = now
                closePalmHeld = true
            }
        } else {
            closePalmHeld = false
        }



        // === NO HAND OR UNKOWN GESTURE ===
        if (result.landmarks().isEmpty()) {
            Log.d(TAG, "No hand detected or unknown gesture")
            triggerGesture(GestureType.UNKNOWN)

            // Reset cooldown for all gestures
            lastGestureTimes.clear()
            lastOpenPalmStartTime = now
            pinchStartTime = now

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

        if (gesture == GestureType.UNKNOWN) {
            onGestureDetected(gesture)
        }
        if (canTrigger(gesture)) {
            lastGestureTimes[gesture] = SystemClock.uptimeMillis()
            onGestureDetected(gesture)
        }
    }
}
