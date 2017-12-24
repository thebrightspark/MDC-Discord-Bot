package mdcbot.points

import com.fasterxml.jackson.databind.JsonNode
import mdcbot.DatedUser
import mdcbot.listeners.PlayerTrafficData
import mdcbot.utils.*
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.User
import java.util.*

var players = ArrayList<Player>()

fun initPlayers(){
//    val objs = getListOfObjectsFromJson()
//    if(objs != null) {
//        if (objs.size() > 0) {
//            for (node in objs) {
//                players.add(getPlayerFromNode(node))
//            }
//        }
//    }
}

data class Player(val user: User?, var points: Int)

data class DatedPlayer(val datedUser: DatedUser, val player: Player)

data class PlayerMessage(val player: Player, val date: Date, val message: Message)

fun readPlayerFromJson(name: String): Player?{
//    val data = readFromJson(Player::class.java)
//    if(data.user?.name == name){
//        return data
//    }
    return null
}

fun readPlayerFromJson(user: User): Player?{
    return readPlayerFromJson(user.name)
}

fun getPlayerFromNode(node: JsonNode): Player{
    return objmapper.treeToValue(node, Player::class.java)
}

fun writePlayerToJson(player: Player){
    var player1 = readPlayerFromJson(player.user!!)
    if(player1 != null){
        player1 = Player(player.user, player.points)
        refreshQueue + player1
    }
}

fun checkForPlayer(user: User): Boolean{
    if(getPlayerWithUser(user) != null){
        return true
    }
    return false
}

fun getPlayerWithUser(user: User): Player?{
    for(player in players){
        if(player.user == user) return player
    }
    return null
}

fun syncPointData(){
    for(player: Player in players){
        val playr = readPlayerFromJson(player.user!!)
        if(playr != null){
            if(player.points != playr.points){
                writePlayerToJson(player)
            }
        }
    }
}
