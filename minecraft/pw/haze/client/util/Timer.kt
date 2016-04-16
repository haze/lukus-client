package pw.haze.client.util

/**
 * |> Author: haze
 * |> Since: 4/12/16
 */
class Timer {
    var beginningMS = now()
    fun now(): Double = System.nanoTime() / 1E6
    fun elapsed(delay: Long): Boolean = now() >= beginningMS + delay
    fun reset() { beginningMS = now() }
}