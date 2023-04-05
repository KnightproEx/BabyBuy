package com.me.babybuy.ui.home

import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.me.babybuy.R
import com.me.babybuy.data.model.Item
import com.me.babybuy.ui.theme.spacing

/**
 * A [Composable] function for custom card Composable.
 */
@Composable
fun ItemCard(item: Item, itemViewModel: ItemViewModel, onClick: () -> Unit) {
    var uri by rememberSaveable { mutableStateOf<Uri?>(null) }

    LaunchedEffect(itemViewModel.itemFlow.collectAsState().value) {
        item.imagePath?.let {
            uri = itemViewModel.getImage(it)
        }
    }

    ElevatedCard(
        modifier = Modifier
            .wrapContentSize()
            .clickable(onClick = onClick)
    ) {
        SubcomposeAsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8))
                .background(MaterialTheme.colorScheme.surface),
            contentScale = ContentScale.Crop,
            model = uri,
            loading = { CircularProgressIndicator(modifier = Modifier.padding(MaterialTheme.spacing.extraLarge)) },
            error = {
                Image(
                    // TODO: Change the default image
                    painter = painterResource(id = R.drawable.ic_app_logo),
                    contentDescription = item.name
                )
            },
            contentDescription = null
        )

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.small)
        ) {
            Text(
                modifier = Modifier
                    .wrapContentHeight(),
                text = item.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                modifier = Modifier
                    .wrapContentHeight(),
                text = if (item.description.isNullOrBlank()) "No description" else item.description,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}