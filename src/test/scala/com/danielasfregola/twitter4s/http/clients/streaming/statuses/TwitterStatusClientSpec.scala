package com.danielasfregola.twitter4s.http.clients.streaming.statuses

import akka.http.scaladsl.model.{HttpEntity, HttpMethods}
import com.danielasfregola.twitter4s.entities.enums.Language
import com.danielasfregola.twitter4s.helpers.ClientSpec

class TwitterStatusClientSpec extends ClientSpec {

  class TwitterStatusClientSpecContext extends StreamingClientSpecContext with TwitterStatusClient

  "Twitter Status Streaming Client" should {

    "start a filtered status stream" in new TwitterStatusClientSpecContext {
      val result: Unit =
        when(filterStatuses(follow = Seq(1L, 2L, 3L),
                            tracks = Seq("trending", "other"),
                            languages = Seq(Language.Hungarian, Language.Bengali))(dummyProcessing))
          .expectRequest { request =>
            request.method === HttpMethods.POST
            request.uri.endpoint === "https://stream.twitter.com/1.1/statuses/filter.json"
            request.entity === HttpEntity(`application/x-www-form-urlencoded`,
                                          "follow=1%2C2%2C3&language=hu%2Cbn&stall_warnings=false&track=trending%2Cother")
          }
          .respondWithOk
          .await
      result.isInstanceOf[Unit] should beTrue
    }

    "start a sample status stream" in new TwitterStatusClientSpecContext {
      val result: Unit =
        when(sampleStatuses(languages = Seq(Language.Hungarian, Language.Bengali))(dummyProcessing))
          .expectRequest { request =>
            request.method === HttpMethods.GET
            request.uri.endpoint === "https://stream.twitter.com/1.1/statuses/sample.json"
            request.uri.queryString() === Some("language=hu,bn&stall_warnings=false")
          }
          .respondWithOk
          .await
      result.isInstanceOf[Unit] should beTrue
    }

    "start a firehose status stream" in new TwitterStatusClientSpecContext {
      val result: Unit =
        when(firehoseStatuses(languages = Seq(Language.Hungarian, Language.Bengali))(dummyProcessing))
          .expectRequest { request =>
            request.method === HttpMethods.GET
            request.uri.endpoint === "https://stream.twitter.com/1.1/statuses/firehose.json"
            request.uri.queryString() === Some("language=hu,bn&stall_warnings=false")
          }
          .respondWithOk
          .await
      result.isInstanceOf[Unit] should beTrue
    }
  }
}
