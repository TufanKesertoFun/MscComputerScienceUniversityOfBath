package com.example.apache

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.rdd.RDD

object FilePreparer {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)
    Logger.getLogger("akka").setLevel(Level.ERROR)

    // Create Spark session
    val spark = SparkSession.builder()
      .appName("ETL Processor")
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")
    spark.conf.set("spark.hadoop.io.native.lib.available", "false")

    // Generate sample data for each table

    // Customer Data
    val customerData: RDD[(Int, String, String)] = spark.sparkContext.parallelize(
      (1 to 100000).map(i => (i, s"Customer $i", s"customer$i@example.com"))
    )

    // Order Data
    val orderData: RDD[(Int, Int, String)] = spark.sparkContext.parallelize(
      (1 to 1000).map(i => (i, (i % 100) + 1, "2024-10-12")) // Customer IDs range from 1 to 100
    )

    // Product Data
    val productData: RDD[(Int, String, Double)] = spark.sparkContext.parallelize(
      (1 to 1000).map(i => (i, s"Product $i", i * 10.0))
    )

    // Inventory Data
    val inventoryData: RDD[(Int, Int, Int)] = spark.sparkContext.parallelize(
      (1 to 1000).map(i => (i, (i % 100) + 1, i * 5)) // Product IDs range from 1 to 100
    )

    // Supplier Data
    val supplierData: RDD[(Int, String, String)] = spark.sparkContext.parallelize(
      (1 to 1000).map(i => (i, s"Supplier $i", s"supplier$i@example.com"))
    )

    // Sales Data
    val salesData: RDD[(Int, Int, Double)] = spark.sparkContext.parallelize(
      (1 to 1000).map(i => (i, (i % 100) + 1, i * 20.0)) // Order IDs range from 1 to 100
    )

    // Customer Feedback Data
    val customerFeedbackData: RDD[(Int, Int, String)] = spark.sparkContext.parallelize(
      (1 to 1000).map(i => (i, (i % 100000) + 1, s"Feedback for Customer $i")) // Customer IDs range from 1 to 100000
    )

    // Order Details Data
    val orderDetailsParquet: RDD[(Int, Int, Int)] = spark.sparkContext.parallelize(
      (1 to 1000).map(i => (i, (i % 100) + 1, i * 2)) // Order IDs range from 1 to 100
    )

    // Save data using the appropriate classes
    new SaveCustomerCSV().save(customerData, "src/main/data/customer_data.csv", "Customer")
    new SaveOrderTSV().save(orderData, "src/main/data/order_data.tsv", "Order")
    new SaveProductJSON().save(productData, "src/main/data/product_data.json", "Product")
    new SaveInventoryORC().save(inventoryData, "src/main/data/inventory_data.orc", "Inventory")
    new SaveSupplierAVRO().save(supplierData, "src/main/data/supplier_data.avro", "Supplier")
    new SaveSalesSequence().save(salesData, "src/main/data/sales_data.seq", "Sales")
    new SaveCustomerFeedbackParquet().save(customerFeedbackData, "src/main/data/customer_feedback_data.parquet", "CustomerFeedback")
    new SaveOrderDetailsParquet().save(orderDetailsParquet, "src/main/data/order_details_data.parquet", "OrderDetails")

    // Stop Spark session
    spark.stop()
  }
}
