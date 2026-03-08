package com.example.campussync.domain.usercase.base

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class AbsUseCase <T, Params> {
//    fun execute(
//        params: Params,
//        scope: CoroutineScope,
//        onSuccess: suspend (T) -> Unit = { },
//        onError: suspend (e: Exception) -> Unit = { },
//        onCancel: suspend(e: CancellationException) -> Unit = { },
//        executeContext: CoroutineContext = Dispatchers.IO,
//        resultContext: CoroutineContext = Dispatchers.Main
//    ): Job? =
//        try {
//        scope.launch(executeContext) {
//            try {
//                val result = buildUseCase(params)
//            }
//        }
//    }

    protected abstract suspend fun execute(params: Params): T
    
    suspend operator fun invoke(params: Params): T {
        return execute(params)
    }
    
}