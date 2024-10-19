package com.example.apache

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SaveMode, SparkSession}

class SaveSupplierAVRO extends DataSaver[(Int, String, String)] {
  override def save(data: RDD[(Int, String, String)], outputPath: String, tableName: String): Unit = {
    val spark = SparkSession.builder().getOrCreate()
    import spark.implicits._

    val df = data.toDF("SupplierID", "SupplierName", "Contact")
    df.coalesce(1)
      .write.mode(SaveMode.Overwrite)
      .format("avro")
      .save(outputPath)

    println(s"Table $tableName saved as AVRO at $outputPath")
  }
}
