package ca.landonjw.remoraids.implementation.battles.restraints;

import ca.landonjw.remoraids.api.battles.IBattleRestraint;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * A base implementation for {@link IBattleRestraint}.
 *
 * @author landonjw
 * @since  1.0.0
 */
public abstract class BaseBattleRestraint implements IBattleRestraint {

    /** The identifier for the restraint. */
    private String id;

    /**
     * Constructor for the base battle restraint.
     *
     * @param id identifier for the restraint
     */
    public BaseBattleRestraint(@Nonnull String id){
        this.id = Objects.requireNonNull(id);
    }

    /**
     * Gets the identifier for the restraint.
     *
     * @return the identifier for the restraint
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * Sets the restraint's identifier.
     *
     * @param id new identifier
     */
    protected void setId(@Nonnull String id){
        this.id = Objects.requireNonNull(id);
    }

}
