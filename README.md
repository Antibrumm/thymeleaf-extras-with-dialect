
Thymeleaf With Dialect
========================

A dialect for Thymeleaf that allows you to use attributes with a "with" prefix to avoid having long "th:with"-expressions.

[![Build Status](https://travis-ci.org/Antibrumm/thymeleaf-extras-with-dialect.png)](https://travis-ci.org/Antibrumm/thymeleaf-extras-with-dialect)

Requirements
------------

 - Java 5
 - Thymeleaf 3.0.0+ (3.0.0.BETA01 and its dependencies included)


Installation
------------

### For Maven and Maven-compatible dependency managers
Add a dependency to your project with the following co-ordinates:

 - GroupId: `ch.mfrey.thymeleaf.extras.with`
 - ArtifactId: `thymeleaf-with-dialect`
 - Version: `${thymeleaf-with-dialect.version}`


Usage
-----

Add the With dialect to your existing Thymeleaf template engine, eg:

```java
ServletContextTemplateResolver templateresolver = new ServletContextTemplateResolver();
templateresolver.setTemplateMode("HTML5");

templateengine = new TemplateEngine();
templateengine.setTemplateResolver(templateresolver);
templateengine.addDialect(new WithDialect());		// This line adds the dialect to Thymeleaf
```

Or, for those using Spring configuration files:

```xml
<bean id="templateResolver" class="org.thymeleaf.templateresolver.ServletContextTemplateResolver">
  <property name="templateMode" value="HTML5"/>
</bean>

<bean id="templateEngine" class="org.thymeleaf.spring3.SpringTemplateEngine">
  <property name="templateResolver" ref="templateResolver"/>

  <!-- These lines add the dialect to Thymeleaf -->
  <property name="additionalDialects">
    <set>
      <beans:bean class="ch.mfrey.thymeleaf.extras.with.WithDialect" />
    </set>
  </property>

</bean>
```

Use it
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
	xmlns:with="http://www.thymeleaf.org/extras/with">
<head></head>
<body>
	<div with:isActive="${true}"
	 	 with:hello="'Hello'"
	 	 with:helloWorld="${hello} + ' World'"
	 	 with:helloUnderWorld="${hello + ' Under World'}"
	 	 with:someCalc="${2 + 4}"
	 	 with:moreCalc="${someCalc + 10}">
		<div th:text="${isActive}">true</div>
		<div th:text="${hello}">Hello</div>
		<div th:text="${helloWorld}">Hello World</div>
		<div th:text="${helloUnderWorld}">Hello Under World</div>
		<div th:text="${someCalc}">6</div>
		<div th:text="${moreCalc}">16</div>
		<div with:evenMoreCalc="${moreCalc + 5}" th:text="${evenMoreCalc}">21</div>
	</div>
</body>
</html>
```


Changelog
---------

### 0.0.1-SNAPSHOT
 - Initial commit.
 
### 1.0.0
 - Considered stable
 - Changed groupid

### 2.0.0
 - Working against Thymeleaf 2.1.0+

### 3.0.0-SNAPSHOT
 - Working againt Thymeleaf 3.0.0.BETA01