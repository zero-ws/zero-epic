/**
 * 异常架构重新整理，和 Formal 中的异常维持一致
 * 错误代码表，用于定义当前系统中的异常
 * 1. 负数为合法值，不定义正数
 * 2. 不绑定资源文件的异常区域：
 * -10000 ~ -10999: 保留区间（往下扩展的异常）
 * -11000 ~ -14999：内部异常代码（不绑定资源文件，只有英文版）
 * -15000 ~ -19999：内部异常专用扩展异常（编程模式）
 * 3. 绑定资源文件的异常区域，依赖SPI扩展
 * -20000 ~ -29999：扩展异常：非第三方扩展异常的基础封装
 * -20001: 内部Web异常（带HTTP状态代码）
 * -25001: 内部其他异常
 * -30000 ~ -39999：扩展异常：第三方异常的基础封装（配置异常）
 * -40000 ~ -49999：应用异常：对应 4xx HTTP状态码
 * -50000 ~ -59999：应用异常：对应 5xx HTTP状态码
 * 顶层双异常
 * Exception
 * -- ProgramException                          不绑定资源文件（编程异常，表示该异常需要带有 throw 定义）
 * ----- DaemonException                        编程模式的守护进程异常
 * RuntimeException
 * -- AbstractException                         不绑定资源文件（顶层异常）
 * ----- InternalException                      不绑定资源文件
 * ----- BootingException                       绑定资源文件，容器启动类异常
 */
package io.horizon.exception;