package com.fund.generator

import cn.hutool.core.util.StrUtil

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.baomidou.mybatisplus.generator.FastAutoGenerator
import com.baomidou.mybatisplus.generator.config.*
import com.baomidou.mybatisplus.generator.config.rules.DateType
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy
import com.baomidou.mybatisplus.generator.fill.Column
import java.util.*
import java.util.function.Function

open class AutoGenerator {}

fun getTables(tables: String): List<String> {
    return if ("all" == tables) emptyList() else listOf(*tables.split(",").toTypedArray())
}

fun main() {
    val host = "localhost"
    val port = 3306
    val username = "root"
    val password = "root"
    val schema = "fund"

    val projectPath = System.getProperty("user.dir")
    val projectModuleName: String = "/orm"
    // /mybatis-plus-support
    var outDirPath = "$projectPath$projectModuleName/src/main/kotlin"
    var xmlDirPath = "$projectPath$projectModuleName/src/main/resources/mapper"
    val test = false
    if (test) {
        outDirPath = "D:\\temp\\kotlin"
        xmlDirPath = "D:\\temp\\resources"
    }
    val finalOutDirPath = outDirPath
    val finalXmlDirPath = xmlDirPath
    val mysqlJdbcUrlTemplate =
        "jdbc:mysql://{}:{}/{}?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=Asia/Shanghai"
    FastAutoGenerator.create(
        DataSourceConfig.Builder(
            StrUtil.format(mysqlJdbcUrlTemplate, host, port, schema),
            username,
            password
        )
            .schema(schema)
    ) // 全局配置
        .globalConfig { builder: GlobalConfig.Builder ->
            builder
                .author("书记") // 设置作者
                //.enableSwagger() // 开启 swagger 模式
                .disableOpenDir() // 禁止打开输出目录
                .dateType(DateType.TIME_PACK)
                .outputDir(finalOutDirPath) // 指定输出目录
                .enableKotlin() // 开启kotlin模式
        } // 包配置
        .packageConfig { scanner: Function<String?, String?>, builder: PackageConfig.Builder ->
            builder.parent("com.fund") // 设置父包名
                 .moduleName(scanner.apply("请输入模块名")!!) // 设置父包模块名
                .entity("model") // 设置Entity包名
                .service("service") // 设置service包名
                .serviceImpl("serviceImpl") // 设置Service Impl 包名
                .mapper("mapper") // 设置 Mapper 包名
                .xml("mapper")
                .pathInfo(
                    Collections.singletonMap(
                        OutputFile.xml,
                        finalXmlDirPath
                    )
                ) // 指定xml生成路径
        } // 策略配置
        .strategyConfig { scanner: Function<String?, String>, builder: StrategyConfig.Builder ->
            val tables = getTables(
                scanner.apply("请输入表名，多个英文逗号分隔？所有输入 all")
            )
            builder.addInclude(tables)
                .serviceBuilder()
                .formatServiceFileName("%sService")
                .formatServiceImplFileName("%sServiceImpl")
                .enableFileOverride() // 覆盖已生成文件
                .entityBuilder() // 实体策略配置
                .enableTableFieldAnnotation() // 开启生成实体时生成字段注解
                .addTableFills( // 添加表字段填充
                    Column("create_time", FieldFill.INSERT),
                    Column("update_time", FieldFill.INSERT_UPDATE)
                )
                //.enableLombok() // 开启lombok
                .idType(IdType.AUTO) // 全局主键类型
                .enableFileOverride() // 覆盖已生成文件
                .mapperBuilder()
                .superClass(BaseMapper::class.java)
                .enableMapperAnnotation() // 开启 @Mapper 注解
                .enableBaseResultMap()
                .enableBaseColumnList()
                .formatMapperFileName("%sMapper")
                .formatXmlFileName("%sMapper")
                .enableFileOverride() // 覆盖已生成文件
        } // 模板配置
        .templateConfig { builder: TemplateConfig.Builder ->
            builder.disable(
                TemplateType.CONTROLLER // 禁用controller模板
            ) /*.entity("/templates/entity.java")
                    .service("/templates/service.java")
                    .serviceImpl("/templates/serviceImpl.java")
                    .mapper("/templates/mapper.java")
                    .xml("/templates/mapper.xml")*/
        } /*
                    模板引擎配置，默认 Velocity 可选模板引擎 Beetl 或 Freemarker
                   .templateEngine(new BeetlTemplateEngine())
                   .templateEngine(new FreemarkerTemplateEngine())
                 */
        .execute()
}

