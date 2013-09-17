
Thymeleaf With Dialect
========================

A dialect for Thymeleaf that allows you to use attributes with a "with" prefix to avoid having long "th:with"-expressions.

 - Current version: 0.0.1-SNAPSHOT
 - Released: 17 September 2013


Requirements
------------

 - Java 5
 - Thymeleaf 2.0.18+ (2.0.18 and its dependencies included)


Installation
------------

### For Maven and Maven-compatible dependency managers
Add a dependency to your project with the following co-ordinates:

 - GroupId: `org.mfrey.thymeleaf.extras.with`
 - ArtifactId: `thymeleaf-extras-with-dialect`
 - Version: `0.0.1-SNAPSHOT`


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
      <beans:bean class="org.mfrey.thymeleaf.extras.with.WithDialect" />
    </set>
  </property>

</bean>
```

Changelog
---------

### 0.0.1-SNAPSHOT
 - Initial commit.

