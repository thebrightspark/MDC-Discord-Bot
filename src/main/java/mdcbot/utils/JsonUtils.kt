package mdcbot.utils

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import mdcbot.MDCBot
import mdcbot.command.Rules
import mdcbot.listeners.PlayerTrafficData
import mdcbot.listeners.readFromFiles
import mdcbot.listeners.saveToFiles
import java.awt.Color
import java.io.File

val objmapper: ObjectMapper = ObjectMapper()
val refreshQueue = ArrayList<Any>()

val rulesJsonHandler = RulesJsonHandler()
val trafficJsonHandler = TrafficJsonHandler()

abstract class JsonHandler<out T>(val file: File, private val objectType: Class<T>){
    protected var lastModified: Long = 0
    
    init{
        if(!file.exists()){
            if(!file.parentFile.exists()) {
                if (file.parentFile.mkdirs()) {
                    file.createNewFile()
                }
            }
        }
        objmapper.enable(SerializationFeature.INDENT_OUTPUT)
    }
    
    fun refreshJson(){
        executeWithRate(Runnable{
            for(pojo in refreshQueue){
                writeToJson(pojo)
            }
        }, 10)
    }
    
    fun writeToJson(obj: Any){
        objmapper.writeValue(file, obj)
    }
    
    fun readFromJson(): T{
        return objmapper.readValue(file, objectType)
    }
    
    fun getListOfObjectsFromJson(): JsonNode?{
        if(!file.exists()) return null
        return objmapper.readTree(file)
    }
    fun watchFileForChanges() {
        executeWithRate(Runnable{
            execute()
        }, 5)
    }
    
    abstract fun execute()
}

class RulesJsonHandler : JsonHandler<Rules>(MDCBot.RULES_FILE, Rules::class.java){
    override fun execute() {
        if(Rules.rules.isEmpty()) {
            getListOfObjectsFromJson()?.elements()?.forEach {
                for(obj in it){
                    Rules.rules.add(obj.asText())
                }
            }
        }
        if(Rules.changed) {
            if (file.lastModified() != lastModified) {
                val sb = StringBuilder()
                Rules.rules.forEach {
                    sb.append(it)
                    sb.append("\n")
                }
                val contents = sb.toString()
                MDCBot.users
                        .filterNot {
                            it.isBot
                        }
                        .forEach {
                            it.openPrivateChannel().queue { privateChannel ->
                                run {
                                    println("Sending message to: $it!")
                                    privateChannel.sendMessage(Util.createEmbedMessage(Color.MAGENTA, "Updated Rules", contents)).queue()
                                }
                            }
                        }
                lastModified = file.lastModified()
                Rules.changed = false
            }
        }
    }
}

class TrafficJsonHandler : JsonHandler<PlayerTrafficData>(MDCBot.TRAFFIC_FILE, PlayerTrafficData::class.java){
    override fun execute() {
        readFromFiles()
        if (MDCBot.trafficManager.trafficData.dataChanged)
            saveToFiles()
    }
}
