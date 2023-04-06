/*
 * MegaMek - Copyright (C) 2016 The MegaMek Team
 *
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the Free
 *  Software Foundation; either version 2 of the License, or (at your option)
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 *  for more details.
 */
package megamek.client.ratgenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import megamek.common.Compute;
import megamek.common.EntityMovementMode;
import megamek.common.MechSummary;
import megamek.common.annotations.Nullable;

/**
 * Manages random assignment table generated by RATGenerator.
 *
 * @author Neoancient
 *
 */
public class UnitTable {

    @FunctionalInterface
    public interface UnitFilter {
        boolean include(MechSummary ms);
    }

    private static final int CACHE_SIZE = 32;

    private static final LinkedHashMap<Parameters,UnitTable> cache = new LinkedHashMap<>(CACHE_SIZE, 0.75f, true) {

        private static final long serialVersionUID = -8016095510116134800L;

        @Override
        protected boolean removeEldestEntry(Map.Entry<Parameters, UnitTable> entry) {
            return size() >= CACHE_SIZE;
        }
    };

    /**
     * Checks the cache for a previously generated table meeting the criteria. If none is
     * found, generates it and adds it to the cache.
     *
     * @param faction - The faction for which to generate a table
     * @param unitType - a UnitType constant
     * @param year - the game year
     * @param rating - the unit's equipment rating; if null, the table is not adjusted for unit rating.
     * @param weightClasses - a collection of EntityWeightClass constants to include in the table;
     * if null or empty all weight classes are included
     * @param networkMask - a ModelRecord.NETWORK_* constant
     * @param movementModes - the movement modes allowed to appear in the table; if null or empty, no filtering
     * is applied.
     * @param roles - the roles for which to adjust the availability
     * @param roleStrictness - how rigidly to apply the role adjustments; normal range is &lt;= 4
     * @param deployingFaction - when using the salvage/isorla mechanism, any omni unit will select
     * the configuration based on the faction actually deploying
     * @return - a table containing the available units and their relative weights
     */
    public static UnitTable findTable(FactionRecord faction, int unitType, int year,
            String rating, Collection<Integer> weightClasses, int networkMask,
            Collection<EntityMovementMode> movementModes,
            Collection<MissionRole> roles, int roleStrictness, FactionRecord deployingFaction) {
        Objects.requireNonNull(faction);
        Parameters params = new Parameters(faction, unitType, year, rating, weightClasses, networkMask,
                movementModes, roles, roleStrictness, deployingFaction);
        return findTable(params);
    }
    
    /**
     * deployingFaction not specified, uses main faction.
     *
     */

    public static UnitTable findTable(FactionRecord faction, int unitType, int year,
            String rating, Collection<Integer> weightClasses, int networkMask,
            Collection<EntityMovementMode> movementModes,
            Collection<MissionRole> roles, int roleStrictness) {
        return findTable(faction, unitType, year, rating, weightClasses, networkMask,
                movementModes, roles, roleStrictness, faction);
    }

    /**
     * Checks cache for a unit table with the given parameters. If none is found, generates
     * one and adds to the cache using a copy of the Parameters object as a key.
     * 
     * @param params - the parameters to use in generating the table.
     * @return a generated table matching the parameters
     */
    public static synchronized UnitTable findTable(Parameters params) {
        Objects.requireNonNull(params);
        UnitTable retVal = cache.get(params);
        if (retVal == null) {
            retVal = new UnitTable(params);
            if (retVal.hasUnits()) {
                //Use a copy of the params for the cache key to prevent changing it.
                cache.put(params.copy(), retVal);
            }
        }
        return retVal;
    }

    private final Parameters key;
    private final List<TableEntry> salvageTable = new ArrayList<>();
    private final List<TableEntry> unitTable = new ArrayList<>();

    int salvageTotal;
    int unitTotal;
    /* Filtering can reduce the total weight of the units. Calculate the salvage pct when
     * creating the table to maintain the same proportion. */
    int salvagePct;

    /**
     * Initializes table based on values provided by key
     *
     * @param key - a structure providing the parameters for generating the table
     */
    protected UnitTable(Parameters key) {
        this.key = key;
        /*
         * Generate the RAT, then go through it to build the NavigableMaps that
         * will be used for random selection.
         */
        if (key.getFaction().isActiveInYear(key.getYear())) {
            List<TableEntry> table = RATGenerator.getInstance().generateTable(key.getFaction(),
                    key.getUnitType(), key.getYear(), key.getRating(), key.getWeightClasses(),
                    key.getNetworkMask(), key.getMovementModes(),
                    key.getRoles(), key.getRoleStrictness(), key.getDeployingFaction());
            Collections.sort(table);

            table.forEach(te -> {
                if (te.isUnit()) {
                    unitTotal += te.weight;
                    unitTable.add(te);
                } else {
                    salvageTotal += te.weight;
                    salvageTable.add(te);
                }
            });
            if (salvageTotal + unitTotal > 0) {
                salvagePct = salvageTotal * 100 / (salvageTotal + unitTotal);
            }
        }
    }

    /**
     * @return - number of entries in the table
     */

    public int getNumEntries() {
        return salvageTable.size() + unitTable.size();
    }

    /**
     * @param index
     * @return - the entry at the indicated index
     */
    private TableEntry getEntry(int index) {
        if (index < salvageTable.size()) {
            return salvageTable.get(index);
        }
        return unitTable.get(index - salvageTable.size());
    }

    /**
     * @param index
     * @return - the weight value for the entry at the indicated index
     */
    public int getEntryWeight(int index) {
        return getEntry(index).weight;
    }

    /**
     * @param index
     * @return - a string representing the entry at the indicated index for use in the table
     */
    public String getEntryText(int index) {
        if (index >= salvageTable.size()) {
            return unitTable.get(index - salvageTable.size()).getUnitEntry().getName();
        } else {
            if (key.getFaction().isClan()) {
                return "Isorla: " + salvageTable.get(index).getSalvageFaction().getName(key.getYear() - 5);
            } else {
                return "Salvage: " + salvageTable.get(index).getSalvageFaction().getName(key.getYear() - 5);
            }
        }
    }

    /**
     * @param index
     * @return - a string representing the entry at the indicated index for use in the table
     */
    public String getTechBase(int index) {
        if (index >= salvageTable.size()) {
            return unitTable.get(index - salvageTable.size()).getUnitEntry().isClan() ? "Clan" : "IS";
        } else {
            return key.getFaction().isClan() ? "Clan" : "IS";
        }
    }

    /**
     * @param index
     * @return - a string representing the entry at the indicated index for use in the table
     */
    public String getUnitRole(int index) {
        if (index >= salvageTable.size()) {
            return unitTable.get(index - salvageTable.size()).getUnitEntry().getRole().toString();
        } else {
            return "";
        }
    }

    /**
     * @param index
     * @return - the MechSummary entry for the indicated index, or null if this is a salvage entry
     */
    public MechSummary getMechSummary(int index) {
        if (index >= salvageTable.size()) {
            return unitTable.get(index - salvageTable.size()).getUnitEntry();
        }
        return null;
    }

    /**
     * @param index
     * @return - the BV of the unit at the indicated index, or 0 if this is a salvage entry
     */
    public int getBV(int index) {
        if (index >= salvageTable.size()) {
            return unitTable.get(index - salvageTable.size()).getUnitEntry().getBV();
        } else {
            return 0;
        }
    }

    /**
     * @return true if the generated table has any unit entries
     */
    public boolean hasUnits() {
        return !unitTable.isEmpty();
    }

    /**
     * Selects a unit from the full table.
     *
     * @return the selected unit
     */
    public MechSummary generateUnit() {
        return generateUnit(null);
    }

    /**
     * Selects a unit from the entries in the table that pass the filter
     *
     * @param filter - the function that determines which units are permitted; if null, no filter is applied.
     * @return - the selected unit, or null if no units pass the filter.
     */
    public @Nullable MechSummary generateUnit(UnitFilter filter) {
        int roll = Compute.randomInt(100);
        if (roll < salvagePct) {
            MechSummary ms = generateSalvage(filter);
            if (ms != null) {
                return ms;
            }
        }
        List<TableEntry> useUnitList = unitTable;
        int unitMapSize = unitTotal;
        if (filter != null) {
            useUnitList = unitTable.stream().filter(te -> filter.include(te.getUnitEntry()))
                    .collect(Collectors.toList());
            unitMapSize = useUnitList.stream().mapToInt(te -> te.weight).sum();
        }

        if (unitMapSize > 0) {
            roll = Compute.randomInt(unitMapSize);
            for (TableEntry te : useUnitList) {
                if (roll < te.weight) {
                    return te.getUnitEntry();
                }
                roll -= te.weight;
            }
        }
        return null;
    }

    /**
     * Selects a number of units from the table.
     *
     * @param num - the number of units to be generated.
     * @return - a list of randomly generated units
     */
    public ArrayList<MechSummary> generateUnits(int num) {
        return generateUnits(num, null);
    }

    /**
     * Selects a number of units from the table with a filter.
     *
     * @param num - the number of units to be generated.
     * @param filter - the function that determines which units are permitted; if null, no filter is applied.
     * @return - a list of randomly generated units
     */
    public ArrayList<MechSummary> generateUnits(int num, UnitFilter filter) {
        ArrayList<MechSummary> retVal = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            MechSummary ms = generateUnit(filter);
            if (ms != null) {
                retVal.add(ms);
            }
        }
        return retVal;
    }

    /**
     * Selects a faction from the salvage list and generates a table using the same parameters
     * as this table, but from five years earlier. Generated tables are cached for later use.
     * If the generated table contains no units, it is discarded and the selected entry is deleted.
     * This continues until either a unit is generated or there are no remaining entries.
     *
     * @param filter - passed to generateUnit() in the generated table.
     * @return - a unit generated from another faction, or null if none of the factions in
     * the salvage list contain any units that meet the parameters.
     */
    private @Nullable MechSummary generateSalvage(UnitFilter filter) {
        while (salvageTotal > 0) {
            int roll = Compute.randomInt(salvageTotal);
            TableEntry salvageEntry = null;
            for (TableEntry te : salvageTable) {
                if (roll < te.weight) {
                    salvageEntry = te;
                    break;
                }
                roll -= te.weight;
            }
            if (salvageEntry != null) {
                UnitTable salvage = UnitTable.findTable(salvageEntry.getSalvageFaction(),
                        key.getUnitType(), key.getYear() - 5, key.getRating(),
                        key.getWeightClasses(), key.getNetworkMask(), key.getMovementModes(),
                        key.getRoles(), key.getRoleStrictness(), key.getFaction());
                if (salvage.hasUnits()) {
                    return salvage.generateUnit(filter);
                } else {
                    salvageTotal -= salvageEntry.weight;
                    salvageTable.remove(salvageEntry);
                }
            }
        }
        return null;
    }

    /* A tuple that contains either a salvage or a faction entry along with its relative weight.
     * in the table. */
    public static class TableEntry implements Comparable<TableEntry> {
        final int weight;
        final Object entry;

        public TableEntry(int weight, Object entry) {
            this.weight = weight;
            this.entry = entry;
        }

        public MechSummary getUnitEntry() {
            return (MechSummary) entry;
        }

        public FactionRecord getSalvageFaction() {
            return (FactionRecord) entry;
        }

        public boolean isUnit() {
            return entry instanceof MechSummary;
        }

        @Override
        public int compareTo(TableEntry other) {
            if (entry instanceof MechSummary && other.entry instanceof FactionRecord) {
                return 1;
            }
            if (entry instanceof FactionRecord && other.entry instanceof MechSummary) {
                return -1;
            }
            return toString().compareTo(other.toString());
        }

        @Override
        public String toString() {
            if (entry instanceof MechSummary) {
                return ((MechSummary) entry).getName();
            }
            return entry.toString();
        }
    }

    /*
     * A class that holds all the parameters used to generate a table and is used as the
     * key for the cache.
     *
     */
    public static final class Parameters implements Cloneable {
        private FactionRecord faction;
        private int unitType;
        private int year;
        private String rating;
        private Collection<Integer> weightClasses;
        private int networkMask;
        private Collection<EntityMovementMode> movementModes;
        private Collection<MissionRole> roles;
        private int roleStrictness;
        private FactionRecord deployingFaction;

        public Parameters(FactionRecord faction, int unitType, int year,
                String rating, Collection<Integer> weightClasses, int networkMask,
                Collection<EntityMovementMode> movementModes,
                Collection<MissionRole> roles, int roleStrictness, FactionRecord deployingFaction) {
            this.faction = faction;
            this.unitType = unitType;
            this.year = year;
            this.rating = rating;
            this.weightClasses = weightClasses == null?
                    new ArrayList<>() : new ArrayList<>(weightClasses);
            this.networkMask = networkMask;
            this.movementModes = ((movementModes == null) || movementModes.isEmpty())
                    ? EnumSet.noneOf(EntityMovementMode.class) : EnumSet.copyOf(movementModes);
            this.roles = ((roles == null) || roles.isEmpty())
                    ? EnumSet.noneOf(MissionRole.class) : EnumSet.copyOf(roles);
            this.roleStrictness = roleStrictness;
            this.deployingFaction = (deployingFaction == null) ? faction : deployingFaction;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime
                    * result
                    + ((deployingFaction == null) ? 0 : deployingFaction
                            .hashCode());
            result = prime * result
                    + ((faction == null) ? 0 : faction.hashCode());
            result = prime * result
                    + ((movementModes == null) ? 0 : movementModes.hashCode());
            result = prime * result + networkMask;
            result = prime * result
                    + ((rating == null) ? 0 : rating.hashCode());
            result = prime * result + roleStrictness;
            result = prime * result + ((roles == null) ? 0 : roles.hashCode());
            result = prime * result + unitType;
            result = prime * result
                    + ((weightClasses == null) ? 0 : weightClasses.hashCode());
            result = prime * result + year;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof Parameters)) {
                return false;
            }
            Parameters other = (Parameters) obj;
            if (deployingFaction == null) {
                if (other.deployingFaction != null) {
                    return false;
                }
            } else if (!deployingFaction.equals(other.deployingFaction)) {
                return false;
            }
            if (faction == null) {
                if (other.faction != null) {
                    return false;
                }
            } else if (!faction.equals(other.faction)) {
                return false;
            }
            if (movementModes == null) {
                if (other.movementModes != null) {
                    return false;
                }
            } else if (!movementModes.equals(other.movementModes)) {
                return false;
            }
            if (networkMask != other.networkMask) {
                return false;
            }
            if (rating == null) {
                if (other.rating != null) {
                    return false;
                }
            } else if (!rating.equals(other.rating)) {
                return false;
            }
            if (roleStrictness != other.roleStrictness) {
                return false;
            }
            if (roles == null) {
                if (other.roles != null) {
                    return false;
                }
            } else if (!roles.equals(other.roles)) {
                return false;
            }
            if (unitType != other.unitType) {
                return false;
            }
            if (weightClasses == null) {
                if (other.weightClasses != null) {
                    return false;
                }
            } else if (!weightClasses.equals(other.weightClasses)) {
                return false;
            }
            return year == other.year;
        }

        public FactionRecord getFaction() {
            return faction;
        }

        public void setFaction(FactionRecord faction) {
            this.faction = faction;
        }

        public int getUnitType() {
            return unitType;
        }

        public void setUnitType(int unitType) {
            this.unitType = unitType;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

        public Collection<Integer> getWeightClasses() {
            return weightClasses;
        }

        public void setWeightClasses(Collection<Integer> weightClasses) {
            this.weightClasses = weightClasses;
        }

        public int getNetworkMask() {
            return networkMask;
        }

        public void setNetworkMask(int networkMask) {
            this.networkMask = networkMask;
        }

        public Collection<EntityMovementMode> getMovementModes() {
            return movementModes;
        }

        public void setMovementModes(Collection<EntityMovementMode> movementModes) {
            this.movementModes = movementModes;
        }

        public Collection<MissionRole> getRoles() {
            return roles;
        }

        public void setRoles(Collection<MissionRole> roles) {
            this.roles = roles;
        }

        public int getRoleStrictness() {
            return roleStrictness;
        }

        public void setRoleStrictness(int roleStrictness) {
            this.roleStrictness = roleStrictness;
        }

        public FactionRecord getDeployingFaction() {
            return deployingFaction;
        }

        public void setDeployingFaction(FactionRecord deployingFaction) {
            this.deployingFaction = deployingFaction;
        }

        public Parameters copy() {
            return new Parameters(faction, unitType, year,
                rating, weightClasses, networkMask, movementModes,
                roles, roleStrictness, deployingFaction);
        }
        
    }
}
