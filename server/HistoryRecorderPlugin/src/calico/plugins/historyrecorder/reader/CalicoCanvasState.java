package calico.plugins.historyrecorder.reader;

import java.awt.image.BufferedImage;


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
	long canvasUUID;
	int x;
	int y;
	String gridCoordText;
	String[] users;
	String user;
	BufferedImage img;
	byte[] state;
	String imgName;
	int posInTimeline;
	
	public CalicoCanvasState(long t, long u, String coord, String us, String iName)
	{
		this.time = t;
		this.canvasUUID = u;
		this.gridCoordText = coord;
		this.user = us;
		this.imgName = iName;
		//this.img = i;
		//this.state = s;
	}

}
