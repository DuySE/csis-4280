package com.example.wms

import android.os.Bundle
import com.example.wms.databinding.ActivityTransactionBinding

class TransactionActivity : DrawerActivity() {
    private lateinit var transactionBinding: ActivityTransactionBinding
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var transactions: MutableList<Transaction>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        transactionBinding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(transactionBinding.getRoot())
        allocateActivityTitle("Transaction History")

//        DatabaseHelper(this).use { databaseHelper ->
//            val username = StoredDataHelper.get(this)
//            transactions = databaseHelper.getTransactions(username, null)
//            transactionAdapter = TransactionAdapter(transactions)
//            val listViewTransactions = findViewById<ListView>(R.id.listViewTransactions)
//            listViewTransactions.adapter = transactionAdapter
//            // Filter by date
//            val editTextDate = findViewById<EditText>(R.id.editTextDate)
//            editTextDate.setOnClickListener {
//                val calendar = Calendar.getInstance()
//                val year = calendar.get(Calendar.YEAR)
//                val month = calendar.get(Calendar.MONTH)
//                val day = calendar.get(Calendar.DAY_OF_MONTH)
//                val datePickerDialog = DatePickerDialog(
//                    this@TransactionActivity,
//                    OnDateSetListener { view1: DatePicker?, year1: Int, monthOfYear: Int, dayOfMonth: Int ->
//                        val selectedDate = String.format(
//                            Locale.getDefault(),
//                            "%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth
//                        )
//                        editTextDate.setText(selectedDate)
//                        val filteredTransactions = databaseHelper.getTransactions(
//                            username,
//                            editTextDate.text.toString()
//                        )
//                        transactionAdapter.setFilteredList(filteredTransactions)
//                    }, year, month, day
//                )
//                datePickerDialog.show()
//            }
//            val chart = findViewById<LineChart>(R.id.chart)
//            val xAxisValues: MutableList<String?> = ArrayList<String?>() // x-axis
//            val entries: MutableList<Entry?> = ArrayList<Entry?>() // data
//            val transactionData =
//                databaseHelper.getTransactionsChart(username)
//            for (i in transactionData.indices) {
//                // only display month and year
//                xAxisValues.add(transactionData[i]!!.getDate())
//                entries.add(Entry(i.toFloat(), transactionData[i]!!.getAmount().toFloat()))
//            }
//
//            chart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisValues)
//
//            val set = LineDataSet(entries, "Quantity")
//            val leftYAxis = chart.axisLeft
//            val rightYAxis = chart.axisRight
//            // avoid duplicate value when zooming
//            leftYAxis.setGranularity(1f)
//            rightYAxis.setGranularity(1f)
//            val xAxis = chart.xAxis
//            xAxis.setGranularity(1f)
//            // cast Y-axis values to integer
//            leftYAxis.valueFormatter = object : ValueFormatter() {
//                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
//                    return value.toInt().toString()
//                }
//            }
//            rightYAxis.valueFormatter = object : ValueFormatter() {
//                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
//                    return value.toInt().toString()
//                }
//            }
//            set.setDrawFilled(true)
//            set.setFillColor(Color.RED)
//            set.setColor(Color.RED)
//            set.setCircleColor(Color.DKGRAY)
//
//            val dataSets: MutableList<ILineDataSet?> = ArrayList<ILineDataSet?>()
//            dataSets.add(set)
//            if (!transactions.isEmpty()) {
//                chart.setData(LineData(dataSets))
//                chart.description.text = ""
//                chart.description.textColor = Color.RED
//                chart.animateY(1400, Easing.EaseInOutBounce)
//                chart.invalidate()
//            }
//            // Setup search view
//            setUpSearchView()
//        }
//    }
//
//    private fun setUpSearchView() {
//        val searchView = findViewById<SearchView>(R.id.transactionSearchView)
//        searchView.clearFocus()
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String): Boolean {
//                filterList(newText, transactions)
//                return true
//            }
//        })
//    }
//
//    private fun filterList(text: String, transactions: MutableList<Transaction?>) {
//        val filteredList: MutableList<Transaction?> = ArrayList<Transaction?>()
//        for (transaction in transactions) {
//            if (transaction?.getProductName()!!
//                    .lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))
//            ) filteredList.add(transaction)
//        }
//        if (filteredList.isEmpty()) Toast.makeText(
//            this,
//            "Transaction not found!",
//            Toast.LENGTH_SHORT
//        ).show()
//        else transactionAdapter.setFilteredList(filteredList)
    }
}