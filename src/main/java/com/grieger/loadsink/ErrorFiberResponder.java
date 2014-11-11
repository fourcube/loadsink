package com.grieger.loadsink;


import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;

public class ErrorFiberResponder extends Fiber<Void> {
  final Logger logger = Logger.getLogger(RandomTimeFiberResponder.class.getName());
  final HttpServerExchange exchange;
  final Duration latency;

  public ErrorFiberResponder(HttpServerExchange exchange) {
    super();
    this.exchange = exchange;
    this.latency = Duration.of((long) (Math.random() * 1000), ChronoUnit.MILLIS);
  }

  @Override
  protected Void run() throws SuspendExecution, InterruptedException {
    logger.info("Waiting " + this.latency.toMillis() + "ms.");
    Fiber.sleep(this.latency.toMillis());

    exchange.setResponseCode(StatusCodes.INTERNAL_SERVER_ERROR);
    exchange.endExchange();
    return null;
  }
}
