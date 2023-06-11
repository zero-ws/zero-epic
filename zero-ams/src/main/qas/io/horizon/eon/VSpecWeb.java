package io.horizon.eon;

import io.horizon.eon.em.Environment;

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

        interface oob {
            // init/oob/secure
            String SECURE = OOB + "/secure";
            // init/oob/environment
            String ENVIRONMENT = OOB + "/environment";
            // init/oob/navigation
            String NAVIGATION = OOB + "/navigation";
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
