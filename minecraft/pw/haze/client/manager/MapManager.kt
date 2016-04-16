package pw.haze.client.manager

/**
 * |> Author: haze
 * |> Since: 4/12/16
 */
abstract class MapManager<K, V> {
    abstract var contents: MutableMap<K, V>
}