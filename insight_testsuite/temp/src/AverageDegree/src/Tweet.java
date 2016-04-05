import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/*
 * This class is used to encaptulate and represent  tweet objects comprised of created_at time and hashtags,
 * maintained as nodes of a graph.
 */
public class Tweet implements Comparable<Tweet>{

	
	private Date created_at;
	private HashMap<String,List<Edge>> edges=new HashMap<String,List<Edge>>();
	
	
	public Tweet(List<String> nodes, String created_at,String formatPattern) throws ParseException{
		//this.nodes=nodes;
		DateFormat format = new SimpleDateFormat(formatPattern==null?"EEE MMM dd HH:mm:ss Z yyyy":formatPattern,Locale.ENGLISH);
		this.created_at=format.parse(created_at);
		generateEdges(nodes);
	}
	
	private void generateEdges(List<String> nodes){
		for(String h_node: nodes){
			for(String e_node:nodes){
				if(h_node.equals(e_node))
					continue;
				if(!edges.containsKey(h_node)){
					List<Edge> edges_list=new LinkedList<Edge>();
					edges_list.add(new Edge(h_node,e_node));
					edges.put(h_node,edges_list);
				}
				else{
					edges.get(h_node).add(new Edge(h_node,e_node));					
				}
			}
		}
	}
	
	
	public List<Edge> getEdges(String node) {
		return edges.get(node);
	}

	public Set<String> getNodes() {
		return edges.keySet();
	}

	public Date getCreated_at() {
		return created_at;
	}

	@Override
	public int compareTo(Tweet t) {
		return this.getCreated_at().compareTo(t.getCreated_at());
	}
	
	
}
