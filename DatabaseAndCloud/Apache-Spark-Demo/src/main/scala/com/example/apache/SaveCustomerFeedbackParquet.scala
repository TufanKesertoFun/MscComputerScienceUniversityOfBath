package com.example.apache

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SaveMode, SparkSession}

class SaveCustomerFeedbackParquet extends DataSaver[(Int, Int, String)] {
  override def save(data: RDD[(Int, Int, String)], outputPath: String, tableName: String): Unit = {
    val spark = SparkSession.builder().getOrCreate()
    import spark.implicits._

    // Convert RDD to DataFrame
    val df = data.toDF("FeedbackID", "CustomerID", "Feedback")

    // Save as Parquet file (efficient storage)
    df.coalesce(1)
      .write.mode(SaveMode.Overwrite)
      .parquet(outputPath)

    println(s"Table $tableName saved as Parquet at $outputPath")
  }
}
