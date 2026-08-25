package com.skycoin4444.anomaly

class AnomalyDetectorSuite extends munit.FunSuite:
  test("flags observations beyond tolerance") {
    val result = AnomalyDetector.detect(Observation("latency", 130, 100, 20))
    assertEquals(result.map(_.anomalous), Right(true))
  }

  test("accepts observations inside tolerance") {
    val result = AnomalyDetector.detect(Observation("latency", 110, 100, 20))
    assertEquals(result.map(_.anomalous), Right(false))
  }

  test("rejects invalid tolerance") {
    assert(AnomalyDetector.detect(Observation("latency", 1, 1, 0)).isLeft)
  }
