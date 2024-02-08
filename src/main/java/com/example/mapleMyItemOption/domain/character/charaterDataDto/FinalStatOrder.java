package com.example.mapleMyItemOption.domain.character.charaterDataDto;

public enum FinalStatOrder {
        MINIMUM_STAT_OFFENSE(0),
        MAXIMUM_STAT_OFFENSE(1),
        DAMAGE(2),
        BOSS_DAMAGE(3),
        FINAL_DAMAGE(4),
        IGNORE_ARMOR(5),
        CRITICAL_PROBABILITY(6),
        CRITICAL_DAMAGE(7),
        CC_TOLERANCE(8),
        STANCE(9),
        DEFENSE(10),
        VELOCITY(11),
        JUMP(12),
        STAR_FORCE(13),
        ARCANE_FORCE(14),
        ACCENTIC_FORCE(15),
        STR(16),
        DEX(17),
        INT(18),
        LUK(19),
        HP(20),
        MP(21),
        AP_USED_STR(22),
        AP_USED_DEX(23),
        AP_USED_INT(24),
        AP_USED_LUK(25),
        AP_USED_HP(26),
        AP_USED_MP(27),
        ITEM_DROP(28),
        MONEY_DROP(29),
        BUFF_DURATION(30),
        ATTACK_FREQUENCY(31),
        NORMAL_DAMAGE(32),
        COOL_DOWN_SEC(33),
        COOL_DOWN_PERCENT(34),
        COOL_DOWN_IGNORE(35),
        TOLERANCE_IGNORE(36),
        CC_ADDITIONAL_DAMAGE(37),
        WEAPON_SKILLED(38),
        EXP_ADDITIONAL(39),
        PHYSICAL_OFFENSE(40),
        MAGICAL_OFFENSE(41),
        ASSAULT(42),
        SUMMON_DURATION(43);


        private final int order;
        FinalStatOrder(int order){
            this.order = order;
        }

        public int getOrder(){
            return order;
        }

}
