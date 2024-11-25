package it.unibo.oop.lab.lambda;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

/*
 * CHECKSTYLE: MagicNumber OFF
 * The above comment shuts down checkstyle: in a test suite, magic numbers may be tolerated.
 */
/**
 * Simple test for {@link it.unibo.oop.lab.lambda.LambdaUtilities}.
 */
final class TestLambdaUtilities {

    /**
     * Test dup method.
     */
    @Test
    void testDup() {
        assertEquals(
            List.of(1, 101, 2, 102, 3, 103, 4, 104, 5, 105),
            LambdaUtilities.dup(List.of(1, 2, 3, 4, 5), x -> x + 100) 
        );
        assertEquals(
            List.of("a", "aa", "b", "bb", "c", "cc"),
            LambdaUtilities.dup(List.of("a", "b", "c"), x -> x + x)
        );
    }

    /**
     * Test optFilter method.
     */
    @Test
    void testOptFilter() {
        assertEquals(
            List.of(Optional.empty(), Optional.of(2), Optional.empty(), Optional.of(4), Optional.empty(), Optional.of(6)),
            LambdaUtilities.optFilter(List.of(1, 2, 3, 4, 5, 6), x -> x % 2 == 0)
        );
        assertEquals(
            List.of(Optional.empty(), Optional.of("bcd"), Optional.of("qw"), Optional.empty(), Optional.empty()),
            LambdaUtilities.optFilter(List.of("a", "bcd", "qw", "e", ""), x -> x.length() > 1)
        );
    }

    /**
     * Test group method.
     */
    @Test
    void testGroup() {
        assertEquals(
            Map.of(
                "even", Set.of(2, 4),
                "odd", Set.of(1, 3, 5)
            ),
            LambdaUtilities.group(List.of(1, 2, 3, 4, 5), x -> x % 2 == 0 ? "even" : "odd")
        );
        assertEquals(
            Map.of(
                3, Set.of("abc", "qwe"),
                2, Set.of("zx", "cv"),
                1, Set.of("o")
            ),
            LambdaUtilities.group(List.of("abc", "qwe", "zx", "o", "cv"), String::length)
        );
    }

    /**
     * Test fill method.
     */
    @Test
    void testFill() {
        final var random = new Random();
        final var map = LambdaUtilities.fill(
            Map.of(
                "p1", Optional.of(1),
                "p2", Optional.of(2),
                "n1", Optional.empty(),
                "p3", Optional.of(3),
                "n2", Optional.empty()
            ),
            () -> random.nextInt(10) - 10
        );
        assertEquals(map.keySet(), Set.of("p1", "p2", "p3", "n1", "n2"));
        assertEquals(map.get("p1").intValue(), 1);
        assertEquals(map.get("p2").intValue(), 2);
        assertEquals(map.get("p3").intValue(), 3);
        assertTrue(map.get("n1") < 0);
        assertTrue(map.get("n2") < 0);
    }
}
