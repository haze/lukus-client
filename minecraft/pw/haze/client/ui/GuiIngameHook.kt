package pw.haze.client.ui

import net.minecraft.client.gui.GuiIngame
import net.minecraft.client.renderer.GlStateManager
import pw.haze.client.*
import pw.haze.client.events.EventHUDRender
import pw.haze.client.manager.mod.ToggleableMod
import pw.haze.client.manager.mod.interfaces.Displayable
import java.text.SimpleDateFormat
import java.util.*

/**
 * |> Author: haze
 * |> Since: 4/7/16
 */

class GuiIngameHook(): GuiIngame(minecraft) {
    override fun renderGameOverlay(partialTicks: Float) {
        super.renderGameOverlay(partialTicks)
        if(!gameSettings.showDebugInfo) {
            GlStateManager.enableBlend()
            drawTag()
            drawArrayList()
            eventManager.fire(EventHUDRender())
            GlStateManager.disableBlend()
        }
     }

    fun drawTag(){
        fontRend.drawStringWithShadow(SimpleDateFormat("h:mm a").format(Date()), 2F, 2F, 0xA6EEEEEE.toInt())
    }


    fun drawArrayList(){
        var initialY = 0.5F;
        modManager.contents.asSequence()
                .filter { mod -> mod is ToggleableMod && mod is Displayable }
                .map { mod -> mod as ToggleableMod }
                .filter { mod -> mod.state }
            .forEach {
                mod ->
                initialY += fontRenderer.FONT_HEIGHT + 2
                fontRenderer.drawStringWithShadow(mod.name, 2F, initialY, (mod as Displayable).getColor())
            }
    }
}