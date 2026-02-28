package com.nemogz.mantracounter.shared.util

actual fun platformLog(tag: String, message: String) {
    println("[$tag] $message")
}
