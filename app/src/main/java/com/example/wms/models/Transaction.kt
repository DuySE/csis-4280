package com.example.wms.models

class Transaction(
    private val date: String?,
    private val productName: String?, private val imageName: String?,
    private var username: String?
) {
    private var amount = 0

    fun getDate(): String? {
        return date
    }

    fun getProductName(): String? {
        return productName
    }

    fun getUsername(): String? {
        return username
    }

    fun getImageName(): String? {
        return imageName
    }

    fun getAmount(): Int {
        return amount
    }

    fun setAmount(amount: Int) {
        this.amount = amount
    }
}