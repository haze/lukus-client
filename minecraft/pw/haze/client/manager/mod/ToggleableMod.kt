package pw.haze.client.manager.mod

import org.lwjgl.input.Keyboard
import pw.haze.client.actionManager
import pw.haze.client.eventManager
import pw.haze.client.events.Action
import pw.haze.client.events.EventModuleStateChange
import pw.haze.client.fireEvent
import pw.haze.client.manager.action.ModuleToggleTask
import pw.haze.client.manager.mod.interfaces.Toggleable

/**
 * |> Author: haze
 * |> Since: 4/12/16
 */
abstract class ToggleableMod : Mod, Toggleable {

    constructor(name: String, description: String, keyStr: String, category: Category) : super (name, description){
        actionManager.contents.put(arrayOf(Action.KEYPRESS), arrayOf(ModuleToggleTask(this, Keyboard.getKeyIndex(keyStr))))
    }

    var state: Boolean = false

    fun setState(newState: Boolean, shouldFireEvent: Boolean){
        if(shouldFireEvent) {
            val event = EventModuleStateChange(state, newState)
            fireEvent(event)
            if(event.cancelled) return
        }
            this.state = !state
    }

    open fun onEnable() { eventManager.registerAll(this) }
    open fun onDisable() { eventManager.unregisterAll(this) }

    override fun toggle() {
        setState(!state, true)
        when(state){
            true -> { onEnable() }
            false -> { onDisable() }
        }
    }
}