package com.darekbx.lightlauncher.ui.mathgame

import org.junit.Assert.assertEquals
import org.junit.Test

class EquationGeneratorTest {

    @Test(expected = AssertionError::class)
    fun `should throw on invalid level`() {
        EquationGenerator().generateEquation(0)
    }

    @Test
    fun `should generate equation for level 1`() {
        // Given
        val chars = listOf('+', '-')
        val numbers = listOf(2, 4, 9)
        var numberIndex = 0
        var charIndex = 0

        val equationGenerator = EquationGenerator(
            numberGenerator = { _, _ -> numbers[numberIndex++] },
            charGenerator = { chars[charIndex++] }
        )

        // When
        val result = equationGenerator.generateEquation(1)

        // Then
        assertEquals("2+4-9", result.equation)
        assertEquals(-3, result.result)
    }

    @Test
    fun `should generate equation with division`() {
        // Given
        val chars = listOf('+', '-')
        val numbers = listOf(8, 4, 2)
        var numberIndex = 0
        var charIndex = 0

        val equationGenerator = EquationGenerator(
            numberGenerator = { _, _ -> numbers[numberIndex++] },
            charGenerator = { chars[charIndex++] }
        )

        // When
        val result = equationGenerator.generateEquation(1)

        // Then
        assertEquals("8/4-2", result.equation)
        assertEquals(0, result.result)
    }

    @Test
    fun `should evaluate equation`() {
        val eg = EquationGenerator()
        assertEquals(5, eg.evaluate(listOf(10, 6, 1), listOf('-', '+')))
        assertEquals(10, eg.evaluate(listOf(1, 4, 5), listOf('+', '+')))
        assertEquals(-13, eg.evaluate(listOf(-10, 1, 2), listOf('-', '-')))
        assertEquals(-4, eg.evaluate(listOf(1, 2, 3), listOf('-', '-')))
        assertEquals(17, eg.evaluate(listOf(3, 4, 5), listOf('*', '+')))
        assertEquals(27, eg.evaluate(listOf(3, 3, 3), listOf('*', '*')))
        assertEquals(4, eg.evaluate(listOf(8, 4, 2), listOf('/', '+')))
        assertEquals(0, eg.evaluate(listOf(16, 4, 4), listOf('/', '-')))
        assertEquals(-2, eg.evaluate(listOf(1, 30, 10), listOf('-', '/')))
        assertEquals(35, eg.evaluate(listOf(7, 10, 2), listOf('*', '/')))
    }
}