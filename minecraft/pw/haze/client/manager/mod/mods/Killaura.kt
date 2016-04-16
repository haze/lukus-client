package pw.haze.client.manager.mod.mods

import com.google.common.base.Predicate
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.CPacketUseEntity
import net.minecraft.util.EnumHand
import net.minecraft.util.math.MathHelper
import pw.haze.client.events.EnumMotion
import pw.haze.client.events.EventMotion
import pw.haze.client.manager.mod.Category
import pw.haze.client.manager.mod.ToggleableMod
import pw.haze.client.manager.mod.interfaces.Displayable
import pw.haze.client.netHandler
import pw.haze.client.player
import pw.haze.client.playerCont
import pw.haze.client.util.Timer
import pw.haze.client.world
import pw.haze.event.annotation.EventMethod
import java.util.*

/**
 * |> Author: haze
 * |> Since: 4/12/16
 */
class Killaura: ToggleableMod("Killaura", "Kill entities nearby", "K", Category.COMBAT), Displayable {


    var target: Optional<EntityLivingBase> = Optional.empty()
    var entites: ArrayList<EntityLivingBase> = arrayListOf()
    val angleTimer: Timer = Timer()

    override fun getColor(): Int {
        return 0xFFff4040.toInt()
    }

    val filter: Predicate<EntityLivingBase> = Predicate {
        e ->
           !e.isDead
        && !e.isInvisible
        && player.getDistanceToEntity(e) < 4
        && e != player
    //    && !this.entites.contains(e)
    }

    fun gather(){
        this.entites = world.getEntities(EntityLivingBase::class.java, filter) as ArrayList<EntityLivingBase>
    }

    @EventMethod(EventMotion::class) fun onMotion(motion: EventMotion) {
        println( player.func_184825_o(1F) )
        println( player.field_184617_aD)
        println()
        when (motion.enum) {
            EnumMotion.PRE -> {
                gather()
                if (angleTimer.elapsed(150) && !this.entites.isEmpty() && !this.target.isPresent) {
                    this.target = Optional.of(this.entites.last())
                    angleTimer.reset()
                }
                if (this.target.isPresent) {
                    val rotations = angles(this.target.get())
                    motion.yaw = rotations[0]
                    motion.pitch = rotations[1]
                }
            }

            EnumMotion.POST -> {
                if(this.target.isPresent && player.func_184825_o(1F) >= 1F && Random().nextInt(5) + player.field_184617_aD > 12){
                    attack(this.target.get())
                    this.target = Optional.empty()
                }
            }
        }
    }

    private fun attack(target: EntityLivingBase) {
        player.swingArm(EnumHand.MAIN_HAND)
        playerCont.attackEntity(player, target)
        /* playerCont.syncCurrentPlayItem()
        netHandler.addToSendQueue(CPacketUseEntity(target, EnumHand.MAIN_HAND))
         */
    }

    fun angles(target: EntityLivingBase): Array<Float> {
        val x: Double = target.posX - player.posX
        val y: Double = (target.entityBoundingBox.maxY - 0.6) - player.entityBoundingBox.maxY - 0.4
        val z: Double = target.posZ - player.posZ
        val distance = MathHelper.sqrt_double(x * x + z * z).toDouble()
        return arrayOf(((Math.atan2(z, x) * 180F / Math.PI) - 90F).toFloat(), -((Math.atan2(y, distance) * 180F / Math.PI) - 90F).toFloat())
    }

    override fun onDisable(){
        this.target = Optional.empty()
        this.entites.clear()
        super.onDisable()
    }


}