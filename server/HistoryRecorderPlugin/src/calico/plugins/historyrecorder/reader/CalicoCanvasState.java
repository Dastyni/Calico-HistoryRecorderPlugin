package calico.plugins.historyrecorder.reader;

import java.awt.image.BufferedImage;
import java.util.Map;


/**
 * 
 * 
 * ArrayList<CalicoCanvasState> history;
 * 
 * 
 * @author calvmari
 *
 */

public class CalicoCanvasState {
	
	long time;
	int command;
	int x;
	int y;
	String gridCoordText;
	String[] users;
	String user;
	BufferedImage img;
	byte[] state;
	String imgName;
	int posInTimeline;
	String fileName;
	Map<String, String> clientLocations;
	
	public CalicoCanvasState(long t, Integer cmd, String coord, String us, String iName, String fileName, Map<String, String> clientLocations)
	{
		this.time = t;
		this.command = cmd;
		this.gridCoordText = coord;
		this.user = us;
		this.imgName = iName;
		this.fileName = fileName;
		this.clientLocations = clientLocations;
	}

}
