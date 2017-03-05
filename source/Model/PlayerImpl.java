package Model;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by xingfanxia on 2/26/17.
 */
public class PlayerImpl implements Player {
    /*
    player class holding all relevant attributes like
    gold, king, score, name, team, color, minions and buildings
     */
    CombatManager manager = new CombatManager();
    public Integer gold;
    private ArrayList<Building> buildings = new ArrayList<Building>();
    public ArrayList<MinionImpl> minions = new ArrayList<MinionImpl>();
    public King myKing;
    private Integer myScore;
    private int teamNum;
    private String playerName;
    private int[] playerColor; //in rgba

    public PlayerImpl opponent;
    public PlayerImpl(int teamNum, String playerName, int[] playerColor) {
        this.gold = 0;
        this.playerName = playerName;
        this.teamNum = teamNum;
        this.playerColor = playerColor;
        this.myScore = 0;
        this.myKing = new King(teamNum);
        this.opponent = opponent;
    }

    public Integer getGold() {
        return this.gold;
    }

    public ArrayList getBuildings() {
        return this.buildings;
    }

    public ArrayList<MinionImpl> getMinions() {
        return minions;
    }

    public String getPlayerName() {
        return playerName;
    }

    public King getKing() {
        return this.myKing;
    }

    public Integer getScore() {
        return this.myScore;
    }

    public int getTeam() {
        return this.teamNum;
    }

    public void setScore(Integer score) {
        this.myScore = score;
    }

    public void setTeam(int teamNum) {
        this.teamNum = teamNum;
    }

    public void add_Building(Building building) {
        this.buildings.add(building);
    }

    public void add_Minions(MinionImpl minion) {
        this.minions.add(minion);
        minion.master = this;
    }

    public void remove_Minions(MinionImpl minion) {
        this.minions.remove(minion);
    }

    public PlayerImpl getOpponent() {
        return opponent;
    }

    public void setOpponent(PlayerImpl opponent) {
        this.opponent = opponent;
        opponent.opponent = this;
    }

    /*
    To be implemented
     */
    public void getFarmers() {

    }

    public void spawn_farmer() {

    }

    public Integer getCrystal() {
        return null;
    }

    public ArrayList getMercenaries() {
        return null;
    }

    public void upgradeKing(King king) {

    }

}
