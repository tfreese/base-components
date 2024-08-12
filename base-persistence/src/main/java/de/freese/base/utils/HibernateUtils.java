// Created: 02.07.2009
package de.freese.base.utils;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.hibernate.Cache;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.JdbcSettings;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.hibernate.stat.Statistics;
import org.slf4j.Logger;

/**
 * @author Thomas Freese
 */
public final class HibernateUtils {
    public static void clearCache(final SessionFactory sessionFactory, final String name, final Logger logger) {
        logger.info("Clear Cache: {}", name);

        final Cache cache = sessionFactory.getCache();

        try {
            logger.info("evictDefaultQueryRegion");
            cache.evictDefaultQueryRegion();

            logger.info("evictEntityData");
            cache.evictEntityData();

            logger.info("evictCollectionData");
            cache.evictCollectionData();

            logger.info("evictNaturalIdData");
            cache.evictNaturalIdData();

            try {
                logger.info("evict QueryRegions");
                cache.evictQueryRegions();
            }
            catch (Exception ex) {
                logger.warn(ex.getMessage());
            }

            logger.info("evict Statistics");
            sessionFactory.getStatistics().clear();
        }
        catch (Exception ex) {
            logger.warn(ex.getMessage());
        }
    }

    /**
     * Liefert das konkrete Objekt hinter dem {@link HibernateProxy}.
     *
     * @param maybeProxy möglicher {@link HibernateProxy}
     */
    public static <T> T deProxy(final Object maybeProxy) throws ClassCastException {
        final Class<T> baseClass = getClassFromProxy(maybeProxy);

        return deProxy(maybeProxy, baseClass);
    }

    /**
     * Liefert das konkrete Objekt hinter dem {@link HibernateProxy}.
     *
     * @param maybeProxy möglicher {@link HibernateProxy}
     * @param baseClass Klasse für den cast
     */
    public static <T> T deProxy(final Object maybeProxy, final Class<T> baseClass) throws ClassCastException {
        if (maybeProxy instanceof HibernateProxy hibernateProxy) {
            final LazyInitializer initializer = hibernateProxy.getHibernateLazyInitializer();

            return baseClass.cast(initializer.getImplementation());
        }

        return baseClass.cast(maybeProxy);
    }

    public static void dumpStatistics(final PrintWriter pw, final SessionFactory sessionFactory) {
        Object jdbcUrl = null;

        for (String key : List.of(JdbcSettings.JAKARTA_JDBC_URL, JdbcSettings.JAKARTA_JTA_DATASOURCE, JdbcSettings.JAKARTA_NON_JTA_DATASOURCE)) {
            jdbcUrl = sessionFactory.getProperties().get(key);

            if (jdbcUrl != null) {
                break;
            }
        }

        pw.println("----------------------------------------------");
        pw.printf("PersistenceStatistics: %s%n", jdbcUrl);
        pw.println("----------------------------------------------");
        pw.println();

        final Statistics stats = sessionFactory.getStatistics();

        pw.println("Statistics enabled......: " + stats.isStatisticsEnabled());
        pw.println();

        pw.println("Start Date..............: " + stats.getStart());
        pw.println("Current Date............: " + LocalDateTime.now());
        pw.println();
        pw.println("PreparedStatement Count : " + stats.getPrepareStatementCount());
        pw.println("Session open Count......: " + stats.getSessionOpenCount());
        pw.println("Session close Count.....: " + stats.getSuccessfulTransactionCount());
        pw.println("Begin Transaction Count : " + stats.getTransactionCount());
        pw.println("Commit Transaction Count: " + stats.getSuccessfulTransactionCount());

        pw.println();
        pw.println("Query Cache");
        long hitCount = stats.getQueryCacheHitCount();
        long missCount = stats.getQueryCacheMissCount();
        double hitRatio = (double) hitCount / (double) (hitCount + missCount);

        if (Double.isNaN(hitRatio) || Double.isInfinite(hitRatio)) {
            hitRatio = 0D;
        }

        pw.println("SQL Query Hit Count..: " + hitCount);
        pw.println("SQL Query Miss Count.: " + missCount);
        pw.println("SQL Query Hit ratio %: " + BigDecimal.valueOf(hitRatio * 100D).setScale(3, RoundingMode.HALF_UP));

        pw.println();
        pw.println("2nd Level Cache");
        hitCount = stats.getSecondLevelCacheHitCount();
        missCount = stats.getSecondLevelCacheMissCount();
        hitRatio = (double) hitCount / (double) (hitCount + missCount);

        if (Double.isNaN(hitRatio) || Double.isInfinite(hitRatio)) {
            hitRatio = 0D;
        }

        pw.println("2nd Level Cache Hit Count...: " + hitCount);
        pw.println("2nd Level Cache Miss Count..: " + missCount);
        pw.println("2nd Level Cache Hit ratio[%]: " + BigDecimal.valueOf(hitRatio * 100D).setScale(3, RoundingMode.HALF_UP));

        pw.println();
        pw.println("2nd Level Cache-Regions");
        Stream.of(stats.getSecondLevelCacheRegionNames()).sorted().map(stats::getCacheRegionStatistics).filter(Objects::nonNull).forEach(cacheStatistics -> {
            final long hCount = cacheStatistics.getHitCount();
            final long mCount = cacheStatistics.getMissCount();
            double hRatio = (double) hCount / (double) (hCount + mCount);

            if (Double.isNaN(hRatio) || Double.isInfinite(hRatio)) {
                hRatio = 0D;
            }

            pw.println("Cache Region.........: " + cacheStatistics.getRegionName());
            pw.println("Objects in Memory....: " + cacheStatistics.getElementCountInMemory());
            pw.println("Objects in Memory[MB]: " + BigDecimal.valueOf(cacheStatistics.getSizeInMemory() / 1024D / 1024D).setScale(3, RoundingMode.HALF_UP));
            pw.println("Hit Count............: " + hCount);
            pw.println("Miss Count...........: " + mCount);
            pw.println("Hit ratio[%].........: " + BigDecimal.valueOf(hRatio * 100D).setScale(3, RoundingMode.HALF_UP));
            pw.println();
        });

        pw.println();
        pw.println("CollectionStatistics");
        Stream.of(stats.getCollectionRoleNames()).sorted().map(stats::getCollectionStatistics).filter(Objects::nonNull).forEach(collectionStatistics -> {
            final long hCount = collectionStatistics.getCacheHitCount();
            final long mCount = collectionStatistics.getCacheMissCount();
            double hRatio = (double) hCount / (double) (hCount + mCount);

            if (Double.isNaN(hRatio) || Double.isInfinite(hRatio)) {
                hRatio = 0D;
            }

            pw.println("Cache Region: " + collectionStatistics.getCacheRegionName());

            pw.println("Hit Count...: " + hCount);
            pw.println("Miss Count..: " + mCount);
            pw.println("Hit ratio[%]: " + BigDecimal.valueOf(hRatio * 100D).setScale(3, RoundingMode.HALF_UP));

            pw.println("Puts...: " + collectionStatistics.getCachePutCount());
            pw.println("Fetches: " + collectionStatistics.getFetchCount());
            pw.println("Loads..: " + collectionStatistics.getLoadCount());
            pw.println("Updates: " + collectionStatistics.getUpdateCount());
            pw.println();
        });

        pw.println();
        pw.println("QueryRegionStatistics");
        Stream.of(stats.getQueries()).sorted().map(stats::getQueryRegionStatistics).filter(Objects::nonNull).forEach(cacheRegionStatistics -> {
            final long hCount = cacheRegionStatistics.getHitCount();
            final long mCount = cacheRegionStatistics.getMissCount();
            double hRatio = (double) hCount / (double) (hCount + mCount);

            if (Double.isNaN(hRatio) || Double.isInfinite(hRatio)) {
                hRatio = 0D;
            }

            pw.println("Cache Region.........: " + cacheRegionStatistics.getRegionName());
            pw.println("Objects in Memory....: " + cacheRegionStatistics.getElementCountInMemory());
            pw.println("Objects in Memory[MB]: " + (cacheRegionStatistics.getSizeInMemory() / 1024D / 1024D));
            pw.println("Hit Count............: " + hCount);
            pw.println("Miss Count...........: " + mCount);
            pw.println("Hit ratio[%].........: " + BigDecimal.valueOf(hRatio * 100D).setScale(3, RoundingMode.HALF_UP));
            pw.println();
        });

        pw.println();
        pw.println("EntityStatistics");
        Stream.of(stats.getEntityNames()).sorted().map(stats::getEntityStatistics).filter(Objects::nonNull).forEach(entityStatistics -> {
            final long hCount = entityStatistics.getCacheHitCount();
            final long mCount = entityStatistics.getCacheMissCount();
            double hRatio = (double) hCount / (double) (hCount + mCount);

            if (Double.isNaN(hRatio) || Double.isInfinite(hRatio)) {
                hRatio = 0D;
            }

            pw.println("Cache Region: " + entityStatistics.getCacheRegionName());
            pw.println("Hit Count...: " + hCount);
            pw.println("Miss Count..: " + mCount);
            pw.println("Hit ratio[%]: " + BigDecimal.valueOf(hRatio * 100D).setScale(3, RoundingMode.HALF_UP));
            pw.println("Fetches: " + entityStatistics.getFetchCount());
            pw.println("Loads..: " + entityStatistics.getLoadCount());
            pw.println("Inserts: " + entityStatistics.getInsertCount());
            pw.println("Updates: " + entityStatistics.getUpdateCount());
            pw.println("Deletes: " + entityStatistics.getDeleteCount());
            pw.println();
        });

        // final Metamodel metamodel = sessionFactory.getMetamodel();
        // final Metamodel metamodel = ((SessionFactoryImplementor) sessionFactory).getMetamodel();
        // final Map<String, ClassMetadata> classMetadata = sessionFactory.getAllClassMetadata();
        //
        // Sort by Class name.
        // classMetadata.values().stream()
        //     .map(cmd -> cmd.getEntityName())

        // metamodel.getEntities().stream()
        //     .map(entityType -> entityType.getJavaType().getName())
        //     .sorted()
        //     .forEach(className -> {

        //        final Cache cache = sessionFactory.getCache();
        //        final CacheImplementor cacheImplementor = (CacheImplementor) cache;
        //        final RegionFactory regionFactory = cacheImplementor.getRegionFactory();
        //        final JCacheRegionFactory jCacheRegionFactory = (JCacheRegionFactory) regionFactory;
        //        final CacheManager cacheManager = jCacheRegionFactory.getCacheManager();

        pw.println();
        pw.flush();
    }

    /**
     * Liefert die Klasse des Objektes hinter dem {@link HibernateProxy}.
     */
    public static <T> Class<T> getClassFromProxy(final Object maybeProxy) {
        // HibernateProxyHelper
        return (Class<T>) getClassWithoutInitializingProxy(maybeProxy);
    }

    /**
     * Force initialization of a proxy or persistent collection.<br>
     * Note: This only ensures initialization of a proxy object or collection.<br>
     * It is not guaranteed that the elements INSIDE the collection will be initialized/materialized.
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
    private static Class<?> getClassWithoutInitializingProxy(final Object object) {
        if (object instanceof HibernateProxy hibernateProxy) {
            final LazyInitializer initializer = hibernateProxy.getHibernateLazyInitializer();

            return initializer.getPersistentClass();
        }
        else {
            return object.getClass();
        }
    }

    private HibernateUtils() {
        super();
    }
}
