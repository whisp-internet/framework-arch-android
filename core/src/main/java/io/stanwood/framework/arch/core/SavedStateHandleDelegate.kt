/*
 * Copyright (c) 2020 whisp Internet GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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

package io.stanwood.framework.arch.core

import androidx.lifecycle.SavedStateHandle
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/*
 * This file contains methods to delegate properties to a [SavedStateHandle].
 */

/**
 * Creates a property delegate for a non-nullable property backed by this [SavedStateHandle].
 *
 * Performance-wise slightly slower than a plain property (especially when it comes to lookup),
 * but lookup time is still constant and the difference is usually not noticeable.
 *
 * Performs slightly better without a listener, so only supply it if you really need it.
 *
 * @param key the key of the value stored in the SavedStateHandle bundle
 * @param default a default value to be supplied if the value in the bundle is [null].
 *  Note, that this will never be written into the bundle.
 * @param onChanged a listener which gets notified when the property value is changed.
 *  Note, that this doesn't listen on the bundle, only the property!
 */
fun <R, T> SavedStateHandle.delegate(
    key: String,
    default: T,
    onChanged: ((T, T) -> Unit)? = null
) =
    SavedStateHandleDelegate<R, T>(this, key, default, onChanged)

/**
 * Creates a property delegate for a nullable property backed by this [SavedStateHandle].
 *
 * Performance-wise slightly slower than a plain property (especially when it comes to lookup),
 * but lookup time is still constant and the difference is usually not noticeable.
 *
 * Performs slightly better without a listener, so only supply it if you really need it.
 *
 * @param key the key of the value stored in the SavedStateHandle bundle
 * @param onChanged a listener which gets notified when the property value is changed.
 *  Note, that this doesn't listen on the bundle, only the property!
 */
fun <R, T> SavedStateHandle.delegate(
    key: String,
    onChanged: ((T?, T?) -> Unit)? = null
) =
    NullableSavedStateHandleDelegate<R, T>(this, key, onChanged)

class SavedStateHandleDelegate<in R, T>(
    private val savedStateHandle: SavedStateHandle,
    private val key: String,
    private val default: T,
    private val onChanged: ((T, T) -> Unit)? = null
) : ReadWriteProperty<R, T> {
    override fun getValue(thisRef: R, property: KProperty<*>): T =
        savedStateHandle[key] ?: default

    override fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        if (onChanged != null) {
            // optimized for performance - we only read the old value if we there is a listener
            val oldValue = getValue(thisRef, property)
            savedStateHandle[key] = value
            onChanged.invoke(oldValue, value)
        } else {
            savedStateHandle[key] = value
        }
    }
}

class NullableSavedStateHandleDelegate<in R, T>(
    private val savedStateHandle: SavedStateHandle,
    private val key: String,
    private val onChanged: ((T?, T?) -> Unit)? = null
) : ReadWriteProperty<R, T?> {
    override fun getValue(thisRef: R, property: KProperty<*>): T? =
        savedStateHandle[key]

    override fun setValue(thisRef: R, property: KProperty<*>, value: T?) {
        if (onChanged != null) {
            // optimized for performance - we only read the old value if we there is a listener
            val oldValue = getValue(thisRef, property)
            savedStateHandle[key] = value
            onChanged.invoke(oldValue, value)
        } else {
            savedStateHandle[key] = value
        }
    }
}