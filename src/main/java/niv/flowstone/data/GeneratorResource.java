package niv.flowstone.data;

import java.util.Set;

public class GeneratorResource {

    private String block;
    private Set<ConfigurationResource> configurations;

    public GeneratorResource() {
        this("minecraft:stone", Set.of());
    }

    public GeneratorResource(String block, Set<ConfigurationResource> configurations) {
        this.block = block;
        this.configurations = configurations;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public Set<ConfigurationResource> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(Set<ConfigurationResource> configurations) {
        this.configurations = configurations;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((block == null) ? 0 : block.hashCode());
        result = prime * result + ((configurations == null) ? 0 : configurations.hashCode());
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
        GeneratorResource other = (GeneratorResource) obj;
        if (block == null) {
            if (other.block != null)
                return false;
        } else if (!block.equals(other.block))
            return false;
        if (configurations == null) {
            if (other.configurations != null)
                return false;
        } else if (!configurations.equals(other.configurations))
            return false;
        return true;
    }

}
