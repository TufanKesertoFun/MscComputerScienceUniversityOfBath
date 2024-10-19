package com.example.apache

import org.apache.spark.rdd.RDD

abstract class DataSaver[T] {
  def save(data: RDD[T], outputPath: String, tableName: String): Unit
}
