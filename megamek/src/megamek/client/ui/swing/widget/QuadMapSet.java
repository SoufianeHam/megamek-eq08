/*
 * Copyright (c) 2000-2002 - Ben Mazur (bmazur@sev.org).
 * Copyright (c) 2013 - Edward Cullen (eddy@obsessedcomputers.co.uk).
 * Copyright (c) 2022 - The MegaMek Team. All Rights Reserved.
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
 * Very cumbersome class that handles set of polygonal areas and labels for
 * PicMap component to represent single mech unit in MechDisplay
 */
public class QuadMapSet implements DisplayMapSet {

    // Because of keeping all areas of single type in one array
    // some index offset values required
    private static final int REAR_AREA_OFFSET = 7;
    private static final int INT_STRUCTURE_OFFSET = 11;

    private final UnitDisplay unitDisplay;
    
    // Array of polygonal areas - parts of mech body.
    private final PMSimplePolygonArea[] areas = new PMSimplePolygonArea[19];
    // Array of fixed labels - short names of body parts
    private final PMSimpleLabel[] labels = new PMSimpleLabel[19];
    // Array of value labels to show armor and IS values
    private final PMValueLabel[] vLabels = new PMValueLabel[20];
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
    private final Polygon rightArm = new Polygon(new int[] { 102, 102, 100, 95, 95,
            100, 110, 120, 120, 125 }, new int[] { 120, 70, 65, 65, 50, 55, 55,
            65, 115, 120 }, 10);
    private final Polygon leftArm = new Polygon(new int[] { 0, 5, 5, 15, 25, 30, 30,
            25, 23, 23 },
            new int[] { 120, 115, 65, 55, 55, 50, 65, 65, 70, 120 }, 10);
    private final Polygon head = new Polygon(new int[] { 50, 50, 55, 70, 75, 75 },
            new int[] { 40, 25, 20, 20, 25, 40 }, 6);
    private final Polygon centralTorso = new Polygon(new int[] { 50, 50, 75, 75 },
            new int[] { 80, 40, 40, 80 }, 4);
    private final Polygon leftTorso = new Polygon(
            new int[] { 50, 35, 30, 30, 35, 50 }, new int[] { 80, 80, 75, 45,
                    40, 40 }, 6);
    private final Polygon rightTorso = new Polygon(
            new int[] { 75, 75, 90, 95, 95, 90 }, new int[] { 80, 40, 40, 45,
                    75, 80 }, 6);
    private final Polygon leftLeg = new Polygon(new int[] { 30, 30, 35, 50, 50, 55 },
            new int[] { 120, 85, 80, 80, 115, 120 }, 6);
    private final Polygon rightLeg = new Polygon(
            new int[] { 70, 75, 75, 90, 95, 95 }, new int[] { 120, 115, 80, 80,
                    85, 120 }, 6);

    // Mek Armor - Rear
    private final Polygon rearLeftTorso = new Polygon(new int[] { 142, 142, 148, 139,
            123, 123, 142 }, new int[] { 14, 43, 76, 76, 44, 17, 14 }, 7);
    private final Polygon rearCentralTorso = new Polygon(new int[] { 142, 148, 162,
            168, 168, 142 }, new int[] { 44, 76, 76, 44, 14, 14 }, 6);
    private final Polygon rearRightTorso = new Polygon(new int[] { 168, 168, 162,
            171, 187, 187, 168 }, new int[] { 14, 43, 76, 76, 44, 17, 14 }, 7);

    // Internal Structure
    private final Polygon inStRightArm = new Polygon(new int[] { 102, 102, 100, 95,
            95, 100, 110, 120, 120, 125 }, new int[] { 112 + 120, 112 + 70,
            112 + 65, 112 + 65, 112 + 50, 112 + 55, 112 + 55, 112 + 65,
            112 + 115, 112 + 120 }, 10);
    private final Polygon inStLeftArm = new Polygon(new int[] { 0, 5, 5, 15, 25, 30,
            30, 25, 23, 23 }, new int[] { 112 + 120, 112 + 115, 112 + 65,
            112 + 55, 112 + 55, 112 + 50, 112 + 65, 112 + 65, 112 + 70,
            112 + 120 }, 10);
    private final Polygon intStHead = new Polygon(
            new int[] { 50, 50, 55, 70, 75, 75 }, new int[] { 112 + 40,
                    112 + 25, 112 + 20, 112 + 20, 112 + 25, 112 + 40 }, 6);
    private final Polygon inStCentralTorso = new Polygon(
            new int[] { 50, 50, 75, 75 }, new int[] { 112 + 80, 112 + 40,
                    112 + 40, 112 + 80 }, 4);
    private final Polygon inStLeftTorso = new Polygon(new int[] { 50, 35, 30, 30, 35,
            50 }, new int[] { 112 + 80, 112 + 80, 112 + 75, 112 + 45, 112 + 40,
            112 + 40 }, 6);
    private final Polygon inStRightTorso = new Polygon(new int[] { 75, 75, 90, 95,
            95, 90 }, new int[] { 112 + 80, 112 + 40, 112 + 40, 112 + 45,
            112 + 75, 112 + 80 }, 6);
    private final Polygon inStLeftLeg = new Polygon(new int[] { 30, 30, 35, 50, 50,
            55 }, new int[] { 112 + 120, 112 + 85, 112 + 80, 112 + 80,
            112 + 115, 112 + 120 }, 6);
    private final Polygon inStRightLeg = new Polygon(new int[] { 70, 75, 75, 90, 95,
            95 }, new int[] { 112 + 120, 112 + 115, 112 + 80, 112 + 80,
            112 + 85, 112 + 120 }, 6);

    // Heat control
    private final Polygon heatControl = new Polygon(new int[] { 149, 159, 159, 149 },
            new int[] { 100, 100, 220, 220 }, 4);

    private Image heatImage;

    private static final Font FONT_LABEL = new Font(MMConstants.FONT_SANS_SERIF, Font.PLAIN,
            GUIPreferences.getInstance().getInt("AdvancedMechDisplayArmorSmallFontSize"));
    private static final Font FONT_VALUE = new Font(MMConstants.FONT_SANS_SERIF, Font.PLAIN,
            GUIPreferences.getInstance().getInt("AdvancedMechDisplayArmorLargeFontSize"));

    public QuadMapSet(JComponent c, UnitDisplay unitDisplay) {
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
        boolean mtHeat = false;
        if (e.getGame() != null
                && e.getGame().getOptions().booleanOption(OptionsConstants.ADVCOMBAT_TACOPS_HEAT)) {
            mtHeat = true;
        }
        int a = 1;
        int a0 = 1;
        for (int i = 0; i < m.locations(); i++) {
            a = m.getArmor(i);
            a0 = m.getOArmor(i);
            vLabels[i].setValue(m.getArmorString(i));
            WidgetUtils.setAreaColor(areas[i], vLabels[i], (double) a
                    / (double) a0);
            if (m.hasRearArmor(i)) {
                a = m.getArmor(i, true);
                a0 = m.getOArmor(i, true);
                vLabels[i + REAR_AREA_OFFSET].setValue(m
                        .getArmorString(i, true));
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
        areas[REAR_AREA_OFFSET + Mech.LOC_CT] = new PMSimplePolygonArea(
                rearCentralTorso, unitDisplay, Mech.LOC_CT);
        areas[REAR_AREA_OFFSET + Mech.LOC_RT] = new PMSimplePolygonArea(
                rearRightTorso, unitDisplay, Mech.LOC_RT);
        areas[REAR_AREA_OFFSET + Mech.LOC_LT] = new PMSimplePolygonArea(
                rearLeftTorso, unitDisplay, Mech.LOC_LT);
        areas[INT_STRUCTURE_OFFSET + Mech.LOC_HEAD] = new PMSimplePolygonArea(
                intStHead, unitDisplay, Mech.LOC_HEAD);
        areas[INT_STRUCTURE_OFFSET + Mech.LOC_CT] = new PMSimplePolygonArea(
                inStCentralTorso, unitDisplay, Mech.LOC_CT);
        areas[INT_STRUCTURE_OFFSET + Mech.LOC_RT] = new PMSimplePolygonArea(
                inStRightTorso, unitDisplay, Mech.LOC_RT);
        areas[INT_STRUCTURE_OFFSET + Mech.LOC_LT] = new PMSimplePolygonArea(
                inStLeftTorso, unitDisplay, Mech.LOC_LT);
        areas[INT_STRUCTURE_OFFSET + Mech.LOC_RARM] = new PMSimplePolygonArea(
                inStRightArm, unitDisplay, Mech.LOC_RARM);
        areas[INT_STRUCTURE_OFFSET + Mech.LOC_LARM] = new PMSimplePolygonArea(
                inStLeftArm, unitDisplay, Mech.LOC_LARM);
        areas[INT_STRUCTURE_OFFSET + Mech.LOC_RLEG] = new PMSimplePolygonArea(
                inStRightLeg, unitDisplay, Mech.LOC_RLEG);
        areas[INT_STRUCTURE_OFFSET + Mech.LOC_LLEG] = new PMSimplePolygonArea(
                inStLeftLeg, unitDisplay, Mech.LOC_LLEG);
        heatImage = comp.createImage(10, 120);
        drawHeatControl(0);
        heatHotArea = new PMPicPolygonalArea(heatControl, heatImage);
    }

    private void setLabels() {
        FontMetrics fm = comp.getFontMetrics(FONT_LABEL);

        // Labels for Front view
        labels[Mech.LOC_HEAD] = WidgetUtils.createLabel(Messages.getString("MechMapSet.l_H"),
                fm, Color.black, 58, 29);
        labels[Mech.LOC_LARM] = WidgetUtils.createLabel(Messages.getString("QuadMapSet.L_LA"),
                fm, Color.black, 14, 69);
        labels[Mech.LOC_LT] = WidgetUtils.createLabel(Messages.getString("MechMapSet.l_LT"),
                fm, Color.black, 41, 52);
        labels[Mech.LOC_CT] = WidgetUtils.createLabel(Messages.getString("MechMapSet.l_CT"),
                fm, Color.black, 62, 45);
        labels[Mech.LOC_RT] = WidgetUtils.createLabel(Messages.getString("MechMapSet.l_RT"),
                fm, Color.black, 84, 52);
        labels[Mech.LOC_RARM] = WidgetUtils.createLabel(Messages.getString("QuadMapSet.L_RA"),
                fm, Color.black, 111, 69);
        labels[Mech.LOC_LLEG] = WidgetUtils.createLabel(Messages.getString("QuadMapSet.L_LL"),
                fm, Color.black, 39, 87);
        labels[Mech.LOC_RLEG] = WidgetUtils.createLabel(Messages.getString("QuadMapSet.L_RL"),
                fm, Color.black, 85, 87);
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
        labels[INT_STRUCTURE_OFFSET + Mech.LOC_LARM] = WidgetUtils.createLabel(Messages.getString("QuadMapSet.L_LA"),
                fm, Color.black, 14, 179);
        labels[INT_STRUCTURE_OFFSET + Mech.LOC_LT] = WidgetUtils.createLabel(Messages.getString("MechMapSet.l_LT"),
                fm, Color.black, 42, 166);
        labels[INT_STRUCTURE_OFFSET + Mech.LOC_CT] = WidgetUtils.createLabel(Messages.getString("MechMapSet.L_CT"),
                fm, Color.black, 63, 160);
        labels[INT_STRUCTURE_OFFSET + Mech.LOC_RT] = WidgetUtils.createLabel(Messages.getString("MechMapSet.l_RT"),
                fm, Color.black, 85, 166);
        labels[INT_STRUCTURE_OFFSET + Mech.LOC_RARM] = WidgetUtils.createLabel(Messages.getString("QuadMapSet.L_RA"),
                fm, Color.black, 111, 179);
        labels[INT_STRUCTURE_OFFSET + Mech.LOC_LLEG] = WidgetUtils.createLabel(Messages.getString("QuadMapSet.L_LL"),
                fm, Color.black, 39, 200);
        labels[INT_STRUCTURE_OFFSET + Mech.LOC_RLEG] = WidgetUtils.createLabel(Messages.getString("QuadMapSet.L_RL"),
                fm, Color.black, 85, 200);

        // Value labels for all parts of mek
        // front
        fm = comp.getFontMetrics(FONT_VALUE);
        vLabels[Mech.LOC_HEAD] = WidgetUtils.createValueLabel(68, 30, "", fm);
        vLabels[Mech.LOC_LARM] = WidgetUtils.createValueLabel(13, 82, "", fm);
        vLabels[Mech.LOC_LT] = WidgetUtils.createValueLabel(40, 66, "", fm);
        vLabels[Mech.LOC_CT] = WidgetUtils.createValueLabel(62, 60, "", fm);
        vLabels[Mech.LOC_RT] = WidgetUtils.createValueLabel(85, 66, "", fm);
        vLabels[Mech.LOC_RARM] = WidgetUtils.createValueLabel(112, 82, "", fm);
        vLabels[Mech.LOC_LLEG] = WidgetUtils.createValueLabel(39, 103, "", fm);
        vLabels[Mech.LOC_RLEG] = WidgetUtils.createValueLabel(85, 103, "", fm);

        // back
        vLabels[REAR_AREA_OFFSET + Mech.LOC_LT] = WidgetUtils.createValueLabel(132, 28, "", fm);
        vLabels[REAR_AREA_OFFSET + Mech.LOC_CT] = WidgetUtils.createValueLabel(156, 39, "", fm);
        vLabels[REAR_AREA_OFFSET + Mech.LOC_RT] = WidgetUtils.createValueLabel(177, 28, "", fm);

        // Internal structure
        vLabels[INT_STRUCTURE_OFFSET + Mech.LOC_HEAD] = WidgetUtils.createValueLabel(63, 142, "", fm);
        vLabels[INT_STRUCTURE_OFFSET + Mech.LOC_LARM] = WidgetUtils.createValueLabel(15, 192, "", fm);
        vLabels[INT_STRUCTURE_OFFSET + Mech.LOC_LT] = WidgetUtils.createValueLabel(42, 180, "", fm);
        vLabels[INT_STRUCTURE_OFFSET + Mech.LOC_CT] = WidgetUtils.createValueLabel(63, 175, "", fm);
        vLabels[INT_STRUCTURE_OFFSET + Mech.LOC_RT] = WidgetUtils.createValueLabel(85, 180, "", fm);
        vLabels[INT_STRUCTURE_OFFSET + Mech.LOC_RARM] = WidgetUtils.createValueLabel(111, 192, "", fm);
        vLabels[INT_STRUCTURE_OFFSET + Mech.LOC_LLEG] = WidgetUtils.createValueLabel(39, 215, "", fm);
        vLabels[INT_STRUCTURE_OFFSET + Mech.LOC_RLEG] = WidgetUtils.createValueLabel(85, 215, "", fm);

        // heat
        vLabels[19] = WidgetUtils.createValueLabel(155, 90, "", fm);
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
        UnitDisplaySkinSpecification udSpec = SkinXMLHandler
                .getUnitDisplaySkin();

        Image tile = comp.getToolkit()
                .getImage(
                        new MegaMekFile(Configuration.widgetsDir(), udSpec
                                .getBackgroundTile()).toString());
        PMUtil.setImage(tile, comp);
        int b = BackGroundDrawer.TILING_BOTH;
        bgDrawers.addElement(new BackGroundDrawer(tile, b));
        tile = comp.getToolkit().getImage(
                new MegaMekFile(Configuration.widgetsDir(), udSpec.getMechOutline())
                        .toString());
        PMUtil.setImage(tile, comp);
        b = BackGroundDrawer.NO_TILING | BackGroundDrawer.VALIGN_CENTER
                | BackGroundDrawer.HALIGN_CENTER;
        BackGroundDrawer bgd = new BackGroundDrawer(tile, b);
        bgDrawers.addElement(bgd);
    }

    private void drawHeatControl(int t) {
        drawHeatControl(t, false);
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
