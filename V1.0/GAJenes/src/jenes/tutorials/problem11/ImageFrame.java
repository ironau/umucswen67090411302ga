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

import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

/**
 * An utility class that shows 1 or 2 images in a frame using {@link ImagePanel}
 * 
 * @since 2.0
 */
public class ImageFrame extends JFrame {

    public ImageFrame(BufferedImage image) {
        this.add(new ImagePanel(image));
        
        this.setSize(300, 300);
        this.pack();
    }
    
    public ImageFrame(BufferedImage left, BufferedImage right) {
        this.setLayout(new GridLayout(1, 2));
        this.add(new ImagePanel(left));
        this.add(new ImagePanel(right));
        
        this.setSize(600, 300);
        this.pack();
    }
    
}
