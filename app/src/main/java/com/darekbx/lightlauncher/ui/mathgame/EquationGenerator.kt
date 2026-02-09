package com.darekbx.lightlauncher.ui.mathgame

import kotlin.random.Random

class EquationGenerator(
    private val numberGenerator: (Int, Int) -> Int = { min, max -> Random.nextInt(min, max + 1) },
    private val charGenerator: () -> Char = { CHARS[Random.nextInt(0, CHARS.size)] }
) {

    data class Equation(val equation: String, val result: Int)

    fun generateEquation(level: Int): Equation {
        assert(level > 0)

        val minNumber = 1
        val maxNumber = 5 + (level * STEP_INCREASE)
        val numbers = (0..2).map { numberGenerator(minNumber, maxNumber) }
        val chars = (0..1).map { charGenerator() }.toMutableList()

        // Add divison if possible
        for (index in numbers.indices) {
            if (index >= numbers.size - 1) break
            val divisor = numbers[index + 1]
            if (numbers[index] % divisor == 0) {
                chars[index] = '/'
                break
            }
        }

        var equation = StringBuilder().apply {
            var charIndex = 0
            append(numbers.first())
            numbers.drop(1).forEach { number ->
                append(chars[charIndex++])
                append(number)
            }
        }

        return Equation(equation.toString(), evaluate(numbers, chars))
    }

    fun evaluate(numbers: List<Int>, operations: List<Char>): Int {
        var nums = numbers.toMutableList()
        var ops = operations.toMutableList()
        var index: Int

        fun makeOperation(index: Int, operation: (Int, Int) -> Int) {
            nums[index] = operation(nums[index], nums[index + 1])
            nums.removeAt(index + 1)
            ops.removeAt(index)
        }

        index = 0
        while (index < ops.size) {
            when (ops[index]) {
                '*' -> makeOperation(index) { a, b -> a * b }
                '/' -> makeOperation(index) { a, b -> a / b }
                else -> index++
            }
        }

        index = 0
        while (index < ops.size) {
            when (ops[index]) {
                '+' -> makeOperation(index) { a, b -> a + b }
                '-' -> makeOperation(index) { a, b -> a - b }
                else -> index++
            }
        }

        return nums.first()
    }

    companion object {
        private const val STEP_INCREASE = 5
        private val CHARS = listOf('+', '-', '*')
    }
}
