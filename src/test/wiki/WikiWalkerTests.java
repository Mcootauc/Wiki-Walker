package test.wiki;

import static org.junit.Assert.*;
import java.util.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.rules.Timeout;
import org.junit.runner.Description;

import main.wiki.*;

public class WikiWalkerTests {
    
    // =================================================
    // Test Configuration
    // =================================================
    
    // Global timeout to prevent infinite loops from
    // crashing the test suite
    @Rule
    public Timeout globalTimeout = Timeout.seconds(2);
    
    // Used for grading, reports the total number of tests
    // passed over the total possible
    static int possible = 0, passed = 0;

    
    // Each time you pass a test, you get a point! Yay!
    // [!] Requires JUnit 4+ to run
    @Rule
    public TestWatcher watchman = new TestWatcher() {
        @Override
        protected void succeeded(Description description) {
            passed++;
        }
    };
    
    WikiWalker ww;
    @Before
    public void init () {
        possible++;
        ww = new WikiWalker();
    }
    
    @AfterClass
    public static void gradeReport () {
        System.out.println("============================");
        System.out.println("Tests Complete");
        System.out.println(passed + " / " + possible + " passed!");
        if ((1.0 * passed / possible) >= 0.9) {
            System.out.println("[!] Nice job!"); // Automated acclaim!
        }
        System.out.println("============================");
    }
    
    /**
     * Sets up the structure of the site map for the
     * WikiWalker used in each test
     * @param ww The WikiWalker object to setup with the
     * given 
     */
    private static void setupWW (WikiWalker ww) {
        ww.addArticle("A", Arrays.asList("B", "C"));
        ww.addArticle("B", Arrays.asList("A", "C"));
        ww.addArticle("C", Arrays.asList("D", "E"));
        ww.addArticle("D", Arrays.asList("B", "F"));
        ww.addArticle("E", Arrays.asList("F"));
    }

    
    // =================================================
    // Unit Tests
    // =================================================

    @Test
    public void testAddArticle() {
        setupWW(ww);
    }
    
    @Test
    public void testHasPath() {
        setupWW(ww);
        assertTrue(ww.hasPath("A", "A"));
        assertTrue(ww.hasPath("A", "B"));
        assertTrue(ww.hasPath("B", "A"));
        assertTrue(ww.hasPath("A", "F"));
        assertTrue(ww.hasPath("E", "F"));
        assertTrue(ww.hasPath("D", "E"));
        assertFalse(ww.hasPath("F", "E"));
        assertFalse(ww.hasPath("E", "D"));
    }
    
    @Test
    public void extraTestHasPath() {
        setupWW(ww);
        assertTrue(ww.hasPath("F", "F"));
        assertFalse(ww.hasPath("Doesn", "Deez Nuts"));
    }
    
    @Test
    public void testClickthroughs() {
        setupWW(ww);
        assertEquals(0, ww.clickthroughs("A", "B"));
        assertEquals(0, ww.clickthroughs("B", "A"));
        assertEquals(-1, ww.clickthroughs("A", "A"));
        assertEquals(-1, ww.clickthroughs("A", "D"));
    }
    
    @Test
    public void testTrajectories() {
        setupWW(ww);
        
        ww.logTrajectory(Arrays.asList("A", "B", "C", "D"));
        ww.logTrajectory(Arrays.asList("A", "C", "D", "F"));
        assertEquals(1, ww.clickthroughs("A", "B"));
        assertEquals(1, ww.clickthroughs("A", "C"));
        assertEquals(1, ww.clickthroughs("B", "C"));
        assertEquals(2, ww.clickthroughs("C", "D"));
        
        ww.logTrajectory(Arrays.asList("A", "B", "A", "B", "C"));
        assertEquals(3, ww.clickthroughs("A", "B"));
        assertEquals(1, ww.clickthroughs("B", "A"));
        assertEquals(2, ww.clickthroughs("B", "C"));
    }
    
    @Test
    public void testMostLikelyTrajectory() {
        setupWW(ww);
        
        assertEquals(Arrays.asList("B"), ww.mostLikelyTrajectory("A", 1));
        assertEquals(Arrays.asList("B", "A"), ww.mostLikelyTrajectory("A", 2));
        assertEquals(Arrays.asList("B", "A", "B"), ww.mostLikelyTrajectory("A", 3));
        
        ww.logTrajectory(Arrays.asList("A", "B", "C", "D"));
        ww.logTrajectory(Arrays.asList("A", "C", "D", "F"));
        ww.logTrajectory(Arrays.asList("A", "B", "A", "B", "C"));
        assertEquals(Arrays.asList("B"), ww.mostLikelyTrajectory("A", 1));
        assertEquals(Arrays.asList("B", "C"), ww.mostLikelyTrajectory("A", 2));
        assertEquals(Arrays.asList("B", "C", "D"), ww.mostLikelyTrajectory("A", 3));
        assertEquals(Arrays.asList("B", "C", "D", "F"), ww.mostLikelyTrajectory("A", 4));
        assertEquals(Arrays.asList("B", "C", "D", "F"), ww.mostLikelyTrajectory("A", 5));
        assertEquals(Arrays.asList("B", "C", "D", "F"), ww.mostLikelyTrajectory("A", 100));
    }

}
