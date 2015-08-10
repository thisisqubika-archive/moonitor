package com.mooveit.moonitor.domain.metrics.serialization

import akka.util.ByteString
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.mooveit.moonitor.domain.metrics.MetricConfiguration
import redis.ByteStringFormatter
import spray.http.ContentTypes.`application/json`
import spray.http.{HttpCharsets, HttpEntity, MediaTypes}
import spray.httpx.marshalling.Marshaller
import spray.httpx.unmarshalling.Unmarshaller

object JacksonJsonSupport {

  val jacksonModules = Seq(DefaultScalaModule)
  val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModules(jacksonModules: _*)

  implicit def jacksonJsonUnmarshaller[T : Manifest] =
    Unmarshaller[T](MediaTypes.`application/json`) {
      case x: HttpEntity.NonEmpty =>
        val jsonSource = x.asString(defaultCharset = HttpCharsets.`UTF-8`)
        mapper.readValue[T](jsonSource)
    }

  implicit def jacksonJsonMarshaller[T <: AnyRef] =
    Marshaller.
      delegate[T, String](`application/json`)(mapper.writeValueAsString(_))


  implicit val byteStringFormatter =
    new ByteStringFormatter[Seq[MetricConfiguration]] {
      def serialize(data: Seq[MetricConfiguration]): ByteString = {
        ByteString(mapper.writeValueAsBytes(data))
      }

      def deserialize(bs: ByteString): Seq[MetricConfiguration] = {
        val typeReference = mapper.getTypeFactory.constructCollectionLikeType(
          classOf[List[MetricConfiguration]],
          classOf[MetricConfiguration]
        )

        mapper.readValue(bs.toArray, typeReference)
      }
    }
}
