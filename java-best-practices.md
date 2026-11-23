CodeGen Software ProcessJava Coding Best PracticesVersion 3.0
1. Language and Package Best Practices................................................................ 41.1 String usage................................................................................................................................ 41.1.1 String creation......................................................................................................................41.1.2 String concatenation...........................................................................................................41.1.3 Empty String comparison...................................................................................................51.1.4 charAt() over substring().................................................................................................... 51.2 Boxing and Unboxing...............................................................................................................61.3 Instanceof operator...................................................................................................................61.4 Implementing compareTo().....................................................................................................61.5 ArrayList<> over Vectors<>.................................................................................................... 72. Programming Best Practices.................................................................................. 72.1 Proper access modifiers..........................................................................................................72.2 Collection cloning......................................................................................................................72.3 Do use try…catch…finally {try…catch}...............................................................................82.4 Move commonly used logic to framework method.......................................................... 82.5 Pre-sizing ArrayLists and Vectors........................................................................................ 92.6 Avoid re-initialization of instance variables to default.................................................... 92.7 Avoid unnecessary repeated method calls........................................................................ 92.8 Minimize the number of castings........................................................................................ 102.9 Move common logic out of the conditions.......................................................................102.10 Eliminating unnecessary conditions to assign boolean values.................................112.11 Debugger logging (Conditional Compilation)..................................................................113. Code Readability and Maintenance Best Practices..........................................123.1 Comments................................................................................................................................. 123.1.1 Header Comments............................................................................................................133.1.1.1 Package header comments....................................................................................133.1.1.2 Class and Interface header comments.................................................................133.1.1.3 Field comments........................................................................................................ 143.1.1.4 Method comments....................................................................................................143.1.1.5 Documenting local variables.................................................................................. 153.1.1.6 Do not put comments unnecessarily.....................................................................153.1.2 Proper Indentation and Paragraphing........................................................................... 153.1.2.1 Wrapping Lines.........................................................................................................163.1.3 Use of Accessors.............................................................................................................. 163.1.3.1 Accessor Naming Convention................................................................................163.1.3.2 Non-serializable properties.....................................................................................163.1.4 Declarations.......................................................................................................................173.1.4.1 Local Variables......................................................................................................... 173.1.4.2 Statements................................................................................................................ 174. Swing GUI Coding Best Practices....................................................................... 184.1 General....................................................................................................................................... 184.1.1 Naming Convention.......................................................................................................... 184.1.2 Long Methods....................................................................................................................184.1.3 Unnecessary Fields.......................................................................................................... 184.2 Design Pattern..........................................................................................................................184.3 Class content............................................................................................................................204.4 Look and Feel Guidelines......................................................................................................204.4.1 General Guidelines...........................................................................................................204.4.2 Menus................................................................................................................................. 214.4.2.1 Composition and Construction of Items................................................................214.4.2.2 Conventional key board short cuts used for menus........................................... 224.4.2.3 Conventional mnemonics used for menus...........................................................234.4.3 Capitalization..................................................................................................................... 234.5 GUI Thread.................................................................................................................................244.6 Time Consuming Tasks......................................................................................................... 24
4.7 Data Grids..................................................................................................................................244.8 Data Mappers............................................................................................................................25Revision
Date Version Author Note
31-Oct-06 1.0 Rasika Sherman Created
08-Jan-07 1.1 Dasun Athukorala Reviewed
09-Jan-08 1.2 Dasun Athukorala Second Revision
17-May-07 1.3 Jagath Samarawickrama Added swing practices18-May-07 1.4 Rasika Sherman Reviewed
05-July-07 1.5 Jagath Samarawickrama Reviewed by
Dhammika
1. Language and Package Best Practices
1.1 String usage
1.1.1 String creation
When creating String type objects and assigning values method 1 has better performance over
method 2. Method 1
String s = "This is test string";
Method 2
String s = new String("This is test string");
First method will re-use the same string literal from the heap when we assign similar string valueagain, But in the second method even though the value is same it will create new object. Ex: it.codegen.tbx.crschecker.CRSAirline
CRSAirline CRSAirline = new CRSAirline( new String( this.name ),
new String( this.bookingClass ), this.noOfSeats );
CRSAirline CRSAirline = new CRSAirline( this.name, this.bookingClass,this.noOfSeats );1.1.2 String concatenation
If the string concatenation can be resolved at compile time use method 1, but if it is not (i.e.
involvement of variables in the construction of the string), use StringBuffer class and construct byappending. Method 1
String s = “SELECT fm_cabin_class.priority, " +
“z.fare_type " +
“FROM fm_cabin_class ”;
This will anyway concatenated to single string at the compile time, so no need to use
StringBuffers. Also if the string is built in a single assignment statement as follows, it is not
required to use StringBuffer since it will be done automatically by the compiler. String qsRouteFromAirport = "SELECT *, " + mainAirlineQuery + ", " +
owAllowedQuery + " FROM fm_route WHERE
cont_group_id = ? AND route_group_no = ? AND
route_no = ? AND EXISTS (SELECT airport from
fm_group_airports WHERE group_id = dep_group ANDairport = ?) " ;
Ex: it.codegen.tbx.packagebuilder.service.AccomBoadrBasisScheme
StringBuffer sbAccomBoardBasisWeights = new StringBuffer( "SELECT *
FROM PKG_ACCOM_BOARD_BASIS_WEIGHT WHERE " );
sbAccomBoardBasisWeights.append( "PACKAGE_ID = ? AND " );
sbAccomBoardBasisWeights.append( "ITINERARY_NO = ? AND " );
sbAccomBoardBasisWeights.append( "ITINERARY_ITEM_NO = ? AND " );
sbAccomBoardBasisWeights.append( "BOARDBASIS_NO = ? " );
Use String concatenation (+) instead of StringBuilder, since all are string literals. String sbAccomBoardBasisWeights = "SELECT * FROM
PKG_ACCOM_BOARD_BASIS_WEIGHT WHERE " +
"PACKAGE_ID = ? AND " +
"ITINERARY_NO = ? AND " +
"ITINERARY_ITEM_NO = ? AND " +
"BOARDBASIS_NO = ? ";
Note: Might have to replace all generated classes for SELECT statements. 1.1.3 Empty String comparison
When it is required to test string for emptiness use method 1 over method 2
Method 1
if (s1.length()==0) … Method 2
if (s1.equals("")) …
ex: it.codegen.tbx.faresmanager.FlightTax
if (this.airline.equals(""))
if (this.airline.length()==0)
1.1.4 charAt() over substring()
If the purpose is to get single character use charAt instead of substring(). Ex: package it.codegen.tbx.accommodationsearching.service.Contract
prorataCost = prorataString.substring( 0, 1 ).equals( "t" );
prorataCost = prorataString.charAt( 0) = ‘t’;
1.2 Boxing and Unboxing
Avoid boxing and unboxing whenever possible, instead try to use primitive types whenever
possible. Ex: package it.codegen.tbx.accommodationsearching.service.Contract
public Integer _getSeasonOfDay( CGDate theDay )
Instead of returning reference type instance, return int type, to avoid unnecessary boxing. 1.3 Instanceof operator
Avoid instanceof operator whenever possible, in most cases logic could be written without usingthe instanceof conditions by overloading methods. Ex: package it.codegen.tbx.accountsmanager.DefaultPeriodicExchRate
public int compareTo(Object o)
{
if( !(o instanceof DefaultPeriodicExchRate) ) return 0;
if( ((DefaultPeriodicExchRate)o).getFromDate().equals( this.fromDate ) ) return 0;
if( ((DefaultPeriodicExchRate)o).getFromDate().after( this.fromDate ) ) return -1;
return 1;
}
This unnecessary instanceof validation and casting could have been avoided by implementing
Comparable interface with the correct type parameter.
Instead of implementing Comparable, Comprable< DefaultPeriodicExchRate> could have beenused and then the compareTo() method would be as follows. public int compareTo( DefaultPeriodicExchRate o )
{…. 1.4 Implementing compareTo()
As discussed in previous section pass correct type parameter when implementing the
Comparable interface. This will ensure the type safety, avoid unnecessary instanceof checks andcasting
Ex: package it.codegen.tbx.SeasonalFare
public class SeasonalFare implements Comparable,Cloneable
public class SeasonalFare implements Comparable<SeasonalFare>,Cloneable
1.5 ArrayList<> over Vectors<>
Whenever possible use ArrayList over Vector. If there is no specific need of synchronized threadsafe access to elements in the collection, always use ArrayList
2. Programming Best Practices
2.1 Proper access modifiers
Always make sure to use proper access modifiers with all member variables and functions. Try toavoid package level access rights. (i.e. if no modifier defined it has package level access rights). Try to minimize public and protected modifiers unless it is required (don’t put as public as a
practice), having public functions introduce high coupling and dependency. 2.2 Collection cloning
When there is a requirement to get a copy of a collection try to avoid looping through each itemand adding, always use functions given by the collection for bulk operations.
Instead of lopping through Vector or ArrayList elements and add each element to the newcollection, use clone() function. Note: This should be used only when the copy of collection is required not the copy of each
element in the collection. Ex: package it.codegen.tbx.flightsearch.Airline
Vector<String> newGds = new Vector<String>();
if( gdsList != null )
{
for( String gds : gdsList )
{
newGds.addElement( gds );
}
}
Vector<String> newGds = (Vector<String>) gdsList.clone();
Note: However if it is required to add one by one (cloning the item itself), then initialize the
second collection with the required size.
2.3 Do use try…catch…finally {try…catch}
As a best practice use the try…catch…finally block and resource releases should be in the finallyblock. However most of resource release calls can themselves throws exceptions, so it is very
common to have anther try…catch inside the finally block to handle these exceptions. Do not have multiple places to release the same resource based on the execution path. However if there’s no resource cleanup required then you can omit the finally block. Ex: package it.codegen.tbx.flightsearch.service.RouteChecker
In most functions connection close() will appear everywhere inside the function. Best practice is
to move it inside the finally block. finally
{
try
{
if( con != null && !con.isClosed() )
{
con.close();
}
}
catch (SQLException se)
{
// Do logging…
}
}
2.4 Move commonly used logic to framework method
There are instances where some logic is used very often, instead of repeating it each place
required move it to framework/utility method. Ex: before closing database connection it is checked for nullity and the status of the connection. if( con != null && !con.isClosed() )
{
con.close();
}
So have a class and static method to take care of this… by this we can decouple core technical
services logic from the business logic. public static void closeConnection(Connection con)
{
try
{
if( con != null && !con.isClosed() )
{
con.close();
}
}
catch (SQLException se )
{
se.printStackTrace();
// if needed bubble up the exception
throw new SearchingException( "ConnectionError: " +
se.getMessage() );
}
}
Now we can close database connection using
ResourceCleaner.closeConnection(con);
2.5 Pre-sizing ArrayLists and Vectors
If the size of the elements to be stored in ArrayList or the Vector is known give the capacity whenit is initialized. This will greatly increase performance since reallocation of internal storage for
reference is not needed when the number of items increased. ArrayList<String> list = new ArrayList<String>(ARRAY_SIZE);
It’s always better to allocate slightly more slots than allocation less than the amount required. 2.6 Avoid re-initialization of instance variables to default
Do not attempt to initialize instance variables to their default values.  All objects are initialized to null by default, so it is not necessary to initialize themwith null.  All integer types (byte, char, short, int, long) are initialized to 0 by default, so it is not
necessary to initialize them with 0.  All float types (float and double) are initialized to 0.0 by default, so it is not necessary toinitialize them with 0.0.  All booleans are initialized to false by default. Note: However all local variables inside a function should be initialized. Ex: package it.codegen.tbx.accommodationsearching.DailyRoomGroupAvailability
public DailyRoomGroupAvailability()
{
allots = 0;
reserved = 0;
closeOuts = 0;
}
2.7 Avoid unnecessary repeated method calls
One of the commonly used call is, calling the size() method of a collection inside a loop. If the
number of items of the collection is fairly large it is always better to cache the size before enteringinto the loop and use that cached value for the loop terminating condition. Ex:
for ( int i = 0; i < allocationUnits.size(); i++ )
{
int size = allocationUnits.size();
for ( int i = 0; i < size; i++ )
{
This is not only common for collections but for any repeated method call this could be
implemented. 2.8 Minimize the number of castings
If a reference to a given object is required many times inside a method and it is comes in as Basetype or out of the collection cast once and use correctly typed reference afterwards. 2.9 Move common logic out of the conditions
In most cases If logic and Else logic does the same with slight differences, for ex: accessing
different elements of a collection, in such cases instead of duplicating same logic in both clauses
better to store the value of change in local variable and take the common logic out of the
condition. Ex: package it.codegen.tbx.flightsearch.service.RouteChecker
if( ( (Sector) outLeg.getSectors().get( 0 ) ).isAddon() )
{
airportOutFrom = (Airport) ( (Sector)
outLeg.getSectors().get( 1) ).getFromAirports().getAirports().get( 0 );
airportOutTo = (Airport) ( (Sector)
outLeg.getSectors().get( 1) ).getToAirports().getAirports().get( 0 );
}
else
{
airportOutFrom = (Airport) ( (Sector)
outLeg.getSectors().get( 0 ) ).getFromAirports().getAirports().get( 0 );
airportOutTo = (Airport) ( (Sector)
outLeg.getSectors().get( 0 ) ).getToAirports().getAirports().get( 0 );
}
Could be changed to
Sector outLegFirstSector = (Sector) outLeg.getSectors().get( 0 );
if (!outLegFirstSector.isAddon())
{
outLegFirstSector = (Sector) outLeg.getSectors().get( 1 );
}
airportOutFrom =
(Airport)outLegFirstSector.getFromAirports().getAirports().get(0);
airportOutTo =
(Airport)outLegFirstSector.getToAirports().getAirports().get(0);
2.10Eliminating unnecessary conditions to assign booleanvalues
Some cases if.. else.. condition is used only to assign or return true or false, in such cases it is
not required to use the condition but simply the logical expression could be assigned or returned. Ex: package it.codegen.tbx.flightsearch.service.RouteChecker
boolean openjawAtOrigin = false;
boolean openjawAtDes = false;
if( !outFrom.getCity().equals( inTo.getCity() ) )
{
openjawAtOrigin = true;
}
if( !outTo.getCity().equals( inFrom.getCity() ) )
{
openjawAtDes = true;
}
Could be written as
boolean openjawAtOrigin = (!outFrom.getCity().equals(inTo.getCity()));boolean openjawAtDes = (!outTo.getCity().equals(inFrom.getCity()));2.11Debugger logging (Conditional Compilation)
It is always required to put debugging information to the code in order to track the execution of
the code and the accuracy of processed data. However, not as most languages in Microsoft
platform Java doesn’t support conditional compilation so that we can wrap debugging information. As a workaround for that we can use a separate class in each module and have any number of
final static boolean variables to control the code sections. Advantage of this is once we set those flags to false these code segments will not be emitted tofinal Java byte code, so that no performance or memory footprint issues. But the same time wecan keep the debugging information in the code. Ex:
Define a class as follows in a module, ( we can use one class for several modules or a could
include this class in common module, but then the code base has to be included in the project of
that common module)
public class COMPILE_CONDITION
{
public static final boolean DEBUG_LOGGING = true;
}
So whenever it is required to log debug info we can wrap it inside as follows. ...
if (COMPILE_CONDITION.DEBUG_LOGGING)
{
LOGGER.log( Level.INFO,”Log Message”};
}
... Before committing the COMPILE_CONDITION class into the CVS it always should be false
Here for COMPILE_CONDITION different naming convention used since it is not logical part of thebusiness module, but for internal usage of development team, and also it is easier to identify
sections not part of the business logic easily. NOTE: In the production version never use System.out.println() for logging. Also don’t use stringconcatenation for logging message.
Instead of
LOGGER.log( Level.INFO ," >>> COORDINATOR:ALL SEARCH
COMPLETE:"+this.toString()+" >>>"+allResultsFound+"<<<
COORDINATOR:"+this.toString()+" <<<" );
Use
LOGGER.log( Level.INFO,">>> COORDINATOR:ALL SEARCH COMPLETE:{0}
>>>{1}<<< COORDINATOR:{2} <<<",
new Object[]{this.toString(),allResultsFound,
this.toString()});
If there’s just one parameter to be printed, use as follows
LOGGER.log( Level.INFO, "Mapper vaue: {0}", mapper.getValue());
3. Code Readability and Maintenance Best PracticesGood coding practices always improve the readability, productivity, understandability and easethe maintainability. In order to understand what each segment of code does, it should be clearly
written with required amount of comments explaining logic. 3.1 Comments
Comments in a source file could be any of the following, Header comments: which describes the purpose of particular class, interface, or function.
Code comments: which gives the understanding of business logic, special algorithms used
inside the body of the function.
Instructional comments: Used to indicate another developer special instructions regarding thecode, such as improvement points of the code. Good documentation of code always has following properties.  Always have simple comments, should not need unnecessary information, paragraphs, do not try to explain everything in single block and write the entire logic thereafter, explanation should walk with the code statements throughout the function explaining whyeach statement is there.  Explain why it is done, not what it is done. By going through the code someone can
understand what particular section is doing, but it is required to tell the reader why it is
done in the context of business domain. 3.1.1 Header Comments
3.1.1.1 Package header comments
Each package should have a documentation explaining the purpose of it, and if required versioninformation. To support the javadoc tool with package documentation, each package folder should have a filewith “package.html” name and it should have following format. <HTML>
<BODY>
Provides the classes necessary to test the performance of different
usages
<p>
@since 1.0
</BODY>
</HTML>
Documentation tags
Tag Description
@version Version of the package. @see See also references or descriptions, which could be used to refer the relevant
other packages. 3.1.1.2 Class and Interface header comments
Every class, interface should have class header comment section to describe the purpose of theclass. Defined javadoc tags should be used in the header comments. Use doc comments together with tags defined below for class headers
/**
* Class summary sentence.
* Detailed description of the class and its purpose in the context
*
* @author name of the developer
*
* @version version, date
*
* @see see also
*
*/
Note: First sentence of the class header should be the summary of the class and should be
ended with period “.”. This is the line taken for Class Summary secition of the API documentation. Documentation tags
Tag Description
@author Name of the developer who created this class or who modified it, if there aremany developers for a single file each should appear one after another withthe separate @author tag. This tag is a must
@version Version of the class, in the format of version number and date modified to
new version. @see See also references, this could be either reference to another class of samepackage or class of a different package, if it is a class of different package
need to specify the absolute class name. @see “Refer java documentation”
@see com.codegen.performance.framework.Testable
@since Specify the product version when the this class/interface was added to the API
specification. 3.1.1.3 Field comments
It is always good practice to give an explanation on each fields used in the class, this will
definitely help to understand the class as a whole for a new developer other than the initial author. However it is not necessary to put a doc comment (i.e. /** … */) to all fields in a class unless it
has a public, protected or package level access. However having end-of-line comments (i.e. // …)
associate with fields is a good practice. /**
* List used to keep the test integer elements during performance test*/
protected ArrayList<Integer> testElements;
3.1.1.4 Method comments
Use the following way for the method comments. /**
* Summary sentence of the method.
* Any detailed description explaining the purpose of the method and
* what it does.
*
* @param param1 describe parameters
* @return describe what this method returns
* @see Any see also references
*/
Documentation tags
Tag Description
@param These tags will be put by the IDE once you start documentation tags for a
given method. This tag describes each parameters used
@param parameter-name description
@return Describes the return data of a method, this is not required for methods whichreturn void and for constructors. It’s always best practice to define the rangeof values that could be returned in exceptional cases. @return description
@see See also references, this could be either reference to another class/methodof
same package or class/method of a different package, if it is a class of
different package need to specify the absolute class name. @see “Refer java documentation”
@see com.codegen.performance.framework.Testable
3.1.1.5 Documenting local variables
Use end of line comment (//) to document local variables. 3.1.1.6 Do not put comments unnecessarily
It is always better if your code itself can explain what it stands for, instead of having bunch of
comments. For that always used meaningful variable names. Consider following. Following comment is worthless, since no need to explain what it does. i++; // Increment i
Following comment is better, but do we need a comment?
i++; // Increment the char counter Following needs no comment, which gives the clean code but at the same time required
understanding to the reader. charCounter++;
3.1.2 Proper Indentation and Paragraphing
Proper indentation always helps to produce clean code with good readability. Indentation helps togive the clear picture of level of depth and surrounding loops/conditional blocks. Paragraphing will group the related logic into sections. Instead of writing entire logical statementsone after another, separate them with blanks lines.
Indentation and proper spacing could be done by reformatting the code by pressing [Ctrl]+[Alt]+Lin IDEA. Coding style has been defined in the xml file
ftp://192.168.0.13/IDEA/codestyles/codegen.xml
Declaration, initialization and rest of the logic should be properly separated into paragraphs. 3.1.2.1 Wrapping Lines
When an expression is not fit on a single line, break it according to these general principles:  Break after a comma
function(longExpression1, longExpression2, longExpression3,
longExpression4, longExpression5);
 Break after an operator
int daysDifference = (deptDate._get( Calendar.DAY_OF_YEAR ) -
bookingDate._get( Calendar.DAY_OF_YEAR ));
 Prefer higher level breaks to lower-level breaks
longName1 = longName2 * (longName3 + longName4 - longName5) +
4 * longname6; // PREFER
longName1 = longName2 * (longName3 + longName4 -
longName5) + 4 * longname6; // AVOID
 Align the new line with the beginning of the expression at the same level on the previous
line. Refer all previous examples. 3.1.3 Use of Accessors
Accessors should be defined for all necessary fields (which are supposed to be accessed by
outside) by encapsulating the access to them via getters and setters. Usage of accessors give
the benefits of  single point update, (ease of testing and debugging)  control of presenting the attribute to outside
3.1.3.1 Accessor Naming Convention
Getters should have ‘get’ prefix and setters should have ‘set’ prefix, however for boolean type
member fields there’s an exception, and in such case it should be with either ‘is’, ‘has’ prefix. 3.1.3.2 Non-serializable properties
If any field value that should not be serialized by WASP should be prefixed with the underscorecharacter ‘_’ public int _getNextItemNo( String productCode )
3.1.4 Declarations
3.1.4.1 Local Variables
Only one declaration per line. int level; // indentation level
int size; // size of table
int level, size // not recommended
int index, elements[]; // unacceptable 3.1.4.2 Statements
Each line should contain at most one statement, except in cases like for loop declaration. currentCoordinator++; currentMapper++; // Should be in two lines
4. Swing GUI Coding Best Practices
When developing GUI, best coding practices are essential to improve the quality and the
performance of the software. There are some GUI components have been developed for in houseuse, but the developers need clear understanding of their practical usages and implementations. 4.1 General
4.1.1 Naming Convention
Each swing component variable name should be suffixed by the component type. This enhances
readability since the name gives the user an immediate clue of the type of the variable and
thereby the available resources of the object. JLabel nameLabel; // Correct
JLabel lblName; // Incorrect
JLabel name; // Incorrect
4.1.2 Long Methods
There is a widespread tendency for GUI code - in particular initialization code - to be implementedwith very long methods. This is probably a bad idea, for the same reason that all excessively longmethods are a bad idea - they are harder to understand and maintain. 4.1.3 Unnecessary Fields
Fields should be created only if necessary. If a particular element is created, wired into the GUI, and then no longer referenced anywhere else in the class, then it should probably not exist as afield, but rather as a simple local variable. This style will allow the more important GUI elements
(fields) to stand out from the less important ones (local variables). IntelliJ Idea indicates unusedfields of a class with a yellow line in the right hand side, so it is easy to remove them. 4.2 Design Pattern
In Travel Box we need to restrict the multiple initializations of panels, dialogs, windows and
frames in order to utilize the memory usage. Therefore we follow Singleton Design Pattern for
our GUI developments. With singleton design pattern, it is guaranteed that there is only one
instance of GUI component can be created within one JVM. // Make the constructor private to restrict it’s initialization from outside.
private FeeTypePanel()
{
try
{
jbInit();
setupGUI();
}
catch( Exception exception )
{
exception.printStackTrace();
}
}
// Initialize a static variable with private access and create a getter methodfor that. Getter method // should contain following piece of code.
private static FeeTypePanel instance;
public static FeeTypePanel getInstance()
{
if(instance == null )
{
instance = new FeeTypePanel();
}
return thisInstance;
}
When you need an instance of this panel at anytime, just call
FeeTypePanel.getInstance();
But keep in mind not to access data in this panel from another panel without updating the data. This may cause data inconsistencies, which will be very hard to track down. You can add this template code to your IDEA installation to generate singleton instance
automatically. Add following XML to your IDEA user define templates file
(\.IntelliJIdea50\config\templates\user.xml) to create a live template which generates getInstancemethod automatically by placing it within <templateSet> tag
<template name="sgtn" value="private static $CLASS_NAME$ instance = null;&#10;&#10;public
static $CLASS_NAME$ getInstance()&#10;{&#10; if( instance == null )&#10; {&#10;
instance = new $CLASS_NAME$();&#10; }&#10; return instance;&#10;}" description="Generate getInstance Method for singleton Design pattern" toReformat="false"
toShortenFQNames="true">
<variable name="CLASS_NAME" expression="className()" defaultValue="" alwaysStopAt="false" />
<context>
<option name="JAVA_CODE" value="true" />
<option name="JAVA_COMMENT" value="false" />
<option name="JAVA_STRING" value="false" />
<option name="XML" value="false" />
<option name="HTML" value="false" />
<option name="JSP" value="false" />
<option name="COMPLETION" value="false" />
<option name="OTHER" value="false" />
</context>
</template>
4.3 Class content
All GUI components may consist of all or many number of following methods. private void jbInit() throws Exception // Compulsory. Auto generated code from// GUI designer comes here.
private void setupGUI() // Compulsory. All our initialization
// codes are written here.
public void setData( Object data ) // Optional. Sets data to the
//components inside the GUI.
public Object getData() // Optional. Collect the data
//from the components and set them to the bean.4.4 Look and Feel Guidelines
In order to preserve the consistency among all the Travel Box GUIs there are some guidelines tobe followed. 4.4.1 General Guidelines
 Changing look and feel is meant as a design tool, not as a runtime tool; that is, Sun
recommends that applications should not allow the end user to change the look and feel
at runtime.  Frequently used labels (ie. Compulsory fields of a searching interface) should always
have an associated mnemonic, to allow navigation through the keyboard (Alt + F, for
example) instead of the mouse. Two common exceptions are the default button
(activated by hitting Enter or Return) and the Cancel button (activated by hitting Escape) -
these should not have an associated mnemonic.  Choose mnemonics according to the related menu item, if possible; otherwise, select thefirst letter, a prominent consonant, or a prominent vowel, in that order of preference.  Use the JLabel.setLabelFor method to associate a mnemonic with an element which
would not otherwise have one, for example a JTextField.  The spacing of elements should be multiples of 6 pixels (6N); if one of the elements has awhite border, then the spacing should be one pixel less (6N - 1). In Travel Box 12 pixel ismost preferred.  Dialog title bars should contain the application name, for example "Reservation Manager :
Accommodation Search".  Layout Manager: Card Layout for wizards and Grid Bag Layout for the rest. We need toevaluate the GroupLayout which comes with Mattice GUI builder in NetBeans.  Border Style: Etched lowered border with or without title.
 Buttons: Should be created using Button Factory by specifying the button type. It will
create a standard button according to the type. Example :
JButton selectAllButton =
ButtonFactory.createButton(ButtonFactory.SUB_TOOLBAR_BUTTON, "", "Select All", (Icon)
Images.SELECT_ALL_ICON );
 Popup Messages: Should be created using Message Factory by specifying the messagetype. Example :
MessageFactory.showInformationMessage( "Invalid number of Infant ages hasbeen entered", “Reservation Manager : Accommodation Search” );
 Display only areas: Most preferred method is HTML.  Application Main Frame: Should be inherited from CGMainFrame
 When designing GUIs do it with minimum number of panels. Need to avoid putting
JPanels within Jpanels as much as possible. 4.4.2 Menus
Menus should be logically grouped and displayed, so that the user can easily understand their
functionality. Also menu separators must be added to distinguish similar menu items. 4.4.2.1 Composition and Construction of Items
 Make your menu items brief. Menu items can be verb phrases, such as Align Left. They
can also be nouns, such as Font, particularly when they display a submenu or a dialogbox.  Never give a menu item the same name as its menu title. For example, an Edit menu
should not contain an Edit menu item.  Use headline capitalization for menu items.  Include mnemonics for all menu items.  Offer keyboard shortcuts for frequently used menu items.  Use the same keyboard shortcut if a menu item appears in multiple menus-- for instance,
if a Cut item appears in a contextual menu as well as in a drop- down Edit menu, use
Ctrl-X for both.  Use the same mnemonic if a menu item appears in several menus--for instance, if a
Copy item appears in a contextual menu as well as in a drop- down Edit menu, use Copyfor both.
4.4.2.2 Conventional key board short cuts used for menus
Ctrl-A Select All (Edit menu)
Ctrl-B Bold (Format menu)
Ctrl-C Copy (Edit menu)
Ctrl-E Align Center (Format menu)
Ctrl-F Find (Edit menu)
Ctrl-G Find Again (Edit menu)
Ctrl-H Replace (Edit menu)
Ctrl-I Italic (Format menu)
Ctrl-L Align Left (Format menu)
Ctrl-N New (File menu)
Ctrl-O Open (File menu)
Ctrl-P Print (File menu)
Ctrl-R Align Right (Format menu)
Ctrl-S Save (File menu)
Ctrl-U Underline (Format menu)
Ctrl-V Paste (Edit menu)
Ctrl-W Close (File menu)
Ctrl-X Cut (Edit menu)
Ctrl-Y Redo (Edit menu)
Ctrl-Z Undo (Edit menu)
Delete Delete (Edit menu)
F1 Help
F5 Refresh
Shift-F1 Contextual help
4.4.2.3 Conventional mnemonics used for menus
4.4.3 Capitalization
Almost all short textual items (menus, buttons, labels, for example) should have headline
capitalization, where all words are capitalized except for common words with up to three letters (a, an, the, and, or, so, yet, and so on), and do not appear as the first or last word.
If the text is not short, then the capitalization used in ordinary prose (sentence capitalization)
should be used instead.
A Select All (Edit menu), Save As (File menu), About Application (Help menu)
B Bold (Format menu)
C
Copy (Edit menu), Close (File menu), Align Center (Format menu), Contents
(Help menu)
D Delete (Edit menu), Details (View menu)
E Edit menu
F File menu, Find (Edit menu), Filter (View menu)
G Large Icons (View menu)
H Help menu
I Index (Help menu), Italic (Format menu)
L Align Left (Format menu), List (View menu)
M Small Icons (View menu)
N Find Again (Edit menu), New (File menu)
O Open (File menu), Zoom Out (View menu)
P Paste (Edit menu), Print (File menu)
R Format menu, Redo (Edit menu), Align Right (Format menu), Refresh (View menu)
S Save (File menu), Search (Help menu), Sort By (View menu)
T Cut (Edit menu), Tutorial (Help menu)
U Undo (Edit menu), Page Setup (File menu), Underline (Format menu)
V View menu
X Exit (File menu)
Z Zoom In (View menu)
4.5 GUI Thread
Any Java GUI application has only one event handling threads (GUI thread) and all the GUI tasksshould be carried out within that thread, otherwise the GUI behavior cannot be predicted. Java
provides two static methods to handle this in javax.swing.SwingUtilities class. invokeAndWait andinvokeLater methods. (Please read the JavaDoc of each method)
4.6 Time Consuming Tasks
Time consuming tasks like server calls, should be done in a separate thread. The preferred way
to do this is using a Swing Worker Thread. It avoids the freezing of GUI screens while time
consuming task is in progress. A suitable progress indication should be visible during the task. SwingWorker sw = new SwingWorker()
{
public Object construct()
{
// All non-GUI tasks are done here.
}
public void finished()
{
// GUI updates are done here.
}
};
sw.start(); // Start the thread
4.7 Data Grids
CG data grid is a component developed by codegen to use as an enhanced JTable. There are
many functionalities have been built in this component. So it is advised to use this component
whenever there is a requirement of a table. Here is a sample usage of data grid. // Initialization
CGDataGrid cgDataGrid = new CGDataGrid();
// Add columns to the grid
cgDataGrid.addColumn( new ColumnData( Column Title, Length, Alignment,Column Type, getter, setter ) );
There are more parameters ( like editable, visible, maxLength etc. ) can be set in the column dataas per the requirement. The cell renderer and cell editor will be set automatically according to thecolumn type specified by you. If you want to use your own cell renderer and cell editor you candoit by passing them to the column data constructor.
For foreign key attributes ( eg. currency code. ) , you cannot use the normal getter, setter
methods. Instead of them you have to add new _getter, _setter methods to wrap original
methods. After all columns were added, setup the grid. cgDataGrid.setupModel();
Alternatively you can have your own table model and setup the drid. cgDataGrid.setModel( tableModel );
cgDataGrid.setupModel();
Each grid may have actions to add, delete, and edit binding data. These actions can be added asfollowing. CGAction addAction = new CGAction( "Add", "Add New Row", Images.ADD ){
public void actionPerformed( ActionEvent e )
{
// Action implementation comes here.
}
};
cgDataGrid.addAction( addAction );
Apart from these there are functionalities such as sort, search, copy/paste, arithmetic and find
/replace etc, which can be switched on/off according to the requirement. 4.8 Data Mappers
Data mapper is another component developed by codegen to manipulate data in GUIs. Due to
the tree view of all the TBX data presentation, the data should be handled with more care. Thecommon practice is, once a tree node is clicked, the data associated with that tree node will beset to the appropriate panel on the right side. When the tree node focus is changed, the existingdata object should be updated and the new data object should be visible in the right panel. This
may need lots of listeners to be listened to each element. We can avoid this by using a data
mapper, it does the job automatically. //Initialization
private DataMapper dataMapper = new DataMapper();
Each element has to be registered with the data mapper to do the data mapping. dataMapper.add( new DataMapping( Element Name, Data type class, TargetObject class, getter, setter ) );
After data mapper is setup, call it’s setData() method to set data to the GUI elements.