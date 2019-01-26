package com.citymapper.codingchallenge.stoppoints

import com.citymapper.codingchallenge.common.FeatureScope
import dagger.Subcomponent

@FeatureScope
@Subcomponent(modules = [StopPointsModule::class])
interface StopPointsComponent {
    fun inject(activity: StopPointsActivity)
}
