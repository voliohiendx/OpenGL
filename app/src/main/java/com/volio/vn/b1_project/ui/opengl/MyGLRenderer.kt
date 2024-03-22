package com.volio.vn.b1_project.ui.opengl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer : GLSurfaceView.Renderer {

    private lateinit var mAirHockey: AirHockey
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        //   GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f)
        mAirHockey = AirHockey()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        mAirHockey.draw()
    }
}