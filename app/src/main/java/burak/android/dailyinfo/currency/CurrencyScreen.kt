package burak.android.dailyinfo.currency

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import burak.android.dailyinfo.data.FavouriteCurrencyEntity
import burak.android.dailyinfo.ui.theme.MyBlue
import burak.android.dailyinfo.ui.theme.MyDarkGray
import burak.android.dailyinfo.ui.theme.MyLightGray
import burak.android.dailyinfo.ui.theme.MyPink
import burak.android.dailyinfo.ui.theme.MyPurple
import burak.android.dailyinfo.ui.theme.MySecondPurple

@Composable
fun CurrencyScreen(viewModel: CurrencyViewModel = hiltViewModel()){

    val rates = viewModel.currencyState
    val favourites = viewModel.favourites
    val lastUpdate = rates?.time_last_update_utc ?: "Yükleniyor.."
    val currencyList = rates?.conversion_rates?.toList() ?: emptyList()

    LazyColumn(modifier = Modifier.fillMaxSize().background(brush = Brush.verticalGradient(colors = listOf(Color.LightGray, Color.Black))).padding(16.dp)) {
        item {
            if (favourites.isNotEmpty()){
                Box(modifier = Modifier.height(120.dp)){
                    LazyRow(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                        items(favourites){ favCurrency ->
                            FavouriteCurrencyCard(
                                currency = favCurrency.code,
                                rate = favCurrency.rate,
                                baseCurrency = favCurrency.base,
                                onDelete = {
                                    viewModel.toggleFavourite(
                                        code = favCurrency.code,
                                        base = favCurrency.base,
                                        rate = favCurrency.rate
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
        item {
            var expanded by remember { mutableStateOf(false) }

            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize().background(brush = Brush.verticalGradient(colors = listOf(MyDarkGray, MyLightGray)))){
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tüm Kurlar",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Icon(imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Toggle",
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        AllCurrenciesCard(
                            currencyList = currencyList,
                            onFavouriteToggle = { code ->
                                val rate = currencyList.firstOrNull{ it.first == code}?.second ?: 0.0
                                viewModel.toggleFavourite(code, viewModel.baseCurrency, rate)
                            },
                            favourites = favourites,
                            lastUpdate = lastUpdate,
                            isExpanded = expanded,
                            baseCurrency = viewModel.baseCurrency,
                            onBaseCurrencyChange = {
                                viewModel.updateBaseCurrency(it)
                                viewModel.getCurrencyRates(it)
                            }
                        )
                    }
                }

            }
        }

        item {
                CurrencyConverterCard(
                    currencyList = currencyList.map { it.first },
                    conversionRates = rates?.conversion_rates ?: emptyMap(),
                    fromCurrency = viewModel.fromCurrency,
                    toCurrency = viewModel.toCurrency,
                    amount = viewModel.amount,
                    onFromCurrencyChange = { viewModel.updateFromCurrency(it)},
                    onToCurrencyChange = { viewModel.updateToCurrency(it)},
                    onAmountChange = { viewModel.updateAmount(it)}
                )
        }
    }
}

@Composable
fun AllCurrenciesCard(
    currencyList: List<Pair<String, Double>>,
    onFavouriteToggle: (String) -> Unit,
    favourites: List<FavouriteCurrencyEntity>,
    lastUpdate: String,
    isExpanded: Boolean,
    baseCurrency: String,
    onBaseCurrencyChange: (String) -> Unit
) {
    val displayedList = if (isExpanded) currencyList else currencyList.take(5)

    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            DropdownMenuBox(
                label = "Baz Kur",
                selected = baseCurrency,
                items = currencyList.map { it.first },
                onSelected = { onBaseCurrencyChange(it)}
            )
            Text(
                text = lastUpdate,
                fontSize = 12.sp,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(4.dp))

        Column {
            displayedList.forEach { (currency, value) ->
                if (currency != baseCurrency){
                    val isFavourite = favourites.any { it.code == currency && it.base == baseCurrency }
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "1 $baseCurrency : ${"%.2f".format(value)} $currency",
                            color = Color.White
                        )
                        IconButton(onClick = { onFavouriteToggle(currency) }) {
                            Icon(
                                imageVector = if (isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favourite",
                                tint = Color.Red
                            )
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 1.dp,
                        color = Color.Gray
                    )
                }

            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun FavouriteCurrencyCard(
    currency: String,
    rate: Double,
    baseCurrency: String,
    onDelete: () -> Unit
){
    val text = "1 $baseCurrency = ${String.format("%.2f", rate)} $currency"
    val isTooLong = text.length > 20
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog){
        AlertDialog(
            onDismissRequest = { showDialog = false},
            title = { Text("Favoriden kaldır") },
            text = { Text("Favoriden kaldırmak istediğinize emin misiniz?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDialog = false
                }) {
                    Text("Evet")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Vazgeç")
                }
            }
        )
    }

    Card(modifier = Modifier.padding(top = 16.dp, end = 12.dp).width(160.dp).aspectRatio(1.7f)
        .pointerInput(Unit){
            detectTapGestures(onLongPress = { showDialog = true })
        },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(brush = Brush.horizontalGradient(colors = listOf(MyPurple, MyPink))),
            contentAlignment = Alignment.Center){
            if (isTooLong) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text(
                        text = "1 $baseCurrency = ${String.format("%.2f", rate)}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = currency,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Text(
                    text = text,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("RememberReturnType")
@Composable
fun CurrencyConverterCard(
    currencyList: List<String>,
    conversionRates: Map<String, Double>,
    fromCurrency: String,
    toCurrency: String,
    amount: String,
    onFromCurrencyChange: (String) -> Unit,
    onToCurrencyChange: (String) -> Unit,
    onAmountChange: (String) -> Unit
){
    val result = remember(fromCurrency, toCurrency, amount){
        val input = amount.toDoubleOrNull() ?: 0.0
        val fromRate = conversionRates[fromCurrency] ?: 1.0
        val toRate = conversionRates[toCurrency] ?: 1.0
        val converted = input * (toRate / fromRate)
        "%.2f".format(converted)
    }

    Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 64.dp).height(IntrinsicSize.Min),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(brush = Brush.horizontalGradient(colors = listOf(MyBlue,MySecondPurple)))){
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    DropdownMenuBox(label = "Kaynak Kur", selected = fromCurrency, items = currencyList) {
                        onFromCurrencyChange(it)
                    }
                    DropdownMenuBox(label = "Hedef Kur", selected = toCurrency, items = currencyList) {
                        onToCurrencyChange(it)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { onAmountChange(it) },
                    label = { Text("Miktar") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Cyan,
                        focusedTextColor = Color.White,
                        focusedLabelColor = Color.Cyan
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "$amount $fromCurrency = $result $toCurrency",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }

    }
}

@Composable
fun DropdownMenuBox(
    label: String,
    selected: String,
    items: List<String>,
    onSelected: (String) -> Unit
){
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(text = label,
            color = Color.White,
            fontSize = 12.sp)
        Box {
            OutlinedButton(onClick = { expanded = true}) {
                Text(text = selected, color = Color.White)
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false}) {
            items.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onSelected(it)
                        expanded = false
                    }
                )
            }
        }
    }
}

