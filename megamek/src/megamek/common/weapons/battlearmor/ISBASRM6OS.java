/**
 * MegaMek - Copyright (C) 2005 Ben Mazur (bmazur@sev.org)
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
package megamek.common.weapons.battlearmor;

import megamek.common.TechAdvancement;
import megamek.common.weapons.SRMWeapon;


/**
 * @author Sebastian Brocks
 */
public class ISBASRM6OS extends SRMWeapon {

    /**
     *
     */
    private static final long serialVersionUID = -1926715836221080572L;

    /**
     *
     */
    public ISBASRM6OS() {
        super();
        name = "SRM 6 (OS)";
        setInternalName("ISBASRM6OS");
        addLookupName("ISBASRM6 (OS)"); // mtf
        addLookupName("IS BA SRM 6 (OS)"); // tdb
        addLookupName("IS OS BA SRM-6"); // mep
        heat = 4;
        rackSize = 6;
        shortRange = 3;
        mediumRange = 6;
        longRange = 9;
        extremeRange = 12;
        bv = 12;
        cost = 15000;
        tonnage = .2;
        criticals = 4;
        flags = flags.or(F_NO_FIRES).or(F_BA_WEAPON).or(F_ONESHOT).andNot(F_MECH_WEAPON).andNot(F_TANK_WEAPON).andNot(F_AERO_WEAPON).andNot(F_PROTO_WEAPON);
        rulesRefs = "261, TM";
        techAdvancement.setTechBase(TechAdvancement.TECH_BASE_IS);
        techAdvancement.setISAdvancement(3045, 3050, 3051);
        techAdvancement.setTechRating(RATING_E);
        techAdvancement.setAvailability( new int[] { RATING_X, RATING_X, RATING_D, RATING_B });
    }
    
}
