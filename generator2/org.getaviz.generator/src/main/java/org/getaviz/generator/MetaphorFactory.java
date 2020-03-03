package org.getaviz.generator;

import org.getaviz.generator.abap.city.ACityMetaphor;

class MetaphorFactory {
    static Metaphor createMetaphor(SettingsConfiguration config) {
            return new ACityMetaphor(config);
    }
}
