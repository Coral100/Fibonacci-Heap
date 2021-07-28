/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over integers.
 */
public class FibonacciHeap
{
	private HeapNode min;
	private int size;
	protected HeapNode leftTree;
	protected int TreeNum;
	protected static int totalLinks = 0;
	protected static int totalCuts = 0;
	private int marks;
	
	public FibonacciHeap() { //O(1)
		this.min = null;
		this.size = 0;
		this.leftTree = null;
		this.TreeNum = 0;
		this.marks = 0;
	}
	
	
   /**
    * public boolean isEmpty()
    *
    * precondition: none
    * 
    * The method returns true if and only if the heap
    * is empty.
    *   
    */
    public boolean isEmpty() //O(1)
    {
    	if(this.size == 0) {
    		return true;
    	}
    	return false;
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * 
    * Returns the new node created. 
    */
    public HeapNode insert(int key) //O(1)
    {    
    	HeapNode curr = new HeapNode(key);
    	
    	if(this.isEmpty()) {
    		this.min = curr;
    		this.leftTree = curr;

    	}else {
    		if(key < this.min.getKey()) {
    			this.min = curr;
    		}
  
    		curr.setNext(this.leftTree);
    		curr.setPrev(this.leftTree.getPrev()); 
    		this.leftTree.setPrev(curr);
    		this.leftTree = curr;
    		curr.getPrev().setNext(curr);
      	
    		
    	}
    	this.size = this.size + 1;
    	this.TreeNum = this.TreeNum + 1;
    	return curr;
    }

   /**
    * public void deleteMin()
    *
    * Delete the node containing the minimum key.
    *
    */
    public void deleteMin() //WC-O(n),Amort-O(log(n))
    {
    	if(this.size == 1) {//the heap contains single key
    		
    		this.min = null;
    		this.size = 0;
    		this.TreeNum = 0;
    		this.leftTree = null;
    		
    	}else {//heap contains more than one key
    		
    		DeleteNode(this.min);
    		double phi = (Math.sqrt(5) + 1)*0.5;
    		int n = this.size;
    		double t = Math.log10(n);
    		int maxRank =(int) (t/Math.log10(phi)) + 1; //extract max rank by log's laws
    		
    		HeapNode[] array = new HeapNode[maxRank];
    		HeapNode[] fullBuckets = Consolidating(array, this.leftTree, this.TreeNum);
    		
    		this.TreeNum = 0; //buckets are full so heap has no trees
    		HeapNode Newroot = null;
    		for(int i = 0; i < fullBuckets.length; i++) {
    			if(fullBuckets[i] != null) {
    				if(Newroot == null) {//the heap was empty
    					Newroot = fullBuckets[i] ;
    					this.leftTree = Newroot;
    					this.min = Newroot;
    				}else {
    					Newroot.setNext(fullBuckets[i]);
    					fullBuckets[i].setPrev(Newroot);
    					Newroot = Newroot.getNext();
    					if(Newroot.getKey() < this.min.getKey()) {
    						this.min = Newroot;
    					}
    					
    				}
    				this.TreeNum = this.TreeNum + 1;
    			}
    		}
    		this.leftTree.setPrev(Newroot);
    		Newroot.setNext(this.leftTree);//connect the circle at the end
    		this.size = this.size - 1;
    	
    	}
     	
    }

   /**
    * public HeapNode findMin()
    *
    * Return the node of the heap whose key is minimal. 
    *
    */
    public HeapNode findMin() //O(1)
    {
    	return this.min;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Meld the heap with heap2
    *
    */
    public void meld (FibonacciHeap heap2) //O(1)
    {
    	if(heap2.size() != 0) {//heap2 is not empty
    		if(this.size() == 0) {
    			this.size = heap2.size;
    			this.min = heap2.min;
    			this.leftTree = heap2.leftTree;
    			this.TreeNum = heap2.TreeNum;
    			this.marks = heap2.marks;
    			
    		}else {
    			HeapNode keep = this.leftTree.getPrev();
    			heap2.leftTree.getPrev().setNext(this.leftTree);
    			this.leftTree.getPrev().setNext(heap2.leftTree);
    			this.leftTree.setPrev(heap2.leftTree.getPrev());
    			heap2.leftTree.setPrev(keep);
    			if(heap2.min.getKey() < this.min.getKey()) {
    				this.min = heap2.min;
    			}
    			this.size = this.size + heap2.size;
    			this.TreeNum = this.TreeNum + heap2.TreeNum;
    			this.marks = this.marks + heap2.marks;
    		}
    	}
   	
    }

   /**
    * public int size()
    *
    * Return the number of elements in the heap
    *   
    */
    public int size() //O(1)
    {
    	return this.size; 
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap. 
    * 
    */
    public int[] countersRep() //O(n)
    {
    	double phi = (Math.sqrt(5) + 1)*0.5;
		int n = this.size;
		double t = Math.log10(n);
		int maxRank =(int) (t/Math.log10(phi)) + 1;

	int[] arr = new int[maxRank];
	HeapNode curr = this.leftTree;
	
	for(int i = 0 ; i < this.TreeNum; i++) {
		arr[curr.getRank()] = arr[curr.getRank()] + 1;
		curr = curr.getNext();
	}
        return arr; 
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap. 
    *
    */
    public void delete(HeapNode x) //O(log(n))
    {    
    	int delta = x.getKey() - this.min.getKey() + 1;
    	if(x.getKey() != this.min.getKey()) {
    		decreaseKey(x, delta);
    	}
    	deleteMin(); //updates new min and other parameters inside
 
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * The function decreases the key of the node x by delta. The structure of the heap should be updated
    * to reflect this chage (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta) //O(log(n))
    {  
    	int newKey = x.getKey() - delta;
    	x.setKey(newKey);
    	if(newKey < this.min.getKey()) {
    		this.min = x;
    	}
    	if(x.getParent() != null) { //x is not the root
    		if(newKey < x.getParent().getKey()) {// new key destroys heap's order
    			cascadingaCut(x);
    		}
    		
    	}
    }

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap. 
    */
    public int potential() //O(1)
    {    
    	return (this.TreeNum + 2*this.marks);
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the run-time of the program.
    * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
    * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
    * in its root.
    */
    public static int totalLinks() //O(1)
    {    
    	return totalLinks;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the run-time of the program.
    * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts() //O(1)
    {    
    	return totalCuts;
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k minimal elements in a binomial tree H.
    * The function should run in O(k*deg(H)). 
    * You are not allowed to change H.
    */

    public static int[] kMin(FibonacciHeap H, int k)//O(k*deg(H))
    {    
		int[] arr = new int[k];
		if (k==0) {
			return arr;
		}
		
		if (k==1) {
			arr[0] = H.min.getKey();
			return arr;
		}
		
		arr[0] = H.min.getKey();
		
		FibonacciHeap ezerheap = new FibonacciHeap();
		HeapNode currMin = ChildCandidate(H.min); //next min after the root is necesserally one of the children in the first level
		ezerheap.insert(currMin.getKey()).MyPointer = currMin;
		
		for (int i = 1 ;i < k; i++) {
			currMin = ezerheap.findMin();
			arr[i] = currMin.getKey();
			
			HeapNode MinAmongChild = ChildCandidate(currMin.MyPointer);//next min can be a child of child or brother of child
			HeapNode MinAmongBrother = BrotherCandidate(currMin.MyPointer);
			
			if (MinAmongChild != null) {
				ezerheap.insert(MinAmongChild.getKey()).MyPointer= MinAmongChild;
			}
			if (MinAmongBrother != null) {
				ezerheap.insert(MinAmongBrother.getKey()).MyPointer = MinAmongBrother;
			}
			
			ezerheap.deleteMin();
		}
		return arr;
    }
    
    /**
     * private static BrotherCandidate(HeapNode node)
     *
     * finds next min's candidate among min's brothers
     *
     */
    
	private static HeapNode BrotherCandidate(HeapNode node) { //O(deg(H))
		if (node.getNext().getKey() == node.getKey()) {
			return null;
		}
		
		HeapNode curr = node.getNext();
		HeapNode min = null;
		
		while (curr.getKey() != node.getKey()) {
			if (min == null && curr.getKey() > node.getKey()) {
				min = curr;
			}
			if (min != null && curr.getKey() < min.getKey() && curr.getKey() > node.getKey()) {
				min = curr;
			}
			curr = curr.getNext();
		}
		return min;
	}
    /**
     * private static ChildCandidate(HeapNode node)
     *
     * finds next min's candidate among min's children
     *
     */
	private static HeapNode ChildCandidate(HeapNode node) { //O(deg(H))
		if (node.getChild() == null) {
			return null;
		}
		
		HeapNode curr = node.getChild();
		HeapNode min = curr;
		
	
		curr = curr.getNext();
			
		if (min.getKey() > curr.getKey()) {
				min = curr;
			}
		 while (curr.getKey() != node.getChild().getKey()) {
			 curr = curr.getNext();
				if (min.getKey() > curr.getKey()) {
					min = curr;
		 }
		 }
		
		return min;
	}
    /**
     * public void DeleteNode(HeapNode node)
     *
     * making all necessary changes in order to delete a node from heap
     *
     */
    public void DeleteNode(HeapNode node) { //O(log(n))
    	if(node.getRank() == 0) {//node has no children
    		node.getPrev().setNext(node.getNext());
    		node.getNext().setPrev(node.getPrev());
    		if(node.getKey() == this.leftTree.getKey()) {
    			this.leftTree = node.getNext();
    		}
    	}else {//node has children
    		
    		HeapNode child = node.getChild();
    		child.setParent(null);//disconnect from father
    		
    		if(child.getNext() == null) {//only 1 child
    			if(child.mark == true) {
    				child.mark = false;
    				this.marks = this.marks - 1;
    			}
    		}else {//more than 1 child
    			child = child.getNext();
    			while(child.getKey() != node.getChild().getKey()) {//min's children shoul'dnt be marked
    				child.setParent(null);//disconnect from father
    				if(child.mark == true) {
    					child.mark = false;
    					this.marks = this.marks - 1;
    				}
    			child =child.getNext();
    			}
    		}
    		node.getNext().setPrev(node.getChild().getPrev());
    		node.getChild().getPrev().setNext(node.getNext());//closes the circle
    		node.getChild().setPrev(node.getPrev());
    		node.getPrev().setNext(node.getChild());
    		if(node.getKey() == this.leftTree.getKey()) {
    			this.leftTree = node.getChild();
    		}	
    	
    	}
    	this.TreeNum = this.TreeNum + node.getRank() - 1;
    	node.setChild(null);
    	node.setNext(null);
    	node.setPrev(null);
    	this.min = null;
    }
    /**
     * public static HeapNode[] Consolidating(HeapNode [] array, HeapNode leftmost, int treeNum)
     *
     * linking trees with same degree in consolidating process as we learned in class
     *
     */
    	
    public static HeapNode[] Consolidating(HeapNode [] array, HeapNode leftmost, int treeNum) {//WC-O(n),Amort-O(log(n))
    	
    	if(treeNum == 1) { //heap contains 1 tree so no need consolidating
    		array[leftmost.getRank()] = leftmost;
    		
    	}else {//heap contains more than 1 tree
    		
    		HeapNode curr = leftmost;
        	leftmost.getPrev().setNext(leftmost.getPrev());
        	leftmost.setPrev(leftmost);//disconnect the circle
    	
    	for(int i = 0; i < treeNum; i++) {
    	
    		 HeapNode Nextcurr = curr.getNext();

    		if(array[curr.getRank()] == null) {	//bucket is empty
    			curr.getNext().setPrev(Nextcurr);
        		curr.setNext(curr);//disconnect curr from the next root in the heap
    			array[curr.getRank()] = curr;
    			
    		}else {//bucket has tree inside
    			curr.getNext().setPrev(Nextcurr);
        		curr.setNext(curr);//disconnect curr from the next root in the heap
        		
        		int tmprank = curr.getRank();
    			HeapNode bucketcurr = curr;
    			
    			while(array[tmprank] != null) {//while the bucket is full continue linking
    				HeapNode newTree = null;
    				newTree = Link(bucketcurr, array[tmprank]);
    				totalLinks = totalLinks + 1;
    				array[tmprank] = null;
    			
    				tmprank = newTree.getRank();
    				bucketcurr = newTree;
    			}
    			array[tmprank] = bucketcurr;
    			}
    		
    		curr = Nextcurr;//next root in the heap
    		
    }}
    	return array;
    }
    
    /**
     * public static HeapNode Link(HeapNode root1, HeapNode root2) 
     *
     * making all necessary changes in order to link 2 trees 
     *
     */
    public static HeapNode Link(HeapNode root1, HeapNode root2) { //O(1)
    	HeapNode high = null;
    	HeapNode low = null;
    	if(root2.getKey() < root1.getKey()) {
    		 high = root2;
    		 low = root1;
    	}else {
    		 high = root1;
    		 low = root2;
    	}
    	if(high.getRank() > 0) {
    		high.getChild().getPrev().setNext(low);
    		low.setPrev(high.getChild().getPrev());
    		low.setNext(high.getChild());
    		high.getChild().setPrev(low);	
    	}
    	
    	low.setParent(high);
    	high.setChild(low);
    	high.setRank(high.getRank() + 1);
    	
    	
    	return high;	
    		
    	}
    /**
     * public static HeapNode Link(HeapNode root1, HeapNode root2) 
     *
     * making sure the next invariant always holds- one node can not lose more than 1 child
     *
     */
    	
    public void cascadingaCut(HeapNode node) { //WC-O(num of cuts),Amort-O(1)
    	HeapNode Up = node.getParent();
    	Cutting(node);
    	totalCuts = totalCuts + 1;
    	
    	if(Up.mark == false) {//y is not marked
    		if(Up.getParent() != null) { //y is not the root
        		Up.mark = true;
        		this.marks = this.marks + 1;
        	}
    	}
    	else{//y is already marked- keep cutting
    		HeapNode tmpUp = null;
    		while((Up.getParent() != null) && (Up.mark == true)){ //go up as long as not root and  marked
    			tmpUp = Up.getParent(); //keep the parent before looses him
    			Cutting(Up);
    			totalCuts = totalCuts + 1;
    			Up = tmpUp;
    		}if(Up.getParent() != null) {//if we stopped at y which is not a root - mark
    			Up.mark = true;
    			this.marks = this.marks + 1;
    		}
    	}
    
    }
    /**
     * public void Cutting(HeapNode node) 
     *
     * making all necessary changes in order to cut a node from a tree in the heap
     *
     */
    public void Cutting(HeapNode node) { //O(1)
    	
    	if(node.mark == true) {
    		node.mark = false;
    		this.marks = this.marks - 1;
    	}
    	if(node.getNext().getKey() == node.getKey() && node.getPrev().getKey() == node.getKey()) {//node has no syblings
    		node.getParent().setRank(node.getParent().getRank() - 1);
    		node.getParent().setChild(null);
    		node.setParent(null);
    		
    	}else {//node has syblings
    		if(node.getParent().getChild().getKey() == node.getKey()) {//node is the leftmost child
    			node.getNext().setPrev(node.getPrev());
        		node.getPrev().setNext(node.getNext());
        		node.getParent().setChild(node.getNext());
        		node.getParent().setRank(node.getParent().getRank() - 1);
        		node.setParent(null);
        		
        	}else {
        		node.getNext().setPrev(node.getPrev());
        		node.getPrev().setNext(node.getNext());
        		node.getParent().setRank(node.getParent().getRank() - 1);
        		node.setParent(null);
        	}
    	}
    	if(node.mark == true) {
    		node.mark = false;
    		this.marks = this.marks - 1;
    	}
    	node.setNext(this.leftTree);
    	node.setPrev(this.leftTree.getPrev());
    	this.leftTree.getPrev().setNext(node);
    	this.leftTree.setPrev(node);
    	this.leftTree = node;
    	this.TreeNum = this.TreeNum + 1;    	
    	
    }
   
    
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in 
    * another file 
    *  
    */
    public class HeapNode{

	public int key;
	private int rank;
	private boolean mark;
	private HeapNode child;
	private HeapNode next;
	private HeapNode prev;
	private HeapNode parent;
	private HeapNode MyPointer;

	

  	public HeapNode(int key) { //O(1)
	    this.key = key;
		this.rank = 0;
		this.mark = false;
		this.child = null;
		this.next = this;
		this.prev = this;
		this.parent = null;
		this.MyPointer = null;
      }

  	public int getKey() { //O(1)
	    return this.key;
      }
  	
  	public void setKey(int key) { //O(1)
  		this.key = key;
  	}
  	
  	public int getRank() { //O(1)
  		return this.rank;
  	}
  	
  	public void setRank(int rank) { //O(1)
  		this.rank = rank;
  	}
  	
  	public HeapNode getNext() { //O(1)
  		return this.next;
  	}
  	
  	public void setNext(HeapNode next) { //O(1)
  		this.next = next;
  	}
  	
  	public HeapNode getPrev() { //O(1)
  		 return this.prev;
  	}
  	
  	public void setPrev(HeapNode prev) { //O(1)
  		this.prev = prev;
  	}
  	
  	public HeapNode getParent() { //O(1)
  		return this.parent;
  	}
  	
  	public void setParent(HeapNode parent) { //O(1)
  		this.parent = parent;
  	}
  	
  	public HeapNode getChild() { //O(1)
  		return this.child;
  	}
  	
  	public void setChild(HeapNode child) { //O(1)
  		this.child = child;
  	}
  	

    }
  
}


