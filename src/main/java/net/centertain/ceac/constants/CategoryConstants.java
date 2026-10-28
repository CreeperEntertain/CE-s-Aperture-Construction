package net.centertain.ceac.constants;

import java.util.List;

public final class CategoryConstants {
    private CategoryConstants() {}

    public static final class Main {
        private Main() {}

        public static final String BASICS = "Basic Items";
        public static final String MATERIALS = "Materials";
        public static final String MATERIAL_SHAPES = "Material Shapes";
    }

    public static final class Sub {
        private Sub() {}

        public static final class Mats {
            private Mats() {}

            public static final String MISC = "Miscellaneous";

            public static final String BACKPANEL = "Backpanels";
            public static final String CONCRETE = "Concrete";
            public static final String METAL = "Metal";
            public static final String PLASTER = "Plaster";
            public static final String PLASTIC = "Plastic";
            public static final String TILE = "Tile";
            public static final String WALLPAPER = "Wallpaper";
            public static final String WOOD = "Wood";

            public static final List<String> SORTED = List.of(
                    MISC,
                    BACKPANEL,
                    CONCRETE,
                    METAL,
                    PLASTER,
                    PLASTIC,
                    TILE,
                    WALLPAPER,
                    WOOD
            );
        }

        public static final class Shapes {
            private Shapes() {}

            public static final String MISC = "Miscellaneous";

            public static final String BASIC = "Basic Shapes";
            public static final String ROUND = "Round Shapes";
            public static final String SLOPES_FULL = "Full Slopes";
            public static final String SLOPES_HALF = "Half Slopes";
            public static final String SLOPES_THIRD = "Third Slopes";
            public static final String SLOPES_QUARTER = "Quarter Slopes";

            public static final List<String> SORTED = List.of(
                    MISC,
                    BASIC,
                    ROUND,
                    SLOPES_FULL,
                    SLOPES_HALF,
                    SLOPES_THIRD,
                    SLOPES_QUARTER
            );
        }
    }
}
