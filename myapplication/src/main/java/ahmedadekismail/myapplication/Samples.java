package ahmedadekismail.myapplication;

import com.chaining.Chain;

import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * Created by Ahmed Adel Ismail on 11/12/2017.
 */

public class Samples {

    public static void main(String... args) {

        // manipulate data in a declarative way
        Integer value = 10;

        Integer finalValue = Chain.let(value)
                // apply an action : print value
                .apply((Consumer<Integer>) System.out::println)
                // map the item : convert to an int that holds the value multiplied by 10
                .map(i -> i * 10)
                // apply action : print the new value
                .apply((Consumer<Integer>) System.out::println)
                // retrieve the item to be stored in the Integer variable
                .call();


        // handle optional values by making sure not to execute code if null
        Integer nullableValue = null;

        // pass a value that maybe null :
        String finalNullableValue = Chain.optional(nullableValue)
                // print if not null :
                .apply((Consumer<Integer>) System.out::println)
                // if null, set the item to 10 :
                .defaultIfEmpty(10)
                // multiply the value by 10 :
                .map(i -> i * 10)
                // print the final value :
                .apply((Consumer<Integer>) System.out::println)
                // convert the integer to String
                .map(String::valueOf)
                // retrieve the value to be assigned to the variable :
                .call();

        // handle exception in a better way than Try/Catch
        Chain.let(0)
                .guard(integer -> {
                    throw new UnsupportedOperationException("crash!!!");
                })
                .onError(Throwable::printStackTrace);

        // return a value when a crash is thrown
        Integer crashingValue = Chain.let(0)
                .guard(integer -> {
                    throw new UnsupportedOperationException("crash!!!");
                })
                .onErrorReturnItem(10)
                .call();


        // convert to RxJava2 stream or any other stream through flatMap()
        List<Integer> numbersWithNulls = new ArrayList<>();
        numbersWithNulls.add(1);
        numbersWithNulls.add(null);
        numbersWithNulls.add(2);
        numbersWithNulls.add(null);
        numbersWithNulls.add(3);
        numbersWithNulls.add(4);
        numbersWithNulls.add(5);

        Chain.let(numbersWithNulls)
                .apply(list -> list.remove(null))
                .flatMap(Observable::fromIterable)
                .forEach((Consumer<Integer>) System.out::println);


        // join multiple RxJava2 streams
        Observable.fromIterable(Arrays.asList(1, 2, 3, 4, 5, 6))
                .filter(i -> i % 2 == 0)
                .toList()
                .map(Chain::let) // convert to Chain
                .blockingGet()
                .apply(list -> list.add(5))
                .flatMap(Observable::fromIterable) // start another stream
                .filter(i -> i % 2 != 0)
                .toList()
                .blockingGet();


        // check if an item is in a collection or not :
        List<Integer> evenNumbers = Arrays.asList(2, 4, 6, 8, 10);
        Chain.let(4)
                .in(evenNumbers)
                .apply(pair -> {
                    if (pair.getValue1()) {
                        System.out.println(pair.getValue0() + " is in the passed list");
                    }
                })
                .map(Pair::getValue0)
                .call();


        // when() and then()
        List<Integer> numbers = new ArrayList<>();
        numbers.add(1);
        numbers.add(2);

        Chain.let(numbers)
                .when(list -> list.contains(2))
                .then(list -> System.out.println("list contains value 2"))
                .when(list -> list.contains(3))
                .then(list -> System.out.println("list contains value 3"))
                .apply(List::clear);


    }

}
