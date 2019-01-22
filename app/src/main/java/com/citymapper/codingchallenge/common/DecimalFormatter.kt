package com.citymapper.codingchallenge.common

import java.math.BigDecimal
import java.text.DecimalFormat

class DecimalFormatter {
    fun formatCurrency(number: BigDecimal) : String {
        return DecimalFormat("#,###.00").format(number)
    }
}
