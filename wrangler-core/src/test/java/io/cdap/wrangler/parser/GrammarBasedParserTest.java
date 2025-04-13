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

 package io.cdap.wrangler.parser;

 import io.cdap.wrangler.TestingRig;
 import io.cdap.wrangler.api.CompileException;
 import io.cdap.wrangler.api.CompileStatus;
 import io.cdap.wrangler.api.Compiler;
 import io.cdap.wrangler.api.DirectiveParseException;
 import io.cdap.wrangler.api.RecipeParser;
 import org.junit.Test;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;
 
 public class GrammarBasedParserTest {
     @Test
     public void testValidAggregateSyntax() throws CompileException, DirectiveParseException {
         String[] recipe = {"aggregate-size-and-time size_col time_col total_size total_time MB s total"};
         TestingRig.compileSuccess(recipe);
     }
 
     @Test
     public void testInvalidAggregateSyntax() throws CompileException, DirectiveParseException {
         String[] recipe = {"aggregate-size-and-time size_col time_col"}; // Missing required args
         TestingRig.compileFailure(recipe);
     }
 
     @Test
     public void testBasic() throws Exception {
         String[] recipe = {"#pragma version 2.0;", "rename :col1 :col2", "parse-as-csv :body ',' true;"};
         RecipeParser parser = TestingRig.parse(recipe);
         assertEquals(2, parser.parse().size()); // Adjust if return type differs
     }
 
     @Test
     public void testLoadableDirectives() throws Exception {
         String[] recipe = {
             "#pragma version 2.0;",
             "#pragma load-directives text-reverse, text-exchange;",
             "rename col1 col2",
             "parse-as-csv body , true",
             "text-reverse :body;",
             "test prop: { a='b', b=1.0, c=true};",
             "#pragma load-directives test-change, text-exchange, test1, test2, test3, test4;"
         };
         Compiler compiler = new RecipeCompiler();
         CompileStatus status = compiler.compile(new MigrateToV2(recipe).migrate());
         assertEquals(7, status.getSymbols().getLoadableDirectives().size());
     }
 
     @Test
     public void testCommentOnlyRecipe() throws Exception {
         String[] recipe = {"// test"};
         RecipeParser parser = TestingRig.parse(recipe);
         assertEquals(0, parser.parse().size()); // Adjust if return type differs
     }
 }
