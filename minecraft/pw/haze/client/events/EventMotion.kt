package pw.haze.client.events

import pw.haze.event.Event

/**
 * |> Author: haze
 * |> Since: 4/12/16
 */
enum class EnumMotion {
    PRE, POST
}

class EventMotion(var yaw: Float, var pitch: Float, val enum: EnumMotion): Event() { }