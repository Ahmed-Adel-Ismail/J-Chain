package ahmedadekismail.myapplication;

import android.util.Log;

import com.chaining.Chain;

import org.javatuples.Pair;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Ahmed Adel Ismail on 11/12/2017.
 */

public class Samples {

    public static void main() {

        // manipulate data in a declarative way
        Integer value = 10;
        Integer finalValue = Chain.let(value)
                .apply(i -> Log.d("TAG", "first value : " + i))
                .map(i -> i * 10)
                .apply(i -> Log.d("TAG", "value Multiplied by 10 : " + i))
                .call();


        // handle optional values by making sure not to execute code if null
        Integer nullableValue = null;
        Integer finalNullableValue = Chain.optional(nullableValue)
                .apply(i -> Log.d("TAG", "log if value is not null : " + i))
                .defaultIfEmpty(10)
                .map(i -> i * 10)
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
        Chain.let(Arrays.asList(1, null, 2, null, 3, 4, 5))
                .apply(list -> list.remove(null))
                .flatMap(Observable::fromIterable)
                .forEach(item -> Log.d("TAG", "not null item : " + item));


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
                        Log.d("TAG", pair.getValue0() + " is in the passed list");
                    }
                })
                .map(Pair::getValue0)
                .call();

    }

}
