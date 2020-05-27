package ca.landonjw.remoraids.implementation.ui;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.internal.inventory.api.*;
import com.pixelmonmod.pixelmon.config.PixelmonBlocks;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.items.ItemPixelmonSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class GUIBossOptions {

    private EntityPlayerMP player;
    private IBossEntity bossEntity;

    public GUIBossOptions(EntityPlayerMP player, IBossEntity bossEntity){
        this.player = player;
        this.bossEntity = bossEntity;
    }

    public void open() {
        Button blueFiller = Button.builder()
                .item(new ItemStack(Blocks.STAINED_GLASS_PANE, 1, EnumDyeColor.LIGHT_BLUE.getMetadata()))
                .displayName("")
                .build();

        Button whiteFiller = Button.builder()
                .item(new ItemStack(Blocks.STAINED_GLASS_PANE, 1, EnumDyeColor.WHITE.getMetadata()))
                .displayName("")
                .build();

        Button bossButton = Button.builder()
                .item(ItemPixelmonSprite.getPhoto(bossEntity.getBoss().getPokemon()))
                .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Boss " + bossEntity.getBoss().getPokemon().getSpecies().name)
                .lore(UIUtils.getBossLore(bossEntity))
                .build();

        Button teleport = Button.builder()
                .item(new ItemStack(Items.ENDER_PEARL))
                .displayName(TextFormatting.BLUE + "" + TextFormatting.BOLD + "Teleport")
                .onClick(() -> {
                    InventoryAPI.getInstance().closePlayerInventory(player);
                    player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Teleport to boss..."));

                    Entity bossStatue = bossEntity.getEntity();
                    World bossWorld = bossStatue.getEntityWorld();
                    if(player.dimension != bossWorld.provider.getDimension()){
                        PlayerList playerList = player.getServer().getPlayerList();
                        Teleporter teleporter = ((WorldServer) bossWorld).getDefaultTeleporter();
                        playerList.transferPlayerToDimension(player, bossWorld.provider.getDimension(), teleporter);
                    }
                    player.setPositionAndUpdate(bossStatue.posX, bossStatue.posY, bossStatue.posZ);
                })
                .build();

        Button delete = Button.builder()
                .item(new ItemStack(PixelmonBlocks.trashcanBlock))
                .displayName(TextFormatting.RED + "" + TextFormatting.BOLD + "Delete Boss")
                .onClick(() -> {
                    bossEntity.despawn();
                    InventoryAPI.getInstance().closePlayerInventory(player);
                    player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Boss Pokemon deleted."));
                })
                .build();

        Template template = Template.builder(5)
                .line(LineType.Horizontal, 1, 0, 9, whiteFiller)
                .line(LineType.Horizontal, 3, 0, 9, whiteFiller)
                .border(0,0, 5,9, blueFiller)
                .set(0, 4, bossButton)
                .set(2, 5, delete)
                .set(2, 3, teleport)
                .build();

        Page page = Page.builder()
                .template(template)
                .title(TextFormatting.BLUE + "" + TextFormatting.BOLD + "Boss Editor")
                .build();

        page.forceOpenPage(player);
    }

}
