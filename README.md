[![](https://jitpack.io/v/Ahmed-Adel-Ismail/J-Chain.svg)](https://jitpack.io/#Ahmed-Adel-Ismail/J-Chain)

# J-Chain
A library that provides a set of functional patterns to enable chaining operations one after another, also helps not cutting RxJava2 streams

# Write Your Code the Kotlin way

Chain gives the ability to use some features of <b>Kotlin</b> in Java, like for example :

## Functional operators like Kotlin and RxJava :

Like Kotlin and RxJava, Chain holds some operators to change or update the item stored :
The <b>map()</b> operator, is similar to Kotlin's <b>let()</b>, and RxJava's <b>map()</b>
The <b>apply()</b> operatpr, is similar to Kotlin's <b>also()</b> and RxJava's <b>doOnNext()</b>
The <b>call()</b> operator, is similar to RxJava's <b>blockingGet()</b>
and much more, mentioned in the list of operators at the end of this file

## Lazy Initialization - "by Lazy" in Kotlin

Chain provides the <b>Lazy</b> class which provides lazy initialization like Kotlin, an android sample code (notice that this library is Java, not restricted to Android applications) :

```java
private final Lazy<TextView> textView = Lazy.defer(() -> findViewById(R.id.text_view));

@Override
protected void onResume(){
    super.onResume();
    textView.call()     //  textView will be initialized at this point
        .setText("on Resume");
}
```

Also the Lazy class provides new way for creating singletons, like for example :

```java
class MySingleton {
    public static final Lazy<MySingleton> instance = Lazy.defer(MySingleton::new);
    private MySingleton(){}
}

// now getting this instance will be as follows :
MySingleton.instance.call();

```

One step further is that you can invoke operations like <b>map()</b> and <b>apply</b>, which wont take effect unless the call method is invoked, like for example :

```java
Lazy<String> textLazy = Lazy.defer(() -> (TextView) findViewById(R.id.basic))
        .map(TextView::getText)
        .map(CharSequence::toString);
```

The above code snippet will find the TextView by Id, and get it's Text CharSequence, and convert it to String ... all these steps will be executed the first time <b>textLazy.call()</b> is invoked for the first time only

One more step further is that you can pass the Lazy type as methods parameters, so you can delay the code execution to the one who will invoke <b>Lazy.call()</b>, so we can add the following snippet to the above code :

```java
void requestFromServer(Lazy<String> textLazy){
    retrofitApi.request(textLazy.call());
}
```

now Imagine that if requestFromServer() was not invoked, all the steps required in the textLazy will not be even executed

## Optional & Null safety - "?", ":?" and "!!" in Kotlin

instead of handling null values with if/else blocks, Chain provides the Optional class, where you can hold on to an item, invoke some operations on it, and if it is null, nothing will happen, for example :

```java
Integer value = null;
Chain.optional(value)
    .map(integer -> integer + 1)                // add one to value
    .map(integer -> String.valueOf(integer))    // convert to String
    .map(string -> "Number is :" + string)      // append other string
    .apply(string -> System.out.println(string));   // print
```

In the above code, we had a set of operations that we wanted to execute on the passed value, if the passed value is not null, those operations will happen, but if it is null, nothing will happen ... this behavior is the same as using the "?" operator in Kotlin

But what if we want to take the value in this Optional so we can use it, so there are two options, the first is to invoke <b>callOrCrash()</b> ... which is the "!!" in Kotlin :

```java
Integer value = null;
String text = Chain.optional(value)
                .map(integer -> integer + 1)
                .map(integer -> String.valueOf(integer))
                .map(string -> "Number is :" + string)
                .apply(string -> System.out.println(string))
                .callOrCrash();
```

The second Option is to use <b>defaultIfEmpty()</b> ... which is the "?:" operator in Kotlin, this way the Optional instance will be converted to a Chain instance, that you can invoke it's <b>call()</b> method and retrieve the stored item :

```java
Integer value = null;
String text = Chain.optional(value)
                .map(integer -> integer + 1)
                .map(integer -> String.valueOf(integer))
                .map(string -> "Number is :" + string)
                .apply(string -> System.out.println(string))
                .defaultIfEmpty("Number is Null");
                .call();
```

As you can see, you do not have to care about null values any more, just write down your steps of execution, and the Optional will handle this for you

# How things work

The purpose of the <b>Chain</b> is to hold on to an item, and update it in a functional manner, where we can <b>apply</b> an action to the stored item, or we can <b>map</b> this item to another item, or we can even convert the current functions <b>Chain</b> into another Object or RxJava Stream through the <b>flatMap</b> function ... if we want the item stored after updating it, we can use the <b>call()</b> function to get the stored item ... unlike RxJava, any operation is done when it's function is called, you do not need to wait for the <b>call()</b> method to be invoked to execute the code ... more samples in the coming lines,and more details about the full APIs at the end of this file

# Manipulate data in a declarative way

    String finalValue = Chain.let(10)
		// apply an action : print value
		.apply(System.out::println)
		// map the item : convert to a String that holds the value multiplied by 10
		.map(i -> String.valueOf(i * 10))
		// apply action : print the new value
		.apply(System.out::println)
		// retrieve the item to be stored in the String variable
		.call();

# Handle optional values by making sure not to execute code if null

We have the <b>Chain.optional()</b> function, which accepts a value that can be null, and if the value is not null, it will invoke the <b>apply()</b> functions, you cannot exit the optional state unless you call <b>defaultIfEmpty()</b> :

    String finalNullableValue = Chain.optional(null)
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
            
# when\then operations :

For flow control, instead of the If/Else or Switch/Case blocks, we have two functions, <b>when()</b> and <b>then()</b>, the <b>when()</b> method takes a <b>Predicate</b> that returns either true or false, if it returned true, the <b>then()</b> method will be executed, else it will skip it's execution :

```java
List<Integer> numbers = new ArrayList<>();
numbers.add(1);
numbers.add(2);

Chain.let(numbers)
        .when(list -> list.contains(2)) // returns true
        .then(list -> Log.d("TAG", "list contains value 2"))  // this is invoked
        .when(list -> list.contains(3)) // returns false
        .then(list -> Log.d("TAG", "list contains value 3")) // this is skipped
        .apply(List::clear);
```

In the above example, the operations did not attempt to change the item stored in the Chain, so the <b>then()</b> operation can be skipped safely, but there are other operators like <b>thenMap(), thenTo()</b> which attempts to convert the item stored in the Chain, those operators will return an <b>Optional</b> instance which will hold the new value if the operation is invoked, or will return an empty <b>Optional</b> if the operation is skipped, so for example :

```java
Chain.let(2)
    .when(integer -> integer == 2)      // returns true
    .thenMap(integer -> integer * 10)   // returns an Optional that holds "20"
    .when(integer -> integer == 3)      // returns false
    .thenMap(integer -> integer * 100)  // will be skipped and returns an empty Optional
    .defaultIfEmpty(0)                  // converts the Optional into a Chain, if the Optional is empty, the Chain will hold 0 (which is our case here)
    .call();                            // retrieve the value
```

# Android Sample

The entry point for the full API is through the <b>Chain</b> class, an example from an Android Activity will be as follows :

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    ...
    Chain.let(this)
            .apply(MainActivity::doSomething)
            .debug(MainActivity::logSomethingDone)
            .map(MainActivity::getClass)
            .map(Class::getName)
            .whenIn(liveActivitiesNames)
            .then(name -> Log.d(name, "Activity is alive"));
}
```
    
To set the Debugging behavior, you will need to invoke <b>ChainConfiguration.setDebugging()</b> in your Application's <b>onCreate()</b>, the debugging is disabled by default
    
# Chain Types - Chain, Optional, Guard 

There are major Types in the Chain API, all are created as follows :

```java
Chain.let(Object) : start a Chain with a non-null value, this creates a Chain Object
Chain.optional(Object) : start a Chain that may hold a null value, this creates an Optional Object
Chain.call(Callable) : start a Chain that holds the result of Callable.call(), this creates a Chain Object
Guard.call(Callable) : start a Chain that holds the result of a Callable.call() that may crash, ths creates a Guard Object
Lazy.defer(Callable) : create a Lazy that will invoke the passed callable the first time it's call() or flatMap() methods are invoked
Lazy.defer(Function,parameter) : create a Lazy that will invoke the passed Function and a parameter to be passed to this function ... the first time it's call() or flatMap() methods are invoked
```

# Chain Types Operators

A summery of the Operations that can be invoked while using Chain Types, some operators may not be available in some types, based on the nature of each type

```java
apply(Consumer) : update the stored item through the passed function
lazyApply(Consumer) : update the stored item through the passed function but the update operation wont happen unless you invoke call() or flatMap()
map(Function) : convert the stored item into another item through a mapper function
lazyMap(Function) : convert the stored item into another item through a mapper function, the passed function wont be executed unless you invoke call() or flatMap()
flatMap(Function) :  convert the Chain itself to another Object through the passed function
to(Object) : convert the stored item to another item through passing this new item directly
to(Callable) : convert the stored item to another item through the result of the passed Callable
invoke(Action) : invoke a function that does not affect the stored item, this is intended for side-effects
guard(Consumer) : update the stored item through the passed function, it is safe for this function to crash at this point
guardMap(Function) : convert the stored item to another item through a mapper function, it is safe for this function to crash
onErrorReturnItem(Object) : return the passed item if an error occurred in the guard() or guardMap() operations
onErrorReturn(Function) : return the result of the function if an error occurred, the exception will be passed as a parameter to this function
onError(Consumer) : end the Chain by handling an error if occurred
onErrorMap(Function) : convert the stored item if the guard() or guardMap() operations failed, the mapper function will take the exception in its parameter
onErrorMap(Object) : convert the stored item if the guard() or guardMap() operations failed
defaultIfEmpty(Object) : update the stored item with the passed Object if the stored item is null
call() : retrieve the stored item
callOrCrash() : retrieve the stored item if not null, else it will throw NoSuchElementException
and(Object) : append an Object to the current Object in a list, and return a Collector to handle multiple items
collect(Class<?>) : if the current Chain holds a List of items, this method will create a Collector that holds those items in a List, if the Chain had one item, it will create a Collector that holds a List of items that holds only this item
log(Object) : start a Logger Object that is configured through ChainConfiguration class, with the passed parameter as the Log tag
debug(Consumer) : update the stored item through the passed function only in the debig mode - configured through ChainConfiguration class
pair(Object) : convert the stored item into a Pair of items, it's first value is the stored item, it's second value is the passed item
pair(Function) : convert the stored item into a Pair of items, it's first value is the stored item, it's second value is the result of the passed function (which takes the stored item as it's parameter)
when(Predicate) : the passed Predicate will take the stored item as it's parameter, and should return a boolean value, if the returned boolean is true, the next Conditional function will execute, else it will be ignored
whenNot(Predicate) : the passed Predicate will take the stored item as it's parameter, and should return a boolean value, if the returned boolean is false, the next Conditional function will execute, else it will be ignored
whenIn(Collection) : if the stored item is present in the passed Collection, the next Conditional function will execute, else it will be ignored
whenNotIn(Collection) : if the stored item is NOT present in the passed Collection, the next Conditional function will execute, else it will be ignored
whenEmpty() : if the stored item is null, the next Conditional function will execute, else it will be ignored
whenNotEmpty() : if the stored item is not null, the next Conditional function will execute, else it will be ignored
```

# Condition API

Created when one of when(), whenNot(), whenIn(), whenNotIn(), etc... is invoked

```java
then(Consumer) : update the stored item if invoked
thenMap(Function) : convert the stored item if invoked, and returns an Optional which either contains the item, or is empty (if the condition is not met)
thenTo(Object) : convert the stored item if invoked, and returns an Optional which either contains the item, or is empty (if the condition is not met)
thenTo(Callable) : convert the stored item through the Callable.call() result if invoked
```

# Collector API 

Created when collect() or and() methods is invoked

```java
and(Object) : append another item to the Collector
map(Function) : iterate over the items in the Collector and convert it to another item (can be of the same type)
reduce(BiFunction) : invoke the reduce() function on the items stored in the Collector
flatMap(Function) : converts the Collector to another Object
toList() : convert the Collector back to a Chain that holds List of items
```

# Logger API 

Created when the log() method is invoked

```java
error(Object) : print the passed Object as error
info(Object) : print the passed Object as info
exception(Throwable) : print the passed Throwable
message(Function) : compose a message from the stored item, then return a MessageLogger to log the composed Message
```

# MessageLogger API 

Created when the Logger.message() is invoked

```java
info() : print the composed message as info
error() : print the composed message as error
```
	
	
# Chain Configuration

A class responsible for the Chain API configuration, like debugging mode, and Logging behavior, a sample code from an Android Application class is as follows :

    @Override
    public void onCreate() {
        super.onCreate();

        ChainConfiguration.setDebugging(BuildConfig.DEBUG);
        ChainConfiguration.setLogging(BuildConfig.LOGGING);
        ChainConfiguration.setInfoLogger(this::infoLogger);
        ChainConfiguration.setErrorLogger(this::errorLogger);
        ChainConfiguration.setExceptionLogger(this::exceptionLogger);
    }

    private void infoLogger(Object tag, Object msg) {
        Log.i(tag.toString(), msg.toString());
    }

    private void errorLogger(Object tag, Object msg) {
        Log.e(tag.toString(), msg.toString());
    }

    private void exceptionLogger(Object tag, Throwable msg) {
        Log.e(tag.toString(), msg.getMessage());
    }



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
		compile 'com.github.Ahmed-Adel-Ismail:J-Chain:1.4.0'
	}

                
