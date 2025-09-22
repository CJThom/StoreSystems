package com.gpcasiapac.storesystems

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform