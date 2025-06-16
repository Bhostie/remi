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
            .setMinHandDetectionConfidence(0.6f)
            .setMinTrackingConfidence(0.6f)
            .setMinHandPresenceConfidence(0.6f)
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
    private enum class PinchState { PINCHED, RELEASED }
    private var lastPinchState = PinchState.RELEASED
    private var transitionTimestamps = mutableListOf<Long>()
    private val minTransitions = 4           // PINCH→REL→PINCH→REL
    private val doublePinchCooldownMs = 1000L
    private var lastDoublePinchTime = 0L




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

        // === GESTURE 4: PINCH DETECTION ===
        val thumbTip = landmarks[4]
        val indexTip = landmarks[8]
        val pinchDist = distance(thumbTip, indexTip)

        val isPinched = pinchDist < pinchThreshold
        val currentState = if (isPinched) PinchState.PINCHED else PinchState.RELEASED
        if (currentState != lastPinchState) {
            transitionTimestamps.add(now)
            transitionTimestamps = transitionTimestamps
                .filter { now - it <= gestureWindowMs }.toMutableList()

            if (transitionTimestamps.size >= minTransitions) {
                if (now - lastDoublePinchTime >= doublePinchCooldownMs) {
                    onGestureDetected(GestureType.PINCH)
                    lastDoublePinchTime = now
                }
                transitionTimestamps.clear()
            }
            lastPinchState = currentState
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
