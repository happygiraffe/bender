package net.happygiraffe.bender

import java.net.URI
import org.jibble.pircbot.PircBot
import scala.actors.Actor._
import scala.collection.mutable.Set
import scala.util.matching.Regex

class Bender extends PircBot {

  setName("bender")
  setEncoding("UTF-8")
  setFinger("Bite my shiny metal RSS!")

  // For managing feeds.
  val feeder = new Feeder(this)
  feeder.start()

  // For sending messages.
  val messenger = actor {
    loop {
      react {
        case ("message", wf: WatchedFeed, msg: String) =>
          sendMessage(wf.channel, msg)
      }
    }
  }

  val commands = Map[String, Command](
    "fetch"   -> FetchCommand,
    "help"    -> HelpCommand,
    "list"    -> ListCommand,
    "quit"    -> QuitCommand,
    "unwatch" -> UnwatchCommand,
    "watch"   -> WatchCommand
  )

  // My name, comma|colon, followed by a word and then anything else.
  val Cmd = new Regex("^(?:" + getName()
                      + ")[:,]?(?:\\s+(\\S+)(?:\\s+(.*))?)?$")

  private final val quotes = new Quotes()

  override def onMessage(channel: String, sender: String, login: String,
                         hostname: String, message: String) {
    // Does it look like this message is aimed at us?
    if ((Cmd findFirstIn message.trim()) == None)
      return
    // Funky regex matcher into variables.
    val Cmd(verb, args) = message.trim()
    commands.get(verb) match {
      // If we know about this command, execute it.
      case Some(action) => {
        for (line <- action.respond(this, channel, args))
          sendMessage(channel, line)
      }
      // If we've not heard of it, spit out something unhelpful.
      case None => {
        sendMessage(channel, quotes.getRandomQuote())
      }
    }
  }

  override def onDisconnect {
    exit();
  }
}
