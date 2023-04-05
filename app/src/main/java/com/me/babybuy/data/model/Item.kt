package com.me.babybuy.data.model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * A data class for item.
 *
 * @property id the unique id of item
 * @property name the name of item
 * @property description the description of item
 * @property quantity the quantity of item
 * @property price the price of item
 * @property imagePath the image path of item
 * @property purchased the buying state of item
 * @property locationName the location name of item
 * @property lat the latitude of item's location
 * @property long the longitude of item's location
 */
@Parcelize
data class Item(
    val id: String = "",
    val name: String = "",
    val description: String? = null,
    val quantity: Int? = null,
    val price: Int? = null,
    val imagePath: String? = null,
    val purchased: Boolean = false,
    val locationName: String? = null,
    val lat: Double? = null,
    val long: Double? = null,
    @ServerTimestamp
    val createdAt: Date = Date(),
) : Parcelable {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "description" to description,
            "quantity" to quantity,
            "price" to price,
            "imagePath" to imagePath,
            "purchased" to purchased,
            "locationName" to locationName,
            "lat" to lat,
            "long" to long,
            "createdAt" to createdAt,
        ).filterValues { it != null }
    }
}