package com.yingtianwu.mobileshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yingtianwu.mobileshop.ui.theme.YingtianWuMobileShopTheme
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.snapshots.SnapshotStateList

// Main Android activity that launches the Jetpack Compose UI.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            YingtianWuMobileShopTheme {
                SneakerHubApp()
            }
        }
    }
}

// Represents one sneaker product displayed in the shop.
data class Product(
    val id: Int,
    val name: String,
    val brand: String,
    val category: String,
    val price: Double,
    val description: String,
    val imageRes: Int,
    val color: Color
)

// Represents one selected product in the shopping bag.
// Same product with different sizes is stored as separate cart items.
data class CartItem(
    val product: Product,
    val size: String,
    val quantity: Int
)

// Defines the app screens used for simple in-memory navigation.
sealed class Screen {
    object Welcome : Screen()
    object Home : Screen()
    data class Detail(val product: Product) : Screen()
    object Bag : Screen()
    object Payment : Screen()
}

// Category filters displayed on the home screen.
val categories = listOf(
    "All",
    "Running",
    "Basketball",
    "Lifestyle",
    "Kids"
)

// Brand filters displayed on the home screen.
val brands = listOf(
    "All",
    "Nike",
    "Adidas",
    "Converse"
)

// Products are stored in a simple list.
val products = listOf(
    Product(
        id = 1,
        name = "Air Jordan 1 Retro",
        brand = "Nike",
        category = "Basketball",
        price = 300.00,
        description = "A classic AirJordan basketball sneaker.",
        imageRes = R.drawable.air_jordan_1,
        color = Color(0xFFE53935)
    ),
    Product(
        id = 2,
        name = "Nike Kobe 6 Protro",
        brand = "Nike",
        category = "Basketball",
        price = 200.00,
        description = "A lightweight performance shoe designed for basketball.",
        imageRes = R.drawable.nike_kobe_6,
        color = Color(0xFFFFB300)
    ),
    Product(
        id = 3,
        name = "Nike LeBron 21",
        brand = "Nike",
        category = "Basketball",
        price = 180.00,
        description = "A LeBron James basketball sneaker.",
        imageRes = R.drawable.nike_lebron_21,
        color = Color(0xFF5E35B1)
    ),
    Product(
        id = 4,
        name = "Nike Air Max 90",
        brand = "Nike",
        category = "Running",
        price = 140.00,
        description = "A comfortable running sneaker with visible Air cushioning.",
        imageRes = R.drawable.nike_air_max_90,
        color = Color(0xFF1E88E5)
    ),
    Product(
        id = 5,
        name = "Nike Air Force 1",
        brand = "Nike",
        category = "Lifestyle",
        price = 120.00,
        description = "A clean everyday sneaker.",
        imageRes = R.drawable.nike_air_force_1,
        color = Color(0xFF212121)
    ),
    Product(
        id = 6,
        name = "Nike Dunk Low",
        brand = "Nike",
        category = "Basketball",
        price = 130.99,
        description = "A retro basketball style sneaker made for daily wear.",
        imageRes = R.drawable.nike_dunk_low,
        color = Color(0xFF00897B)
    ),
    Product(
        id = 7,
        name = "Adidas Ultraboost Light",
        brand = "Adidas",
        category = "Running",
        price = 170.99,
        description = "A soft and responsive running shoe for long-distance comfort.",
        imageRes = R.drawable.adidas_ultraboost_light,
        color = Color(0xFF3949AB)
    ),
    Product(
        id = 8,
        name = "Adidas Forum Low",
        brand = "Adidas",
        category = "Lifestyle",
        price = 110.99,
        description = "A low-top lifestyle sneaker inspired by classic court style.",
        imageRes = R.drawable.adidas_forum_low,
        color = Color(0xFF546E7A)
    ),
    Product(
        id = 9,
        name = "Adidas Hoops Classic",
        brand = "Adidas",
        category = "Basketball",
        price = 80.99,
        description = "A simple basketball-style shoe for casual daily outfits.",
        imageRes = R.drawable.adidas_hoops_classic,
        color = Color(0xFFFB8C00)
    ),
    Product(
        id = 10,
        name = "Converse Chuck 70",
        brand = "Converse",
        category = "Lifestyle",
        price = 95.99,
        description = "A premium canvas sneaker.",
        imageRes = R.drawable.converse_chuck_70,
        color = Color(0xFF6D4C41)
    ),
    Product(
        id = 11,
        name = "Converse Run Star Hike",
        brand = "Converse",
        category = "Lifestyle",
        price = 125.99,
        description = "A platform sneaker with a bold sole.",
        imageRes = R.drawable.converse_run_star_hike,
        color = Color(0xFF8E24AA)
    ),
    Product(
        id = 12,
        name = "Converse Weapon",
        brand = "Converse",
        category = "Basketball",
        price = 70.00,
        description = "A court-inspired sneaker.",
        imageRes = R.drawable.converse_weapon,
        color = Color(0xFF43A047)
    ),
    Product(
        id = 13,
        name = "Nike Kids Court Borough",
        brand = "Nike",
        category = "Kids",
        price = 65.99,
        description = "A durable kids sneaker designed for school and everyday play.",
        imageRes = R.drawable.nike_kids_court_borough,
        color = Color(0xFF00ACC1)
    ),
    Product(
        id = 14,
        name = "Adidas Kids Racer TR",
        brand = "Adidas",
        category = "Kids",
        price = 50.99,
        description = "A lightweight kids shoe made for comfort and active movement.",
        imageRes = R.drawable.adidas_kids_racer_tr,
        color = Color(0xFF7CB342)
    )
)

// Main composable that manages app navigation, filter/search state, and the shopping bag state shared across all screens.
@Composable
fun SneakerHubApp() {
    var currentScreen by remember {
        mutableStateOf<Screen>(Screen.Welcome)
    }

    var selectedCategory by remember {
        mutableStateOf("All")
    }

    var selectedBrand by remember {
        mutableStateOf("All")
    }

    var searchText by remember {
        mutableStateOf("")
    }

    // Shopping bag stored in memory using Compose state.
    val cartItems = remember {
        mutableStateListOf<CartItem>()
    }
    // Derived cart values automatically update when cartItems changes.
    val totalItems = cartItems.sumOf {
        it.quantity
    }

    val totalPrice = cartItems.sumOf {
        it.product.price * it.quantity
    }
    // Applies category, brand, and search filters to the product list.
    val filteredProducts = products.filter { product ->
        val categoryMatches = selectedCategory == "All" || product.category == selectedCategory
        val brandMatches = selectedBrand == "All" || product.brand == selectedBrand
        val searchMatches =
            product.name.contains(searchText, ignoreCase = true) ||
                    product.brand.contains(searchText, ignoreCase = true) ||
                    product.category.contains(searchText, ignoreCase = true)

        categoryMatches && brandMatches && searchMatches
    }

    // Keeps a stable reference to the current screen before rendering.
    val screen = currentScreen

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5F5)
    ) {
        when (screen) {
            is Screen.Welcome -> {
                WelcomeScreen(
                    onGetStarted = {
                        currentScreen = Screen.Home
                    }
                )
            }

            is Screen.Home -> {

                HomeScreen(
                    selectedCategory = selectedCategory,
                    selectedBrand = selectedBrand,
                    filteredProducts = filteredProducts,
                    totalItems = totalItems,
                    totalPrice = totalPrice,
                    searchText = searchText,
                    onSearchTextChange = {
                        searchText = it
                    },
                    onCategorySelected = {
                        selectedCategory = it
                    },
                    onBrandSelected = {
                        selectedBrand = it
                    },
                    onProductClick = {
                        currentScreen = Screen.Detail(it)
                    },
                    onAddToBag = {
                        currentScreen = Screen.Detail(it)
                    },
                    onBagClick = {
                        currentScreen = Screen.Bag
                    }
                )
            }

            is Screen.Detail -> {
                ProductDetailScreen(
                    product = screen.product,
                    onBackClick = {
                        currentScreen = Screen.Home
                    },
                    onAddToBag = { selectedSize ->
                        addProductToCart(cartItems, screen.product, selectedSize)
                    },
                    onBagClick = {
                        currentScreen = Screen.Bag
                    },
                    totalItems = totalItems
                )
            }

            is Screen.Bag -> {
                BagScreen(
                    cartItems = cartItems,
                    totalPrice = totalPrice,
                    onBackClick = {
                        currentScreen = Screen.Home
                    },
                    onIncrease = { product, size ->
                        addProductToCart(cartItems, product, size)
                    },
                    onDecrease = { product, size ->
                        decreaseProductQuantity(cartItems, product, size)
                    },
                    onRemove = { product, size ->
                        removeProductFromCart(cartItems, product, size)
                    },
                    onClearBag = {
                        cartItems.clear()
                    },
                    onBuyNow = {
                        currentScreen = Screen.Payment
                    }
                )
            }

            is Screen.Payment -> {
                PaymentScreen(
                    totalPrice = totalPrice,
                    onBackClick = {
                        currentScreen = Screen.Bag
                    },
                    onConfirmPayment = {
                        cartItems.clear()
                        currentScreen = Screen.Home
                    }
                )
            }
        }
    }
}

// Simple welcome screen shown when the app starts.
@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111111))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Yingtian Wu's",
                color = Color(0xFFFF6D00),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Sneaker Hub",
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                lineHeight = 46.sp
            )

            Spacer(modifier = Modifier.weight(1.4f))

            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6D00)
                )
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun addProductToCart(
    cartItems: SnapshotStateList<CartItem>,
    product: Product,
    size: String
) {
    val index = cartItems.indexOfFirst {
        it.product.id == product.id && it.size == size
    }

    if (index >= 0) {
        val oldItem = cartItems[index]
        cartItems[index] = oldItem.copy(
            quantity = oldItem.quantity + 1
        )
    } else {
        cartItems.add(
            CartItem(
                product = product,
                size = size,
                quantity = 1
            )
        )
    }
}

fun decreaseProductQuantity(
    cartItems: SnapshotStateList<CartItem>,
    product: Product,
    size: String
) {
    val index = cartItems.indexOfFirst {
        it.product.id == product.id && it.size == size
    }

    if (index >= 0) {
        val oldItem = cartItems[index]

        if (oldItem.quantity > 1) {
            cartItems[index] = oldItem.copy(
                quantity = oldItem.quantity - 1
            )
        } else {
            cartItems.removeAt(index)
        }
    }
}

fun removeProductFromCart(
    cartItems: SnapshotStateList<CartItem>,
    product: Product,
    size: String
) {
    cartItems.removeAll {
        it.product.id == product.id && it.size == size
    }
}

// Home screen that displays product search, filters, product cards, and the current shopping bag summary.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    selectedCategory: String,
    selectedBrand: String,
    filteredProducts: List<Product>,
    totalItems: Int,
    totalPrice: Double,
    onCategorySelected: (String) -> Unit,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onBrandSelected: (String) -> Unit,
    onProductClick: (Product) -> Unit,
    onAddToBag: (Product) -> Unit,
    onBagClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Yingtian Wu's",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "Sneaker Hub",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {

                    IconButton(
                        onClick = onBagClick
                    ) {
                        Box {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Shopping Bag"
                            )

                            if (totalItems > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .align(Alignment.TopEnd)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFF6D00)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = totalItems.toString(),
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomBagBar(
                totalItems = totalItems,
                totalPrice = totalPrice,
                onBagClick = onBagClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    focusManager.clearFocus()
                }
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                placeholder = {
                    Text(text = "Search sneakers")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(18.dp)
            )

            HeroBanner()

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "Categories",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            FilterRow(
                items = categories,
                selectedItem = selectedCategory,
                onItemSelected = onCategorySelected
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "Brands",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            FilterRow(
                items = brands,
                selectedItem = selectedBrand,
                onItemSelected = onBrandSelected
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Featured Sneakers",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${filteredProducts.size} items",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            ProductGrid(
                products = filteredProducts,
                onProductClick = onProductClick,
                onAddToBag = onAddToBag
            )
        }
    }
}

// Promotional banner shown near the top of the home screen.
@Composable
fun HeroBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(135.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF111111)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Limited Drop",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Exclusive sneakers and limited releases!",
                    color = Color(0xFFE0E0E0),
                    fontSize = 14.sp
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Shop now",
                    color = Color(0xFFFF6D00),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Reusable horizontal filter row for categories and brands.
@Composable
fun FilterRow(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { item ->
            FilterChip(
                selected = selectedItem == item,
                onClick = {
                    onItemSelected(item)
                },
                label = {
                    Text(
                        text = item
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color.Black,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

// Displays products in a simple two-column grid on the home screen.
@Composable
fun ProductGrid(
    products: List<Product>,
    onProductClick: (Product) -> Unit,
    onAddToBag: (Product) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        products.chunked(2).forEach { rowProducts ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowProducts.forEach { product ->
                    ProductCard(
                        product = product,
                        modifier = Modifier.weight(1f),
                        onProductClick = {
                            onProductClick(product)
                        },
                        onAddToBag = {
                            onAddToBag(product)
                        }
                    )
                }

                if (rowProducts.size == 1) {
                    Spacer(
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(90.dp)
        )
    }
}

// Reusable product card showing sneaker image, details, price, and add action.
@Composable
fun ProductCard(
    product: Product,
    modifier: Modifier = Modifier,
    onProductClick: () -> Unit,
    onAddToBag: () -> Unit
) {
    Card(
        modifier = modifier
            .height(275.dp)
            .clickable {
                onProductClick()
            },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            ShoeImageBox(
                product = product,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = product.brand,
                color = Color(0xFFFF6D00),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = product.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )

            Text(
                text = product.category,
                color = Color.Gray,
                fontSize = 12.sp
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$${formatPrice(product.price)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = onAddToBag,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Add",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

// Reusable image container that displays a product image with consistent styling.
@Composable
fun ShoeImageBox(
    product: Product,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = product.imageRes),
            contentDescription = product.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}

// Bottom summary bar showing current bag item count and total price.
@Composable
fun BottomBagBar(
    totalItems: Int,
    totalPrice: Double,
    onBagClick: () -> Unit
) {
    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "$totalItems item(s)",
                    color = Color.Gray,
                    fontSize = 13.sp
                )

                Text(
                    text = "Total: $${formatPrice(totalPrice)}",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onBagClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6D00)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Bag",
                    modifier = Modifier.size(18.dp)
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                Text(
                    text = "View Bag"
                )
            }
        }
    }
}

// Product detail screen where users choose a size and add the sneaker to the bag.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: Product,
    onBackClick: () -> Unit,
    onAddToBag: (String) -> Unit,
    onBagClick: () -> Unit,
    totalItems: Int
) {
    var selectedSize by remember {
        mutableStateOf("9")
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = product.brand,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onBagClick
                    ) {
                        Box {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Shopping Bag"
                            )

                            if (totalItems > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .align(Alignment.TopEnd)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFF6D00)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = totalItems.toString(),
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            ShoeImageBox(
                product = product,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = product.brand.uppercase(),
                color = Color(0xFFFF6D00),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = product.name,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating",
                    tint = Color(0xFFFFB300),
                    modifier = Modifier.size(20.dp)
                )

                Spacer(
                    modifier = Modifier.width(4.dp)
                )

                Text(
                    text = "4.8 / 5.0",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text = "Description",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = product.description,
                color = Color.DarkGray,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(
                text = "Select Size",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("6", "6.5", "7", "7.5", "8", "8.5", "9", "9.5", "10", "10.5", "11", "12", "13").forEach { size ->
                    Box(
                        modifier = Modifier
                            .size(width = 54.dp, height = 46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (selectedSize == size) Color.Black else Color.White
                            )
                            .clickable {
                                selectedSize = size
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = size,
                            color = if (selectedSize == size) Color.White else Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Price",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                    Text(
                        text = "$${formatPrice(product.price)}",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Button(
                    onClick = {
                        onAddToBag(selectedSize)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    ),
                    modifier = Modifier.height(54.dp)
                ) {
                    Text(
                        text = "Add to Bag",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

// Shopping bag screen showing selected items, quantity controls, and checkout actions.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BagScreen(
    cartItems: List<CartItem>,
    totalPrice: Double,
    onBackClick: () -> Unit,
    onIncrease: (Product, String) -> Unit,
    onDecrease: (Product, String) -> Unit,
    onRemove: (Product, String) -> Unit,
    onClearBag: () -> Unit,
    onBuyNow: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Shopping Bag",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            CheckoutBottomBar(
                totalPrice = totalPrice,
                onClearBag = onClearBag,
                onBuyNow = onBuyNow,
                isBagEmpty = cartItems.isEmpty()
            )
        }
    ) { paddingValues ->
        if (cartItems.isEmpty()) {
            EmptyBagContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                cartItems.forEach { cartItem ->
                    BagItemCard(
                        cartItem = cartItem,
                        onIncrease = {
                            onIncrease(cartItem.product, cartItem.size)
                        },
                        onDecrease = {
                            onDecrease(cartItem.product, cartItem.size)
                        },
                        onRemove = {
                            onRemove(cartItem.product, cartItem.size)
                        }
                    )
                }

                Spacer(
                    modifier = Modifier.height(120.dp)
                )
            }
        }
    }
}

// Empty state displayed when there are no items in the shopping bag.
@Composable
fun EmptyBagContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Empty Bag",
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Your shopping bag is empty",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Add sneakers from the home page.",
                color = Color.Gray
            )
        }
    }
}

// Reusable card for one shopping bag item with quantity and remove controls.
@Composable
fun BagItemCard(
    cartItem: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    val product = cartItem.product

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShoeImageBox(
                product = product,
                modifier = Modifier.size(86.dp)
            )

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                Text(
                    text = product.brand,
                    color = Color.Gray,
                    fontSize = 13.sp
                )

                Text(
                    text = "Size: US ${cartItem.size}",
                    color = Color.Gray,
                    fontSize = 12.sp
                )

                Text(
                    text = "$${formatPrice(product.price)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QuantityButton(
                        text = "-",
                        onClick = onDecrease
                    )

                    Text(
                        text = cartItem.quantity.toString(),
                        modifier = Modifier.width(34.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )

                    QuantityButton(
                        text = "+",
                        onClick = onIncrease
                    )
                }
            }

            IconButton(
                onClick = onRemove
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove",
                    tint = Color(0xFFE53935)
                )
            }
        }
    }
}

// Small circular button used to increase or decrease item quantity.
@Composable
fun QuantityButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(Color.Black)
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

// Simple payment screen that confirms the final shopping bag total.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    totalPrice: Double,
    onBackClick: () -> Unit,
    onConfirmPayment: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Payment",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            Text(
                text = "Payment Method",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF3E0)
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF6D00)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(14.dp)
                    )

                    Column {
                        Text(
                            text = "Cash",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Pay with cash when you receive your sneakers.",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Payment Method",
                            color = Color.Gray
                        )

                        Text(
                            text = "Cash",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "$${formatPrice(totalPrice)}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Button(
                onClick = onConfirmPayment,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6D00)
                )
            ) {
                Text(
                    text = "Confirm Cash Payment",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Bottom checkout bar showing subtotal, total, and checkout actions.
@Composable
fun CheckoutBottomBar(
    totalPrice: Double,
    onClearBag: () -> Unit,
    onBuyNow: () -> Unit,
    isBagEmpty: Boolean
) {
    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Subtotal",
                    color = Color.Gray
                )

                Text(
                    text = "$${formatPrice(totalPrice)}",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Tax",
                    color = Color.Gray
                )

                Text(
                    text = "$0.00",
                    fontWeight = FontWeight.Bold
                )
            }

            Divider(
                modifier = Modifier.padding(vertical = 10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = "$${formatPrice(totalPrice)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onClearBag,
                    modifier = Modifier.weight(1f),
                    enabled = !isBagEmpty,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.DarkGray
                    )
                ) {
                    Text(text = "Clear")
                }

                Button(
                    onClick = onBuyNow,
                    modifier = Modifier.weight(1f),
                    enabled = !isBagEmpty,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6D00)
                    )
                ) {
                    Text(text = "Buy Now")
                }
            }
        }
    }
}

// Formats product and cart prices with two decimal places.
fun formatPrice(
    price: Double
): String {
    return String.format("%.2f", price)
}