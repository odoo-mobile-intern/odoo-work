package com.odoo.work.utils;

import java.util.ArrayList;
import java.util.List;

public class DataUtils {

    public static List<Integer> getDoubletToInt(List<Double> ids) {
        List<Integer> newIds = new ArrayList<>();
        for (Double id : ids) {
            newIds.add(id.intValue());
        }
        return newIds;
    }
}
