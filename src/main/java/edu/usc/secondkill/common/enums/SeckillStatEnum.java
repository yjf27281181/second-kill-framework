package edu.usc.secondkill.common.enums;

import lombok.Getter;

@Getter
public enum SeckillStatEnum {
    MUCH(2,"too many people"),
    SUCCESS(1,"success"),
    END(0,"activity is over"),
    REPEAT_KILL(-1,"repead kill"),
    INNER_ERROR(-2,"system error"),
    DATE_REWRITE(-3,"data is tampered");

    private int state;
    private String info;

    SeckillStatEnum(int state, String info) {
        this.state = state;
        this.info = info;
    }

    public static SeckillStatEnum stateOf(int index) {
        for (SeckillStatEnum state : values()) {
            if(state.getState() == index)
                return state;
        }
        return null;
    }

}
