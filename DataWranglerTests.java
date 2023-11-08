import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.FileNotFoundException;

/**
 * Tester class
 */
public class DataWrangerTests {
  AirportLoader l;
  private final String times = "airportsTime.gv",
                       costs = "airports.gv";

  /**
   * Creates AirportLoader instances
   */
  @BeforeEach
  void createInstance() {
    l = new AirportLoader();
  }

  /**
   * Tests if the proper exceptions are thrown by the loader
   * @throws FileNotFoundException if something unexpected happens
   */
  @Test
  void testOpenFiles() throws FileNotFoundException {
    l.loadAirport(this.times, this.costs); // everything should work

    String times = "airport.png", // wrong file extensions
           costs = "dummy.png";

    Assertions.assertThrows(FileNotFoundException.class, () ->
            l.loadAirport(times, costs));

    Assertions.assertThrows(FileNotFoundException.class, () ->
            l.loadAirport(null, null));
  }

  /**
   * Test the airport
   */
  @Test
  void testAirport() {
    Airport a = new Airport("Test");
    Assertions.assertEquals("Test", a.name);

    Airport a2 = new Airport("Test");
    Assertions.assertEquals(a, a2);

    Assertions.assertEquals(a.toString(), "Test");
  }

  /**
   * Tests the line-parser when handling an exception and on reading in vertices
   */
  @Test
  void testParseLine1() {
    String line2parse = "digraph {"; // we don't do anything with this line
    l.parseLine(line2parse, false);
    Assertions.assertTrue(l.vLabels.isEmpty());
    Assertions.assertTrue(l.timeGraph.isEmpty());
    Assertions.assertTrue(l.costGraph.isEmpty());
    l.parseLine(line2parse, true);
    Assertions.assertTrue(l.vLabels.isEmpty());
    Assertions.assertTrue(l.timeGraph.isEmpty());
    Assertions.assertTrue(l.costGraph.isEmpty());

    line2parse = "A->B [weight=12.3]";
    String finalLine2parse = line2parse;
    Assertions.assertThrows(IllegalStateException.class, () ->
            l.parseLine(finalLine2parse, false)); // Error should be thrown because A is undefined

    // testing if we can parse a label line correctly
    line2parse = "A [label=\"Washington Dulles\"]";

    l.parseLine(line2parse, false);
    l.parseLine(line2parse, true);
    Assertions.assertEquals(l.vLabels.get("A"), new Airport("Washington Dulles"));
    Assertions.assertEquals(1, l.timeGraph.getVertexCount());


    line2parse = "B [label=\"DIA\"]";
    l.parseLine(line2parse, false);
    l.parseLine(line2parse, true);
    Assertions.assertEquals(2, l.timeGraph.getVertexCount());
    Assertions.assertEquals(l.vLabels.get("B"), new Airport("DIA"));

    // Testing if we can parse a weight line properly
    line2parse = "A->B [weight=2]";
    l.parseLine(line2parse, false);
    Assertions.assertEquals(1, l.timeGraph.getEdgeCount());
  }

  /**
   * Ensures that all the lines were parsed and we have a complete graph
   * @throws FileNotFoundException If something unexpected happens
   */
  @Test
  void testAirportLoader() throws FileNotFoundException {
    l.loadAirport(times, costs);

    /*
    A [label="Denver International Airport"]
   B [label="Dallas Fort/Worth"]
   C [label="Orlando International Airport"]
   D [label="Salt Lake City International"]
   E [label="O'Hare International Airport"]
   F [label="Dane County Regional Airport"]
     */

    Airport A = new Airport("Denver International Airport"),
            B = new Airport("Dallas Fort/Worth"),
            C = new Airport("Orlando International Airport"),
            D = new Airport("Salt Lake City International"),
            E = new Airport("O'Hare International Airport"),
            F = new Airport("Dane County Regional Airport");

    Airport[] list = {A, B, C, D, E, F};
    for(Airport a : list) {
      Assertions.assertTrue(l.costGraph.containsVertex(a));
      Assertions.assertTrue(l.timeGraph.containsVertex(a));
      for(Airport b : list) {
        if(!a.equals(b)) Assertions.assertTrue(l.timeGraph.containsEdge(a,b));
      }
    }

  }

  /**
   * Tests line parser on a weight line
   */
  @Test
  void testParseLine2() {
    String line2parse = "A [label=\"Washington Dulles\"]";

    l.parseLine(line2parse, false);
    l.parseLine(line2parse, true);
    Assertions.assertEquals(l.vLabels.get("A"), new Airport("Washington Dulles"));
    Assertions.assertEquals(1, l.timeGraph.getVertexCount());


    line2parse = "B [label=\"DIA\"]";
    l.parseLine(line2parse, false);
    l.parseLine(line2parse, true);
    Assertions.assertEquals(2, l.timeGraph.getVertexCount());
    Assertions.assertEquals(l.vLabels.get("B"), new Airport("DIA"));

    // Testing if we can parse a weight line properly
    line2parse = "A->B [weight=2]";
    l.parseLine(line2parse, false);
    Assertions.assertEquals(1, l.timeGraph.getEdgeCount());
  }

}
