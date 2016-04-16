package pw.haze.client.manager

/**
 * |> Author: haze
 * |> Since: 4/12/16
 */
abstract class ListManager<T> {
    abstract var contents: MutableList<T>
}