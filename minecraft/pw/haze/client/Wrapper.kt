package pw.haze.client

import com.mojang.authlib.Agent
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.multiplayer.PlayerControllerMP
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.client.network.NetHandlerPlayClient
import net.minecraft.client.settings.GameSettings
import net.minecraft.util.Session
import org.lwjgl.util.Color
import pw.haze.client.manager.action.ActionManager
import pw.haze.client.manager.mod.ModManager
import pw.haze.event.Event
import pw.haze.event.EventManager
import java.io.File
import java.net.Proxy
import java.util.*

/**
 * |> Author: haze
 * |> Since: 4/7/16
 */

//TODO: EXTENSION STUFF

//TODO: CLIENT STUFF

val client: Client
    get() = Client.getInstance()

val folder: File
    get() = File(System.getProperty("user.home") + "/Videos/${client.info.name}/")

val actionManager: ActionManager = ActionManager.instance
val modManager: ModManager = ModManager.instance

val eventManager: EventManager
    get() = EventManager.getInstance()

fun fireEvent(event: Event) { eventManager.fire(event) }


//TODO: MINECRAFT STUFF

val minecraft: Minecraft
    get() = Minecraft.getMinecraft()

val gameSettings: GameSettings
    get() = minecraft.gameSettings

val player: EntityPlayerSP
    get() = minecraft.thePlayer

val world: WorldClient
    get() = minecraft.theWorld

val playerCont: PlayerControllerMP
    get() = minecraft.playerController

val netHandler: NetHandlerPlayClient
    get() = minecraft.netHandler

val fontRend: FontRenderer
    get() = minecraft.fontRendererObj


//TODO: STATIC METHODS

fun login(username: String, password: String){
    val service = YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString())
    YggdrasilUserAuthentication(service, Agent.MINECRAFT).apply {
        setUsername(username)
        setPassword(password)
    }.run {
        logIn()
        minecraft.session = Session(selectedProfile.name, selectedProfile.id.toString(), authenticatedToken, Session.Type.MOJANG.name)
    }
}