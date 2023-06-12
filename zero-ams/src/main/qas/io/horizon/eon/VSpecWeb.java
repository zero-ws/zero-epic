package io.horizon.eon;

import io.horizon.eon.em.Environment;

import java.util.Objects;

/**
 * @author lang : 2023-05-29
 */
interface VSpecWeb {
    String CONFIGURATION = "configuration";
    String INIT = "init";
    String RUNTIME = "runtime";
    String PLUGIN = "plugin";
    // OSGI：OSGI部分全部采用复数形式
    String PLUGINS = "plugins";
    String FEATURES = "features";
    String EXTENSIONS = "extensions";

    String ATOM = "atom";

    interface atom {

        String TARGET = ATOM + "/target";

        static String of(final String name) {
            Objects.requireNonNull(name);
            return ATOM + "/" + name;
        }
    }

    interface runtime {
        // runtime/cache
        String CACHE = RUNTIME + "/cache";
        // runtime/log
        String LOG = RUNTIME + "/log";
        // runtime/configuration.json
        String CONFIGURATION_JSON = RUNTIME + "/configuration.json";
        // runtime/external/
        String EXTERNAL = RUNTIME + "/external";
        // runtime/environment/
        String ENVIRONMENT = RUNTIME + "/environment";

        interface environment {

            // runtime/environment/{0}-integration/
            static String ofIntegration(final Environment environment) {
                final String environmentName = environment.name().toLowerCase();
                return ENVIRONMENT + "/" + environmentName + "-integration/";
            }

            // runtime/environment/{0}-database.json
            static String ofDatabase(final Environment environment) {
                final String environmentName = environment.name().toLowerCase();
                return ENVIRONMENT + "/" + environmentName + "-database.json";
            }
        }

        // runtime/configuration.json
        interface configuration {
            String STELLAR = "stellar";                  /* Definition: stellar */
            String OPTIONS = "options";                  /* Definition: configuration options */
        }
    }

    interface init {
        // init/oob
        String OOB = INIT + "/oob";
        // init/permission
        String PERMISSION = INIT + "/permission";

        interface permission {
            // init/permission/ui.menu
            String UI_MENU = PERMISSION + "/ui.menu";

            interface ui_menu {
                // init/permission/ui.menu/role
                String ROLE = UI_MENU + "/role";

                // {root}/init/permission/ui.menu/{role}.json
                static String of(final String root, final String role) {
                    return root + UI_MENU + VString.SLASH + role + ".json";
                }

                interface role {
                    // init/permission/ui.menu/role/{role}.json
                    static String of(final String role) {
                        return ROLE + VString.SLASH + role + ".json";
                    }
                }
            }
        }

        interface oob {
            // init/oob/secure
            String SECURE = OOB + "/secure";
            // init/oob/navigation
            String NAVIGATION = OOB + "/navigation";
            /**
             * 前端对接目录：init/oob/cab
             * Cab目录主要用于前端UI连接，对应到表`UI_PAGE`和`UI_LAYOUT`，目录结构如下：
             * <pre><code>
             *     - init/oob/cab/component
             *       Page的UI配置
             *     - init/oob/cab/container
             *       Layout（Container）容器的配置
             * </code></pre>
             */
            String CAB = OOB + "/cab";
            /**
             * 数据目录：init/oob/data
             * 此目录用于将基础数据导入到数据库中
             * <pre><code>
             *     - 1）服务目录数据：Service Catalog
             *     - 2）字典数据：Directory Data
             *     - 3）位置数据，对接进销存PSI：Location of WH
             *     - 4）系统菜单数据：System Menu / Extension Menu
             *     - 5）ACL资源树数据：Resource Tree Data for ACL
             * </code></pre>
             */
            String DATA = OOB + "/data";
            /**
             * 环境目录：init/oob/environment
             * 此目录用于环境数据的导入
             * <pre><code>
             *     - 1）默认的账号数据
             *     - 2）组织架构数据：Company / Dept / Team
             *     - 3）员工和档案数据：Employee / Identity
             * </code></pre>
             */
            String ENVIRONMENT = OOB + "/environment";
            /**
             * 集成环境目录：init/oob/integration
             * 此目录在最新的（ 0.9 < ）中使用，存储了和集成相关的数据信息
             * <pre><code>
             *     - 1）FTP
             *     - 2）RESTful
             *     - 3）SMS
             *     - 4）Email
             * </code></pre>
             */
            String INTEGRATION = OOB + "/integration";
            /**
             * 模块化专用目录：init/oob/modulat
             * 模块化目录会关联到模块部分 {@link VSpec.Extension} 的完整目录结构，并带上模块名称
             */
            String MODULAT = OOB + "/modulat";
            /**
             * 角色目录：init/oob/role
             * 角色专用数据，包含了权限相关信息
             * <pre><code>
             *     - 1）针对每个角色的权限设置脚本
             *     - 2）超级角色 LANG.YU 中存储的内容
             * </code></pre>
             * 直接运行 ./run-perm.sh 脚本可处理
             */
            String ROLE = OOB + "/role";

            /**
             * 工作流专用目录：init/oob/workflow，此目录关联到新开发的 `X_ACTIVITY_RULE` 功能，在新的工作流引擎中，您可以
             * 针对不同的工作流节点设置不同的活动执行规则，这些规则会被存储在此目录下
             * <pre><code>
             *     - 1）为当前操作生成最新的操作日志记录
             *     - 2）绑定一个 Hooker 的回调执行函数，可以在每个规则触发滞后执行
             *     - 3）和操作有关的提醒（Email/SMS）
             *     - 4）AOP 在流程行为中注入
             * </code></pre>
             * 条件检查使用 JEXL 部分
             */
            String WORKFLOW = OOB + "/workflow";

            interface workflow {
                static String of(final String name) {
                    Objects.requireNonNull(name);
                    return WORKFLOW + "/" + name;
                }
            }

            interface modulat {
                static String of(final String name) {
                    Objects.requireNonNull(name);
                    return MODULAT + "/" + name;
                }
            }

            interface role {
                static String of(final String role) {
                    Objects.requireNonNull(role);
                    return ROLE + "/" + role;
                }
            }

            interface cab {
                String COMPONENT = CAB + "/component";
                String CONTAINER = CAB + "container";
            }
        }
    }

    interface configuration {
        // configuration/library
        String LIBRARY = CONFIGURATION + "/library";
        // configuration/editor
        String EDITOR = CONFIGURATION + "/editor";

        interface library {
            // configuration/library/system
            String SYSTEM = LIBRARY + "/system";
            // configuration/library/environment
            String INTERNAL = LIBRARY + "/environment";
            // configuration/library/external
            String EXTERNAL = LIBRARY + "/external";
        }

        interface editor {
            // configuration/editor/environment
            String INTERNAL = EDITOR + "/environment";
            // configuration/editor/external
            String EXTERNAL = EDITOR + "/external";
        }
    }
}
