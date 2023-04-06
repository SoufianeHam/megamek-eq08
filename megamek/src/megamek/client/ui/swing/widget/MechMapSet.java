/*
 * MegaMek - Copyright (C) 2000-2002 Ben Mazur (bmazur@sev.org)
 * Copyright © 2013 Edward Cullen (eddy@obsessedcomputers.co.uk)
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
package megamek.client.ui.swing.widget;

import megamek.MMConstants;
import megamek.client.ui.Messages;
import megamek.client.ui.swing.GUIPreferences;
import megamek.client.ui.swing.unitDisplay.UnitDisplay;
import megamek.common.Configuration;
import megamek.common.Entity;
import megamek.common.Mech;
import megamek.common.options.OptionsConstants;
import megamek.common.util.fileUtils.MegaMekFile;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

/**
 * Very cumbersome class that handles set of polygonal areas and labels for PicMap component to
 * represent single mech unit in MechDisplay
 */
public class MechMapSet implements DisplayMapSet {
    // Because of keeping all areas of single type in one array
    // some index offset values required
    private static final int REAR_AREA_OFFSET = 7;
    private static final int INT_STRUCTURE_OFFSET = 11;

    private final UnitDisplay unitDisplay;
    
    // Array of polygonal areas - parts of mech body.
    private final PMSimplePolygonArea[] areas = new PMSimplePolygonArea[19];
    // Array of fixed labels - short names of body parts
    private final PMSimpleLabel[] labels = new PMSimpleLabel[20];
    // Array of value labels to show armor and IS values
    private final PMValueLabel[] vLabels = new PMValueLabel[21];
    // Heat control area
    private PMPicPolygonalArea heatHotArea;
    // Set of Background Drawers
    private final Vector<BackGroundDrawer> bgDrawers = new Vector<>();
    // Main areas group that keeps everything in itself and is passed to PicMap
    // component
    private final PMAreasGroup content = new PMAreasGroup();
    // Reference to Component class (need to manage images and fonts)
    private final JComponent comp;

    // Points for build hot areas (maybe too heavy, think of to load from external file)
    // Mek armor - Front
    // Right hand
    private final Polygon rightArm = new Polygon(new int[] { 106, 105, 110, 114, 111,
            108, 106, 109, 112, 119, 116, 122, 124, 118, 115, 112, 114, 113,
            111, 95, 93, 93, 91, 91, 95, 99, 99, 102, 104, 101, 104, 106 },
            new int[] { 89, 87, 86, 90, 94, 92, 94, 97, 98, 91, 81, 81, 78, 53,
                    50, 36, 33, 30, 25, 23, 25, 25, 27, 33, 37, 51, 78, 81, 81,
                    86, 91, 89 }, 32);
    // Left hand
    private final Polygon leftArm = new Polygon(new int[] { 18, 19, 14, 10, 13, 16,
            18, 15, 12, 5, 8, 2, 0, 6, 9, 12, 10, 11, 13, 29, 31, 31, 33, 33,
            29, 25, 25, 22, 20, 23, 20, 18 }, new int[] { 89, 87, 86, 90, 94,
            92, 94, 97, 98, 91, 81, 81, 78, 53, 50, 36, 33, 30, 25, 23, 25, 25,
            27, 33, 37, 51, 78, 81, 81, 86, 91, 89 }, 32);
    // Head
    private final Polygon head = new Polygon(new int[] { 53, 71, 81, 83, 83, 62, 41,
            41, 43 }, new int[] { 32, 32, 22, 22, 8, 0, 8, 22, 22 }, 9);
    // Central Torso
    private final Polygon centralTorso = new Polygon(new int[] { 48, 54, 70, 76, 76,
            48 }, new int[] { 45, 85, 85, 45, 36, 36 }, 6);
    // left Torso
    private final Polygon leftTorso = new Polygon(new int[] { 54, 48, 48, 62, 62, 53,
            43, 41, 41, 31, 33, 33, 29, 26, 47 }, new int[] { 82, 45, 36, 36,
            32, 32, 22, 22, 20, 25, 27, 33, 37, 47, 82 }, 15);
    // right Torso
    private final Polygon rightTorso = new Polygon(new int[] { 70, 76, 76, 62, 62,
            71, 81, 83, 83, 93, 91, 91, 95, 98, 77 }, new int[] { 82, 45, 36,
            36, 32, 32, 22, 22, 20, 25, 27, 33, 37, 47, 82 }, 15);
    // Left Leg

    private final Polygon leftLeg = new Polygon(new int[] { 18, 21, 37, 47, 54, 54,
            61, 43, 45, 45, 43, 44, 19, 20, 18 }, new int[] { 104, 104, 65, 82,
            82, 85, 85, 103, 103, 121, 121, 129, 129, 122, 122 }, 15);
    // right Leg
    private final Polygon rightLeg = new Polygon(new int[] { 107, 104, 88, 77, 70,
            70, 64, 82, 80, 80, 82, 81, 106, 105, 107 }, new int[] { 104, 104,
            63, 82, 82, 85, 85, 103, 103, 121, 121, 129, 129, 122, 122 }, 15);

    // Mek Armor - Rear
    // Left Torso

    private final Polygon rearLeftTorso = new Polygon(new int[] { 142, 142, 148, 139,
            123, 123, 142 }, new int[] { 14, 43, 76, 76, 44, 17, 14 }, 7);
    // Central Torso

    private final Polygon rearCentralTorso = new Polygon(new int[] { 142, 148, 162,
            168, 168, 142 }, new int[] { 44, 76, 76, 44, 14, 14 }, 6);
    // Right Torso

    private final Polygon rearRightTorso = new Polygon(new int[] { 168, 168, 162,
            171, 187, 187, 168 }, new int[] { 14, 43, 76, 76, 44, 17, 14 }, 7);

    // Internal Structure
    // Head
    private final Polygon intStHead = new Polygon(new int[] { 78, 48, 48, 78 },
            new int[] { 149, 149, 127, 127 }, 4);
    // Left hand
    private final Polygon inStLeftArm = new Polygon(new int[] { 17, 11, 5, 5, 8, 8,
            21, 21, 25, 25, 28, 51, 51, 29, 29 }, new int[] { 147, 170, 170,
            194, 194, 197, 197, 194, 194, 170, 157, 157, 154, 154, 147 }, 15);
    // Right hand
    private final Polygon inStRightArm = new Polygon(new int[] { 109, 115, 121, 121,
            118, 118, 105, 105, 101, 101, 98, 75, 75, 97, 97 }, new int[] {
            147, 170, 170, 194, 194, 197, 197, 194, 194, 170, 157, 157, 154,
            154, 147 }, 15);
    // Central Torso
    private final Polygon inStCentralTorso = new Polygon(
            new int[] { 75, 75, 51, 51 }, new int[] { 203, 149, 149, 203 }, 4);
    // Left Torso
    private final Polygon inStLeftTorso = new Polygon(new int[] { 32, 32, 51, 51 },
            new int[] { 188, 160, 160, 193 }, 4);
    // Right Torso
    private final Polygon inStRightTorso = new Polygon(new int[] { 94, 94, 75, 75 },
            new int[] { 188, 160, 160, 193 }, 4);
    // Left Leg
    private final Polygon inStLeftLeg = new Polygon(new int[] { 51, 51, 44, 44, 47,
            47, 20, 20, 41, 41, 44, 44 }, new int[] { 195, 199, 199, 206, 206,
            230, 230, 206, 206, 192, 192, 195 }, 12);
    // right Leg
    private final Polygon inStRightLeg = new Polygon(new int[] { 75, 75, 82, 82, 79,
            79, 106, 106, 85, 85, 82, 82 }, new int[] { 195, 199, 199, 206,
            206, 230, 230, 206, 206, 192, 192, 195 }, 12);
    // Heat control
    private final Polygon heatControl = new Polygon(new int[] { 149, 159, 159, 149 },
            new int[] { 100, 100, 220, 220 }, 4);

    private Image heatImage;

    private static final Font FONT_LABEL = new Font(MMConstants.FONT_SANS_SERIF, Font.PLAIN,
            GUIPreferences.getInstance().getInt("AdvancedMechDisplayArmorSmallFontSize"));
    private static final Font FONT_VALUE = new Font(MMConstants.FONT_SANS_SERIF, Font.PLAIN,
            GUIPreferences.getInstance().getInt("AdvancedMechDisplayArmorLargeFontSize"));

    public MechMapSet(JComponent c, UnitDisplay unitDisplay) {
        this.unitDisplay = unitDisplay;
        comp = c;
        setAreas();
        setLabels();
        setGroups();
        setBackGround();        
    }

    @Override
    public PMAreasGroup getContentGroup() {
        return content;
    }

    @Override
    public Vector<BackGroundDrawer> getBackgroundDrawers() {
        return bgDrawers;
    }

    @Override
    public void setEntity(Entity e) {
        Mech m = (Mech) e;
        boolean mtHeat = (e.getGame() != null)
                && e.getGame().getOptions().booleanOption(OptionsConstants.ADVCOMBAT_TACOPS_HEAT);
        int a = 1;
        int a0 = 1;
        for (int i = 0; i < m.locations(); i++) {
            a = m.getArmor(i);
            a0 = m.getOArmor(i);
            vLabels[i].setValue(m.getArmorString(i));
            WidgetUtils.setAreaColor(areas[i], vLabels[i], (double) a / (double) a0);
            if (m.hasRearArmor(i)) {
                a = m.getArmor(i, true);
                a0 = m.getOArmor(i, true);
                vLabels[i + REAR_AREA_OFFSET].setValue(m.getArmorString(i, true));
                WidgetUtils.setAreaColor(areas[i + REAR_AREA_OFFSET], vLabels[i
                        + REAR_AREA_OFFSET], (double) a / (double) a0);
            }
            a = m.getInternal(i);
            a0 = m.getOInternal(i);
            vLabels[i + INT_STRUCTURE_OFFSET].setValue(m.getInternalString(i));
            WidgetUtils.setAreaColor(areas[i + INT_STRUCTURE_OFFSET], vLabels[i
                    + INT_STRUCTURE_OFFSET], (double) a / (double) a0);
        }

        // heat
        vLabels[19].setValue(Integer.toString(m.heat));
        drawHeatControl(m.heat, mtHeat);
        // FIXME : this messes up the layout a bit, but only for industrial meks
        if (!m.hasPatchworkArmor() && m.hasBARArmor(1)) {
            content.addArea(labels[19]);
            content.addArea(vLabels[20]);
            vLabels[20].setValue(String.valueOf(m.getBARRating(1)));
        } else {
            content.removeArea(labels[19]);
            content.removeArea(vLabels[20]);
        }
    }

    private void setAreas() {
        areas[Mech.LOC_HEAD] = new PMSimplePolygonArea(head, unitDisplay, Mech.LOC_HEAD);
        areas[Mech.LOC_CT] = new PMSimplePolygonArea(centralTorso, unitDisplay, Mech.LOC_CT);
        areas[Mech.LOC_RT] = new PMSimplePolygonArea(rightTorso, unitDisplay, Mech.LOC_RT);
        areas[Mech.LOC_LT] = new PMSimplePolygonArea(leftTorso, unitDisplay, Mech.LOC_LT);
        areas[Mech.LOC_RARM] = new PMSimplePolygonArea(rightArm, unitDisplay, Mech.LOC_RARM);
        areas[Mech.LOC_LARM] = new PMSimplePolygonArea(leftArm, unitDisplay, Mech.LOC_LARM);
        areas[Mech.LOC_RLEG] = new PMSimplePolygonArea(rightLeg, unitDisplay, Mech.LOC_RLEG);
        areas[Mech.LOC_LLEG] = new PMSimplePolygonArea(leftLeg, unitDisplay, Mech.LOC_LLEG);
        areas[REAR_AREA_OFFSET + Mech.LOC_CT] = new PMSimplePolygonArea(rearCentralTorso, unitDisplay, Mech.LOC_CT);
        areas[REAR_AREA_OFFSET + Mech.LOC_RT] = new PMSimplePolygonArea(rearRightTorso, unitDisplay, Mech.LOC_RT);
        areas[REAR_AREA_OFFSET + Mech.LOC_LT] = new PMSimplePolygonArea(rearLeftTorso, unitDisplay, Mech.LOC_LT);
        areas[INT_STRUCTURE_OFFSET + Mech.LOC_HEAD] = new PMSimplePolygonArea(intStHead, unitDisplay, Mech.LOC_HEAD);
        areas[INT_STRUCTURE_OFFSET + Mech.LOC_CT] = new PMSimplePolygonArea(inStCentralTorso, unitDisplay, Mech.LOC_CT);
        areas[INT_STRUCTURE_OFFSET + Mech.LOC_RT] = new PMSimplePolygonArea(inStRightTorso, unitDisplay, Mech.LOC_RT);
        areas[INT_STRUCTURE_OFFSET + Mech.LOC_LT] = new PMSimplePolygonArea(inStLeftTorso, unitDisplay, Mech.LOC_LT);
        areas[INT_STRUCTURE_OFFSET + Mech.LOC_RARM] = new PMSimplePolygonArea(inStRightArm, unitDisplay, Mech.LOC_RARM);
        areas[INT_STRUCTURE_OFFSET + Mech.LOC_LARM] = new PMSimplePolygonArea(inStLeftArm, unitDisplay, Mech.LOC_LARM);
        areas[INT_STRUCTURE_OFFSET + Mech.LOC_RLEG] = new PMSimplePolygonArea(inStRightLeg, unitDisplay, Mech.LOC_RLEG);
        areas[INT_STRUCTURE_OFFSET + Mech.LOC_LLEG] = new PMSimplePolygonArea(inStLeftLeg, unitDisplay, Mech.LOC_LLEG);
        heatImage = comp.createImage(10, 120);
        drawHeatControl();
        heatHotArea = new PMPicPolygonalArea(heatControl, heatImage);
    }

    private void setLabels() {
        FontMetrics fm = comp.getFontMetrics(FONT_LABEL);

        // Labels for Front view
        labels[Mech.LOC_HEAD] = WidgetUtils.createLabel(Messages.getString("MechMapSet.l_H"),
                fm, Color.black, 62, 6);
        labels[Mech.LOC_LARM] = WidgetUtils.createLabel(Messages.getString("MechMapSet.l_LA"),
                fm, Color.black, 14, 59);
        labels[Mech.LOC_LT] = WidgetUtils.createLabel(Messages.getString("MechMapSet.l_LT"),
                fm, Color.black, 41, 52);
        labels[Mech.LOC_CT] = WidgetUtils.createLabel(Messages.getString("MechMapSet.l_CT"),
                fm, Color.black, 62, 42);
        labels[Mech.LOC_RT] = WidgetUtils.createLabel(Messages.getString("MechMapSet.l_RT"),
                fm, Color.black, 84, 52);
        labels[Mech.LOC_RARM] = WidgetUtils.createLabel(Messages.getString("MechMapSet.l_RA"),
                fm, Color.black, 109, 59);
        labels[Mech.LOC_LLEG] = WidgetUtils.createLabel(Messages.getString("MechMapSet.l_LL"),
                fm, Color.black, 36, 92);
        labels[Mech.LOC_RLEG] = WidgetUtils.createLabel(Messages.getString("MechMapSet.l_RL"),
                fm, Color.black, 90, 92);
        // Labels for Back view
        labels[REAR_AREA_OFFSET + Mech.LOC_LT] = WidgetUtils.createLabel(Messages.getString("MechMapSet.l_LT"),
                fm, Color.black, 133, 39);
        labels[REAR_AREA_OFFSET + Mech.LOC_CT] = WidgetUtils.createLabel(Messages.getString("MechMapSet.l_CT"),
                fm, Color.black, 156, 25);
        labels[REAR_AREA_OFFSET + Mech.LOC_RT] = WidgetUtils.createLabel(Messages.getString("MechMapSet.l_RT"),
                fm, Color.black, 178, 39);
        // Labels for Internal Structure
        labels[INT_STRUCTURE_OFFSET + Mech.LOC_HEAD] = WidgetUtils.createLabel(Messages.getString("MechMapSet.l_H"),
                fm, Color.black, 63, 130);
        labels[INT_STRUCTURE_OFFSET + Mech.LOC_LARM] = WidgetUtils.createLabel(Messages.getString("MechMapSet.l_LA"),
                fm, Color.black, 14, 174);
        labels[INT_STRUCTURE_OFFSET + Mech.LOC_LT] = WidgetUtils.createLabel(Messages.getString("MechMapSet.l_LT"),
                fm, Color.black, 42, 166);
        labels[INT_STRUCTURE_OFFSET + Mech.LOC_CT] = WidgetUtils.createLabel(Messages.getString("MechMapSet.L_CT"),
                fm, Color.black, 63, 168);
        labels[INT_STRUCTURE_OFFSET + Mech.LOC_RT] = WidgetUtils.createLabel(Messages.getString("MechMapSet.l_RT"),
                fm, Color.black, 85, 166);
        labels[INT_STRUCTURE_OFFSET + Mech.LOC_RARM] = WidgetUtils.createLabel(Messages.getString("MechMapSet.L_RA"),
                fm, Color.black, 111, 174);
        labels[INT_STRUCTURE_OFFSET + Mech.LOC_LLEG] = WidgetUtils.createLabel(Messages.getString("MechMapSet.l_LL"),
                fm, Color.black, 33, 210);
        labels[INT_STRUCTURE_OFFSET + Mech.LOC_RLEG] = WidgetUtils.createLabel(Messages.getString("MechMapSet.l_RL"),
                fm, Color.black, 93, 210);
        labels[19] = WidgetUtils.createLabel(Messages.getString("MechMapSet.BARRating"),
                fm, Color.white, 65, 343);

        // Value labels for all parts of mek
        // front
        fm = comp.getFontMetrics(FONT_VALUE);
        vLabels[Mech.LOC_HEAD] = WidgetUtils.createValueLabel(62, 22, "", fm);
        vLabels[Mech.LOC_LARM] = WidgetUtils.createValueLabel(13, 72, "", fm);
        vLabels[Mech.LOC_LT] = WidgetUtils.createValueLabel(38, 44, "", fm);
        vLabels[Mech.LOC_CT] = WidgetUtils.createValueLabel(62, 57, "", fm);
        vLabels[Mech.LOC_RT] = WidgetUtils.createValueLabel(86, 44, "", fm);
        vLabels[Mech.LOC_RARM] = WidgetUtils.createValueLabel(112, 72, "", fm);
        vLabels[Mech.LOC_LLEG] = WidgetUtils.createValueLabel(31, 113, "", fm);
        vLabels[Mech.LOC_RLEG] = WidgetUtils.createValueLabel(94, 113, "", fm);

        // back
        vLabels[REAR_AREA_OFFSET + Mech.LOC_LT] = WidgetUtils.createValueLabel(132, 28, "", fm);
        vLabels[REAR_AREA_OFFSET + Mech.LOC_CT] = WidgetUtils.createValueLabel(156, 39, "", fm);
        vLabels[REAR_AREA_OFFSET + Mech.LOC_RT] = WidgetUtils.createValueLabel(177, 28, "", fm);

        // Internal structure
        vLabels[INT_STRUCTURE_OFFSET + Mech.LOC_HEAD] = WidgetUtils.createValueLabel(63, 142, "", fm);
        vLabels[INT_STRUCTURE_OFFSET + Mech.LOC_LARM] = WidgetUtils.createValueLabel(15, 187, "", fm);
        vLabels[INT_STRUCTURE_OFFSET + Mech.LOC_LT] = WidgetUtils.createValueLabel(42, 180, "", fm);
        vLabels[INT_STRUCTURE_OFFSET + Mech.LOC_CT] = WidgetUtils.createValueLabel(63, 182, "", fm);
        vLabels[INT_STRUCTURE_OFFSET + Mech.LOC_RT] = WidgetUtils.createValueLabel(85, 180, "", fm);
        vLabels[INT_STRUCTURE_OFFSET + Mech.LOC_RARM] = WidgetUtils.createValueLabel(111, 187, "", fm);
        vLabels[INT_STRUCTURE_OFFSET + Mech.LOC_LLEG] = WidgetUtils.createValueLabel(33, 223, "", fm);
        vLabels[INT_STRUCTURE_OFFSET + Mech.LOC_RLEG] = WidgetUtils.createValueLabel(92, 223, "", fm);
        // heat
        vLabels[19] = WidgetUtils.createValueLabel(155, 90, "", fm);
        // BAR rating
        vLabels[20] = WidgetUtils.createValueLabel(100, 345, "", fm);
    }

    private void setGroups() {
        // Have to remove it later
        PMAreasGroup frontArmor = new PMAreasGroup();
        PMAreasGroup rearArmor = new PMAreasGroup();
        PMAreasGroup intStructure = new PMAreasGroup();
        PMAreasGroup heat = new PMAreasGroup();

        for (int i = 0; i < 8; i++) {
            frontArmor.addArea(areas[i]);
            frontArmor.addArea(labels[i]);
            frontArmor.addArea(vLabels[i]);
        }

        for (int i = 0; i < 3; i++) {
            rearArmor.addArea(areas[8 + i]);
            rearArmor.addArea(labels[8 + i]);
            rearArmor.addArea(vLabels[8 + i]);
        }

        for (int i = 0; i < 8; i++) {
            intStructure.addArea(areas[11 + i]);
            intStructure.addArea(labels[11 + i]);
            intStructure.addArea(vLabels[11 + i]);
        }

        heat.addArea(heatHotArea);
        heat.addArea(vLabels[19]);

        frontArmor.translate(7, 18);
        rearArmor.translate(19, 20);
        intStructure.translate(6, 42);
        heat.translate(20, 52);

        // This have to be left
        for (int i = 0; i < 19; i++) {
            content.addArea(areas[i]);
            content.addArea(labels[i]);
            content.addArea(vLabels[i]);
        }

        content.addArea(heatHotArea);
        content.addArea(vLabels[19]);
    }

    private void setBackGround() {
        UnitDisplaySkinSpecification udSpec = SkinXMLHandler.getUnitDisplaySkin();

        Image tile = comp.getToolkit().getImage(
                new MegaMekFile(Configuration.widgetsDir(), udSpec.getBackgroundTile()).toString());
        PMUtil.setImage(tile, comp);
        int b = BackGroundDrawer.TILING_BOTH;
        bgDrawers.addElement(new BackGroundDrawer(tile, b));
        tile = comp.getToolkit().getImage(
                new MegaMekFile(Configuration.widgetsDir(), udSpec.getMechOutline()).toString());
        PMUtil.setImage(tile, comp);
        b = BackGroundDrawer.NO_TILING | BackGroundDrawer.VALIGN_CENTER | BackGroundDrawer.HALIGN_CENTER;
        BackGroundDrawer bgd = new BackGroundDrawer(tile, b);
        bgDrawers.addElement(bgd);
    }

    private void drawHeatControl() {
        drawHeatControl(0, false);
    }

    private void drawHeatControl(int t, boolean mtHeat) {
        int y = 0;
        int maxHeat, steps;
        if (mtHeat) {
            maxHeat = 50;
            steps = 2;
        } else {
            maxHeat = 30;
            steps = 4;
        }
        Graphics g = heatImage.getGraphics();
        for (int i = 0; i < maxHeat; i++) {
            y = 120 - (i + 1) * steps;
            if (i < t) {
                g.setColor(Color.red);
            } else {
                g.setColor(Color.lightGray);
            }
            g.fillRect(0, y, 10, steps);
            g.setColor(Color.black);
            g.drawRect(0, y, 10, steps);
        }
    }
}
