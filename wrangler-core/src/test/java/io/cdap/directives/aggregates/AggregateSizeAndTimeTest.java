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

 package io.cdap.directives.aggregates;

 import io.cdap.wrangler.TestingRig;
 import io.cdap.wrangler.api.Row;
 import org.junit.Assert;
 import org.junit.Test;
 
 import java.util.Arrays;
 import java.util.List;
 
 public class AggregateSizeAndTimeTest {
     @Test
     public void testSingleRow() throws Exception {
         String[] directive = new String[] {
             "aggregate-size-and-time data_transfer_size response_time " +
             "total_size_mb total_time_sec MB s total"
         };
         List<Row> rows = Arrays.asList(
             new Row().add("data_transfer_size", "10KB").add("response_time", "150ms")
         );
         List<Row> results = TestingRig.execute(directive, rows);
         Assert.assertEquals(1, results.size());
         Assert.assertEquals("Total size MB", 0.009765625,
             ((Double) results.get(0).getValue("total_size_mb")).doubleValue(), 0.001);
         Assert.assertEquals("Total time sec", 0.15,
             ((Double) results.get(0).getValue("total_time_sec")).doubleValue(), 0.001);
     }
 
     @Test
     public void testMultipleRows() throws Exception {
         String[] directive = new String[] {
             "aggregate-size-and-time data_transfer_size response_time " +
             "total_size_mb total_time_sec MB s total"
         };
         List<Row> rows = Arrays.asList(
             new Row().add("data_transfer_size", "10KB").add("response_time", "150ms"),
             new Row().add("data_transfer_size", "1MB").add("response_time", "1s")
         );
         List<Row> results = TestingRig.execute(directive, rows);
         Assert.assertEquals(1, results.size());
         Assert.assertEquals("Total size MB", 1.009765625,
             ((Double) results.get(0).getValue("total_size_mb")).doubleValue(), 0.001);
         Assert.assertEquals("Total time sec", 1.15,
             ((Double) results.get(0).getValue("total_time_sec")).doubleValue(), 0.001);
     }
 }
