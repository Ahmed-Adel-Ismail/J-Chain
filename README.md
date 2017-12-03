[![](https://jitpack.io/v/Ahmed-Adel-Ismail/J-Chain.svg)](https://jitpack.io/#Ahmed-Adel-Ismail/J-Chain)

# J-Chain
A library that provides a set of functional patterns to enable chaining operations one after another, also helps not cutting RxJava2 streams 

# How things work

The purpose of the <b>Chain</b> is to hold on to an item, and update it in a functional manner, where we can <b>apply</b> an action to the stored item, or we can <b>map</b> this item to another item, or we can even convert the current functions <b>Chain</b> into another Object or RxJava Stream through the <b>flatMap</b> function ... if we want the item stored after updating it, we can use the <b>call()</b> function to get the stored item ... unlike RxJava, any operation is done when it's function is called, you do not need to wait for the <b>call()</b> method to be invoked to execute the code ... more samples in the coming lines :

# Manipulate data in a declarative way

    Integer value = 10;       
	Integer finalValue = Chain.let(value)
		// apply an action : print value
		.apply(System.out::println)
		// map the item : convert to an int that holds the value multiplied by 10
		.map(i -> i * 10)
		// apply action : print the new value
		.apply(System.out::println)
		// retrieve the item to be stored in the Integer variable
		.call();

# Handle optional values by making sure not to execute code if null

We have the <b>Chain.optional()</b> function, which accepts a value that can be null, and if the value is not null, it will invoke the <b>apply()</b> functions, you cannot exit the optional state unless you call <b>defaultIfEmpty()</b> :

    Integer nullableValue = null;
	
	// pass a value that maybe null :
	String finalNullableValue = Chain.optional(nullableValue)
			// print if not null :
			.apply(System.out::println)
			// if null, set the item to 10 :
			.defaultIfEmpty(10)
			// multiply the value by 10 :
			.map(i -> i * 10)
			// print the final value :
			.apply(System.out::println)
			// convert the integer to String
			.map(String::valueOf)
			// retrieve the value to be assigned to the variable :
			.call();                 

# Handle exception in a better way than Try/Catch

We can execute the risky code in a <b>Chain.guard()</b> function, in this case we guarantee that the application wont crash, but to exit the <b>Guard</b> state for the chain, we should call <b>onError()</b> or <b>onErrorReturn()</b> or <b>onErrorReturnItem()</b> :

    Chain.let(0)
            .guard(i -> {
                throw new UnsupportedOperationException("crash!!!");
            })
            .onError(Throwable::printStackTrace);
            
    Integer crashingValue = Chain.let(0)
            .guard(integer -> {
                throw new UnsupportedOperationException("crash!!!");
            })
            .onErrorReturnItem(10)
            .call();
            
# Convert to RxJava stream or any other Object through flatMap()

We can convert the <b>Chain</b> to any Object or RxJava Stream through the <b>flatMap()</b> 

	List<Integer> numbersWithNulls = new ArrayList<>();
        numbersWithNulls.add(1);
        numbersWithNulls.add(null);
        numbersWithNulls.add(2);
        numbersWithNulls.add(null);
        numbersWithNulls.add(3);
        numbersWithNulls.add(4);
        numbersWithNulls.add(5);
    
	// list with null values
    Chain.let(numbersWithNulls)                            
			// remove null values :
            .apply(list -> list.remove(null))              
			// convert to RxJava 2 Observable :
            .flatMap(Observable::fromIterable)             
            .forEach(item -> Log.d("TAG", "not null item : " + item));
    
            
We can even chain multiple RxJava streams 

 	Observable.fromIterable(Arrays.asList(1, 2, 3, 4, 5, 6))
            .filter(i -> i % 2 == 0)
            .toList()
			// convert to Chain :
            .map(Chain::let)                   
            .blockingGet()
            .apply(list -> list.add(5))
			// start another stream :
            .flatMap(Observable::fromIterable) 
            .filter(i -> i % 2 != 0)
            .toList()
            .blockingGet();
            
# Check if an item is "in" a collection or not

We can pass a Collection so we can check if the current item stored in the CHain is available in the passed Collection or not, this is through invoking the <b>in()</b> function, this function returns a <b>Pair</b> Object, which holds the original item in it's <b>Pair.getValue0()</b>, and boolean indicating the result of the search in the <b>Pair.getValue1()</b>, if the item was found in the collection, this value will be true, else it will be false :

	List<Integer> evenNumbers = Arrays.asList(2, 4, 6, 8, 10);
    
	Chain.let(4)
            .in(evenNumbers)
            .apply(pair -> {
                if (pair.getValue1()) {
                    Log.d("TAG", pair.getValue0() + " is in the passed list");
                }
            })
			// convert the current Pair back to the original item :
            .map(Pair::getValue0)    
            .call();
            
# when() - then() operations :

For flow control, instead of the If/Else or Switch/Case blocks, we have two functions, <b>when()</b> and <b>then()</b>, the <b>when()</b> method takes a <b>Predicate</b> that returns either true or false, if it returned true, the <b>then()</b> method will be executed, else it will skip it's execution :

 	List<Integer> numbers = new ArrayList<>();
    numbers.add(1);
    numbers.add(2);

    Chain.let(numbers)
            .when(list -> list.contains(2))
			// will invoke this foperation :
            .then(list -> Log.d("TAG", "list contains value 2"))  
            .when(list -> list.contains(3))
			// will skip this operation :
            .then(list -> Log.d("TAG", "list contains value 3"))  
			// clear the list any way :
            .apply(List::clear);   
            
# Android example :

The entry point for the full API is through the <b>Chain</b> class, an example from an Android Activity will be as follows :

 	@Override
    protected void onCreate(Bundle savedInstanceState) {
        ...
        Chain.let(this)
                .apply(MainActivity::doSomething)
				// invoke this function in Debugging mode only :
                .debug(MainActivity::logSomethingDone)    
                .map(MainActivity::getClass)
                .map(Class::getName)
                .in(liveActivitiesNames)
                .when(Pair::getValue1)
                .then(pair -> Log.d(pair.getValue0(), "Activity is alive"));
    }
    
To set the Debugging behavior, you will need to invoke <b>ChainConfiguration.setDebugging()</b> in your Application's <b>onCreate()</b>, the debugging is disabled by default
    

# Gradle dependency

    Step 1. Add the JitPack repository to your build file, 
    Add it in your root build.gradle at the end of repositories:
    
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
    
    Step 2. Add the dependency
    
	dependencies {
		compile 'com.github.Ahmed-Adel-Ismail:J-Chain:0.0.7'
	}

                
