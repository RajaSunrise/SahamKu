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
        assertTrue(positions.isEmpty())
        assertEquals(100000.0, repository.virtualCash.value, 0.01)
    }

    @Test
    fun testBuyStockSuccessful() {
        val stock = Stock("AMD", "Advanced Micro Devices", "NASDAQ", 100.0, 2.0, 2.0, 1000000.0, "1M")
        val initialCash = repository.virtualCash.value
        val success = repository.buyStock(stock, 10.0)

        assertTrue(success)
        assertEquals(initialCash - 1000.0, repository.virtualCash.value, 0.01)
        assertEquals(1, repository.positions.value.size)
        assertEquals("AMD", repository.positions.value[0].ticker)
        assertEquals(10.0, repository.positions.value[0].shares, 0.001)
    }

    @Test
    fun testSellStockSuccessful() {
        val stock = Stock("NVDA", "NVIDIA Corporation", "NASDAQ", 150.0, 5.0, 3.0, 10000000.0, "10M")
        repository.buyStock(stock, 100.0)

        val initialHistoryCount = repository.tradeHistory.value.size
        val success = repository.sellStock("NVDA", 50.0)

        assertTrue(success)
        val nvdaPos = repository.positions.value.find { it.ticker == "NVDA" }
        assertEquals(50.0, nvdaPos?.shares ?: 0.0, 0.001)
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
