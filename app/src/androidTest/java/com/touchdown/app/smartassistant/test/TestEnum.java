package com.touchdown.app.smartassistant.test;

import android.test.AndroidTestCase;

import com.touchdown.app.smartassistant.models.ActionType;

/**
 * Created by Pete on 1.9.2014.
 */
public class TestEnum extends AndroidTestCase {

    public void testValue(){
        ActionType type = ActionType.ALARM;
        assertEquals(type.value, 0);

        type = ActionType.RINGERVOLUME;

        assertEquals(type.value, 1);
    }

    public void testGetEnum(){
        int typeInt = 0;

        assertEquals(ActionType.ALARM, ActionType.getEnum(typeInt));

        typeInt = 1;

        assertEquals(ActionType.RINGERVOLUME, ActionType.getEnum(typeInt));
    }
}
