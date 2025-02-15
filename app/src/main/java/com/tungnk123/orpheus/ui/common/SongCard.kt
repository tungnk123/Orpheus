package com.tungnk123.orpheus.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.tungnk123.orpheus.ui.theme.OrpheusTheme

@Composable
fun SongCard(
    image: ImageRequest?,
    title: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageLabel: (@Composable () -> Unit)? = null,
    subtitle: (@Composable () -> Unit)? = null,
    options: (@Composable (expanded: Boolean, onDismissRequest: () -> Unit) -> Unit)? = null,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            image?.let {
                Box(modifier = Modifier.size(45.dp)) {
                    AsyncImage(
                        model = it,
                        contentDescription = null,
                        modifier = Modifier.clip(RoundedCornerShape(10.dp))
                    )
                    imageLabel?.let { label ->
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = 8.dp)
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 3.dp, vertical = 0.dp)
                        ) {
                            ProvideTextStyle(
                                MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.primary
                                )
                            ) { label() }
                        }
                    }
                }
                Spacer(modifier = Modifier.width(15.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                ProvideTextStyle(MaterialTheme.typography.bodyMedium) { title() }
                subtitle?.let {
                    ProvideTextStyle(MaterialTheme.typography.bodySmall) { it() }
                }
            }

            options?.let {
                var showOptionsMenu by remember { mutableStateOf(false) }
                IconButton(onClick = { showOptionsMenu = !showOptionsMenu }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = null)
                    it(showOptionsMenu) { showOptionsMenu = false }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGenericGrooveCard() {
    OrpheusTheme {
        SongCard(
            image = null,
            imageLabel = { Text("Label") },
            title = { Text("Song Title") },
            subtitle = { Text("Artist Name") },
            options = { expanded, onDismissRequest ->
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = onDismissRequest
                ) {
                    DropdownMenuItem(
                        text = { Text("Option 1") },
                        onClick = onDismissRequest
                    )
                    DropdownMenuItem(
                        text = { Text("Option 2") },
                        onClick = onDismissRequest
                    )
                }
            },
            onClick = {}
        )
    }
}
