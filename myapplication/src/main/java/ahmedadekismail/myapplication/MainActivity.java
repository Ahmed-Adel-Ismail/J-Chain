package ahmedadekismail.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.chaining.Chain;

import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final List<String> liveActivitiesNames = new ArrayList<>(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        liveActivitiesNames.add(getClass().getName());

        Chain.let(this)
                .apply(MainActivity::doSomething)
                .debug(MainActivity::logSomethingDone)
                .map(MainActivity::getClass)
                .map(Class::getName)
                .in(liveActivitiesNames)
                .when(Pair::getValue1)
                .then(pair -> Log.d(pair.getValue0(), "Activity is alive"));
    }


    private void doSomething() {
        // ...
    }

    private static void logSomethingDone(MainActivity mainActivity) {
        // ...
    }


}
