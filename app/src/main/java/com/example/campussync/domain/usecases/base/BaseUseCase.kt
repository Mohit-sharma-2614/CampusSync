package com.example.campussync.domain.usecases.base

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

abstract class BaseUseCase<T,Params>{
    protected abstract suspend fun buildUseCase(params: Params): T

    fun execute(
        params: Params,
        onSuccess: suspend (T) -> Unit = {},
        onError: suspend (e: Exception) -> Unit = {},
        onCancel: suspend (e: CancellationException) -> Unit = {},
        coroutineScope: CoroutineScope,
        executeContext: CoroutineContext = Dispatchers.IO,
        resultContext: CoroutineContext = Dispatchers.Main
    ) : Job? =
        try {
            coroutineScope.launch(executeContext) {
                try {
                    val result = buildUseCase(params)
                    withContext(resultContext) {
                        onSuccess(result)
                    }
                } catch (e: Exception) {
                    if (e is CancellationException) {
                        coroutineScope.launch(resultContext) {
                            onCancel(e)
                        }
                    } else {
                        withContext(resultContext) {
                            onError(e)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            if (e is CancellationException) {
                coroutineScope.launch(resultContext) {
                    onCancel(e)
                }
            }
            null
        }

    suspend fun execute(
        params: Params,
        onSuccess: suspend (T) -> Unit,
        onError: suspend (e: Exception) -> Unit,
        executeContext: CoroutineContext = Dispatchers.IO,
        resultContext: CoroutineContext = Dispatchers.Main
    ) {
        withContext(executeContext) {
            try {
                val result = buildUseCase(params)
                withContext(resultContext) {
                    onSuccess(result)
                }
            } catch (e: Exception) {
                withContext(resultContext) {
                    onError(e)
                }
            }
        }
    }

    suspend fun execute(params: Params): T = buildUseCase(params)
}

