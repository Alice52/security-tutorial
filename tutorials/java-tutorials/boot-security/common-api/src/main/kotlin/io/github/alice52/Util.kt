package io.github.alice52

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author alice52
 * @date 2023/9/18
 * @project boot-security
 */
class Util

// logger()
inline fun <reified R : Any> R.logger(): Logger = LoggerFactory.getLogger(
    this::class.java.name.substringBefore("\$Companion").substringBefore("\$\$EnhancerBy")
)
