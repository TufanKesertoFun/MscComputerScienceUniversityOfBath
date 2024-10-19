package com.example.apache

import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.io.{IntWritable, DoubleWritable}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

class SaveSalesSequence extends DataSaver[(Int, Int, Double)] {
  override def save(data: RDD[(Int, Int, Double)], outputPath: String, tableName: String): Unit = {
    val spark = SparkSession.builder().getOrCreate()

    // Convert the (Int, Int, Double) tuple RDD to SequenceFile format (Writable types)
    val sequenceRDD: RDD[(IntWritable, DoubleWritable)] = data.map { case (salesId, productId, saleAmount) =>
      (new IntWritable(salesId), new DoubleWritable(saleAmount))
    }

    // Get the Hadoop FileSystem object
    val fs = FileSystem.get(spark.sparkContext.hadoopConfiguration)
    val outputDir = new Path(outputPath)

    // Check if output path exists, and if it does, delete it to allow overwriting
    if (fs.exists(outputDir)) {
      println(s"Output path $outputPath exists. Deleting it for overwrite.")
      fs.delete(outputDir, true) // true to recursively delete the folder
    }

    // Save as SequenceFile
    sequenceRDD.saveAsSequenceFile(outputPath)

    println(s"Table $tableName saved as Sequence at $outputPath")
  }
}
