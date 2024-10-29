package com.hopeinyang.zeroknowledge.data.dao

interface LogService {
    fun logNonFatalCrash(throwable: Throwable)

}