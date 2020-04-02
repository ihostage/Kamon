package kamon.instrumentation.lagom

import kamon.Kamon
import kamon.metric.{Counter, Gauge, Histogram, InstrumentGroup, MeasurementUnit, Metric, Timer}
import kamon.tag.TagSet

object LagomMetrics {

  val defaultTags: TagSet = TagSet.of("component", "lagom")

  val CBLatency: Metric.Histogram = Kamon.histogram(
    name = "lagom.cb.latency",
    description = "Latency of Lagom Circuit Breakers",
    unit = MeasurementUnit.time.nanoseconds
  )

  val CBState: Metric.Gauge = Kamon.gauge(
    name = "lagom.cb.state",
    description = "State of Lagom Circuit Breakers"
  )

  val CBCounter: Metric.Counter = Kamon.counter(
    name = "lagom.cb.count",
    description = "Count of calls of Lagom Circuit Breakers"
  )

  val CBTimer: Metric.Timer = Kamon.timer(
    name = "lagom.cb.timer",
    description = "Timer of calls of Lagom Circuit Breakers"
  )

  class CircuitBreakerInstruments(circuitBreaker: String, tags: TagSet) extends InstrumentGroup(tags.withTag("cb", circuitBreaker)) {

    val latency: Histogram = register(CBLatency)
    val state: Gauge = register(CBState)
    val successCount: Counter = register(CBCounter, "success", value = true)
    val failureCount: Counter = register(CBCounter, "success", value = false)
    val timeoutFailureCount: Counter = register(
      CBCounter, TagSet.builder().add("success", value = false).add("fail", "timeout").build()
    )
    val breakerOpenFailureCount: Counter = register(
      CBCounter, TagSet.builder().add("success", value = false).add("fail", "open").build()
    )
    val timer: Timer = register(CBTimer, "success", value = true)
    val failureTimer: Timer = register(CBTimer, "success", value = false)

  }

}
