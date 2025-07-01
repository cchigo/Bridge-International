package com.bridge.androidtechnicaltest.common

/**
 * This is a generic interface for mapping between two model types.
 *
 * How to use:
 * @param T The target model type (e.g., domain model).
 * @param F The source model type (e.g., DTO, entity).
 */
interface BaseModelMapper<T, F> {
    fun from(data: F): T
    fun to(data: T): F
}