package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.domain.model.CounterConstants

class ValidateCounterCountUseCase {
    operator fun invoke(count: Int): Boolean {
        return count in 0..CounterConstants.MAX_COUNT
    }
}
