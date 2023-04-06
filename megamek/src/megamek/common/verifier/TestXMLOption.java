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

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Reinhard Vicinus
 */
public class TestXMLOption implements TestEntityOption {

    @XmlElement(name = "ceilWeight")
    private final WeightCeiling weightCeiling = new WeightCeiling();

    @XmlElement(name = "ignoreFailedEquipment")
    @XmlJavaTypeAdapter(CSVAdapter.class)
    private final List<String> ignoreFailedEquip = new ArrayList<>();

    public TestXMLOption() {
    }

    @Override
    public TestEntity.Ceil getWeightCeilingEngine() {
        return weightCeiling.engine;
    }

    @Override
    public TestEntity.Ceil getWeightCeilingStructure() {
        return weightCeiling.structure;
    }

    @Override
    public TestEntity.Ceil getWeightCeilingArmor() {
        return weightCeiling.armor;
    }

    @Override
    public TestEntity.Ceil getWeightCeilingControls() {
        return weightCeiling.controls;
    }

    @Override
    public TestEntity.Ceil getWeightCeilingWeapons() {
        return weightCeiling.weapons;
    }

    @Override
    public TestEntity.Ceil getWeightCeilingTargComp() {
        return weightCeiling.targComp;
    }

    @Override
    public TestEntity.Ceil getWeightCeilingGyro() {
        return weightCeiling.gyro;
    }

    @Override
    public TestEntity.Ceil getWeightCeilingTurret() {
        return weightCeiling.turret;
    }

    @Override
    public TestEntity.Ceil getWeightCeilingLifting() {
        return weightCeiling.lifting;
    }
    
    @Override
    public TestEntity.Ceil getWeightCeilingPowerAmp() {
        return weightCeiling.powerAmp;
    }

    @Override
    public double getMaxOverweight() {
        double maxOverweight = 0.25;
        return maxOverweight;
    }

    @Override
    public boolean showOverweightedEntity() {
        boolean showOverweighted = true;
        return showOverweighted;
    }

    @Override
    public boolean showUnderweightedEntity() {
        boolean showUnderweighted = false;
        return showUnderweighted;
    }

    @Override
    public double getMinUnderweight() {
        double minUnderweight = 1.0;
        return minUnderweight;
    }

    @Override
    public boolean ignoreFailedEquip(String name) {
        for (String s : ignoreFailedEquip) {
            if (s.equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean skip() {
        boolean skip = false;
        return skip;
    }

    @Override
    public boolean showCorrectArmor() {
        boolean showCorrectArmor = true;
        return showCorrectArmor;
    }

    @Override
    public boolean showCorrectCritical() {
        boolean showCorrectCritical = true;
        return showCorrectCritical;
    }

    @Override
    public boolean showFailedEquip() {
        boolean showFailedEquip = true;
        return showFailedEquip;
    }
    
    @Override
    public boolean showIncorrectIntroYear() {
        boolean showIncorrectIntroYear = true;
        return showIncorrectIntroYear;
    }
    
    @Override
    public int getIntroYearMargin() {
        int introYearMargin = 5;
        return introYearMargin;
    }

    @Override
    public int getTargCompCrits() {
        int targCompCrits = 0;
        return targCompCrits;
    }

    @Override
    public int getPrintSize() {
        int printSize = 70;
        return printSize;
    }

    public String printIgnoredFailedEquip() {
        System.out.println("--->printIgnoredFailedEquip");
        StringBuilder sb = new StringBuilder();
        for (String s : ignoreFailedEquip) {
            sb.append("  ").append(s).append("\n");
        }
        return sb.toString();
    }

    public String printOptions() {
        return "Skip: " + skip() + "\n" + "Show Overweighted Entity: "
                + showOverweightedEntity() + "\n" + "Max Overweight: "
                + getMaxOverweight() + "\n"
                + "Show Underweighted Entity: " + showUnderweightedEntity()
                + "\n" + "Min Underweight: "
                + getMinUnderweight() + "\n"
                + "Show bad Armor Placement: " + showCorrectArmor() + "\n"
                + "Show bad Critical Allocation: " + showCorrectCritical()
                + "\n" + "Show Failed to Load Equipment: " + showFailedEquip()
                + "\n" + "Show Incorrect Intro Year: " + showIncorrectIntroYear()
                + "\n" + "Margin of error for Intro Year: " + getIntroYearMargin()
                + "\n" + "Weight Ceiling Engine: "
                + 1 / getWeightCeilingEngine().mult + "\n"
                + "Weight Ceiling Structure: "
                + 1 / getWeightCeilingStructure().mult + "\n"
                + "Weight Ceiling Armor: "
                + 1 / getWeightCeilingArmor().mult + "\n"
                + "Weight Ceiling Controls: "
                + 1 / getWeightCeilingControls().mult + "\n"
                + "Weight Ceiling Weapons: "
                + 1 / getWeightCeilingWeapons().mult + "\n"
                + "Weight Ceiling TargComp: "
                + 1 / getWeightCeilingTargComp().mult + "\n"
                + "Weight Ceiling Gyro: "
                + 1 / getWeightCeilingGyro().mult + "\n"
                + "Weight Ceiling Turret: "
                + 1 / getWeightCeilingTurret().mult + "\n"
                + "Weight Ceiling Lifting:"
                + 1 / getWeightCeilingLifting().mult + "\n"
                + "Weight Ceiling PowerAmp: "
                + 1 / getWeightCeilingPowerAmp().mult + "\n"
                + "Ignore Failed Equipment: \n" + printIgnoredFailedEquip();
    }
    
    /**
     * JAXB helper class for the ceilWeight tag.
     */
    @XmlType
    private static class WeightCeiling {
        @XmlElement
        final
        TestEntity.Ceil engine = TestEntity.Ceil.HALFTON;

        @XmlElement
        final
        TestEntity.Ceil structure = TestEntity.Ceil.HALFTON;

        @XmlElement
        final
        TestEntity.Ceil armor = TestEntity.Ceil.HALFTON;

        @XmlElement
        final
        TestEntity.Ceil controls = TestEntity.Ceil.HALFTON;

        @XmlElement
        final
        TestEntity.Ceil weapons = TestEntity.Ceil.TON;

        @XmlElement(name = "targcomp")
        final
        TestEntity.Ceil targComp = TestEntity.Ceil.TON;

        @XmlElement
        final
        TestEntity.Ceil turret = TestEntity.Ceil.HALFTON;

        @XmlElement
        final
        TestEntity.Ceil lifting = TestEntity.Ceil.HALFTON;

        @XmlElement(name = "poweramp")
        final
        TestEntity.Ceil powerAmp = TestEntity.Ceil.HALFTON;

        @XmlElement
        final
        TestEntity.Ceil gyro = TestEntity.Ceil.HALFTON;

        WeightCeiling() {
        }
    }
    
    /**
     * An adapter that unmarshals a comma-separated string of values into a list of values.
     */
    private static class CSVAdapter extends XmlAdapter<String, List<String>> {

        @Override
        public String marshal(final List<String> v) throws Exception {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public List<String> unmarshal(final String v) throws Exception {
            List<String> list = new ArrayList<>();
            StringTokenizer st = new StringTokenizer(v, ",");
            
            while (st.hasMoreTokens()) {
                list.add(st.nextToken().trim());
            }
            
            return list;
        }
        
    }
}
