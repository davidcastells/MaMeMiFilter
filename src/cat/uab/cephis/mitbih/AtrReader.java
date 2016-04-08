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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 *
 * @author dcr
 */
public class AtrReader
{
    private final BufferedInputStream bis;
    private final int channel;
    
    public AtrReader(File file, int channel) throws FileNotFoundException, MalformedURLException, IOException
    {
        if (!file.exists())
        {
            URL url = new URL("http://www.physionet.org/physiobank/database/mitdb/"+file.getName());
            
            System.out.println("Downloading " + url);

            Wget.get(url, file);
        }
        
        bis = new BufferedInputStream(new FileInputStream(file));
        
        this.channel = channel;
               
        if ((channel < 0) || (channel > 1)) 
            throw new IllegalArgumentException("Supported channels are 0 and 1");
    }
    
    /**
     * Reads a sample from the first channel of the DAT file.
     * 
     * The DAT file is organized in 212 format  i.e.
     * 
     * [ 7 6 5 4 3 2 1 0 ] [ 7 6 4 5  3 2 1 0 ] [ 7 6 4 5 3 2 1 0 ] 
     * [  Channel 0 Low  ] [ Ch 1 H ][ Ch 0 L ] [  Channel 1 Low  ]
     * 
     * @return -1 if nothing read
     * 
     * @throws java.io.IOException 
     */
    public int get() throws IOException
    {
        byte[] buff = new byte[3];
        
        int n = bis.read(buff, 0, 3);
        
        if (n == -1)
            return -1;
        
        if (n != 3)
            throw new RuntimeException("Read less than 3 = " +  n);
        
        if (channel == 0)
        {
            int r = ((((byte)(buff[1] << 4) >> 4) << 8)) | (buff[0] & 0xFF);
            //System.out.println("r=" + r);
            return r;
        }
        else
        {
            int r = (( (buff[1] & 0xF0)) << (8-4)) | (buff[2] & 0xFF);
            return r;
        }
    }
    
    /**
     * 
     * @param buff
     * @return the number of bytes read
     * @throws IOException 
     */
//    public int getBeats() throws IOException
//    {
//        for (int i=0; i < buff.length; i++)
//        {
//            buff[i] = get();
//            
//            if (buff[i] == -1)
//            {
//                buff[i] = 0;
//                return i-1;
//            }
//        }
//        
//        return buff.length;
//    }

    void skip(int k) throws IOException {
        for (int i= 0; i < k; i++)
            get();
        
    }

//    
//    #define	NOTQRS	0	/* not-QRS (not a getann/putann code) */
//#define NORMAL	1	/* normal beat */
//#define	LBBB	2	/* left bundle branch block beat */
//#define	RBBB	3	/* right bundle branch block beat */
//#define	ABERR	4	/* aberrated atrial premature beat */
//#define	PVC	5	/* premature ventricular contraction */
//#define	FUSION	6	/* fusion of ventricular and normal beat */
//#define	NPC	7	/* nodal (junctional) premature beat */
//#define	APC	8	/* atrial premature contraction */
//#define	SVPB	9	/* premature or ectopic supraventricular beat */
//#define	VESC	10	/* ventricular escape beat */
//#define	NESC	11	/* nodal (junctional) escape beat */
//#define	PACE	12	/* paced beat */
//#define	UNKNOWN	13	/* unclassifiable beat */
//#define	NOISE	14	/* signal quality change */
//#define ARFCT	16	/* isolated QRS-like artifact */
//#define STCH	18	/* ST change */
//#define TCH	19	/* T-wave change */
//#define SYSTOLE	20	/* systole */
//#define DIASTOLE 21	/* diastole */
//#define	NOTE	22	/* comment annotation */
//#define MEASURE 23	/* measurement annotation */
//#define PWAVE	24	/* P-wave peak */

//#define	PACESP	26	/* non-conducted pacer spike */
//#define TWAVE	27	/* T-wave peak */
//#define RHYTHM	28	/* rhythm change */
//#define UWAVE	29	/* U-wave peak */
//#define	FLWAV	31	/* ventricular flutter wave */
//#define	VFON	32	/* start of ventricular flutter/fibrillation */
//#define	VFOFF	33	/* end of ventricular flutter/fibrillation */
//#define	AESC	34	/* atrial escape beat */
//#define LINK    36	/* link to external data (aux contains URL) */
//#define	NAPC	37	/* non-conducted P-wave (blocked APB) */
//#define	PFUS	38	/* fusion of paced and normal beat */
//#define WFON	39	/* waveform onset */
//#define PQ	WFON	/* PQ junction (beginning of QRS) */
//#define WFOFF	40	/* waveform end */
//#define	JPT	WFOFF	/* J point (end of QRS) */
//#define RONT	41	/* R-on-T premature ventricular contraction */
    
    public static int NOTQRS = 0;
    public static int NORMAL = 1;       /* normal beat */
    public static int LBBB	= 2;	/* left bundle branch block beat */
    public static int RBBB = 	3;	/* right bundle branch block beat */
    public static int ABERR = 	4;	/* aberrated atrial premature beat */
    public static int PVC =5;   	/* premature ventricular contraction */
    public static int FUSION	= 6;	/* fusion of ventricular and normal beat */
    public static int  NPC = 	7;	/* nodal (junctional) premature beat */
    public static int APC = 8;          /* atrial premature contraction */
    public static int SVPB = 9; 	/* premature or ectopic supraventricular beat */
    public static int VESC	=10;	/* ventricular escape beat */
    public static int NESC = 	11;	/* nodal (junctional) escape beat */
    public static int PACE = 	12;	/* paced beat */
    public static int UNKNOWN = 	13;	/* unclassifiable beat */
    public static int NOISE = 14;
    public static int ARFCT	= 16;	/* isolated QRS-like artifact */
    public static int BBB	 = 25;	/* left or right bundle branch block */
    public static int RHYTHM = 28;
    public static int LEARN = 	30;	/* learning */

    public static int FLWAV	=31;	/* ventricular flutter wave */
    public static int VFON	=32;	/* start of ventricular flutter/fibrillation */
    public static int VFOFF	=33;	/* end of ventricular flutter/fibrillation */
    public static int AESC	=34;	/* atrial escape beat */
    public static int SVESC = 	35;	/* supraventricular escape beat */
    public static int NAPC = 	37;	/* non-conducted P-wave (blocked APB) */
    public static int PFUS = 	38;	/* fusion of paced and normal beat */
    public static int NOTE = 	22;	/* comment annotation */;
        public static int RONT = 41;    	/* R-on-T premature ventricular contraction */

    public static int SKIP = 59;
    public static int NUM = 60;
    public static int SUBTYP = 61;
    public static int AUX = 63;

    public boolean verbose = false;

    int[] getBeatsAfter(int v) throws IOException
    {
        int[] r = getBeats();
        ArrayList<Integer> beats = new ArrayList<>();
        
        for (int i=0; i < r.length; i++)
        {
            if (r[i] > v)
                beats.add(r[i]-v);
        }
        
        int[] aBeats = new int[beats.size()];
        
        for (int i=0; i < beats.size(); i++)
        {
            aBeats[i] = beats.get(i).intValue();
        }
        
        return aBeats;
    }
    
    /**
     * [ 7 6 5 4 3 2 1 0 ] [ 7 6 5 4 3 2  1 0 ]  
     * [  Time Low       ] [ typecode   ][ TH ] 
     * @return
     * @throws IOException 
     */
    int[] getBeats() throws IOException 
    {
        ArrayList<Integer> beats = new ArrayList<>();
        
        byte[] buff = new byte[2];
        
        int n=0;
        int totalTime = 0;
        
        do
        {
            n = bis.read(buff, 0, 2);
        
            if (n == -1)
                break;
        
            int time = (buff[0] & 0xFF)  | (buff[1] & 0x03)<< 8;
            int typecode = (buff[1]>>2) & 0x3F;
            
           if ((typecode == BBB) || (typecode == RONT) || (typecode == SVESC) || (typecode == LEARN))
	   {
	       throw new RuntimeException("Found unhandled BEAT typecode " + typecode);
	   }
            if (( typecode == NORMAL) || (typecode == LBBB) || (typecode == PVC) || (typecode == APC) 
                    || (typecode == SVPB) || (typecode == ABERR) || (typecode == NPC)|| (typecode == NESC) 
                    || (typecode == FUSION) || (typecode == UNKNOWN) || (typecode == RBBB) || (typecode == PACE)
                    || (typecode == PFUS) || (typecode == VESC) || (typecode == AESC))
            {
                totalTime += time;
                beats.add(totalTime);
                
                if (verbose) System.out.println("BEAT("+typecode+") at " + totalTime );
            }
            else if (typecode == NOTQRS)
            {
                totalTime += time;
            }
            else if (typecode == VFON)
            {
                totalTime += time;
            }
            else if (typecode == VFOFF)
            {
                totalTime += time;
            }
            else if (typecode == SKIP)
            {
                
                byte[] aux = new byte[4];
                int n2 = bis.read(aux, 0, 4);
                
                int period = (aux[0] & 0xff) << 16 | (aux[1] & 0xff) << 24 | (aux[2]& 0xff) | (aux[3]& 0xff) << 8;

                if (verbose) System.out.println("Skip:" + period + " in " + totalTime);
                
                totalTime += period;
            }
            else if (typecode == NOTE)
            {
                totalTime += time;
            }
            else if (typecode == FLWAV)
            {
                totalTime += time;
            }
            else if (typecode == ARFCT)
            {
                totalTime += time;
            }
            else if (typecode == AUX)
            {
                int tr = time;
                if ((tr % 2)== 1)
                    tr++;
                
                byte[] aux = new byte[tr];
                
                int n2 = bis.read(aux, 0, tr);
                
                if (verbose) System.out.println("Aux:"  + new String(aux, 0, time-1, Charset.defaultCharset()));
            }
            else if (typecode == SUBTYP)
            {
                if (verbose) System.out.println("Subtype:" + time);
            }
            else if (typecode == NUM)
            {
                if (verbose) System.out.println("Num field: " + time);
            }
            else if (typecode == RHYTHM)
            {
                if (verbose) System.out.println("Rhythm Change at " + totalTime);
                totalTime += time;
            }
            else if (typecode == NOISE)
            {
                if (verbose) System.out.println("Noise at " + time);
                totalTime += time;
            }
            else if (typecode == NAPC)
            {
                if (verbose) System.out.println("NAPC " + time);
                totalTime += time;
            }
            else
            {
                if (typecode < 50)
                {
                    totalTime += time;
                    System.err.println("Unhandled typecode " + typecode + " at time "  + totalTime);
                    System.exit(-1);
                }
                else
                {
                    System.err.println("Unexpected typecode " + typecode);
                }
                    
            }            
        } while (n != -1);
        
        
        int[] aBeats = new int[beats.size()];
        
        for (int i=0; i < beats.size(); i++)
        {
            aBeats[i] = beats.get(i).intValue();
        }
        
        return aBeats;
    }
}
