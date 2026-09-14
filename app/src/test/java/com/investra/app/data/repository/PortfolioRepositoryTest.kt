package com.investra.app.data.repository

import com.investra.app.data.model.Stock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PortfolioRepositoryTest {

    private val repository = PortfolioRepository()

    @Test
    fun testInitialPositions() {
        val positions = repository.positions.value
        assertEquals(4, positions.size)
        assertEquals("NVDA", positions[0].ticker)
        assertEquals(150, positions[0].shares)
    }

    @Test
    fun testBuyStockSuccessful() {
        val stock = Stock("AMD", "Advanced Micro Devices", "NASDAQ", 100.0, 2.0, 2.0, 1000000.0, "1M")
        val initialCash = repository.virtualCash.value
        val success = repository.buyStock(stock, 10)

        assertTrue(success)
        assertEquals(initialCash - 1000.0, repository.virtualCash.value, 0.01)
    }

    @Test
    fun testSellStockSuccessful() {
        val initialHistoryCount = repository.tradeHistory.value.size
        val success = repository.sellStock("NVDA", 50)

        assertTrue(success)
        val nvdaPos = repository.positions.value.find { it.ticker == "NVDA" }
        assertEquals(100, nvdaPos?.shares)
        assertEquals(initialHistoryCount + 1, repository.tradeHistory.value.size)
    }

    @Test
    fun testResetPortfolio() {
        repository.resetPortfolio(50000.0)
        assertEquals(50000.0, repository.virtualCash.value, 0.01)
        assertEquals(50000.0, repository.initialCapital.value, 0.01)
        assertTrue(repository.positions.value.isEmpty())
        assertTrue(repository.tradeHistory.value.isEmpty())
    }
}
