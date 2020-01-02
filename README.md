# exprTool
基于Aviator的简单二元表达式模板工具


# 语法介绍

```g4
grammar Expr;

tokens {
    DELIMITER
}

expr: IF condition=str THEN body (ELIF condition=str THEN body)* (ELSE body)? END;

body: str? (expr str?)*;

str: ~(IF | THEN | ELSE | ELIF | END);

IF: '#IF';
THEN: '#THEN';
ELSE: '#ELSE';
ELIF: '#ELIF';
END: '#END';

SIMPLE_COMMENT
    : '--' ~[\r\n]* '\r'? '\n'? -> channel(HIDDEN)
    ;

BRACKETED_COMMENT
    : '/*' .*? '*/' -> channel(HIDDEN)
    ;

WS
    : [ \r\n\t]+ -> channel(HIDDEN)
    ;

UNRECOGNIZED
    : .
    ;
```



# 快速开始

```scala
val sql = """
SELECT concat(year,"-",month,"-",day) as ddate,count(1) num
FROM #if woe #then hive.woe.l_activity_taskcomplete_log #else hive.boe.l_activity_taskcomplete_log #end
WHERE concat(year,month,day) between "20190321" and "20191231"
#if 1=1 #then 
	1.0
	#if 2>1 #then
		2.1
		#if 2<1 #then
			2.1.1
		#elif 2==2 #then
			2.1.2
		#else
			2.1.3
		#end
		2.2
	#else
		333
	#end
  	1.1
#else
   1.2
#end;
	and activityid=30015 as e1;
 """;
println(exprEvalService.eval(sql, params));
```

执行结果：

```
SELECT concat(year,"-",month,"-",day) as ddate,count(1) num
FROM  hive.woe.l_activity_taskcomplete_log 
WHERE concat(year,month,day) between "20190321" and "20191231"
 
	1.0
	
		2.1
		
			2.1.2
		
		2.2
	
  	1.1
;
	and activityid=30015 as e1;
```

