package dev.flur.ranks.requirement.records;

import dev.flur.ranks.requirement.Requirement;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

public record RequirementRecord(String name, Function<String[], Requirement> constructor,
                                Class<? extends Requirement> requirementClass) {
    public RequirementRecord(@NotNull String name,
                             @NotNull Function<String[], Requirement> constructor,
                             @NotNull Class<? extends Requirement> requirementClass) {
        this.name = name;
        this.constructor = constructor;
        this.requirementClass = requirementClass;
    }

    @Override
    @NotNull
    public String name() {
        return name;
    }

    @Override
    @NotNull
    public Function<String[], Requirement> constructor() {
        return constructor;
    }

    @Override
    @NotNull
    public Class<? extends Requirement> requirementClass() {
        return requirementClass;
    }

    @Override
    public @NotNull String toString() {
        return "RequirementInfo{" +
                "name='" + name + '\'' +
                ", class=" + requirementClass.getSimpleName() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequirementRecord that = (RequirementRecord) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(requirementClass, that.requirementClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, requirementClass);
    }
}
