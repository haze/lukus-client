package pw.haze.client.events

import pw.haze.event.Event

/**
 * |> Author: haze
 * |> Since: 4/12/16
 */
enum class Action {
    LEFT_CLICK, RIGHT_CLICK, MIDDLE_CLICK, KEYPRESS
}

class EventAction(val action: Action, val key: Int): Event() {
    constructor(action: Action) : this(action, -1)
}