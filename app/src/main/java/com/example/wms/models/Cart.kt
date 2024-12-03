package com.example.wms.models

data class Cart (
    var productList: MutableList<Product> = mutableListOf()
)