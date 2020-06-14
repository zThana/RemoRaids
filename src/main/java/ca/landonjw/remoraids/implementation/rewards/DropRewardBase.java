package ca.landonjw.remoraids.implementation.rewards;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.battles.IBossBattle;
import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;
import ca.landonjw.remoraids.api.services.messaging.IMessageService;
import ca.landonjw.remoraids.api.services.placeholders.IParsingContext;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import ca.landonjw.remoraids.internal.text.TextUtils;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.comm.ChatHandler;
import com.pixelmonmod.pixelmon.comm.packetHandlers.itemDrops.ItemDropMode;
import com.pixelmonmod.pixelmon.comm.packetHandlers.itemDrops.ItemDropPacket;
import com.pixelmonmod.pixelmon.entities.pixelmon.drops.DropItemQueryList;
import com.pixelmonmod.pixelmon.entities.pixelmon.drops.DroppedItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * A base for a {@link IReward} to be given in boss battles within the drop UI.
 *
 * @author landonjw
 * @since  1.0.0
 */
public abstract class DropRewardBase implements IReward {

    /** A list of all of the {@link IRewardContent} to be given from the reward. */
    protected List<IRewardContent> contents = new ArrayList<>();

    /**
     * Constructor for the reward base.
     *
     * @param contents the contents to be given from the reward. null contents are ignored.
     */
    public DropRewardBase(IRewardContent... contents){
        for (IRewardContent rewardContent : contents){
            if(rewardContent != null){
                this.contents.add(rewardContent);
            }
        }
    }

    /** {@inheritDoc} **/
    @Override
    public List<IRewardContent> getContents() {
        return contents;
    }

    /** {@inheritDoc} **/
    @Override
    public void addContents(IRewardContent... contents) {
        for (IRewardContent rewardContent : contents){
            if(rewardContent != null){
                this.contents.add(rewardContent);
            }
        }
    }

    /** {@inheritDoc} **/
    @Override
    public void removeContents(IRewardContent... contents) {
        this.contents.removeAll(Arrays.asList(contents));
    }

    /** {@inheritDoc} **/
    @Override
    public void clearContents() {
        contents.clear();
    }

    /** {@inheritDoc} */
    @Override
    public void distributeReward(IBossBattle battle) {
        for(EntityPlayerMP player : getWinnersList(battle)){
            Config config = RemoRaids.getMessageConfig();
            IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);
            IParsingContext context = IParsingContext.builder()
                    .add(EntityPlayerMP.class, () -> player)
                    .add(IReward.class, () -> this)
                    .build();
            ITextComponent rewardText = new TextComponentString(service.interpret(config.get(MessageConfig.REWARD_RECEIVED), context));

            rewardText = TextUtils.addCallback(rewardText, (sender) -> {
                RaidDropQuery rewardQuery = new RaidDropQuery(new Vec3d(0,0,0), player.getUniqueID(), this);
                DropItemQueryList.queryList.add(rewardQuery);


                TextComponentTranslation title = ChatHandler.getMessage(
                        "gui.guiItemDrops.beatPokemon",
                        "the raid boss " + TextFormatting.DARK_RED + "" + TextFormatting.BOLD
                                + battle.getBossEntity().getBoss().getPokemon().getDisplayName() + TextFormatting.RESET
                );

                ItemDropPacket packet = new ItemDropPacket(ItemDropMode.NormalPokemon, title, getDropItemList());
                Pixelmon.network.sendTo(packet, player);
            }, true);

            player.sendMessage(rewardText);
        }
    }

    /**
     * Gets a list of drop items to be used in the drop UI.
     *
     * @return a list of drop items corresponding to each {@link IRewardContent}.
     */
    public ArrayList<DroppedItem> getDropItemList(){
        ArrayList<DroppedItem> droppedItems = new ArrayList<>();
        int idCount = 0;

        for(IRewardContent content : contents){
            idCount++;
            droppedItems.add(new DroppedItem(content.toItemStack(), idCount));
        }

        return droppedItems;
    }

}
