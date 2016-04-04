/*
 * This class is used to encaptulate and represent edge comprised of two nodes.
 */
public class Edge {
	private String head_node;
	private String end_node;
	
	public Edge(String h,String e){
		head_node=h;
		end_node=e;
	
	}
	
	@Override
	public int hashCode() {
		return head_node.hashCode()+end_node.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(obj instanceof Edge)
			return (this.hashCode()==((Edge)obj).hashCode());
		return false;
	}
	
	
}
