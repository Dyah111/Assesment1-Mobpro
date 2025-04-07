package com.dyahayuning0134.assesment1

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dyahayuning0134.assesment1.ui.theme.Assesment1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assesment1Theme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val data = listOf(
        Stock("Soaps", R.drawable.sabun),
        Stock("Alum", R.drawable.tawas),
        Stock("Eggs", R.drawable.telur),
        Stock("Rice", R.drawable.beras)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
        ) {
            ScreenStockList(data)
        }
    }
}

@Composable
fun ScreenStockList(stockList: List<Stock>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(stockList.chunked(2)) { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (item in rowItems) {
                    ScreenContent(stock = item, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenContent(stock: Stock, modifier: Modifier = Modifier) {
    var harga by remember { mutableIntStateOf(0) }
    var jumlah by remember { mutableIntStateOf(0) }
    var input by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedQuantity by remember { mutableStateOf("Sedang") }
    val quantityOptions = listOf("Banyak", "Sedang", "Sedikit")

    Column(
        modifier = modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = input,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() }) {
                    input = newValue
                    harga = newValue.toIntOrNull() ?: 0
                }
            },
            label = { Text("Harga") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (stock.name == "Rice") {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedQuantity,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Kuantitas") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    quantityOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedQuantity = option
                                expanded = false
                            }
                        )
                    }
                }
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { if (jumlah > 0) jumlah-- },
                    modifier = Modifier.size(50.dp)
                ) {
                    Text("-")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = jumlah.toString(),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { jumlah++ },
                    modifier = Modifier.size(50.dp)
                ) {
                    Text("+")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = painterResource(id = stock.imageResId),
            contentDescription = stock.name,
            modifier = Modifier.size(138.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stock.name,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun MainScreensPreview() {
    Assesment1Theme {
        MainScreen()
    }
}
