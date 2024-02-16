package com.homihq.db2rest.dialect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homihq.db2rest.exception.GenericDataAccessException;
import com.homihq.db2rest.model.DbColumn;
import com.homihq.db2rest.model.DbTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.convert.StringConvert;
import org.postgresql.util.PGobject;
import org.springframework.stereotype.Component;
import schemacrawler.schema.DatabaseInfo;


import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class PostGreSQLDialect implements Dialect{

    private final ObjectMapper objectMapper;

    @Override
    public boolean canSupport(DatabaseInfo databaseInfo) {
        return StringUtils.equalsAnyIgnoreCase(databaseInfo.getDatabaseProductName(), "POSTGRESQL");
    }

    @Override
    public void processTypes(DbTable table, List<String> insertableColumns, Map<String, Object> data) {
        try {
            for (String columnName : insertableColumns) {
                Object value = data.get(columnName);

                String columnDataTypeName = table.lookupColumn(columnName).getColumnDataType().getName();

                if (StringUtils.equalsAnyIgnoreCase(columnDataTypeName, "json")) {
                    Object v = convertToJson(value, columnDataTypeName);

                    data.put(columnName, v);
                }

            }
        }
        catch (Exception exception) {
            throw new GenericDataAccessException(exception.getMessage());
        }
    }

    @Override
    public Object processValue(String value, Class<?> type, String format) {
        if (String.class == type) {
            return "'" + value + "'";
        }
        else if (Boolean.class == type || boolean.class == type) {
            return Boolean.valueOf(value);

        }
        else if (Integer.class == type || int.class == type) {
            return Integer.valueOf(value);
        }
        else {
            return value;
        }

    }

    private Object convertToInt(Object value) {
        if(value.getClass().isAssignableFrom(Integer.class)) {
            return value;
        }
        else{
            return StringConvert.INSTANCE.convertFromString(Integer.class, value.toString());
        }
    }

    private Object convertToJson(Object value, String columnDataTypeName) throws Exception {
        PGobject pGobject = new PGobject();
        pGobject.setType(columnDataTypeName);
        pGobject.setValue(objectMapper.writeValueAsString(value));

        return pGobject;
    }


}