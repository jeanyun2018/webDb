# WebDB

一款webDB操作工具，在工作中遇到的实际问题。完成项目需求的时候，自己负责的一部分程序，可能需要其他部门给出数据。当然uat是必须要进行多部门联调。sit和开发单元测试的时候，往往为了更快的进行测试进度。一般会自己往数据库，填写必须的数据，模拟返回的结果。而且面对的情况比较复杂，比如，最终的结果表里面的数据，可能需要判断其他表的数据满足某种条件，并且依赖其他表中的数据。比如，最终表的数据，也可能是需要轮询该表，并且一次需要给出两条数据。假设轮询的频率为一分钟，如果手动查询数据库，查询到两个符合条件的结果，然后再用插入操作，放入结果表。中间时间可能间隔超过一分钟，导致轮询结果不准确，无法通过测试。
# 解决的问题：
  - 数据库的重复操作(根据同样的条件，查询同样的表，依照同样的规则进行操作。
  - 手动操作数据库不可避免的，延迟和重复劳动问题。


# 特点：
  - 以任务链的方式执行。
  - 支持mysql和oracle。
  - post数据/上传文件两种方式驱动。

# 涉及技术：

  - Spring boot
  - commons-dbutils （执行sql语句）
  - guava EventBus  (web层与处理层交互)
  - jsoup (解析xml)


### 使用：

java环境：jdk1.8

1.以maven方式导入项目。
```sh
运行WebDbApplication.java
访问http://127.0.0.1:8080/
```

2.java -jar方式执行

```sh
运行target目录下jar包。
java -jar ./webDB-0.0.1-SNAPSHOT.jar
访问http://127.0.0.1:8080/
```

### 实际案例：
```sh
<sql>
	<connectName>testName</connectName>
	<userName>root</userName>
	<passWord>123456</passWord>
	<url>jdbc:mysql://localhost:3306/project_man?characterEncoding=UTF8&useSSL=false</url>
	<driverName>com.mysql.jdbc.Driver</driverName>
</sql>

<config>
	<userId>3</userId>
</config>

<!--showRes="true"代表输出查询结果 -->
<job showRes="true">
	<name>selectMain</name>
	<type>select</type>
	<!--两个@包裹，代表该值，需要引用config标签中的值 -->
	<query>select * from user_info where id=@userId@</query>
	<!--如果查询结果为空，则显示该提示信息 -->
	<none>未查询到结果</none>
	<rules>
		<!-- firstOnly属性，代表如果查询多条结果，该规则只会匹配第一次
			 mustDo="true"表示，忽略匹配条件，查询结果后执行next的操作。
			 也可以替换为：rule="=@userId@"。按照查询结果中的id是否等于config中的userId进行匹配
			 next="insert:insertRES"
			 指明下一条job的类型和名称。
		-->
		<rule mustDo="true" next="select:nextSelect" firstOnly="true">id</rule>
	</rules>
</job>

<job>
	<name>insertRES</name>
	<type>insert</type>
	<query>INSERT INTO client (name, client_describe) VALUES ('#name#', '#client_describe#') 
	</query>
	<fail>新增失败</fail>
	<success>处理成功</success>
</job>

<job showRes="true">
	<name>nextSelect</name>
	<type>select</type>
	<!--两个#包裹，代表，该数据，接收上一次查询结果的结果集中的某一列的数据-->
	<query>select * from client where id<#id#</query>
	<none>未查询到结果</none>
	<rules>
		<rule mustDo="true" next="insert:insertRES">id</rule>
	</rules>
</job>
```

#### 标签说明：
<sql>标签包裹的为jdbc连接信息<sql>标签包裹的为jdbc连接信息
<config>标签中代表的是配置信息。会在xml文件解析过程中被替换到所有job标签中去。即该脚本的参数。
<job>标签为需要调度的任务，分为select和insert两种

### 执行顺序
会从解析到的第一个Job标签开始执行。如上实例，会执行查询名称为selectMain的Job。然后根据查询结果，以及对应的<rule>标签进行匹配。如果有匹配的结果，则进行next的Job。

如上的执行逻辑为，先去执行第一个解析出来的Job标签selectMain查询Job，对user_info表进行查询，id为3的结果，然后进行会泽匹配，执行select类型的nextSelect查询，nextSelec接收上一次查询的结果中的id，作为查询参数，即查询客户表中id小于3的的所有客户信息，然后查询的结果会调用insertRES操作，将数据新增到数据库中。


当然，上述操作，只是去用户表查询id，然后将所有的客户小于该id的值，进行复制一遍。并没有实际意义，但是说明了，该工具的作用。可以根据任意的条件，去组合查询和插入操作。完成我们所需要的功能。



