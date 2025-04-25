package com.example.ai_ds_project

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

fun loadModel(context: android.content.Context, modelName: String): Interpreter {
    val assetFileDescriptor = context.assets.openFd(modelName)
    val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
    val fileChannel = inputStream.channel
    val startOffset = assetFileDescriptor.startOffset
    val declaredLength = assetFileDescriptor.declaredLength

    val mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    return Interpreter(mappedByteBuffer)
}

fun bitmapToInputBuffer(bitmap: Bitmap, size: Int = 32): ByteBuffer {
    val inputBuffer = ByteBuffer.allocateDirect(4 * size * size * 3)
    inputBuffer.order(ByteOrder.nativeOrder())

    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true)

    for (y in 0 until size) {
        for (x in 0 until size) {
            val pixel = scaledBitmap.getPixel(x, y)
            val r = ((pixel shr 16) and 0xFF) / 255.0f
            val g = ((pixel shr 8) and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f
            inputBuffer.putFloat(r)
            inputBuffer.putFloat(g)
            inputBuffer.putFloat(b)
        }
    }

    return inputBuffer
}

fun classifyImage(context: android.content.Context, bitmap: Bitmap): String {
    val interpreter = loadModel(context, "model.tflite")
    val inputBuffer = bitmapToInputBuffer(bitmap)

    val output = Array(1) { FloatArray(2) } // Change to FloatArray(1) if using sigmoid
    interpreter.run(inputBuffer, output)

    val labels = listOf("Real", "Fake")
    val predictedIndex = output[0].indices.maxByOrNull { output[0][it] } ?: 0
    val confidence = output[0][predictedIndex] * 100

    return "${labels[predictedIndex]} (Confidence: %.2f%%)".format(confidence)
}



