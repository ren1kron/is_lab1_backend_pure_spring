package se.ifmo.origin_backend.aop;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.stereotype.Component;
import se.ifmo.origin_backend.cache.CacheStatsLoggingToggle;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class L2CacheStatsAspect {
    private final CacheStatsLoggingToggle toggle;
    private final EntityManagerFactory emf;

    @Around("execution(* se.ifmo.origin_backend.repo..*(..))")
    public Object logCacheStats(ProceedingJoinPoint pjp) throws Throwable {
        if (!toggle.isEnabled()) {
            return pjp.proceed();
        }

        SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
        Statistics stats = sessionFactory.getStatistics();
        if (!stats.isStatisticsEnabled()) {
            stats.setStatisticsEnabled(true);
        }

        long l2HitBefore = stats.getSecondLevelCacheHitCount();
        long l2MissBefore = stats.getSecondLevelCacheMissCount();
        long l2PutBefore = stats.getSecondLevelCachePutCount();
        long qHitBefore = stats.getQueryCacheHitCount();
        long qMissBefore = stats.getQueryCacheMissCount();
        long qPutBefore = stats.getQueryCachePutCount();

        Object result = null;
        try {
            result = pjp.proceed();
        } finally {
            long l2HitAfter = stats.getSecondLevelCacheHitCount();
            long l2MissAfter = stats.getSecondLevelCacheMissCount();
            long l2PutAfter = stats.getSecondLevelCachePutCount();
            long qHitAfter = stats.getQueryCacheHitCount();
            long qMissAfter = stats.getQueryCacheMissCount();
            long qPutAfter = stats.getQueryCachePutCount();

            long l2HitDelta = l2HitAfter - l2HitBefore;
            long l2MissDelta = l2MissAfter - l2MissBefore;
            long l2PutDelta = l2PutAfter - l2PutBefore;
            long qHitDelta = qHitAfter - qHitBefore;
            long qMissDelta = qMissAfter - qMissBefore;
            long qPutDelta = qPutAfter - qPutBefore;

            if (l2HitDelta != 0 || l2MissDelta != 0 || l2PutDelta != 0 || qHitDelta != 0 || qMissDelta != 0 || qPutDelta != 0) {
                log.info(
                    "L2 cache stats for {}: hits={}, misses={}, puts={}, queryHits={}, queryMisses={}, queryPuts={}",
                    pjp.getSignature().toShortString(),
                    l2HitDelta,
                    l2MissDelta,
                    l2PutDelta,
                    qHitDelta,
                    qMissDelta,
                    qPutDelta);
            }
        }

        return result;
    }
}
