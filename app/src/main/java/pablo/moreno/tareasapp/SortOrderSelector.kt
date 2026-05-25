package pablo.moreno.tareasapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortOrderSelector(
    sortOrder: SortOrder,
    onSortOrderChanged: (SortOrder) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }

    val etiquetaOrden = mapOf(
        SortOrder.DATE_DESC  to stringResource(R.string.sort_date_desc),
        SortOrder.DATE_ASC   to stringResource(R.string.sort_date_asc),
        SortOrder.TITLE_ASC  to stringResource(R.string.sort_title_asc),
        SortOrder.TITLE_DESC to stringResource(R.string.sort_title_desc)
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.sort_label),
            modifier = Modifier.padding(end = 4.dp)
        )
        ExposedDropdownMenuBox(
            expanded = expandido,
            onExpandedChange = { expandido = it },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = etiquetaOrden[sortOrder] ?: "",
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido)
                },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(
                expanded = expandido,
                onDismissRequest = { expandido = false }
            ) {
                SortOrder.entries.forEach { orden ->
                    DropdownMenuItem(
                        text = { Text(etiquetaOrden[orden] ?: "") },
                        onClick = {
                            onSortOrderChanged(orden)
                            expandido = false
                        }
                    )
                }
            }
        }
    }
}

