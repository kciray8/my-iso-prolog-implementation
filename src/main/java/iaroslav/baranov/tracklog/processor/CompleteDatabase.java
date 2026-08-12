package iaroslav.baranov.tracklog.processor;

import iaroslav.baranov.tracklog.service.db.Procedure;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CompleteDatabase {
    Map<String, Procedure> proceduresMap = new LinkedHashMap<>();

    public Map<String, Procedure> getProceduresMap() {
        return proceduresMap;
    }

    public void setProceduresMap(Map<String, Procedure> proceduresMap) {
        this.proceduresMap = proceduresMap;
    }

    public List<String> getIndicators() {
        return new ArrayList<>(proceduresMap.keySet());
    }
}
