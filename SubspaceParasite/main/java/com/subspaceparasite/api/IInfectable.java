package com.subspaceparasite.api;

/**
 * Interface for entities that can be infected by parasites.
 * <p>
 * Infection is a progressive condition: as the infection level rises the
 * entity becomes more impaired and may eventually convert into a parasite.
 * Implementations should store infection data via capabilities or entity
 * NBT in Minecraft 1.20.1.
 *
 * @author SubspaceParasite Team
 * @since 1.0.0
 */
public interface IInfectable {

    /**
     * Adds infection to this entity.
     * <p>
     * The {@code amplifier} parameter controls how many infection levels
     * are added. A typical attack adds 1 level; stronger parasites may
     * add more.
     *
     * @param amplifier the number of infection levels to add (must be &ge; 0)
     */
    void addInfection(int amplifier);

    /**
     * Returns the current infection level of this entity.
     *
     * @return the infection level (0 = uninfected)
     */
    int getInfectionLevel();

    /**
     * Sets the infection level directly.
     *
     * @param level the new infection level (must be &ge; 0)
     */
    void setInfectionLevel(int level);

    /**
     * Returns whether this entity is completely immune to parasite infection.
     * <p>
     * Immune entities will never receive infection, regardless of amplifier.
     *
     * @return {@code true} if this entity cannot be infected
     */
    boolean isImmuneToInfection();

    /**
     * Returns a resistance factor that reduces infection gain.
     * <p>
     * A value of {@code 0.0f} means no resistance (full infection gain);
     * a value of {@code 1.0f} means complete resistance (effectively immune).
     *
     * @return resistance factor in the range [0.0, 1.0]
     */
    float getInfectionResistance();
}
