package com.grieger.loadsink;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;

public class RandomTimeFiberResponder extends Fiber<Void> {
  final Logger logger = Logger.getLogger(RandomTimeFiberResponder.class.getName());
  final HttpServerExchange exchange;
  final Duration latency;

  public RandomTimeFiberResponder(HttpServerExchange exchange) {
    super();
    this.exchange = exchange;
    this.latency = Duration.of((long) (Math.random() * 500), ChronoUnit.MILLIS);
  }

  @Override
  protected Void run() throws SuspendExecution, InterruptedException {
    logger.info("Waiting " + this.latency.toMillis() + "ms.");
    Fiber.sleep(this.latency.toMillis());

    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
    exchange.setResponseCode(200);
    exchange.getResponseSender().send("{\"timeout\": " + this.latency.toMillis() + "}");
    exchange.endExchange();

    return null;
  }
}
