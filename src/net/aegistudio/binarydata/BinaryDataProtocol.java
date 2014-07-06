package net.aegistudio.binarydata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class BinaryDataProtocol
{
	private BinaryDataProtocol()
	{
		//can not be instantialized!
	}
	
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface INSTANTIALIZE
	{
		
	}
	
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface TRANSIENT
	{
		
	}
	
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface COLLECTION
	{
		Class<?> value() default Object.class;
	}
}