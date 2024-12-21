package com.noureddinetaleb.polyhome.utils

/**
 * Removes the first element matching the given predicate from the list.
 *
 * @param predicate The predicate to match the element to remove.
 * @return `true` if an element was removed, `false` otherwise.
 */
fun <T> MutableList<T>.removeFirstMatching(predicate: (T) -> Boolean): Boolean {
    val index = this.indexOfFirst(predicate)
    return if (index != -1) {
        this.removeAt(index)
        true
    } else {
        false
    }
}