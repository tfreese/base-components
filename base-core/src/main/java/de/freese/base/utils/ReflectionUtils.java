/**
 * Created: 20.04.2020
 */

package de.freese.base.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Geklaut von org.springframework.util.ReflectionUtils.
 *
 * @author Thomas Freese
 */
public final class ReflectionUtils
{
    /**
     * Callback interface invoked on each field in the hierarchy.
     */
    @FunctionalInterface
    public interface FieldCallback
    {
        /**
         * Perform an operation using the given field.
         *
         * @param field the field to operate on
         * @throws IllegalAccessException Falls was schief geht.
         */
        public void doWith(Field field) throws IllegalAccessException;
    }

    /**
     * Callback optionally used to filter fields to be operated on by a field callback.
     */
    @FunctionalInterface
    public interface FieldFilter
    {
        /**
         * Determine whether the given field matches.
         *
         * @param field the field to check
         * @return boolean
         */
        public boolean matches(Field field);
    }

    /**
     * Action to take on each method.
     */
    @FunctionalInterface
    public interface MethodCallback
    {
        /**
         * Perform an operation using the given method.
         *
         * @param method the method to operate on
         * @throws IllegalAccessException Falls was schief geht.
         */
        public void doWith(Method method) throws IllegalAccessException;
    }

    /**
     * Callback optionally used to filter methods to be operated on by a method callback.
     */
    @FunctionalInterface
    public interface MethodFilter
    {
        /**
         * Determine whether the given method matches.
         *
         * @param method the method to check
         * @return boolean
         */
        public boolean matches(Method method);
    }

    /**
     *
     */
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    /**
     * Pre-built MethodFilter that matches all non-bridge non-synthetic methods which are not declared on {@code java.lang.Object}.
     */
    public static final MethodFilter USER_DECLARED_METHODS = (method -> !method.isBridge() && !method.isSynthetic());

    /**
     * Determine whether the given method explicitly declares the given exception or one of its superclasses, which means that an exception of that type can be
     * propagated as-is within a reflective invocation.
     *
     * @param method the declaring method
     * @param exceptionType the exception to throw
     * @return {@code true} if the exception can be thrown as-is; {@code false} if it needs to be wrapped
     */
    public static boolean declaresException(final Method method, final Class<?> exceptionType)
    {
        Objects.requireNonNull(method, "method required");

        Class<?>[] declaredExceptions = method.getExceptionTypes();

        for (Class<?> declaredException : declaredExceptions)
        {
            if (declaredException.isAssignableFrom(exceptionType))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Invoke the given callback on all fields in the target class, going up the class hierarchy to get all declared fields.
     *
     * @param clazz the target class to analyze
     * @param fieldCallback the callback to invoke for each field
     * @throws IllegalStateException if introspection fails
     */
    public static void doWithFields(final Class<?> clazz, final FieldCallback fieldCallback)
    {
        doWithFields(clazz, fieldCallback, null);
    }

    /**
     * Invoke the given callback on all fields in the target class, going up the class hierarchy to get all declared fields.
     *
     * @param clazz the target class to analyze
     * @param fieldCallback the callback to invoke for each field
     * @param fieldFilter the filter that determines the fields to apply the callback to
     * @throws IllegalStateException if introspection fails
     */
    public static void doWithFields(final Class<?> clazz, final FieldCallback fieldCallback, final FieldFilter fieldFilter)
    {
        // Keep backing up the inheritance hierarchy.
        Class<?> targetClass = clazz;

        do
        {
            Field[] fields = getDeclaredFields(targetClass);

            for (Field field : fields)
            {
                if ((fieldFilter != null) && !fieldFilter.matches(field))
                {
                    continue;
                }

                try
                {
                    fieldCallback.doWith(field);
                }
                catch (IllegalAccessException ex)
                {
                    throw new IllegalStateException("Not allowed to access field '" + field.getName() + "': " + ex);
                }
            }

            targetClass = targetClass.getSuperclass();
        }
        while ((targetClass != null) && (targetClass != Object.class));
    }

    /**
     * Invoke the given callback on all locally declared fields in the given class.
     *
     * @param clazz the target class to analyze
     * @param fieldCallback the callback to invoke for each field
     * @throws IllegalStateException if introspection fails
     * @see #doWithFields
     */
    public static void doWithLocalFields(final Class<?> clazz, final FieldCallback fieldCallback)
    {
        for (Field field : getDeclaredFields(clazz))
        {
            try
            {
                fieldCallback.doWith(field);
            }
            catch (IllegalAccessException ex)
            {
                throw new IllegalStateException("Not allowed to access field '" + field.getName() + "': " + ex);
            }
        }
    }

    /**
     * Perform the given callback operation on all matching methods of the given class and superclasses.
     * <p>
     * The same named method occurring on subclass and superclass will appear twice, unless excluded by a {@link MethodFilter}.
     *
     * @param clazz the class to introspect
     * @param mc the callback to invoke for each method
     * @throws IllegalStateException if introspection fails
     * @see #doWithMethods(Class, MethodCallback, MethodFilter)
     */
    public static void doWithMethods(final Class<?> clazz, final MethodCallback mc)
    {
        doWithMethods(clazz, mc, null);
    }

    /**
     * Perform the given callback operation on all matching methods of the given class and superclasses (or given interface and super-interfaces).
     * <p>
     * The same named method occurring on subclass and superclass will appear twice, unless excluded by the specified {@link MethodFilter}.
     *
     * @param clazz the class to introspect
     * @param mc the callback to invoke for each method
     * @param mf the filter that determines the methods to apply the callback to
     * @throws IllegalStateException if introspection fails
     */
    public static void doWithMethods(final Class<?> clazz, final MethodCallback mc, final MethodFilter mf)
    {
        // Keep backing up the inheritance hierarchy.
        Method[] methods = getDeclaredMethods(clazz);

        for (Method method : methods)
        {
            if ((mf != null) && !mf.matches(method))
            {
                continue;
            }

            try
            {
                mc.doWith(method);
            }
            catch (IllegalAccessException ex)
            {
                throw new IllegalStateException("Not allowed to access method '" + method.getName() + "': " + ex);
            }
        }

        if ((clazz.getSuperclass() != null) && ((mf != USER_DECLARED_METHODS) || (clazz.getSuperclass() != Object.class)))
        {
            doWithMethods(clazz.getSuperclass(), mc, mf);
        }
        else if (clazz.isInterface())
        {
            for (Class<?> superIfc : clazz.getInterfaces())
            {
                doWithMethods(superIfc, mc, mf);
            }
        }
    }

    /**
     * @param clazz the class to introspect
     * @return {@link List}
     */
    private static List<Method> findConcreteMethodsOnInterfaces(final Class<?> clazz)
    {
        List<Method> result = new ArrayList<>();

        for (Class<?> ifc : clazz.getInterfaces())
        {
            for (Method ifcMethod : ifc.getMethods())
            {
                if (!Modifier.isAbstract(ifcMethod.getModifiers()))
                {
                    result.add(ifcMethod);
                }
            }
        }

        return result;
    }

    /**
     * This variant retrieves {@link Class#getDeclaredFields()} from a local cache in order to avoid the JVM's SecurityManager check and defensive array
     * copying.
     *
     * @param clazz the class to introspect
     * @return the array of fields
     * @throws IllegalStateException if introspection fails
     * @see Class#getDeclaredFields()
     */
    private static Field[] getDeclaredFields(final Class<?> clazz)
    {
        Objects.requireNonNull(clazz, "clazz required");

        Field[] fields = null; // declaredFieldsCache.get(clazz);

        if (fields == null)
        {
            try
            {
                fields = clazz.getDeclaredFields();
                // declaredFieldsCache.put(clazz, (fields.length == 0 ? EMPTY_FIELD_ARRAY : fields));
            }
            catch (Exception ex)
            {
                throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() + "] from ClassLoader [" + clazz.getClassLoader() + "]", ex);
            }
        }

        return fields;
    }

    /**
     * @param clazz the class to introspect
     * @return the array of methods
     */
    private static Method[] getDeclaredMethods(final Class<?> clazz)
    {
        Objects.requireNonNull(clazz, "clazz required");

        Method[] methods = null; // declaredMethodsCache.get(clazz);

        if (methods == null)
        {
            try
            {
                Method[] declaredMethods = clazz.getDeclaredMethods();
                List<Method> defaultMethods = findConcreteMethodsOnInterfaces(clazz);

                if (defaultMethods != null)
                {
                    methods = new Method[declaredMethods.length + defaultMethods.size()];
                    System.arraycopy(declaredMethods, 0, methods, 0, declaredMethods.length);
                    int index = declaredMethods.length;

                    for (Method defaultMethod : defaultMethods)
                    {
                        methods[index] = defaultMethod;
                        index++;
                    }
                }
                else
                {
                    methods = declaredMethods;
                }

                // declaredMethodsCache.put(clazz, (methods.length == 0 ? EMPTY_METHOD_ARRAY : methods));
            }
            catch (Exception ex)
            {
                throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() + "] from ClassLoader [" + clazz.getClassLoader() + "]", ex);
            }
        }

        return methods;
    }

    /**
     * Handle the given invocation target exception. Should only be called if no checked exception is expected to be thrown by the target method.
     * <p>
     * Throws the underlying RuntimeException or Error in case of such a root cause. Throws an UndeclaredThrowableException otherwise.
     *
     * @param ex the invocation target exception to handle
     */
    public static void handleInvocationTargetException(final InvocationTargetException ex)
    {
        rethrowRuntimeException(ex.getTargetException());
    }

    /**
     * Handle the given reflection exception.
     * <p>
     * Should only be called if no checked exception is expected to be thrown by a target method, or if an error occurs while accessing a method or field.
     * <p>
     * Throws the underlying RuntimeException or Error in case of an InvocationTargetException with such a root cause. Throws an IllegalStateException with an
     * appropriate message or UndeclaredThrowableException otherwise.
     *
     * @param ex the reflection exception to handle
     */
    public static void handleReflectionException(final Exception ex)
    {
        if (ex instanceof NoSuchMethodException)
        {
            throw new IllegalStateException("Method not found: " + ex.getMessage());
        }

        if (ex instanceof IllegalAccessException)
        {
            throw new IllegalStateException("Could not access method or field: " + ex.getMessage());
        }

        if (ex instanceof InvocationTargetException)
        {
            handleInvocationTargetException((InvocationTargetException) ex);
        }

        if (ex instanceof RuntimeException)
        {
            throw (RuntimeException) ex;
        }

        throw new UndeclaredThrowableException(ex);
    }

    /**
     * Invoke the specified {@link Method} against the supplied target object with no arguments. The target object can be {@code null} when invoking a static
     * {@link Method}.
     * <p>
     * Thrown exceptions are handled via a call to {@link #handleReflectionException}.
     *
     * @param method the method to invoke
     * @param target the target object to invoke the method on
     * @return the invocation result, if any
     * @see #invokeMethod(java.lang.reflect.Method, Object, Object[])
     */
    public static Object invokeMethod(final Method method, final Object target)
    {
        return invokeMethod(method, target, EMPTY_OBJECT_ARRAY);
    }

    /**
     * Invoke the specified {@link Method} against the supplied target object with the supplied arguments. The target object can be {@code null} when invoking a
     * static {@link Method}.
     * <p>
     * Thrown exceptions are handled via a call to {@link #handleReflectionException}.
     *
     * @param method the method to invoke
     * @param target the target object to invoke the method on
     * @param args the invocation arguments (may be {@code null})
     * @return the invocation result, if any
     */
    public static Object invokeMethod(final Method method, final Object target, final Object...args)
    {
        try
        {
            return method.invoke(target, args);
        }
        catch (Exception ex)
        {
            handleReflectionException(ex);
        }

        throw new IllegalStateException("Should never get here");
    }

    /**
     * Make the given field accessible, explicitly setting it accessible if necessary. The {@code setAccessible(true)} method is only called when actually
     * necessary, to avoid unnecessary conflicts with a JVM SecurityManager (if active).
     *
     * @param field the field to make accessible
     * @see java.lang.reflect.Field#setAccessible
     */
    @SuppressWarnings("deprecation")  // on JDK 9
    public static void makeAccessible(final Field field)
    {
        if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || Modifier.isFinal(field.getModifiers()))
                && !field.isAccessible())
        {
            field.setAccessible(true);
        }
    }

    /**
     * Make the given method accessible, explicitly setting it accessible if necessary. The {@code setAccessible(true)} method is only called when actually
     * necessary, to avoid unnecessary conflicts with a JVM SecurityManager (if active).
     *
     * @param method the method to make accessible
     * @see java.lang.reflect.Method#setAccessible
     */
    @SuppressWarnings("deprecation")  // on JDK 9
    public static void makeAccessible(final Method method)
    {
        if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !method.isAccessible())
        {
            method.setAccessible(true);
        }
    }

    /**
     * Rethrow the given {@link Throwable exception}, which is presumably the <em>target exception</em> of an {@link InvocationTargetException}. Should only be
     * called if no checked exception is expected to be thrown by the target method.
     * <p>
     * Rethrows the underlying exception cast to a {@link RuntimeException} or {@link Error} if appropriate; otherwise, throws an
     * {@link UndeclaredThrowableException}.
     *
     * @param ex the exception to rethrow
     * @throws RuntimeException the rethrown exception
     */
    public static void rethrowRuntimeException(final Throwable ex)
    {
        if (ex instanceof RuntimeException)
        {
            throw (RuntimeException) ex;
        }

        if (ex instanceof Error)
        {
            throw (Error) ex;
        }

        throw new UndeclaredThrowableException(ex);
    }

    /**
     * Erstellt ein neues {@link ReflectionUtils} Object.
     */
    private ReflectionUtils()
    {
        super();
    }
}
