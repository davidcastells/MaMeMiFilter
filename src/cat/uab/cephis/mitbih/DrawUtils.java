/**
 * Copyright (C) David Castells-Rufas, CEPHIS, Universitat Autonoma de Barcelona  
 * david.castells@uab.cat
 * 
 * This work was used in the publication of "Simple real-time QRS detector with the MaMeMi filter"
 * available online on: http://www.sciencedirect.com/science/article/pii/S1746809415001032 
 * 
 * I encourage that you cite it as:
 * [*] Castells-Rufas, David, and Jordi Carrabina. "Simple real-time QRS detector with the MaMeMi filter." 
 *     Biomedical Signal Processing and Control 21 (2015): 137-145.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cat.uab.cephis.mitbih;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author dcr
 */
public class DrawUtils {

    /**
     * Draws a string inside a box
     * @param g
     * @param rect
     * @param str 
     */
    static void drawCenteredString(Graphics g, Rectangle rect, String str) {
        Graphics2D g2d = (Graphics2D) g;
        FontMetrics fm = g2d.getFontMetrics();
        Rectangle2D r = fm.getStringBounds(str, g2d);
        int dx = (int) ((rect.getWidth() - (int) r.getWidth()) / 2);
        int dy = (int) ((rect.getHeight() - (int) r.getHeight()) / 2 + fm.getAscent());
        g.drawString(str, rect.x + dx, rect.y + dy);    
    }
    
    static void drawCenteredString(Graphics g, int x, int y, String str) {
        Graphics2D g2d = (Graphics2D) g;
        FontMetrics fm = g2d.getFontMetrics();
        Rectangle2D r = fm.getStringBounds(str, g2d);
        int nx = (int) (x - (r.getWidth() / 2));
        int ny = (int) (y - (r.getHeight() / 2) + fm.getAscent());
        g.drawString(str, nx , ny);    
    }
    
}
