/*
 * MegaMek -
 * Copyright (C) 2000-2005 Ben Mazur (bmazur@sev.org)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 */
package megamek.common.verifier;

/**
 * @author Reinhard Vicinus
 */
public interface TestEntityOption {
    int CEIL_TARGCOMP_CRITS = 0;
    int ROUND_TARGCOMP_CRITS = 1;
    int FLOOR_TARGCOMP_CRITS = 2;

    TestEntity.Ceil getWeightCeilingEngine();

    TestEntity.Ceil getWeightCeilingStructure();

    TestEntity.Ceil getWeightCeilingArmor();

    TestEntity.Ceil getWeightCeilingControls();

    TestEntity.Ceil getWeightCeilingWeapons();

    TestEntity.Ceil getWeightCeilingTargComp();

    TestEntity.Ceil getWeightCeilingGyro();

    TestEntity.Ceil getWeightCeilingTurret();
    
    TestEntity.Ceil getWeightCeilingLifting();

    TestEntity.Ceil getWeightCeilingPowerAmp();

    double getMaxOverweight();

    boolean showOverweightedEntity();

    boolean showUnderweightedEntity();

    boolean showCorrectArmor();

    boolean showCorrectCritical();

    boolean showFailedEquip();
    
    boolean showIncorrectIntroYear();
    
    int getIntroYearMargin();

    double getMinUnderweight();

    boolean ignoreFailedEquip(String name);

    boolean skip();

    int getTargCompCrits();

    int getPrintSize();
}
