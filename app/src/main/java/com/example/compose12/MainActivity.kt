package com.example.compose12

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose12.ui.theme.Compose12Theme
import kotlinx.serialization.*
import kotlinx.serialization.json.*

// Данные о товарах
@Serializable
data class BakeryItem(val name: String, val price: Double, val imageRes: Int)

// Сериализация и десериализация корзины
@Serializable
data class Cart(val items: Map<String, Int>)

fun Map<BakeryItem, Int>.toJson(): String {
    val items = this.mapKeys { it.key.name }
    val cart = Cart(items)
    return Json.encodeToString(cart)
}

fun String.toBakeryItemMap(allItems: List<BakeryItem>): Map<BakeryItem, Int> {
    val cart = Json.decodeFromString<Cart>(this)
    return cart.items.mapKeys { name ->
        allItems.find { it.name == name.key } ?: BakeryItem(name.key, 0.0, 0)
    }
}

// ViewModel для управления корзиной
class BakeryViewModel : ViewModel() {
    var cart by mutableStateOf<Map<BakeryItem, Int>>(emptyMap())
        private set

    var totalPrice by mutableStateOf(0.0)
        private set

    // Добавление товара в корзину
    fun addToCart(item: BakeryItem) {
        val updatedCart = cart.toMutableMap()
        updatedCart[item] = (updatedCart[item] ?: 0) + 1
        cart = updatedCart
        totalPrice += item.price
    }

    // Удаление товара из корзины
    fun removeFromCart(item: BakeryItem) {
        val updatedCart = cart.toMutableMap()
        updatedCart[item]?.let {
            if (it > 1) {
                updatedCart[item] = it - 1
                totalPrice -= item.price
            } else {
                updatedCart.remove(item)
                totalPrice -= item.price
            }
        }
        cart = updatedCart
    }

    // Сброс корзины
    fun resetCart() {
        cart = emptyMap()
        totalPrice = 0.0
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Compose12Theme {
                BakeryApp()
            }
        }
    }
}

@Composable
fun BakeryApp() {
    val navController = rememberNavController()
    val bakeryViewModel: BakeryViewModel = viewModels<BakeryViewModel>().value

    NavHost(navController = navController, startDestination = "start_page") {
        composable("start_page") {
            StartPage(navController = navController)
        }
        composable("bakery_order") {
            BakeryOrderScreen(navController = navController, bakeryViewModel = bakeryViewModel)
        }
        composable("order_summary") {
            val cart = bakeryViewModel.cart
            val totalPrice = bakeryViewModel.totalPrice
            OrderSummaryScreen(navController = navController, cart = cart, totalPrice = totalPrice)
        }
        composable("payment") {
            PaymentScreen(navController = navController)
        }
        composable("about_us") {
            AboutUsScreen(navController = navController)  // Экран с информацией о нас
        }
    }
}

@Composable
fun StartPage(navController: NavController) {
    val backgroundImage = painterResource(id = R.drawable.start_page)

    Box(modifier = Modifier.fillMaxSize()) {
        // Фоновое изображение
        Image(
            painter = backgroundImage,
            contentDescription = "Start Page Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "welcome!",
                style = MaterialTheme.typography.headlineLarge.copy(color = Color.Yellow),
                modifier = Modifier.padding(bottom = 1.dp)
            )

            Spacer(modifier = Modifier.height(600.dp))

            Button(
                onClick = { navController.navigate("bakery_order") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("make an order")
            }

            Spacer(modifier = Modifier.height(100.dp))

            Button(
                onClick = { navController.navigate("about_us") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("about")
            }
        }
    }
}

@Composable
fun BakeryOrderScreen(navController: NavController, bakeryViewModel: BakeryViewModel) {
    val bakeryItems = listOf(
        BakeryItem("Багет", 40.0, R.drawable.baget),
        BakeryItem("Круассан", 50.0, R.drawable.kruassan),
        BakeryItem("Пирог с вишней", 150.0, R.drawable.vishnya_pirog),
        BakeryItem("Сырники", 80.0, R.drawable.syrniki),
        BakeryItem("Торт Наполеон", 350.0, R.drawable.napoleon_tort)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { navController.navigate("start_page") { popUpTo("start_page") } }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }

        Text(
            text = "Заказ выпечки",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(bakeryItems) { item ->
                BakeryItemRow(item = item, onAddToCart = {
                    bakeryViewModel.addToCart(item)
                })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Итого: ${"%.2f".format(bakeryViewModel.totalPrice)} руб.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate("order_summary")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Оформить заказ")
        }
    }
}

@Composable
fun BakeryItemRow(item: BakeryItem, onAddToCart: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val image: Painter = painterResource(id = item.imageRes)
        Image(
            painter = image,
            contentDescription = item.name,
            modifier = Modifier
                .size(40.dp)
                .padding(end = 16.dp)
        )

        Text(item.name)
        Spacer(modifier = Modifier.width(8.dp))

        Text("${item.price} руб.")
        Spacer(modifier = Modifier.width(8.dp))

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onAddToCart,
            modifier = Modifier.height(40.dp)
        ) {
            Text("Добавить")
        }
    }
}

@Composable
fun OrderSummaryScreen(
    navController: NavController,
    cart: Map<BakeryItem, Int>,
    totalPrice: Double
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { navController.navigate("start_page") { popUpTo("start_page") } }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }

        Text(
            text = "Подтверждение заказа",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(cart.keys.toList()) { item ->
                Text("${item.name} x${cart[item]}")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Итого: ${"%.2f".format(totalPrice)} руб.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("payment") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Перейти к оплате")
        }
    }
}

@Composable
fun PaymentScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Оплата еще не реализована")
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("start_page") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Вернуться на главную")
        }
    }
}

@Composable
fun AboutUsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("О нас")
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("start_page") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Назад")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Compose12Theme {
        BakeryApp()
    }
}
