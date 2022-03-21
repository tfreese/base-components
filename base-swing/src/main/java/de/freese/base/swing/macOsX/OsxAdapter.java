/*
 * File: OsxAdapter.java Abstract: Hooks existing preferences/about/quit functionality from an existing Java app into handlers for the Mac OS X application
 * menu. Uses a Proxy object to dynamically implement the com.apple.eawt.ApplicationListener interface and register it with the com.apple.eawt.Application
 * object. This allows the complete project to be both built and run on any platform without any stubs or placeholders. Useful for developers looking to
 * implement Mac OS X features while supporting multiple platforms with minimal impact. Version: 2.0 Disclaimer: IMPORTANT: This Apple software is supplied to
 * you by Apple Inc. ("Apple") in consideration of your agreement to the following terms, and your use, installation, modification or redistribution of this
 * Apple software constitutes acceptance of these terms. If you do not agree with these terms, please do not use, install, modify or redistribute this Apple
 * software. In consideration of your agreement to abide by the following terms, and subject to these terms, Apple grants you a personal, non-exclusive license,
 * under Apple's copyrights in this original Apple software (the "Apple Software"), to use, reproduce, modify and redistribute the Apple Software, with or
 * without modifications, in source and/or binary forms; provided that if you redistribute the Apple Software in its entirety and without modifications, you
 * must retain this notice and the following text and disclaimers in all such redistributions of the Apple Software. Neither the name, trademarks, service marks
 * or logos of Apple Inc. may be used to endorse or promote products derived from the Apple Software without specific prior written permission from Apple.
 * Except as expressly stated in this notice, no other rights or licenses, express or implied, are granted by Apple herein, including but not limited to any
 * patent rights that may be infringed by your derivative works or by other works in which the Apple Software may be incorporated. The Apple Software is
 * provided by Apple on an "AS IS" basis. APPLE MAKES NO WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE IMPLIED WARRANTIES OF
 * NON-INFRINGEMENT, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, REGARDING THE APPLE SOFTWARE OR ITS USE AND OPERATION ALONE OR IN COMBINATION WITH
 * YOUR PRODUCTS. IN NO EVENT SHALL APPLE BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) ARISING IN ANY WAY OUT OF THE USE, REPRODUCTION, MODIFICATION
 * AND/OR DISTRIBUTION OF THE APPLE SOFTWARE, HOWEVER CAUSED AND WHETHER UNDER THEORY OF CONTRACT, TORT (INCLUDING NEGLIGENCE), STRICT LIABILITY OR OTHERWISE,
 * EVEN IF APPLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. Copyright 2003-2007 Apple, Inc., All Rights Reserved
 */
package de.freese.base.swing.macOsX;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter für Mac OS Menüs.
 *
 * @author Thomas Freese
 */
public class OsxAdapter implements InvocationHandler
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OsxAdapter.class);
    /**
     *
     */
    static Object macOSXApplication;

    /**
     * Pass this method an Object and Method equipped to display application info.<br>
     * They will be called when the About menu item is selected from the application menu.
     * <p/>
     *
     * @param target Object
     * @param aboutHandler {@link Method}
     */
    public static void setAboutHandler(final Object target, final Method aboutHandler)
    {
        boolean enableAboutMenu = ((target != null) && (aboutHandler != null));

        if (enableAboutMenu)
        {
            setHandler(new OsxAdapter("handleAbout", target, aboutHandler));
        }

        // If we're setting a handler, enable the About menu item by calling
        // com.apple.eawt.Application reflectively
        try
        {
            Method enableAboutMethod = OsxAdapter.macOSXApplication.getClass().getDeclaredMethod("setEnabledAboutMenu", boolean.class);
            enableAboutMethod.invoke(OsxAdapter.macOSXApplication, enableAboutMenu);
        }
        catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
        {
            OsxAdapter.LOGGER.error("OsxAdapter could not access the About Menu", ex);
        }
    }

    /**
     * Pass this method an Object and a Method equipped to handle document events from the Finder.<br>
     * Documents are registered with the Finder via the CFBundleDocumentTypes dictionary in the<br>
     * application bundle's Info.plist
     * <p/>
     *
     * @param target Object
     * @param fileHandler {@link Method}
     */
    public static void setFileHandler(final Object target, final Method fileHandler)
    {
        setHandler(new OsxAdapter("handleOpenFile", target, fileHandler)
        {
            /**
             * @see OsxAdapter#callTarget(java.lang.Object)
             */
            @Override
            public boolean callTarget(final Object appleEvent)
            {
                if (appleEvent != null)
                {
                    try
                    {
                        Method getFilenameMethod = appleEvent.getClass().getDeclaredMethod("getFilename", (Class[]) null);
                        String filename = (String) getFilenameMethod.invoke(appleEvent, (Object[]) null);
                        this.targetMethod.invoke(this.targetObject, filename);
                    }
                    catch (Exception ex)
                    {
                        // Empty
                    }
                }

                return true;
            }
        });
    }

    /**
     * setHandler creates a Proxy object from the passed OsxAdapter and adds it as an ApplicationListener
     * <p/>
     *
     * @param adapter {@link OsxAdapter}
     */
    public static void setHandler(final OsxAdapter adapter)
    {
        try
        {
            Class<?> applicationClass = Class.forName("com.apple.eawt.Application");

            if (OsxAdapter.macOSXApplication == null)
            {
                OsxAdapter.macOSXApplication = applicationClass.getConstructor((Class[]) null).newInstance((Object[]) null);
            }

            Class<?> applicationListenerClass = Class.forName("com.apple.eawt.ApplicationListener");
            Method addListenerMethod = applicationClass.getDeclaredMethod("addApplicationListener", applicationListenerClass);

            // Create a proxy object around this handler that can be reflectively added as an Apple
            // ApplicationListener
            Object osxAdapterProxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]
                    {
                            applicationListenerClass
                    }, adapter);
            addListenerMethod.invoke(OsxAdapter.macOSXApplication, osxAdapterProxy);
        }
        catch (ClassNotFoundException ex)
        {
            OsxAdapter.LOGGER.error("This version of Mac OS X does not support the Apple EAWT.  ApplicationEvent handling has been disabled.", ex);
        }
        catch (Exception ex)
        {
            // Likely a NoSuchMethodException or an IllegalAccessException loading/invoking
            // eawt.Application methods
            OsxAdapter.LOGGER.error("Mac OS X Adapter could not talk to EAWT:", ex);
        }
    }

    /**
     * Pass this method an Object and a Method equipped to display application options.<br>
     * They will be called when the Preference's menu item is selected from the application menu..
     * <p/>
     *
     * @param target Object
     * @param prefsHandler {@link Method}
     */
    public static void setPreferencesHandler(final Object target, final Method prefsHandler)
    {
        boolean enablePrefsMenu = ((target != null) && (prefsHandler != null));

        if (enablePrefsMenu)
        {
            setHandler(new OsxAdapter("handlePreferences", target, prefsHandler));
        }

        // If we're setting a handler, enable the Preference's menu item by calling
        // com.apple.eawt.Application reflectively
        try
        {
            Method enablePrefsMethod = OsxAdapter.macOSXApplication.getClass().getDeclaredMethod("setEnabledPreferencesMenu", boolean.class);
            enablePrefsMethod.invoke(OsxAdapter.macOSXApplication, enablePrefsMenu);
        }
        catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
        {
            OsxAdapter.LOGGER.error("OsxAdapter could not access the About Menu", ex);
        }
    }

    /**
     * Pass this method an Object and Method equipped to perform application shutdown logic.<br>
     * The method passed should return a boolean stating whether or not the quit should occur.
     * <p/>
     *
     * @param target Object
     * @param quitHandler {@link Method}
     */
    public static void setQuitHandler(final Object target, final Method quitHandler)
    {
        setHandler(new OsxAdapter("handleQuit", target, quitHandler));
    }

    /**
     *
     */
    protected String proxySignature;
    /**
     *
     */
    protected Method targetMethod;
    /**
     *
     */
    protected Object targetObject;

    /**
     * Each OsxAdapter has the name of the EAWT method it intends to listen for (handleAbout, forexample),<br>
     * the Object that will ultimately perform the task, and the Method to be called on that Object Erstellt ein neues {@link OsxAdapter} Object.
     * <p/>
     *
     * @param proxySignature String
     * @param target Object
     * @param handler {@link Method}
     */
    protected OsxAdapter(final String proxySignature, final Object target, final Method handler)
    {
        super();

        this.proxySignature = proxySignature;
        this.targetObject = target;
        this.targetMethod = handler;
    }

    /**
     * Override this method to perform any operations on the event<br>
     * that comes with the various callbacks.<br>
     * See setFileHandler above for an example.
     * <p/>
     *
     * @param appleEvent Object
     * <p/>
     *
     * @return boolean
     * <p/>
     *
     * @throws Exception Falls was schief geht.
     */
    public boolean callTarget(final Object appleEvent) throws Exception
    {
        Object result = this.targetMethod.invoke(this.targetObject, (Object[]) null);

        if (result == null)
        {
            return true;
        }

        return Boolean.parseBoolean(result.toString());
    }

    /**
     * InvocationHandler implementation.<br>
     * This is the entry point for our proxy object; it is called every time an ApplicationListener<br>
     * method is invoked.
     * <p/>
     *
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
    {
        if (isCorrectMethod(method, args))
        {
            boolean handled = callTarget(args[0]);
            setApplicationEventHandled(args[0], handled);
        }

        // All of the ApplicationListener methods are void; return null regardless of what happens
        return null;
    }

    /**
     * Compare the method that was called to the intended method when the OsxAdapter instance was<br>
     * created (e.g. handleAbout, handleQuit, handleOpenFile, etc.).
     * <p/>
     *
     * @param method {@link Method}
     * @param args Object[]
     * <p/>
     *
     * @return boolean
     */
    protected boolean isCorrectMethod(final Method method, final Object[] args)
    {
        return ((this.targetMethod != null) && this.proxySignature.equals(method.getName()) && (args.length == 1));
    }

    /**
     * It is important to mark the ApplicationEvent as handled and cancel the default behavior.<br>
     * This method checks for a boolean result from the proxy method and sets the event accordingly.
     * <p/>
     *
     * @param event Object
     * @param handled boolean
     */
    protected void setApplicationEventHandled(final Object event, final boolean handled)
    {
        if (event != null)
        {
            try
            {
                Method setHandledMethod = event.getClass().getDeclaredMethod("setHandled", boolean.class);
                // If the target method returns a boolean, use that as a hint
                setHandledMethod.invoke(event, handled);
            }
            catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
            {
                System.err.println("OsxAdapter was unable to handle an ApplicationEvent: " + event);
                ex.printStackTrace();
            }
        }
    }
}
