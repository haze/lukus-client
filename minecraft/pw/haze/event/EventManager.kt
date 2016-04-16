/*
 * MIT License
 *
 * Copyright (c) 2016 haze
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package pw.haze.event

import pw.haze.event.annotation.EventMethod
import pw.haze.event.annotation.EventPriority
import pw.haze.event.annotation.Priority
import pw.haze.event.util.FunctionData
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.declaredFunctions

/**
 * |> Author: haze
 * |> Since: 3/28/16
 */
class EventManager {
    companion object {

        private var inst: Optional<EventManager> = Optional.empty()

        @JvmStatic fun getInstance(): EventManager {
            if (!inst.isPresent)
                inst = Optional.of(EventManager())
            return inst.get()
        }
    }


    private var registeredEvents: MutableMap<KClass<out Event>, CopyOnWriteArrayList<FunctionData>> = Hashtable<KClass<out Event>, CopyOnWriteArrayList<FunctionData>>()

    fun KClass<*>.getFunctionsWithAnnotation(anno: KClass<out Annotation>): List<KFunction<*>> =
            this.declaredFunctions.filter { func -> func.isAnnotationPresent(anno) }

    fun KFunction<*>.isAnnotationPresent(anno: KClass<out Annotation>): Boolean = this.annotations.any { a -> a.annotationClass.equals(anno) }

    fun KFunction<*>.getAnnotation(anno: KClass<out Annotation>): Annotation? {
        if (isAnnotationPresent(anno)) {
            annotations@ for (an: Annotation in this.annotations)
                if (an.annotationClass.equals(anno))
                    return an
        }
        return null
    }

    fun getAllEventsInClass(clazz: KClass<*>): List<KClass<out Event>> {
        val events = arrayListOf<KClass<out Event>>()
        val functions = clazz.getFunctionsWithAnnotation(EventMethod::class)
        functions@ for (event: KFunction<*> in functions) {
            if (event.isAnnotationPresent(EventMethod::class)) {
                annotations@ for (anno: Annotation in event.annotations) {
                    if (anno is EventMethod) {
                        val value = anno.value
                        if (!events.contains(value))
                            events.add(value)
                        break@annotations
                    }
                }
            }
        }
        return events
    }


    fun unregisterAll(inst: Any) {
        unregister(inst, getAllEventsInClass(inst.javaClass.kotlin))
    }

    fun unregister(inst: Any, event: KClass<out Event>) {
        unregister(inst, listOf(event))
    }

    fun unregister(inst: Any, events: List<KClass<out Event>>) {
        val instClass = inst.javaClass.kotlin
        events@ for (event: KClass<out Event> in events) {
            var funcs: CopyOnWriteArrayList<FunctionData> = this.registeredEvents.getOrElse(event, { CopyOnWriteArrayList() })
            val functions = instClass.getFunctionsWithAnnotation(EventMethod::class)
            for (entry: MutableMap.MutableEntry<KClass<out Event>, CopyOnWriteArrayList<FunctionData>> in this.registeredEvents) {
                if (entry.key.equals(event)) {
                    for (func: KFunction<*> in functions) {
                        for (registeredFuncs: FunctionData in funcs) {
                            if (registeredFuncs.function.equals(func)) {
                                funcs.remove(registeredFuncs)
                            }
                        }
                    }
                }
            }

            if (this.registeredEvents.containsKey(event))
                this.registeredEvents.remove(event)
            this.registeredEvents.put(event, funcs)
        }
    }

    fun registerAll(inst: Any) {
        register(inst, getAllEventsInClass(inst.javaClass.kotlin))
    }

    fun register(inst: Any, events: List<KClass<out Event>>) {
        val instClass = inst.javaClass.kotlin
        events@ for (event: KClass<out Event> in events) {
            var funcs = this.registeredEvents.getOrElse(event, { CopyOnWriteArrayList() })
            val functions = instClass.getFunctionsWithAnnotation(EventMethod::class)
            functions@ for (func: KFunction<*> in functions) {
                if (func.isAnnotationPresent(EventMethod::class)) {
                    val anno = func.getAnnotation(EventMethod::class)
                    val eventClass = (anno as EventMethod).value
                    var priority = Priority.NORMAL
                    if (func.isAnnotationPresent(EventPriority::class))
                        priority = (func.getAnnotation(EventPriority::class) as EventPriority).value
                    if (eventClass.equals(event)) {
                        funcs.add(FunctionData(inst, priority, func, event))
                    }
                }
                funcs.sort ({ f1, f2 -> f2.priority.ordinal - f1.priority.ordinal })
                if (this.registeredEvents.containsKey(event))
                    this.registeredEvents.remove(event)
                this.registeredEvents.put(event, funcs)
            }
        }
    }

    fun register(inst: Any, event: KClass<out Event>) {
        register(inst, listOf(event))
    }


    fun fire(event: Event) {
        val eventClass = event.javaClass.kotlin
        if (this.registeredEvents.containsKey(eventClass) && this.registeredEvents[eventClass] != null) {
            functions@ for (func: FunctionData in this.registeredEvents[eventClass]!!) {
                try {
                    when (func.function.parameters.size) {
                        1 -> func.function.call(func.inst)
                        2 -> func.function.call(func.inst, event)
                        else -> throw IllegalArgumentException("Too many arguments! Either use the argument in the function or don't use it at all!")
                    }
                } catch(err: InternalError) {
                    err.printStackTrace()
                    print("Caught SERIOUS exception! Cannot find method to call? Was it registered? Try restarting...")
                } catch(err: IllegalArgumentException) {
                    err.printStackTrace()
                    print("Caught Illegal Argument Exception, check the method parameters and the event annotation-- make sure they match!")
                }
            }
        }
    }
}