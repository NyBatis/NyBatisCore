# NyBatis Data Mapper Framework

## Introduction

NyBatis is Persistent Data Mapper Framework for Relation DataBase and abbreviation is Nextgeneration of Your Batis.

Nowaday general trend is JPA or Hibernate and many people think that Data Mapper Framework is old and not easy.

I agree JPA is very brilliant solution but believe that there is no silver bullet. DMF(Data Mapper Framework) is also appropriate in some business.

I admire MyBatis but it's feature is gradually outdated and nothing to happy for novice developer.
So I develop this an alternative for IBatis or MyBatis. 

Focus of NyBatis is simple and easy. Many feature to be determined by developer was automated so it helps to keep code clear from SQL.

I confess my lazy. I developed it on circumstance with Java 8 so NyBatis is **only supports Java 8**.
NyBatis also can be used in spring, struts and so on But it still doesn't have simple configruation to apply with above popular framework yet. 


## Example ##

### SQL ###

SQL is written as XML in file like MyBatis. but SQL id decided only with **<font color="blue">FileName</font><font color="red">.</font><font color="green">id</font>**


```
User.xml

<mapper>
  <sql id="select">
    SELECT  *
    FROM    USER
    WHERE   1=1
    AND     name = #{name}
      <if test="#{age} != empty">
      AND     age  = #{age}
      </if>
      <else>
      AND     age <= 20
      </else>
  </sql>
</mapper>
```

Java code is like below.

```
SqlSession session = SessionManager.openSession();

Map param = new HashMap();
param.put( "name", "nayasis" );
param.put( "age",  18 );

List<Map> list = session.sqlId( "User.mapper", param ).list().select();
```

Or 

```
public class User {
   public String name;
   public int    age;
}
...

SqlSession session = SessionManager.openSession();

User param = new User();
param.setName( "nayasis" );
param.setAge( 18 );

List<User> list = session.sqlId( "User.mapper", param ).list().select( User.class );

```

XML SQL only contains <font color="red">id</font> and <font color="red">query</font>. it does not contains code dependent attribute like **parameter type** or **return class**.


SQL also can be injected in NyBatis like Spring JDBC template.

```
session.sql( "UPDATE USER SET age = #{age} WHERE name = #{name}", param ).execute();
```

When method <font color=blue>execute()</font> is called, transaction will be activated automatically. NyBatis manages transaction mechanism by itself and developer does not annoy to controll it. It will be explained in detail later.


Another tip is that you can write '<' charater in XML :)
 
 
### ORM ###

NyBatis supports simple ORM mechanism.


```
@Table( "TB_DEV_USER" )
public class User {
   public String name;
   public int    age;
}
...

OrmSession session = SessionManager.openOrmSession( User.class );

User param = new User();
param.setName( "nayasis" );

User user = session.select( user );
user.setAge( 20 );

session.update( user ); // if user data was cached, cache will be refreshed.
session.delete( user ); // if user data was cached, cache will be deleted.
session.insert( user );

```

List data also can be retrived.

```
OrmSession session = SessionManager.openOrmSession( User.class );

List<User> list = session.list().setPage( 10, 50 ).select();

int count = session.list().count();

```

Dynamic condition can be written like below.

```
OrmSession session = SessionManager.openOrmSession( User.class );

List<User> list01 = session.list()
   .where( "age >= #{age}",  19 ).select();

List<User> list02 = session.list()
   .where( "name like #{name}", "%y%" )
   .where( "age >= #{age}",  19 )
   .orderBy( "name" )
   .select();

Map param = new HashMap();
param.put( "name", "%y%" );
param.put( "age", 19 );

List<User> list03 = session.list()
   .where( "name like #{name} OR age >= #{age}", param )
   .orderBy( "name, age DESC" )
   .select();
```

### MASS Insert / Update / Delete ###

NyBatis supports mass data manifulation mode like addBatch in JDBC.
 
```
List<User> users = ...

// In SqlSession
SqlSession sqlSession = SessionManager.openSession();
sqlSession.batchSqlId( "User.insert", users ).execute();

// In OrmSession
OrmSession ormSession = SessionManager.openOrmSession( User.class );
ormSession.batch().insert( users );
ormSession.batch().update( users );
ormSession.batch().delete( users );
```

It does not needs any addtional configuration. 
