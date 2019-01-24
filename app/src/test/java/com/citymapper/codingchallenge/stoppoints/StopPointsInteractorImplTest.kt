package com.citymapper.codingchallenge.stoppoints

import com.nhaarman.mockito_kotlin.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class StopPointsInteractorImplTest {

    @Mock private lateinit var presenter: StopPointsPresenter
    @Mock private lateinit var repository: StopPointsRepository
    @InjectMocks private lateinit var interactor: StopPointsInteractorImpl

    @Test
    fun `loadStopPoints should retrieve list then call presenter`() {
        // Given
        val expected = listOf(mock<StopPoint>())
        given(repository.loadStopPoints(10.0, 10.0)).willReturn(
            expected
        )

        // When
        interactor.loadStopPoints(10.0, 10.0)

        // Then
        then(presenter).should(only()).presentStopPoints(expected)
    }

    @Test
    fun `loadArrivalTimes should call repository`() {
        // Given
        given(repository.loadStopPoints(10.0, 10.0)).willReturn(
            listOf(
                StopPoint(
                    id = "id",
                    name = "name"
                )
            )
        )
        // When
        interactor.loadStopPoints(10.0, 10.0)
        interactor.loadArrivalTimes()

        // Then
        then(repository).should().loadArrivalTimes(
            listOf(
                StopPoint(
                    id = "id",
                    name = "name"
                )
            ),
            listener = interactor
        )
    }

    @Test
    fun `loadArrivalTimes should not call repository`() {
        // Given
        given(repository.loadStopPoints(10.0, 10.0)).willReturn(
            emptyList()
        )
        // When
        interactor.loadStopPoints(10.0, 10.0)
        interactor.loadArrivalTimes()

        // Then
        then(repository).should(never()).loadArrivalTimes(
            any(),
            any()
        )
    }

    @Test
    fun `onArrivalTimesLoaded should not call presenter`() {
        // Given
        given(repository.loadStopPoints(10.0, 10.0)).willReturn(
            emptyList()
        )
        // When
        interactor.loadStopPoints(10.0, 10.0)
        interactor.onArrivalTimesLoaded("id", emptyList())

        // Then
        then(presenter).should(times(1)).presentStopPoints(any())
    }
}
