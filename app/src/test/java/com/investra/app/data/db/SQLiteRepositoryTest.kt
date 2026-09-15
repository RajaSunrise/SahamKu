package com.investra.app.data.db

import com.investra.app.data.model.Stock
import com.investra.app.data.repository.PortfolioRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SQLiteRepositoryTest {

    @Test
    fun testPortfolioRepositoryWithoutDB() {
        val repo = PortfolioRepository(null)
        assertEquals(100000.00, repo.virtualCash.value, 0.01)
        assertTrue(repo.positions.value.isEmpty())

        val stock = Stock("TSLA", "Tesla", "NASDAQ", 200.0, 5.0, 2.5, 1e9, "1B")
        val buyOk = repo.buyStock(stock, 10)
        assertTrue(buyOk)
        assertEquals(100000.00 - 2000.0, repo.virtualCash.value, 0.01)

        val sellOk = repo.sellStock("TSLA", 10)
        assertTrue(sellOk)
    }
}
