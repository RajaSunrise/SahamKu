package com.investra.app.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.investra.app.data.model.Position
import com.investra.app.data.model.TradeHistory

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "sahamku_investra.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_ACCOUNT = "account"
        private const val TABLE_POSITIONS = "positions"
        private const val TABLE_TRADE_HISTORY = "trade_history"
        private const val TABLE_WATCHLIST = "watchlist"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_ACCOUNT (
                id INTEGER PRIMARY KEY DEFAULT 1,
                virtual_cash REAL NOT NULL,
                initial_capital REAL NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_POSITIONS (
                ticker TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                exchange TEXT NOT NULL,
                shares REAL NOT NULL,
                avg_buy_price REAL NOT NULL,
                current_price REAL NOT NULL,
                stop_loss REAL,
                take_profit REAL,
                logo_url TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_TRADE_HISTORY (
                id TEXT PRIMARY KEY,
                ticker TEXT NOT NULL,
                name TEXT NOT NULL,
                exchange TEXT NOT NULL,
                type TEXT NOT NULL,
                shares REAL NOT NULL,
                price REAL NOT NULL,
                realized_pnl REAL NOT NULL,
                realized_pnl_percent REAL NOT NULL,
                date_text TEXT NOT NULL,
                reason_text TEXT NOT NULL,
                is_win INTEGER NOT NULL,
                logo_url TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_WATCHLIST (
                ticker TEXT PRIMARY KEY
            )
            """.trimIndent()
        )

        // Seed initial data for empty portfolio ($100,000 cash, no positions/history)
        val accountValues = ContentValues().apply {
            put("id", 1)
            put("virtual_cash", 100000.00)
            put("initial_capital", 100000.00)
        }
        db.insert(TABLE_ACCOUNT, null, accountValues)

        val seedWatchlist = listOf("MSFT", "AMZN", "META", "GOOGL", "AMD", "BRK.B")
        for (ticker in seedWatchlist) {
            val cv = ContentValues().apply {
                put("ticker", ticker)
            }
            db.insert(TABLE_WATCHLIST, null, cv)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ACCOUNT")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_POSITIONS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRADE_HISTORY")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WATCHLIST")
        onCreate(db)
    }

    fun getAccountInfo(): Pair<Double, Double> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT virtual_cash, initial_capital FROM $TABLE_ACCOUNT WHERE id = 1", null)
        var cash = 100000.0
        var initial = 100000.0
        if (cursor.moveToFirst()) {
            cash = cursor.getDouble(0)
            initial = cursor.getDouble(1)
        }
        cursor.close()
        return Pair(cash, initial)
    }

    fun updateVirtualCash(cash: Double) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("virtual_cash", cash)
        }
        db.update(TABLE_ACCOUNT, cv, "id = 1", null)
    }

    fun resetAccount(newCapital: Double) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("virtual_cash", newCapital)
            put("initial_capital", newCapital)
        }
        db.update(TABLE_ACCOUNT, cv, "id = 1", null)
        db.delete(TABLE_POSITIONS, null, null)
        db.delete(TABLE_TRADE_HISTORY, null, null)
    }

    fun getPositions(): List<Position> {
        val db = readableDatabase
        val list = mutableListOf<Position>()
        try {
            val cursor = db.rawQuery("SELECT ticker, name, exchange, shares, avg_buy_price, current_price, stop_loss, take_profit, logo_url FROM $TABLE_POSITIONS", null)
            while (cursor.moveToNext()) {
                list.add(
                    Position(
                        ticker = cursor.getString(0),
                        name = cursor.getString(1),
                        exchange = cursor.getString(2),
                        shares = cursor.getDouble(3),
                        avgBuyPrice = cursor.getDouble(4),
                        currentPrice = cursor.getDouble(5),
                        stopLoss = if (cursor.isNull(6)) null else cursor.getDouble(6),
                        takeProfit = if (cursor.isNull(7)) null else cursor.getDouble(7),
                        logoUrl = if (cursor.isNull(8)) null else cursor.getString(8)
                    )
                )
            }
            cursor.close()
        } catch (e: Exception) {
            // Fallback for older schema if needed
            val cursor = db.rawQuery("SELECT ticker, name, exchange, shares, avg_buy_price, current_price, stop_loss, take_profit FROM $TABLE_POSITIONS", null)
            while (cursor.moveToNext()) {
                list.add(
                    Position(
                        ticker = cursor.getString(0),
                        name = cursor.getString(1),
                        exchange = cursor.getString(2),
                        shares = cursor.getDouble(3),
                        avgBuyPrice = cursor.getDouble(4),
                        currentPrice = cursor.getDouble(5),
                        stopLoss = if (cursor.isNull(6)) null else cursor.getDouble(6),
                        takeProfit = if (cursor.isNull(7)) null else cursor.getDouble(7),
                        logoUrl = null
                    )
                )
            }
            cursor.close()
        }
        return list
    }

    fun savePosition(position: Position) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("ticker", position.ticker)
            put("name", position.name)
            put("exchange", position.exchange)
            put("shares", position.shares)
            put("avg_buy_price", position.avgBuyPrice)
            put("current_price", position.currentPrice)
            put("stop_loss", position.stopLoss)
            put("take_profit", position.takeProfit)
            put("logo_url", position.logoUrl)
        }
        db.insertWithOnConflict(TABLE_POSITIONS, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun deletePosition(ticker: String) {
        val db = writableDatabase
        db.delete(TABLE_POSITIONS, "ticker = ?", arrayOf(ticker))
    }

    fun getWatchlist(): Set<String> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT ticker FROM $TABLE_WATCHLIST", null)
        val set = mutableSetOf<String>()
        while (cursor.moveToNext()) {
            set.add(cursor.getString(0))
        }
        cursor.close()
        return set
    }

    fun addWatchlist(ticker: String) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("ticker", ticker)
        }
        db.insertWithOnConflict(TABLE_WATCHLIST, null, cv, SQLiteDatabase.CONFLICT_IGNORE)
    }

    fun removeWatchlist(ticker: String) {
        val db = writableDatabase
        db.delete(TABLE_WATCHLIST, "ticker = ?", arrayOf(ticker))
    }

    fun getTradeHistory(): List<TradeHistory> {
        val db = readableDatabase
        val list = mutableListOf<TradeHistory>()
        try {
            val cursor = db.rawQuery("SELECT id, ticker, name, exchange, type, shares, price, realized_pnl, realized_pnl_percent, date_text, reason_text, is_win, logo_url FROM $TABLE_TRADE_HISTORY", null)
            while (cursor.moveToNext()) {
                list.add(
                    TradeHistory(
                        id = cursor.getString(0),
                        ticker = cursor.getString(1),
                        name = cursor.getString(2),
                        exchange = cursor.getString(3),
                        type = cursor.getString(4),
                        shares = cursor.getDouble(5),
                        price = cursor.getDouble(6),
                        realizedPnL = cursor.getDouble(7),
                        realizedPnLPercent = cursor.getDouble(8),
                        dateText = cursor.getString(9),
                        reasonText = cursor.getString(10),
                        isWin = cursor.getInt(11) == 1,
                        logoUrl = if (cursor.isNull(12)) null else cursor.getString(12)
                    )
                )
            }
            cursor.close()
        } catch (e: Exception) {
            val cursor = db.rawQuery("SELECT id, ticker, name, exchange, type, shares, price, realized_pnl, realized_pnl_percent, date_text, reason_text, is_win FROM $TABLE_TRADE_HISTORY", null)
            while (cursor.moveToNext()) {
                list.add(
                    TradeHistory(
                        id = cursor.getString(0),
                        ticker = cursor.getString(1),
                        name = cursor.getString(2),
                        exchange = cursor.getString(3),
                        type = cursor.getString(4),
                        shares = cursor.getDouble(5),
                        price = cursor.getDouble(6),
                        realizedPnL = cursor.getDouble(7),
                        realizedPnLPercent = cursor.getDouble(8),
                        dateText = cursor.getString(9),
                        reasonText = cursor.getString(10),
                        isWin = cursor.getInt(11) == 1,
                        logoUrl = null
                    )
                )
            }
            cursor.close()
        }
        return list
    }

    fun addTradeHistory(history: TradeHistory) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("id", history.id)
            put("ticker", history.ticker)
            put("name", history.name)
            put("exchange", history.exchange)
            put("type", history.type)
            put("shares", history.shares)
            put("price", history.price)
            put("realized_pnl", history.realizedPnL)
            put("realized_pnl_percent", history.realizedPnLPercent)
            put("date_text", history.dateText)
            put("reason_text", history.reasonText)
            put("is_win", if (history.isWin) 1 else 0)
            put("logo_url", history.logoUrl)
        }
        db.insertWithOnConflict(TABLE_TRADE_HISTORY, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }
}
