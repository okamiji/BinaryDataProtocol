package net.aegistudio.binarydata;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.io.DataOutputStream;
import java.io.IOException;

public class BinaryDataOutputStream extends OutputStream
{
	
	private final DataOutputStream dataoutputstream;
	
	public BinaryDataOutputStream(OutputStream outputstream)
	{
		this.dataoutputstream = new DataOutputStream(outputstream);
	}
	
	@Override
	public void write(int count) throws IOException
	{
		this.dataoutputstream.write(count);
	}
	
	public void writeObject(Object object) throws IOException
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
				if(classtype.equals(boolean.class)) this.dataoutputstream.writeBoolean(field.getBoolean(object));
				else if(classtype.equals(boolean[].class))
				{
					boolean[] valuearray = (boolean[])field.get(object);
					this.dataoutputstream.writeInt(valuearray.length);
					for(int j = 0; j < valuearray.length; j++) this.dataoutputstream.writeBoolean(valuearray[j]);
				}
				else if(classtype.equals(byte.class)) this.dataoutputstream.writeByte(field.getByte(object));
				else if(classtype.equals(byte[].class))
				{
					byte[] valuearray = (byte[])field.get(object);
					this.dataoutputstream.writeInt(valuearray.length);
					for(int j = 0; j < valuearray.length; j++) this.dataoutputstream.writeByte(valuearray[j]);
				}
				else if(classtype.equals(char.class)) this.dataoutputstream.writeChar(field.getChar(object));
				else if(classtype.equals(char[].class))
				{
					char[] valuearray = (char[])field.get(object);
					this.dataoutputstream.writeInt(valuearray.length);
					for(int j = 0; j < valuearray.length; j++) this.dataoutputstream.writeChar(valuearray[j]);
				}
				else if(classtype.equals(short.class)) this.dataoutputstream.writeShort(field.getShort(object));
				else if(classtype.equals(short[].class))
				{
					short[] valuearray = (short[])field.get(object);
					this.dataoutputstream.writeInt(valuearray.length);
					for(int j = 0; j < valuearray.length; j++) this.dataoutputstream.writeShort(valuearray[j]);
				}
				else if(classtype.equals(int.class)) this.dataoutputstream.writeInt(field.getInt(object));
				else if(classtype.equals(int[].class))
				{
					int[] valuearray = (int[])field.get(object);
					this.dataoutputstream.writeInt(valuearray.length);
					for(int j = 0; j < valuearray.length; j++) this.dataoutputstream.writeInt(valuearray[j]);
				}
				else if(classtype.equals(long.class)) this.dataoutputstream.writeLong(field.getLong(object));
				else if(classtype.equals(long[].class))
				{
					long[] valuearray = (long[])field.get(object);
					this.dataoutputstream.writeInt(valuearray.length);
					for(int j = 0; j < valuearray.length; j++) this.dataoutputstream.writeLong(valuearray[j]);
				}
				else if(classtype.equals(float.class)) this.dataoutputstream.writeFloat(field.getFloat(object));
				else if(classtype.equals(float[].class))
				{
					float[] valuearray = (float[])field.get(object);
					this.dataoutputstream.writeInt(valuearray.length);
					for(int j = 0; j < valuearray.length; j++) this.dataoutputstream.writeFloat(valuearray[j]);
				}
				else if(classtype.equals(double.class)) this.dataoutputstream.writeDouble(field.getDouble(object));
				else if(classtype.equals(double[].class))
				{
					double[] valuearray = (double[])field.get(object);
					this.dataoutputstream.writeInt(valuearray.length);
					for(int j = 0; j < valuearray.length; j++) this.dataoutputstream.writeDouble(valuearray[j]);
				}
				else if(classtype.equals(String.class)) this.dataoutputstream.writeUTF((String)field.get(object));
				else if(classtype.equals(String[].class))
				{
					String[] valuearray = (String[])field.get(object);
					this.dataoutputstream.writeInt(valuearray.length);
					for(int j = 0; j < valuearray.length; j++) this.dataoutputstream.writeUTF(valuearray[j]);
				}
				else if(Object[].class.isAssignableFrom(classtype))
				{
					Object[] valuearray = (String[])field.get(object);
					this.dataoutputstream.writeInt(valuearray.length);
					for(int j = 0; j < valuearray.length; j++) this.writeObject(valuearray[j]);
				}
				else if(Collection.class.isAssignableFrom(classtype) && field.isAnnotationPresent(BinaryDataProtocol.COLLECTION.class))
				{
					Class<?> innerclasstype = field.getAnnotation(BinaryDataProtocol.COLLECTION.class).value();
					@SuppressWarnings("rawtypes")
					Collection collection = Collection.class.cast(field.get(object));
					this.dataoutputstream.writeInt(collection.size());
					Object[] objects = collection.toArray();
					for(int j = 0; j < objects.length; j++)
					{
						if(innerclasstype.equals(String.class)) this.dataoutputstream.writeUTF((String)objects[j]);
						else this.writeObject(objects[j]);
					}
				}
				else if(Object.class.isAssignableFrom(classtype)) this.writeObject(field.get(object));
				field.setAccessible(accessible);
			}
			catch(Exception exception)
			{
				if(exception instanceof IOException) throw (IOException)exception;
			}
		}
	}
	
}