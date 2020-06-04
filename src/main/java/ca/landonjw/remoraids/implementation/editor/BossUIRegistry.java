package ca.landonjw.remoraids.implementation.editor;

import ca.landonjw.remoraids.api.battles.IBattleRestraint;
import ca.landonjw.remoraids.api.editor.IBossUI;
import ca.landonjw.remoraids.api.editor.IBossUIRegistry;
import ca.landonjw.remoraids.api.editor.ICreatorUI;
import ca.landonjw.remoraids.api.editor.IEditorUI;
import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;
import ca.landonjw.remoraids.implementation.rewards.KillerReward;
import ca.landonjw.remoraids.implementation.rewards.ParticipationReward;
import ca.landonjw.remoraids.implementation.rewards.TopDamageReward;
import ca.landonjw.remoraids.implementation.rewards.contents.CommandContent;
import ca.landonjw.remoraids.implementation.rewards.contents.CurrencyContent;
import ca.landonjw.remoraids.implementation.rewards.contents.ItemContent;
import ca.landonjw.remoraids.implementation.rewards.contents.PokemonContent;
import ca.landonjw.remoraids.implementation.rewards.contents.creators.CommandContentCreator;
import ca.landonjw.remoraids.implementation.rewards.contents.creators.CurrencyContentCreator;
import ca.landonjw.remoraids.implementation.rewards.contents.creators.ItemContentCreator;
import ca.landonjw.remoraids.implementation.rewards.contents.creators.PokemonContentCreator;
import ca.landonjw.remoraids.implementation.rewards.creators.KillerRewardCreator;
import ca.landonjw.remoraids.implementation.rewards.creators.ParticipationRewardCreator;
import ca.landonjw.remoraids.implementation.rewards.creators.TopDamageRewardCreator;

import java.util.*;

public class BossUIRegistry implements IBossUIRegistry {

    private static BossUIRegistry instance;

    private Map<Class<? extends IReward>, IEditorUI<? extends IReward>> rewardEditors = new HashMap<>();
    private Map<Class<? extends IReward>, ICreatorUI> rewardCreators = new HashMap<>();
    private Map<Class<? extends IRewardContent>, IEditorUI<? extends IRewardContent>> rewardContentEditors = new HashMap<>();
    private Map<Class<? extends IRewardContent>, ICreatorUI> rewardContentCreators = new HashMap<>();
    private Map<Class<? extends IBattleRestraint>, IEditorUI<? extends IBattleRestraint>> restraintEditors = new HashMap<>();
    private Map<Class<? extends IBattleRestraint>, ICreatorUI> restraintCreators = new HashMap<>();

    private BossUIRegistry(){
        registerRewardCreator(ParticipationReward.class, new ParticipationRewardCreator());
        registerRewardCreator(TopDamageReward.class, new TopDamageRewardCreator());
        registerRewardCreator(KillerReward.class, new KillerRewardCreator());
        registerRewardContentCreator(CommandContent.class, new CommandContentCreator());
        registerRewardContentCreator(CurrencyContent.class, new CurrencyContentCreator());
        registerRewardContentCreator(ItemContent.class, new ItemContentCreator());
        registerRewardContentCreator(PokemonContent.class, new PokemonContentCreator());
    }

    /** {@inheritDoc} */
    @Override
    public <T extends IReward> void registerRewardEditor(Class<T> clazz, IEditorUI<T> editor) {
        rewardEditors.put(clazz, editor);
    }

    /** {@inheritDoc} */
    @Override
    public <T extends IReward> void registerRewardCreator(Class<T> clazz, ICreatorUI creator) {
        rewardCreators.put(clazz, creator);
    }

    /** {@inheritDoc} */
    @Override
    public <T extends IReward> Optional<IEditorUI<T>> getRewardEditor(Class<T> editor) {
        return Optional.ofNullable((IEditorUI<T>) rewardEditors.get(editor));
    }

    /** {@inheritDoc} */
    @Override
    public Optional<ICreatorUI> getRewardCreator(Class<? extends IReward> creator) {
        return Optional.ofNullable(rewardCreators.get(creator));
    }

    @Override
    public List<ICreatorUI> getRewardCreators() {
        return new ArrayList<>(rewardCreators.values());
    }

    /** {@inheritDoc} */
    @Override
    public <T extends IRewardContent> void registerRewardContentEditor(Class<T> clazz, IEditorUI<T> editor) {
        rewardContentEditors.put(clazz, editor);
    }

    /** {@inheritDoc} */
    @Override
    public <T extends IRewardContent> void registerRewardContentCreator(Class<T> clazz, ICreatorUI creator) {
        rewardContentCreators.put(clazz, creator);
    }

    /** {@inheritDoc} */
    @Override
    public <T extends IRewardContent> Optional<IEditorUI<T>> getRewardContentEditor(Class<T> editor) {
        return Optional.ofNullable((IEditorUI<T>) rewardContentEditors.get(editor));
    }

    /** {@inheritDoc} */
    @Override
    public Optional<ICreatorUI> getRewardContentCreator(Class<? extends IRewardContent> creator) {
        return Optional.ofNullable(rewardContentCreators.get(creator));
    }

    @Override
    public List<ICreatorUI> getRewardContentCreators() {
        return new ArrayList<>(rewardContentCreators.values());
    }

    /** {@inheritDoc} */
    @Override
    public <T extends IBattleRestraint> void registerRestraintEditor(Class<T> clazz, IEditorUI<T> editor) {
        restraintEditors.put(clazz, editor);
    }

    /** {@inheritDoc} */
    @Override
    public <T extends IBattleRestraint> void registerRestraintCreator(Class<T> clazz, ICreatorUI creator) {
        restraintCreators.put(clazz, creator);
    }

    /** {@inheritDoc} */
    @Override
    public <T extends IBattleRestraint> Optional<IEditorUI<T>> getRestraintEditor(Class<T> editor) {
        return Optional.ofNullable((IEditorUI<T>) restraintCreators.get(editor));
    }

    /** {@inheritDoc} */
    @Override
    public Optional<ICreatorUI> getRestraintCreator(Class<? extends IBossUI> creator) {
        return Optional.ofNullable(restraintCreators.get(creator));
    }

    @Override
    public List<ICreatorUI> getRestraintCreators() {
        return new ArrayList<>(restraintCreators.values());
    }

    public static BossUIRegistry getInstance() {
        if(instance == null){
            instance = new BossUIRegistry();
        }
        return instance;
    }
}
