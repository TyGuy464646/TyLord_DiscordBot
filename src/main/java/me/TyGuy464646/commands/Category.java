package me.TyGuy464646.commands;

public enum Category {
//    AUTOMATION(":gear:", "Automation"),
//    CASINO(":game_die:", "Casino"),
//    ECONOMY(":moneybag:", "Economy"),
//    FUN(":smile:", "Fun"),
//    GREETINGS(":wave:", "Greetings"),
//    LEVELS(":chart_with_upwards_trend:", "Levels"),
//    MUSIC(":musical_note:", "Music"),
    STAFF(":computer:", "Staff"),
//    STARBOARD(":star:", "Starboard"),
//    SUGGESTIONS(":thought_balloon:", "Suggestions"),
    UTILITY(":tools:", "Utility");
//    PETS(":dog:", "Pets");

    public final String emoji;
    public final String name;

    Category(String emoji, String name) {
        this.emoji = emoji;
        this.name = name;
    }
}