package edu.washington.cs.knowitall.tool.conf.impl
import java.io.PrintWriter

import java.io.File
import edu.washington.cs.knowitall.common.Resource
import edu.washington.cs.knowitall.tool.conf.ConfidenceFunction
import edu.washington.cs.knowitall.tool.conf.FeatureSet
import edu.washington.cs.knowitall.common.Resource.using
import java.io.OutputStream

/** An implementation of logistic regression of features that can be
  * represented as a double. 
  * 
  * @param  featureSet  the features to use
  * @param  featureWeights  the feature weights
  * @param  intercept  the intercept value
  */
class LogisticRegression[T](
  featureSet: FeatureSet[T, Double],
  val featureWeights: Map[String, Double],
  val intercept: Double) extends ConfidenceFunction[T](featureSet) {

  def this(featureSet: FeatureSet[T, Double], weights: Map[String, Double]) = {
    this(featureSet, weights, weights.getOrElse("Intercept", 0.0))
  }

  override def apply(extraction: T): Double = getConf(extraction)

  def getConf(extraction: T): Double = {
    // > 1 pushes values closer to 1 and 0
    // < 1 pulls values away from 1 and 0
    // this is only used for adjusting the aesthetics of the range
    val exponentCoefficient = 2.0

    val z = this.featureSet.featureNames.iterator.map { name =>
      val weight = featureWeights(name)
      if (weight == 0.0 || weight == -0.0) 0
      else weight * featureSet.featureMap(name).apply(extraction)
    }.sum

    1.0 / (1.0 + math.exp(-(exponentCoefficient*z + this.intercept)))
  }
  
  override def save(output: OutputStream): Unit = {
    using (new PrintWriter(output)) { pw =>
      save(pw)
    }
  }

  def save(writer: PrintWriter): Unit = {
    for ((name, weight) <- featureWeights) {
      writer.println(name + "\t" + weight)
    }

    println("Intercept" + "\t" + intercept)
  }
}