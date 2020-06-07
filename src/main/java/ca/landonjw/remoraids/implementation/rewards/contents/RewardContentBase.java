package ca.landonjw.remoraids.implementation.rewards.contents;

import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * A base for {@link IRewardContent} that will store the content's description.
 *
 * @author landonjw
 * @since  1.0.0
 */
public abstract class RewardContentBase implements IRewardContent {

    /** Description of the reward content. */
    private String description;

    /**
     * Default contructor.
     *
     * @param description description of the reward content
     */
    public RewardContentBase(@Nonnull String description){
        this.description = description;
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Sets the reward content's description.
     *
     * @param description the new description
     */
    public void setDescription(@Nonnull String description) {
        this.description = Objects.requireNonNull(description);
    }
}
