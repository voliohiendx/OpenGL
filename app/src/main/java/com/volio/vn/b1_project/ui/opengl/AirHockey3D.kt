package com.volio.vn.b1_project.ui.opengl

import android.R.attr.height
import android.R.attr.width
import android.opengl.GLES20
import android.opengl.Matrix.multiplyMM
import android.opengl.Matrix.orthoM
import android.opengl.Matrix.perspectiveM
import android.opengl.Matrix.rotateM
import android.opengl.Matrix.setIdentityM
import android.opengl.Matrix.translateM
import com.volio.vn.common.utils.getScreenHeight
import com.volio.vn.common.utils.getScreenWidth
import java.nio.ByteBuffer
import java.nio.ByteOrder


class AirHockey3D {

    val color = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)
    val colorLine = floatArrayOf(1.0f, 1.0f, 0f, 1.0f)

    val tableVerticesWithTriangles = floatArrayOf(

            // Order of coordinates: X, Y, Z, W, R, G, B
// Triangle Fan
            0f, 0f, 0f, 1.5f, 1f, 1f, 1f,
            -0.5f, -0.8f, 0f, 1f, 0.7f, 0.7f, 0.7f,
            0.5f, -0.8f, 0f, 1f, 0.7f, 0.7f, 0.7f,
            0.5f, 0.8f, 0f, 2f, 0.7f, 0.7f, 0.7f,
            -0.5f, 0.8f, 0f, 2f, 0.7f, 0.7f, 0.7f,
            -0.5f, -0.8f, 0f, 1f, 0.7f, 0.7f, 0.7f,
// Line 1
            -0.5f, 0f, 0f, 1.5f, 1f, 0f, 0f,
            0.5f, 0f, 0f, 1.5f, 1f, 0f, 0f,

            // Mallets
            0f, -0.4f, 0f, 1.25f, 0f, 0f, 1f,
            0f, 0.4f, 0f, 1.75f, 1f, 0f, 0f
    )

    val vertexData by lazy {
        ByteBuffer.allocateDirect(tableVerticesWithTriangles.size * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
            put(tableVerticesWithTriangles)
            position(0)
        }
    }

    private val projectionMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)

    private val vertexShaderCode = """
                           uniform mat4 u_Matrix;
        
                           attribute vec4 a_Position;
                           attribute vec3 a_color;
                           varying mediump vec3 color;
                           void main()
                           {
                              gl_Position = u_Matrix * a_Position;
                              color = a_color;
                              
                              gl_PointSize = 10.0;
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

    fun setAspectRatio() {
        val aspectRatio: Float = if (getScreenWidth() > getScreenHeight()) getScreenWidth().toFloat() / getScreenHeight().toFloat() else getScreenHeight().toFloat() / getScreenWidth().toFloat()
        if (getScreenWidth() > getScreenHeight()) {
            // Landscape
            orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            // Portrait or square
            orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }
    }

    fun setModelMatrix() {

        perspectiveM(projectionMatrix,0, 45f, getScreenWidth().toFloat() / getScreenHeight().toFloat(), 1f, 10f)

        setIdentityM(modelMatrix, 0)

        translateM(modelMatrix, 0, 0f, 0f, -3.2f)
        rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f)

        val temp = FloatArray(16)
        multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0)
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.size)

    }

    fun draw() {
        GLES20.glUseProgram(mProgram)

        val postion = GLES20.glGetAttribLocation(mProgram, "a_Position")
        val color = GLES20.glGetAttribLocation(mProgram, "a_color")
        val uMatrixLocation = GLES20.glGetUniformLocation(mProgram, "u_Matrix")


        GLES20.glEnableVertexAttribArray(postion)
        vertexData.position(0)

        GLES20.glVertexAttribPointer(postion, 2, GLES20.GL_FLOAT, false, POSITION_COMPONENT_COUNT * 4, vertexData)

        GLES20.glEnableVertexAttribArray(color)
        vertexData.position(4)

        GLES20.glVertexAttribPointer(color, 3, GLES20.GL_FLOAT, false, POSITION_COMPONENT_COUNT * 4, vertexData)

        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6)
        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2)
        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 2)


    }


    companion object {
        const val POSITION_COMPONENT_COUNT = 7
        const val BYTES_PER_FLOAT = 4
    }
}