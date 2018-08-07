/**
 * Ignore these file extensions with webpack. Meant to be used in tests.
 */

function noop() {
    return null;
}

require.extensions['.png'] = noop;
