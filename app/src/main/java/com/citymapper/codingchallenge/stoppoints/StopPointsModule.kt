package com.citymapper.codingchallenge.stoppoints

import com.citymapper.codingchallenge.common.FeatureScope
import com.citymapper.codingchallenge.common.MainThreadExecutor
import com.nicolasmouchel.executordecorator.ImmutableExecutorDecorator
import com.nicolasmouchel.executordecorator.MutableDecorator
import com.nicolasmouchel.executordecorator.MutableExecutorDecorator
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import java.util.concurrent.Executor

@Module
class StopPointsModule {

    @ImmutableExecutorDecorator
    @Provides
    fun provideController(
        interactor: StopPointsInteractor,
        executor: Executor
    ): StopPointsController = StopPointsControllerDecorator(
        executor,
        StopPointsControllerImpl(interactor)
    )

    @Provides
    fun provideInteractor(
        presenter: StopPointsPresenter,
        repository: StopPointsRepository
    ): StopPointsInteractor = StopPointsInteractorImpl(presenter, repository)

    @Provides
    fun provideRepository(retrofit: Retrofit): StopPointsRepository = StopPointsRepositoryImpl(
        retrofit.create(StopPointsService::class.java)
    )

    @Provides
    fun providePresenter(view: StopPointsView): StopPointsPresenter = StopPointsPresenterImpl(
        view
    )

    @MutableExecutorDecorator
    @Provides
    fun provideView(decorator: MutableDecorator<StopPointsView>): StopPointsView = decorator.asDecorated()

    @FeatureScope
    @Provides
    fun provideViewDecorator(executor: MainThreadExecutor): MutableDecorator<StopPointsView> =
        StopPointsViewDecorator(executor)
}
