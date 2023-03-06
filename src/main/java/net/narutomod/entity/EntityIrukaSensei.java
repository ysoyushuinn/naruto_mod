
package net.narutomod.entity;

import net.minecraft.village.Village;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.inventory.EntityEquipmentSlot;

import net.narutomod.item.ItemBijuMap;
import net.narutomod.item.ItemScrollBodyReplacement;
import net.narutomod.item.ItemScrollKageBunshin;
import net.narutomod.item.ItemKunai;
import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityIrukaSensei extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 166;
	public static final int ENTITYID_RANGED = 167;

	public EntityIrukaSensei(ElementsNarutomodMod instance) {
		super(instance, 438);
	}

	@Override
	public void initElements() {
		elements.entities
.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
		 .id(new ResourceLocation("narutomod", "iruka_sensei"), ENTITYID)
		 .name("iruka_sensei").tracker(64, 3, true).egg(-16751104, -6711040).build());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		Biome[] spawnBiomes = {Biomes.FOREST, Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.FOREST_HILLS,
		 Biomes.JUNGLE, Biomes.JUNGLE_EDGE, Biomes.JUNGLE_HILLS, Biomes.PLAINS, Biomes.ROOFED_FOREST, Biomes.TAIGA, Biomes.TAIGA_HILLS};
		EntityRegistry.addSpawn(EntityCustom.class, 20, 1, 1, EnumCreatureType.AMBIENT, spawnBiomes);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
			return new RenderLiving(renderManager, new ModelBiped64(), 0.5f) {
				protected ResourceLocation getEntityTexture(Entity entity) {
					return new ResourceLocation("narutomod:textures/iruka64x64.png");
				}
			};
		});
	}

	public static class EntityCustom extends EntityNinjaMerchant.Base {
		private static final MerchantRecipeList[] trades = { new MerchantRecipeList(), new MerchantRecipeList(), new MerchantRecipeList() };

		static {
			trades[0].add(new MerchantRecipe(new ItemStack(Items.EMERALD, 3), ItemStack.EMPTY, new ItemStack(Items.GOLDEN_APPLE, 1), 0, 1));
			trades[0].add(new MerchantRecipe(new ItemStack(Items.EMERALD, 1), ItemStack.EMPTY, new ItemStack(ItemScrollBodyReplacement.block, 1), 0, 1));
			trades[1].add(new MerchantRecipe(new ItemStack(Items.EMERALD, 5), ItemStack.EMPTY, new ItemStack(ItemScrollKageBunshin.block, 1), 0, 1));
			trades[1].add(new MerchantRecipe(new ItemStack(Items.EMERALD, 30), ItemStack.EMPTY, new ItemStack(Items.GOLDEN_APPLE, 1, 1), 0, 1));
			trades[2].add(new MerchantRecipe(new ItemStack(Items.EMERALD, 64), ItemStack.EMPTY, new ItemStack(ItemBijuMap.block, 1), 0, 1));
		};

		public EntityCustom(World world) {
			super(world, 20, trades);
			this.setSize(0.6f, 2.0f);
		}

		@Override
		public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
			this.setItemToInventory(new ItemStack(ItemKunai.block), 0);
			return super.onInitialSpawn(difficulty, livingdata);
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityZombie.class, false, false));
		}

		@Override
		protected Item getDropItem() {
			return null;
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5D);
		}
		
		@Override
		public boolean isOnSameTeam(Entity entityIn) {
			return EntityNinjaMob.TeamKonoha.contains(entityIn.getClass());
		}

		@Override
		protected int getTradeLevel(EntityPlayer player) {
			Village village = this.getVillage();
			return village != null ? village.getPlayerReputation(player.getUniqueID()) / 3 : 0;
		}

		@Override
		public boolean getCanSpawnHere() {
			return super.getCanSpawnHere() 
			 && this.world.getEntitiesWithinAABB(EntityCustom.class, this.getEntityBoundingBox().grow(128d, 16d, 128d)).isEmpty();
		}

		//@Override
		//public void setAttackTarget(@Nullable EntityLivingBase entityIn) {
		//	super.setAttackTarget(entityIn);
		//	this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, entityIn == null ? ItemStack.EMPTY : new ItemStack(ItemKunai.block, 1));
		//}
	}

	// Made with Blockbench 3.7.4
	// Exported for Minecraft version 1.12
	// Paste this class into your mod and generate all required imports
	@SideOnly(Side.CLIENT)
	public class ModelBiped64 extends ModelBiped {
		public ModelBiped64() {
			this.textureWidth = 64;
			this.textureHeight = 64;
			this.leftArmPose = ModelBiped.ArmPose.EMPTY;
			this.rightArmPose = ModelBiped.ArmPose.EMPTY;
			this.bipedHead = new ModelRenderer(this);
			this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
			this.bipedHead.cubeList.add(new ModelBox(this.bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
			this.bipedHead.cubeList.add(new ModelBox(this.bipedHead, 24, 0, -2.0F, -10.0F, 3.0F, 4, 4, 4, 0.0F, false));
			this.bipedHeadwear = new ModelRenderer(this);
			this.bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
			this.bipedHeadwear.cubeList.add(new ModelBox(this.bipedHeadwear, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.25F, false));
			this.bipedBody = new ModelRenderer(this);
			this.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
			this.bipedBody.cubeList.add(new ModelBox(this.bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));
			this.bipedBody.cubeList.add(new ModelBox(this.bipedBody, 16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.25F, false));
			this.bipedRightArm = new ModelRenderer(this);
			this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
			this.bipedRightArm.cubeList.add(new ModelBox(this.bipedRightArm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));
			this.bipedRightArm.cubeList.add(new ModelBox(this.bipedRightArm, 40, 32, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.25F, false));
			this.bipedLeftArm = new ModelRenderer(this);
			this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
			this.bipedLeftArm.cubeList.add(new ModelBox(this.bipedLeftArm, 32, 48, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));
			this.bipedLeftArm.cubeList.add(new ModelBox(this.bipedLeftArm, 48, 48, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.25F, false));
			this.bipedRightLeg = new ModelRenderer(this);
			this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
			this.bipedRightLeg.cubeList.add(new ModelBox(this.bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
			this.bipedRightLeg.cubeList.add(new ModelBox(this.bipedRightLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, false));
			this.bipedLeftLeg = new ModelRenderer(this);
			this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
			this.bipedLeftLeg.cubeList.add(new ModelBox(this.bipedLeftLeg, 16, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
			this.bipedLeftLeg.cubeList.add(new ModelBox(this.bipedLeftLeg, 0, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, false));
		}
	}
}
