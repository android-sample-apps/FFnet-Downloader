package com.citymapper.codingchallenge.stoppoints

import android.location.Location
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.only
import com.nhaarman.mockito_kotlin.then
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class StopPointsControllerImplTest {

    @Mock private lateinit var interactor: StopPointsInteractor
    @InjectMocks private lateinit var controller: StopPointsControllerImpl

    @Test
    fun `loadStopPoints without location should call interactor with default location`() {
        // Given

        // When
        controller.loadStopPoints(null)

        // Then
        then(interactor).should(only()).loadStopPoints(
            51.510, -0.09
        )
    }

    @Test
    fun `loadStopPoints with a location outside of london should call interactor with default location`() {
        // Given
        val location = mock<Location>()
        val london = Location("LONDON").apply {
            latitude = 51.510
            longitude = -0.09
        }
        given(location.distanceTo(london)).willReturn(90000f)

        // When
        controller.loadStopPoints(location)
        // Then
        then(interactor).should(only()).loadStopPoints(
            51.510, -0.09
        )
    }

    @Test
    fun `loadStopPoints with location should call interactor with location`() {
        // Given
        val location = Location("IN LONDON").apply {
            latitude = 51.410
            longitude = -0.07
        }
        val london = Location("LONDON").apply {
            latitude = 51.510
            longitude = -0.09
        }
        given(location.distanceTo(london)).willReturn(10000f)

        // When
        controller.loadStopPoints(location)
        // Then
        then(interactor).should(only()).loadStopPoints(
            51.410, -0.07
        )
    }

    @Test
    fun `loadArrivalTimes should call interactor`() {
        // When
        controller.loadArrivalTimes()

        // Then
        then(interactor).should(only()).loadArrivalTimes()
    }
}
