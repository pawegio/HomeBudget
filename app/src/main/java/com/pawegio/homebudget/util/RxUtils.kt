package com.pawegio.homebudget.util

import io.reactivex.Observable
import io.reactivex.functions.Consumer

@Suppress("unused")
val <T> T.unit
    get() = Unit

infix fun <T> Consumer<T>.put(value: T) = accept(value)

infix fun <T> MutableCollection<T>.replaceWith(c: Collection<T>) {
    clear()
    addAll(c)
}

data class Optional<out T>(val value: T?)

val <T> T?.optional get() = Optional(this)

val <T> Optional<T>.isNull get() = value === null

val <T> Optional<T>.isNotNull get() = value !== null

fun <T> Observable<T>.startWithNull(): Observable<Optional<T>> =
    map<Optional<T>> { it.optional }.startWith(null.optional)

fun <T> Observable<Optional<T>>.filterNotNull(): Observable<T> = filter { it.isNotNull }.map { it.value!! }
