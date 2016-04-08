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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JPanel;

/**
 *
 * @author dcr
 */
public class Plot1D extends javax.swing.JFrame {
    private double[] data;
    private double[] data2;
    private double[] data3;
    private double dataMax;
    private double dataMin;
    private final Plot1DPanel panel;
    private int len;
    private int[] beats;
    private int skipped;
    
    boolean drawYAxis;

    /**
     * Creates new form Plot1D
     */
    public Plot1D(String name)
    {
        setTitle(name);
        
        initComponents();
        
        panel = new Plot1DPanel();
        
        panel.setPreferredSize(new Dimension(1000, 500));
        
        getContentPane().add(panel);
        
        pack();
    }

    
    
    
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        btnZoomIn = new javax.swing.JButton();
        btnZoomOut = new javax.swing.JButton();
        btnLeft = new javax.swing.JButton();
        btnRight = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jToolBar1.setRollover(true);

        btnZoomIn.setText("zoom in");
        btnZoomIn.setFocusable(false);
        btnZoomIn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnZoomIn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnZoomIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnZoomInActionPerformed(evt);
            }
        });
        jToolBar1.add(btnZoomIn);

        btnZoomOut.setText("zoom out");
        btnZoomOut.setFocusable(false);
        btnZoomOut.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnZoomOut.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnZoomOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnZoomOutActionPerformed(evt);
            }
        });
        jToolBar1.add(btnZoomOut);

        btnLeft.setText("<<<");
        btnLeft.setFocusable(false);
        btnLeft.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnLeft.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLeftActionPerformed(evt);
            }
        });
        jToolBar1.add(btnLeft);

        btnRight.setText(">>>");
        btnRight.setFocusable(false);
        btnRight.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRight.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRightActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRight);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.PAGE_START);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnZoomInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnZoomInActionPerformed
        panel.zoomIn();
    }//GEN-LAST:event_btnZoomInActionPerformed

    private void btnRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRightActionPerformed
        panel.moveRight();
    }//GEN-LAST:event_btnRightActionPerformed

    private void btnLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLeftActionPerformed
        panel.moveLeft();
    }//GEN-LAST:event_btnLeftActionPerformed

    private void btnZoomOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnZoomOutActionPerformed
        panel.zoomOut();
    }//GEN-LAST:event_btnZoomOutActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Plot1D.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Plot1D.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Plot1D.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Plot1D.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Plot1D("").setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLeft;
    private javax.swing.JButton btnRight;
    private javax.swing.JButton btnZoomIn;
    private javax.swing.JButton btnZoomOut;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables

    void setData(double[] chunk, int len) {
        data = chunk;
        this.len = len;
        
        panel.repaint();
    }
    
    void setData(double[] chunk) {
        setData(chunk, chunk.length);
    }


    private void findMaxMin(int start, int slen) {
        dataMax = data[start];
        dataMin = data[start];
        
        for (int i=1; (i < len) && (i < (start + slen)); i++)
        {
            if ((start+i) >= data.length)
                continue;
            
            if (data[start+i] > dataMax) dataMax = data[start+i];
            if (data[start+i] < dataMin) dataMin = data[start+i];
        }
    }

    void setZoom(double i) {
        panel.zoom = i;
    }

    void setOffset(int i)
    {
        if (i < 0)
            return;
        
        panel.offsetX = i;
        panel.repaint();
    }

    void setAnnotation(int[] beats) {
        this.beats = beats;
    }

    void setData2(double[] ret3) {
        data2 = ret3;
    }
    
    void setData3(double[] ret3) {
        data3 = ret3;
    }

    void setSkipped(int skipped) {
        this.skipped = skipped;
        
    }
    
    class Plot1DPanel extends JPanel
    {
        double zoom = 1;
        private int offsetX;
	

        public Plot1DPanel() 
        {
            super(true);
            
            setOpaque(false);
            setBackground(Color.WHITE);
        }

        /**
         * 
         * @param g 
         */
        @Override
        protected void paintComponent(Graphics g) {

            int w = this.getWidth();
            int h = this.getHeight();

            
            int digitizerScale = 2048;  // 2^11
            int mVScale = 10;   // (-5, 5) mV
            int digitizerZero = 1024;
            int unitsPermV = digitizerScale/mVScale;
            
            
            g.setColor(Color.white);
            g.fillRect(0, 0, w, h);
            
            g.setColor(Color.black);
            
            int m = 30;

            double rangeWindowY = h - 3 * m;
            double rangeDataX = len / zoom;
            double rangeWindowX = w - 3 * m;

            findMaxMin(offsetX, (int) rangeDataX);

            double rangeDataY = dataMax - dataMin;

            // vertical axis
            g.drawLine(2*m, m, 2*m, h-2*m);
            g.drawLine(w-m, m, w-m, h-2*m);
            
            // horizontal axis
            g.drawLine(2*m, h-2*m, w-m, h-2*m);
            g.drawLine(2*m, m, w-m, m);
            
            // delta is number of units of data per pixel
            double dx = rangeWindowX / rangeDataX;  // pixels/unit
            double dy = rangeWindowY / rangeDataY;
            
            
            int yAxisSpacing = axisScale((int) (rangeWindowY / (unitsPermV * 5))) * unitsPermV;
            
            // draw x axis separators
            int fs = 360;   // 360 Hz is the sampling freq
            int xAxisSpacing = axisScale((int) (rangeDataX / (fs * 20))) * fs;
            for (int i= nextMultiple(offsetX, xAxisSpacing) - offsetX; 
                    i < rangeDataX; i+= xAxisSpacing)
            {
                int x1 = 2*m + (int) (i * dx);
                int y1 = h -2*m  - m/2;
                int y2 = h -2*m  + m/2;
                
                g.drawLine(x1, y1, x1, y2);
                
                DrawUtils.drawCenteredString(g, x1, y2 + m/2, "" + ((skipped+ offsetX + i)/fs) + "");
//                g.drawString("" + ((skipped+ offsetX + i)/fs) + "", x1, y2);
            }
            
//            // draw y axis separators
	    if (drawYAxis)
            for (int i=0; i< (h - 2*m); i+= yAxisSpacing)
            {
                int x1 = 2*m - m/2;
                int x2 = 2*m + m/2;
                int y1 = h - (2*m + i);
                
                g.drawLine(x1, y1, x2, y1);
 
                DrawUtils.drawCenteredString(g, x1-m/2, y1, "" + (int)((dataMin + (i/dy)-digitizerZero))/unitsPermV);
//                g.drawString("" + (int)(dataMin + (i/dy)), x1, y1 );
            }
            
            g.setColor(Color.BLUE);
            
            int zeroyInPixels = h - 2*m - (int) ((0 - dataMin) * dy);
                    
            // draw data
            for (int i=1; i < rangeDataX; i++)
            {
                if ((i + offsetX) >= len)
                    break;
                            
                int x1 = 2*m + (int) ((i-1) * dx);
                int x2 = 2*m + (int) (i * dx);
                int y1 = zeroyInPixels - (int) (data[offsetX + i-1] * dy);
                int y2 = zeroyInPixels - (int) (data[offsetX + i] * dy);
                
                g.drawLine(x1, y1, x2, y2);
            }
            
            
            // draw beats
            if (beats != null)
            {
                int minIndex = offsetX;
                int maxIndex = (int) (offsetX + rangeDataX);
                
                for (int i=0; i < beats.length; i++)
                {
                    int index = beats[i];
                    
                    if (index < minIndex)
                        continue;
                    if (index >= maxIndex)
                        break;
                    
                    int x1 = 2*m + (int) ((index-offsetX) * dx);
                    
                    int y1 = m;
                    int y2 = h-2*m;
                    
                    g.setColor(Color.red);
                    g.drawLine(x1, y1, x1, y2);
                }
            }
            
            // draw data 2

            g.setColor(Color.red);

            if (data2 != null)
            for (int i=1; i < rangeDataX; i++)
            {
                if ((i + offsetX) >= len)
                    break;
                            
                int x1 = 2*m + (int) ((i-1) * dx);
                int x2 = 2*m + (int) (i * dx);
                int y1 = zeroyInPixels - (int) (data2[offsetX + i-1] * dy);
                int y2 = zeroyInPixels - (int) (data2[offsetX + i] * dy);
                
                g.drawLine(x1, y1, x2, y2);
            }
            
            // draw data 3
            if (data3 != null)
            for (int i=1; i < rangeDataX; i++)
            {
                if ((i + offsetX) >= len)
                    break;
                            
                int x1 = 2*m + (int) ((i-1) * dx);
                int x2 = 2*m + (int) (i * dx);
                int y1 = zeroyInPixels - (int) (data3[offsetX + i-1] * dy);
                int y2 = zeroyInPixels - (int) (data3[offsetX + i] * dy);
                
                g.drawLine(x1, y1, x2, y2);
            }
        }

        

        private void moveRight() {
            int rangeDataX = (int) (len / zoom);
            
            offsetX += rangeDataX / 2;
            
            if (offsetX >= (len - rangeDataX))
                offsetX = len - rangeDataX;
            
            repaint();
        }

        private void moveLeft() {
            int rangeDataX = (int) (len / zoom);
            
            offsetX -= rangeDataX / 2;
            
            if (offsetX < 0)
                offsetX = 0;
            
            repaint();
        }

        private void zoomIn() 
        {
            zoom *= 1.1;
            
            
            repaint();
        }
        
        private void zoomOut() {
            zoom /= 1.1;
            
            
            repaint();
        }

        private int axisScale(int d) 
        {
            if (d < 5)
                return 1;
            
            if (d < 10)
                return 5;
            if (d < 100)
                return 50;
            if (d < 1000)
                return 500;
            else
                return 1000;
        }
        
        int nextMultiple(int value, int m)
        {            
            while (true)
            {
                if ((value % m) == 0)
                    return value;
                
                value++;
            }
         
        }
        
    }
}



        