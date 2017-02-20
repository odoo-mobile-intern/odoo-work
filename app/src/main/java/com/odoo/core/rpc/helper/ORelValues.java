package com.odoo.core.rpc.helper;

import java.util.ArrayList;
import java.util.Arrays;

public class ORelValues extends ArrayList<Object> {

    public ORelValues append() {
        //todo
        return this;
    }

    public ORelValues add(ORelData data) {
        add(Arrays.asList(0, 0, data));
        return this;
    }
}
