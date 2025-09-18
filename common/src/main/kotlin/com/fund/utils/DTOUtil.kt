package com.fund.utils

import cn.hutool.core.bean.BeanUtil
import cn.hutool.core.collection.CollUtil
import com.baomidou.mybatisplus.core.metadata.IPage
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import mu.KotlinLogging
import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.Collectors

class DTOUtil {


    data class NodeConfig<T>(
        // 主键字段名称
        var idKey: String = "id",
        // 关联的父级主键字段名称
        var pidKey: String = "pid",
        // 树形结构的子集字段名称
        var childrenKey: String = "children",
        // 主键成为根级的条件。
        var rootCondition: Long = -1L,

        var comparator: Comparator<T>? = null
    )

    companion object {
        private val logger = KotlinLogging.logger {}

        fun <T> partition(source: List<T>, splitItemNum: Int): List<List<T>> {
            return source.withIndex().groupBy { it.index / splitItemNum }.map { it -> it.value.map { it.value } }
        }

        /**
         * 将目标转换为对应的实体
         *
         * @param <T>   目标泛型
         * @param <D>   当前泛型
         * @param d     待转换对象
         * @param clazz 目标类型class
         * @return [T] 目标对象
         * @author lemon
         * @since 2022 -02-21 13:44:24
        </D></T> */
        @JvmStatic
        fun <T, D> toDTO(d: D, clazz: Class<T>): T? {
            var t: T? = null
            try {
                t = clazz.getConstructor().newInstance()
                BeanUtil.copyProperties(d, t)
            } catch (e: Exception) {
                logger.error(e) { "Can not instantiate ${clazz.name}" }
            }
            return t
        }

        /**
         * Build page 函数释义.
         *
         * @param <T>      the type parameter
         * @param <D>      the type parameter
         * @param pageData 入参释义
         * @return [IPage] 出参释义
         * @author lemon
         * @since 2022 -04-07 17:14:02
        </D></T> */
        @JvmStatic
        fun <T, D> buildPage(pageData: IPage<D>): Page<T> {
            return Page(pageData.current, pageData.size, pageData.total)
        }

        /**
         * map转dto
         *
         * @param <T>      目标泛型
         * @param d        map
         * @param clazz    需要转换的class
         * @return [T] 目标
         * @author lemon
         * @since 2022 -02-25 14:27:48
        </T> */
        @JvmStatic
        fun <T> toDTO(d: Map<String?, Any?>?, clazz: Class<T>): T {
            return BeanUtil.mapToBean(d, clazz, true)
        }

        /**
         * 将目标列表转换为对应类型的列表
         *
         * @param <T>      目标泛型
         * @param <D>      当前泛型
         * @param dataList 待转换列表
         * @param clazz    目标类型class
         * @return [List] 目标列表
         * @author lemon
         * @since 2022 -02-21 13:44:24
        </D></T> */
        @JvmStatic
        fun <T, D> toDTO(dataList: List<D>, clazz: Class<T>): List<T> {
            return toDTO(dataList, clazz, null)
        }

        /**
         * 将目标列表转换为对应类型的列表
         *
         * @param <T>      目标泛型
         * @param <D>      当前泛型
         * @param dataList 待转换列表
         * @param clazz    目标类型class
         * @param consumer 对得到的目标对象进行进一步的处理
         * @return [List] 目标列表
         * @author lemon
         * @since 2022 -02-21 13:44:25
        </D></T> */
        @JvmStatic
        fun <T, D> toDTO(dataList: List<D>, clazz: Class<T>, consumer: Consumer<T>?): List<T> {
            val list: MutableList<T> = ArrayList()
            for (d in dataList) {
                var t: T? = null
                try {
                    t = clazz.getConstructor().newInstance()
                    BeanUtil.copyProperties(d, t)
                    consumer?.accept(t)
                    list.add(t)
                } catch (e: Exception) {
                    logger.error("", e)
                }
            }
            return list
        }


        /**
         * 集合转树形结构集合
         *
         * @param <T>      the type parameter
         * @param <D>      the type parameter
         * @param list     入参释义
         * @param clazz    入参释义
         * @param consumer 入参释义
         * @param config   入参释义
         * @return [List] 出参释义
         * @author lemon
         * @since 2022 -03-04 15:30:58
        </D></T> */
        @JvmStatic
        fun <T, D> toNodeTree(list: List<D>, clazz: Class<T>, consumer: Consumer<T>?, config: NodeConfig<T>): List<T>? {
            val nodeList = toDTO(list, clazz, consumer)
            var root: List<T> = mutableListOf()
            //BeanUtil.getFieldValue()
            for (dto in nodeList) {
                val pid = BeanUtil.getFieldValue(dto, config.pidKey)
                if (config.rootCondition == pid) {
                    (root as MutableList).add(dto)
                }
            }

            // 根据pid分组
            val hash = nodeList.stream().collect(
                Collectors.groupingBy(Function { dto: T -> BeanUtil.getFieldValue(dto, config.pidKey) })
            )
            config.comparator?.let {
                root = root.stream().sorted(it).toList()
            }

            deepTree(root, hash, config)
            return root
        }

        @JvmStatic
        private fun <T> deepTree(root: List<T>, hash: Map<Any, List<T>>, config: NodeConfig<T>) {
            for (dto in root) {
                val id = BeanUtil.getFieldValue(dto, config.idKey)
                var children = hash[id]
                children?.let {
                    if (CollUtil.isNotEmpty(children)) {
                        config.comparator?.let {
                            children = children!!.stream().sorted(it).toList()
                        }
                        BeanUtil.setFieldValue(dto, config.childrenKey, children)
                        deepTree(children!!, hash, config)
                    }
                }
            }
        }

    }
}