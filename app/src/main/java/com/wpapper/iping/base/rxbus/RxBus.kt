package com.wpapper.iping.base.rxbus

import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class RxBus {

    companion object {
        val defaultBus = RxBus()
    }

    private val bus: Subject<Any> = PublishSubject.create<Any>().toSerialized()

    fun post(event: Any) {
        bus.onNext(event)
    }

    fun <T> toObservable(eventClass: Class<T>): Observable<T> {
        return bus.ofType(eventClass)
    }

    fun <E, T> toAutoLifecycleObservable(provider: LifecycleProvider<E>, eventClass: Class<T>, untilEvent: E? = null): Observable<T> {
        val observable = bus.ofType(eventClass)
        if (untilEvent == null) {
            observable.bindToLifecycle(provider)
        } else {
            observable.bindUntilEvent(provider, untilEvent)
        }
        return observable
    }

}
