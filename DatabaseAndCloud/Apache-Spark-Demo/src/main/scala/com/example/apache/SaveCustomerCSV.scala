package com.example.apache

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SaveMode, SparkSession}

class SaveCustomerCSV extends DataSaver[(Int, String, String)] {
  override def save(data: RDD[(Int, String, String)], outputPath: String, tableName: String): Unit = {
    val spark = SparkSession.builder().getOrCreate()
    import spark.implicits._

    val df = data.toDF("CustomerID", "Name", "Email")
    df.coalesce(1)
      .write.mode(SaveMode.Overwrite)
      .csv(outputPath)

    println(s"Table $tableName saved as CSV at $outputPath")
  }
}
