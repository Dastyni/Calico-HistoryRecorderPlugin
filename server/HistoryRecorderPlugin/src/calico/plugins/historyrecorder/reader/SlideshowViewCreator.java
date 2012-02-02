/* SingleUserSlineshowViewCreator.java
 * 
 * When passed a list of CalicoCanvasStates this class will 
 * create an HTML page wich contains a slideshow of images
 * for each event passed in the list.
 * 
 *  Intended for use with single user design session.
 */

package calico.plugins.historyrecorder.reader;

import java.awt.print.Printable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.List;

public class SlideshowViewCreator extends BaseViewCreator{
	private int imgSize = 200;
	
	public SlideshowViewCreator(){
		createPage(null);
	}
	
	
	//imageArray[ImageNum++] = new imageItem(image_dir + "NickM_image_18.png");

	public void createPage(List<CalicoCanvasState> states){
		System.out.println("In createPage");
		
		  try{
			  out = new BufferedWriter(new FileWriter("SlideshowViewer.html"));
			  out.write("<html><head><title>Calico History View "+vers+" </title>\n");
			  out.write("<Center><h1>Calico History Viewer</h1></center><br><br>\n");
			  FileInputStream fstream = new FileInputStream("../../SlideshowVierwer.tmpl");
			  
			  DataInputStream in = new DataInputStream(fstream);
			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
			  
			  String strLine;
			  
			  while ((strLine = br.readLine()) != null)   {
				  if(strLine.compareTo("[ARRAY_FILE_INSERT]") == 0){
					  for (CalicoCanvasState calicoCanvasState : states) {
						  out.write("imageArray[ImageNum++] = new imageItem(image_dir + \""+calicoCanvasState.imgName+"\");");
					  }
				  } else {
					  out.write(strLine+"\n");
				  }
			  }
			  in.close();
			  out.close();
			    }catch (Exception e){
			    	System.err.println("Error: " + e.getMessage());
			  }
	}
}
