package com.test.testclean.life

import android.animation.Animator
import android.annotation.SuppressLint
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class AnimatorLifeObserver(var animator: Animator) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestory() {
        animator.cancel()
    }

    @SuppressLint("NewApi")
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onResume() {
        animator.resume()
    }

    @SuppressLint("NewApi")
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun onPause() {
        animator.pause()
    }
}