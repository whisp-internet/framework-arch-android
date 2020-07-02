package io.stanwood.mhwdb.feature.armors.dataprovider

import androidx.lifecycle.SavedStateHandle
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.BehaviorSubject
import io.stanwood.framework.arch.core.ViewDataProvider
import io.stanwood.framework.arch.core.delegate
import io.stanwood.framework.arch.core.rx.ResourceTransformer
import io.stanwood.mhwdb.interactor.GetArmorByIdInteractor
import io.stanwood.mhwdb.repository.mhw.Armor

class ArmorDetailsDataProviderImpl(
    savedStateHandle: SavedStateHandle,
    val interactor: GetArmorByIdInteractor
) : ViewDataProvider(),
    ArmorDetailsDataProvider {

    private var disposable: CompositeDisposable = CompositeDisposable()
    private val idSubject = BehaviorSubject.create<Long>()

    override var armorId: Long by savedStateHandle.delegate("armorId", 0L) { _, new -> idSubject.onNext(new) }
    override val data =
        idSubject.switchMap {
            interactor.getArmor(it).compose(ResourceTransformer.fromSingle<Armor>()).toObservable()
        }
            .replay(1)
            .autoConnect(1) { disposable += it }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}