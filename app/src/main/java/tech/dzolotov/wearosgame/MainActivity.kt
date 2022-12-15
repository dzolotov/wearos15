@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)

package tech.dzolotov.wearosgame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.material.*
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Navigation()
            }
        }
    }
}

@Composable
fun Navigation() {
    val navController = rememberSwipeDismissableNavController()
    SwipeDismissableNavHost(
        navController = navController,
        startDestination = "game"
    ) {
        composable("main") {
            Content(navController = navController)
        }
        composable("game") {
            GameScreen()
        }
    }
}

var gameField = mutableStateOf(mutableListOf<Int>())

fun initializeGame() {
    val field = mutableListOf<Int>()
    field.addAll(List(16) { 0 })
    for (i in 1..15) {
        var position = Random.nextInt(16)
        while (field[position] != 0) {
            position = Random.nextInt(16)
        }
        field[position] = i
    }
    gameField.value = field
}

@Composable
fun GameScreen() {

    fun toLeft() {
        val pos = gameField.value.indexOf(0)
        if (pos % 4 < 3) {
            //shift next
            val fieldCopy = mutableListOf<Int>()
            fieldCopy.addAll(gameField.value)
            fieldCopy[pos] = fieldCopy[pos + 1]
            fieldCopy[pos + 1] = 0
            gameField.value = fieldCopy
        }
    }

    fun toRight() {
        val pos = gameField.value.indexOf(0)
        if (pos % 4 > 0) {
            //shift next
            val fieldCopy = mutableListOf<Int>()
            fieldCopy.addAll(gameField.value)
            fieldCopy[pos] = fieldCopy[pos - 1]
            fieldCopy[pos - 1] = 0
            gameField.value = fieldCopy
        }
    }

    fun toTop() {
        val pos = gameField.value.indexOf(0)
        if (pos / 4 >= 1) {
            //shift next
            val fieldCopy = mutableListOf<Int>()
            fieldCopy.addAll(gameField.value)
            fieldCopy[pos] = fieldCopy[pos + 4]
            fieldCopy[pos + 4] = 0
            gameField.value = fieldCopy
        }
    }

    fun toBottom() {
        val pos = gameField.value.indexOf(0)
        if (pos / 4 < 3) {
            //shift next
            val fieldCopy = mutableListOf<Int>()
            fieldCopy.addAll(gameField.value)
            fieldCopy[pos] = fieldCopy[pos - 4]
            fieldCopy[pos - 4] = 0
            gameField.value = fieldCopy
        }
    }

    LaunchedEffect(Unit) {
        initializeGame()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Blue)
    ) {
        val size = LocalConfiguration.current.screenWidthDp
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        println(change)
                        if (dragAmount > size / 32) {
                            toRight()
                        }
                        if (dragAmount < -size / 32) {
                            toLeft()
                        }
                    }
                    detectVerticalDragGestures { change, dragAmount ->
                        if (dragAmount > size / 64) {
                            toBottom()
                        }
                        if (dragAmount < -size / 64) {
                            toTop()
                        }
                    }
                }
                .padding((size / 16).dp)
                .safeDrawingPadding()
                .fillMaxSize(),
            content = {
                println("GF = ${gameField.value}")
                items(items = gameField.value) {
                    if (it != 0) {
                        Text(text = it.toString(), textAlign = TextAlign.Center)
                    }
                }
            })
    }
}

@Composable
fun Counter(i: Int) {
    Text(
        "Counter: $i",
        color = Color.Green,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
    )
}

@Composable
fun IncrementButton(onPressed: () -> Unit) {
    Card(onClick = {
        onPressed()
    }, modifier = Modifier.padding(top = 8.dp)) {
        Text(
            "Tap me",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun StartGameButton(onPressed: () -> Unit) {
    Card(onClick = {
        onPressed()
    }, modifier = Modifier.padding(top = 8.dp)) {
        Text(
            "Start game",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
    }
}


@Composable
fun Logo() {
    Image(painter = painterResource(id = R.drawable.demo), contentDescription = "Logo")
}

@Composable
fun Content(navController: NavController) {
    println("Round is ${LocalConfiguration.current.isScreenRound}")
    var counter by remember { mutableStateOf(0) }

    val itemSpacing = 8.dp
    val scrollOffset = 0
    val state = rememberScalingLazyListState(
        initialCenterItemIndex = 1,
        initialCenterItemScrollOffset = scrollOffset
    )

    ScalingLazyColumn(
        modifier = Modifier.fillMaxWidth(),
        anchorType = ScalingLazyListAnchorType.ItemCenter,
        verticalArrangement = Arrangement.spacedBy(itemSpacing),
        state = state,
        autoCentering = AutoCenteringParams(itemOffset = scrollOffset)
    ) {
        item {
            Logo()
        }
        item {
            Counter(i = counter)
        }
        item {
            IncrementButton {
                counter++
            }
        }
        item {
            StartGameButton {
                navController.navigate("game")
            }
        }
        val list = MutableList(10) { "Task $it " }
        items(items = list) {
            Text(it)
        }
    }
}
