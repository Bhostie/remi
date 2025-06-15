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
    private val palmHoldDurationMillis = 2000L // 2 seconds

    fun detectAsync(mpImage: MPImage) {
        handLandmarker.detectAsync(mpImage, SystemClock.uptimeMillis())
    }

    private fun onHandResult(result: HandLandmarkerResult, input: MPImage) {
        if (result.landmarks().isEmpty()) return

        val landmarks = result.landmarks()[0]
        val x = landmarks[0].x()

        swipeHistory.add(x)
        if (swipeHistory.size > maxHistorySize) swipeHistory.removeFirst()

        // Gesture 1: Swipe Detection
        val delta = swipeHistory.last - swipeHistory.first
        if (abs(delta) > swipeThreshold) {
            val gesture = if (delta > 0) GestureType.SWIPE_RIGHT else GestureType.SWIPE_LEFT
            onGestureDetected(gesture)
            swipeHistory.clear()
            return
        }

        // Gesture 2: Open Palm Detection
        val dist1 = distance(landmarks[4], landmarks[8])  // thumb to index
        val dist2 = distance(landmarks[8], landmarks[12]) // index to middle
        val isPalmOpen = dist1 > palmThreshold && dist2 > palmThreshold

        if (isPalmOpen) {
            val now = SystemClock.uptimeMillis()

            if (!palmHeld) {
                // Start tracking duration
                lastOpenPalmStartTime = now
                palmHeld = true
            } else {
                // Already tracking, check if enough time passed
                if (now - lastOpenPalmStartTime >= palmHoldDurationMillis) {
                    onGestureDetected(GestureType.OPEN_PALM)
                    palmHeld = false  // Reset so it doesn't keep triggering
                }
            }
        } else {
            palmHeld = false
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
}
