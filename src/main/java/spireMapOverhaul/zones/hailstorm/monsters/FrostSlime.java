package spireMapOverhaul.zones.hailstorm.monsters;

import basemod.abstracts.CustomMonster;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateShakeAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.CanLoseAction;
import com.megacrit.cardcrawl.actions.unique.CannotLoseAction;
import com.megacrit.cardcrawl.actions.utility.HideHealthBarAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Slimed;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.exordium.AcidSlime_M;
import com.megacrit.cardcrawl.powers.*;
import spireMapOverhaul.SpireAnniversary6Mod;

public class FrostSlime extends CustomMonster {
    public static final String ID = SpireAnniversary6Mod.makeID(FrostSlime.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final String WOUND_NAME;
    private static final String SPLIT_NAME;
    private static final String BP_NAME;
    private boolean firstMove = true;
    public static final int HP_MIN = 65;
    public static final int HP_MAX = 69;
    public static final int A_2_HP_MIN = 68;
    public static final int A_2_HP_MAX = 72;
    public static final int W_TACKLE_DMG = 11;
    public static final int N_TACKLE_DMG = 16;
    public static final int A_2_W_TACKLE_DMG = 12;
    public static final int A_2_N_TACKLE_DMG = 18;
    public static final int BP_TURNS = 1;
    public static final int WOUND_COUNT = 2;
    private static final byte SLIME_TACKLE = 1;
    private static final byte NORMAL_TACKLE = 2;
    private static final byte SPLIT = 3;
    private static final byte BP_LICK = 4;
    private float saveX;
    private float saveY;
    private boolean splitTriggered;
    private int slimeTackleDamage;
    private int slimeTackleSlimed;
    private int lickBlockPreventionTurns;
    private int normalTackleDamage;

    public FrostSlime() {
        this(0.0f, 0.0f);
    }

    public FrostSlime(final float x, final float y) {
        super(NAME, ID, HP_MAX, -5.0F, -4.0F, 180.0F, 280.0F, null, x, y);
        this.type = EnemyType.NORMAL;
        if (AbstractDungeon.ascensionLevel >= 7) {
            this.setHp(A_2_HP_MIN, A_2_HP_MAX);
        } else {
            this.setHp(HP_MIN, HP_MAX);
        }

        if (AbstractDungeon.ascensionLevel >= 2) {
            this.slimeTackleDamage = A_2_W_TACKLE_DMG;
            this.normalTackleDamage = A_2_N_TACKLE_DMG;
        } else {
            this.slimeTackleDamage = W_TACKLE_DMG;
            this.normalTackleDamage = N_TACKLE_DMG;
        }
        this.damage.add(new DamageInfo(this, this.slimeTackleDamage));
        this.damage.add(new DamageInfo(this, this.normalTackleDamage));

        this.slimeTackleSlimed = WOUND_COUNT;
        this.lickBlockPreventionTurns = BP_TURNS;

        this.powers.add(new SplitPower(this));
        this.powers.add(new BarricadePower(this));

        this.loadAnimation(SpireAnniversary6Mod.makeImagePath("monsters/ThievesHideout/BanditLieutenant/skeleton.atlas"), SpireAnniversary6Mod.makeImagePath("monsters/ThievesHideout/BanditLieutenant/skeleton.json"), 1.0F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        this.stateData.setMix("Hit", "Idle", 0.2F);
        this.state.setTimeScale(1.0F);
    }

    @Override
    public void takeTurn() {
        switch (this.nextMove) {
            case SLIME_TACKLE:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new SFXAction("MONSTER_SLIME_ATTACK"));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, (DamageInfo)this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new Slimed(), 2));
                AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
                break;
            case NORMAL_TACKLE:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, (DamageInfo)this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
                break;
            case SPLIT:
                AbstractDungeon.actionManager.addToBottom(new CannotLoseAction());
                AbstractDungeon.actionManager.addToBottom(new AnimateShakeAction(this, 1.0F, 0.1F));
                AbstractDungeon.actionManager.addToBottom(new HideHealthBarAction(this));
                AbstractDungeon.actionManager.addToBottom(new SuicideAction(this, false));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(1.0F));
                AbstractDungeon.actionManager.addToBottom(new SFXAction("SLIME_SPLIT"));
                AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(new AcidSlime_M(this.saveX - 134.0F, this.saveY + MathUtils.random(-4.0F, 4.0F), 0, this.currentHealth), false));
                AbstractDungeon.actionManager.addToBottom(new SpawnMonsterAction(new AcidSlime_M(this.saveX + 134.0F, this.saveY + MathUtils.random(-4.0F, 4.0F), 0, this.currentHealth), false));
                AbstractDungeon.actionManager.addToBottom(new CanLoseAction());
                this.setMove(SPLIT_NAME, (byte)3, Intent.UNKNOWN);
                break;
            case BP_LICK:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new NoBlockPower(AbstractDungeon.player, BP_TURNS, true), BP_TURNS));
                AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
        }

    }

    public void damage(DamageInfo info) {
        super.damage(info);
        if (!this.isDying && (float)this.currentHealth <= (float)this.maxHealth / 2.0F && this.nextMove != 3 && !this.splitTriggered) {
            this.setMove(SPLIT_NAME, (byte)3, Intent.UNKNOWN);
            this.createIntent();
            AbstractDungeon.actionManager.addToBottom(new TextAboveCreatureAction(this, TextAboveCreatureAction.TextType.INTERRUPTED));
            AbstractDungeon.actionManager.addToBottom(new SetMoveAction(this, SPLIT_NAME, (byte)3, Intent.UNKNOWN));
            this.splitTriggered = true;
        }

    }

    protected void getMove(int num) {
        if (AbstractDungeon.ascensionLevel >= 17) {
            if (num < 40) {
                if (this.lastTwoMoves((byte)1)) {
                    if (AbstractDungeon.aiRng.randomBoolean(0.6F)) {
                        this.setMove((byte)2, Intent.ATTACK, ((DamageInfo)this.damage.get(1)).base);
                    } else {
                        this.setMove(BP_NAME, (byte)4, Intent.DEBUFF);
                    }
                } else {
                    this.setMove(WOUND_NAME, (byte)1, Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(0)).base);
                }
            } else if (num < 70) {
                if (this.lastTwoMoves((byte)2)) {
                    if (AbstractDungeon.aiRng.randomBoolean(0.6F)) {
                        this.setMove(WOUND_NAME, (byte)1, Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(0)).base);
                    } else {
                        this.setMove(BP_NAME, (byte)4, Intent.DEBUFF);
                    }
                } else {
                    this.setMove((byte)2, Intent.ATTACK, ((DamageInfo)this.damage.get(1)).base);
                }
            } else if (this.lastMove((byte)4)) {
                if (AbstractDungeon.aiRng.randomBoolean(0.4F)) {
                    this.setMove(WOUND_NAME, (byte)1, Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(0)).base);
                } else {
                    this.setMove((byte)2, Intent.ATTACK, ((DamageInfo)this.damage.get(1)).base);
                }
            } else {
                this.setMove(BP_NAME, (byte)4, Intent.DEBUFF);
            }
        } else if (num < 30) {
            if (this.lastTwoMoves((byte)1)) {
                if (AbstractDungeon.aiRng.randomBoolean()) {
                    this.setMove((byte)2, Intent.ATTACK, ((DamageInfo)this.damage.get(1)).base);
                } else {
                    this.setMove(BP_NAME, (byte)4, Intent.DEBUFF);
                }
            } else {
                this.setMove(WOUND_NAME, (byte)1, Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(0)).base);
            }
        } else if (num < 70) {
            if (this.lastMove((byte)2)) {
                if (AbstractDungeon.aiRng.randomBoolean(0.4F)) {
                    this.setMove(WOUND_NAME, (byte)1, Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(0)).base);
                } else {
                    this.setMove(BP_NAME, (byte)4, Intent.DEBUFF);
                }
            } else {
                this.setMove((byte)2, Intent.ATTACK, ((DamageInfo)this.damage.get(1)).base);
            }
        } else if (this.lastTwoMoves((byte)4)) {
            if (AbstractDungeon.aiRng.randomBoolean(0.4F)) {
                this.setMove(WOUND_NAME, (byte)1, Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(0)).base);
            } else {
                this.setMove((byte)2, Intent.ATTACK, ((DamageInfo)this.damage.get(1)).base);
            }
        } else {
            this.setMove(BP_NAME, (byte)4, Intent.DEBUFF);
        }

    }

    @Override
    public void changeState(String key) {
        if (key != null && key.equals("MAUL")) {
            this.state.setAnimation(0, "Attack", false);
            this.state.addAnimation(0, "Idle", true, 0.0F);
        }
    }

    static {
        WOUND_NAME = MOVES[0];
        SPLIT_NAME = MOVES[1];
        BP_NAME = MOVES[2];
    }

}
