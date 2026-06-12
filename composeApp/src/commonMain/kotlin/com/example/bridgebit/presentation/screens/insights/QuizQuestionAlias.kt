package com.example.bridgebit.presentation.screens.insights

/**
 * Package-level type alias re-exporting [com.example.bridgebit.domain.model.QuizQuestion]
 * into the presentation package.
 *
 * This exists for backwards-compatibility with unit tests that live in the same
 * package (`com.example.bridgebit.presentation.screens.insights`) and reference
 * `QuizQuestion` without an explicit import, relying on Kotlin's same-package
 * class resolution.
 *
 * The canonical domain model lives at:
 * [com.example.bridgebit.domain.model.QuizQuestion]
 */
typealias QuizQuestion = com.example.bridgebit.domain.model.QuizQuestion
