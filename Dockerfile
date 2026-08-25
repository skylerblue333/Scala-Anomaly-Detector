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
RUN sbt -batch compile test
RUN useradd --system --uid 10001 --create-home appuser \
    && chown -R appuser:appuser /app /home/appuser
ENV HOME=/home/appuser \
    SBT_OPTS="-Dsbt.server.autostart=false -Dsbt.ci=true"
USER 10001
ENTRYPOINT ["sbt", "-batch", "run"]
