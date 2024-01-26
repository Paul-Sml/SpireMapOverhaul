package spireMapOverhaul.zones.hailstorm;

import basemod.BaseMod;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import com.megacrit.cardcrawl.ui.campfire.RestOption;
import spireMapOverhaul.BetterMapGenerator;
import spireMapOverhaul.SpireAnniversary6Mod;
import spireMapOverhaul.abstracts.AbstractZone;
import spireMapOverhaul.zoneInterfaces.CampfireModifyingZone;
import spireMapOverhaul.zoneInterfaces.EncounterModifyingZone;
import spireMapOverhaul.zoneInterfaces.ModifiedEventRateZone;
import spireMapOverhaul.zoneInterfaces.RenderableZone;
import spireMapOverhaul.zones.hailstorm.monsters.FrostSlimeL;
import spireMapOverhaul.zones.hailstorm.monsters.FrostSlimeM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HailstormZone extends AbstractZone implements ModifiedEventRateZone, RenderableZone, EncounterModifyingZone, CampfireModifyingZone {
    public static final String ID = "Hailstorm";
    public static final String Frost_Slime_L = SpireAnniversary6Mod.makeID("Frost_Slime_L");
    public static final String Frost_Slime_M = SpireAnniversary6Mod.makeID("Frost_Slime_M");

    /*@Override
    public AbstractEvent forceEvent() {
        return ModifiedEventRateZone.returnIfUnseen(CoolExampleEvent.ID);
    }*/

    @Override
    public float zoneSpecificEventRate() {
        return 1;
    }

    public HailstormZone() {
        super(ID, Icons.MONSTER, Icons.EVENT, Icons.REST);
        this.width = 2;
        this.maxWidth = 4;
        this.height = 3;
        this.maxHeight = 4;
    }

    @Override
    public AbstractZone copy() {
        return new HailstormZone();
    }

    /*@Override
    protected boolean allowAdditionalPaths() {
        return false;
    }

    @Override
    public boolean generateMapArea(BetterMapGenerator.MapPlanner planner) {
        return generateNormalArea(planner, width, height);
    }*/
    @Override
    public Color getColor() {
        return Color.CYAN.cpy();
    }

    /*@Override
    public void manualRoomPlacement(Random rng) {
        //set all nodes to a specific room.
        *//*for (MapRoomNode node : nodes) {
            node.setRoom(new EventRoom());//new MonsterRoomElite());
        }*//*
    }*/

    /*@Override
    public void distributeRooms(Random rng, ArrayList<AbstractRoom> roomList) {
        //Guarantee at least One Elite Room in zone. This method will do nothing if the zone is already full.
        //placeRoomRandomly(rng, roomOrDefault(roomList, (room)->room instanceof MonsterRoomElite, MonsterRoomElite::new));
    }*/

    /*@Override
    public void replaceRooms(Random rng) {
        //Replace 100% of event rooms with treasure rooms.
        //replaceRoomsRandomly(rng, TreasureRoom::new, (room)->room instanceof EventRoom, 1);
    }*/

    /*@Override
    public void renderBackground(SpriteBatch sb) {
        // Render things in the background when this zone is active.
    }*/

    /*@Override
    public void renderForeground(SpriteBatch sb) {
        // Render things in the foreground when this zone is active.
    }*/

    /*@Override
    public void update() {
        // Update things when this zone is active.
    }*/

    @Override
    public boolean canSpawn() {
        return this.isAct(1);
    }

    @Override
    protected boolean canIncludeEarlyRows() {
        return false;
    }

    @Override
    public void registerEncounters() {
        EncounterModifyingZone.super.registerEncounters();
        BaseMod.addMonster(Frost_Slime_M, () -> new MonsterGroup(
                new AbstractMonster[] {
                        new FrostSlimeM(0.0f, 0.0f),
                }
        ));
    }

    @Override
    public List<ZoneEncounter> getNormalEncounters() {
        return Collections.singletonList(
                new ZoneEncounter(Frost_Slime_L, 1, () -> new MonsterGroup(
                        new AbstractMonster[]{
                                new FrostSlimeL(0.0f, 0.0f),
                        }))
        );
    }

    public void postAddButtons(ArrayList<AbstractCampfireOption> buttons) {
        for (AbstractCampfireOption c : buttons) {
            if (c instanceof RestOption && c.usable) {
                c.usable = false;
                ((RestOption) c).updateUsability(false);
                break;
            }
        }
    }
}
