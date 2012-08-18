package calico.plugins.historyrecorder.reader;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import calico.ProcessQueue;
import calico.controllers.CCanvasController;
import calico.networking.netstuff.ByteUtils;
import calico.networking.netstuff.CalicoPacket;
import calico.networking.netstuff.NetworkCommand;


public class CalicoHistoryReader {
	
	public static int counter = 0;
	public static PrintWriter out;
	//public static StateStore stateStore;
	
	private static Database db;
	
	private static Map<String, String> clientLocations;
	
	public static void processHistoryEventsFromDisk(String fileLocation, CanvasHistoryEventProcessor processor) throws IOException
	{

		//stateStore = new StateStore();
		File f = new File(fileLocation);
		FileInputStream fis = new FileInputStream(f);
		db = new Database();
		db.connect(); 
		
		clientLocations = new HashMap<String, String>();

		while (fis.available() > 0)
		{
			byte[] sizeArray = new byte[ByteUtils.SIZE_OF_INT];
			fis.read(sizeArray);
			int size = ByteUtils.readInt(sizeArray, 0);
			byte[] nextInput = new byte[size];
			fis.read(nextInput);
			handleNextEvent(new CalicoPacket(nextInput), processor, f.getName());
		}
		
		//Create view pages here.
		//new MultiUserViewCreator().createPage( stateStore.getList() );
		//new SlideshowViewCreator().createPage( stateStore.getList() );
		db.disconnect();
		
	}
	
	public static void handleNextEvent(CalicoPacket historyPacket, CanvasHistoryEventProcessor processor, String fileName)
	{
		historyPacket.rewind();
		int histComm = historyPacket.getInt();
		long time = historyPacket.getLong();
		String username = historyPacket.getString();
		long cuid = historyPacket.getLong();
		String coordString = (cuid == 0) ? "NA" : CCanvasController.canvases.get(cuid).getCoordText();
		int sizeOfPacket = historyPacket.getInt();
		byte[] packetAsBytes = historyPacket.getByteArray(sizeOfPacket);
		CalicoPacket eventPacket = new CalicoPacket (packetAsBytes);

		eventPacket.rewind();
		int comm = eventPacket.getInt();
		
		//Client leaving
		if(comm == NetworkCommand.PRESENCE_LEAVE_CANVAS && clientLocations.containsKey(username)){
			clientLocations.remove(username);
		}
		//Client joining
		if(comm == NetworkCommand.PRESENCE_VIEW_CANVAS){
			//if(coordString != "NA"){
				clientLocations.put(username, coordString);
			//}
		}
		
		String imgNameString = "";
		try
		{
			ProcessQueue.receive(comm, null, eventPacket);
			imgNameString = processor.processCanvasState(eventPacket, time, username, cuid);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		//Store it in the DB
		if(!imgNameString.isEmpty()){
			CalicoCanvasState newState =  new CalicoCanvasState(time, comm, coordString, username, imgNameString+".png", fileName, clientLocations );
			//stateStore.add(newState);
			db.add(newState);
		}
	}

//	public static CalicoCanvasState getCanvasState(CalicoPacket packet)
//	{
////		CalicoPacket packet = new CalicoPacket(totalSize);
////		packet.putInt(HistoryRecorderNetworkCommands.HISTORY_BACKUP_STATE);
////		packet.putLong(time);
////		packet.putInt(infoPacket.getBufferSize());
////		packet.putByte(infoPacket.getBuffer());
////		packet.putImage(canvasImage);
////		packet.putInt(users.size());
////		for (String s : users)
////			packet.putString(s);
////		packet.putInt(canvasStateSize);
////		for (int i = 0; i < canvasState.length; i++)
////			packet.putBytes(canvasState[i].getBuffer());
//		packet.rewind();
//		
//		int command = packet.getInt();
//		if (command != 2600)
//			return null;
//		
//		long time = packet.getLong();
//		int infoPacketLength = packet.getInt();
//		CalicoPacket infoPacket = new CalicoPacket(packet.getByteArray(infoPacketLength));
//		BufferedImage canvasImage = packet.getBufferedImage();
//		String[] users = new String[packet.getInt()];
//		for (int i = 0 ; i < users.length; i++)
//			users[i] = packet.getString();
//		byte[] backupState = packet.getByteArray(packet.getInt());
//		
//		infoPacket.rewind();
////		return CalicoPacket.getPacket(
////				NetworkCommand.CANVAS_INFO,
////				this.uuid,
////				this.getGridCoordTxt(),
////				this.getGridCol(),
////				this.getGridRow()
////			);
//		infoPacket.getInt();
//		long uuid = infoPacket.getLong();
//		String coord = infoPacket.getString();
//		int x = infoPacket.getInt();
//		int y = infoPacket.getInt();
//		
//		CalicoCanvasState ret = new CalicoCanvasState(time, uuid, x, y, coord, users, canvasImage, backupState);
//		
//		return ret;
//	}
	
	public static void save_to_disk(String name, BufferedImage img) throws IOException {
		
		byte[] image = CalicoPacket.getImageByteArray(img);
		
		String filePath = name + ".png";
		
	    File imageFile = new File(filePath);
	    if (!imageFile.exists())
	    	imageFile.createNewFile();
	    
		OutputStream os = new FileOutputStream(imageFile);
	    
	    os.write(image);
	    
	    os.close();
	    
	}
	
	public static void text_output_open(String filename)
	{
		FileWriter outFile;
		try {
			outFile = new FileWriter(filename);
			out = new PrintWriter(outFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void text_output_write(String text)
	{
		// Write text to file
		out.println(text);
	}
	
	public static void text_output_close()
	{
		out.close();
	}
	
}
