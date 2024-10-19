package com.example.apache

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{SparkSession, functions => F}
import org.apache.spark.sql.types._

object HighLevelSummaryOfCustomerDatabase {
  def main(args: Array[String]): Unit = {
    // Initialize Spark session
    Logger.getLogger("org").setLevel(Level.ERROR)
    Logger.getLogger("akka").setLevel(Level.ERROR)

    val spark = SparkSession.builder()
      .appName("ETL Processor")
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")
    spark.conf.set("spark.hadoop.io.native.lib.available", "false")

    // Define schemas
    val customerSchema = StructType(Array(
      StructField("CustomerID", IntegerType, true),
      StructField("CustomerName", StringType, true),
      StructField("Email", StringType, true)
    ))

    val orderSchema = StructType(Array(
      StructField("OrderID", IntegerType, true),
      StructField("CustomerID", IntegerType, true),
      StructField("OrderDate", TimestampType, true)  // Change to TimestampType
    ))

    val productSchema = StructType(Array(
      StructField("ProductID", IntegerType, true),
      StructField("ProductName", StringType, true),
      StructField("Price", DoubleType, true),
      StructField("SupplierID", IntegerType, true)
    ))

    val inventorySchema = StructType(Array(
      StructField("InventoryID", IntegerType, true),
      StructField("ProductID", IntegerType, true),
      StructField("Quantity", IntegerType, true)
    ))

    val supplierSchema = StructType(Array(
      StructField("SupplierID", IntegerType, true),
      StructField("SupplierName", StringType, true),
      StructField("Email", StringType, true)
    ))

    val feedbackSchema = StructType(Array(
      StructField("FeedbackID", IntegerType, true),
      StructField("CustomerID", IntegerType, true),
      StructField("Feedback", StringType, true)
    ))

    val orderDetailsSchema = StructType(Array(
      StructField("OrderDetailID", IntegerType, true),
      StructField("OrderID", IntegerType, true),
      StructField("ProductID", IntegerType, true),
      StructField("Quantity", IntegerType, true)
    ))

    // Load data from previously saved formats with schemas
    val customerDF = spark.read.schema(customerSchema).option("header", "true").csv("src/main/data/customer_data.csv")
    val orderDF = spark.read.schema(orderSchema).option("header", "true").csv("src/main/data/order_data.tsv")
    val productDF = spark.read.schema(productSchema).option("header", "true").json("src/main/data/product_data.json")
    val inventoryDF = spark.read.schema(inventorySchema).option("header", "true").orc("src/main/data/inventory_data.orc")
    val supplierDF = spark.read.schema(supplierSchema).format("avro").load("src/main/data/supplier_data.avro")
    val feedbackDF = spark.read.schema(feedbackSchema).parquet("src/main/data/customer_feedback_data.parquet")
    val orderDetailsDF = spark.read.schema(orderDetailsSchema).parquet("src/main/data/order_details_data.parquet")

    // Show 5 values from each DataFrame except for the sales table
    println("Customer Data:")
    customerDF.show(5)

    println("Order Data:")
    orderDF.show(5)

    println("Product Data:")
    productDF.show(5)

    println("Inventory Data:")
    inventoryDF.show(5)

    println("Supplier Data:")
    supplierDF.show(5)

    println("Feedback Data:")
    feedbackDF.show(5)

    println("Order Details Data:")
    orderDetailsDF.show(5)

    // Perform full outer joins while selecting necessary columns to avoid duplicates
    val joinedDF = customerDF
      .join(orderDF, Seq("CustomerID"), "full_outer")
      .join(orderDetailsDF.select("OrderID", "ProductID"), Seq("OrderID"), "full_outer")
      .join(productDF, Seq("ProductID"), "full_outer")
      .join(feedbackDF.select("CustomerID", "Feedback"), Seq("CustomerID"), "full_outer")
      .join(supplierDF.select("SupplierID", "SupplierName"), Seq("SupplierID"), "full_outer")
      .join(inventoryDF.select("ProductID", "Quantity"), Seq("ProductID"), "full_outer") // Assuming ProductID is relevant for inventory

    // Generate fake OrderDate based on CustomerID
    val resultDF = joinedDF.withColumn("OrderDate",
      F.when(joinedDF("CustomerID") < 100, F.trunc(F.current_date(), "MM"))  // Current Month
        .when(joinedDF("CustomerID").between(100, 500), F.add_months(F.trunc(F.current_date(), "MM"), -1))  // Last Month
        .when(joinedDF("CustomerID").between(501, 1000), F.add_months(F.trunc(F.current_date(), "MM"), -2))  // 2 Months Ago
        .when(joinedDF("CustomerID") > 1000, F.add_months(F.trunc(F.current_date(), "MM"), -3))  // 3 Months Ago
        .otherwise(null)  // Set to null for any other cases
    )

    println("Joined Table:")
    resultDF.show(20)

    println("Count of joined Table ")
    resultDF.cache()
    println(resultDF.count())

    // Write the result DataFrame to a partitioned table
    resultDF.write
      .mode("overwrite")
      .partitionBy("OrderDate")  // Partitioning by OrderCategory
      .format("parquet")
      .save("src/main/data/joined_output")

    // Clean up and stop Spark session
    spark.stop()
  }
}
