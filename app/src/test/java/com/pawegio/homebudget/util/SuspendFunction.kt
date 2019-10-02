package com.pawegio.homebudget.util

interface SuspendFunction<A> {
    suspend fun invokeSuspend(): A
}
