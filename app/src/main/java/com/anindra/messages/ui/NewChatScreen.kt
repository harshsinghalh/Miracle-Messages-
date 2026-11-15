package com.anindra.messages.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AddComment
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.anindra.messages.R
import com.anindra.messages.data.AddressIdentity
import com.anindra.messages.ui.design.MiracleActionSurface
import com.anindra.messages.ui.design.MiracleIconButton
import com.anindra.messages.ui.design.MiracleTokens
import com.anindra.messages.ui.design.miracleGlass

data class Contact(val name: String, val number: String, val workProfile: Boolean = false)

@Composable
fun NewChatScreen(
    vm: com.anindra.messages.AppViewModel,
    onBack: () -> Unit,
    onPick: (String, String?) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val context = androidx.compose.ui.platform.LocalContext.current
    val contacts by vm.contacts.collectAsState()
    val filtered = contacts.filter {
        it.name.contains(query, ignoreCase = true) ||
            it.number.contains(query) ||
            (isPhoneNumber(query.trim()) && AddressIdentity.matchesNumber(it.number, query.trim()))
    }
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }
    BackHandler(onBack = onBack)

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .padding(horizontal = MiracleTokens.PageHorizontal)
        ) {
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                MiracleIconButton(Icons.AutoMirrored.Rounded.ArrowBack, context.getString(R.string.icon_back), onBack)
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.new_chat_title), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                    Text("Find a person or enter a phone number", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(20.dp))
            OutlinedTextField(
                value = query, onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                singleLine = true,
                leadingIcon = { Icon(Icons.Rounded.Search, null) },
                placeholder = { Text(stringResource(R.string.new_chat_hint)) },
                shape = RoundedCornerShape(MiracleTokens.Radius),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.76f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.56f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                )
            )
            Spacer(Modifier.height(MiracleTokens.Gap))
            val canSendToQuery = isPhoneNumber(query.trim())
            AnimatedVisibility(query.isNotBlank(), enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
                Column {
                    MiracleActionSurface(
                        label = if (canSendToQuery) context.getString(R.string.new_chat_send_to, query.trim()) else "Enter a valid phone number",
                        icon = Icons.Rounded.AddComment,
                        enabled = canSendToQuery,
                        onClick = { onPick(query.trim(), null) }
                    )
                    Spacer(Modifier.height(MiracleTokens.SectionGap))
                }
            }
            Text(
                if (query.isBlank()) "CONTACTS" else "MATCHING CONTACTS",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.4.sp,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )
            LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(filtered, key = { it.number }) { contact ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                            .miracleGlass(RoundedCornerShape(20.dp), alpha = 0.34f, borderAlpha = 0.08f)
                            .clickable { onPick(contact.number, contact.name) }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        PersonAvatar(contact.number, size = 46.dp)
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(contact.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                if (contact.workProfile) {
                                    Spacer(Modifier.width(6.dp))
                                    WorkProfileBadge()
                                }
                            }
                            Spacer(Modifier.height(2.dp))
                            Text(formatPhoneNumber(contact.number), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
                if (filtered.isEmpty() && query.isNotBlank()) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(top = 36.dp), contentAlignment = Alignment.Center) {
                            Text("No matching contacts", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                item { Spacer(Modifier.height(32.dp)) }
            }
        }
    }
}