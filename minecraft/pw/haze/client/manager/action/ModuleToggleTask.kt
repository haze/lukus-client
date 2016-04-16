package pw.haze.client.manager.action

import pw.haze.client.manager.mod.ToggleableMod

/**
 * |> Author: haze
 * |> Since: 4/12/16
 */
class ModuleToggleTask(val module: ToggleableMod, val key: Int): Task {
    override fun invoke() {
        this.module.toggle()
    }
}