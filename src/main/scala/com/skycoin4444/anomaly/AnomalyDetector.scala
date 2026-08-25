package com.skycoin4444.anomaly

final case class Observation(metric: String, value: Double, baseline: Double, tolerance: Double)
final case class Detection(metric: String, anomalous: Boolean, deviation: Double, score: Double)

object AnomalyDetector:
  def detect(o: Observation): Either[String, Detection] =
    if o.metric.trim.isEmpty then Left("metric must not be empty")
    else if !o.value.isFinite || !o.baseline.isFinite then Left("value and baseline must be finite")
    else if !o.tolerance.isFinite || o.tolerance <= 0 then Left("tolerance must be positive and finite")
    else
      val deviation = math.abs(o.value - o.baseline)
      val score = deviation / o.tolerance
      Right(Detection(o.metric.trim, score > 1.0, deviation, score))
