The Gosu Programming Language
=============================

The Basics
==========

Variables and Type Declarations
-------------------------------

Gosu is statically typed, but uses type inference to eliminate the vast majority of syntax overhead usually involved with static typing.

Check it out:

var foo = "Foo" // a String
var one = 1     // a Number
var guess : String = null  // Must declare the type because it can't be inferred

Operators
---------

Gosu supports the standard Java operators, with a few minor restrictions and some great bonuses:

*   `++` / `--` - Just like the Java operators, except they cannot be used within another statement
*   `==` - Tests for object equality, just like `.equals()`
*   `===` - Tests for _instance_ equality
*   `<>` - The same as `!=`
*   `<`,`>`, etc - Standard comparison semantics which also works on `java.lang.Comparable` objects

Loops
-----

Gosu supports the standard loop variants: `for`, `while`, `do ... while` etc.

### The Gosu _for_ loop

The `for` loop in Gosu allows you to iterate over both arrays and anything that implements the `java.lang.Iterable` interface.

var list = { "one", "two", "three" } // Creates a java.lang.List<String>
for ( num in list ) {
print( num )
}

You can also get access to the zero-based index of the loop by including an `index` variable:

var list = { "one", "two", "three" }
for ( num in list index i ) {
print ( "${i} : ${num}" ) // i is an int, and num is still of type String
}

And, if you need access to the iterator for the loop (assuming you are looping over an `Iterable`) you can use the `iterator` keyword:

var list = { "one", "two", "three" }
for ( num in list iterator iter ) {
iter.remove()
}
print(list)

Here are a few examples making use of the Gosu range operator, `..`

for ( i in 0..5 ) {   // Range from 0 through 5
print ( i ) // Prints 0-5
}
print("---")
// Range from 0 up to 5
for ( i in 0..|5 ) {
print ( i ) // Prints 0-4
}
print("---")
// Range from 1 up to 5
for ( i in 0|..|5 ) {
print ( i ) // Prints 1-4
}

Properties
----------

Gosu _properties_ are a way to abstract field access in Gosu classes.

Consider this standard Java boilerplate:

public class Foo {
private String \_bar = "bar";
public void setBar( String value ) {
\_bar = value;
}
public String getBar() {
return \_bar;
}
}

This is a verbose way to simply expose a field, but exposing the field directly has it's own problems, which is where properties come in.

Here is the same class in Gosu:

public class Foo {
var \_bar : String as Bar = "bar" // the 'as Bar' exposes this field as the property Bar
}

If you want the property to be readonly, you can use the `readonly` modifier:

public class Foo {
var \_bar : String as readonly Bar = "bar"
}

If you want to add some logic to the get or set of a propery, you can use this longer syntax:

public class Foo {
var \_bar = "bar"

property get Bar() : String {
return \_bar
}
property set Bar( value : String ) {
if(value == "Foo") throw "That's not a valid value for Bar!"
\_bar = value
}
}

Reading and writing properties works just like accessing a field:

var f = new Foo()
f.Bar = "su"
print( f.Bar )

Null Safety
-----------

Gosu offers a few helpful tricks to deal with `null` in your code.

### Null Safe Method Invocation

Consider this code:

var aList = getAListOfStrings()
if(aList.get(0).isEmpty()) {
print("The first string is empty")
}

This code can cause a `NullPointerException` if either the list or the first string in the list is null. We can address this by using the null-safe invocation operator `?.`:

var aList = getAListOfStrings()
if(aList?.get(0)?.isEmpty()) {
print("The first string is empty")
}

The null safe invocation operator works on both methods and properties.

### The Elvis Operator

Sometimes you want a default value if an expression is null. For this use case, Gosu supports the Elvis Operator, `?:`:

var myString = getAPossiblyNullString() ?: "default"

If `getAPossiblyNullString` returns `null`, then the value `"default"` will be assigned to `myString`

Classes
-------

Gosu classes have a familiar syntax. Gosu classes are defined in a file ending with the `.gs` extension.

Here is a basic Gosu class:

package example

uses java.util.List

class SampleClass {
var \_names : List<String> // a private class var

// A public constructor
construct( names : List<String> ) {
\_names = names
}

// A public function
function printNames( prefix : String ) {
for( n in \_names ) {
print( prefix + n )
}
}

// A public property getter, making 'Names' a read-only property
property get Names() : List<String> {
return \_names
}
}

The above code demonstrates the following features:

*   The `uses` statement, which is identical to the `import` statement in Java, and makes a class (or package of classes) available for use without qualification.
*   A class variable, declared using the `var` keyword, just like local variables. Class variables default to private access. You can declare them to be `static` as well.
*   A class constructor, declared using the `construct` keyword. This constructor allows you to declare new instances of`SampleClass` like so:

    var c = new SampleClass({"joe", "john", "jack"})

    NOTE: Constructors default to public access.
*   A function, declared using the `function` keyword. This function takes a `String` argument, and returns no value, so no return type declaration is necessary. It can be invoked like so:

    var c = new SampleClass({"joe", "john", "jack"})
    c.printNames("\* ")

*   A property getter, declared using the `property` and `get` keywords. This property returns a list of strings. It can be invoked like so:

    var c = new SampleClass({"joe", "john", "jack"})
    print( c.Names )


### Named Arguments & Default Parameters

Gosu supports both named arguments and default parameter values, which can dramatically improve APIs.

Let's say you wanted to make the argument to `printNames()` in the class above optional, with a default value of `"> "`. You would change the parameter declaration to:

// A public function
function printNames( prefix : String = "> ") {
for( n in \_names ) {
print( prefix + n )
}
}

And you could now invoke it like so:

var c = new SampleClass({"joe", "john", "jack"})
c.printNames() // No argument is necessary, it will use the default value of "> "

Additionally, Gosu allows you to use a named argument syntax when you are working with non-overloaded methods on Gosu classes. You prefix the parameter name with a colon `:` like so:

var c = new SampleClass({"joe", "john", "jack"})
c.printNames(:prefix = "\* ")

Named arguments can be used to clarify code, so you don't end up with things like this:

someMethod(true, false, null, false, true)  //bwah?

someMethod( :enableLogging = true, :debug = false,
:contextObject = null, :trace = false,
:summarizeTiming = true)  //Oh, I see

### Superclasses, Interfaces and Delegates

Gosu classes can extend other classes and implement interfaces just like in Java, using the `extends` and `implements` keywords respectively.

One interesting additional feature of Gosu is the ability to delegate the implementation of an interface to a class variable using the `delegate` and `represents` keywords:

uses java.lang.Runnable

class MyRunnable implements Runnable {
//A delegate, exposed as the Impl property
delegate \_runnable represents Runnable

property get Impl : Runnable {
return \_runnable
}

property set Impl( r : Runnable ) {
\_runnable = r
}
}

Note that the class `MyRunnable` does not declare a `run()` method, as `Runnable` requires. Rather, it uses the delegate field `_runnable` to implement the interface:

var x = new MyRunnable()
x.Impl = new Runnable() {
function run() {
print("Hello, Delegation")
}
}
x.run() // prints "Hello, Delegation"

Delegates give you a convenient way to favor [composition over inheritance](http://www.artima.com/lejava/articles/designprinciples4.html).

Structure
---------



The Using Statement
-------------------

The `using` statement allows you to wrap sections of code that require connections to be closed to be handled automatically when the block finishes. Instead of writing code like this:

var conn = getConnection()
try {
conn.execute( "Some advanced SQL" )
} finally {
conn.close()
}

In Gosu, you can use the `using` statement which will handle closing the `Connection` for you:

using( var conn = getConnection() ) {
conn.execute( "Some advanced SQL" )
}

The `using` statement works with the following interfaces:

*   `java.io.Closeable`
*   `java.util.concurrent.locks.Lock`
*   `gw.lang.IReentrant`
*   `gw.lang.IDisposable`

Gosu Program Files
==================

Gosu doesn't have the concept of a `public static void main(String[] args)` entrypoint. Instead, it has programs, which are just bits of code in file ending a `.gsp` extension.

Here is a simple _Hello World_ application, in the file hello.gsp:

print ( "Hello World!" )

Running the program is simple:

$> gosu hello.gsp
Hello World!

Classpath Statements, Program Extends & Shebang
-----------------------------------------------

Gosu programs can embed a classpath in their source, obviating the need for users to pass in a correct classpath externally:

#! /path/to/gosu
classpath "../src,../lib/lib1.jar"

print( "Here is a library object: ${new SweetLibraryObject()}")

The classpath statement is comma delimited, to avoid system specific dependencies. Each path on it will be added to the classpath. If a path points at a folder and that folder contains jars, all those jars will be added to the classpath as well.

The classpath can also include Maven coordinates, and Gosu will automatically resolve and download them at runtime:

#! /path/to/gosu
classpath "../src,org.gosu-lang.gosu:sparkgs:0.1.0"

print( "Here is a library object: ${new SweetLibraryObject()}")


Gosu supports the Unix shebang standard, so your program can begin with `#! gosu` and Unix-like shells will execute the script with gosu. This makes it much more pleasant to run gosu programs:

$> ./my\_sweet\_gosu\_program.gsp
Here is a sweet library object: super.SweetLibraryObject@12b27c3

No wrapping scripts, no complicated class paths.

Finally, you can set the superclass for a program using the `extends` keyword:

classpath "org.gosu-lang.gosu:sparkgs:0.1.0"
extends sparkgs.SparkFile  // this is a sparkfile program
get('/', \\-> "Hello World")

This allows you to access methods and features in the parent class within your program, and can be used to create simple Gosu-based scripting tools.

Blocks
======

Blocks (also called closures or lambda expressions) are a simple way to specify an inline function. They have a lot of uses, but they really shine in data structure manipulation:

var lstOfStrings = {"This", "is", "a", "list"}
var longStrings = lstOfStrings.where( \\ s -> s.length > 2 )
print( longStrings.join(", ") )  // prints "This, list"

The `\ s -> s.length > 2` is the block. It declares an inline function that says "Given a String `s` return whether `s.length` is greater than two".

You can think of it as an inline version of this function:

function isLongerThanTwo( s : String ) : boolean {
return s.length > 2
}

Blocks allow you to express your logic much more succinctly.

Note that the blocks parameter, `s`, does not have a type annotation. Gosu does type inference here and figures out that `s` is a `String`.

With blocks you can dramatically reduce the amount of code you write when compared with Java.

Consider this complicated Java code:

List<String> lstOfStrings = Arrays.asList("This", "is", "a", "list");

List<String> longStrings = new ArrayList<String>();

for( String s : lstOfStrings ) {
if( s.length() > 2 ) {
longStrings.add( s.toUpperCase() );
}
}

Collections.sort(longStrings, new Comparator<String>() {
public int compare( String s, String s2 ) {
return s.compareTo( s2 );
}
})

StringBuilder sb = new StringBuilder();
for( String s : longStrings ) {
if(sb.length() != 0) {
sb.append(", ");
}
sb.append(s);
}

System.out.println(sb.toString());

This can be rewritten in Gosu as:

var lstOfStrings = {"This", "is", "a", "list"}
var longStrings = lstOfStrings.where( \\ s -> s.length > 2 )
.map( \\ s -> s.toUpperCase() )  // converts each string to upper case
.orderBy( \\ s -> s )            // there is a .order() method that could be used here instead
print( longStrings.join(", ") ) // prints "LIST, THIS"

The Gosu code is clearer and far more brief.

#### Blocks and Interfaces

Java has many interfaces that contain a single method, which are used as a stand-in for actual closures. In order to facilitate Java interoperability, Gosu blocks and one-method interfaces are automatically converted between one another:

var r : Runnable
r = \\-> print("This block was converted to a Runnable")

This makes some Java APIs much more pleasant to work with in Gosu.

Enhancements
============

Enhancements provide a way to add methods and properties to existing types. They are similar to [Extension Methods](http://msdn.microsoft.com/en-us/library/bb383977.aspx) in C#, but do not need to be explicitly imported.

Enhancements are defined in files ending with a .gsx suffix and cannot be defined inline with other Gosu resources. Here is an example enhancement for the type `java.lang.String`

package example

enhancement MyStringEnhancement : String {
function printWarning() {
print ( "WARNING: " + this );
}
}

In enhancements, the `this` symbol refers to the _enhanced type_, as opposed to the enhancement itself.

Once an enhancement has been added to your classpath, you can use it in any place you have an object of the enhanced type with no need to explicitly import the enhancement itself. Therefore, using the enhancement above is as simple as just calling the new function anywhere you have a String:

"I'm not sure I can go back to Java".printWarning()

Semantics And Limitations
-------------------------

The above code can be thought of as shorthand for this code:

example.MyStringEnhancement.print( "Warning: " + "Hello World" )

Enhancements are _statically_ dispatched. This means they cannot be used to implement interfaces or to achieve polymorphism

Generics
--------

Enhancements can be generic, so you can add an enhancement to `List<T>`:

package example

uses java.util.List

enhancement MyListEnhancement<T> : List<T> {
function firstAndLast() : List<T> {
return {this.first(), this.last()}
}
}

This method will now be available on all generic lists, and will be properly typed.

### Type Variable Reification

Unlike in Java, type variables _can_ be used in general expressions in Gosu. In Enhancements, the type variables are statically, rather than dynamically, reified, much like enhancement methods are statically, rather than dynamically dispatched. The enhancement method `toTypedArray():T[]` on `Iterable<T>` demonstrates this:

var lstOfStrings : List<String> = {"a", "b", "c"}
var arrOfStrings = lstOfStrings.toTypedArray() //returns a String\[\]

var lstOfObjs : List<Object> = lstOfStrings //type variables are covariant in Gosu, see [generics](generics.html)
var arrOfObjs = lstOfObjs.toTypedArray() //returns an Object\[\]

This "best effort" reification usually does what you want, but can occasionally lead to surprising results.

### Enhancing Parameterized Types

A really neat trick with enhancements is that you can enhance _parameterized_ types:

package example

uses java.util.\*

enhancement MyListOfDatesEnhancement : List<Date> {
function allBetween( start : Date, end : Date ) : List<Date>{
this.where( \\ d -> start <= d and d <= end )
}
}

This is how all lists of comparable objects have the `sort()` method on them, while other lists do not.

Strings & Gosu Templates
========================

String literals in Gosu can be expressed using either double or single quotes:

var s1 = "I'm a String"
var s2 = 'I\\'m also a String!'

Strings support concatenation:

var s1 = "Hello"
var s2 = "World!"
print ( s1 + " " + s2 )  // prints "Hello World!"

Strings also support inline expressions using the `${}` syntax:

var s1 = "Hello"
var s2 = "World!"
print ( "${s1} ${s2}" )  // prints "Hello World!"

Because Strings are so common, there are also a bunch of handy _enhancements_ which allows for easy conversion from strings to other types:

var bool = "true".toBoolean()
var integ = "42".toInt()
var dubble = "42.2".toDouble()
var date = "01/25/2012".toDate()

Here is a short sample of additional enhancements on `String`:

*   `repeat(n:int)` - Repeat the String `n` times
*   `chomp()` - Removes a trailing newline from the end of the String, if present
*   `chop()` - Remove the last character from the String
*   `elide(len:int)` - Cap the String at a fixed length and replace the last three characters with '...' to denote truncation
*   `rightPad(w:int)`, `leftPad(w:int)`, `center(w:int)` - Format the string with additional whitespace
*   `notBlank()` - Returns true if the string is not null and contains at least one non-whitespace character

Gosu Template Files (.gst)
--------------------------

Gosu supports string templates as first class citizens in the language. A Gosu String Template is a file that ends in the `.gst` extension.

Here is an example definition, `sample.SampleTemplate.gst`:

<%@ params( names : String\[\] ) %>
All Names: <% for( name in names ) { %>
\* ${name}
<% } %>

The template explicitly declares the names and types of its arguments using the `params()` directive

You can render a template by calling the `render(w:Writer)` or `renderToString()` static methods:

For each parameter defined in the `params` directive, an additional argument with that name and type is added to the `render()` and `renderToString()` methods.

So, given the template definition above, you could render it like so:

// render directly to string
var str = sample.SampleTemplate.renderToString( {"Joe", "John", "Josh"} )
print( str)

// render directly to writer (potentially more efficient for large strings)
var writer = new java.io.StringWriter()
sample.SampleTemplate.render(writer, {"Joe", "John", "Josh"})
print( writer )

Using templates gives you a type safe way to generate large strings in your applications.

Collections In Gosu
===================

List & Map Syntax
-----------------

`java.util.List` and `java.util.Map` are the two most commonly used data structures in Java. Unfortunately, they can also be fairly verbose to deal with in Java:

Map<String, Object> map = new HashMap<String, Object>();
map.put( "isOverlyVerbose", true );
List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
list.add( map );

Luckily Gosu provides a shorthand syntax for these two types, allowing the above code to simply be written as:

var map = { "isOverlyVerbose" -> false }
var list = { map }

Enhancements
------------

Gosu adds a whole slew of enhancements to collections classes. Here are some of the most useful ones for `java.lang.Iterable<T>`:

Enhancement

Description

allMatch( cond(elt1 : T):boolean ) : boolean

Returns true if all elements in this collection match the given condition and false otherwise

average( select:block(elt:T):java.lang.Number ) : BigDecimal

Return the average of the mapped value

concat( that : Collection<T> ) : Collection<T>

Return a new list that is the concatenation of the two lists

Count() : int

Return the number of elements in this Iterable object

countWhere( cond(elt:T):boolean ) : int

Return the count of elements in this collection that match the given condition

disjunction( that : Collection<T> ) : Set<T>

Returns a the set disjunction of this collection and the other collection, that is, all elements that are in one collection _not_ and not the other

each( operation(elt : T) )

This method will invoke the operation on each element in the Collection

eachWithIndex( operation(elt : T, index : int ) )

This method will invoke the operation on each element in the Collection, passing in the index as well as the element

first() : T

Returns the first element in this collection. If the collection is empty, null is returned

firstWhere( cond(elt:T):boolean ) : T

Returns the first element in this collection that matches the given condition. If no element matches the criteria, null is returned

fold( aggregator(elt1 : T, elt2 : T):T ) : T

Returns all the values of this collection folded into a single value

hasMatch( cond(elt1 : T):boolean ) : boolean

Returns true if any elements in this collection match the given condition and false otherwise

intersect( that : Collection<T> ) : Set<T>

Return the set intersection of these two collections

join( delimiter : String ) : String

Coerces each element in the collecion to a string and joins them together with the given delimiter

last() : T

Returns the last element in this collection. If the collection is empty, null is returned

lastWhere( cond(elt:T):boolean ) : T

Returns the last element in this collection that matches the given condition. If the collection is empty, null is returned

map<Q>( mapper(elt : T):Q ) : List<Q>

Maps the values of the collection to a list of values by calling the mapper block on each element

maxBy( comparison(elt : T):Comparable ) : T

Returns the maximum value of this collection with respect to the Comparable attribute calculated by the given block. If more than one element has the maximum value, the first element encountered is returned

max<R extends Comparable>( transform(elt:T):R ) : R

Returns the maximum value of the transformed elements

minBy( comparison(elt : T):Comparable ) : T

Returns the minimum value of this collection with respect to the Comparable attribute calculated by the given block. If more than one element has the minimum value, the first element encountered is returned

min<R extends Comparable>( transform(elt:T):R ) : R

Returns the minimum value of the transformed elements

partitionUniquely<Q>( mapper(elt : T):Q ) : Map<Q, T>

Partitions each element into a Map where the keys are the value produce by the mapper block and the values are the elements of the Collection. If two elements map to the same key an IllegalStateException is thrown

orderBy<R extends Comparable>( value(elt:T):R ) : IOrderedList<T>

Returns a lazily-computed List that consists of the elements of this Collection, ordered by the value mapped to by the given block

orderByDescending<R extends Comparable>( value(elt:T):R ) : IOrderedList<T>

Returns a lazily-computed List that consists of the elements of this Collection, descendingly ordered by the value mapped to by the given block

reduce<V>( init : V, aggregator(val : V, elt2 : T):V ) : V

Returns all the values of this collection down to a single value

removeWhere( cond(elt:T):boolean )

Removes all elements that match the given condition in this collection

retainWhere( cond(elt:T):boolean )

Retains all elements that match the given condition in this collection

reverse() : List<T>

Returns a new list of the elements in the collection, in their reverse iteration order

single() : T

Returns a single element from this iterable, if only one exists. It no elements are in this iterable, or if there are more than one elements in it, an IllegalStateException is thrown

singleWhere( cond(elt:T):boolean ) : T

Returns a single item matching the given condition. If there is no such element or if multiple elements match the condition, and IllegalStateException is thrown

subtract( that : Collection<T> ) : Set<T>

Returns the Set subtraction of that Collection from this Collection

toCollection() : Collection<T>

If this Iterable is already a Collection, return this Itearble cast to a Collection. Otherwise create a new Collection and copy this Iterable into it

toList() : List<T>

If this Iterable is already a List, return this Iterable cast to a List. Otherwise create a new List and copy this Iterable into it

toSet() : Set<T>

If this Iterable is already a Set, return this Iterable cast to a Set. Otherwise create a new Set based on this Iterable

toTypedArray() : T\[\]

Returns a strongly-typed array of this Iterable, as opposed to the argumentless Iterable#toArray(), which returns an Object array. This method takes advantage of static reification and, therefore, does not necessarily return an array that matches the theoretical runtime type of the Iterable, if actual reification were supported

union( that : Collection<T> ) : Set<T>

Returns the set union of the two collections

where( cond(elt:T): boolean ) : List<T>

Returns all the elements of this collection for which the given condition is true

whereTypeIs<R>( type : Type<R> ) : List<R>

Returns all the elements of this collection that are assignable to the given type

zip<R>( other : Iterable<R>) : List<Pair<T,R>>

Returns a list of `gw.util.Pair`s of elements from matching indices of `this` and the `other` Iterables. If one Iterable contains more elements than the other then only return a list of the same length as the shortest of the two Iterables.

Enjoy!
======

That's a good overview of what the Gosu language provides for you. Please give it a try and, if you have any questions, hit us up on the [Newsgroup](https://groups.google.com/group/gosu-lang)!

* * *