package calico.plugins.historyrecorder.reader;

import java.util.ArrayList;

public class StateStore {
	private int stateCount = 0;
	private ArrayList<CalicoCanvasState> states;
	
	public StateStore(){
		states = new ArrayList<CalicoCanvasState>();
	}
	
	public void add(CalicoCanvasState cs){
		cs.posInTimeline = stateCount++;
		states.add(cs);
	}
	
	public ArrayList<CalicoCanvasState> getList(){
		return states;
	}
}
