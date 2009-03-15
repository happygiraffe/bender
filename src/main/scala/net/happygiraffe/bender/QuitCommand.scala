package net.happygiraffe.bender

object QuitCommand extends Command {
  def getDescription() : String =
    "stop the bot"
    
  def respond(bot: Bender, args: String) : List[String] = {
    bot.quitServer("bye bye, suckers!");
    return List()
  }
}