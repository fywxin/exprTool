package com.wjs.expr.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.net.URL;
import java.util.*;

/**
 * 反射工具类.
 *
 * @author wjs
 * 2014年9月6日-下午1:32:39
 */
public class ReflectionUtil {

	private static Logger logger = LoggerFactory.getLogger(ReflectionUtil.class);
	
	private static final int ACCESS_TEST = Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE;


	public static Object getPojoFieldValue(Object target, String propertyName){
		try{
			return invokeGetterMethod(target, propertyName);
		}catch (Exception e){
			return getFieldValue(target, propertyName);
		}
	}

	/**
	 * 调用Getter方法.
	 */
	public static Object invokeGetterMethod(Object target, String propertyName) {
		String getterMethodName = "get" + Character.toUpperCase(propertyName.charAt(0))+propertyName.substring(1);
		return invokeMethod(target, getterMethodName, new Class[] {}, new Object[] {});
	}

	/**
	 * 调用Setter方法.使用value的Class来查找Setter方法.
	 */
	public static void invokeSetterMethod(Object target, String propertyName, Object value) {
		invokeSetterMethod(target, propertyName, value, null);
	}

	/**
	 * 调用Setter方法.
	 * 
	 * @param propertyType 用于查找Setter方法,为空时使用value的Class替代.
	 */
	public static void invokeSetterMethod(Object target, String propertyName, Object value, Class<?> propertyType) {
		Class<?> type = propertyType != null ? propertyType : value.getClass();
		String setterMethodName = "set" + Character.toUpperCase(propertyName.charAt(0))+propertyName.substring(1);
		invokeMethod(target, setterMethodName, new Class[]{type}, new Object[]{value});
	}

	/**
	 * 直接读取对象属性值, 无视private/protected修饰符, 不经过getter函数.
	 */
	public static Object getFieldValue(final Object object, final String fieldName) {
		Field field = getDeclaredField(object, fieldName);

		if (field == null) {
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
		}

		makeAccessible(field);

		Object result = null;
		try {
			result = field.get(object);
		} catch (IllegalAccessException e) {
			logger.error("不可能抛出的异常{}", e.getMessage());
		}
		return result;
	}

	/**
	 * 直接设置对象属性值, 无视private/protected修饰符, 不经过setter函数.
	 */
	public static void setFieldValue(final Object object, final String fieldName, final Object value) {
		Field field = getDeclaredField(object, fieldName);

		if (field == null) {
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
		}

		makeAccessible(field);

		try {
			field.set(object, value);
		} catch (IllegalAccessException e) {
			if(logger.isDebugEnabled()){
				logger.error("设置对象属性值异常" + e.getMessage() + ", [ReflectionUtils.setFieldValue]", e);
			}
		}
	}

	/**
	 * 直接调用对象方法, 无视private/protected修饰符.
	 */
	public static Object invokeMethod(final Object object, final String methodName, final Class<?>[] parameterTypes,
			final Object[] parameters) {
		Method method = getDeclaredMethod(object, methodName, parameterTypes);
		if (method == null) {
			throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + object + "]");
		}

		method.setAccessible(true);

		try {
			return method.invoke(object, parameters);
		} catch (Exception e) {
			throw convertReflectionExceptionToall(e);
		}
	}

	/**
	 * 循环向上转型, 获取对象的DeclaredField.
	 * 
	 * 如向上转型到Object仍无法找到, 返回null.
	 */
	protected static Field getDeclaredField(final Object object, final String fieldName) {
		if (object == null){
			throw new IllegalArgumentException("object不能为空");
		}
		if (fieldName == null){
			throw new IllegalArgumentException("fieldName不能为空");
		}
		for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
			try {
				return superClass.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {// NOSONAR
				// Field不在当前类定义,继续向上转型
			}
		}
		return null;
	}

	/**
	 * 强行设置Field可访问.
	 */
	public static void makeAccessible(final Field field) {
		if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
			field.setAccessible(true);
		}
	}

	/**
	 * 循环向上转型, 获取对象的DeclaredMethod.
	 * 
	 * 如向上转型到Object仍无法找到, 返回null.
	 */
	protected static Method getDeclaredMethod(Object object, String methodName, Class<?>[] parameterTypes) {
		if (object == null){
			throw new IllegalArgumentException("object不能为空");
		}
		for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass
				.getSuperclass()) {
			try {
				return superClass.getDeclaredMethod(methodName, parameterTypes);
			} catch (NoSuchMethodException e) {//NOSONAR
				// Method不在当前类定义,继续向上转型
			}
		}
		return null;
	}

	/**
	 * 通过反射, 获得Class定义中声明的父类的泛型参数的类型.
	 * 如无法找到, 返回Object.class.
	 * eg.
	 * public UserDao extends HibernateDao<User>
	 *
	 * @param clazz The class to introspect
	 * @return the first generic declaration, or Object.class if cannot be determined
	 */
	@SuppressWarnings("all")
	public static <T> Class<T> getSuperClassGenricType(final Class clazz) {
		return getSuperClassGenricType(clazz, 0);
	}

	/**
	 * 通过反射, 获得定义Class时声明的父类的泛型参数的类型.
	 * 如无法找到, 返回Object.class.
	 * 
	 * 如public UserDao extends HibernateDao<User,Long>
	 *
	 * @param clazz clazz The class to introspect
	 * @param index the Index of the generic ddeclaration,start from 0.
	 * @return the index generic declaration, or Object.class if cannot be determined
	 */
	@SuppressWarnings("all")
	public static Class getSuperClassGenricType(final Class clazz, final int index) {

		Type genType = clazz.getGenericSuperclass();

		if (!(genType instanceof ParameterizedType)) {
			logger.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType");
			return Object.class;
		}

		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

		if (index >= params.length || index < 0) {
			logger.warn("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: "
					+ params.length);
			return Object.class;
		}
		if (!(params[index] instanceof Class)) {
			logger.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
			return Object.class;
		}

		return (Class) params[index];
	}

	

	/**
	 * 将反射时的checked exception转换为all exception.
	 */
	public static RuntimeException convertReflectionExceptionToall(Exception e) {
		if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException
				|| e instanceof NoSuchMethodException) {
			return new IllegalArgumentException("Reflection Exception.", e);
		} else if (e instanceof InvocationTargetException) {
			return new RuntimeException("Reflection Exception.", ((InvocationTargetException) e).getTargetException());
		} else if (e instanceof RuntimeException) {
			return (RuntimeException) e;
		}
		return new RuntimeException("Unexpected Checked Exception.", e);
	}
	
	
	/**
	 * 通过反射将对象内所有字符串属性值 进行trim() 
	 * @param obj
	 * @return
	 */
	public static Object trim(Object obj){
		return trim(obj, null);
	}
	
	/**
	 * 通过反射将对象内所有字符串属性值 进行trim() 
	 * 暂时无法提供父对象属性的 trim(); 原因是未找到 子对象获取父对象
	 * @param obj
	 * @param escapeList 不需要trim()的属性列表
	 * @return
	 */
	public static Object trim(Object obj, List<String> escapeList){
		if(obj == null)
			return null;
		try {
			Field[] fields =  obj.getClass().getDeclaredFields();
			if(fields != null && fields.length > 0){
				for(Field field : fields){
					if(field.getModifiers() < 15 && field.getType().toString().equals("class java.lang.String")){
						Object val = readField(field, obj, true);
						if(val != null){
							if(escapeList != null && escapeList.indexOf(field.getName()) != -1)
								continue;
							writeField(field, obj, val.toString().trim(), true);
						}
					}
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	
	/**
	 * 实例化类
	 * @param className
	 * @param args
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings("all")
	public static Object newInstance(String className, Object[] args) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Class newoneClass = Class.forName(className);
		Class[] argsClass = new Class[args.length];
		for (int i = 0, j = args.length; i < j; i++) {
			argsClass[i] = args[i].getClass();
		}
		Constructor cons = newoneClass.getConstructor(argsClass);
		return cons.newInstance(args);
	}
	
	/**
	 * 
	 *功能说明: 得到本类物理路径所在文件夹
	 *创建人: wjs
	 *创建时间:2012-10-25 下午4:01:41
	 *@return String
	 *
	 */
	public String getClassPath(){ 
        String strClassName = getClass().getName(); 
        String strPackageName = ""; 
        if(getClass().getPackage() != null) { 
            strPackageName = getClass().getPackage().getName(); 
        } 
        String strClassFileName = ""; 
        if(!"".equals(strPackageName)){
            strClassFileName = strClassName.substring(strPackageName.length() + 1,strClassName.length()); 
        }else { 
            strClassFileName = strClassName; 
        } 
        URL url = null; 
        url = getClass().getResource(strClassFileName + ".class"); 
        String strURL = url.toString();
        strURL = strURL.substring(strURL.indexOf( "/" ) + 1,strURL.lastIndexOf( "/" ));
        return strURL; 
    }
	
	
	 /**
     * Reads an accessible Field.
     * @param field  the field to use
     * @param target  the object to call on, may be null for static fields
     * @return the field value
     * @throws IllegalArgumentException if the field is null
     * @throws IllegalAccessException if the field is not accessible
     */
    public static Object readField(Field field, Object target)  {
        return readField(field, target, true);
    }

    /**
     * Reads a Field.
     * @param field  the field to use
     * @param target  the object to call on, may be null for static fields
     * @param forceAccess  whether to break scope restrictions using the
     *  <code>setAccessible</code> method.
     * @return the field value
     * @throws IllegalArgumentException if the field is null
     * @throws IllegalAccessException if the field is not made accessible
     */
    public static Object readField(Field field, Object target, boolean forceAccess) {
        if (field == null) {
            throw new IllegalArgumentException("The field must not be null");
        }
        if (forceAccess && !field.isAccessible()) {
            field.setAccessible(true);
        } else {
            setAccessibleWorkaround(field);
        }
        try {
			return field.get(target);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
    
    
    static void setAccessibleWorkaround(AccessibleObject o) {
        if (o == null || o.isAccessible()) {
            return;
        }
        Member m = (Member) o;
        if (Modifier.isPublic(m.getModifiers())
                && isPackageAccess(m.getDeclaringClass().getModifiers())) {
            try {
                o.setAccessible(true);
            } catch (SecurityException e) { // NOPMD
                // ignore in favor of subsequent IllegalAccessException
            }
        }
    }

    /**
     * Returns whether a given set of modifiers implies package access.
     * @param modifiers to test
     * @return true unless package/protected/private modifier detected
     */
    static boolean isPackageAccess(int modifiers) {
        return (modifiers & ACCESS_TEST) == 0;
    }

    /**
     * Reads the named public field. Superclasses will be considered.
     * @param target  the object to reflect, must not be null
     * @param fieldName  the field name to obtain
     * @return the value of the field
     * @throws IllegalArgumentException if the class or field name is null
     * @throws IllegalAccessException if the named field is not public
     */
    public static Object readField(Object target, String fieldName) throws IllegalAccessException {
        return readField(target, fieldName, false);
    }

    /**
     * Reads the named field. Superclasses will be considered.
     * @param target  the object to reflect, must not be null
     * @param fieldName  the field name to obtain
     * @param forceAccess  whether to break scope restrictions using the
     *  <code>setAccessible</code> method. <code>False</code> will only
     *  match public fields.
     * @return the field value
     * @throws IllegalArgumentException if the class or field name is null
     * @throws IllegalAccessException if the named field is not made accessible
     */
    public static Object readField(Object target, String fieldName, boolean forceAccess) throws IllegalAccessException {
        if (target == null) {
            throw new IllegalArgumentException("target object must not be null");
        }
        Class<?> cls = target.getClass();
        Field field = getField(cls, fieldName, forceAccess);
        if (field == null) {
            throw new IllegalArgumentException("Cannot locate field " + fieldName + " on " + cls);
        }
        //already forced access above, don't repeat it here:
        return readField(field, target);
    }
    
    /**
     * Gets an accessible <code>Field</code> by name respecting scope.
     * Superclasses/interfaces will be considered.
     *
     * @param cls  the class to reflect, must not be null
     * @param fieldName  the field name to obtain
     * @return the Field object
     * @throws IllegalArgumentException if the class or field name is null
     */
    public static Field getField(Class<?> cls, String fieldName) {
        Field field = getField(cls, fieldName, false);
        setAccessibleWorkaround(field);
        return field;
    }

    /**
     * Gets an accessible <code>Field</code> by name breaking scope
     * if requested. Superclasses/interfaces will be considered.
     *
     * @param cls  the class to reflect, must not be null
     * @param fieldName  the field name to obtain
     * @param forceAccess  whether to break scope restrictions using the
     *  <code>setAccessible</code> method. <code>False</code> will only
     *  match public fields.
     * @return the Field object
     * @throws IllegalArgumentException if the class or field name is null
     */
    public static Field getField(final Class<?> cls, String fieldName, boolean forceAccess) {
        if (cls == null) {
            throw new IllegalArgumentException("The class must not be null");
        }
        if (fieldName == null) {
            throw new IllegalArgumentException("The field name must not be null");
        }
        // Sun Java 1.3 has a bugged implementation of getField hence we write the
        // code ourselves

        // getField() will return the Field object with the declaring class
        // set correctly to the class that declares the field. Thus requesting the
        // field on a subclass will return the field from the superclass.
        //
        // priority order for lookup:
        // searchclass private/protected/package/public
        // superclass protected/package/public
        //  private/different package blocks access to further superclasses
        // implementedinterface public

        // check up the superclass hierarchy
        for (Class<?> acls = cls; acls != null; acls = acls.getSuperclass()) {
            try {
                Field field = acls.getDeclaredField(fieldName);
                // getDeclaredField checks for non-public scopes as well
                // and it returns accurate results
                if (!Modifier.isPublic(field.getModifiers())) {
                    if (forceAccess) {
                        field.setAccessible(true);
                    } else {
                        continue;
                    }
                }
                return field;
            } catch (NoSuchFieldException ex) { // NOPMD
                // ignore
            }
        }
        // check the public interface case. This must be manually searched for
        // incase there is a public supersuperclass field hidden by a private/package
        // superclass field.
        Field match = null;
        for (Class<?> class1 : getAllInterfaces(cls)) {
            try {
                Field test = ((Class<?>) class1).getField(fieldName);
                if (match != null) {
                    throw new IllegalArgumentException("Reference to field " + fieldName + " is ambiguous relative to " + cls
                            + "; a matching field exists on two or more implemented interfaces.");
                }
                match = test;
            } catch (NoSuchFieldException ex) { // NOPMD
                // ignore
            }
        }
        return match;
    }
    
    
    /**
     * Writes an accessible field.
     * @param field to write
     * @param target  the object to call on, may be null for static fields
     * @param value to set
     * @throws IllegalArgumentException if the field is null
     * @throws IllegalAccessException if the field is not accessible or is final
     */
    public static void writeField(Field field, Object target, Object value) throws IllegalAccessException {
        writeField(field, target, value, false);
    }

    /**
     * Writes a field.
     * @param field to write
     * @param target  the object to call on, may be null for static fields
     * @param value to set
     * @param forceAccess  whether to break scope restrictions using the
     *  <code>setAccessible</code> method. <code>False</code> will only
     *  match public fields.
     * @throws IllegalArgumentException if the field is null
     * @throws IllegalAccessException if the field is not made accessible or is final
     */
    public static void writeField(Field field, Object target, Object value, boolean forceAccess)
        throws IllegalAccessException {
        if (field == null) {
            throw new IllegalArgumentException("The field must not be null");
        }
        if (forceAccess && !field.isAccessible()) {
            field.setAccessible(true);
        } else {
            setAccessibleWorkaround(field);
        }
        field.set(target, value);
    }

    /**
     * Writes a public field. Superclasses will be considered.
     * @param target  the object to reflect, must not be null
     * @param fieldName  the field name to obtain
     * @param value to set
     * @throws IllegalArgumentException if <code>target</code> or <code>fieldName</code> is null
     * @throws IllegalAccessException if the field is not accessible
     */
    public static void writeField(Object target, String fieldName, Object value) throws IllegalAccessException {
        writeField(target, fieldName, value, false);
    }

    /**
     * Writes a field. Superclasses will be considered.
     * @param target  the object to reflect, must not be null
     * @param fieldName  the field name to obtain
     * @param value to set
     * @param forceAccess  whether to break scope restrictions using the
     *  <code>setAccessible</code> method. <code>False</code> will only
     *  match public fields.
     * @throws IllegalArgumentException if <code>target</code> or <code>fieldName</code> is null
     * @throws IllegalAccessException if the field is not made accessible
     */
    public static void writeField(Object target, String fieldName, Object value, boolean forceAccess)
            throws IllegalAccessException {
        if (target == null) {
            throw new IllegalArgumentException("target object must not be null");
        }
        Class<?> cls = target.getClass();
        Field field = getField(cls, fieldName, forceAccess);
        if (field == null) {
            throw new IllegalArgumentException("Cannot locate declared field " + cls.getName() + "." + fieldName);
        }
        //already forced access above, don't repeat it here:
        writeField(field, target, value);
    }
    
    public static List<Class<?>> getAllInterfaces(Class<?> cls) {
        if (cls == null) {
            return null;
        }

        LinkedHashSet<Class<?>> interfacesFound = new LinkedHashSet<Class<?>>();
        getAllInterfaces(cls, interfacesFound);

        return new ArrayList<Class<?>>(interfacesFound);
    }

    /**
     * Get the interfaces for the specified class.
     *
     * @param cls  the class to look up, may be {@code null}
     * @param interfacesFound the {@code Set} of interfaces for the class
     */
    private static void getAllInterfaces(Class<?> cls, HashSet<Class<?>> interfacesFound) {
        while (cls != null) {
            Class<?>[] interfaces = cls.getInterfaces();

            for (Class<?> i : interfaces) {
                if (interfacesFound.add(i)) {
                    getAllInterfaces(i, interfacesFound);
                }
            }

            cls = cls.getSuperclass();
         }
     }
    
    /**
     * 得到指定类型的指定位置的泛型实参
     *
     * @param clazz
     * @param index
     * @param <T>
     * @return
     */
    @SuppressWarnings("all")
    public static <T> Class<T> findParameterizedType(Class<?> clazz, int index) {
        Type parameterizedType = clazz.getGenericSuperclass();
        //CGLUB subclass target object(泛型在父类上)
        if (!(parameterizedType instanceof ParameterizedType)) {
            parameterizedType = clazz.getSuperclass().getGenericSuperclass();
        }
        if (!(parameterizedType instanceof  ParameterizedType)) {
            return null;
        }
        Type[] actualTypeArguments = ((ParameterizedType) parameterizedType).getActualTypeArguments();
        if (actualTypeArguments == null || actualTypeArguments.length == 0) {
            return null;
        }
        return (Class<T>) actualTypeArguments[0];
    }

    
    /**
     * 判断一个类是否基础类型
     * @param clazz
     * @return
     * @throws Exception
     */
    public static boolean isBaseDataType(Class<?> clazz) {   
//        return
//        (
//
//            clazz.equals(String.class) ||
//            clazz.equals(Integer.class)||
//            clazz.equals(Byte.class) ||
//            clazz.equals(Long.class) ||
//            clazz.equals(Double.class) ||
//            clazz.equals(Float.class) ||
//            clazz.equals(Character.class) ||
//            clazz.equals(Short.class) ||
//            clazz.equals(BigDecimal.class) ||
//            clazz.equals(BigInteger.class) ||
//            clazz.equals(Boolean.class) ||
//            clazz.equals(Date.class) ||
//            clazz.isPrimitive()
//        );
		return clazz.isPrimitive() || clazz.equals(String.class)|| clazz.getSuperclass().equals(Number.class) || clazz.equals(Date.class);
	}


	/**
	 * 实例化泛型的实际类型参数
	 * http://jisonami.iteye.com/blog/2282650
	 *
	 * @param type
	 * @throws Exception
	 */
	public static Class getActualTypeArguments(Type type) throws Exception{
		logger.debug("该类型是" + type);
		// 参数化类型
		if ( type instanceof ParameterizedType ) {
			Type[] typeArguments = ((ParameterizedType)type).getActualTypeArguments();
			for (int i = 0; i < typeArguments.length; i++) {
				// 类型变量
				if(typeArguments[i] instanceof TypeVariable){
					logger.debug("第" + (i + 1) + "个泛型参数类型是类型变量" + typeArguments[i] + "，无法实例化。");
				}
				// 通配符表达式
				else if(typeArguments[i] instanceof WildcardType){
					logger.debug("第" + (i + 1) + "个泛型参数类型是通配符表达式" + typeArguments[i] + "，无法实例化。");
				}
				// 泛型的实际类型，即实际存在的类型
				else if(typeArguments[i] instanceof Class){
					logger.debug("第" + (i+1) +  "个泛型参数类型是:" + typeArguments[i] + "，可以直接实例化对象");
					return (Class)typeArguments[i];
				}
			}
			// 参数化类型数组或类型变量数组
		} else if ( type instanceof GenericArrayType) {
			logger.debug("该泛型类型是参数化类型数组或类型变量数组，可以获取其原始类型。");
			Type componentType = ((GenericArrayType)type).getGenericComponentType();
			// 类型变量
			if(componentType instanceof TypeVariable){
				logger.debug("该类型变量数组的原始类型是类型变量" + componentType + "，无法实例化。");
			}
			// 参数化类型，参数化类型数组或类型变量数组
			// 参数化类型数组或类型变量数组也可以是多维的数组，getGenericComponentType()方法仅仅是去掉最右边的[]
			else {
				// 递归调用方法自身
				getActualTypeArguments(componentType);
			}
		} else if( type instanceof TypeVariable){
			logger.debug("该类型是类型变量");
		}else if( type instanceof WildcardType){
			logger.debug("该类型是通配符表达式");
		} else if( type instanceof Class ){
			logger.debug("该类型不是泛型类型");
			return (Class) type;
		} else {
			throw new Exception();
		}
		return null;
	}

}
