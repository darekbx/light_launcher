package com.darekbx.lightlauncher.ui.mathgame

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.darekbx.lightlauncher.ui.theme.fontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

private const val KEY_BACKSPACE = "back"
private const val KEY_SUBMIT = "="
private const val KEY_MINUS = "-"
private const val MAX_INPUT_LENGTH = 6

private val MATH_CORRECT_COUNT_KEY = intPreferencesKey("mathgame_correct_count")
private val MATH_WRONG_COUNT_KEY = intPreferencesKey("mathgame_wrong_count")

@Composable
fun MathGamePage(onBack: () -> Unit = { }) {
    BackHandler { onBack() }
    Box(modifier = Modifier.fillMaxSize()) {
        MathGameView(modifier = Modifier.fillMaxSize())
        Column(
            modifier = Modifier.align(Alignment.BottomEnd),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier
                    .clickable { onBack() }
                    .padding(24.dp),
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "forward"
            )
            repeat(2) {
                Icon(
                    modifier = Modifier.padding(24.dp),
                    tint = Color.Transparent,
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = "up"
                )
            }
        }
    }
}

@Composable
fun MathGameView(modifier: Modifier = Modifier) {
    val equationGenerator = remember { EquationGenerator() }
    val dataStore: DataStore<Preferences> = koinInject()
    val countsFlow = remember {
        dataStore.data.map { preferences ->
            MathGameCounters(
                correct = preferences[MATH_CORRECT_COUNT_KEY] ?: 0,
                wrong = preferences[MATH_WRONG_COUNT_KEY] ?: 0
            )
        }
    }
    val mathCounts by countsFlow.collectAsState(initial = MathGameCounters())
    var level by remember { mutableIntStateOf(1) }
    var equation by remember { mutableStateOf(equationGenerator.generateEquation(level)) }
    var currentCorrect by remember { mutableIntStateOf(0) }
    var userInput by remember { mutableStateOf("") }
    var wrongAnswer by remember { mutableStateOf<WrongAnswerState?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(level, currentCorrect) {
        equation = equationGenerator.generateEquation(level)
        userInput = ""
    }

    LaunchedEffect(wrongAnswer) {
        wrongAnswer?.let {
            delay(5_000)
            wrongAnswer = null
        }
    }

    fun updateCounters(correctIncrement: Int = 0, wrongIncrement: Int = 0) {
        if (correctIncrement == 0 && wrongIncrement == 0) return
        coroutineScope.launch {
            dataStore.edit { preferences ->
                val currentCorrect = preferences[MATH_CORRECT_COUNT_KEY] ?: 0
                val currentWrong = preferences[MATH_WRONG_COUNT_KEY] ?: 0
                preferences[MATH_CORRECT_COUNT_KEY] = currentCorrect + correctIncrement
                preferences[MATH_WRONG_COUNT_KEY] = currentWrong + wrongIncrement
            }
        }
    }

    fun appendDigit(value: String) {
        if (userInput.length >= MAX_INPUT_LENGTH) return
        userInput += value
    }

    fun backspace() {
        if (userInput.isNotEmpty()) {
            userInput = userInput.dropLast(1)
        }
    }

    fun submitAnswer() {
        val submitted = userInput.toIntOrNull() ?: return
        if (submitted == equation.result) {
            currentCorrect += 1
            if (currentCorrect > 0 && currentCorrect % 5 == 0) {
                level += 1
            }
            updateCounters(correctIncrement = 1)
            wrongAnswer = null
        } else {
            updateCounters(wrongIncrement = 1)
            wrongAnswer = WrongAnswerState(equation.equation, equation.result)
            equation = equationGenerator.generateEquation(level)
        }
        userInput = ""
    }

    val keypad = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf(KEY_BACKSPACE, "0", KEY_MINUS)
    )

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                text = equation.equation.replace('/', '÷').replace('*', '\u00D7'),
                textAlign = TextAlign.Center,
                letterSpacing = 16.sp,
                style = MaterialTheme.typography.displayMedium,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            wrongAnswer
                ?.let {
                    Text(
                        text = "${
                            it.equation.replace('/', '÷').replace('*', '\u00D7')
                        } = ${it.answer}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.displaySmall,
                        letterSpacing = 14.sp,
                        fontSize = 18.sp,
                        fontFamily = fontFamily,
                        textAlign = TextAlign.Center
                    )
                }
                ?: run {
                    Text(
                        text = if (userInput.isBlank()) "" else userInput,
                        style = MaterialTheme.typography.displaySmall,
                        fontFamily = fontFamily,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatLabel("wrong", mathCounts.wrong, countColor = Color(0xFFFF8A80))
                StatLabel("correct", mathCounts.correct, countColor = Color(0xFF85C677))
                StatLabel("level", level)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 64.dp, end = 64.dp, bottom = 64.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            keypad.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    row.forEach { key ->
                        val buttonModifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                        when (key) {
                            KEY_BACKSPACE -> MathKeyButton(
                                modifier = buttonModifier,
                                onClick = ::backspace
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                                    contentDescription = "backspace"
                                )
                            }

                            else -> MathKeyButton(
                                modifier = buttonModifier,
                                onClick = { appendDigit(key) }
                            ) {
                                Text(
                                    text = key,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontFamily = fontFamily,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }
            }

            MathKeyButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                onClick = ::submitAnswer
            ) {
                Text(
                    text = KEY_SUBMIT,
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = fontFamily,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
private fun StatLabel(label: String, value: Int, countColor: Color? = null) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontFamily = fontFamily
        )
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleLarge,
            color = countColor ?: MaterialTheme.colorScheme.onBackground,
            fontFamily = fontFamily
        )
    }
}

@Composable
private fun MathKeyButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f),
            contentColor = MaterialTheme.colorScheme.onBackground
        )
    ) {
        content()
    }
}

private data class MathGameCounters(
    val correct: Int = 0,
    val wrong: Int = 0
)

private data class WrongAnswerState(
    val equation: String,
    val answer: Int
)
