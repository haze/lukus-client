package pw.haze.client.manager.mod

import pw.haze.client.manager.ListManager
import pw.haze.client.manager.action.ActionManager
import pw.haze.client.manager.mod.mods.Killaura

/**
 * |> Author: haze
 * |> Since: 4/12/16
 */
class ModManager: ListManager<Mod> {

    private object Holder { val INSTANCE = ModManager() }

    companion object {
        val instance: ModManager by lazy { Holder.INSTANCE }
    }

    constructor(){
        this.contents.add(Killaura())
    }

    override var contents: MutableList<Mod> = mutableListOf()
}