package mdcbot.command

import com.jagrosh.jdautilities.commandclient.CommandEvent
import mdcbot.utils.Util
import mdcbot.utils.rulesJsonHandler

class CommandRules : CommandBase("rules", "Use this command to check or modify the list of rules."){
    override fun doCommand(event: CommandEvent?) {
        val args = Util.splitCommandArgs(event!!.args)
        args.forEach(::println)
        if(args.size == 2) {
            System.out.println("Args size is 2")
            if(args[0] == "add"){
                Rules.rules.add(args[1])
                Rules.update()
            }else if(args[0] == "remove" || args[0] == "rm"){
                if(args[1].toInt() != 0){
                
                }
            }
        }
    }
}

object Rules{
    val rules = ArrayList<String>()
    
    fun update(){
        println("Updating rules in json.")
        rules.forEach(::println)
        rulesJsonHandler.writeToJson(this)
    }
}