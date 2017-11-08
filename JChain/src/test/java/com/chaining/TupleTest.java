package com.chaining;

import org.junit.Test;

import java.util.HashMap;

import io.reactivex.Observable;

import static org.junit.Assert.assertTrue;


public class TupleTest {

    @Test
    public void storeAPairOfValuesSuccessfully() throws Exception {
        Tuple<Integer, String> tuple = new Tuple<>(10, "string value");
        assertTrue(tuple.first() == 10 && tuple.second().equals("string value"));
    }

    @Test
    public void storeAPairOfValuesThroughFactoryMethodSuccessfully() throws Exception {
        Tuple<Integer, String> tuple = Tuple.from(10, "string value");
        assertTrue(tuple.first() == 10 && tuple.second().equals("string value"));
    }

    @Test
    public void createNewTupleFromWithValue_NewTupleCreatedAndOldNotChanged() throws Exception {
        Tuple<Integer, String> tuple = new Tuple<>(10, "string value");
        Tuple<Integer, Integer> newTuple = tuple.withSecond(10);
        assertTrue(tuple.second().equals("string value") && newTuple.second().equals(10));
    }

    @Test
    public void createNewTupleFromWithKey_NewTupleCreatedAndOldNotChanged() throws Exception {
        Tuple<Integer, String> tuple = new Tuple<>(10, "string value");
        Tuple<String, String> newTuple = tuple.withFirst("key");
        assertTrue(tuple.first().equals(10) && newTuple.first().equals("key"));
    }

    @Test
    public void getEntryInHashMapAsTuple_RetrieveTupleSuccessfully() throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("1", "a");
        Tuple<String, String> tuple = Tuple.from(Observable.fromIterable(map.entrySet())
                .firstElement()
                .blockingGet());

        assertTrue(tuple.first().equals("1") && tuple.second().equals("a"));
    }
}