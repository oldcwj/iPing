package com.wpapper.iping.ui.utils.exts

import android.app.Activity
import android.view.View
import com.gturedi.views.StatefulLayout
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <T> Observable<T>.withCommitting(activity: Activity) = compose {
    it
            .doOnNext {
                com.wpapper.iping.ui.utils.T.committing(activity)
            }
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterNext {
                com.wpapper.iping.ui.utils.T.dismissHub()
            }
}

fun <T> Observable<T>.with(statefulLayout: StatefulLayout, reloadCallback: (v: View) -> Unit) = compose {
    it
            .doOnSubscribe {
                statefulLayout.showLoading()
            }
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                statefulLayout.showError(reloadCallback)
            }
            .doAfterNext {
                statefulLayout.showContent()
            }
}

fun <T> Observable<T>.withLoading(activity: Activity) = compose {
    it
            .doOnSubscribe {
                com.wpapper.iping.ui.utils.T.loading(activity)
            }
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnTerminate {
                com.wpapper.iping.ui.utils.T.dismissHub()
            }
}



fun <T> Observable<T>.subscribeNext(onNext: (t: T) -> Unit) = subscribe(onNext, Throwable::printStackTrace)

fun <T> Observable<T>.subscribeOnUi(): Observable<T> = subscribeOn(AndroidSchedulers.mainThread())
fun <T> Observable<T>.subscribeOnIo(): Observable<T> = subscribeOn(Schedulers.io())
fun <T> Observable<T>.subscribeOnComputation(): Observable<T> = subscribeOn(Schedulers.computation())

fun <T> Observable<T>.ui(): Observable<T> = observeOn(AndroidSchedulers.mainThread())
fun <T> Observable<T>.io(): Observable<T> = observeOn(Schedulers.io())
fun <T> Observable<T>.computation(): Observable<T> = observeOn(Schedulers.computation())
