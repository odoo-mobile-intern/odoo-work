package com.odoo.core.rpc.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ORelValues extends ArrayList<Object> {

    public ORelValues append() {
        //todo
        return this;
    }

    public ORelValues put(ORelData data) {
        add(Arrays.asList(0, 0, data));
        return this;
    }

    public ORelValues replace(List<Integer> ids) {
        add(Arrays.asList(6, 0, ids.toArray()));
        return this;
    }

    public ORelValues replace(int... ids) {
        add(Arrays.asList(6, 0, Arrays.asList(ids)));
        return this;
    }
}
