package com.example.apache

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SaveMode, SparkSession}

class SaveProductJSON extends DataSaver[(Int, String, Double)] {
  override def save(data: RDD[(Int, String, Double)], outputPath: String, tableName: String): Unit = {
    val spark = SparkSession.builder().getOrCreate()
    import spark.implicits._

    val df = data.toDF("ProductID", "ProductName", "Price")
    df.coalesce(1)
      .write.mode(SaveMode.Overwrite)
      .json(outputPath)

    println(s"Table $tableName saved as JSON at $outputPath")
  }
}
