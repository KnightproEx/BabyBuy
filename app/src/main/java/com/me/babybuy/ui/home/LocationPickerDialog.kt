package com.me.babybuy.ui.home

import android.location.Address
import android.location.Geocoder
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.me.babybuy.ui.component.AppTextField
import com.me.babybuy.ui.theme.spacing

/**
 * A [Composable] function for custom location picker Composable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPickerDialog(
    initialLocation: LatLng,
    onDismiss: () -> Unit,
    onAddressChanged: (String, LatLng) -> Unit,
) {
    var search by rememberSaveable { mutableStateOf("") }
    var currentSelection by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val cameraPosition = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 15f)
    }
    val markerPosition = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 15f)
    }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var suggestionList by rememberSaveable { mutableStateOf(listOf<Address>()) }

    LaunchedEffect(Unit) {
        val geocoder = Geocoder(context)
        markerPosition.position = CameraPosition.fromLatLngZoom(initialLocation, 20f)
        geocoder.getFromLocation(
            initialLocation.latitude,
            initialLocation.longitude,
            5
        ) { addressList ->
            currentSelection = addressList[0].getAddressLine(0)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAddressChanged(currentSelection, markerPosition.position.target)
                    onDismiss()
                }
            ) {
                Text("Save")
            }
        },
        text = {
            ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                val (searchRef, mapRef, addressRef) = createRefs()
                val spacing = MaterialTheme.spacing

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {},
                    modifier = Modifier.constrainAs(searchRef) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                ) {
                    AppTextField(
                        modifier = Modifier.menuAnchor(),
                        label = "Search",
                        value = search,
                        onValueChange = { search = it },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    val geocoder = Geocoder(context)
                                    geocoder.getFromLocationName(search, 5) {
                                        suggestionList = it
                                    }
                                    expanded = true
                                }
                            ) {
                                Icon(Icons.Default.Search, null)
                            }
                        }
                    )
                    DropdownMenu(
                        modifier = Modifier.exposedDropdownSize(),
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        suggestionList.ifEmpty {
                            Text(
                                modifier = Modifier.padding(spacing.small),
                                text = "Not found",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        suggestionList.map {
                            DropdownMenuItem(
                                text = { Text(it.getAddressLine(0)) },
                                onClick = {
                                    val location = LatLng(it.latitude, it.longitude)
                                    val geocoder = Geocoder(context)
                                    cameraPosition.position =
                                        CameraPosition.fromLatLngZoom(location, 15f)
                                    markerPosition.position =
                                        CameraPosition.fromLatLngZoom(location, 15f)
                                    geocoder.getFromLocation(
                                        location.latitude,
                                        location.longitude,
                                        5
                                    ) { addressList ->
                                        currentSelection = addressList[0].getAddressLine(0)
                                    }
                                }
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier.constrainAs(mapRef) {
                        top.linkTo(searchRef.bottom, spacing.medium)
                        bottom.linkTo(addressRef.top, spacing.medium)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        height = Dimension.fillToConstraints
                        width = Dimension.fillToConstraints
                    }
                ) {
                    GoogleMap(
                        modifier = Modifier.matchParentSize(),
                        cameraPositionState = cameraPosition,
                        onMapClick = {
                            val geocoder = Geocoder(context)
                            markerPosition.position = CameraPosition.fromLatLngZoom(it, 20f)
                            geocoder.getFromLocation(
                                it.latitude,
                                it.longitude,
                                5
                            ) { addressList ->
                                currentSelection = addressList[0].getAddressLine(0)
                            }
                        }
                    ) {
                        Marker(state = MarkerState(position = markerPosition.position.target))
                    }
                }

                Text(
                    modifier = Modifier.constrainAs(addressRef) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    },
                    text = currentSelection,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )
}