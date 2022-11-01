package tsp.headstorage;

public enum Category {

    ALPHABET("alphabet"),
    ANIMALS("animals"),
    BLOCKS("blocks"),
    DECORATION("decoration"),
    FOOD_DRINKS("food-drinks"),
    HUMANS("humans"),
    HUMANOID("humanoid"),
    MISCELLANEOUS("miscellaneous"),
    MONSTERS("monsters"),
    PLANTS("plants");

    private final String name;
    private final String url;

    public static final Category[] VALUES = values();

    Category(String name) {
        this.name = name;
        this.url = String.format("https://minecraft-heads.com/scripts/api.php?cat=%s&tags=true", name);
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

}