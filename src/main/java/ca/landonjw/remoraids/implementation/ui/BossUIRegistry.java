package ca.landonjw.remoraids.implementation.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ca.landonjw.remoraids.api.battles.IBattleRestraint;
import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;
import ca.landonjw.remoraids.api.ui.IBossUIRegistry;
import ca.landonjw.remoraids.api.ui.ICreatorUI;
import ca.landonjw.remoraids.api.ui.IEditorUI;
import ca.landonjw.remoraids.implementation.battles.restraints.CapacityRestraint;
import ca.landonjw.remoraids.implementation.battles.restraints.CooldownRestraint;
import ca.landonjw.remoraids.implementation.battles.restraints.NoRebattleRestraint;
import ca.landonjw.remoraids.implementation.battles.restraints.SpeciesClauseRestraint;
import ca.landonjw.remoraids.implementation.rewards.contents.CommandContent;
import ca.landonjw.remoraids.implementation.rewards.contents.CurrencyContent;
import ca.landonjw.remoraids.implementation.rewards.contents.ItemContent;
import ca.landonjw.remoraids.implementation.rewards.contents.PokemonContent;
import ca.landonjw.remoraids.implementation.rewards.options.KillerReward;
import ca.landonjw.remoraids.implementation.rewards.options.ParticipationReward;
import ca.landonjw.remoraids.implementation.rewards.options.TopDamageReward;
import ca.landonjw.remoraids.implementation.ui.creators.CapacityRestraintCreator;
import ca.landonjw.remoraids.implementation.ui.creators.CommandContentCreator;
import ca.landonjw.remoraids.implementation.ui.creators.CooldownRestraintCreator;
import ca.landonjw.remoraids.implementation.ui.creators.CurrencyContentCreator;
import ca.landonjw.remoraids.implementation.ui.creators.ItemContentCreator;
import ca.landonjw.remoraids.implementation.ui.creators.KillerRewardCreator;
import ca.landonjw.remoraids.implementation.ui.creators.NoRebattleRestraintCreator;
import ca.landonjw.remoraids.implementation.ui.creators.ParticipationRewardCreator;
import ca.landonjw.remoraids.implementation.ui.creators.PokemonContentCreator;
import ca.landonjw.remoraids.implementation.ui.creators.SpeciesClauseRestraintCreator;
import ca.landonjw.remoraids.implementation.ui.creators.TopDamageRewardCreator;
import ca.landonjw.remoraids.implementation.ui.editors.CapacityRestraintEditor;
import ca.landonjw.remoraids.implementation.ui.editors.CommandContentEditor;
import ca.landonjw.remoraids.implementation.ui.editors.CooldownRestraintEditor;
import ca.landonjw.remoraids.implementation.ui.editors.CurrencyContentEditor;
import ca.landonjw.remoraids.implementation.ui.editors.ItemContentEditor;
import ca.landonjw.remoraids.implementation.ui.editors.KillerRewardEditor;
import ca.landonjw.remoraids.implementation.ui.editors.ParticipationRewardEditor;
import ca.landonjw.remoraids.implementation.ui.editors.PokemonContentEditor;
import ca.landonjw.remoraids.implementation.ui.editors.TopDamageRewardEditor;

@SuppressWarnings("unchecked")
public class BossUIRegistry implements IBossUIRegistry {

	private static BossUIRegistry instance;

	private Map<Class<? extends IReward>, IEditorUI<? extends IReward>> rewardEditors = new HashMap<>();
	private Map<Class<? extends IReward>, ICreatorUI<IReward>> rewardCreators = new HashMap<>();
	private Map<Class<? extends IRewardContent>, IEditorUI<? extends IRewardContent>> rewardContentEditors = new HashMap<>();
	private Map<Class<? extends IRewardContent>, ICreatorUI<IRewardContent>> rewardContentCreators = new HashMap<>();
	private Map<Class<? extends IBattleRestraint>, IEditorUI<? extends IBattleRestraint>> restraintEditors = new HashMap<>();
	private Map<Class<? extends IBattleRestraint>, ICreatorUI<IBattleRestraint>> restraintCreators = new HashMap<>();

	private BossUIRegistry() {
		registerRewardCreator(ParticipationReward.class, new ParticipationRewardCreator());
		registerRewardEditor(ParticipationReward.class, new ParticipationRewardEditor());
		registerRewardCreator(TopDamageReward.class, new TopDamageRewardCreator());
		registerRewardEditor(TopDamageReward.class, new TopDamageRewardEditor());
		registerRewardCreator(KillerReward.class, new KillerRewardCreator());
		registerRewardEditor(KillerReward.class, new KillerRewardEditor());

		registerRewardContentCreator(CommandContent.class, new CommandContentCreator());
		registerRewardContentEditor(CommandContent.class, new CommandContentEditor());
		registerRewardContentCreator(CurrencyContent.class, new CurrencyContentCreator());
		registerRewardContentEditor(CurrencyContent.class, new CurrencyContentEditor());
		registerRewardContentCreator(ItemContent.class, new ItemContentCreator());
		registerRewardContentEditor(ItemContent.class, new ItemContentEditor());
		registerRewardContentCreator(PokemonContent.class, new PokemonContentCreator());
		registerRewardContentEditor(PokemonContent.class, new PokemonContentEditor());

		registerRestraintCreator(CapacityRestraint.class, new CapacityRestraintCreator());
		registerRestraintEditor(CapacityRestraint.class, new CapacityRestraintEditor());
		registerRestraintCreator(CooldownRestraint.class, new CooldownRestraintCreator());
		registerRestraintEditor(CooldownRestraint.class, new CooldownRestraintEditor());
		registerRestraintCreator(NoRebattleRestraint.class, new NoRebattleRestraintCreator());
		registerRestraintCreator(SpeciesClauseRestraint.class, new SpeciesClauseRestraintCreator());
	}

	/** {@inheritDoc} */
	@Override
	public <T extends IReward> void registerRewardEditor(Class<T> clazz, IEditorUI<T> editor) {
		rewardEditors.put(clazz, editor);
	}

	/** {@inheritDoc} */
	@Override
	public void registerRewardCreator(Class<? extends IReward> clazz, ICreatorUI<IReward> creator) {
		rewardCreators.put(clazz, creator);
	}

	/** {@inheritDoc} */
	@Override
	public <T extends IReward> Optional<IEditorUI<T>> getRewardEditor(Class<T> editor) {
		return Optional.ofNullable((IEditorUI<T>) rewardEditors.get(editor));
	}

	/** {@inheritDoc} */
	@Override
	public Optional<ICreatorUI<IReward>> getRewardCreator(Class<? extends IReward> creator) {
		return Optional.ofNullable(rewardCreators.get(creator));
	}

	@Override
	public List<ICreatorUI<IReward>> getRewardCreators() {
		return new ArrayList<>(rewardCreators.values());
	}

	/** {@inheritDoc} */
	@Override
	public <T extends IRewardContent> void registerRewardContentEditor(Class<T> clazz, IEditorUI<T> editor) {
		rewardContentEditors.put(clazz, editor);
	}

	/** {@inheritDoc} */
	@Override
	public void registerRewardContentCreator(Class<? extends IRewardContent> clazz, ICreatorUI<IRewardContent> creator) {
		rewardContentCreators.put(clazz, creator);
	}

	/** {@inheritDoc} */
	@Override
	public <T extends IRewardContent> Optional<IEditorUI<T>> getRewardContentEditor(Class<T> editor) {
		return Optional.ofNullable((IEditorUI<T>) rewardContentEditors.get(editor));
	}

	/** {@inheritDoc} */
	@Override
	public Optional<ICreatorUI<IRewardContent>> getRewardContentCreator(Class<? extends IRewardContent> clazz) {
		return Optional.ofNullable(rewardContentCreators.get(clazz));
	}

	@Override
	public List<ICreatorUI<IRewardContent>> getRewardContentCreators() {
		return new ArrayList<>(rewardContentCreators.values());
	}

	/** {@inheritDoc} */
	@Override
	public <T extends IBattleRestraint> void registerRestraintEditor(Class<T> clazz, IEditorUI<T> editor) {
		restraintEditors.put(clazz, editor);
	}

	/** {@inheritDoc} */
	@Override
	public void registerRestraintCreator(Class<? extends IBattleRestraint> clazz, ICreatorUI<IBattleRestraint> creator) {
		restraintCreators.put(clazz, creator);
	}

	/** {@inheritDoc} */
	@Override
	public <T extends IBattleRestraint> Optional<IEditorUI<T>> getRestraintEditor(Class<T> editor) {
		return Optional.ofNullable((IEditorUI<T>) restraintEditors.get(editor));
	}

	/** {@inheritDoc} */
	@Override
	public Optional<ICreatorUI<IBattleRestraint>> getRestraintCreator(Class<? extends IBattleRestraint> creator) {
		return Optional.ofNullable(restraintCreators.get(creator));
	}

	@Override
	public List<ICreatorUI<IBattleRestraint>> getRestraintCreators() {
		return new ArrayList<>(restraintCreators.values());
	}

	public static BossUIRegistry getInstance() {
		if (instance == null) {
			instance = new BossUIRegistry();
		}
		return instance;
	}
}
