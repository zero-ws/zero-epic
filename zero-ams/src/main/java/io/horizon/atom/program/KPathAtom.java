package io.horizon.atom.program;

import io.horizon.eon.VString;
import io.horizon.eon.em.Environment;
import io.horizon.eon.spec.VWeb;
import io.horizon.util.HUt;

/**
 * @author lang : 2023-06-12
 */
public class KPathAtom {
    private final String name;
    private final String path;

    private final String input;

    private final String output;

    private String identifier;

    private Environment environment = Environment.Production;

    private KPathAtom(final String name) {
        this.name = name;
        this.path = VWeb.init.OOB + VString.SLASH + name;
        this.input = VWeb.atom.of(name);
        this.output = VWeb.atom.TARGET;
    }

    public static KPathAtom of(final String name) {
        return new KPathAtom(name);
    }

    public KPathAtom create(final String identifier) {
        final KPathAtom atom = of(this.name);
        atom.bind(this.environment);
        atom.bind(identifier);
        return atom;
    }

    public KPathAtom bind(final Environment environment) {
        this.environment = environment;
        return this;
    }

    public KPathAtom bind(final String identifier) {
        this.identifier = identifier;
        return this;
    }

    public String identifier() {
        return this.identifier;
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
        return VWeb.init.OOB + VString.SLASH + this.name + VString.SLASH + identifier + VString.SLASH;
    }

    public String atomUi() {
        return VWeb.init.OOB + VString.SLASH + this.name + VString.SLASH + this.identifier + VString.SLASH;
    }
}
