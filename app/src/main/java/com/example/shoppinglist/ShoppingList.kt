package com.example.shoppinglist

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class ShoppingItem(
    val id : Int = 0,
    val itemName : String = "",
    val itemQuantity : Int = 0,
    val isEditing : Boolean = false
)

@Preview
@Composable
fun ShoppingList() {
    val shoppingListItems : MutableList<ShoppingItem> = mutableListOf()
    val editId = remember { mutableIntStateOf(-1) }

    Column (
        modifier = Modifier.fillMaxSize(1f),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Button(onClick = {
            if (shoppingListItems.size == 0) {
                editId.intValue = 0
            } else {
                editId.intValue = shoppingListItems[shoppingListItems.size - 1].id + 1
            }
        }) {
            Text(text = "New Item")
        }
        CreateOrUpdateTask(shoppingListItems, editId)
    }
}

fun validIntString(inputValue: String) : String {
    return if (inputValue.toIntOrNull() == null) "" else inputValue
}

fun validInt(inputValue: String) : Int {
    return if (inputValue.toIntOrNull() == null) 0 else inputValue.toInt()
}

fun getIndexFromId(shoppingListItems: MutableList<ShoppingItem>, id: Int) : Int{
    shoppingListItems.forEachIndexed { index, value ->
        if (value.id == id) {
            return index
        }
    }
    return shoppingListItems.size
}

@Composable
fun CreateOrUpdateTask(
    shoppingListItems: MutableList<ShoppingItem>,
    editId : MutableIntState
) {
    if (editId.intValue == -2) {
        editId.intValue = -1
        DisplayItemLists(shoppingListItems, editId)
    }
    else
    if (editId.intValue != -1) {
        val itemIndex = getIndexFromId(shoppingListItems, editId.intValue)
        var itemName by remember { mutableStateOf("") }
        var itemQuantity by remember { mutableStateOf("") }
        if (itemIndex != shoppingListItems.size) {
            val shoppingItem = shoppingListItems[itemIndex]
            itemName = shoppingItem.itemName
            itemQuantity = shoppingItem.itemQuantity.toString()
        }
        AlertDialog (
            onDismissRequest = { editId.intValue = -1 },
            confirmButton = {
                Button(onClick = {
                    if (itemIndex == shoppingListItems.size) {
                        val shoppingItem = ShoppingItem(
                            id = shoppingListItems.size,
                            itemName = itemName,
                            itemQuantity = validInt(itemQuantity),
                            isEditing = false
                        )
                        if (shoppingItem.itemName != "" && shoppingItem.itemQuantity != 0) {
                            Log.i("Added", "item added ${shoppingItem.itemName}")
                            shoppingListItems.add(shoppingItem)
                        }
                    } else {
                        val shoppingItem = shoppingListItems[itemIndex].copy(
                            itemName = itemName,
                            itemQuantity = validInt(itemQuantity)
                        )
                        if (shoppingItem.itemName != "" && shoppingItem.itemQuantity != 0) {
                            Log.i("Update", "item updated ${shoppingItem.itemName}")
                            shoppingListItems[itemIndex] = shoppingItem
                        }
                    }
                    editId.intValue = -1
                }) {
                    Text(text = "Save")
                }
                Button(onClick = { editId.intValue = -1 }) {
                    Text(text = "Cancel")
                }
            },
            text = {
                Column {
                    TextField(value = itemName, onValueChange = {itemName = it})
                    TextField(
                        value = validIntString(itemQuantity),
                        onValueChange = { itemQuantity = it })
                }
            }
        )
    } else {
        DisplayItemLists(shoppingListItems, editId)
    }
}

@Composable
fun DisplayItemLists(
    shoppingListItems : MutableList<ShoppingItem>,
    editId: MutableIntState
) {
    Column (
        modifier = Modifier.fillMaxSize()
    ){
        for (shoppingItem in shoppingListItems) {
            DisplayItem(shoppingListItems = shoppingListItems, shoppingItem, editId)
        }
    }
}

@Composable
fun DisplayItem(
    shoppingListItems: MutableList<ShoppingItem>,
    shoppingItem : ShoppingItem,
    editId: MutableIntState
) {

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(
                border = BorderStroke(1.dp, Color.Cyan)
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Log.i("inside item", shoppingItem.itemName)
        Text(text = shoppingItem.itemName)
        Text(text = "Qty = ${shoppingItem.itemQuantity}")
        Row {
            IconButton( onClick = { editId.intValue = shoppingItem.id }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "To Edit Shopping Item ${shoppingItem.id}")
            }
            IconButton( onClick = {
                shoppingListItems -= shoppingItem
                editId.intValue = -2
            }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "To Delete Shopping Item ${shoppingItem.id}")
            }
        }
    }
}

