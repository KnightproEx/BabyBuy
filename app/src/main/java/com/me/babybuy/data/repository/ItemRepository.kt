package com.me.babybuy.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.me.babybuy.data.Resource
import com.me.babybuy.data.model.Item
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * A class for repository of [Item].
 */
class ItemRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: AuthRepository,
) : IItemRepository {
    private val userRef
        get() = db.collection("user").document(auth.currentUser!!.uid)
    private val storageRef get() = storage.reference

    override fun itemsFlow(): Flow<Resource<List<Item>>> = callbackFlow {
        val callback = userRef.collection("item").addSnapshotListener { snapshot, e ->
            if (e != null) {
                trySend(Resource.Failure("Something went wrong"))
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val result = snapshot.documents.map {
                    it.toObject(Item::class.java)!!.copy(id = it.id)
                }
                trySend(Resource.Success(result))
            }
        }
        awaitClose { callback.remove() }
    }

    override suspend fun createItem(item: Item, uri: Uri?): Resource<Unit> {
        return try {
            val createSnapshot = userRef.collection("item")
                .add(item.toMap()).await().get().await()
            val createResult =
                createSnapshot.toObject(Item::class.java)!!.copy(id = createSnapshot.id)
            val id = createResult.id

            if (uri == null) return Resource.Success(Unit)
            val uploadSnapshot = uploadImage(uri, "items/${id}")

            val path = uploadSnapshot.metadata?.path
            userRef.collection("item")
                .document(id)
                .set(createResult.copy(imagePath = path).toMap())
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Failure("Something went wrong")
        }
    }

    override suspend fun modifyItem(item: Item, uri: Uri?): Resource<Unit> {
        return try {
            val id = item.id
            userRef.collection("item")
                .document(id).set(item.toMap()).await()

            if (uri == null) return Resource.Success(Unit)
            val uploadSnapshot = uploadImage(uri, "items/${id}")

            val path = uploadSnapshot.metadata?.path
            userRef.collection("item")
                .document(id)
                .set(item.copy(imagePath = path).toMap())
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Failure("Something went wrong")
        }
    }

    override suspend fun removeItem(item: Item): Resource<Unit> {
        return try {
            userRef.collection("item")
                .document(item.id).delete().await()
            item.imagePath?.let {
                deleteImage(it)
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Failure("Something went wrong")
        }
    }

    suspend fun getImage(path: String): Uri? {
        return try {
            storageRef.child(path).downloadUrl.await()
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun uploadImage(uri: Uri, path: String): UploadTask.TaskSnapshot {
        try {
            storageRef.child(path).delete().await()
        } catch (_: Exception) {
        }
        return storageRef.child(path).putFile(uri).await()
    }

    private suspend fun deleteImage(path: String) {
        storageRef.child(path).delete().await()
    }
}