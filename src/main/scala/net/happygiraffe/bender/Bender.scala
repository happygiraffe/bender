package net.happygiraffe.bender

import org.jibble.pircbot.PircBot

class Bender extends PircBot {
  
  setName("bender")
  setEncoding("UTF-8")
  setFinger("Bite my shiny metal RSS!")

  override def onMessage(channel: String, sender: String, login: String,
                         hostname: String, message: String) {
    if (message == "!bender") {
      this.sendMessage(channel, "yeah?")
    }
  }

  override def onDisconnect {
    System.exit(0);
  }
}