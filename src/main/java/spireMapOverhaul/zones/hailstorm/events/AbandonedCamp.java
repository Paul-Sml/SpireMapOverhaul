package spireMapOverhaul.zones.hailstorm.events;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.events.city.Colosseum;
import com.megacrit.cardcrawl.events.shrines.WomanInBlue;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.AbstractRelic.RelicTier;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.relics.DeadBranch;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import jdk.nashorn.internal.ir.IfNode;
import spireMapOverhaul.SpireAnniversary6Mod;
import spireMapOverhaul.zones.hailstorm.cards.Freeze;
import spireMapOverhaul.zones.hailstorm.relics.Flint;
import spireMapOverhaul.zones.thieveshideout.ThievesHideoutZone;

// We extend the Colosseum event because ProceedButton.java specifically checks if an event is an instance of this type
// (or a few other types) in the logic for what happens when you click proceed. This is easier than a patch.
public class AbandonedCamp extends AbstractImageEvent {
    public static final String ID = SpireAnniversary6Mod.makeID(AbandonedCamp.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = SpireAnniversary6Mod.makeImagePath("events/Invasion/ThiefKing.png");

    private static final float FLINT_HP_LOSS = (float) 1 /24;
    private static final float A_15_FLINT_HP_LOSS = (float) 1 /16;
    private static final int GOLD = 90;
    private static final int A_15_GOLD = 60;
    private static final int HEALING = 1/4;
    private static final int A_15_HEALING = 1/6;

    private boolean T_DEADBRANCH_F_FLINT;
    private boolean T_GOLD_F_HEALING;

    private AbstractCard curse = new Freeze();
    private int hpLoss;
    private int goldGain;
    private int healing;


    public AbandonedCamp() {
        super(ID, NAME ,IMG );
        this.noCardsInRewards = true;

        //Event randomly chooses one big reward between two, and one light reward between two
        T_DEADBRANCH_F_FLINT = AbstractDungeon.miscRng.randomBoolean();
        T_GOLD_F_HEALING = AbstractDungeon.miscRng.randomBoolean();

        //Rewards
        if (AbstractDungeon.ascensionLevel >= 15) {//Asc 15

            //First impactful reward
            if (T_DEADBRANCH_F_FLINT) {
                curse.upgrade();
            } else {
                hpLoss = (int)(AbstractDungeon.player.maxHealth * A_15_FLINT_HP_LOSS);
            }

            //Second light reward
            if (T_GOLD_F_HEALING) {
                goldGain = A_15_GOLD;
            } else {
                healing = A_15_HEALING;
            }

        } else {//Non-Asc 15

            //First impactful reward
            if (!T_DEADBRANCH_F_FLINT) {
                hpLoss = (int)(AbstractDungeon.player.maxHealth * FLINT_HP_LOSS);
            }

            //Second light reward
            if (T_GOLD_F_HEALING) {
                goldGain = GOLD;
            } else {
                healing = HEALING;
            }

        }

        //Text
        //First impactful choice
        if (T_DEADBRANCH_F_FLINT) {
            this.imageEventText.setDialogOption(OPTIONS[0]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[1] + hpLoss + OPTIONS[2]);
        }

        //Second light choice
        if (T_GOLD_F_HEALING) {
            this.imageEventText.setDialogOption(OPTIONS[3] + goldGain + OPTIONS[4]);
        } else {
            this.imageEventText.setDialogOption(OPTIONS[5] + healing + OPTIONS[6]);
        }

        this.imageEventText.setDialogOption(OPTIONS[7]);
    }

    protected void buttonEffect(int buttonPressed) {
        switch (buttonPressed) {
            case 0:
                if (T_DEADBRANCH_F_FLINT)
                    AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse, (float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                else {
                    AbstractDungeon.player.damage(new DamageInfo((AbstractCreature) null, hpLoss));
                }

                AbstractDungeon.getCurrRoom().rewards.clear();
                String targetRelicId = T_DEADBRANCH_F_FLINT ? DeadBranch.ID : Circlet.ID;
                AbstractRelic relic = RelicLibrary.getRelic(targetRelicId).makeCopy();
                AbstractDungeon.getCurrRoom().rewards.add(new RewardItem(relic));

                AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
                AbstractDungeon.combatRewardScreen.open();
                break;
            case 1:
                if (T_GOLD_F_HEALING)
                    AbstractDungeon.player.gainGold(goldGain);
                else {
                    AbstractDungeon.player.heal(healing, true);
                }

                AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
                break;
            case 2:
                AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
                break;
        }
    }


}
