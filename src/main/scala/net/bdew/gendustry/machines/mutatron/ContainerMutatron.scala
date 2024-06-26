/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines.mutatron

import net.bdew.lib.data.base.ContainerDataSlots
import net.bdew.lib.gui.{BaseContainer, SlotValidating}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack

class ContainerMutatron(val te: TileMutatron, player: EntityPlayer)
    extends BaseContainer(te)
    with ContainerDataSlots {
  lazy val dataSource = te

  addSlotToContainer(new SlotValidating(te, 0, 60, 30))
  addSlotToContainer(new SlotValidating(te, 1, 60, 53))
  addSlotToContainer(new SlotValidating(te, 2, 142, 41))
  addSlotToContainer(new SlotValidating(te, 3, 98, 17))
  bindPlayerInventory(player.inventory, 8, 84, 142)

  te.lastPlayer := player.getGameProfile

  override def slotClick(
      slotNum: Int,
      button: Int,
      modifiers: Int,
      player: EntityPlayer
  ): ItemStack = {
    te.lastPlayer := player.getGameProfile
    super.slotClick(slotNum, button, modifiers, player)
  }
}
