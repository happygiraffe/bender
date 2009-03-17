package net.happygiraffe.bender

trait Command {
  def getDescription() : String
  def respond(bot: Bender, args: String) : Iterable[String]
}