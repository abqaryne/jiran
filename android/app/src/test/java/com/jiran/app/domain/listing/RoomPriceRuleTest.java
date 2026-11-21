package com.jiran.app.domain.listing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class RoomPriceRuleTest {
    private final RoomPriceRule rule = new RoomPriceRule();
    private final List<RoomPriceRule.Room> trio = Arrays.asList(
            room("master", "master"), room("medium", "medium"), room("small", "small"));

    @Test
    public void defaultExample() {
        assertPrices(rule.splitRoomPrices(150000, trio), 65000, 50000, 35000);
    }

    @Test
    public void positiveAndNegativeRemaindersGoToMaster() {
        assertPrices(rule.splitRoomPrices(100000, trio), 43400, 33300, 23300);
        assertPrices(rule.splitRoomPrices(100100, trio), 43300, 33400, 23400);
        assertPrices(rule.splitRoomPrices(100001, trio), 43401, 33300, 23300);
    }

    @Test
    public void negativeRemainderSubtractsFromMaster() {
        assertPrices(rule.splitRoomPrices(180, trio), 80, 100, 0);
    }

    @Test
    public void singleRoomReceivesExactRentForEveryType() {
        for (String type : Arrays.asList("master", "medium", "small")) {
            assertPrices(rule.splitRoomPrices(150049, Collections.singletonList(room("r", type))), 150049);
        }
    }

    @Test
    public void explicitAllocationReturnsUnallocatedSen() {
        assertEquals(10001, RoomPriceRule.remainingSen(150001, Arrays.asList(65000L, 50000L, 25000L)));
        assertEquals(150001, RoomPriceRule.remainingSen(150001, Collections.emptyList()));
    }

    @Test
    public void overAllocationRejectedIncludingOverflowSizedInputs() {
        assertThrows(IllegalArgumentException.class,
                () -> RoomPriceRule.remainingSen(150000, Arrays.asList(65000L, 50000L, 35001L)));
        assertThrows(IllegalArgumentException.class,
                () -> RoomPriceRule.remainingSen(Long.MAX_VALUE, Arrays.asList(Long.MAX_VALUE, 1L)));
    }

    @Test
    public void occupiedAndVacantPricesBothCount() {
        Map<String, Long> pricesByStatus = new HashMap<>();
        pricesByStatus.put("occupied", 65000L);
        pricesByStatus.put("vacant", 50000L);
        assertEquals(35000, RoomPriceRule.remainingSen(150000, pricesByStatus.values()));
        pricesByStatus.put("occupied", 100001L);
        assertThrows(IllegalArgumentException.class,
                () -> RoomPriceRule.remainingSen(150000, pricesByStatus.values()));
    }

    @Test
    public void rereadsDynamicConfigOnSameRuleInstance() {
        Map<String, Object> app = new HashMap<>();
        RoomPriceRule dynamic = new RoomPriceRule(() -> app);
        assertPrices(dynamic.splitRoomPrices(150000, trio), 65000, 50000, 35000);
        app.put("roomPriceWeights", weights(2, 1, 1));
        assertPrices(dynamic.splitRoomPrices(150000, trio), 75000, 37500, 37500);
        app.put("roomPriceWeights", weights(1, 1, 1));
        assertPrices(dynamic.splitRoomPrices(150000, trio), 50000, 50000, 50000);
    }

    @Test
    public void missingConfigAndMissingWeightUseDraftDefaults() {
        assertPrices(new RoomPriceRule(() -> null).splitRoomPrices(150000, trio), 65000, 50000, 35000);
        RoomPriceRule partial = new RoomPriceRule(() -> Collections.singletonMap(
                "roomPriceWeights", Collections.singletonMap("master", 1.3)));
        assertPrices(partial.splitRoomPrices(150000, trio), 65000, 50000, 35000);
    }

    @Test
    public void noMasterUsesHighestWeightAndFirstOnTies() {
        assertPrices(rule.splitRoomPrices(1001, Arrays.asList(room("s", "small"), room("m", "medium"))),
                400, 601);
        assertPrices(rule.splitRoomPrices(1001, Arrays.asList(room("a", "medium"), room("b", "medium"))),
                501, 500);
        RoomPriceRule custom = new RoomPriceRule(() -> Collections.singletonMap(
                "roomPriceWeights", weights(1, 1, 2)));
        assertPrices(custom.splitRoomPrices(1001, Arrays.asList(room("m", "medium"), room("s", "small"))),
                300, 701);
    }

    @Test
    public void firstMasterWinsEvenIfAnotherTypeHasHigherWeight() {
        RoomPriceRule custom = new RoomPriceRule(() -> Collections.singletonMap(
                "roomPriceWeights", weights(1, 2, 1)));
        assertPrices(custom.splitRoomPrices(1001, Arrays.asList(
                room("m", "medium"), room("a", "master"), room("b", "master"))), 500, 201, 300);
    }

    @Test
    public void tinyRentRemainsNonNegativeAndReducesOtherRoomsInOrder() {
        RoomPriceRule custom = new RoomPriceRule(() -> Collections.singletonMap(
                "roomPriceWeights", weights(0.01, 1, 1)));
        assertPrices(custom.splitRoomPrices(101, trio), 0, 1, 100);
        assertPrices(rule.splitRoomPrices(1, trio), 1, 0, 0);
        assertPrices(rule.splitRoomPrices(0, trio), 0, 0, 0);
    }

    @Test
    public void halfRinggitRoundsUpBeforeRemainderCorrection() {
        assertPrices(rule.splitRoomPrices(100, Arrays.asList(room("a", "master"), room("b", "master"))),
                0, 100);
    }

    @Test
    public void emptyRoomsLeaveRentUnallocated() {
        RoomPriceRule.Split split = rule.splitRoomPrices(1000, Collections.emptyList());
        assertTrue(split.pricesSen.isEmpty());
        assertEquals(1000, split.remainingSen);
    }

    @Test
    public void largeRentDoesNotOverflowDuringRounding() {
        assertPrices(rule.splitRoomPrices(Long.MAX_VALUE, Collections.singletonList(room("r", "master"))),
                Long.MAX_VALUE);
        assertEquals(0, rule.splitRoomPrices(Long.MAX_VALUE, trio).remainingSen);
    }

    @Test
    public void rejectsMalformedRoomsAndRent() {
        assertThrows(IllegalArgumentException.class, () -> new RoomPriceRule.Room("", "master"));
        assertThrows(IllegalArgumentException.class, () -> new RoomPriceRule.Room("a", "unknown"));
        assertThrows(IllegalArgumentException.class, () -> rule.splitRoomPrices(-1, trio));
        assertThrows(IllegalArgumentException.class, () -> rule.splitRoomPrices(1, null));
        assertThrows(IllegalArgumentException.class,
                () -> rule.splitRoomPrices(1, Collections.singletonList(null)));
        assertThrows(IllegalArgumentException.class,
                () -> rule.splitRoomPrices(1, Arrays.asList(room("a", "master"), room("a", "small"))));
    }

    @Test
    public void rejectsInvalidPrices() {
        assertThrows(IllegalArgumentException.class, () -> RoomPriceRule.remainingSen(-1, Collections.emptyList()));
        assertThrows(IllegalArgumentException.class, () -> RoomPriceRule.remainingSen(1, null));
        assertThrows(IllegalArgumentException.class,
                () -> RoomPriceRule.remainingSen(1, Collections.singletonList(null)));
        assertThrows(IllegalArgumentException.class,
                () -> RoomPriceRule.remainingSen(1, Collections.singletonList(-1L)));
    }

    @Test
    public void rejectsMalformedConfigInsteadOfSilentlyUsingDefaults() {
        for (Object value : Arrays.asList(0, -1, Double.NaN, Double.POSITIVE_INFINITY, "1.3", null)) {
            RoomPriceRule invalid = new RoomPriceRule(() -> Collections.singletonMap(
                    "roomPriceWeights", Collections.singletonMap("master", value)));
            assertThrows(IllegalArgumentException.class, () -> invalid.splitRoomPrices(1000, trio));
        }
        RoomPriceRule invalid = new RoomPriceRule(() -> Collections.singletonMap("roomPriceWeights", "bad"));
        assertThrows(IllegalArgumentException.class, () -> invalid.splitRoomPrices(1000, trio));
    }

    @Test
    public void resultCannotBeMutated() {
        assertThrows(UnsupportedOperationException.class,
                () -> rule.splitRoomPrices(150000, trio).pricesSen.put("master", 0L));
    }

    @Test
    public void smallRentsConserveEverySenAcrossRoomCombinations() {
        for (String a : Arrays.asList("master", "medium", "small")) {
            for (String b : Arrays.asList("master", "medium", "small")) {
                for (int rent = 0; rent <= 1000; rent++) {
                    RoomPriceRule.Split split = rule.splitRoomPrices(rent,
                            Arrays.asList(room("a", a), room("b", b), room("c", "medium")));
                    long sum = 0;
                    for (long price : split.pricesSen.values()) {
                        assertTrue(price >= 0);
                        sum += price;
                    }
                    assertEquals(rent, sum);
                    assertEquals(0, split.remainingSen);
                }
            }
        }
    }

    private static RoomPriceRule.Room room(String id, String type) {
        return new RoomPriceRule.Room(id, type);
    }

    private static Map<String, Number> weights(Number master, Number medium, Number small) {
        Map<String, Number> result = new HashMap<>();
        result.put("master", master);
        result.put("medium", medium);
        result.put("small", small);
        return result;
    }

    private static void assertPrices(RoomPriceRule.Split split, long... expected) {
        assertEquals(expected.length, split.pricesSen.size());
        int index = 0;
        for (long actual : split.pricesSen.values()) {
            assertEquals(expected[index++], actual);
        }
        assertEquals(0, split.remainingSen);
    }
}
