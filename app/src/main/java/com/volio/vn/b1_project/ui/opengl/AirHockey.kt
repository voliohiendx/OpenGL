package com.volio.vn.b1_project.ui.opengl

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder

class AirHockey {

    val color = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)
    val colorLine = floatArrayOf(1.0f, 1.0f, 0f, 1.0f)

    val tableVerticesWithTriangles = floatArrayOf(
        // Triangle 1
        -0.5f, -0.5f, 1.0f, 1.0f, 1.0f,
        0.5f, 0.5f, 1.0f, 1.0f, 1.0f,
        -0.5f, 0.5f, 1.0f, 1.0f, 1.0f,
        // Triangle 2
        -0.5f, -0.5f, 1.0f, 1.0f, 1.0f,
        0.5f, -0.5f, 1.0f, 1.0f, 1.0f,
        0.5f, 0.5f, 1.0f, 1.0f, 1.0f,
        // Line 1
        -0.5f, 0f, 1.0f, 0f, 0f,
        0.5f, 0f, 1.0f, 0f, 0f,
        // Mallets
        0f, -0.25f, 1.0f, 0f, 0f,
        0f, 0.25f, 1.0f, 0f, 0f,
    )

    val vertexData by lazy {
        ByteBuffer.allocateDirect(tableVerticesWithTriangles.size * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
                put(tableVerticesWithTriangles)
                position(0)
            }
    }

    private val vertexShaderCode = """attribute vec4 a_Position;
                           attribute vec3 a_color;
                           varying mediump vec3 color;
                           void main()
                           {
                              gl_Position = a_Position;
                              color = a_color;
                           }
                           """

    private val fragmentShaderCode = """precision mediump float;
                            varying mediump vec3 color;
                            void main()
                            {
                               gl_FragColor = vec4(color,1.);
                            }
                            """

    private val mProgram: Int by lazy {
        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }

    fun draw() {
        GLES20.glUseProgram(mProgram)

        val postion = GLES20.glGetAttribLocation(mProgram, "a_Position")
        val color = GLES20.glGetAttribLocation(mProgram, "a_color")

        GLES20.glEnableVertexAttribArray(postion)
        vertexData.position(0)

        GLES20.glVertexAttribPointer(
            postion,
            2,
            GLES20.GL_FLOAT,
            false,
            POSITION_COMPONENT_COUNT * 4,
            vertexData
        )

        GLES20.glEnableVertexAttribArray(color)
        vertexData.position(2)

        GLES20.glVertexAttribPointer(
            color,
            3,
            GLES20.GL_FLOAT,
            false,
            POSITION_COMPONENT_COUNT * 4,
            vertexData
        )

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6)
        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2)
        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 2)

    }


    companion object {
        const val POSITION_COMPONENT_COUNT = 5
        const val BYTES_PER_FLOAT = 4
    }
}