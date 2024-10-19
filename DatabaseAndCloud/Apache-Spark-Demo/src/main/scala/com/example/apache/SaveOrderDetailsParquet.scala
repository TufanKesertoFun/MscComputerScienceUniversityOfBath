package com.example.apache

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SaveMode, SparkSession}

class SaveOrderDetailsParquet extends DataSaver[(Int, Int, Int)] {
  override def save(data: RDD[(Int, Int, Int)], outputPath: String, tableName: String): Unit = {
    val spark = SparkSession.builder().getOrCreate()
    import spark.implicits._

    // Convert RDD to DataFrame
    val df = data.toDF("OrderDetailID", "OrderID", "Quantity")

    // Save as Parquet
    try {
      df.write
        .mode(SaveMode.Overwrite)
        .parquet(outputPath) // Use Parquet format
      // You can also use .format("parquet") instead of .parquet(outputPath)

      println(s"Table $tableName saved to Parquet at $outputPath.")
    } catch {
      case e: Exception =>
        println(s"Error saving table $tableName: ${e.getMessage}")
    }
  }
}
