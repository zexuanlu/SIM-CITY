package simcity.astar;
import java.util.*;
import java.util.concurrent.*;

public class AStarTraversal extends GraphTraversal
{
    private Semaphore[][] grid;

    public AStarTraversal(Semaphore[][] grid){
	super();
	this.grid = grid; 
	
	//grid = new Object[1000][2000];
	nodes = new PriorityQueue<Node>(6, new Comparator<Node>()
	{
	    public int compare(Node a, Node b)
	    {
		double distanceA = ((AStarNode)a).getApproxTotalDist();
		double distanceB = ((AStarNode)b).getApproxTotalDist();
		//System.out.println("Comparing Nodes" +distanceA+ "  "+distanceB);
		if (distanceA>distanceB)
		    return 1;
		else if (distanceA<distanceB)
		    return -1;
		else return 0;     }
	}
	);
    }
    public AStarNode createStartNode(Object state){
	Position p = (Position) state;
	AStarNode n = new AStarNode(p);
	n.setDistTravelled(0);
	n.setApproxTotalDist(p.distance((Position)getEndingState()));
	List<Position> path = new ArrayList<Position>();
	path.add(p);
	n.setPath(path);
	//System.out.print("createStartNode"); n.printNode();
	return n;
    }
    
    
    public List<Node> expandFunc(Node n) {
	AStarNode node = (AStarNode) n;
	//loop computes the positions you can get to from node
	List<Node> expandedNodes = new ArrayList<Node>();
	List<Position> path = node.getPath();
	Position pos = node.getPosition();
	int x = pos.getX();
	int y = pos.getY();
	//this next pair of loops will create all the possible moves
	//from pos.
	//in the square they can move however the hell they want 
	if ((y>=9 && y<=13) && (x>=17 && x<=21)){
		for (int i=-1;i<2;i++){
			for (int j = -1; j<2;j++){
				if ((j==0)|| (i==0 && j!=0)){
					//create the potential next position
					int nextX=x+i;
					int nextY=y+j;
					if ((nextX+1>grid.length || nextY+1>grid[0].length) ||
					      (nextX<0 || nextY<0)) continue;
					Position next = new Position(nextX,nextY);
					if (inPath(next,path) || !next.open(grid) ) continue;
					AStarNode nodeTemp = new AStarNode(next);

					nodeTemp.setDistTravelled(
			                        node.getDistTravelled()+pos.distance(next));
					//update approximate total distance to destination
					//note that we are computing the straight-line
					//heuristic on the fly right here from next to endingState
					nodeTemp.setApproxTotalDist(
						nodeTemp.getDistTravelled() + next.distance((Position)endingState));	
					//update internal path
					nodeTemp.updatePath(path);
					expandedNodes.add(nodeTemp);//could have just added
								    //them directly to nodelist 
				}
			}
		}
		
	}
	if (y==9){ //left and down
		for(int i = -1; i < 1; i++) {//increment for x direction
		    for (int j = 0; j <= 1; j++) {//increment for y direction
			    if (true){
					//create the potential next position
					int nextX=x+i;
					int nextY=y+j;
					if ((nextX+1>grid.length || nextY+1>grid[0].length) ||
					      (nextX<0 || nextY<0)) continue;
					Position next = new Position(nextX,nextY);
					if (inPath(next,path) || (!next.open(grid)) ) continue;  

					AStarNode nodeTemp = new AStarNode(next);

					nodeTemp.setDistTravelled(
			                        node.getDistTravelled()+pos.distance(next));
					//update approximate total distance to destination
					//note that we are computing the straight-line
					//heuristic on the fly right here from next to endingState
					nodeTemp.setApproxTotalDist(
					nodeTemp.getDistTravelled() + next.distance((Position)endingState));	
					//update internal path
					nodeTemp.updatePath(path);
					expandedNodes.add(nodeTemp);
								    //them directly to nodelist 
				}
			}
		}	
	}
	
	if (y==10){
		for(int i = -1; i < 1; i++) {//increment for x direction
		    for (int j = -1; j <= 2; j++) {//increment for y direction
			    if (true){
					//create the potential next position
					int nextX=x+i;
					int nextY=y+j;
					if ((nextX+1>grid.length || nextY+1>grid[0].length) ||
					      (nextX<0 || nextY<0)) continue;
					Position next = new Position(nextX,nextY);
					if (inPath(next,path) || (!next.open(grid)) ) continue;  

					AStarNode nodeTemp = new AStarNode(next);

					nodeTemp.setDistTravelled(
			                        node.getDistTravelled()+pos.distance(next));
					//update approximate total distance to destination
					//note that we are computing the straight-line
					//heuristic on the fly right here from next to endingState
					nodeTemp.setApproxTotalDist(
					nodeTemp.getDistTravelled() + next.distance((Position)endingState));	
					//update internal path
					nodeTemp.updatePath(path);
					expandedNodes.add(nodeTemp);
								    //them directly to nodelist 
				}
			}
		}	
	}
	
	
	else if (y==11){ //check down or up only
			int i=0; 
			for (int j = -1; j<=2;j++){
				if (j!=0){
					//create the potential next position
					int nextX=x+i;
					int nextY=y+j;
					if ((nextX+1>grid.length || nextY+1>grid[0].length) ||
					      (nextX<0 || nextY<0)) continue;
					Position next = new Position(nextX,nextY);
					if (inPath(next,path) || !next.open(grid) ) continue;
					AStarNode nodeTemp = new AStarNode(next);

					nodeTemp.setDistTravelled(
			                        node.getDistTravelled()+pos.distance(next));
					//update approximate total distance to destination
					//note that we are computing the straight-line
					//heuristic on the fly right here from next to endingState
					nodeTemp.setApproxTotalDist(
						nodeTemp.getDistTravelled() + next.distance((Position)endingState));	
					//update internal path
					nodeTemp.updatePath(path);
					expandedNodes.add(nodeTemp);//could have just added
								    //them directly to nodelist 
				}
			}
		}
	
	
	else if (y==12){//only check right or up & down
	for (int i=0;i<2;i++){
		for (int j = -1; j<2;j++){
			if ((j==0)|| (i==0 && j!=0)){
				//create the potential next position
				int nextX=x+i;
				int nextY=y+j;
				if ((nextX+1>grid.length || nextY+1>grid[0].length) ||
				      (nextX<0 || nextY<0)) continue;
				Position next = new Position(nextX,nextY);
				if (inPath(next,path) || !next.open(grid) ) continue;
				AStarNode nodeTemp = new AStarNode(next);

				nodeTemp.setDistTravelled(
		                        node.getDistTravelled()+pos.distance(next));
				//update approximate total distance to destination
				//note that we are computing the straight-line
				//heuristic on the fly right here from next to endingState
				nodeTemp.setApproxTotalDist(
					nodeTemp.getDistTravelled() + next.distance((Position)endingState));	
				//update internal path
				nodeTemp.updatePath(path);
				expandedNodes.add(nodeTemp);//could have just added
							    //them directly to nodelist 
			}
		}}}
	
	
	else if (y==13){//only check right or up
	for (int i=0;i<2;i++){
		for (int j = -1; j<1;j++){
			if ((j==0)|| (i==0 && j!=0)){
				//create the potential next position
				int nextX=x+i;
				int nextY=y+j;
				if ((nextX+1>grid.length || nextY+1>grid[0].length) ||
				      (nextX<0 || nextY<0)) continue;
				Position next = new Position(nextX,nextY);
				if (inPath(next,path) || !next.open(grid) ) continue;
				AStarNode nodeTemp = new AStarNode(next);

				nodeTemp.setDistTravelled(
		                        node.getDistTravelled()+pos.distance(next));
				//update approximate total distance to destination
				//note that we are computing the straight-line
				//heuristic on the fly right here from next to endingState
				nodeTemp.setApproxTotalDist(
					nodeTemp.getDistTravelled() + next.distance((Position)endingState));	
				//update internal path
				nodeTemp.updatePath(path);
				expandedNodes.add(nodeTemp);//could have just added
							    //them directly to nodelist 
			}
		}}}
	
	
	
	else if (x==17){ //only check the right and down
		for (int i=0;i<2;i++){
			for (int j = 0; j<2;j++){
				if ((j==0)|| (i==0 && j!=0)){
					//create the potential next position
					int nextX=x+i;
					int nextY=y+j;
					if ((nextX+1>grid.length || nextY+1>grid[0].length) ||
					      (nextX<0 || nextY<0)) continue;
					Position next = new Position(nextX,nextY);
					if (inPath(next,path) || !next.open(grid) ) continue;
					AStarNode nodeTemp = new AStarNode(next);

					nodeTemp.setDistTravelled(
			                        node.getDistTravelled()+pos.distance(next));
					//update approximate total distance to destination
					//note that we are computing the straight-line
					//heuristic on the fly right here from next to endingState
					nodeTemp.setApproxTotalDist(
						nodeTemp.getDistTravelled() + next.distance((Position)endingState));	
					//update internal path
					nodeTemp.updatePath(path);
					expandedNodes.add(nodeTemp);//could have just added
								    //them directly to nodelist 
				}
			}
		}
	}
	
	else if (x==18){ //only check the right and down
		for (int i=-1;i<2;i++){
			for (int j = 0; j<2;j++){
				if ((j==0)|| (i==0 && j!=0)){
					//create the potential next position
					int nextX=x+i;
					int nextY=y+j;
					if ((nextX+1>grid.length || nextY+1>grid[0].length) ||
					      (nextX<0 || nextY<0)) continue;
					Position next = new Position(nextX,nextY);
					if (inPath(next,path) || !next.open(grid) ) continue;
					AStarNode nodeTemp = new AStarNode(next);

					nodeTemp.setDistTravelled(
			                        node.getDistTravelled()+pos.distance(next));
					//update approximate total distance to destination
					//note that we are computing the straight-line
					//heuristic on the fly right here from next to endingState
					nodeTemp.setApproxTotalDist(
						nodeTemp.getDistTravelled() + next.distance((Position)endingState));	
					//update internal path
					nodeTemp.updatePath(path);
					expandedNodes.add(nodeTemp);//could have just added
								    //them directly to nodelist 
				}
			}
		}
	}
	
	
	
	else if (x==19){ //only check left and right
		int j = 0; 
		for (int i=-1;i<2;i++){
					//create the potential next position
					int nextX=x+i;
					int nextY=y+j;
					if ((nextX+1>grid.length || nextY+1>grid[0].length) ||
					      (nextX<0 || nextY<0)) continue;
					Position next = new Position(nextX,nextY);
					if (inPath(next,path) || !next.open(grid) ) continue;
					AStarNode nodeTemp = new AStarNode(next);

					nodeTemp.setDistTravelled(
			                        node.getDistTravelled()+pos.distance(next));
					//update approximate total distance to destination
					//note that we are computing the straight-line
					//heuristic on the fly right here from next to endingState
					nodeTemp.setApproxTotalDist(
						nodeTemp.getDistTravelled() + next.distance((Position)endingState));	
					//update internal path
					nodeTemp.updatePath(path);
					expandedNodes.add(nodeTemp);//could have just added
								    //them directly to nodelist 
				}
	}
	
	else if (x == 20 ){ //only check up and left	
		for (int i=-1;i<2;i++){
			for (int j = -1; j<1;j++){
				if ((j==0)|| (i==0 && j!=0)){
					//create the potential next position
					int nextX=x+i;
					int nextY=y+j;
					if ((nextX+1>grid.length || nextY+1>grid[0].length) ||
					      (nextX<0 || nextY<0)) continue;
					Position next = new Position(nextX,nextY);
					if (inPath(next,path) || !next.open(grid) ) continue;
					AStarNode nodeTemp = new AStarNode(next);

					nodeTemp.setDistTravelled(
			                        node.getDistTravelled()+pos.distance(next));
					//update approximate total distance to destination
					//note that we are computing the straight-line
					//heuristic on the fly right here from next to endingState
					nodeTemp.setApproxTotalDist(
						nodeTemp.getDistTravelled() + next.distance((Position)endingState));	
					//update internal path
					nodeTemp.updatePath(path);
					expandedNodes.add(nodeTemp);//could have just added
								    //them directly to nodelist 
				}
			}
		}
	}
	
	else if (x == 21 ){ //only check up and left	
		for (int i=-1;i<1;i++){
			for (int j = -1; j<1;j++){
				if ((j==0)|| (i==0 && j!=0)){
					//create the potential next position
					int nextX=x+i;
					int nextY=y+j;
					if ((nextX+1>grid.length || nextY+1>grid[0].length) ||
					      (nextX<0 || nextY<0)) continue;
					Position next = new Position(nextX,nextY);
					if (inPath(next,path) || !next.open(grid) ) continue;
					AStarNode nodeTemp = new AStarNode(next);

					nodeTemp.setDistTravelled(
			                        node.getDistTravelled()+pos.distance(next));
					//update approximate total distance to destination
					//note that we are computing the straight-line
					//heuristic on the fly right here from next to endingState
					nodeTemp.setApproxTotalDist(
						nodeTemp.getDistTravelled() + next.distance((Position)endingState));	
					//update internal path
					nodeTemp.updatePath(path);
					expandedNodes.add(nodeTemp);//could have just added
								    //them directly to nodelist 
				}
			}
		}
	}
	
		
	return expandedNodes;
    }//end expandFunc
    
    
    
    private boolean inPath (Position pos, List<Position> path){
	for (Position n:path) {if (pos.equals(n)) return true;};
	return false;
    }
    
    
    public void printCurrentList() {
	PriorityQueue<Node> pq = new PriorityQueue<Node>(nodes);
	AStarNode p;
	System.out.print("\n[");
	while ((p = (AStarNode)pq.poll()) != null) {
	    System.out.print("\n");
	    p.printNode();
	}
	System.out.println("]");
    }
    public void queueFn(Queue<Node> old, List<Node> newNodes){
	for (Node m:newNodes) {
	    old.offer((AStarNode)m);
	}
    }
    public Semaphore[][] getGrid(){return grid;}
}