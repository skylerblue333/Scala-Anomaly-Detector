# Sky Anomaly Detector

A small Scala 3 engineering-beta library and CLI for deterministic threshold-based anomaly checks.

## What it does

Each observation contains a metric name, value, baseline, and positive tolerance. The detector computes absolute deviation and a normalized score (`deviation / tolerance`). Scores greater than `1.0` are marked anomalous; a score exactly equal to `1.0` is not.

The implementation validates finite numeric inputs, trims and bounds metric names to 128 characters, and supports bounded batches of up to 10,000 observations.

## Run locally

Requirements: JDK 21 and sbt.

```bash
sbt -batch test
sbt -batch 'run latency 130 100 20'
```

Example output:

```text
metric=latency anomalous=true deviation=30.0 score=1.5
```

## Container

```bash
docker build -t sky-anomaly .
docker run --rm sky-anomaly latency 110 100 20
```

The image runs as UID `10001`. The container intentionally carries the Scala build runtime; this repository favors transparent reproducibility over claiming a minimized production image.

## CI gate

Pull requests and pushes compile and test the Scala code, exercise the CLI, build the image, verify non-root execution configuration, and run a container smoke check.

## Product boundary

Status: **engineering beta**.

This is a deterministic threshold detector, not a trained machine-learning anomaly model, streaming analytics platform, forecasting service, alerting system, persistent metrics database, or production monitoring deployment. Threshold selection and operational meaning remain the caller's responsibility.

## SKYCOIN4444 integration

The detector can be consumed as a JVM library or wrapped behind a stable adapter for metrics/analytics components. Keep policy configuration outside this library so the standalone product remains reusable.

## Security

The core performs no network access and executes no caller-supplied code. Treat untrusted metric data as untrusted input at any surrounding API boundary. See `SECURITY.md` for reporting guidance.

## License

See `LICENSE`.
