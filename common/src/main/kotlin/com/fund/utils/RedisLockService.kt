package com.fund.utils


import com.fund.exception.BusinessException
import mu.KotlinLogging
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.support.DefaultTransactionDefinition
import java.util.concurrent.TimeUnit


@Service
class RedisLockService(
    private val redissonClient: RedissonClient,
    private val transactionManager: PlatformTransactionManager
) {

    init {
        Companion.redissonClient = this.redissonClient
        Companion.transactionManager = this.transactionManager
    }

    companion object {
        private val log = KotlinLogging.logger {}
        lateinit var redissonClient:RedissonClient
        lateinit var transactionManager: PlatformTransactionManager

        fun <T> transaction(supplier: () -> T) : T {
            //事务基础信息 超时时间、隔离级别、传播属性等
            val def = DefaultTransactionDefinition() // 定义事务属性
            // 设置传播行为属性， 默认事务级别。 当前无事务则新建事务，已有事务则加入事务。
            def.propagationBehavior = DefaultTransactionDefinition.PROPAGATION_REQUIRED
            // 设置事务隔离级别为可重复读. 可重复读，能确保同一事物多次查询结果一致，但可能在此期间内，同样的查询条件，
            // 查询到数据条数不一样，导致好像出现了幻觉，故而有幻读问题，这种更多出现在数据的新增/删除的情况。
            //def.isolationLevel = Isolation.REPEATABLE_READ.value() // 有幻读问题
            def.isolationLevel = Isolation.SERIALIZABLE.value()
            // 获得事务状态
            val status = transactionManager.getTransaction(def)
            return try {
                val t = supplier()
                transactionManager.commit(status);// 提交
                t
            } catch (e: BusinessException) {
                // 除了BusinessException可以直接抛出到前端，其他异常都隐藏必要的信息，只打印。
                log.error("BusinessException: ${e.message}", e)
                transactionManager.rollback(status);// 回滚
                throw e
            } catch (e: Exception) {
                log.error("Exception", e)
                transactionManager.rollback(status);// 回滚
                throw BusinessException("operation_failed")
            }
        }

        fun <T> lockTransaction(key: String, supplier: ()->T): T {
            val lock = redissonClient.getReadWriteLock(key)
            //事务基础信息 超时时间、隔离级别、传播属性等
            val def = DefaultTransactionDefinition() // 定义事务属性
            // 设置传播行为属性， 默认事务级别。 当前无事务则新建事务，已有事务则加入事务。
            def.propagationBehavior = DefaultTransactionDefinition.PROPAGATION_REQUIRED
            // 设置事务隔离级别为可重复读. 可重复读，能确保同一事物多次查询结果一致，但可能在此期间内，同样的查询条件，
            // 查询到数据条数不一样，导致好像出现了幻觉，故而有幻读问题，这种更多出现在数据的新增/删除的情况。
            //def.isolationLevel = Isolation.REPEATABLE_READ.value() // 有幻读问题
            def.isolationLevel = Isolation.SERIALIZABLE.value()
            // 获得事务状态
            val status = transactionManager.getTransaction(def)
            return try {
                // 写锁
                val bool = lock.writeLock().tryLock(100, 20, TimeUnit.SECONDS)
                if (!bool) {
                    log.error("{} 没拿到锁", key)
                    throw BusinessException("run fail")
                }
                val t = supplier()
                transactionManager.commit(status);// 提交
                t
            } catch (e: BusinessException) {
                // 除了BusinessException可以直接抛出到前端，其他异常都隐藏必要的信息，只打印。
                log.error("BusinessException: ${e.message}", e)
                transactionManager.rollback(status);// 回滚
                throw e
            } catch (e: Exception) {
                log.error("Exception", e)
                transactionManager.rollback(status);// 回滚
                throw BusinessException("operation_failed")
            } finally {
                // 释放锁
                lock.writeLock().unlock()
            }
        }
        /**
         * 拿到锁之后才可以执行方法
         * @param key
         * @param supplier
         * @param <T>
         * @return
        </T> */
        fun <T> lock(key: String, supplier: ()->T): T {
            val lock = redissonClient.getReadWriteLock(key)
            return try {
                // 写锁
                val bool = lock.writeLock().tryLock(100, 20, TimeUnit.SECONDS)
                if (!bool) {
                    log.error("{} 没拿到锁", key)
                    throw BusinessException("run fail")
                }
                supplier()
            } catch (e: BusinessException) {
                // 除了BusinessException可以直接抛出到前端，其他异常都隐藏必要的信息，只打印。
                log.error("BusinessException: ${e.message}")
                throw e
            } catch (e: NullPointerException) {
                log.error("NullPointerException", e)
                throw BusinessException("operation_failed")
            } catch (e: Exception) {
                log.error("Exception", e)
                throw BusinessException("operation_failed")
            } finally {
                // 释放锁
                lock.writeLock().unlock()
            }
        }
    }
}
