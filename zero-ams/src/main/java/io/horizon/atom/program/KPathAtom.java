package io.horizon.atom.program;

import io.horizon.eon.VSpec;
import io.horizon.eon.VString;
import io.horizon.eon.em.Environment;
import io.horizon.util.HUt;

/**
 * @author lang : 2023-06-12
 */
public class KPathAtom {
    private transient final String name;
    private transient final String path;

    private transient final String input;

    private transient final String output;

    private transient Environment environment = Environment.Production;

    private KPathAtom(final String name) {
        this.name = name;
        this.path = VSpec.Web.init.OOB + VString.SLASH + name;
        this.input = VSpec.Web.atom.of(name);
        this.output = VSpec.Web.atom.TARGET;
    }

    public static KPathAtom of(final String name) {
        return new KPathAtom(name);
    }

    public KPathAtom bind(final Environment environment) {
        this.environment = environment;
        return this;
    }

    public String path() {
        return this.path;
    }

    public String input() {
        return this.input;
    }

    public String output() {
        return HUt.ioPath(this.output, this.environment);
    }

    public String ui(final String identifier) {
        return VSpec.Web.init.OOB + VString.SLASH + this.name + VString.SLASH + identifier + VString.SLASH;
    }
}
