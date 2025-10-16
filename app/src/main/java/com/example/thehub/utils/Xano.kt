package com.example.thehub.utils

object Xano {

    const val HOST = "https://x8ki-letl-twmt.n7.xano.io"
}

fun String?.asVaultUrl(): String? =
    this?.let { if (it.startsWith("http")) it else Xano.HOST + it }
