package ahmedadekismail.myapplication;

import com.chaining.Chain;

import org.junit.Test;

import java.util.concurrent.Callable;

/**
 * Created by Ahmed Adel Ismail on 11/20/2017.
 */
public class MainActivityTest {

    @Test
    public void main() {
        Chain
                .call(new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        return 10;
                    }
                })
                .and(20)
                .toList()
                .call();
    }

}