package mdcbot.utils

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

val executorService = Executors.newScheduledThreadPool(5)!!

fun execute(code: Runnable){
    executorService.execute(code)
}

fun executeWithRate(code: Runnable, time: Long){
    executeWithRateAndDelay(code, 0, time)
}

fun executeWithRateAndDelay(code: Runnable, delay: Long, time: Long){
    executorService.scheduleAtFixedRate(code, delay, time, TimeUnit.SECONDS)
}