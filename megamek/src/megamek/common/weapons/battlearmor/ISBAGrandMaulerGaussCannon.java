/**
 * MegaMek - Copyright (C) 2004,2005 Ben Mazur (bmazur@sev.org)
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
/*
 * Created on Sep 24, 2004
 *
 */
package megamek.common.weapons.battlearmor;

import megamek.common.AmmoType;
import megamek.common.TechAdvancement;
import megamek.common.weapons.Weapon;

/**
 * @author Sebastian Brocks
 */
public class ISBAGrandMaulerGaussCannon extends Weapon {
    /**
     *
     */
    private static final long serialVersionUID = 7045591700835271280L;

    /**
     *
     */
    public ISBAGrandMaulerGaussCannon() {
        super();
        name = "Gauss Rifle [Grand Mauler]";
        setInternalName("ISBAGrandMaulerGaussCannon");
        addLookupName("ISBAGrandMauler");
        heat = 0;
        damage = 1;
        ammoType = AmmoType.T_NA;
        shortRange = 2;
        mediumRange = 4;
        longRange = 5;
        extremeRange = 8;
        tonnage = 0.125;
        cost = 8000;
        criticals = 2;
        bv = 6;
        flags = flags.or(F_NO_FIRES).or(F_DIRECT_FIRE).or(F_BALLISTIC).or(F_BA_WEAPON).andNot(F_MECH_WEAPON).andNot(F_TANK_WEAPON).andNot(F_AERO_WEAPON).andNot(F_PROTO_WEAPON);
        rulesRefs = "255, TM";
        techAdvancement.setTechBase(TechAdvancement.TECH_BASE_IS);
        techAdvancement.setISAdvancement(3050, 3059, 3060);
        techAdvancement.setTechRating(RATING_E);
        techAdvancement.setAvailability( new int[] { RATING_X, RATING_X, RATING_D, RATING_E });
    }
}
