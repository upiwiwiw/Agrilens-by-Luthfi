package com.example.capstone.model

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class ImageClassifierHelper(private val context: Context) {

    private lateinit var interpreter: Interpreter
    private lateinit var labels: List<Pair<String, Int>> // Pair of disease name and treatment index
    private val treatmentMap: Map<Int, String> = mapOf(
        0 to """
        Apple Scab detected. 
        - Apply a fungicide containing captan or myclobutanil immediately to control the spread of the disease.
        - Prune and dispose of infected leaves and branches to prevent reinfection in the next season.
        - Ensure good air circulation around the tree by thinning out the canopy.
    """.trimIndent(),
        1 to """
        Bacterial Spot detected. 
        - Use a copper-based bactericide spray to control the bacteria. Apply every 7-10 days during wet conditions.
        - Avoid splashing water on the plant's leaves as it spreads bacteria.
        - Remove infected leaves and fruits and dispose of them far from the planting area.
    """.trimIndent(),
        2 to """
        Black Rot detected. 
        - Immediately remove and destroy infected fruits, leaves, and branches to reduce sources of infection.
        - Spray with a fungicide like thiophanate-methyl or captan during the growing season.
        - Sanitize pruning tools after each cut to prevent spreading the fungus.
    """.trimIndent(),
        3 to """
        Cedar Apple Rust detected. 
        - Apply a sulfur-based fungicide at the first sign of rust spots on the leaves. Repeat every 7-10 days.
        - Remove any nearby cedar or juniper trees that act as alternate hosts for the fungus.
        - Ensure good air circulation by pruning and spacing plants appropriately.
    """.trimIndent(),
        4 to """
        Cercospora Leaf Spot detected. 
        - Use a copper-based fungicide or a fungicide containing azoxystrobin. Apply every 10-14 days.
        - Remove infected leaves and improve soil drainage around the plant.
        - Avoid overhead irrigation to minimize leaf wetness.
    """.trimIndent(),
        5 to """
        Common Rust detected. 
        - Apply fungicides containing mancozeb or propiconazole early in the growing season to prevent infection.
        - Plant rust-resistant hybrids to minimize recurrence.
        - Remove and destroy infected plant material after the growing season.
    """.trimIndent(),
        6 to """
        Early Blight detected. 
        - Apply a protective fungicide such as chlorothalonil or copper-based products at the first sign of symptoms.
        - Rotate crops to avoid planting the same family of crops in the same area consecutively.
        - Improve drainage to reduce moisture buildup around the plants.
    """.trimIndent(),
        7 to """
        Esca (Black Measles) detected. 
        - Avoid overhead irrigation to keep leaves dry and prevent spore germination.
        - Use systemic fungicides like thiophanate-methyl for long-term protection.
        - Prune and remove infected wood and sanitize tools between uses.
    """.trimIndent(),
        8 to """
        Healthy Plant. 
        - No action needed. Maintain your regular care routine and monitor the plant's condition.
        - Ensure the plant receives adequate sunlight, water, and nutrients to keep it thriving.
        - Congratulations! Your plant is doing great. 😊
    """.trimIndent(),
        9 to """
        Late Blight detected. 
        - Promptly apply a late blight-specific fungicide containing chlorothalonil or mancozeb to stop the spread.
        - Remove infected plants entirely and dispose of them to avoid contaminating other plants.
        - Ensure proper spacing between plants to improve air circulation and reduce humidity.
    """.trimIndent(),
        10 to """
        Leaf Blight detected. 
        - Use a foliar fungicide containing mancozeb or copper oxychloride to control the blight.
        - Prune and dispose of dead or severely infected leaves to reduce sources of infection.
        - Improve air circulation and avoid wetting the foliage during irrigation.
    """.trimIndent(),
        11 to """
        Leaf Scorch detected. 
        - Ensure the plant is receiving consistent watering, especially during hot weather.
        - Apply a layer of organic mulch around the base of the plant to retain soil moisture.
        - Avoid over-fertilizing, which can exacerbate the symptoms of leaf scorch.
    """.trimIndent(),
        12 to """
        Northern Leaf Blight detected. 
        - Spray with a broad-spectrum fungicide such as azoxystrobin or mancozeb at the first sign of symptoms.
        - Remove and destroy infected plant debris after harvest to reduce the spread of spores.
        - Rotate crops to prevent the buildup of pathogens in the soil.
    """.trimIndent(),
        13 to """
        Powdery Mildew detected. 
        - Apply sulfur-based or potassium bicarbonate fungicides directly to affected leaves.
        - Increase sunlight exposure and reduce humidity by pruning and spacing plants.
        - Avoid excessive use of nitrogen fertilizers, which can make plants more susceptible to mildew.
    """.trimIndent(),
        14 to """
        Septoria Leaf Spot detected. 
        - Use fungicides containing chlorothalonil or mancozeb to treat the affected plants.
        - Remove infected leaves and avoid overhead irrigation to reduce the spread of spores.
        - Apply mulch to prevent soil splash-up, which spreads the pathogen to leaves.
    """.trimIndent(),
        15 to """
        Yellow Leaf Curl Virus detected. 
        - Control vector insects, such as whiteflies, with insecticides or sticky traps.
        - Apply a virus-resistant hybrid for long-term management of the issue.
        - Remove infected plants promptly to prevent spreading the virus to healthy plants.
    """.trimIndent()
    )

    init {
        setupImageClassifier()
        loadLabels()
    }

    interface ClassifierListener {
        fun onResult(result: ClassificationResult)
        fun onError(error: String)
    }

    private fun setupImageClassifier() {
        try {
            val model = loadModelFile()
            val options = Interpreter.Options()
            interpreter = Interpreter(model, options)
            Log.d("ImageClassifierHelper", "Model loaded successfully.")
        } catch (e: Exception) {
            throw RuntimeException("Error initializing TensorFlow Lite Interpreter: ${e.message}")
        }
    }

    private fun loadModelFile(): MappedByteBuffer {
        val assetFileDescriptor = context.assets.openFd("ml/plant-disease-model-with-info.tflite")
        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            assetFileDescriptor.startOffset,
            assetFileDescriptor.declaredLength
        )
    }

    private fun loadLabels() {
        try {
            context.assets.open("ml/labels.txt").use { inputStream ->
                val reader = InputStreamReader(inputStream)
                labels = reader.readLines().map { line ->
                    val parts = line.split(":").map { it.trim() }
                    Pair(parts[0], parts[1].toInt())
                }
                Log.d("ImageClassifierHelper", "Labels loaded successfully: ${labels.size} labels.")
            }
        } catch (e: Exception) {
            throw RuntimeException("Error loading labels: ${e.message}")
        }
    }

    fun classifyBitmap(bitmap: Bitmap, listener: ClassifierListener) {
        try {
            val resizedBitmap = preprocessImage(bitmap)
            val inputBuffer = convertBitmapToByteBuffer(resizedBitmap)

            val outputBuffer = Array(1) { FloatArray(labels.size) }
            interpreter.run(inputBuffer, outputBuffer)

            val result = processResults(outputBuffer[0])
            listener.onResult(result)
        } catch (e: Exception) {
            listener.onError("Error processing bitmap: ${e.message}")
        }
    }

    private fun preprocessImage(bitmap: Bitmap): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, IMG_SIZE, IMG_SIZE, true)
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * IMG_SIZE * IMG_SIZE * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        for (pixel in pixels) {
            val r = (pixel shr 16 and 0xFF)
            val g = (pixel shr 8 and 0xFF)
            val b = (pixel and 0xFF)

            byteBuffer.putFloat(r / 255.0f)
            byteBuffer.putFloat(g / 255.0f)
            byteBuffer.putFloat(b / 255.0f)
        }

        return byteBuffer
    }

    private fun processResults(outputs: FloatArray): ClassificationResult {
        val maxIndex = outputs.indices.maxByOrNull { outputs[it] } ?: -1
        val confidence = outputs[maxIndex]
        val (diagnosis, treatmentIndex) = if (maxIndex in labels.indices) labels[maxIndex] else Pair("Unknown", -1)
        val treatment = treatmentMap[treatmentIndex] ?: "No treatment information available."

        val title = if (diagnosis == "Healthy") {
            "Your plant is healthy! 😊"
        } else {
            "Hmm, there's a little issue here!"
        }

        return ClassificationResult(title, treatment, diagnosis, confidence)
    }

    companion object {
        private const val IMG_SIZE = 128
    }
}

data class ClassificationResult(
    val title: String,
    val treatment: String,
    val diagnosis: String,
    val confidence: Float
)
