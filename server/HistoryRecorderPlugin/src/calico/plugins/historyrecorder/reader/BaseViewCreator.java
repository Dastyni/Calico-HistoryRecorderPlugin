package calico.plugins.historyrecorder.reader;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseViewCreator {
	protected static String vers = "v0.06a";
	
	protected Map<String, List<CalicoCanvasState>> rows = new HashMap<String, List<CalicoCanvasState>>();
	protected List<String> rowOrder = new ArrayList<String>();
	protected List<Long> timestamps = new ArrayList<Long>();
	protected static BufferedWriter out;
}
