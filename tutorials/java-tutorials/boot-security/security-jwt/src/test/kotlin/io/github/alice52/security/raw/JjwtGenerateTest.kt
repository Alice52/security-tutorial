package io.github.alice52.security.raw

import io.github.alice52.logger
import org.junit.Test

/**
 * @author alice52
 * @date 2023/9/18
 * @project boot-security
 */
class JjwtGenerateTest {
    private val generate = JjwtGenerate4Sha256()

    @Test
    fun testSimpleGenerate() {
        val rawToken = generate.simpleGenerate()
        logger().info("raw jwt token: {}", rawToken)
    }

    @Test
    fun testGenerate() {
        val rawToken = generate.generate()
        logger().info("raw jwt token: {}", rawToken)
    }

    @Test
    fun testParse() {
        val rawToken: String =
            "eyJhSGVhZGVyTmFtZSI6ImFWYWx1ZSIsImFsZyI6IkhTMjU2In0.eyJpc3MiOiJTdG9ybXBhdGgiLCJzdWIiOiJtc2lsdmVybWFuIiwiaWF0IjoxNDY2Nzk2ODIyLCJleHAiOjQ2MjI0NzA0MjIsIm5hbWUiOiJNaWNhaCBTaWx2ZXJtYW4iLCJzY29wZSI6ImFkbWlucyJ9.M5zn62GACr3DNfbc_YX2iCRBBe8iLCYjRFcFvfrG60M"
        generate.validThenParse(rawToken)
    }

}