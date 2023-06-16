package io.horizon.eon.em.web;

/**
 * 服务器类型专用枚举，在不同场景定义为主不一样，针对不同容器其服务器的种类也有所区别
 * <pre><code>
 *     - http：      标准HTTP服务器
 *     - http3:      HTTP3服务器，基于QUIC协议的新服务器模式，这种模式下开的sock就是QUIC的，现阶段用于
 *                   SMAVE的 Jetty Server 中
 *     - sock：      WebSocket服务器
 *     - rx：        RxJava模式下的HTTP服务器，这种模式主要牵涉Vertx中的编程风格
 *     - ipc：       RPC服务器，内部通讯专用
 *     - api：       API网关，微服务模式下的Gateway
 * </code></pre>
 */
public enum ServerType {
    HTTP("http"),
    HTTP3("http3"),
    SOCK("sock"),
    RX("rx"),
    IPC("ipc"),
    API("api");

    private transient final String literal;

    ServerType(final String literal) {
        this.literal = literal;
    }

    public String key() {
        return this.literal;
    }

    public boolean match(final String literal) {
        return this.literal.equals(literal);
    }
}
