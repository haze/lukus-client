package pw.haze.client.manager.action

import pw.haze.client.eventManager
import pw.haze.client.events.Action
import pw.haze.client.events.EventAction
import pw.haze.client.manager.ListManager
import pw.haze.client.manager.MapManager
import pw.haze.event.annotation.EventMethod
import java.util.*

/**
 * |> Author: haze
 * |> Since: 4/12/16
 */
class ActionManager : MapManager<Array<Action>, Array<Task>> {


    private object Holder { val INSTANCE = ActionManager() }

    companion object {
        val instance: ActionManager by lazy { Holder.INSTANCE }
    }

    constructor(){
        eventManager.register(this, EventAction::class)
    }

    @EventMethod(EventAction::class) fun onAction(action: EventAction) {
        when(action.action){
            Action.KEYPRESS -> {
                getTasksForAction(Action.KEYPRESS).asSequence()
                    .filter { task -> task is ModuleToggleTask }
                        .map { task -> task as ModuleToggleTask }
                    .filter { keyTask -> keyTask.key == action.key }
                        .forEach { keyTask -> keyTask.invoke() }
            }
            else -> {
                getTasksForAction(action.action).asSequence()
                    .forEach { task -> task.invoke() }
            }
        }
    }

    fun getTasksForAction(action: Action): List<Task> {
        val tasks: ArrayList<Task> = arrayListOf()
        entries@ for(entry: Map.Entry<Array<Action>, Array<Task>> in this.contents.entries){
            actions@ for(actionEntr: Action in entry.key){
                if(actionEntr == action) tasks.addAll(entry.value)
            }
        }
        return tasks
    }

    override var contents: MutableMap<Array<Action>, Array<Task>> = mutableMapOf()

}