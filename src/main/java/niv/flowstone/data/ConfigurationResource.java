package niv.flowstone.data;

import java.util.Set;

public class ConfigurationResource {

    private Set<String> biomes;
    private int min;
    private int max;
    private Integer plateau;
    private int density;

    public ConfigurationResource() {
        this(Set.of(), 0, 0, null, 0);
    }

    public ConfigurationResource(Set<String> biomes, int min, int max, Integer plateau, int density) {
        this.biomes = biomes;
        this.min = min;
        this.max = max;
        this.plateau = plateau;
        this.density = density;
    }

    public Set<String> getBiomes() {
        return biomes;
    }

    public void setBiomes(Set<String> biomes) {
        this.biomes = biomes;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public Integer getPlateau() {
        return plateau;
    }

    public void setPlateau(Integer plateau) {
        this.plateau = plateau;
    }

    public int getDensity() {
        return density;
    }

    public void setDensity(int density) {
        this.density = density;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((biomes == null) ? 0 : biomes.hashCode());
        result = prime * result + density;
        result = prime * result + max;
        result = prime * result + min;
        result = prime * result + ((plateau == null) ? 0 : plateau.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ConfigurationResource other = (ConfigurationResource) obj;
        if (biomes == null) {
            if (other.biomes != null)
                return false;
        } else if (!biomes.equals(other.biomes))
            return false;
        if (density != other.density)
            return false;
        if (max != other.max)
            return false;
        if (min != other.min)
            return false;
        if (plateau == null) {
            if (other.plateau != null)
                return false;
        } else if (!plateau.equals(other.plateau))
            return false;
        return true;
    }

}
