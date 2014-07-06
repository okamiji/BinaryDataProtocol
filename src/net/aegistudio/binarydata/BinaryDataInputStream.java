package net.aegistudio.binarydata;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.io.DataInputStream;
import java.io.IOException;

public class BinaryDataInputStream extends InputStream
{
	
	private final DataInputStream datainputstream;
	
	public BinaryDataInputStream(InputStream inputstream)
	{
		this.datainputstream = new DataInputStream(inputstream);
	}
	
	@Override
	public int read() throws IOException
	{
		return this.datainputstream.read();
	}
	
	public Object readObjectByClass(Class<?> objectclass) throws IOException
	{
		Method[] methods = objectclass.getMethods();
		for (Method method : methods) if(method.isAnnotationPresent(BinaryDataProtocol.INSTANTIALIZE.class))
		{
			try
			{
				Object object = method.invoke(null, (Object[])null);
				return this.readObject(object);
			}
			catch(Exception exception)
			{
				return null;
			}
		}
		try
		{
			Object object = objectclass.newInstance();
			return this.readObject(object);
		}
		catch(Exception exception)
		{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public Object readObject(Object object) throws IOException
	{
		Field[] fields = object.getClass().getDeclaredFields();
		for(Field field : fields)
		{
			try
			{
				if(field.isAnnotationPresent(BinaryDataProtocol.TRANSIENT.class)) continue;
				if(Modifier.isTransient(field.getModifiers())) continue;
				boolean accessible = field.isAccessible();
				Class<?> classtype = field.getType();
				field.setAccessible(true);
				if(classtype.equals(boolean.class)) field.setBoolean(object, this.datainputstream.readBoolean());
				else if(classtype.equals(boolean[].class))
				{
					int elementcount = this.datainputstream.readInt();
					boolean[] valuearray = new boolean[elementcount];
					for(int j = 0; j < elementcount; j++) valuearray[j] = this.datainputstream.readBoolean();
					field.set(object, valuearray);
				}
				else if(classtype.equals(byte.class)) field.setByte(object, this.datainputstream.readByte());
				else if(classtype.equals(byte[].class))
				{
					int elementcount = this.datainputstream.readInt();
					byte[] valuearray = new byte[elementcount];
					for(int j = 0; j < elementcount; j++) valuearray[j] = this.datainputstream.readByte();
					field.set(object, valuearray);
				}
				else if(classtype.equals(char.class)) field.setChar(object, this.datainputstream.readChar());
				else if(classtype.equals(char[].class))
				{
					int elementcount = this.datainputstream.readInt();
					char[] valuearray = new char[elementcount];
					for(int j = 0; j < elementcount; j++) valuearray[j] = this.datainputstream.readChar();
					field.set(object, valuearray);
				}
				else if(classtype.equals(short.class)) field.setShort(object, this.datainputstream.readShort());
				else if(classtype.equals(short[].class))
				{
					int elementcount = this.datainputstream.readInt();
					short[] valuearray = new short[elementcount];
					for(int j = 0; j < elementcount; j++) valuearray[j] = this.datainputstream.readShort();
					field.set(object, valuearray);
				}
				else if(classtype.equals(int.class)) field.setInt(object, this.datainputstream.readInt());
				else if(classtype.equals(int[].class))
				{
					int elementcount = this.datainputstream.readInt();
					int[] valuearray = new int[elementcount];
					for(int j = 0; j < elementcount; j++) valuearray[j] = this.datainputstream.readInt();
					field.set(object, valuearray);
				}
				else if(classtype.equals(long.class)) field.setLong(object, this.datainputstream.readLong());
				else if(classtype.equals(long[].class))
				{
					int elementcount = this.datainputstream.readInt();
					long[] valuearray = new long[elementcount];
					for(int j = 0; j < elementcount; j++) valuearray[j] = this.datainputstream.readLong();
					field.set(object, valuearray);
				}
				else if(classtype.equals(float.class)) field.setFloat(object, this.datainputstream.readFloat());
				else if(classtype.equals(float[].class))
				{
					int elementcount = this.datainputstream.readInt();
					float[] valuearray = new float[elementcount];
					for(int j = 0; j < elementcount; j++) valuearray[j] = this.datainputstream.readFloat();
					field.set(object, valuearray);
				}
				else if(classtype.equals(double.class)) field.setDouble(object, this.datainputstream.readDouble());
				else if(classtype.equals(double[].class))
				{
					int elementcount = this.datainputstream.readInt();
					double[] valuearray = new double[elementcount];
					for(int j = 0; j < elementcount; j++) valuearray[j] = this.datainputstream.readDouble();
					field.set(object, valuearray);
				}
				else if(classtype.equals(String.class))	field.set(object, this.datainputstream.readUTF());
				else if(classtype.equals(String[].class))
				{
					int elementcount = this.datainputstream.readInt();
					String[] valuearray = new String[elementcount];
					for(int j = 0; j < elementcount; j++) valuearray[j] = this.datainputstream.readUTF();
					field.set(object, valuearray);
				}
				else if(Object[].class.isAssignableFrom(classtype))
				{
					int elementcount = this.datainputstream.readInt();
					Object[] valuearray = new Object[elementcount];
					for(int j = 0; j < elementcount; j++) valuearray[j] = this.readObjectByClass(classtype.getComponentType());
					field.set(object, valuearray);
				}
				else if(Collection.class.isAssignableFrom(classtype) && field.isAnnotationPresent(BinaryDataProtocol.COLLECTION.class))
				{
					Class<?> innerclasstype = field.getAnnotation(BinaryDataProtocol.COLLECTION.class).value();
					int elementcount = this.datainputstream.readInt();
					@SuppressWarnings("rawtypes")
					Collection collection = Collection.class.cast(classtype.newInstance());
					for(int j = 0; j < elementcount; j++)
					{
						if(innerclasstype.equals(String.class)) collection.add(this.datainputstream.readUTF());
						else collection.add(this.readObjectByClass(innerclasstype));
					}
					field.set(object, collection);
				}
				else if(Object.class.isAssignableFrom(classtype)) field.set(object, this.readObjectByClass(classtype));
				
				field.setAccessible(accessible);
			}
			catch(Exception exception)
			{
				if(exception instanceof IOException) throw (IOException)exception;
			}
		}
		return object;
	}
	
}