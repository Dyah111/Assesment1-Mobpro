package com.dyahayuning0134.assesment1

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dyahayuning0134.assesment1.navigation.Screen
import com.dyahayuning0134.assesment1.navigation.SetupNavGraph
import com.dyahayuning0134.assesment1.ui.theme.Assesment1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assesment1Theme {
                SetupNavGraph()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
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
                ),
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Screen.Profile.route)
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.AccountCircle,
                            contentDescription = stringResource(R.string.profile),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
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

@SuppressLint("UnrememberedMutableState", "StringFormatMatches")
@Composable
fun ScreenStockList(stockList: List<Stock>) {
    val context = LocalContext.current
    val quantities = rememberSaveable {
        stockList.associate { it.name to mutableStateOf(0) }
    }
    val riceQuantity = rememberSaveable { mutableStateOf("Medium") }

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
                    if (item.name == "Rice") {
                        ScreenContentRice(
                            stock = item,
                            quantityState = riceQuantity,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        ScreenContent(
                            stock = item,
                            quantityState = quantities[item.name] ?: mutableStateOf(0),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val summaryMessage = context.getString(
                        R.string.summary_template,
                        "Soaps", quantities["Soaps"]?.value ?: 0,
                        "Alum", quantities["Alum"]?.value ?: 0,
                        "Eggs", quantities["Eggs"]?.value ?: 0,
                        "Rice", riceQuantity.value // <-- Ini udah string bukan angka!
                    )
                    shareData(context, summaryMessage)
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.summarize))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenContent(stock: Stock, quantityState: MutableState<Int>, modifier: Modifier = Modifier) {
    var price by rememberSaveable { mutableIntStateOf(0) }
    var input by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedQuantity by rememberSaveable { mutableStateOf("Medium") }

    val quantityOptions = listOf(
        stringResource(id = R.string.banyak),
        stringResource(id = R.string.sedang),
        stringResource(id = R.string.sedikit)
    )

    Column(
        modifier = modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = input,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() }) {
                    input = newValue
                    price = newValue.toIntOrNull() ?: 0
                }
            },
            label = { Text(stringResource(R.string.price_label)) },
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
                    label = { Text(stringResource(R.string.quantity_label)) },
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
                    onClick = { if (quantityState.value > 0) quantityState.value-- },
                    modifier = Modifier.size(50.dp)
                ) {
                    Text("-")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = quantityState.value.toString(),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { quantityState.value++ },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenContentRice(stock: Stock, quantityState: MutableState<String>, modifier: Modifier = Modifier) {
    var price by rememberSaveable { mutableIntStateOf(0) }
    var input by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }

    val quantityOptions = listOf(
        stringResource(id = R.string.banyak), // Many
        stringResource(id = R.string.sedang), // Medium
        stringResource(id = R.string.sedikit) // Little
    )

    Column(
        modifier = modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = input,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() }) {
                    input = newValue
                    price = newValue.toIntOrNull() ?: 0
                }
            },
            label = { Text(stringResource(R.string.price_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = quantityState.value,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.quantity_label)) },
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
                            quantityState.value = option
                            expanded = false
                        }
                    )
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

private fun shareData(context: Context, message: String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, message)
    }
    if (shareIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(shareIntent)
    }
}


@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun MainScreensPreview() {
    Assesment1Theme {
        MainScreen(rememberNavController())
    }
}
