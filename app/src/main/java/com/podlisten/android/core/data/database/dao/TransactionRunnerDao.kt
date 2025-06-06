package com.podlisten.android.core.data.database.dao

import androidx.room.Dao
import androidx.room.Ignore
import androidx.room.Transaction

@Dao
abstract class TransactionRunnerDao : TransactionRunner {
    @Transaction
    open suspend fun runInTransaction(tx: suspend () -> Unit) = tx()

    @Ignore
    override suspend fun invoke(tx: suspend () -> Unit) {
        runInTransaction(tx)
    }
}

interface TransactionRunner {
    suspend operator fun invoke(tx: suspend () -> Unit)
}