package net.happygiraffe.bender

trait Command {
  def getDescription() : String
  def respond(bot: Bender, channel: String, args: String) : Iterable[String]
}