package io.github.alice52

import io.undertow.server.DefaultByteBufferPool
import io.undertow.servlet.api.DeploymentInfo
import io.undertow.websockets.jsr.WebSocketDeploymentInfo
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.stereotype.Component

@Component
class ContainerConfiguration : WebServerFactoryCustomizer<UndertowServletWebServerFactory> {

    /**
     * Fix UT026010 warning.
     *
     * @param factory
     */
    override fun customize(factory: UndertowServletWebServerFactory) {
        factory.addDeploymentInfoCustomizers(UndertowDeploymentInfoCustomizer { deploymentInfo: DeploymentInfo ->
            val webSocketDeploymentInfo = WebSocketDeploymentInfo()
            webSocketDeploymentInfo.buffers = DefaultByteBufferPool(false, 1024)
            deploymentInfo.addServletContextAttribute(
                "io.undertow.websockets.jsr.WebSocketDeploymentInfo", webSocketDeploymentInfo
            )
        })
    }
}