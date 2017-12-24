package mdcbot.command

import com.jagrosh.jdautilities.commandclient.CommandEvent
import mdcbot.utils.Util
import mdcbot.utils.rulesJsonHandler

//In here, for some reason, the rules aren't actually being changed, according to the DM from the bot
class CommandRules : CommandBase("rules", "Use this command to check or modify the list of rules."){
    override fun doCommand(event: CommandEvent?) {
        val args = Util.splitCommandArgs(event!!.args)
        if(args.size == 2) {
            if(args[0] == "add"){
                Rules.add(args[0])
            }else if(args[0] == "remove" || args[0] == "rm"){
                val index: Int = args[1].toInt()
                if(Rules.rules[index].isNotBlank()){
                    Rules.remove(index)
                }
            }
        }
    }
}

object Rules{
    val rules = ArrayList<String>()
    internal var changed = false
    
    fun update(){
        println("Updating rules in json.")
        rules.forEach(::println)
        rulesJsonHandler.writeToJson(this)
    }
    
    fun add(rule: String){
        changed = true
        rules+rule
        update()
    }
    
    fun remove(index: Int){
        changed = true
        rules-(index-1)
        update()
    }
}
