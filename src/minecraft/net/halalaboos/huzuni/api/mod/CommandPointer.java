package net.halalaboos.huzuni.api.mod;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation used to point to functions that can automatically loaded as an annotation command. <br/>
 * The value is a string array representing the command's aliases.
 * */
@Retention(RUNTIME)
@Target(METHOD)
public @interface CommandPointer {

	String[] value();
	
	String description() default "No description provided!";
	
}
