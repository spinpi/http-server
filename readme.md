# Http server

[![Build Status](https://travis-ci.org/spinpi/http-server.svg?branch=master)](https://travis-ci.org/spinpi/http-server)

Akka HTTP server with plug-and-play components.

## Basic usage

```scala
import com.spinpi.http.directives.AccessLoggingFilter
import com.spinpi.http.routes.HttpRoute
import com.spinpi.http.HttpServer

class PingRoute extends HttpRoute {
  override def route: Route = path("ping") {
    complete("pong")
  }
}

object ExampleServer extends HttpServer with App {
  router
    .addPreFilter[AccessLoggingFilter]
    .add[PingRoute]

  startHttpServer()
}

```

## Components

### HTTP router

All components are Akka HTTP directives, organized as diagram below.

```
+------------------------------------------+
|PreFilters                                |
|  +------------------------------------+  |
|  | Exception & Rejection handler      |  |
|  |  +-----------------------------+   |  |
|  |  | Filters                     |   |  |
|  |  |  +----------------------+   |   |  |
|  |  |  | Routes               |   |   |  |
|  |  |  +----------------------+   |   |  |
|  |  +-----------------------------+   |  |
|  +------------------------------------+  |
+------------------------------------------+
```

- PreFilters can be useful for request/response logging, measurement

## GraphQL Server

### Example
See [com.spinpi.graphql.examples.GraphQLServer](graphql/src/test/scala/com/spinpi/graphql/examples/GraphQLServer.scala)

### Customizations
1. Middlewares
2. Playground

## HTTP render template

See [com.spinpi.http.TestServerWithTemplate](template/src/test/scala/com/spinpi/http/TestServerWithTemplate.scala)
