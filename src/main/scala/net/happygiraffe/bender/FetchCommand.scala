package net.happygiraffe.bender

object FetchCommand extends Command {
  def getDescription() : String =
    "go and fetch all the feeds being monitored"

  def respond(bot: Bender, channel: String, args: String) : Iterable[String] = {
    bot.feeder ! FetchFeeds
    return List()
  }
}
