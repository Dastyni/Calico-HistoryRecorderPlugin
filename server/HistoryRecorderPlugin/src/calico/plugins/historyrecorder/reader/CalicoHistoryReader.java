package calico.plugins.historyrecorder.reader;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import calico.ProcessQueue;
import calico.controllers.CCanvasController;
import calico.networking.netstuff.ByteUtils;
import calico.networking.netstuff.CalicoPacket;
import calico.plugins.historyrecorder.*;


public class CalicoHistoryReader {
	
	public static int counter = 0;
	public static PrintWriter out;
	public static StateStore stateStore;
	
	private static Database db;
	
	public static void main(String[] args)
	{
		if (new SlideshowViewCreator() != null ) return;
		
		try
		{
			CanvasHistoryEventProcessor processor = new CanvasHistoryEventProcessor() {
				
//				@Override
//				public void processCanvasState() {
//
//
////						text_output_write(state.time + "," + state.canvasUUID);
//
//					
//				}

				@Override
				public String processCanvasState(CalicoPacket p, long time,
						String clientName, long cuid) {
					// For Dastyni: This is how you create an image
//					BufferedImage bi = new BufferedImage(1200, 900, BufferedImage.TYPE_INT_ARGB);
//					Graphics2D ig2 = bi.createGraphics();
//					CCanvasController.canvases.get(cuid).render(ig2);
					return "";
				}
			};
			
			if (out == null)
				text_output_open(System.getProperty(args[0]));
			
			File dir = new File(".");
		
			File[] files = dir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return (name.endsWith(".chist"));
				}
			});
			
			for (File f : files) {
				processHistoryEventsFromDisk(f.getAbsolutePath(), processor);
			}
			
			
			text_output_close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		

	}
	
	
	public static void processHistoryEventsFromDisk(String fileLocation, CanvasHistoryEventProcessor processor) throws IOException
	{

		stateStore = new StateStore();
		File f = new File(fileLocation);
		FileInputStream fis = new FileInputStream(f);
		db = new Database();
		db.connect(); 

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
			CalicoCanvasState newState =  new CalicoCanvasState(time, cuid, coordString, username, imgNameString+".png", fileName );
			stateStore.add(newState);
//			Database db = new Database();
//			db.connect(); 
			db.add(newState);
			//System.out.println("Added");
//			db.disconnect();
			
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
