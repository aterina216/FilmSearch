package com.example.filmsearch.utils

import androidx.lifecycle.MutableLiveData
import java.util.concurrent.atomic.AtomicBoolean

class SingleLiveEvent<T> : MutableLiveData<T>() {
    private val mPending = AtomicBoolean(false)

    override fun setValue(t: T?) {
        if (mPending.compareAndSet(false, true)) {
            super.setValue(t)
        }
    }

    override fun postValue(t: T?) {
        if (mPending.compareAndSet(false, true)) {
            super.postValue(t)
        }
    }
}