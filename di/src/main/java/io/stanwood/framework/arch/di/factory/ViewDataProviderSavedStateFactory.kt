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

package io.stanwood.framework.arch.di.factory

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import javax.inject.Inject

/**
 * Generic factory for DataProviders with SavedStateHandle.
 *
 * Makes use of a user-defined Factory which actually creates the ViewModel
 * and only takes care of adding a SavedStateHandle.
 *
 * When using this don't make your DataProvider injectable. Only its corresponding
 * factory will be injected, e.g.:
 *
 * ```kotlin
 *class DetailDataProviderImpl( // Do not use @Inject annotation here!
 *   private val handle: SavedStateHandle,
 *   private val githubApi: GithubApi
 *) : ViewDataProvider(), DetailDataProvider {
 *    fun loadData() {
 *        val id = handle["id"] ?: "default"
 *        viewModelScope.launch {
 *            val response = githubApi.getCommit(id)
 *            // Handle response
 *        }
 *    }
 * }
 *
 *class DetailDataProviderFactory @Inject constructor( // instead inject the factory
 *    private val githubApi: GithubApi
 *) : ViewDataProviderSavedStateFactory.Factory<DetailDataProviderImpl> {
 *    fun create(handle: SavedStateHandle) =
 *        DetailDataProviderImpl(handle, githubApi)
 *
 *    // optional
 *    fun getDefaultArgs(): Bundle? = Bundle().apply { putString("someKey", "abc") }
 * }
 *
 *@Module
 *object DetailFragmentSubModule {
 *
 *   @Provides
 *   @JvmStatic
 *   internal fun provideDetailDataProvider(
 *       fragment: DetailFragment,
 *       dataProviderFactory: ViewDataProviderSavedStateFactory<DetailDataProviderImpl, DetailDataProviderFactory>
 *   ): DetailDataProvider =
 *       ViewModelProvider(fragment, dataProviderFactory).get(DetailDataProviderImpl::class.java)
 * }
 * ```
 *
 * Heavily inspired by [this blog post](https://proandroiddev.com/saving-ui-state-with-viewmodel-savedstate-and-dagger-f77bcaeb8b08).
 */
class ViewDataProviderSavedStateFactory<V : ViewModel, F : ViewDataProviderSavedStateFactory.Factory<V>> @Inject constructor(
    private val factory: F,
    owner: Fragment
) : AbstractSavedStateViewModelFactory(owner, factory.getDefaultArgs()) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T =
        factory.create(handle) as T

    abstract class Factory<T : ViewModel> {
        abstract fun create(handle: SavedStateHandle): T
        open fun getDefaultArgs(): Bundle? = null
    }
}
