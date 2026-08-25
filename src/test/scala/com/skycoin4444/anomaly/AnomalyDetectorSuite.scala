package com.skycoin4444.anomaly

class AnomalyDetectorSuite extends munit.FunSuite:
  test("flags observations beyond tolerance") {
    val result = AnomalyDetector.detect(Observation(" latency ", 130, 100, 20))
    assertEquals(result.map(_.anomalous), Right(true))
    assertEquals(result.map(_.metric), Right("latency"))
  }

  test("accepts observations exactly at tolerance") {
    val result = AnomalyDetector.detect(Observation("latency", 120, 100, 20))
    assertEquals(result.map(_.anomalous), Right(false))
    assertEquals(result.map(_.score), Right(1.0))
  }

  test("rejects invalid values and metric names") {
    assert(AnomalyDetector.detect(Observation("", 1, 1, 1)).isLeft)
    assert(AnomalyDetector.detect(Observation("x" * 129, 1, 1, 1)).isLeft)
    assert(AnomalyDetector.detect(Observation("metric", Double.NaN, 1, 1)).isLeft)
    assert(AnomalyDetector.detect(Observation("metric", 1, 1, 0)).isLeft)
  }

  test("batch detection is bounded and summarizes anomalies") {
    val result = AnomalyDetector.detectBatch(Seq(
      Observation("latency", 130, 100, 20),
      Observation("errors", 4, 5, 2)
    ))
    assertEquals(result.map(_.total), Right(2))
    assertEquals(result.map(_.anomalies), Right(1))
    assert(AnomalyDetector.detectBatch(Seq.empty).isLeft)
  }
