import java.util.ArrayList;



public class Models implements Model {
	
	public class State {
		
		private int id;
		private ArrayList<Integer> edges;
		
		public State(int id) {
			this.id = id;
			edges = new ArrayList<Integer>();
		}
		
		public int getID() {
			return id;
		}
		
		public void addEdges(int s2) {
			if(!edges.contains(s2)) {
				edges.add(s2);
			}
		}
		
		public boolean isContain(int s) {
			return edges.contains(s);
		}
		
		public int getEdge(int index) {
			return edges.get(index);
		}
		
		public ArrayList<Integer> getEdges() {
			return edges;
		}
		
		public int getEdgeSize() {
			return edges.size();
		}
		
	}
	
	private boolean isFinish;
	private int numS;
	private int[][] edges;
	private State[] allState;
	private StateSet all;
	/**
	 * constructor
	 */
	public Models() {
		isFinish = false;
		numS = 0;
		all = new StateSets();
	}
	
	/**
    Finalize the Kripke structure.
    The parser will call this after all arcs have been processed,
    in case any post-processing must be done on the data structure
    used to store the Kripke structure.
    This method will be called exactly once by the parser.
    */
  public boolean finish() {
	return true;  
  }

  /**
    Set the number of states in the model.
    This method is called once by the parser,
    before any arcs are added to the model,
    and before the call to finish().

    @param  n   Number of states in the Kripke structure.
    Once set, valid states are from 0 to n-1.
    */
  public void setNumStates(int n) {
	  numS = n;
	  edges = new int[n][n];
	  allState = new State[n];
	  for(int j =0; j<n; j++) {
		  all.addState(j);
		 allState[j] = new State(j);
	  }
	  
	  //initializing the matrix
	  for(int i=0; i<n; i++) {
		  for(int j=0; j<n; j++) {
			  edges[i][j] = 0;
		  }
	  }
  }

  /**
    Check if the given state is valid.
    Will be called after setNumStates().

    @param  s   State id to check
    @return     True, iff s is within range 0..n-1.
    */
  public boolean isValidState(int s) {
	  return (s >= 0 && s < numS-1);
  }

  /**
    Add an arc in the Kripke structure,
    from state s1 to state s2.
    Will not be called after finish() has been called.

    @param  s1    Source state id
    @param  s2    Destination state id
    */
  public void addArc(int s1, int s2) {
//	  	  edges[s1][s2] = 1;
	  	  allState[s1].addEdges(s2);
  }


  /**
    Create a new, empty, StateSet for this model.
    */
  public StateSet makeEmptySet() {
	  StateSet s = new StateSets();
	  return s;
  }

  /**
    Destroy a StateSet for this model.
    */
  public void deleteSet(StateSet sset) {
	  sset.destroy();
  }

  /**
    Add a state to a StateSet.
    The parser calls this for state labels.

    @param  s     State id to add
    @param  sset  State_set to add s into
    */
  public void addState(int s, StateSet sset) {
	  sset.addState(s);
  }

  /**
    Copy a set of states.

    @param  sset    Source set of states.
    @param  rset    Destination set of states;
    on return of this method, set rset
    should contain the same states as set sset.
    */
  public void copy(StateSet sset, StateSet rset) {
	  //check if rset is empty or not. If not, clear the elements
	  if(!rset.isEmpty()) {
		  rset.destroy();
	  }
	  
	  for(int i = 0; i<sset.size(); i++) {
		  rset.addState(sset.getEle(i));
	  }
  }

  //
  // Unary operations
  //

  /**
    Take the complement of a set of states.
    @param  sset  On input: a set of states Y.
    @param  rset  On output: complement of Y is stored here.
    Note that rset and sset may point to the
    same object!
    */
  public void NOT(StateSet sset, StateSet rset) {
	StateSet temp = new StateSets();

	  for(int i=0; i<numS; i++) {
		  if(!sset.isContain(i)) {
			  temp.addState(i);
		  }
	  }

	rset.destroy();
	for(int j=0; j<temp.size(); j++) {
		rset.addState(temp.getEle(j));
	}
  }

  /**
    Labeling for EX.
    @param  sset  On input: a set of states satisfying p.
    @param  rset  On output: the set of states satisfying EX p
    is stored here.
    Note that rset and sset may point to the 
    same object!
    */
  public void EX(StateSet sset, StateSet rset) {	  
	  for(int i=0; i<numS; i++) {
		  State s = allState[i];
		  for(int j=0; j<s.getEdgeSize(); j++) {
			  if(sset.isContain(s.getEdge(j)) && !rset.isContain(i)){
				  rset.addState(i);
			  }
		  }
	  }
	
  }

  /**
    Labeling for EF.
    @param  sset  On input: a set of states satisfying p.
    @param  rset  On output: the set of states satisfying EF p
    is stored here.
    Note that rset and sset may point to the 
    same object!
    */
  public void EF(StateSet sset, StateSet rset) {
	  StateSet temp = new StateSets();
	  StateSet temp2 = new StateSets();
	  copy(sset, temp);
	  //E tt U sset
	  EU(all, temp, temp2);

	  rset.destroy();
	  for(int i=0; i<temp2.size(); i++) {
		rset.addState(temp2.getEle(i));		
          }
	  
	  /*
	  while(!temp.isEmpty()) {
		  int origin = temp.pop();
		  for(int i=0; i<numS; i++) {
			  if(edges[i][origin]==1 && !rset.isContain(i)) {
				  rset.addState(i);
				  if(!temp.isContain(i)) {
					  temp.addState(i);
				  }
			  }
		  }
	  }*/
  }

  /**
    Labeling for EG.
    @param  sset  On input: a set of states satisfying p.
    @param  rset  On output: the set of states satisfying EG p
    is stored here.
    Note that rset and sset may point to the 
    same object!
    */
  public void EG(StateSet sset, StateSet rset) {
	  
	  StateSet temp = new StateSets();
	  copy(sset, temp);
	  
	  int tracksize = 0;
	  
	  do {
		  //update tracksize and temp2 with new temp.
		  tracksize = temp.size();
		  StateSet temp2 = new StateSets();
		  copy(temp, temp2);
		  
		  for(int i=0; i<tracksize; i++) {
			  int edgechecking = 0;
			  int curState = temp2.getEle(i);
			  
			  for(int j=0; j <allState[curState].getEdgeSize(); j++) {
				  if(temp.isContain(allState[curState].getEdge(j))) {
					  edgechecking = 1;
					  break;
				  }
			  }
			  
			  //here, if edgechecking is still 0, there is no successor satisfying the phi.
			  if(edgechecking==0){
				  temp.remove(i);
			  } else {
				  //reset edgechecking for next iteration
				  edgechecking = 0;
			  }
			  
		  }
		  
	  }while(tracksize!=temp.size());
	  rset.destroy();
	  for(int l=0; l<temp.size(); l++) {
		  rset.addState(temp.getEle(l));
	  }
	  
	/*  
	  StateSet temp = new StateSets();
	  copy(sset, temp);
	  
	  int trackSize = 0;
	  int check = 0;
	  
	  while(trackSize != temp.size()) {
		  trackSize = temp.size();
		  StateSet temp2 = new StateSets();
		  copy(temp, temp2);
		  for(int i=0; i<temp2.size(); i++) {
			  int k = temp2.getEle(i);
			  for(int j=0; j<numS; j++) {
				  if(edges[k][j]==1 && temp.isContain(j)) {
					  check = 1;
					  break;
				  }
			  }
			  //If here, no successor is satisfying p
			  if(check == 0) {
				  temp.remove(i);
			  }
			  check = 0;
		  }
	  }*/
  }

  /**
    Labeling for AX.
    @param  sset  On input: a set of states satisfying p.
    @param  rset  On output: the set of states satisfying AX p
    is stored here.
    Note that rset and sset may point to the 
    same object!
    */
  public void AX(StateSet sset, StateSet rset) {
	 StateSet temp = new StateSets();
	  
	  for(int i=0; i<numS; i++) {
		  State s = allState[i];
		  int edgechecking = 0;
		  for(int j=0; j<s.getEdgeSize(); j++) {
			  if(!sset.isContain(s.getEdge(j))){
				  edgechecking = 1;
				  break;
			  }
			  //Here, all successor of s contain phi. 
			  if(edgechecking == 0) {
				  temp.addState(i);
			  } else {
				  //reset edgechecking
				  edgechecking = 0;
			  }
		  }
	  }
	rset.destroy();
	for(int k =0; k<temp.size(); k++ ) {
		rset.addState(temp.getEle(k));
	}
	  
  }

  /**
    Labeling for AF.
    @param  sset  On input: a set of states satisfying p.
    @param  rset  On output: the set of states satisfying AF p
    is stored here.
    Note that rset and sset may point to the 
    same object!
    */
  public void AF(StateSet sset, StateSet rset){
	  //all states in sset are going to be in rset.
	  StateSet temp = new StateSets();
	  copy(sset, temp);
	  
	  int tracksize = 0;
	  
	  do {
		  tracksize = temp.size();
		  
		  for(int i=0; i<numS; i++) {
			  State s = allState[i];
			  int edgechecking = 0;
			  for(int j=0; j<s.getEdgeSize(); j++) {
				  if(!temp.isContain(s.getEdge(j))) {
					  edgechecking = 1;
					  break;
				  }
			  }	  
				  //here all successors are phi
			  if(edgechecking == 0 && !temp.isContain(i)) {
				  temp.addState(i);
			  }
			  else {
				  edgechecking = 0;
			  }
			  
		  }
		  
	  }while(tracksize!=temp.size());
	  
	  rset.destroy();
	  for(int k=0; k<temp.size(); k++) {
		rset.addState(temp.getEle(k));
	  }
	  
  }

  /**
    Labeling for AG.
    @param  sset  On input: a set of states satisfying p.
    @param  rset  On output: the set of states satisfying AG p
    is stored here.
    Note that rset and sset may point to the 
    same object!
    */
  public void AG(StateSet sset, StateSet rset) {
	  StateSet temp = new StateSets();
	  StateSet temp2 = new StateSets();
	  
	  NOT(sset, temp);
	  EF(temp, temp2);
	  NOT(temp2, rset);
  }

  //
  // Binary operations
  //

  /**
    Labeling for ^ (and).

    @param  sset1   On input: a set of states satisfying p.
    @param  sset2   On input: a set of states satisfying q.
    @param  rset    On output: the set of states satisfying p ^ q
    is stored here.
    Note that pointers sset1, sset2, and rset
    may be the same!
    */
  public void AND(StateSet sset1, StateSet sset2, StateSet rset) {
	StateSet temp = new StateSets();
	  for(int i=0; i<numS; i++) {
		  if(sset1.isContain(i)&&sset2.isContain(i)) {
			  temp.addState(i);
		  }
	  }
	rset.destroy();
	for(int k=0; k<temp.size(); k++) {
		rset.addState(temp.getEle(k));
	}
  }

  /**
    Labeling for v (or).

    @param  sset1   On input: a set of states satisfying p.
    @param  sset2   On input: a set of states satisfying q.
    @param  rset    On output: the set of states satisfying p v q
    is stored here.
    Note that pointers sset1, sset2, and rset
    may be the same!
    */
  public void OR(StateSet sset1, StateSet sset2, StateSet rset) {
	StateSet temp = new StateSets();
	  for(int i=0; i<numS; i++) {
		  if(sset1.isContain(i)||sset2.isContain(i)) {
			  temp.addState(i);
		  }
	  }
	rset.destroy();
	for(int j=0; j<temp.size(); j++) {
		rset.addState(temp.getEle(j));
	}
  }

  /**
    Labeling for -> (implies).

    @param  sset1   On input: a set of states satisfying p.
    @param  sset2   On input: a set of states satisfying q.
    @param  rset    On output: the set of states satisfying p -> q
    is stored here.
    Note that pointers sset1, sset2, and rset
    may be the same!
    */
  public void IMPLIES(StateSet sset1, StateSet sset2, StateSet rset) {
	  StateSet temp = new StateSets();
	  
	  NOT(sset1, temp);
	  OR(temp, sset2, rset);
  }

  /**
    Labeling for EU.

    @param  sset1   On input: a set of states satisfying p.
    @param  sset2   On input: a set of states satisfying q.
    @param  rset    On output: the set of states satisfying E p U q
    is stored here.
    Note that pointers sset1, sset2, and rset
    may be the same!
    */
  public void EU(StateSet sset1, StateSet sset2, StateSet rset){
	  StateSet temp = new StateSets();

	  copy(sset2, temp);
	  int tracksize;
	  
	  do{
		  tracksize = temp.size();
		  for(int i=0; i<sset1.size(); i++) {
			  int sid = sset1.getEle(i);
			  State s = allState[sid];
			  for(int j=0; j<s.getEdgeSize(); j++) {
				  if(temp.isContain(s.getEdge(j))&&!temp.isContain(sid)){
					  temp.addState(sid);
				  }
			  }
		  }
		  
	  }while(tracksize != temp.size());
	rset.destroy();
	for(int k=0; k<temp.size(); k++) {
		rset.addState(temp.getEle(k));
	}
  }

  /**
    Labeling for AU.

    @param  sset1   On input: a set of states satisfying p.
    @param  sset2   On input: a set of states satisfying q.
    @param  rset    On output: the set of states satisfying A p U q
    is stored here.
    Note that pointers sset1, sset2, and rset
    may be the same!
    */
 public void AU(StateSet sset1, StateSet sset2, StateSet rset){
	 StateSet temp = new StateSets();
	 copy(sset2, temp);
	 int tracksize;
	 do{
		 tracksize = temp.size();
		 for(int i=0; i<sset1.size(); i++) {
			 int checksucc = 0;
			 int sid= sset1.getEle(i);
			 State s = allState[sid];
			 for(int j=0; j<s.getEdgeSize(); j++) {
				 if(!temp.isContain(s.getEdge(j))) {
					 checksucc = 1;
					 break;
				 }
			 }
			 if(checksucc==0 && !temp.isContain(sid)) {
				 temp.addState(sid);
			 }
			 else
				 checksucc = 0;
		 }
		 
	 }while(tracksize!=temp.size());
	 rset.destroy();
	 for(int k=0; k<temp.size(); k++) {
		 rset.addState(temp.getEle(k));
	 }
	 
 }
 
  /**
    Check if a state is contained in a set.

    @param  s     State id to check
    @param  sset  Set of states

    @return true  iff state s is contained in set sset.
    */
  public boolean elementOf(int s, StateSet sset) {
	  return sset.isContain(s);
  }

  /**
    Display all states contained in a set to standard output.
    Output should be a comma separated list of state ids, in order,
    contained in the set.
    For example, output should be
    "{}"      for an empty set,
    "{S42}"   for a set containing state id 42,
    "{S0, S1, S7}"  for a set containing state ids 0, 1, and 7.

    @param  sset    Set to display.
    */
  public void display(StateSet sset) {
	  if(sset.isEmpty()) {
		  System.out.println("{}");
	  }
	  else {
		  System.out.print("{");
		  int i=0;
		  for(; i<sset.size()-1; i++) {
			  System.out.print("S"+sset.getEle(i)+", ");
		  }
		  System.out.print("S"+sset.getEle(i));
		  System.out.println("}");
	  }
  }

  
}

