// Created: 02.07.2009
package de.freese.base.utils;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import jakarta.persistence.metamodel.Metamodel;

import org.hibernate.Cache;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.hibernate.stat.CacheRegionStatistics;
import org.hibernate.stat.EntityStatistics;
import org.hibernate.stat.Statistics;
import org.slf4j.Logger;

/**
 * @author Thomas Freese
 */
public final class HibernateUtils {
    public static void clearCache(final SessionFactory sessionFactory, final String name, final Logger logger) {
        logInfo(logger, String.format("Clear Cache: %s", name));

        Cache cache = sessionFactory.getCache();

        try {
            logInfo(logger, "evictDefaultQueryRegion");
            cache.evictDefaultQueryRegion();

            logInfo(logger, "evictEntityData");
            cache.evictEntityData();

            logInfo(logger, "evictCollectionData");
            cache.evictCollectionData();

            logInfo(logger, "evictNaturalIdData");
            cache.evictNaturalIdData();

            try {
                logInfo(logger, "evict QueryRegions");
                cache.evictQueryRegions();
            }
            catch (NullPointerException ex) {
                logWarn(logger, "evict QueryRegions: NullPointerException");
            }
            catch (Exception ex) {
                logWarn(logger, ex.getMessage());
            }

            logInfo(logger, "evict Statistics");
            sessionFactory.getStatistics().clear();
        }
        catch (Exception ex) {
            logWarn(logger, ex);
        }
    }

    /**
     * Liefert das konkrete Objekt hinter dem {@link HibernateProxy}.
     *
     * @param maybeProxy möglicher {@link HibernateProxy}
     *
     * @see HibernateProxy
     * @see LazyInitializer
     */
    public static <T> T deProxy(final Object maybeProxy) throws ClassCastException {
        Class<T> baseClass = getClassFromProxy(maybeProxy);

        return deProxy(maybeProxy, baseClass);
    }

    /**
     * Liefert das konkrete Objekt hinter dem {@link HibernateProxy}.
     *
     * @param maybeProxy möglicher {@link HibernateProxy}
     * @param baseClass Klasse für den cast
     *
     * @see HibernateProxy
     * @see LazyInitializer
     */
    public static <T> T deProxy(final Object maybeProxy, final Class<T> baseClass) throws ClassCastException {
        if (maybeProxy instanceof HibernateProxy hibernateProxy) {
            LazyInitializer initializer = hibernateProxy.getHibernateLazyInitializer();

            return baseClass.cast(initializer.getImplementation());
        }

        return baseClass.cast(maybeProxy);
    }

    /**
     * Liefert die Klasse des Objektes hinter dem {@link HibernateProxy}.
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClassFromProxy(final Object maybeProxy) {
        // HibernateProxyHelper
        return (Class<T>) getClassWithoutInitializingProxy(maybeProxy);
    }

    @SuppressWarnings("deprecation")
    public static void getPersistenceStatistics(final SessionFactory sessionFactory, final PrintWriter pw, final Logger logger) {
        logInfo(logger, String.format("Read PersistenceStatistics: %s", sessionFactory.getSessionFactoryOptions().getSessionFactoryName()));

        pw.println("----------------------------------------------");
        pw.println(String.format("PersistenceStatistics: %s", sessionFactory.getSessionFactoryOptions().getSessionFactoryName()));
        pw.println("----------------------------------------------");
        pw.println();

        try {
            Statistics stats = sessionFactory.getStatistics();

            pw.println("Statistics enabled......: " + stats.isStatisticsEnabled());
            pw.println();

            final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // Allgemeine Statistiken
            pw.println("Start Date..............: " + DATE_TIME_FORMATTER.format(stats.getStart()));
            pw.println("Current Date............: " + DATE_TIME_FORMATTER.format(LocalDateTime.now()));
            pw.println();
            pw.println("PreparedStatement Count : " + stats.getPrepareStatementCount());
            pw.println("Session open Count......: " + stats.getSessionOpenCount());
            pw.println("Session close Count.....: " + stats.getSessionCloseCount());

            long txCount = stats.getTransactionCount();
            long successfulTxCount = stats.getSuccessfulTransactionCount();

            // Hibernate Bug: Wenn der TxManager kein JTA TransactionManager ist,
            // werden die Tx doppelt gezählt (txCount = 2 * successfulTxCount).
            if (txCount >= (2 * successfulTxCount)) {
                txCount -= successfulTxCount;
            }

            pw.println("Begin SimpleTransaction Count : " + txCount);
            pw.println("Commit SimpleTransaction Count: " + successfulTxCount);
            pw.println();

            double hitCount = 0D;
            double missCount = 0D;
            double hitRatio = 0D;

            try {
                // Globaler 2nd lvl Cache
                hitCount = stats.getSecondLevelCacheHitCount();
                missCount = stats.getSecondLevelCacheMissCount();
                hitRatio = hitCount / (hitCount + missCount);

                pw.println("Second Cache Hit Count...: " + hitCount);
                pw.println("Second Cache Miss Count..: " + missCount);
                pw.println("Second Cache Hit ratio[%]: " + round(hitRatio * 100D, 3));
                pw.println();
            }
            catch (Exception ex) {
                logErr(logger, ex);
                pw.println();
                ex.printStackTrace(pw);
            }

            try {
                // Globaler Query Cache
                hitCount = stats.getQueryCacheHitCount();
                missCount = stats.getQueryCacheMissCount();
                hitRatio = hitCount / (hitCount + missCount);

                pw.println("SQL Query Hit Count...: " + hitCount);
                pw.println("SQL Query Miss Count..: " + missCount);
                pw.println("SQL Query Hit ratio[%]: " + round(hitRatio * 100D, 3));
                pw.println();
            }
            catch (Exception ex) {
                logErr(logger, ex);
                pw.println();
                ex.printStackTrace(pw);
            }

            try {
                // Cache-Regionen
                String[] cacheRegions = stats.getSecondLevelCacheRegionNames();

                Arrays.sort(cacheRegions);

                for (String cacheRegion : cacheRegions) {
                    CacheRegionStatistics cacheStatistics = stats.getDomainDataRegionStatistics(cacheRegion);

                    hitCount = cacheStatistics.getHitCount();
                    missCount = cacheStatistics.getMissCount();
                    hitRatio = hitCount / (hitCount + missCount);

                    pw.println("Cache Region.........: " + cacheRegion);
                    pw.println("Objects in Memory....: " + cacheStatistics.getElementCountInMemory());
                    pw.println("Objects in Memory[MB]: " + round(cacheStatistics.getSizeInMemory() / 1024D / 1024D, 3));
                    pw.println("Hit Count............: " + hitCount);
                    pw.println("Miss Count...........: " + missCount);
                    pw.println("Hit ratio[%].........: " + round(hitRatio * 100D, 3));
                    pw.println();
                }
            }
            catch (Exception ex) {
                logErr(logger, ex);
                pw.println();
                ex.printStackTrace(pw);
            }

            if (stats.isStatisticsEnabled()) {
                // Objektspezifische Statistiken
                // Map<String, ?> metaData = sessionFactory.getAllClassMetadata();
                // .collect(Collectors.toCollection(TreeSet::new))
                Metamodel metamodel = sessionFactory.getMetamodel();

                // @formatter:off
                metamodel.getEntities().stream()
                    .map(entityType -> entityType.getJavaType().getName())
                    .sorted()
                    .forEach(className -> {
                    try
                    {
                        EntityStatistics entityStats = stats.getEntityStatistics(className);

                        long inserts = entityStats.getInsertCount();
                        long updates = entityStats.getUpdateCount();
                        long deletes = entityStats.getDeleteCount();
                        long fetches = entityStats.getFetchCount();
                        long loads = entityStats.getLoadCount();
                        long changes = inserts + updates + deletes;

                        pw.println(className + " fetches " + fetches + " times");
                        pw.println(className + " loads   " + loads + " times");
                        pw.println(className + " inserts " + inserts + " times");
                        pw.println(className + " updates " + updates + " times");
                        pw.println(className + " deletes " + deletes + " times");
                        pw.println(className + " changed " + changes + " times");
                        pw.println();
                    }
                    catch (Exception ex)
                    {
                        logErr(logger, ex);
                        pw.println();
                        ex.printStackTrace(pw);
                    }
                });
                // @formatter:on
            }
        }
        catch (Exception ex) {
            logErr(logger, ex);
            pw.println();
            ex.printStackTrace(pw);
        }
    }

    /**
     * Force initialization of a proxy or persistent collection.<br>
     * Note: This only ensures initialization of a proxy object or collection.<br>
     * It is not guaranteed that the elements INSIDE the collection will be initialized/materialized.
     *
     * @see Hibernate#isInitialized(Object)
     * @see Hibernate#initialize(Object)
     */
    public static void initialize(final Object maybeProxy) {
        if (!Hibernate.isInitialized(maybeProxy)) {
            Hibernate.initialize(maybeProxy);
        }
    }

    /**
     * Get the class of an instance or the underlying class
     * of a proxy (without initializing the proxy!). It is
     * almost always better to use the entity name!
     */
    private static Class<?> getClassWithoutInitializingProxy(Object object) {
        if (object instanceof HibernateProxy hibernateProxy) {
            LazyInitializer initializer = hibernateProxy.getHibernateLazyInitializer();

            return initializer.getPersistentClass();
        }
        else {
            return object.getClass();
        }
    }

    private static void logErr(final Logger logger, final Throwable th) {
        if (logger != null) {
            logger.error(th.getMessage(), th);
        }
    }

    private static void logInfo(final Logger logger, final String message) {
        if (logger != null) {
            logger.info(message);
        }
    }

    private static void logWarn(final Logger logger, final String message) {
        if (logger != null) {
            logger.warn(message);
        }
    }

    private static void logWarn(final Logger logger, final Throwable th) {
        if (logger != null) {
            logger.warn(null, th);
        }
    }

    /**
     * Rundet ein Double Wert auf eine bestimmte Anzahl Nachkommastellen.<br>
     * Ist der Wert NaN oder Infinite, wird 0.0D geliefert.
     *
     * @param scale int Anzahl Nachkommastellen
     */
    private static double round(final double value, final int scale) {
        if (Double.isNaN(value) || Double.isInfinite(value) || (Double.compare(value, 0.0D) == 0)) {
            return 0.0D;
        }

        BigDecimal bigDecimal = BigDecimal.valueOf(value).setScale(scale, RoundingMode.HALF_UP);

        return bigDecimal.doubleValue();
    }

    private HibernateUtils() {
        super();
    }
}
