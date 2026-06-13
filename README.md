# projection-validator

`projection-validator` is a small Java annotation processor that validates Spring Data JPA projection interfaces at compile time.

It helps catch broken projection methods before the application starts by checking that every projected getter maps to a field on the target JPA entity and that the return type matches.

## What it checks

For every interface annotated with `@JPAProjection`, the processor validates:

- The projection method maps to a field on the configured entity.
- JavaBean-style accessors are resolved correctly:
  - `getName()` maps to `name`
  - `isActive()` maps to `active`
  - `name()` maps to `name`
- The projection method return type matches the entity field type.
- Collection projections match collection fields.
- Nested projection return types are resolved through their own `@JPAProjection(entity = ...)` annotation.

By default, validation failures are reported as compiler errors.

## Requirements

- Java 21
- Maven
- Annotation processing enabled in your build

## Installation

The project defines these Maven coordinates:

```xml
<groupId>com.batu</groupId>
<artifactId>jpa-projection-validator</artifactId>
<version>1.0-SNAPSHOT</version>
```

If you are using it from source, first install it into your local Maven repository:

```bash
mvn clean install
```

Then add it to the project that contains your projections:

```xml
<dependency>
    <groupId>com.batu</groupId>
    <artifactId>jpa-projection-validator</artifactId>
    <version>1.0-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

If your build disables automatic annotation processor discovery, register it explicitly:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <annotationProcessorPaths>
                    <path>
                        <groupId>com.batu</groupId>
                        <artifactId>jpa-projection-validator</artifactId>
                        <version>1.0-SNAPSHOT</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

## Usage

Annotate a projection interface with `@JPAProjection` and point it at the entity it represents.

```java
import com.batu.api.JPAProjection;

@JPAProjection(entity = User.class)
public interface UserProjection {
    Long getId();
    String getName();
    boolean isActive();
}
```

Given an entity like this:

```java
public class User {
    private Long id;
    private String name;
    private boolean active;
}
```

the projection compiles successfully because each method maps to an entity field with the same type.

## Example validation failures

### Missing field

```java
@JPAProjection(entity = User.class)
public interface UserProjection {
    String getEmail();
}
```

If `User` does not have an `email` field, compilation fails with a message similar to:

```text
Getter not found in entity User at UserProjection:
 missing getter for: getEmail()
```

### Type mismatch

```java
@JPAProjection(entity = User.class)
public interface UserProjection {
    Integer getId();
}
```

If `User.id` is a `Long`, compilation fails with a message similar to:

```text
Type mismatch in entity User at UserProjection:
 expected: java.lang.Long
 found: java.lang.Integer
```

## Nested projections

Nested projections are supported when the returned projection type is also annotated with `@JPAProjection`.

```java
public class User {
    private Profile profile;
}

public class Profile {
    private String displayName;
}

@JPAProjection(entity = Profile.class)
public interface ProfileProjection {
    String getDisplayName();
}

@JPAProjection(entity = User.class)
public interface UserProjection {
    ProfileProjection getProfile();
}
```

The processor checks `UserProjection#getProfile()` against `User.profile`, then resolves `ProfileProjection` to its configured entity type.

## Collection projections

Collection fields are supported when both the entity field and projection method return collection types.

```java
public class User {
    private List<Role> roles;
}

@JPAProjection(entity = Role.class)
public interface RoleProjection {
    String getName();
}

@JPAProjection(entity = User.class)
public interface UserProjection {
    List<RoleProjection> getRoles();
}
```

The processor compares the collection element types, including nested projection element types.

## Configuration

Validation failures are compiler errors by default.

To report validation failures as warnings instead, pass this compiler option:

```bash
-Abatu.reporter.severity=warning
```

With Maven:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <compilerArgs>
                    <arg>-Abatu.reporter.severity=warning</arg>
                </compilerArgs>
            </configuration>
        </plugin>
    </plugins>
</build>
```

Any value other than `warning` uses error reporting.

## How it works

`projection-validator` registers a standard Java annotation processor through:

```text
META-INF/services/javax.annotation.processing.Processor
```

During compilation, the processor scans types annotated with:

```java
@JPAProjection(entity = SomeEntity.class)
```

It then visits each method in the projection interface, resolves the expected entity field name, and compares the projection return type with the entity field type.

## Limitations

- The processor checks entity fields directly. It does not inspect entity getter methods.
- Field names must match projection method names after JavaBean accessor conversion.
- Collection types are checked recursively by their generic element type.
- The processor treats all `java.util.Collection` implementations the same, so `List<T>` and `Set<T>` are considered compatible when their element types match.
- Raw collections or collections without generic type arguments are treated as invalid.
- Generic wrappers that are not `java.util.Collection`, such as `Optional<T>`, are not unwrapped.
- The processor targets Java 21.

## TODO

- [x] Validate `@JPAProjection` usage only on interfaces, not classes.
- [x] Handle default methods on projection interfaces.
- [ ] Handle wildcard generic types, such as `List<? extends RoleProjection>`.
- [ ] Add an annotation for ignoring selected projection methods during validation.