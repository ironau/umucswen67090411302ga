/*
 * JENES
 * A time and memory efficient Java library for genetic algorithms and more 
 * Copyright (C) 2011 Intelligentia srl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */
package jenes.tutorials.problem11;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MediaTracker;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * An utility JPanel that renders an image given as argument of the constructor
 * 
 * @since 2.0
 */
public class ImagePanel extends JPanel {

    private BufferedImage image;

    public ImagePanel(BufferedImage image) {
        this.image = image;

        MediaTracker mt = new MediaTracker(this);
        mt.addImage(image, 0);
        try {
            mt.waitForID(0);
        } catch (Exception e) {
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
         
        //draw image centered in panel
        int x = (getWidth() - image.getWidth(this)) / 2;
        int y = (getHeight() - image.getHeight(this)) / 2;
        g2d.drawImage(image, x, y, this);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(image.getWidth(this), image.getHeight(this));
    }

}
