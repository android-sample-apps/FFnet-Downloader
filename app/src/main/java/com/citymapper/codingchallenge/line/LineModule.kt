package com.citymapper.codingchallenge.line

import android.content.res.Resources
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
class LineModule {

    @ImmutableExecutorDecorator
    @Provides
    fun provideController(
        interactor: LineInteractor,
        executor: Executor
    ): LineController = LineControllerDecorator(
        executor,
        LineControllerImpl(interactor)
    )

    @Provides
    fun provideInteractor(
        presenter: LinePresenter,
        repository: LineRepository
    ): LineInteractor = LineInteractor(presenter, repository)

    @Provides
    fun provideRepository(retrofit: Retrofit): LineRepository = LineRepositoryImpl(
        retrofit.create(LineService::class.java)
    )

    @Provides
    fun providePresenter(view: LineView, resources: Resources): LinePresenter = LinePresenterImpl(
        view,
        resources
    )

    @MutableExecutorDecorator
    @Provides
    fun provideView(decorator: MutableDecorator<LineView>): LineView = decorator.asDecorated()

    @FeatureScope
    @Provides
    fun provideViewDecorator(executor: MainThreadExecutor): MutableDecorator<LineView> =
        LineViewDecorator(executor)
}
