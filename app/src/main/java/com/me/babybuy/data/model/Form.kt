package com.me.babybuy.data.model

import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import io.github.boguszpawlowski.chassis.Field

/**
 * A data class for login form.
 */
data class LoginForm(
    val email: Field<LoginForm, String>,
    val password: Field<LoginForm, String>,
) {
    val isValid get() = email.isValid && password.isValid
}

/**
 * A data class for registration form.
 */
data class RegistrationForm(
    val name: Field<RegistrationForm, String>,
    val email: Field<RegistrationForm, String>,
    val password: Field<RegistrationForm, String>,
    val confirmPassword: Field<RegistrationForm, String>,
) {
    val isValid
        get() = name.isValid
                && email.isValid
                && password.isValid
                && confirmPassword.isValid
}

/**
 * A data class for item form.
 */
data class ItemForm(
    val name: Field<ItemForm, String>,
    val description: Field<ItemForm, String?>,
    val quantity: Field<ItemForm, String?>,
    val price: Field<ItemForm, String?>,
    val purchased: Field<ItemForm, Boolean>,
    val imageUri: Field<ItemForm, Uri?>,
    val locationName: Field<ItemForm, String?>,
    val location: Field<ItemForm, LatLng?>,
) {
    val isValid
        get() = name.isValid
                && quantity.isValid
                && price.isValid
}
