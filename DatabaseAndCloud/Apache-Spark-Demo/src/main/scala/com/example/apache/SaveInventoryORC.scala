package com.example.apache

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SaveMode, SparkSession}

class SaveInventoryORC extends DataSaver[(Int, Int, Int)] {
  override def save(data: RDD[(Int, Int, Int)], outputPath: String, tableName: String): Unit = {
    val spark = SparkSession.builder().getOrCreate()
    import spark.implicits._

    val df = data.toDF("InventoryID", "ProductID", "Stock")
    df.coalesce(1)
      .write.mode(SaveMode.Overwrite)
      .orc(outputPath)

    println(s"Table $tableName saved as ORC at $outputPath")
  }
}
