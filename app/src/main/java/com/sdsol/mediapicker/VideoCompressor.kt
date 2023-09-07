package com.sdsol.mediapicker
/*

package com.sdsol.mediapicker

import android.media.MediaCodec
import android.media.MediaCodecList
import android.media.MediaFormat
import android.media.MediaMuxer
import java.io.File

class VideoCompressor {

    fun compressVideo(inputFilePath: String, outputFilePath: String) {
        val mediaMuxer = MediaMuxer(outputFilePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        val mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height)
        // Configure the mediaFormat parameters (e.g., width, height, bitrate, etc.)

        val codecName = chooseEncoderForMimeType(MediaFormat.MIMETYPE_VIDEO_AVC)
        val mediaCodec = MediaCodec.createByCodecName(codecName)
        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        mediaCodec.start()

        val inputBufferInfo = MediaCodec.BufferInfo()
        val outputBufferInfo = MediaCodec.BufferInfo()
        val inputBufferArray = mediaCodec.getInputBuffers()
        val outputBufferArray = mediaCodec.getOutputBuffers()

        val inputFile = File(inputFilePath)
        val dataSource = inputFile.inputStream()
        val buffer = ByteArray(bufferSize)

        var inputDone = false
        var outputDone = false

        while (!outputDone) {
            if (!inputDone) {
                val inputBufferIndex = mediaCodec.dequeueInputBuffer(timeoutUs)
                if (inputBufferIndex >= 0) {
                    val inputBuffer = inputBufferArray[inputBufferIndex]
                    val size = dataSource.read(buffer)
                    if (size == -1) {
                        mediaCodec.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                        inputDone = true
                    } else {
                        inputBuffer.clear()
                        inputBuffer.put(buffer, 0, size)
                        mediaCodec.queueInputBuffer(inputBufferIndex, 0, size, 0, 0)
                    }
                }
            }

            val outputBufferIndex = mediaCodec.dequeueOutputBuffer(outputBufferInfo, timeoutUs)
            if (outputBufferIndex >= 0) {
                val outputBuffer = outputBufferArray[outputBufferIndex]
                // You can manipulate the compressed video data here before writing to the muxer
                mediaMuxer.writeSampleData(outputBufferInfo, outputBuffer)
                mediaCodec.releaseOutputBuffer(outputBufferIndex, false)
                if (outputBufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                    outputDone = true
                }
            }
        }

        mediaCodec.stop()
        mediaCodec.release()
        mediaMuxer.stop()
        mediaMuxer.release()
        dataSource.close()
    }

    private fun chooseEncoderForMimeType(mimeType: String): String {
        val codecInfos =
            MediaCodecList(MediaCodecList.REGULAR_CODECS).codecInfos

        for (codecInfo in codecInfos) {
            if (!codecInfo.isEncoder) {
                continue
            }

            val types = codecInfo.supportedTypes
            for (type in types) {
                if (type.equals(mimeType, ignoreCase = true)) {
                    return codecInfo.name
                }
            }
        }

        return ""
    }

    companion object {
        private const val width = 640
        private const val height = 480
        private const val bufferSize = 1024 * 1024 // Adjust as needed
        private const val timeoutUs = 10000L
    }
}
*/
