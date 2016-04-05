import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/*
 * The class aver_degree is the primary class computating the average degree of each node 
 * within updated 60s' window. 
 */
public class average_degree {
	
	/*
	 * This is the main entry for the average degrees computation 
	 */
	public static void main(String[] args) throws IOException, ParseException {
	
		
		FileInputStream fis=new FileInputStream(args[0]);
		InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
		BufferedReader br = new BufferedReader(isr);
		
		File output = new File(args[1]);
		if (!output.exists()) {
			output.createNewFile();
		}
		FileOutputStream fos=new FileOutputStream(output);
		BufferedWriter outputBuffer = new BufferedWriter(new OutputStreamWriter(fos));
	
		
		String jsonObject;
		
		average_degree ave_deg=new average_degree();
		
		
	 	while ((jsonObject = br.readLine()) != null) {
		    // Process and convert a new line  from tweet.txt	to JsonObject
	 		JsonObject obj = new JsonParser().parse(jsonObject).getAsJsonObject();
	 		
	 		if(obj.has("limit") )
	 			continue;
	 		
	 		//Receive created_at time from the JsonObject 
	 		String created_at=obj.get("created_at").getAsString(); 
	 		
	 		//Receive all hashtags from the JsonObject and put them into a linked list
	 		JsonArray entity=obj.getAsJsonObject("entities").getAsJsonArray("hashtags");	 	
	 		Iterator<JsonElement> tags_Iterator=entity.iterator();
	 		List<String> nodes=new LinkedList<String>();
	 		while(tags_Iterator.hasNext()){
	 			JsonObject tag=tags_Iterator.next().getAsJsonObject();
	 			String node =tag.getAsJsonPrimitive("text").getAsString();
	 			nodes.add(node);	 			
	 		}
	 		
	 		//Instantiate Tweet object with created_at time and all hashtags of tweet  
	 		Tweet tweet=new Tweet(nodes,created_at,null);
	 		
	 		//Process the new Tweet object and all expired old Tweets and then
	 		//compute the updated average degrees as the Tweet object arrives 	 	
	 		ave_deg.processNewTweet(tweet);
	 		
	 		outputBuffer.write(String.format("%.2f",ave_deg.ave_degree));
	 		outputBuffer.newLine();
	 			 
	    }
	 	
	 	outputBuffer.close();
	 	fos.close();	 	
	 	br.close();
	 	isr.close();
	 	fis.close();
	 	System.exit(0);
	}
	
	//currentTweets contains Tweets within current time window
	private PriorityQueue<Tweet> currentTweets;
	
	//nodeMap represents the graph, it's key is hashtags ,maintained as nodes of the graph,
	//value is a hashmap, which maps edges connected to this corresponding node  onto their number.   
	private HashMap<String,HashMap<Edge,Integer>> nodeMap;	
	
	private double ave_degree;
	private int totalNodes;
	private int totalEdges;
	
	//current_max_time represents upper border of current time window
	private Date current_max_time;
	
	public average_degree(){
		currentTweets=new PriorityQueue<Tweet>();
		nodeMap=new HashMap<String,HashMap<Edge,Integer>>();
		ave_degree=0;
		totalEdges=0;
		totalNodes=0;
		current_max_time=new Date(Long.MIN_VALUE);
	}
	
	public double processNewTweet(Tweet t){
		if(t.getCreated_at().compareTo(current_max_time)>0){
			current_max_time=t.getCreated_at();
			currentTweets.offer(t);
			addATweet(t);
			
			while(currentTweets.peek()!=null && isExpiredTweet(currentTweets.peek())){
				Tweet drop_t=currentTweets.poll();
				dropATweet(drop_t);
			}
			
		}
		else if(!isExpiredTweet(t)){
			currentTweets.offer(t);
			addATweet(t);
		}
		ave_degree=totalNodes!=0?(double)(Math.round(totalEdges*100/totalNodes)/100.00):0;
		return ave_degree;
	}
	
	/*
	 * This function is used to compute the updated average degrees after a new Tweet object arrived.
	 */
	public void addATweet(Tweet t){
		for(String n: t.getNodes()){
			HashMap<Edge,Integer> nodeM=null;
			if(!nodeMap.containsKey(n)){
				totalNodes++;
				nodeM=new HashMap<Edge,Integer>(); 
				nodeMap.put(n, nodeM);		
			}
			else{
				nodeM=nodeMap.get(n);
			}
			
			List<Edge> edge_list=t.getEdges(n);
			for(Edge e:edge_list){
				if(!nodeM.containsKey(e)){
					nodeM.put(e, new Integer(1));
					totalEdges++;
				}
				else{
					nodeM.put(e,new Integer(nodeM.get(e).intValue()+1));
				}
			}
		}
	}
	
	/*
	 * This function is used to compute the updated average degrees after a expired Tweet object dropped
	 */
	public void dropATweet(Tweet t){
		for(String n: t.getNodes()){		
			HashMap<Edge,Integer> nodeM=nodeMap.get(n);			
			List<Edge> edge_list=t.getEdges(n);
			
			for(Edge e:edge_list){
				if(nodeM.get(e).compareTo(1)==0) {
					nodeM.remove(e);
					totalEdges--;
				}else{
					nodeM.put(e,new Integer(nodeM.get(e).intValue()-1));
				}
			}
			if(nodeM.isEmpty()){
				totalNodes--;
				nodeMap.remove(n);
			}
				
		}
	
	}

	
	
	public  boolean isExpiredTweet(Tweet t){
		if(current_max_time.getTime()-t.getCreated_at().getTime()>=60000)
			return true;
		return false;
	}
			
	
}
