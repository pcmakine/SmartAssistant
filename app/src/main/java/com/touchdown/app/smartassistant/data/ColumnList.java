package com.touchdown.app.smartassistant.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Pete on 17.8.2014.
 */
public class ColumnList {
    private List<String> columnNames;
    private int currentIndex;

    public ColumnList(){
        this.columnNames = new ArrayList<String>();
        this.currentIndex = 0;
    }

    public void buildList(Class cls){
        List<Field> fieldsList = sortFieldsByValue(cls.getDeclaredFields());

        for(Field field: fieldsList){
            if(!field.getName().equals("TABLE_NAME")){
                String value = "";
                try {
                    value = (String) field.get(null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                columnNames.add(value);
            }
        }
    }

    private List<Field> sortFieldsByValue(Field[] fields){
        List<Field> fieldsList = Arrays.asList(fields);
        Collections.sort(fieldsList, new Comparator<Field>() {
            @Override
            public int compare(Field lhs, Field rhs) {
                String firstVal  = "";
                String secondVal  = "";
                try {
                    firstVal = (String) lhs.get(null);
                    secondVal = (String) rhs.get(null);

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return firstVal.compareTo(secondVal);
            }
        });
        return fieldsList;
    }

    public String getCurrentAndAdvanceOne(){
        String retVal = columnNames.get(currentIndex);
        this.currentIndex++;
        return retVal;
    }

    public void resetPointerToBeginning(){
        this.currentIndex = 0;
    }
}
