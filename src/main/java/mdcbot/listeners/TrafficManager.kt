package mdcbot.listeners

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import java.util.*
import mdcbot.DatedUser
import mdcbot.MDCBot
import mdcbot.points.*
import net.dv8tion.jda.core.Permission
import mdcbot.utils.trafficJsonHandler
import java.time.temporal.ChronoUnit
import java.time.Instant
import mdcbot.MDCBot.trafficManager

data class PlayerTrafficData(
        val list: ArrayList<DatedPlayer>,
        val players: Int = list.size,
        var messages: Int = 0,
        var ratio: Float = -1F,
        var date: Date = Date(),
        var dataChanged: Boolean = false)

class TrafficManager : ListenerAdapter(){
    val datedPlayers = ArrayList<DatedPlayer>()
    var trafficData = PlayerTrafficData(datedPlayers)
    val messages = ArrayList<PlayerMessage>()
    
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent?) {
        if (event!!.author.isBot)
            return
        
        val channel = event.channel
        //Make sure the channel isn't restricted to certain roles (i.e. @everyone can read and write on this channel)
        for (perm in channel.rolePermissionOverrides) {
            if (perm.isRoleOverride && perm.role.isPublicRole) {
                val deniedPerms = perm.denied
                if (deniedPerms.contains(Permission.MESSAGE_READ) || deniedPerms.contains(Permission.MESSAGE_WRITE))
                    return
            }
        }
        
        //Record this message
        val messageTimestamp = Date(event.message.creationTime.toEpochSecond() * 1000)
        //debug("Adding message: %s -> '%s'", messageTimestamp, event.getMessage().getContent());
        if(checkForPlayer(event.author)){
            val datedUser = DatedUser(event.author, messageTimestamp)
            val player = getPlayerWithUser(event.author)
            datedPlayers.add(DatedPlayer(datedUser, player!!))
            val message = PlayerMessage(player, messageTimestamp, event.message)
            messages.add(message)
        }
        
        checkData()
        
        trafficData.dataChanged = true
    }
}
private fun checkData() {
    //Get a Date that is 24 hours before now
    val minInstant = Instant.now().minus(24, ChronoUnit.HOURS)
    val minDate = Date(minInstant.toEpochMilli())
    
    //Remove any users that are older than 24 hours
    val playersIterator = trafficManager.datedPlayers.iterator()
    while (playersIterator.hasNext())
        if (playersIterator.next().datedUser.date.before(minDate))
            playersIterator.remove()
    
    //Remove any messages that are older than 24 hours
    val messagesIterator = trafficManager.messages.iterator()
    while (messagesIterator.hasNext()) {
        val message = messagesIterator.next()
        if (message.date.before(minDate))
            messagesIterator.remove()
        else
            break
    }
    
    //Refresh max ratio
    getRatio()
}

private fun getRatio(): Float{
    //For some reason, this is return an arithmatic exception with the trafficManager.datedPlayers.size, when there is????
    val lastRatio = (trafficManager.messages.size / trafficManager.datedPlayers.size).toFloat()
    updateMaxRatio(lastRatio)
    return lastRatio
}

var maxRatio: Float = 0.0f
var maxRatioDateMin: Date? = null
var maxRatioDateMax: Date? = null

private fun updateMaxRatio(ratio: Float){
    if (ratio > maxRatio) {
        maxRatio = ratio
        maxRatioDateMin = trafficManager.messages[0].date
        maxRatioDateMax = trafficManager.messages[trafficManager.messages.size - 1].date
        trafficManager.trafficData.ratio = ratio
    }
}

fun readFromFiles(){
    MDCBot.trafficManager.trafficData = trafficJsonHandler.readFromJson()
}

fun saveToFiles() {
    trafficManager.trafficData.dataChanged = false
    trafficJsonHandler.writeToJson(trafficManager.trafficData)
}
