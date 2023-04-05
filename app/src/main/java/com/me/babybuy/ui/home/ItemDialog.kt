package com.me.babybuy.ui.home

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.LatLng
import com.me.babybuy.R
import com.me.babybuy.data.Resource
import com.me.babybuy.data.model.Item
import com.me.babybuy.data.model.ItemForm
import com.me.babybuy.ui.component.AppTextField
import com.me.babybuy.ui.theme.spacing
import com.me.babybuy.util.formatCurrency

/**
 * A [Composable] function for viewing, modifying and deleting item dialog Composable.
 */
@Composable
fun ItemDialog(
    item: Item,
    itemViewModel: ItemViewModel,
    dismissCallback: () -> Unit,
) {
    val itemFlow = itemViewModel.itemFlow.collectAsState()
    val form = itemViewModel.itemForm(item)
    val formValue by form.state.collectAsState()
    var loading by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf("") }
    var edited by rememberSaveable { mutableStateOf(false) }
    var uploadImage = false
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            if (!edited) uploadImage = true
            if (!edited) edited = true
            form.update(ItemForm::imageUri, it)
        }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        itemViewModel.resetItemFlow()
        item.imagePath?.let {
            form.update(ItemForm::imageUri, itemViewModel.getImage(it))
        }
    }

    itemFlow.value?.let {
        loading = it is Resource.Loading
        if (it is Resource.Failure) {
            errorMessage = it.errorMessage
        } else if (it is Resource.Success) {
            dismissCallback()
            itemViewModel.resetItemFlow()
        }
    }

    AlertDialog(
        onDismissRequest = dismissCallback,
        title = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                    IconButton(
                        onClick = {
                            val message =
                                "Item: ${item.name}\nDescription: ${item.description ?: "N/A"}\nQuantity: ${item.quantity ?: "N/A"}\nPrice per unit: $${
                                    item.price?.toString()?.formatCurrency() ?: "N/A"
                                }"
                            val intent = Intent.createChooser(Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, message)
                                type = "text/plain"
                            }, null)
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(Icons.Default.Share, "")
                    }
                    IconButton(
                        enabled = !loading,
                        onClick = { showDeleteDialog = !showDeleteDialog }
                    ) {
                        Icon(Icons.Default.Delete, null)
                    }
                    if (showDeleteDialog) {
                        AlertDialog(
                            onDismissRequest = { showDeleteDialog = false },
                            title = { Text("Deletion of data") },
                            text = { Text("This action cannot be undone, continue?") },
                            dismissButton = {
                                Button(onClick = { showDeleteDialog = false }) {
                                    Text("Cancel")
                                }
                            },
                            confirmButton = {
                                Button(onClick = { itemViewModel.removeItem(item) }) {
                                    Text("Confirm")
                                }
                            }
                        )
                    }
                }
            }
        },
        icon = {
            if (formValue.imageUri.value == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = MaterialTheme.spacing.medium)
                        .clickable { launcher.launch("image/*") }
                        .clip(RoundedCornerShape(8))
                        .border(3.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = MaterialTheme.spacing.extraLarge),
                        text = "Select an image",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = MaterialTheme.spacing.medium)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .aspectRatio(1f),
                        contentScale = ContentScale.Crop,
                        model = formValue.imageUri.value,
                        fallback = painterResource(id = R.drawable.ic_app_logo),
                        contentDescription = null,
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                AppTextField(
                    modifier = Modifier.padding(bottom = MaterialTheme.spacing.small),
                    value = formValue.name.value.orEmpty(),
                    isError = formValue.name.isInvalid,
                    errorMessage = "Cannot be empty",
                    onValueChange = { form.update(ItemForm::name, it); if (!edited) edited = true },
                    label = "Name",
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                )

                AppTextField(
                    modifier = Modifier.padding(bottom = MaterialTheme.spacing.small),
                    value = formValue.description.value.orEmpty(),
                    isError = formValue.description.isInvalid,
                    onValueChange = {
                        form.update(ItemForm::description, it); if (!edited) edited = true
                    },
                    label = "Description",
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    )
                )

                AppTextField(
                    modifier = Modifier.padding(bottom = MaterialTheme.spacing.small),
                    value = formValue.price.value.orEmpty(),
                    isError = formValue.price.isInvalid,
                    errorMessage = "Invalid format",
                    onValueChange = {
                        form.update(ItemForm::price, it); if (!edited) edited = true
                    },
                    label = "Price",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    trailingIcon = { Text("$") }
                )

                AppTextField(
                    modifier = Modifier.padding(bottom = MaterialTheme.spacing.small),
                    value = formValue.quantity.value.orEmpty(),
                    isError = formValue.quantity.isInvalid,
                    errorMessage = "Invalid format",
                    onValueChange = {
                        form.update(ItemForm::quantity, it); if (!edited) edited = true
                    },
                    label = "Quantity",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    )
                )

                Divider(
                    modifier = Modifier.padding(vertical = MaterialTheme.spacing.medium),
                    color = MaterialTheme.colorScheme.outline
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(modifier = Modifier.weight(1f),
                        text = "Already purchased the item?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Switch(
                        checked = formValue.purchased.value ?: false,
                        onCheckedChange = {
                            form.update(ItemForm::purchased, it); if (!edited) edited = true
                        },
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier
                            .clickable {
                                if (formValue.locationName.value.isNullOrBlank()) return@clickable
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("http://maps.google.com/maps?daddr=${formValue.location.value?.latitude},${formValue.location.value?.longitude}")
                                )
                                context.startActivity(intent)
                            }
                            .weight(1f),
                        text = formValue.locationName.value ?: "No location",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    IconButton(onClick = { showDialog = !showDialog }) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            tint = MaterialTheme.colorScheme.error,
                            contentDescription = null
                        )
                    }
                }

                if (showDialog) {
                    LocationPickerDialog(
                        initialLocation = formValue.location.value ?: LatLng(51.5, 0.1),
                        onDismiss = {
                            if (!showDialog) return@LocationPickerDialog
                            showDialog = false
                        },
                        onAddressChanged = { locationName, location ->
                            form.update(ItemForm::locationName, locationName)
                            form.update(ItemForm::location, location)
                            if (!edited) edited = true
                        }
                    )
                }

                if (errorMessage != "") {
                    Text(errorMessage, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        dismissButton = {
            Button(
                onClick = dismissCallback
            ) {
                Text("Cancel")
            }
        },
        confirmButton = {
            when {
                (!edited) -> Button(
                    onClick = dismissCallback
                ) {
                    Text("Done")
                }
                else -> Button(
                    enabled = !loading,
                    onClick = {
                        if (loading) return@Button
                        itemViewModel.modifyItem(item, form, uploadImage)
                    }
                ) {
                    Text("Save")
                }
            }
        }
    )
}