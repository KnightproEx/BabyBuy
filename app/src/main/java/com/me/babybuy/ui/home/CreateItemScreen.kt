package com.me.babybuy.ui.home

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.LatLng
import com.me.babybuy.R
import com.me.babybuy.data.Resource
import com.me.babybuy.data.model.ItemForm
import com.me.babybuy.ui.auth.AuthViewModel
import com.me.babybuy.ui.component.AppTextField
import com.me.babybuy.ui.theme.spacing

/**
 * A [Composable] function for create item page.
 */
@Composable
fun CreateItemScreen(
    authViewModel: AuthViewModel,
    itemViewModel: ItemViewModel,
    navController: NavHostController,
) {
    val createItemFlow = itemViewModel.createItemFlow.collectAsState()
    val form = itemViewModel.createItemForm
    val formValue by form.state.collectAsState()
    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            form.update(ItemForm::imageUri, uri)
        }
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        itemViewModel.resetCreateItemFlow()
    }

    createItemFlow.value?.let {
        loading = it is Resource.Loading
        if (it is Resource.Failure) {
            errorMessage = it.errorMessage
        } else if (it is Resource.Success) {
            navController.popBackStack()
            itemViewModel.resetCreateItemFlow()
        }
    }

    AppBar(
        authViewModel = authViewModel,
        navController = navController,
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, "")
            }
        }
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (imageRef, nameRef, descRef, priceRef, quantityRef, dividerRef, purchasedRef, mapRef, errorRef, clearRef, saveRef) = createRefs()
            val spacing = MaterialTheme.spacing

            if (formValue.imageUri.value == null) {
                Box(
                    modifier = Modifier
                        .constrainAs(imageRef) {
                            top.linkTo(parent.top, spacing.extraLarge)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                        .fillMaxWidth()
                        .padding(horizontal = MaterialTheme.spacing.large)
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
                        .constrainAs(imageRef) {
                            top.linkTo(parent.top, spacing.extraLarge)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                        .fillMaxWidth()
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

            AppTextField(
                modifier = Modifier.constrainAs(nameRef) {
                    top.linkTo(imageRef.bottom, spacing.large)
                    start.linkTo(parent.start, spacing.large)
                    end.linkTo(parent.end, spacing.large)
                    width = Dimension.fillToConstraints
                },
                value = formValue.name.value.orEmpty(),
                isError = formValue.name.isInvalid,
                errorMessage = "Cannot be empty",
                onValueChange = { form.update(ItemForm::name, it) },
                label = "Name",
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
            )

            AppTextField(
                modifier = Modifier.constrainAs(descRef) {
                    top.linkTo(nameRef.bottom, spacing.medium)
                    start.linkTo(parent.start, spacing.large)
                    end.linkTo(parent.end, spacing.large)
                    width = Dimension.fillToConstraints
                },
                value = formValue.description.value.orEmpty(),
                onValueChange = { form.update(ItemForm::description, it) },
                label = "Description",
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Default,
                ),
                multiLine = true
            )

            AppTextField(
                modifier = Modifier.constrainAs(priceRef) {
                    top.linkTo(descRef.bottom, spacing.medium)
                    start.linkTo(parent.start, spacing.large)
                    end.linkTo(parent.end, spacing.large)
                    width = Dimension.fillToConstraints
                },
                value = formValue.price.value.orEmpty(),
                isError = formValue.price.isInvalid,
                errorMessage = "Invalid format",
                onValueChange = { form.update(ItemForm::price, it) },
                label = "Price",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                trailingIcon = { Text("$") }
            )

            AppTextField(
                modifier = Modifier.constrainAs(quantityRef) {
                    top.linkTo(priceRef.bottom, spacing.medium)
                    start.linkTo(parent.start, spacing.large)
                    end.linkTo(parent.end, spacing.large)
                    width = Dimension.fillToConstraints
                },
                value = formValue.quantity.value.orEmpty(),
                isError = formValue.quantity.isInvalid,
                errorMessage = "Invalid format",
                onValueChange = { form.update(ItemForm::quantity, it) },
                label = "Quantity",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
            )

            Divider(
                modifier = Modifier.constrainAs(dividerRef) {
                    top.linkTo(quantityRef.bottom, spacing.large)
                    start.linkTo(parent.start, spacing.large)
                    end.linkTo(parent.end, spacing.large)
                    width = Dimension.fillToConstraints
                },
                color = MaterialTheme.colorScheme.outline
            )

            Row(
                modifier = Modifier.constrainAs(purchasedRef) {
                    top.linkTo(dividerRef.bottom, spacing.medium)
                    start.linkTo(parent.start, spacing.large)
                    end.linkTo(parent.end, spacing.large)
                    width = Dimension.fillToConstraints
                },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already purchased the item?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Switch(
                    checked = formValue.purchased.value ?: false,
                    onCheckedChange = { form.update(ItemForm::purchased, it) },
                )
            }

            Row(
                modifier = Modifier.constrainAs(mapRef) {
                    top.linkTo(purchasedRef.bottom, spacing.medium)
                    start.linkTo(parent.start, spacing.large)
                    end.linkTo(parent.end, spacing.large)
                    width = Dimension.fillToConstraints
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
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

            if (errorMessage != "") {
                Text(
                    modifier = Modifier.constrainAs(errorRef) {
                        bottom.linkTo(saveRef.top, spacing.small)
                        start.linkTo(parent.start, spacing.large)
                        end.linkTo(parent.end, spacing.large)
                        width = Dimension.fillToConstraints
                    },
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
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
                    }
                )
            }

            Button(
                modifier = Modifier.constrainAs(clearRef) {
                    bottom.linkTo(parent.bottom, spacing.medium)
                    end.linkTo(saveRef.start, spacing.medium)
                },
                onClick = {
                    itemViewModel.resetCreateItemFlow()
                }
            ) {
                Text("Clear")
            }

            Button(
                modifier = Modifier.constrainAs(saveRef) {
                    bottom.linkTo(parent.bottom, spacing.medium)
                    end.linkTo(parent.end, spacing.large)
                },
                enabled = !loading,
                onClick = {
                    if (!loading) itemViewModel.createItem()
                }
            ) {
                Text("Save")
            }
        }
    }
}
