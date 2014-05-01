package com.podio.sdk.domain;

public final class Application {

    public static final class Config {
        public final String type = null;
        public final String name = null;
        public final String item_name = null;
        public final String description = null;
        public final String usage = null;
        public final String external_id = null;
        public final String icon = null;
        public final Integer icon_id = 0;
    }

    public final Long app_id = 0L;
    public final Long space_id = 0L;
    public final String status = null;
    public final Config config = null;
    public final String[] rights = {};

    private Application() {
        // Hide the constructor.
    }
}
