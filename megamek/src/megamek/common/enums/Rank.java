package megamek.common.enums;

public enum Rank {
    PLATINUM(500),
    GOLD(200),
    SILVER(100),
    BRONZE(0);

    private final int score;

    Rank (int score){
        this.score=score;
    }

    /**
     * @param score the score of the player
     * @return the rank of player according to the score
     */
    public static Rank getRankForScore(int score){
        if (score >= PLATINUM.score) {
            return PLATINUM;
        } else if (score >= GOLD.score ) {
            return GOLD;
        } else if (score >= SILVER.score ) {
            return SILVER;
        } else {
            return BRONZE;
        }
    }
}
