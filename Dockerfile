FROM eclipse-temurin:21-jdk-jammy
ARG SBT_VERSION=1.10.7
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl ca-certificates \
    && curl -fsSL "https://github.com/sbt/sbt/releases/download/v${SBT_VERSION}/sbt-${SBT_VERSION}.tgz" -o /tmp/sbt.tgz \
    && tar -xzf /tmp/sbt.tgz -C /opt \
    && ln -s /opt/sbt/bin/sbt /usr/local/bin/sbt \
    && rm -f /tmp/sbt.tgz \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY build.sbt ./
COPY project ./project
RUN sbt -batch update
COPY src ./src
RUN sbt -batch compile test \
    && mkdir -p /app/runtime-classes /app/runtime-libs \
    && cp -R target/scala-3.3.3/classes/. /app/runtime-classes/ \
    && find /root/.cache/coursier -type f -path '*/org/scala-lang/scala3-library_3/3.3.3/scala3-library_3-3.3.3.jar' -exec cp {} /app/runtime-libs/ \; \
    && find /root/.cache/coursier -type f -path '*/org/scala-lang/scala-library/2.13.12/scala-library-2.13.12.jar' -exec cp {} /app/runtime-libs/ \; \
    && test -f /app/runtime-libs/scala3-library_3-3.3.3.jar \
    && test -f /app/runtime-libs/scala-library-2.13.12.jar
RUN useradd --system --uid 10001 --create-home appuser \
    && chown -R appuser:appuser /app /home/appuser
ENV HOME=/home/appuser
USER 10001
ENTRYPOINT ["java", "-cp", "/app/runtime-classes:/app/runtime-libs/*", "com.skycoin4444.anomaly.Main"]
