package com.example.campussync.data.repository

import javax.inject.Inject

class UserRepository @Inject constructor() {
    private var _userId: Long? = null
    private var _isTeacher: Boolean? = null

    fun setUserData(id: Long, isTeacher: Boolean) {
        _userId = id
        _isTeacher = isTeacher
    }

    fun getUserId(): Long? = _userId
    fun isTeacher(): Boolean? = _isTeacher
}
