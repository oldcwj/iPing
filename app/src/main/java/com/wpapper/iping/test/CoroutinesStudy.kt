package com.wpapper.iping.test

import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.actor
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.system.measureTimeMillis

/**
 * Created by oldcwj@gmail.com on 2017/10/23.
 */
object CoroutinesStudy {
    @JvmStatic fun main(args: Array<String>) = runBlocking<Unit> {
        val job = launch(CommonPool) {
            println("in launch")
            delay(1000L)
            println("after delay")
            val aa = async(coroutineContext) { doOne()}
            aa.await()
        }
        println("main")
        job.join()
        print("main after join")

        val counter = counterActor() // create the actor
        massiveRun(CommonPool) {
            counter.send(IncCounter)
        }
        // send a message to get a counter value from an actor
        val response = CompletableDeferred<Int>()
        counter.send(GetCounter(response))
        println("Counter = ${response.await()}")
        counter.close() // shutdown the actor
    }
}

suspend fun doOne() : Int{
    delay(500L)
    return 2
}

suspend fun massiveRun(context: CoroutineContext, action: suspend () -> Unit) {
    val n = 1000 // number of coroutines to launch
    val k = 1000 // times an action is repeated by each coroutine
    val time = measureTimeMillis {
        val jobs = List(n) {
            launch(context) {
                repeat(k) { action() }
            }
        }
        jobs.forEach { it.join() }
    }
    println("Completed ${n * k} actions in $time ms")
}

// Message types for counterActor
sealed class CounterMsg
object IncCounter : CounterMsg() // one-way message to increment counter
class GetCounter(val response: CompletableDeferred<Int>) : CounterMsg() // a request with reply

fun counterActor() = actor<CounterMsg>(CommonPool) {
    var counter = 0 // actor state
    for (msg in channel) { // iterate over incoming messages
        when (msg) {
            is IncCounter -> counter++
            is GetCounter -> msg.response.complete(counter)
        }
    }
}