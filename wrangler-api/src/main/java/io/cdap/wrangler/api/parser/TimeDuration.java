/*
 * Copyright © 2024-2025 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

 package io.cdap.wrangler.api.parser;

 import com.google.gson.JsonElement;
 import com.google.gson.JsonPrimitive;
 
 import java.util.Arrays;
 import java.util.HashSet;
 import java.util.Set;
 
 /**
  * Represents a time duration value with a numeric value and unit (e.g., "5ms", "2.1s").
  * Provides methods to parse and convert the duration to milliseconds.
  */
 public class TimeDuration implements Token {
     private final String raw;
     private final double value;
     private final String unit;
 
     private static final Set<String> VALID_UNITS = new HashSet<>(Arrays.asList("ms", "s"));
 
     public TimeDuration(String raw) {
         this.raw = raw;
         if (raw == null || raw.trim().isEmpty()) {
             throw new IllegalArgumentException("TimeDuration input cannot be null or empty");
         }
 
         // Validate format: number (optional decimal) followed by unit
         if (!raw.matches("^[0-9]+(\\.[0-9]+)?[mMsS]+$")) {
             throw new IllegalArgumentException(
                 "Invalid TimeDuration format: " + raw + ". Expected format like '5ms', '2.1s'."
             );
         }
 
         // Extract unit and normalize to lowercase
         String tempUnit = raw.replaceAll("[0-9.]", "").toLowerCase();
         if (!VALID_UNITS.contains(tempUnit)) {
             throw new IllegalArgumentException(
                 "Invalid time unit in " + raw + ". Valid units are: " + VALID_UNITS
             );
         }
         this.unit = tempUnit;
 
         // Extract numeric value
         String valueStr = raw.replaceAll("[^0-9.]", "");
         try {
             this.value = Double.parseDouble(valueStr);
         } catch (NumberFormatException e) {
             throw new IllegalArgumentException("Invalid numeric value in TimeDuration: " + valueStr);
         }
 
         if (this.value < 0) {
             throw new IllegalArgumentException("TimeDuration value cannot be negative: " + raw);
         }
     }
 
     public long getMilliseconds() {
         switch (unit) {
             case "ms": return (long) value;
             case "s": return (long) (value * 1000);
             default: throw new IllegalStateException("Unexpected unit: " + unit);
         }
     }
 
     @Override
     public Object value() {
         return raw;
     }
 
     @Override
     public TokenType type() {
         return TokenType.TIME_DURATION;
     }
 
     @Override
     public JsonElement toJson() {
         return new JsonPrimitive(raw);
     }
 }
