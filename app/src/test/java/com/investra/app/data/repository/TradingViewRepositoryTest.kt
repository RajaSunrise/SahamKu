package com.investra.app.data.repository

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TradingViewRepositoryTest {

    private val repository = TradingViewRepository()

    @Test
    fun testFallbackStockData() {
        val gainers = repository.getFallbackStocks("gainers")
        assertNotNull(gainers)
        assertTrue(gainers.isNotEmpty())
        assertEquals("NVDA", gainers[0].ticker)
        assertEquals("NVIDIA Corporation", gainers[0].name)
        assertTrue(gainers[0].price > 0)
    }

    @Test
    fun testFetchStockScannerReturnsData() = runBlocking {
        val result = repository.fetchStockScanner("gainers", 10)
        assertTrue(result.isSuccess)
        val stocks = result.getOrNull()
        assertNotNull(stocks)
        assertTrue(stocks!!.isNotEmpty())
    }
}
