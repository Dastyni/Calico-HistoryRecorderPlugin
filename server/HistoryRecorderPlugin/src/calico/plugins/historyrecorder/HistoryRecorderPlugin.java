package calico.plugins.historyrecorder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import calico.CalicoServer;
import calico.clients.Client;
import calico.clients.ClientManager;
import calico.events.CalicoEventHandler;
import calico.events.CalicoEventListener;
import calico.networking.netstuff.ByteUtils;
import calico.networking.netstuff.CalicoPacket;
import calico.networking.netstuff.NetworkCommand;
import calico.plugins.AbstractCalicoPlugin;
import calico.plugins.historyrecorder.reader.CalicoHistoryReader;
import calico.plugins.historyrecorder.reader.CanvasHistoryEventProcessor;

public class HistoryRecorderPlugin extends AbstractCalicoPlugin
	implements CalicoEventListener
{

	private FileOutputStream outputStream;
	public static int count = 0;
	
	public HistoryRecorderPlugin()
	{
		super();
		PluginInfo.name = "HistoryRecorder";
	}

	public void onPluginStart()
	{
		//TODO testing.
		//System.out.println("****************** HERE *****************");
		//if (new SlideshowViewCreator() != null ) return;

		for (int i = 0; i < CalicoServer.args.length; i++)
		{
			if (CalicoServer.args[i].compareTo("-processHistoryFiles") == 0)
			{
				System.out.println("Processing history files...");
				CanvasHistoryEventProcessor processor = getHistoryProcessor();
				String curDir = System.getProperty("user.dir");
				File dir = new File(curDir);

				//Grabs all of the files in the user directory
				File[] files = dir.listFiles(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return (name.endsWith(".chist"));
					}
				});
				
				System.out.println("Found " + files.length + " files");
				
				//Process each of the files.
				// This will be changed to single file as found at beginning of app.
				for (File f : files) {
					System.out.println("Starting file "+f.getName().toString());
					try {
						CalicoHistoryReader.processHistoryEventsFromDisk(f.getAbsolutePath(), processor);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				System.exit(0);
			}
		}
		
		CalicoEventHandler.getInstance().addGlobalListener(this);
		openOutputStream();
	}



	public void onPluginEnd()
	{
		closeOutputStream();
	}
	

	@Override
	public Class<?> getNetworkCommandsClass()
	{
		// TODO Auto-generated method stub
		return HistoryRecorderNetworkCommands.class;
	}
	
	private static CanvasHistoryEventProcessor getHistoryProcessor()
	{
		CanvasHistoryEventProcessor processor = new CanvasHistoryEventProcessor();		
		return processor;
	}
	
//	private CalicoPacket getCalicoCanvasState()
//	{
//		CCanvas canvas = CCanvasController.canvasdb.get(CCanvasController.getCurrentUUID());
//
//		
//		//time
//		long time = System.currentTimeMillis();
//		
//		//canvas information
//		CalicoPacket infoPacket = canvas.getInfoPacket();
//		
//		//canvas image
//    	BufferedImage bIMG = new BufferedImage(CalicoDataStore.ScreenWidth, CalicoDataStore.ScreenHeight, BufferedImage.TYPE_INT_ARGB);
//    	BufferedImage canvasImage = (BufferedImage)CCanvasController.canvasdb.get(CCanvasController.getCurrentUUID()).getLayer().toImage(bIMG, Color.white);
//    	
//    	//users on canvas
//		ArrayList<String> users = new ArrayList<String>();
//		users.add(CalicoDataStore.Username);
//		int[] clients = canvas.getClients();
//		for(int i=0;i<clients.length;i++) {
//			if(CalicoDataStore.clientInfo.containsKey(clients[i]) && CalicoDataStore.clientInfo.get(clients[i]).compareTo(CalicoDataStore.Username) != 0) {
//				users.add(CalicoDataStore.clientInfo.get(clients[i]));
//			}
//		}
//		
//		//canvas backup state
//		CalicoPacket[] canvasState = canvas.getUpdatePackets();
//		
//		//size of image
//		int canvasImageSize = CalicoPacket.getSizeOfImage(canvasImage);
//		
//		//size of users on canvas strings
//		int usersStringSize = 0;
//		for (String s : users)
//			usersStringSize += CalicoPacket.getSizeOfString(s);
//		
//		//size of backup image
//		int canvasStateSize = 0;
//		for (int i = 0; i < canvasState.length; i++) 
//			canvasStateSize += canvasState[i].getBufferSize();
//		
//		int totalSize = ByteUtils.SIZE_OF_INT
//						+ ByteUtils.SIZE_OF_INT
//						+ ByteUtils.SIZE_OF_LONG		//time
//						+ ByteUtils.SIZE_OF_INT + infoPacket.getBufferSize()	//info packet
//						+ canvasImageSize				//canvas image
//						+ ByteUtils.SIZE_OF_INT + usersStringSize				//string of users
//						+ ByteUtils.SIZE_OF_INT + canvasStateSize;				//canvas backup state
//		
//		CalicoPacket packet = new CalicoPacket(totalSize);
//		packet.putInt(totalSize - ByteUtils.SIZE_OF_INT);
//		packet.putInt(HistoryRecorderNetworkCommands.HISTORY_BACKUP_STATE);
//		packet.putLong(time);
//		packet.putInt(infoPacket.getBufferSize());
//		packet.putByte(infoPacket.getBuffer());
//		packet.putImage(canvasImage);
//		packet.putInt(users.size());
//		for (String s : users)
//			packet.putString(s);
//		packet.putInt(canvasStateSize);
//		for (int i = 0; i < canvasState.length; i++)
//			packet.putBytes(canvasState[i].getBuffer());
//		
//		return packet;
//	}
	
	private void openOutputStream() {
		try {
			if (!(new File("history_logs/")).exists())
				(new File("history_logs/")).mkdir();
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
			Date date = new Date();
			String d = dateFormat.format(date);
			outputStream = new FileOutputStream("history_logs/calicoHistory_" + d + ".chist");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void recordEvent(CalicoPacket p, Client c)
	{
		//time
		long time = System.currentTimeMillis();
		
		int totalSize = ByteUtils.SIZE_OF_INT
						+ ByteUtils.SIZE_OF_INT
						+ ByteUtils.SIZE_OF_LONG
						+ CalicoPacket.getSizeOfString(c.getUsername())
						+ ByteUtils.SIZE_OF_LONG
						+ ByteUtils.SIZE_OF_INT
						+ p.getBufferSize();
		
		final CalicoPacket packet = new CalicoPacket(totalSize);
		packet.putInt(totalSize - ByteUtils.SIZE_OF_INT);
		packet.putInt(HistoryRecorderNetworkCommands.HISTORY_BACKUP_EVENT);
		packet.putLong(time);
		packet.putString(c.getUsername());
		packet.putLong(ClientManager.getClientThread(c.getClientID()).getCurrentCanvasUUID());
		packet.putInt(p.getBufferSize());
		packet.putBytes(p.getBuffer());

		if (outputStream != null)
		{
			Thread recordStateThread = new Thread(null, new Runnable() {
				@Override
				public void run() {
					try {
						outputStream.write(packet.getBuffer());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			},"Record Calico State");
			recordStateThread.start();
		}
		

	}
	
	private void closeOutputStream()
	{
		if (outputStream != null)
			try {
				outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	@Override
	public void handleCalicoEvent(int event, CalicoPacket packet, Client client) {
		if (event == NetworkCommand.HEARTBEAT
				|| event == NetworkCommand.GROUP_MOVE
				|| packet == null
				|| client == null)
			return;
		
		recordEvent(packet, client);
//		System.out.println("HistoryRecorderPlugin: Recorded event - " + event);
		
//		switch (event)
//		{
//			case NetworkCommand.VIEWING_SINGLE_CANVAS:
//				recordState();
//				break;
//		}
		
	}
	
	
}
