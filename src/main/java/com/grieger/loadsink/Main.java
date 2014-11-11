package com.grieger.loadsink;

import co.paralleluniverse.fibers.Fiber;
import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;

import static io.undertow.Handlers.path;

public class Main {
  final static ArrayList<Class<?>> responders = new ArrayList<>();
  final static private Random rng = new Random();
  static {
    responders.add(RandomTimeFiberResponder.class);
    responders.add(ErrorFiberResponder.class);
  }

  private static Fiber<Void> randomResponder(HttpServerExchange exchange) {
    try {
      Constructor<?> c = responders.get(rng.nextInt(responders.size())).getConstructor(HttpServerExchange.class);
      return (Fiber<Void>) c.newInstance(exchange);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }

    return null;
  }

  public static void main(String [] args) {


    Undertow server = Undertow.builder()
        .addHttpListener(8080, "0.0.0.0")
        .setHandler(
            path().addPrefixPath("/api", exchange -> {
              exchange.dispatch();
              randomResponder(exchange).start();
            })
        )
        .build();

    server.start();
  }
}
