package com.example.wms

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.wms.StoredDataHelper.get
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Locale

class DatabaseHelper(context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    // This method is called the first time a database is accessed.
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_USERS)
        sqLiteDatabase.execSQL(CREATE_TABLE_PRODUCTS)
        sqLiteDatabase.execSQL(CREATE_TABLE_TRANSACTIONS)
    }

    /* This method is called if the database version number changes.
    It prevents previous users apps from breaking when change the database design.
    */
    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSACTIONS")
        onCreate(sqLiteDatabase)
    }

    // Table Transactions methods
    fun addTransaction(transaction: Transaction) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        val locale = Locale.getDefault()
        val formatter = SimpleDateFormat("yyyy-MM-dd", locale)
        val date = Date()
        val transactionDate = formatter.format(date)
        contentValues.put(COLUMN_PRODUCT_NAME, transaction.getProductName())
        contentValues.put(COLUMN_IMG_NAME, transaction.getImageName())
        contentValues.put(COLUMN_TRANSACTION_DATE, transactionDate)
        contentValues.put(COLUMN_AMOUNT, transaction.getAmount())
        contentValues.put(COLUMN_USERNAME, transaction.getUsername())
        db.insert(TABLE_TRANSACTIONS, null, contentValues)
        db.close()
    }

    fun getTransactionsChart(username: String?): MutableList<Transaction?> {
        val transactions: MutableList<Transaction?> = ArrayList<Transaction?>()
        val db = this.readableDatabase
        val selection = "$COLUMN_USERNAME = ?"
        val selectionArgs = arrayOf<String?>(username)
        val columns: Array<String?> = arrayOf<String?>(
            COLUMN_PRODUCT_NAME, COLUMN_IMG_NAME,
            COLUMN_TRANSACTION_DATE, "SUM(Amount) AS Amount"
        )
        val cursor = db.query(
            TABLE_TRANSACTIONS, columns, selection,
            selectionArgs, COLUMN_TRANSACTION_DATE, null, null
        )
        val colProductName = cursor.getColumnIndex(COLUMN_PRODUCT_NAME)
        val colImageName = cursor.getColumnIndex(COLUMN_IMG_NAME)
        val colTransactionDate = cursor.getColumnIndex(COLUMN_TRANSACTION_DATE)
        val colAmount = cursor.getColumnIndex(COLUMN_AMOUNT)
        var transaction: Transaction?
        while (cursor.moveToNext()) {
            transaction = Transaction(
                cursor.getString(colTransactionDate),
                cursor.getString(colProductName), cursor.getString(colImageName), username
            )
            transaction.setAmount(cursor.getString(colAmount).toInt())
            transactions.add(transaction)
        }
        cursor.close()
        db.close()
        return transactions
    }

    fun getTransactions(username: String?, date: String?): MutableList<Transaction?> {
        val transactions: MutableList<Transaction?> = ArrayList<Transaction?>()
        val db = this.readableDatabase
        val columns: Array<String?> = arrayOf<String?>(
            COLUMN_PRODUCT_NAME, COLUMN_IMG_NAME,
            COLUMN_TRANSACTION_DATE
        )
        var selection: String?
        var selectionArgs: Array<String?>?
        if (date != null) {
            selection = "$COLUMN_USERNAME = ? AND $COLUMN_TRANSACTION_DATE = ?"
            selectionArgs = arrayOf<String?>(username, date)
        } else {
            selection = "$COLUMN_USERNAME = ?"
            selectionArgs = arrayOf<String?>(username)
        }
        val cursor = db.query(
            TABLE_TRANSACTIONS, columns, selection,
            selectionArgs, null, null, null
        )
        val colProductName = cursor.getColumnIndex(COLUMN_PRODUCT_NAME)
        val colImageName = cursor.getColumnIndex(COLUMN_IMG_NAME)
        val colTransactionDate = cursor.getColumnIndex(COLUMN_TRANSACTION_DATE)
        var transaction: Transaction?
        while (cursor.moveToNext()) {
            transaction = Transaction(
                cursor.getString(colTransactionDate),
                cursor.getString(colProductName), cursor.getString(colImageName), username
            )
            transactions.add(transaction)
        }
        cursor.close()
        db.close()
        return transactions
    }

    fun updateTransactions(context: Context, username: String?) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_USERNAME, username)
        val where = "$COLUMN_USERNAME = ?"
        val whereArgs: Array<String?> = arrayOf<String?>(get(context))
        db.update(TABLE_TRANSACTIONS, contentValues, where, whereArgs)
    }

    companion object {
        // Database information
        private const val DB_NAME = "MARKETEER.DB"

        // Table name
        private const val TABLE_USERS = "Users"
        private const val TABLE_PRODUCTS = "Products"
        private const val TABLE_TRANSACTIONS = "Transactions"

        // Database version
        private const val DB_VERSION = 1

        // Column name
        private const val COLUMN_USERNAME = "Username"
        private const val COLUMN_PASSWORD = "Password"
        private const val COLUMN_ADDRESS = "Address"
        private const val COLUMN_PHONE = "Phone"
        private const val COLUMN_PROFILE_IMG = "ProfileImage"
        private const val COLUMN_PRODUCT_ID = "ProductId"
        private const val COLUMN_NAME = "Name"
        private const val COLUMN_PRICE = "Price"
        private const val COLUMN_SELLER = "Seller"
        private const val COLUMN_STATUS = "Status"
        private const val COLUMN_IMG_NAME = "Image"
        private const val COLUMN_PRODUCT_NAME = "ProductName"
        private const val COLUMN_TRANSACTION_DATE = "TransactionDate"
        private const val COLUMN_AMOUNT = "Amount"

        // Create table
        private const val CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "(" +
                COLUMN_USERNAME + " TEXT PRIMARY KEY NOT NULL," +
                COLUMN_PASSWORD + " TEXT NOT NULL," +
                COLUMN_ADDRESS + " TEXT," +
                COLUMN_PHONE + " TEXT," +
                COLUMN_PROFILE_IMG + " TEXT" +
                ")"

        private const val CREATE_TABLE_PRODUCTS = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL, " + COLUMN_PRICE + " REAL NOT NULL, " +
                COLUMN_SELLER + " TEXT NOT NULL, " + COLUMN_STATUS + " TEXT NOT NULL, " +
                COLUMN_IMG_NAME + " TEXT NOT NULL)"

        private const val CREATE_TABLE_TRANSACTIONS = "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
                COLUMN_IMG_NAME + " TEXT NOT NULL, " +
                COLUMN_TRANSACTION_DATE + " TEXT NOT NULL, " +
                COLUMN_AMOUNT + " INTEGER NOT NULL, " +
                COLUMN_USERNAME + " TEXT NOT NULL)"
    }
}