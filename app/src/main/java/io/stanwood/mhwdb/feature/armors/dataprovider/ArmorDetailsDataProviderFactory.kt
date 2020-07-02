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

package io.stanwood.mhwdb.feature.armors.dataprovider

import androidx.lifecycle.SavedStateHandle
import io.stanwood.framework.arch.di.factory.ViewDataProviderSavedStateFactory
import io.stanwood.mhwdb.interactor.GetArmorByIdInteractor
import javax.inject.Inject

class ArmorDetailsDataProviderFactory @Inject constructor(
    val interactor: GetArmorByIdInteractor
) : ViewDataProviderSavedStateFactory.Factory<ArmorDetailsDataProviderImpl>() {
    override fun create(handle: SavedStateHandle) =
        ArmorDetailsDataProviderImpl(handle, interactor)
}