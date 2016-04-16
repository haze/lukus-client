package pw.haze.client.events

import pw.haze.event.Event

/**
 * |> Author: haze
 * |> Since: 4/12/16
 */
class EventModuleStateChange(val oldState: Boolean, val newState: Boolean): Event() {

}