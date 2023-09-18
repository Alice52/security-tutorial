package io.github.alice52.security.raw

import io.github.alice52.logger
import org.jetbrains.annotations.TestOnly
import org.junit.Test


/**
 * @author alice52
 * @date 2023/9/18
 * @project boot-security
 */
class RawJwtTest {
    val generate = JwtGenerate()

    @TestOnly
    @Test
    fun testGenerate() {
        val array: Array<String> = Array(0) { index -> "Element $index" }
        val rawToken = generate.generate("sub", array, 36000)

        logger().info("raw jwt token: {}", rawToken)
    }

    @Test
    @TestOnly
    fun testParse() {
        val rawToken: String =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOlsic3ViIl0sImF1ZCI6W1tdXSwiZXhwIjpbMTY5NTA3NDc2OV0sImlhdCI6WzE2OTUwMzg3NjldLCJpc3MiOlsibWFzb24ubWV0YW11Zy5uZXQiXSwianRpIjpbImIzMWYzZDc0LTI5NWMtNGQ4YS04NmJiLTBiMTg5NTZlZmQzMyJdfQ.e0GgOGKwCspK0qZaOPYF4dRjQFYdWPd8TdJO97PAmcA"
        generate.validThenParse(rawToken)
    }

}