package ca.landonjw.remoraids.api.ui;

import java.util.List;
import java.util.Optional;

import ca.landonjw.remoraids.api.battles.IBattleRestraint;
import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;
import ca.landonjw.remoraids.implementation.ui.BossUIRegistry;

/**
 * Represents a registry to store {@link ICreatorUI} and {@link IEditorUI} to be referenced later in the editor user interface.
 * This allows for custom implementations of rewards, reward contents, and battle restraints to be customizable within the
 * native editor user interface.
 *
 * @author landonjw
 * @since 1.0.0
 */
public interface IBossUIRegistry {

	/**
	 * Registers an editor to a given {@link IReward}.
	 *
	 * @param clazz  the reward class to register editor for
	 * @param editor the reward editor
	 * @param <T>    the reward to register editor for
	 */
	<T extends IReward> void registerRewardEditor(Class<T> clazz, IEditorUI<T> editor);

	/**
	 * Registers a creator to a given {@link IReward}.
	 *
	 * @param clazz   the reward class to register creator for
	 * @param creator the reward creator
	 */
	void registerRewardCreator(Class<? extends IReward> clazz, ICreatorUI<IReward> creator);

	/**
	 * Gets the editor for a {@link IReward}.
	 *
	 * @param clazz the class of reward to get editor for
	 * @param <T>   the reward
	 * @return Editor for reward, if present
	 */
	<T extends IReward> Optional<IEditorUI<T>> getRewardEditor(Class<T> clazz);

	/**
	 * Gets the creator for a {@link IReward}.
	 *
	 * @param clazz the class of reward to get editor for
	 * @return Creator for reward, if present
	 */
	Optional<ICreatorUI<IReward>> getRewardCreator(Class<? extends IReward> clazz);

	/**
	 * Gets a list of all reward creators in the registry.
	 *
	 * @return list of all reward creators in the registry
	 */
	List<ICreatorUI<IReward>> getRewardCreators();

	/**
	 * Registers an editor to a given {@link IRewardContent}.
	 *
	 * @param clazz  the reward content class to register editor for
	 * @param editor the reward content editor
	 * @param <T>    the reward content to register editor for
	 */
	<T extends IRewardContent> void registerRewardContentEditor(Class<T> clazz, IEditorUI<T> editor);

	/**
	 * Registers a creator to a given {@link IRewardContent}.
	 *
	 * @param clazz   the reward content class to register creator for
	 * @param creator the reward content creator
	 */
	void registerRewardContentCreator(Class<? extends IRewardContent> clazz, ICreatorUI<IRewardContent> creator);

	/**
	 * Gets the editor for a {@link IRewardContent}.
	 *
	 * @param clazz the class of reward content to get editor for
	 * @param <T>   the reward content
	 * @return Editor for reward content, if present
	 */
	<T extends IRewardContent> Optional<IEditorUI<T>> getRewardContentEditor(Class<T> clazz);

	/**
	 * Gets the creator for a {@link IRewardContent}.
	 *
	 * @param clazz the class of reward content to get editor for
	 * @return Creator for reward content, if present
	 */
	Optional<ICreatorUI<IRewardContent>> getRewardContentCreator(Class<? extends IRewardContent> clazz);

	/**
	 * Gets a list of all reward content creators in the registry.
	 *
	 * @return list of all reward content creators in the registry
	 */
	List<ICreatorUI<IRewardContent>> getRewardContentCreators();

	/**
	 * Registers an editor to a given {@link IBattleRestraint}.
	 *
	 * @param clazz  the restraint class to register editor for
	 * @param editor the restraint editor
	 * @param <T>    the restraint to register editor for
	 */
	<T extends IBattleRestraint> void registerRestraintEditor(Class<T> clazz, IEditorUI<T> editor);

	/**
	 * Registers a creator to a given {@link IBattleRestraint}.
	 *
	 * @param clazz   the restraint class to register creator for
	 * @param creator the restraint creator
	 */
	void registerRestraintCreator(Class<? extends IBattleRestraint> clazz, ICreatorUI<IBattleRestraint> creator);

	/**
	 * Gets the editor for a {@link IBattleRestraint}.
	 *
	 * @param clazz the class of restraint to get editor for
	 * @param <T>   the restraint
	 * @return Editor for restraint, if present
	 */
	<T extends IBattleRestraint> Optional<IEditorUI<T>> getRestraintEditor(Class<T> clazz);

	/**
	 * Gets the creator for a {@link IBattleRestraint}.
	 *
	 * @param clazz the class of restraint to get editor for
	 * @return Creator for restraint, if present
	 */
	Optional<ICreatorUI<IBattleRestraint>> getRestraintCreator(Class<? extends IBattleRestraint> clazz);

	/**
	 * Gets a list of all restraint creators in the registry.
	 *
	 * @return list of all restraint creators in the registry
	 */
	List<ICreatorUI<IBattleRestraint>> getRestraintCreators();

	/**
	 * Gets an instance of the registry.
	 *
	 * @return instance of the registry
	 */
	static IBossUIRegistry getInstance() {
		return BossUIRegistry.getInstance();
	}

}
