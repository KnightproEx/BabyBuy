package com.me.babybuy.data.repository

import android.net.Uri
import com.me.babybuy.data.Resource
import com.me.babybuy.data.model.Item
import kotlinx.coroutines.flow.Flow

/**
 * An interface for [ItemRepository].
 */
interface IItemRepository {
    fun itemsFlow(): Flow<Resource<List<Item>>>
    suspend fun createItem(item: Item, uri: Uri?): Resource<Unit>
    suspend fun modifyItem(item: Item, uri: Uri?): Resource<Unit>
    suspend fun removeItem(item: Item): Resource<Unit>
}