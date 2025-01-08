package com.hydrogen.hydrogenpaymentsdk

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

const val TAG = "LOG_TAG"
const val TAG_2 = "LOG_TAG_2"
fun main() {
//    runBlocking {
//        flow()
//            .filter {
//                it.contains("this", true)
//            }
//            .map {
//                "$it New"
//            }
//            .collect {
//                println(it)
//            }
//    }
//
//    runBlocking {
//        println()
//        println()
//
//        flowOfInt().flatMapConcat {
//            flow {
//                delay(2000)
//                emit(it * 10)
//            }
//        }.collect {
//            println("Counter: $it")
//        }
//    }
//
//    println()
//    println()

    runBlocking {
        collectLatestFlow()
            .onEach { pendingResult ->
                println("Pending $pendingResult")
            }.collectLatest { finalResult ->
                delay(1000)
                println("Final $finalResult")
                println()
            }
    }
}

val db = listOf(
    "Result for Python",
    "Result for CSS",
    "Result for Kotlin",
    "Result for HTML",
    "Result for Rust",
    "Result for Java",
    "Result for Dart",
    "Result for Php",
    "Result for Swift",
    "Result for C",
    "Result for C#",
    "Result for C++",
    "Result for JavaScript",
)

var index = 0

fun collectLatestFlow(): Flow<String> = flow {
    while (index < db.size) {
        emit(db[index])
        ++index
        delay(1000)
    }
}

var counter = 1
fun flow(): Flow<String> = flow {
    repeat(10) {
        val thisOrThat = if (counter % 2 != 0) "This" else "That"
        emit("$thisOrThat is $counter")
        delay(1000)
        ++counter
    }
}

fun flowOfInt(): Flow<Int> = flow {
    repeat(5) {
        emit(it)
        delay(1000)
    }
}