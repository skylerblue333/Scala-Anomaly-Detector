package com.skycoin4444.anomaly

object Main:
  def main(args: Array[String]): Unit =
    if args.length != 4 then
      System.err.println("usage: sky-anomaly <metric> <value> <baseline> <tolerance>")
      sys.exit(2)

    val parsed = for
      value <- args(1).toDoubleOption.toRight("value must be numeric")
      baseline <- args(2).toDoubleOption.toRight("baseline must be numeric")
      tolerance <- args(3).toDoubleOption.toRight("tolerance must be numeric")
      detection <- AnomalyDetector.detect(Observation(args(0), value, baseline, tolerance))
    yield detection

    parsed match
      case Right(d) =>
        println(s"metric=${d.metric} anomalous=${d.anomalous} deviation=${d.deviation} score=${d.score}")
      case Left(error) =>
        System.err.println(s"error: $error")
        sys.exit(2)
