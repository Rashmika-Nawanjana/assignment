DATABASEBESTPRACTICESPrepared by : AsangaNimalasena
Goal of DatabaseOptimizationReduceDBTimeDB Time = Non-IdleWait Time+CPUTimeCPU Time = Parse/ Execute/FetchWait Time = I/O/ Network/Locks
CPU Time - Parsel Use of Bind Variables
l Prepared Statement – Reduce Number of Hard ParsesExample …
l Statement Cache
l Already implemented in the Connection Pool – ReduceNumberofSoft Parses
Example …
l Batching
l Reduce Number of Executions
Example
CPU Time - ParseBatch Vs No Batch
0
2000
4000
6000
8000
10000
12000
14000
16000
1 5 10 25 50 100 500 1000 2000 5000 10000
Inserts
Tim
e
T
a
k
e
n to In
s
ert
No Batch Batch
Inserts Time in ms
No Batch Batch
1 219 204
5 218 203
10 234 218
25 250 219
50 297 235
100 375 235
500 969 390
1000 2453 562
2000 3281 812
5000 6765 1454
10000 13468 2719
CPU Time - Executel Reduce Number of Blocks AccessedlAvoid Full Table Scans
• Reads all rows from a table and filters out thosethat donotmeet the selection criteria
• Full Table Scans are used … Lack of Index
 Large Amount of Data
 Small Table
CPU Time - Executel Reduce Number of BlocksAccessedl Use Index Base Access
• B-Tree Indexes
 The standard index type
 Excellent for primary key and highly-selectiveindexes.  Used as concatenated indexes.  B-tree indexes can be used to retrievedatasortedby the index columns.
• Performs poorly when selectivity is low.
CPU Time - Executel Reduce Number of Blocks AccessedlUse Index Base Access
• Bitmap Indexes
 Suitable for low cardinality data.  Can generate a large number of rowids withminimal I/O. Combining bitmap indexes on non-selectivecolumnsallows efficient AND and OR operations withagreat
number of rowids with minimal I/O.  Bitmap indexes are particularly efficient inquerieswithCOUNT(), because the query can be satisfiedwithintheindex
• Performs far worse on tables which has heavy concurrentDML(inserts/updates/deletes)
CPU Time - Executel Reduce Number of Blocks AccessedlUse Index Base Access
• Function Base Indexes
 Columns that are either transformed by afunction, suchasthe UPPER function, or included in an expression, suchascol1 + col2.  Can store computation-intensive expressionsintheindex. Allows that data to be returned using the indexwhenthatfunction or expression is used in a WHEREclauseoranORDER BY clause.  Allows Oracle to bypass computing the valueof theexpression when processing SELECT andDELETEstatements. l CREATE INDEX uppercase_idx ON employees (UPPER(last_name));l SELECT * FROM employees WHERE UPPER(last_name) = 'MARKSON';
CPU Time - ExecutelOrdering Columns in anIndex• Order columns with most selectivityfirst.Mainlyfor primary keys and for very selectiverangescans.
• In large range scans, I/Os canusuallybereduced by ordering the columnsintheleastselective order, or in a manner that sortsthedata in the way it should be retrieved.
CPU Time - Executel Consider indexing keys that are used frequentlyinWHERE clauses. l Consider indexing keys that are used frequentlytojointables in SQL statements. l Do not use standard B-tree indexes onkeysor
expressions with few distinct values. l Do not index columns that are modifiedfrequently.l Do not index keys that appear only in WHEREclauseswith functions or operators except withfunction-basedindexes . l Consider indexing foreign keys of referential integrityconstraints
l When choosing to index a key, consider whethertheperformance gain for queries is worththeperformanceloss for DML
CPU Time - Executel A composite index contains more than one key columnl Sometimes two or more columns or expressions can be combinedtoformacomposite index with higher selectivity
l If all columns selected by a query are in a composite index canreturnvalueswithout accessing the table
l A SQL statement can use an access path involvingacompositeindex if the statement contains constructs that usealeadingportionof the index
l CREATE INDEX comp_ind ON table1(x, y, z);
l x, xy, and xyz combinations of columns are leading portions of the indexl yz, y, and z combinations of columns are not leading portions of the indexl create composite index on keys frequently in WHEREclauseconditions combined with AND operators, especially combinedselectivity is better than the selectivity of either key individually
CPU Time - ExecutelProper Table Design
lAvoid Row Chaining and Migration• Nullable Columns Towards the End
• Varchar2(4000) Vs LOB
lOver use of 3NF
CPU Time - ExecutelPL/SQL nocopy hint
lPasses pl/sql parameters by referencelBeneficial when passing large arrays, tablesetc.lBatching
lExplained earlier…
CPU Time - Executel Restructuring the SQL Statements
l Avoid Transformed Columns in the WHERE Clause• Use
 WHERE a.order_no = b.order_no
• rather than:
 WHERE TO_NUMBER (SUBSTR(a.order_no, INSTR(b.order_no,'.') - 1)) = TO_NUMBER (SUBSTR(a.order_no, INSTR(b.order_no,'.') - 1))
l When need to use SQL functions on filters or join predicates, donotuse them on indexed columns
• Use
 TO_CHAR(numcol) = varcol
• rather than
 varcol = TO_CHAR(numcol)
l EXISTS versus IN
• If the selective predicate is in the subquery, then use IN• If the selective predicate is in the parent query, then useEXISTS
CPU Time - ExecutelWhen to use INl Select * from T1 where x in ( select y fromT2 )
is typically processed as:
select * from t1, ( select distinct y fromt2 ) t2 wheret1.x=t2.y;l The subquery is evaluated, distinct'ed, indexed (or hashedorsorted)and then joined to the original table
l If the result of the subquery is small -- then INis typicalymoreappropriate
CPU Time - ExecutelWhen to use exist
l select * from t1 where exists ( select null from t2 where y = x )
That is processed more like:
l for x in ( select * from t1 )
loop
if ( exists ( select null from t2 where y = x.x ) then
OUTPUT THE RECORD
end if
end loop
l It always results in a full scan of T1
l If ( select y from T2 ) is "huge" and takes a long time but table T1 is relativelysmalland
l executing ( select null from t2 where y = x.x ) is very very fast (index ont2(y)). l Then the exists will be faster
CPU Time - FetchlSet Fetch Size
lCan be set in Connection or Statement
lFetch Multiple Rows in a Single FetchlReduce Network overhead
• Example
lUse Oracle Schedules
lRepeated Tasks are carried out in Server SidelNo network usage
CPU Time - Fetchl Using a Scheduler
¡ 1. create a procedure
procedure testproc is
begin
insert into test values('aaa',systimestamp);
end testproc;
¡ 2. create a scheduler
• begin
• DBMS_SCHEDULER.create_job (
• job_name => 'test_job_definition',
• job_type => 'PLSQL_BLOCK',
• job_action => 'testproc();',
• start_date => SYSTIMESTAMP,
• repeat_interval => 'freq=secondly;interval=10',
• end_date => NULL,
• enabled => TRUE,
• comments => 'Job defined entirely by the CREATE JOBprocedure.');
• end;
• /
CPU Time - Fetchl Find out the Status of the Job
l SQL> SELECT owner, job_name, enabled FROM dba_scheduler_jobs;
OWNER JOB_NAME ENABLE
---------- ------------------------------ ------------ SYS AUTO_SPACE_ADVISOR_JOB TRUE
SYS GATHER_STATS_JOB TRUE
SYS FGR$AUTOPURGE_JOB FALSE
SYS PURGE_LOG TRUETBX TEST_JOB_DEFINITION TRUE
l exec
dbms_scheduler.set_attribute('test_job_definition','repeat_interval','freq=minutely;interval=1');
l exec dbms_scheduler.drop_job(job_name =>'test_job_definition');
TheEND