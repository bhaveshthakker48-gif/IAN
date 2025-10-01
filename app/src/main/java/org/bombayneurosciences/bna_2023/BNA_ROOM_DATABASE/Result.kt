package org.impactindiafoundation.iifllemeddocket.LLE_MedDocket_ROOM_DATABASE

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}
