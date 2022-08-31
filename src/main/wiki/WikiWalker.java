package main.wiki;

import java.util.*;

public class WikiWalker {
	
	private HashMap<String, Map<String, Integer>> hashM;
    
	public WikiWalker() {
		this.hashM = new HashMap<>();
    }

    /**
     * Adds an article with the given name to the site map and associates the
     * given linked articles found on the page. Duplicate links in that list are
     * ignored, as should an article's links to itself.
     * 
     * @param articleName
     *            The name of the page's article
     * @param articleLinks
     *            List of names for those articles linked on the page
     */
    public void addArticle(String articleName, List<String> articleLinks) {
    	TreeMap<String, Integer> innerMap = new TreeMap<String, Integer>();
    	Iterator<String> articleIt = articleLinks.iterator();
    	while(articleIt.hasNext()) {
    		innerMap.put(articleIt.next(), 0);
    	}
    	hashM.put(articleName, innerMap);
    }

    /**
     * Determines whether or not, based on the added articles with their links,
     * there is *some* sequence of links that could be followed to take the user
     * from the source article to the destination.
     * 
     * @param src
     *            The beginning article of the possible path
     * @param dest
     *            The end article along a possible path
     * @return boolean representing whether or not that path exists
     */
    public boolean hasPath(String src, String dest) {
    	boolean hasAPath = false;
        Queue<String> frontier = new LinkedList<String>();
        Set<String> visited = new HashSet<>();
        
        //puts all the articles into the frontier
        frontier.add(src);
        while(!frontier.isEmpty()) {
        	String current = frontier.poll();
        
        	//checks if the current article is the dest and checks
        	//if the src is the same as the dest
        	if(current.equals(dest)) {
        		hasAPath = true;
        		break;
        	}
        	if(visited.contains(current)) {
        		continue;
        	}
        	visited.add(current);
        	//if child is null then resets
        	if(hashM.get(current) == null) {
    			continue;
    		}
        	Set<String> hashKeys = hashM.get(current).keySet();
        	for(String innerKey : hashKeys) {
            	frontier.add(innerKey);
        	}
        }
        return hasAPath;
    }

    /**
     * Increments the click counts of each link along some trajectory. For
     * instance, a trajectory of ["A", "B", "C"] will increment the click count
     * of the "B" link on the "A" page, and the count of the "C" link on the "B"
     * page. Assume that all given trajectories are valid, meaning that a link
     * exists from page i to i+1 for each index i
     * 
     * @param traj
     *            A sequence of a user's page clicks; must be at least 2 article
     *            names in length
     */
    public void logTrajectory(List<String> traj) {
        if(traj.size() < 2) {
        	throw new IllegalArgumentException("Your trajectory needs to have"
        			+ "at least 2 items in length!");
        }
        ListIterator<String> trajIt = traj.listIterator();
        String current = trajIt.next();
        String currentNext = null;
        for(int i = 0; i < traj.size() - 1; i++) {
        	//skips first and looks at 2nd item
        	currentNext = trajIt.next();
        	hashM.get(current).replace(currentNext, hashM.get(current).get(currentNext), 
        			hashM.get(current).get(currentNext) + 1);
        	current = currentNext;
        }
    }

    /**
     * Returns the number of clickthroughs recorded from the src article to the
     * destination article. If the destination article is not a link directly
     * reachable from the src, returns -1.
     * 
     * @param src
     *            The article on which the clickthrough occurs.
     * @param dest
     *            The article requested by the clickthrough.
     * @throws IllegalArgumentException
     *             if src isn't in site map
     * @return The number of times the destination has been requested from the
     *         source.
     */
    public int clickthroughs(String src, String dest) {
    	//if src and dest same then return -1
    	if(src.equals(dest)) { return -1; }
    	if(!hasPath(src, dest)) { throw new IllegalArgumentException("Your source does not exist!"); }
    	//checks if src is on map
    	if(hashM.get(src).containsKey(dest)) {
    		return hashM.get(src).get(dest);
    	}
        return -1;
    }

    /**
     * Based on the pattern of clickthrough trajectories recorded by this
     * WikiWalker, returns the most likely trajectory of k clickthroughs
     * starting at (but not including in the output) the given src article.
     * Duplicates and cycles are possible outputs along a most likely trajectory. In
     * the event of a tie in max clickthrough "weight," this method will choose
     * the link earliest in the ascending alphabetic order of those tied.
     * 
     * @param src
     *            The starting article of the trajectory (which will not be
     *            included in the output)
     * @param k
     *            The maximum length of the desired trajectory (though may be
     *            shorter in the case that the trajectory ends with a terminal
     *            article).
     * @return A List containing the ordered article names of the most likely
     *         trajectory starting at src.
     */
    public List<String> mostLikelyTrajectory(String src, int k) {
    	ArrayList<String> theList = new ArrayList<String>();
    	String current = src;
    	for(int i = 0; i < k; i++) {
    		Integer mostClicks = 0;
    		Set<String> hashKeys = hashM.get(current).keySet();
    		for(String innerKey : hashKeys) {
            		if(hashM.get(current).get(innerKey) > mostClicks) {
				mostClicks = hashM.get(current).get(innerKey);
				current = innerKey;
				theList.add(current);
            		}
        	}
    	}
    	return theList;
    }
}


