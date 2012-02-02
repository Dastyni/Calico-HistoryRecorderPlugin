package calico.plugins.historyrecorder.reader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* Adapted/hackafied for single user for new version of recorder 
 * 1/25/2012
 * 
 * - Dastyni
 * */

public class MultiUserViewCreator extends BaseViewCreator {
	private int imgSize = 200;
	
	public void createPage(List<CalicoCanvasState> states){
		// parse the List of states into usable rows
		for(CalicoCanvasState cs : states){
			timestamps.add(cs.time);
			
			// Separates states into rows and collects the row order
			if (!rows.containsKey(cs.gridCoordText)){
				rows.put(cs.gridCoordText , new ArrayList<CalicoCanvasState>());
				rowOrder.add(cs.gridCoordText);
			}
			rows.get(cs.gridCoordText).add(cs);
		}

		try {
			out = new BufferedWriter(new FileWriter("viewer.html"));
			out.write("<html><head><title>Calico History View "+vers+" </title></head>\n");
			 out.write("<link href='http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/base/jquery-ui.css' rel='stylesheet' type='text/css'/>");
			 out.write("<script src='http://ajax.googleapis.com/ajax/libs/jquery/1.5/jquery.min.js'></script>");
			 out.write("<script src='http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/jquery-ui.min.js'></script>");

			out.write("<script>function update(url,index,isSuper) {document['PhotoBig'].src=url;}</script>");
			
			out.write("<script>");
			out.write("$(document).ready(function() {");
			out.write("$('#slider').slider();");
			out.write("$('#slider' ).slider({ value:"+ imgSize +"});");
			out.write("$('#slider' ).slider({ max:"+ imgSize +"});");
			out.write("$('#slider' ).slider({ min:10});");
			out.write("$( '#slider' ).bind( 'slide', function(event, ui) {");
			out.write("$('.resizable').width(ui.value);");
			out.write("$('.resizable').height(ui.value);");
			out.write("});});</script>");
			
			out.write("<Center><h1>Calico History Viewer</h1></center>\n");
			out.write("<div id='slider' style='width:20%'><center>Zoom View</center></div>");
			out.write("<div class=ViewerContent style=\"inline-table; clear:both; overflow:hidden; padding-top:5px; \">");
			out.write("<div class=CellViews style=\"width:60%; height:850px; background-color:#9BB3CC; float:left; border-style:solid; border-width:1px;overflow:auto;\">");
			out.write("<table border=0>");
			out.write(drawTableTimestamps());
			
			// Visualize each row used.
			for(String key : rowOrder){
				out.write(addTableLine(rows.get(key)));
			}
			out.write("</table>");
			out.write("</div><div class=imgView style=\"float:left; width:39%; height:850px; border-style:solid; border-width:1px;background-color:white\">");
			out.write("<center><img src='' name='PhotoBig' width='650' height='650' ></center>");
			out.write("</div></div>");
			out.close();
		} catch (IOException e) {
		}
	}

	private String addTableLine(List<CalicoCanvasState> states){
		String output = "<tr><td><center>"+states.get(0).gridCoordText+"</center></td>";
		int cellNum = 0;
		
		for(CalicoCanvasState cs : states){
			while (cellNum++ < cs.posInTimeline){
				output += "<td></td>";
			}
			
			output += "<td><div class=scalablediv><div><center>";
//			String[] users = cs.users;
//			String allUsers = "";
//			for(int i=0; i<users.length; i++){
//				if (i>0)
//					allUsers += ", ";
//				allUsers += users[i];
//			}
			String allUsers = cs.user;
			output += allUsers+"</center></div>";
			output += "<a onMouseOver=\"update('"+ cs.imgName +"', 0, false); return false;\"><img src=\""+cs.imgName+"\" border=1 height="+imgSize+" width="+imgSize+" class=\"resizable\"> </a>";
			output += "</div></td>";
		}
		
		return output;
	}
	

	private String drawTableTimestamps(){
		String output = "<tr><td></td>\n";
		for(Long t : timestamps){
			Time ft = new Time(t);
			output += "<td><center>"+ft+"</center></td>";
		}
		output += "</tr>\n";
		return output;
	}
}
