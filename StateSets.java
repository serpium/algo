import java.util.ArrayList;

public class StateSets implements StateSet {
		
	private ArrayList<Integer> stateSet;
	
	/**
	 * Constructor
	 */
	public StateSets() {
		stateSet = new ArrayList<Integer>();
	}
	
	public void destroy() {
		if(!stateSet.isEmpty()){
			stateSet.clear();
		}
	}
	
	public void addState(int id) {
		stateSet.add(id);
	}
	
	public boolean isEmpty() {
		return stateSet.isEmpty();
	}
	
	public int size() {
		return stateSet.size();
	}
	
	public int getEle(int index) {
		return stateSet.get(index);
	}
	
	public boolean isContain(int id) {
		for(int i=0; i<stateSet.size(); i++) {
			if(stateSet.get(i)==id) {
				return true;
			}
		}
		
		return false;
	}
	
	public int pop() {
		stateSet.remove(0);
		return stateSet.get(0);
	}
	
	public void remove(int index) {
		stateSet.remove(index);
	}
}
