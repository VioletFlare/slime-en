package com.github.nekolr.slime.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SpiderOutput {

    /**
     * Question Name
     */
    private String nodeName;

    /**
     * 15th Last ID
     */
    private String nodeId;

    /**
     * All Outputs
     */
    @Getter
    private List<OutputItem> outputItems = new ArrayList<>();


    @AllArgsConstructor
    public static class OutputItem {

        /**
         * The name of the output item
         */
        @Getter
        private String name;

        /**
         * Value of the output variable
         */
        @Getter
        private Object value;


        @Override
        public String toString() {
            return "OutputItem{" +
                    "name='" + name + '\'' +
                    ", value=" + value +
                    '}';
        }
    }

    /**
     * Add an output item
     *
     * @param name  The name of the output item
     * @param value Value of the output variable
     */
    public void addItem(String name, Object value) {
        this.outputItems.add(new OutputItem(name, value));
    }
}
