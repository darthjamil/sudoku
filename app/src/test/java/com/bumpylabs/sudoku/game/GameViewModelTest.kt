package com.bumpylabs.sudoku.game

import com.bumpylabs.sudoku.GameViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*
import org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)
internal class GameVIewModelTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun can_create_new_vm() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        val uiState = vm.uiState.value

        assertEquals(3, uiState.rank)
        assertArrayEquals((1..9).toList().toTypedArray(), uiState.valueOptions)
        assertNull(uiState.selectedCellRow)
        assertNull(uiState.selectedCellCol)
        assertFalse(uiState.enableValueSelection)
        assertEquals(9, uiState.grid.size)
        assertFalse(gridIsFull(uiState.grid))
        assertFalse(gridIsEmpty(uiState.grid))
    }

    @Test
    fun clicking_cell_allows_editing() = runTest {
        val vm = createViewModel()
        vm.clickCell(0, 2)
        advanceUntilIdle()

        val uiState = vm.uiState.value

        assertEquals(0, uiState.selectedCellRow)
        assertEquals(2, uiState.selectedCellCol)
        assertTrue(uiState.enableValueSelection)
    }

    @Test
    fun clicking_already_selected_cell_disallows_editing() = runTest {
        val vm = createViewModel()
        vm.clickCell(0, 2)
        vm.clickCell(0, 2)
        advanceUntilIdle()

        val uiState = vm.uiState.value

        assertNull(uiState.selectedCellRow)
        assertNull(uiState.selectedCellCol)
        assertFalse(uiState.enableValueSelection)
    }

    @Test
    fun clicking_given_does_not_allow_editing() = runTest {
        val vm = createViewModel()
        vm.clickCell(0, 0)
        advanceUntilIdle()

        val uiState = vm.uiState.value

        assertEquals(0, uiState.selectedCellRow)
        assertEquals(0, uiState.selectedCellCol)
        assertFalse(uiState.enableValueSelection)
    }

    @Test
    fun clicking_value_without_a_selected_cell_does_nothing() = runTest {
        val vm = createViewModel()
        vm.clickValue(5)
        advanceUntilIdle()

        val uiState = vm.uiState.value

        assertNull(uiState.selectedCellRow)
        assertNull(uiState.selectedCellCol)
        assertFalse(uiState.enableValueSelection)
    }

    @Test
    fun clicking_invalid_value_on_selected_cell_does_nothing() = runTest {
        val vm = createViewModel()
        vm.clickCell(0, 2)
        vm.clickValue(6)
        advanceUntilIdle()

        val uiState = vm.uiState.value

        assertEquals(0, uiState.grid[0][2])
        assertEquals(0, uiState.selectedCellRow)
        assertEquals(2, uiState.selectedCellCol)
        assertTrue(uiState.enableValueSelection)
    }

    @Test
    fun clicking_valid_value_on_selected_cell_updates_cell() = runTest {
        val vm = createViewModel()
        vm.clickCell(0, 2)
        vm.clickValue(2)
        advanceUntilIdle()

        val uiState = vm.uiState.value

        assertEquals(2, uiState.grid[0][2])
        assertNull(uiState.selectedCellRow)
        assertNull(uiState.selectedCellCol)
        assertFalse(uiState.enableValueSelection)
    }

    private fun createViewModel(): GameViewModel {
        return GameViewModel(3, GoldenGameGenerator())
    }

    private fun gridIsFull(grid: Array<IntArray>): Boolean {
        return grid.all { row ->
            row.all { cell ->
                cell != 0
            }
        }
    }

    private fun gridIsEmpty(grid: Array<IntArray>): Boolean {
        return grid.all { row ->
            row.all { cell ->
                cell == 0
            }
        }
    }
}