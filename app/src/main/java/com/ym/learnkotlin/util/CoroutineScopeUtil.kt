package com.ym.learnkotlin.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce

class CoroutineScopeUtil {
    fun CoroutineScope.produceNumbers()=produce<Int> {
        var x = 1
        while (true)send(x++)
    }

    fun CoroutineScope.square(numbers: ReceiveChannel<Int>): ReceiveChannel<Int> = produce {
        for (x in numbers) send(x * x)
    }




}