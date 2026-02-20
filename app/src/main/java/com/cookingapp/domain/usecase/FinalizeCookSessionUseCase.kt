package com.example.cookingapp.domain.usecase

import com.example.cookingapp.data.repository.CookSessionRepository
import com.example.cookingapp.domain.model.CookSessionPayload
import javax.inject.Inject

class FinalizeCookSessionUseCase @Inject constructor(
    private val cookSessionRepo: CookSessionRepository
) {
    suspend operator fun invoke(payload: CookSessionPayload): Result<Long> =
        runCatching { cookSessionRepo.finalizeCookSession(payload) }
}