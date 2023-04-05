package com.me.babybuy.ui.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.me.babybuy.data.Resource
import com.me.babybuy.data.model.Item
import com.me.babybuy.data.model.ItemForm
import com.me.babybuy.data.repository.ItemRepository
import com.me.babybuy.util.formatCurrency
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.boguszpawlowski.chassis.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

/**
 * A [ViewModel] class for [Item].
 * Contains functions to perform CRUD of [Item].
 * Uses [MutableStateFlow] for CRUD of [Item].
 * Collect [StateFlow] to read changes of [Item].
 */
@HiltViewModel
class ItemViewModel @Inject constructor(private val repository: ItemRepository) : ViewModel() {
    private val _itemsFlow = MutableStateFlow<Resource<List<Item>>?>(null)
    val itemsFlow: StateFlow<Resource<List<Item>>?> = _itemsFlow

    private val _createItemFlow = MutableStateFlow<Resource<Unit>?>(null)
    val createItemFlow: StateFlow<Resource<Unit>?> = _createItemFlow

    private val _itemFlow = MutableStateFlow<Resource<Unit>?>(null)
    val itemFlow: StateFlow<Resource<Unit>?> = _itemFlow

    /**
     * A [Chassis] of [ItemForm].
     */
    @Suppress("RemoveExplicitTypeArguments")
    val createItemForm = chassis<ItemForm> {
        ItemForm(
            name = field(initialValue = "") {
                validators(notEmpty())
                reduce { copy(name = it) }
            },
            description = field(initialValue = "") {
                validators()
                reduce { copy(description = it) }
            },
            quantity = field {
                validators(matches("\\d*".toRegex()))
                reduce { copy(quantity = it) }
            },
            price = field {
                validators(matches("^(\\d*|\\d+(.\\d{1,2})?)\$".toRegex()))
                reduce { copy(price = it) }
            },
            purchased = field(initialValue = false) {
                reduce { copy(purchased = it) }
            },
            imageUri = field {
                reduce { copy(imageUri = it) }
            },
            locationName = field {
                reduce { copy(locationName = it) }
            },
            location = field {
                reduce { copy(location = it) }
            },
        )
    }

    /**
     * A [Chassis] of [ItemForm].
     *
     * @param item an [Item] object.
     */
    @Suppress("RemoveExplicitTypeArguments")
    fun itemForm(item: Item) = chassis<ItemForm> {
        ItemForm(
            name = field(initialValue = item.name) {
                validators(notEmpty())
                reduce { copy(name = it) }
            },
            description = field(initialValue = item.description) {
                validators()
                reduce { copy(description = it) }
            },
            quantity = field(initialValue = item.quantity?.toString()) {
                validators(matches("\\d*".toRegex()))
                reduce { copy(quantity = it) }
            },
            price = field(initialValue = item.price?.toString()?.formatCurrency()) {
                validators(matches("^(\\d*|\\d+(.\\d{1,2})?)\$".toRegex()))
                reduce { copy(price = it) }
            },
            purchased = field(initialValue = item.purchased) {
                reduce { copy(purchased = it) }
            },
            imageUri = field {
                reduce { copy(imageUri = it) }
            },
            locationName = field(initialValue = item.locationName) {
                reduce { copy(locationName = it) }
            },
            location = field(initialValue = item.lat?.let {
                item.long?.let { it1 ->
                    LatLng(it, it1)
                }
            }) {
                reduce { copy(location = it) }
            },
        )
    }

    /**
     * A job function for retrieving a [List] of [Item].
     */
    fun getItems() = viewModelScope.launch {
        repository.itemsFlow().collect {
            _itemsFlow.value = it
        }
    }

    /**
     * A job function for creating an [Item].
     */
    fun createItem() = viewModelScope.launch {
        with(createItemForm()) {
            if (!isValid) return@launch
            val item = Item(
                name = name(),
                description = description().orEmpty(),
                quantity = quantity()?.toIntOrNull(),
                price = price()?.toBigDecimal()?.multiply(BigDecimal(100))?.toInt(),
                purchased = purchased(),
                locationName = locationName(),
                lat = location()?.latitude,
                long = location()?.longitude,
            )
            _createItemFlow.value = Resource.Loading
            val result = repository.createItem(item, imageUri())
            _createItemFlow.value = result
        }
    }

    /**
     * A job function for updating an [Item].
     *
     * @param item an [Item] object.
     * @param form a [Chassis] of [ItemForm]
     * @param uploadImage whether to enable image upload or not
     */
    fun modifyItem(item: Item, form: Chassis<ItemForm>, uploadImage: Boolean) =
        viewModelScope.launch {
            with(form()) {
                val modifiedItem = item.copy(
                    name = name(),
                    description = description().orEmpty(),
                    quantity = quantity()?.toIntOrNull(),
                    price = price()?.toBigDecimalOrNull()?.multiply(BigDecimal(100))?.toInt(),
                    purchased = purchased(),
                    locationName = locationName(),
                    lat = location()?.latitude,
                    long = location()?.longitude,
                )

                _itemFlow.value = Resource.Loading
                val result = repository.modifyItem(
                    modifiedItem,
                    if (uploadImage) imageUri() else null
                )
                _itemFlow.value = result
            }
        }

    /**
     * A job function for removing an [Item].
     *
     * @param item an [Item] object.
     */
    fun removeItem(item: Item) = viewModelScope.launch {
        _itemFlow.value = Resource.Loading
        val result = repository.removeItem(item)
        _itemFlow.value = result
    }

    /**
     * A suspend function for getting image Uri from Firebase Storage.
     *
     * @param path image path on Firebase Storage.
     * @return image [Uri]
     */
    suspend fun getImage(path: String): Uri? {
        return repository.getImage(path)
    }

    /**
     * A function for resetting the [createItemForm] and [createItemFlow].
     */
    fun resetCreateItemFlow() {
        createItemForm.reset()
        _createItemFlow.value = null
    }

    /**
     * A function for resetting the [itemFlow].
     */
    fun resetItemFlow() {
        _itemFlow.value = null
    }
}