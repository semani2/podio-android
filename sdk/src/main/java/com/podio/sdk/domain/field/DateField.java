/*
 *  Copyright (C) 2014 Copyright Citrix Systems, Inc.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of
 *  this software and associated documentation files (the "Software"), to deal in
 *  the Software without restriction, including without limitation the rights to
 *  use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 *  of the Software, and to permit persons to whom the Software is furnished to
 *  do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package com.podio.sdk.domain.field;

import com.podio.sdk.internal.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author László Urszuly
 */
public class DateField extends Field<DateField.Value> {
    /**
     * This class describes the particular settings of a Date field configuration.
     *
     * @author László Urszuly
     */
    private static class Settings {
        private final Boolean calendar = null;
        private final String end = null;
        private final String time = null;
    }

    /**
     * This class describes the specific configuration of a Date field.
     *
     * @author László Urszuly
     */
    public static class Configuration extends Field.Configuration {
        private final Value default_value = null;
        private final Settings settings = null;

        public Value getDefaultValue() {
            return default_value;
        }

        public boolean isCalendar() {
            return settings != null && Utils.getNative(settings.calendar, false);
        }

        public State getEndDateState() {
            try {
                return State.valueOf(settings.end);
            } catch (NullPointerException e) {
                return State.undefined;
            } catch (IllegalArgumentException e) {
                return State.undefined;
            }
        }

        public State getTimeState() {
            try {
                return State.valueOf(settings.time);
            } catch (NullPointerException e) {
                return State.undefined;
            } catch (IllegalArgumentException e) {
                return State.undefined;
            }
        }
    }

    /**
     * This class describes a Date field value.
     *
     * @author László Urszuly
     */
    public static class Value extends Field.Value {
        private final String end;
        private final String end_date = null;
        private final String end_date_utc = null;
        private final String end_time = null;
        private final String end_time_utc = null;
        private final String end_utc = null;
        private final String start;
        private final String start_date = null;
        private final String start_date_utc = null;
        private final String start_time = null;
        private final String start_time_utc = null;
        private final String start_utc = null;

        public Value(Date start) {
            this(start, null);
        }

        public Value(Date start, Date end) {
            this.start = start != null ? Utils.formatDateTime(start) : null;
            this.end = end != null ? Utils.formatDateTime(end) : null;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Value) {
                Value other = (Value) o;

                boolean equalStart = other.start != null && other.start.equals(this.start)
                        || other.start == null && this.start == null;
                boolean equalEnd = other.end != null && other.end.equals(this.end)
                        || other.end == null && this.end == null;

                return equalStart && equalEnd;
            }

            return false;
        }

        @Override
        public Map<String, Object> getCreateData() {
            HashMap<String, Object> data = null;

            if (Utils.notEmpty(start) || Utils.notEmpty(end)) {
                data = new HashMap<String, Object>();

                if (Utils.notEmpty(start)) {
                    data.put("start", start);
                }

                if (Utils.notEmpty(end)) {
                    data.put("end", end);
                }
            }

            return data;
        }

        @Override
        public int hashCode() {
            String s1 = start != null ? start : "";
            String s2 = end != null ? end : "";

            return (s1 + s2).hashCode();
        }

        /**
         * @return returns true if you have a utc start date.
         */
        public boolean hasStartDateUtc(){
            return start_date_utc != null;
        }

        /**
         * @return returns true if you have a utc end date.
         */
        public boolean hasEndDateUtc(){
            return end_date_utc != null;
        }

        /**
         * @return returns true if you can rely on having a start time component in
         *         the UTC start date Date object, otherwise false.
         */
        public boolean hasStartTimeUtc(){
            return start_time_utc != null;
        }

        /**
         * @return returns true if you can rely on having a end time component in
         *         the UTC end date Date object, otherwise false.
         */
        public boolean hasEndTimeUtc(){
            return end_time_utc != null;
        }

        public Date getEndDateTime() {
            return Utils.parseDateTimeDefault(end);
        }

        public Date getEndDate() {
            return Utils.parseDateDefault(end_date);
        }

        public Date getEndDateUtc() {
            return Utils.parseDateUtc(end_date_utc);
        }

        public Date getEndTime() {
            return Utils.parseTimeDefault(end_time);
        }

        public Date getEndTimeUtc() {
            return Utils.parseTimeUtc(end_time_utc);
        }

        public Date getEndUtc() {
            if(hasEndTimeUtc()) {
                return Utils.parseDateTimeUtc(end_utc);
            } else {
                return Utils.parseDateUtc(end_utc);
            }
        }

        public Date getStartDateTime() {
            return Utils.parseDateTimeUtc(start);
        }

        public Date getStartDate() {
            return Utils.parseDateDefault(start_date);
        }

        public Date getStartDateUtc() {
            return Utils.parseDateUtc(start_date_utc);
        }

        public Date getStartTime() {
            return Utils.parseTimeDefault(start_time);
        }

        public Date getStartTimeUtc() {
            return Utils.parseTimeUtc(start_time_utc);
        }

        public Date getStartUtc() {
            if(hasStartTimeUtc()) {
                return Utils.parseDateTimeUtc(start_utc);
            } else {
                return Utils.parseDateUtc(start_utc);
            }
        }

    }

    public static enum State {
        disabled, enabled, required, undefined
    }

    // Private fields.
    private final Configuration config = null;
    private final ArrayList<Value> values;

    public DateField(String externalId) {
        super(externalId);
        this.values = new ArrayList<Value>();
    }

    @Override
    public void setValues(List<Value> values) {
        this.values.clear();
        this.values.addAll(values);
    }

    @Override
    public void addValue(Value value) {
        if (values != null && !values.contains(value)) {
            values.clear();
            values.add(0, value);
        }
    }

    @Override
    public Value getValue(int index) {
        return values != null ? values.get(index) : null;
    }

    @Override
    public List<Value> getValues() {
        return values;
    }

    @Override
    public void removeValue(Value value) {
        if (values != null && values.contains(value)) {
            values.remove(value);
        }
    }

    @Override
    public int valuesCount() {
        return values != null ? values.size() : 0;
    }

    public Configuration getConfiguration() {
        return config;
    }
}
