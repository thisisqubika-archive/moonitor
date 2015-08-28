package com.mooveit.moonitor.principal.actors

import javax.mail.internet.InternetAddress

import akka.actor.Actor
import com.mooveit.moonitor.domain.alerts.AlertConfiguration
import com.mooveit.moonitor.principal.Main
import com.mooveit.moonitor.principal.actors.MailInformer.Alert
import courier._

class MailInformer extends Actor {

  import context.dispatcher

  val config = Main.config
  val from = config.getString("smtp.user")

  private val mailer =
    Mailer(config.getString("smtp.host"), config.getInt("smtp.port"))
      .auth(config.getBoolean("smtp.auth"))
      .as(from, config.getString("smtp.pass"))
      .startTtls(config.getBoolean("smtp.use_tls"))()

  override def receive = {
    case Alert(host, aconf) =>
      mailer(
        Envelope.from(new InternetAddress(from))
          .to(new InternetAddress(aconf.mailTo))
          .subject(s"$host: ${aconf.metricId.toString} ${aconf.operator} ${aconf.value}")
      )
  }
}

object MailInformer {

  case class Alert(host: String, aconf: AlertConfiguration)
}
