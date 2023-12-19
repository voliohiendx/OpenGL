package com.volio.vn.common.utils

import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner


fun Fragment.isFragmentResumed(block: () -> Unit) {
    if (lifecycle.currentState == Lifecycle.State.RESUMED) {
        block.invoke()
    }
}

fun Fragment.setBackPressListener(viewBack: View? = null, onClickBack: () -> Unit) {
    viewBack?.setPreventDoubleClick {
        onClickBack()
    }
    activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, true) {
        onClickBack()
    }
}

fun Fragment.safeCall(onCall: () -> Unit) {
    lifecycle.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (event == Lifecycle.Event.ON_RESUME) {
                onCall.invoke()
                lifecycle.removeObserver(this)
            }
            if (event == Lifecycle.Event.ON_DESTROY) {
                lifecycle.removeObserver(this)
            }
        }
    })
}