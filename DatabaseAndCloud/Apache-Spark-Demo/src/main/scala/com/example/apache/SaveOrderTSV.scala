package com.example.apache

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SaveMode, SparkSession}

class SaveOrderTSV extends DataSaver[(Int, Int, String)] {
  override def save(data: RDD[(Int, Int, String)], outputPath: String, tableName: String): Unit = {
    val spark = SparkSession.builder().getOrCreate()
    import spark.implicits._

    val df = data.toDF("OrderID", "CustomerID", "OrderDate")
    df.coalesce(1)
      .write.mode(SaveMode.Overwrite)
      .option("sep", "\t")
      .csv(outputPath)

    println(s"Table $tableName saved as TSV at $outputPath")
  }
}
