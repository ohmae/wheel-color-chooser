/*
 * Copyright (c) 2019 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color

fun Int.clamp(min: Int, max: Int): Int = when {
    this < min -> min
    this > max -> max
    else -> this
}

fun Long.clamp(min: Long, max: Long): Long = when {
    this < min -> min
    this > max -> max
    else -> this
}

fun Float.clamp(min: Float, max: Float): Float = when {
    this < min -> min
    this > max -> max
    else -> this
}
