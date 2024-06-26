/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines.sampler

import forestry.api.genetics.AlleleManager
import net.bdew.gendustry.apiimpl.TileWorker
import net.bdew.gendustry.config.Items
import net.bdew.gendustry.forestry.GeneSampleInfo
import net.bdew.gendustry.items.GeneSample
import net.bdew.gendustry.power.TilePowered
import net.bdew.lib.block.TileKeepData
import net.bdew.lib.covers.TileCoverable
import net.bdew.lib.power.TileItemProcessor
import net.minecraft.item.ItemStack
import net.minecraftforge.common.util.ForgeDirection

import scala.util.Random

class TileSampler
    extends TileItemProcessor
    with TileWorker
    with TilePowered
    with TileCoverable
    with TileKeepData {
  lazy val cfg = MachineSampler
  val outputSlots = Seq(slots.outSample)

  object slots {
    val inSampleBlank = 0
    val inLabware = 1
    val inIndividual = 2
    val outSample = 3
  }

  def getSizeInventory = 4

  def selectRandomAllele(stack: ItemStack): ItemStack = {
    val root = AlleleManager.alleleRegistry.getSpeciesRoot(stack)
    if (root == null) return new ItemStack(Items.waste)
    val member = root.getMember(stack)
    val genome = member.getGenome
    val chromosomes = genome.getChromosomes.zipWithIndex.filter(_._1 != null)
    val alleles = chromosomes.flatMap { case (x, n) =>
      Seq(n -> x.getPrimaryAllele, n -> x.getSecondaryAllele)
    }

    val rand = new Random()
    val (chr, allele) = alleles(rand.nextInt(alleles.length))
    return GeneSample.newStack(GeneSampleInfo(root, chr, allele))
  }

  def canStart =
    getStackInSlot(slots.inSampleBlank) != null &&
      getStackInSlot(slots.inLabware) != null &&
      getStackInSlot(slots.inIndividual) != null

  def tryStart(): Boolean = {
    if (canStart) {
      output := selectRandomAllele(getStackInSlot(slots.inIndividual))
      decrStackSize(slots.inSampleBlank, 1)
      decrStackSize(slots.inIndividual, 1)
      if (worldObj.rand.nextInt(100) < cfg.labwareConsumeChance)
        decrStackSize(slots.inLabware, 1)

      return true
    } else return false
  }

  override def isItemValidForSlot(slot: Int, stack: ItemStack): Boolean = {
    if (stack == null || stack.getItem == null) return false
    slot match {
      case slots.inSampleBlank =>
        return stack.getItem == Items.geneSampleBlank
      case slots.inLabware =>
        return stack.getItem == Items.labware
      case slots.inIndividual =>
        return AlleleManager.alleleRegistry.getIndividual(stack) != null
      case _ =>
        return false
    }
  }

  allowSided = true
  override def canExtractItem(slot: Int, item: ItemStack, side: Int) =
    slot == slots.outSample

  override def isValidCover(side: ForgeDirection, cover: ItemStack) = true
}
