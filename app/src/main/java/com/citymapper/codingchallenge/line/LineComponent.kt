package com.citymapper.codingchallenge.line

import com.citymapper.codingchallenge.common.FeatureScope
import dagger.Subcomponent

@FeatureScope
@Subcomponent(modules = [LineModule::class])
interface LineComponent {
    fun inject(activity: LineActivity)
}
