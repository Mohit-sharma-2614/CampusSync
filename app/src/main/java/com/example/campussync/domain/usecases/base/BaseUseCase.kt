package com.example.campussync.domain.usecases.base

import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

abstract class BaseUseCase<T,Params>{
    protected abstract suspend fun buildUseCase(params: Params): T

    fun execute(
        params: Params,
        coroutineScope: CoroutineScope,
        onSuccess: suspend (T) -> Unit,
        onError: suspend (e: Exception) -> Unit,
        onCancel: suspend (e: CancellationException) -> Unit = {},
        executeContext: CoroutineContext = Dispatchers.IO,
        resultContext: CoroutineContext = Dispatchers.Main
    ) : Job? =
        try {
            coroutineScope.launch(executeContext) {
                try {
                    val result = buildUseCase(params)
                    Log.d("BaseUseCase", "execute: $result")
                    withContext(resultContext) {
                        onSuccess(result)
                    }
                } catch (e: Exception) {
                    if (e is CancellationException) {
                        Log.d("BaseUseCase", "execute: cancellation exception $e")
                        coroutineScope.launch(resultContext) {
                            onCancel(e)
                        }
                    } else {
                        Log.d("BaseUseCase", "execute: exception $e")
                        withContext(resultContext) {
                            onError(e)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            if (e is CancellationException) {
                Log.d("BaseUseCase", "execute: cancellation exception $e")
                coroutineScope.launch(resultContext) {
                    onCancel(e)
                }
            }
            Log.d("BaseUseCase", "execute: exception $e")
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
                Log.d("BaseUseCase", "execute: $result")
                withContext(resultContext) {
                    onSuccess(result)
                }
            } catch (e: Exception) {
                Log.d("BaseUseCase", "execute: exception ${e.message}")
                withContext(resultContext) {
                    onError(e)
                }
            }
        }
    }

    suspend fun execute(params: Params): T {
        Log.d("BaseUseCase", "execute: called")
        return buildUseCase(params)
    }
}

