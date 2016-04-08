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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static cat.uab.cephis.mitbih.ECGProcess.SELECT_VALUE;

/**
 *
 * @author dcr
 */
public class TestSquareSignal {
    public static void main(String[] args) 
    {
	try {
	    
	    DatReader dr0 = new DatReader(new File(ECGProcess.dir, "101.dat"), 0);
	    
	    double[] chunk1 = new double[1000000];
	    
	    
	    int read0  = dr0.get(chunk1);
	    
	    double[] chunk0 = SignalGenerator.greateSquareSignal(10, 1, 360, 150, 1024);
	    
	    double[] filtered1_1 = ECGProcess.minMaxFilter(chunk0,  1, 1, SELECT_VALUE   );
	    double[] filtered1_2 = ECGProcess.minMaxFilter(chunk0,  1, 2, SELECT_VALUE   );
	    double[] filtered2_1 = ECGProcess.minMaxFilter(chunk0,  2, 1, SELECT_VALUE   );
	    double[] filtered2_2 = ECGProcess.minMaxFilter(chunk0,  2, 2, SELECT_VALUE   );
	    Plot1D plot;
	    
	    plot = new Plot1D("Source");
	    plot.drawYAxis = true;
	    plot.setData(chunk1, chunk0.length);
	    plot.setData2(chunk0);
	    //plot.setData2(filtered);
//plot.setZoom(300);
	    plot.setVisible(true);

	    
	    plot = new Plot1D("Filter 1_1");
	    plot.drawYAxis = true;
	    plot.setData(filtered1_1, chunk0.length);
	    plot.setVisible(true);

	    plot = new Plot1D("Filter 1_2");
	    plot.drawYAxis = true;
	    plot.setData(filtered1_2, chunk0.length);
	    plot.setVisible(true);

	    plot = new Plot1D("Filter 2_1");
	    plot.drawYAxis = true;
	    plot.setData(filtered2_1, chunk0.length);
	    plot.setVisible(true);
	    
	    plot = new Plot1D("Filter 2_2");
	    plot.drawYAxis = true;
	    plot.setData(filtered2_2, chunk0.length);
	    plot.setVisible(true);
	} catch (MalformedURLException ex) {
	    Logger.getLogger(TestSquareSignal.class.getName()).log(Level.SEVERE, null, ex);
	} catch (IOException ex) {
	    Logger.getLogger(TestSquareSignal.class.getName()).log(Level.SEVERE, null, ex);
	}
	
    }

    
}
