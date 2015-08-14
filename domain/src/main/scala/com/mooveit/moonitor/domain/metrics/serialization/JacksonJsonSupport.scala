package com.mooveit.moonitor.domain.metrics.serialization

import java.io.{ObjectInputStream, ByteArrayInputStream, ByteArrayOutputStream, ObjectOutputStream}

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


  implicit val metricConfigurationFormatter =
    new ByteStringFormatter[MetricConfiguration] {
      def serialize(data: MetricConfiguration): ByteString = {
        ByteString(mapper.writeValueAsBytes(data))
      }

      def deserialize(bs: ByteString): MetricConfiguration = {
        mapper.readValue(bs.toArray, classOf[MetricConfiguration])
      }
    }

  implicit val booleanFormatter =
    new ByteStringFormatter[Boolean] {
      override def serialize(data: Boolean) =
        ByteString(mapper.writeValueAsBytes(data))

      override def deserialize(bs: ByteString) =
        mapper.readValue(bs.toArray, classOf[Boolean])
    }
}
