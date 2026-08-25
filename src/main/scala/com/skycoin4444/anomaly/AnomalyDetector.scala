package com.skycoin4444.anomaly

final case class Observation(metric: String, value: Double, baseline: Double, tolerance: Double)
final case class Detection(metric: String, anomalous: Boolean, deviation: Double, score: Double)
final case class BatchDetection(total: Int, anomalies: Int, detections: Vector[Detection])

object AnomalyDetector:
  val MaxMetricLength = 128
  val MaxBatchSize = 10_000

  def detect(o: Observation): Either[String, Detection] =
    val metric = o.metric.trim
    if metric.isEmpty then Left("metric must not be empty")
    else if metric.length > MaxMetricLength then Left(s"metric must be at most $MaxMetricLength characters")
    else if !o.value.isFinite || !o.baseline.isFinite then Left("value and baseline must be finite")
    else if !o.tolerance.isFinite || o.tolerance <= 0 then Left("tolerance must be positive and finite")
    else
      val deviation = math.abs(o.value - o.baseline)
      val score = deviation / o.tolerance
      Right(Detection(metric, score > 1.0, deviation, score))

  def detectBatch(observations: Seq[Observation]): Either[String, BatchDetection] =
    if observations.isEmpty then Left("batch must contain at least one observation")
    else if observations.lengthCompare(MaxBatchSize) > 0 then Left(s"batch must contain at most $MaxBatchSize observations")
    else
      observations.foldLeft[Either[String, Vector[Detection]]](Right(Vector.empty)) {
        case (Right(acc), observation) => detect(observation).map(acc :+ _)
        case (left @ Left(_), _)       => left
      }.map { detections =>
        BatchDetection(detections.size, detections.count(_.anomalous), detections)
      }
