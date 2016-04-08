/**
 * 
 * This work was used in the publication of "Simple real-time QRS detector with the MaMeMi filter"
 * available online on: http://www.sciencedirect.com/science/article/pii/S1746809415001032 
 * 
 * I encourage that you cite it as:
 * [*] Castells-Rufas, David, and Jordi Carrabina. "Simple real-time QRS detector with the MaMeMi filter." 
 *     Biomedical Signal Processing and Control 21 (2015): 137-145.
 * 
 */
package cat.uab.cephis.mitbih;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;



/** This class does a simple HTTP GET and writes the retrieved content to a local file
 * 
 * @author Brian Pipa - http://pipasoft.com
 * @version 1.0
 */
public class Wget {

    static final String FS = File.separator;

    /** This method does the actual GET
     * 
     * @param theUrl The URL to retrieve
     * @param filename the local file to save to
     * @exception IOException 
     */
    public static void get(URL gotoUrl, File file) throws IOException
    {
        try 
        {
            BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(file));
            BufferedInputStream bis = new BufferedInputStream(gotoUrl.openStream());
            
            int c; 
            
            do
            {
                c = bis.read();
                        
                if (c == -1)
                    break;
                
                fos.write(c);
            } while (c != -1);
                    
            fos.close();
        }
        catch (IOException ioe) {
            throw ioe;
        }
    }

    //creates a local file
    /** Writes a String to a local file
     * 
     * @param outfile the file to write to
     * @param content the contents of the file
     * @exception IOException 
     */
    public static void createAFile(File outfile, String content) throws IOException {
        FileOutputStream fileoutputstream = new FileOutputStream(outfile);
        DataOutputStream dataoutputstream = new DataOutputStream(fileoutputstream);
        dataoutputstream.writeBytes(content);
        dataoutputstream.flush();
        dataoutputstream.close();
    }

    /** The main method.
     * 
     * @param args 
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("\nUsage: java wget URL localfilename");
            System.out.println("Example: java wget http://google.com google.html");
            System.exit(1);
        }
        try {
            Wget httpGetter = new Wget();
            httpGetter.get(new URL(args[0]), new File(args[1]));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
