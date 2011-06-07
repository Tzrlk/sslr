/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonar.structural.search;

import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.api.LeftRecursiveRule;
import com.sonar.sslr.api.Rule;
import com.sonar.sslr.dsl.Literal;

import static com.sonar.sslr.dsl.DslTokenType.LITERAL;
import static com.sonar.sslr.dsl.DslTokenType.WORD;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Predicate.not;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.one2n;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.opt;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.or;

public class StructuralSearchPatternGrammar extends Grammar {

  public Rule pattern;
  public Rule matcher;
  public Rule thisMatcher;
  public Rule parentMatcher;
  public Rule directParentMatcher;
  public Rule indirectParentMatcher;
  public Rule childMatcher;
  public Rule directChildMatcher;
  public Rule indirectChildMatcher;
  public Rule sequenceMatcher;
  public Rule beforeMatcher;
  public LeftRecursiveRule afterMatcher;
  public Rule ruleValueList;
  public Rule tokenValue;
  public Rule nodeName;
  public Rule nodeNameOrTokenValue;
  public Rule ruleName;

  public StructuralSearchPatternGrammar(StructuralSearchPattern structuralSearchPattern) {
    pattern.is(or(sequenceMatcher, parentMatcher));
    parentMatcher.isOr(directParentMatcher, indirectParentMatcher);
    directParentMatcher.is(ruleName, "(", or(sequenceMatcher, parentMatcher), ")");
    indirectParentMatcher.is(ruleName, "(", "(", or(sequenceMatcher, parentMatcher), ")", ")");

    sequenceMatcher.is(opt(beforeMatcher), thisMatcher, opt(afterMatcher));
    beforeMatcher.is(not("this"), nodeNameOrTokenValue, opt(beforeMatcher));
    afterMatcher.is(opt(afterMatcher), nodeNameOrTokenValue);

    thisMatcher.is("this", "(", or("*", one2n(nodeNameOrTokenValue, opt(","))), ")", opt("(", childMatcher, ")"));

    childMatcher.isOr(directChildMatcher, indirectChildMatcher);
    directChildMatcher.is(ruleName, opt("(", or(childMatcher), ")"));
    indirectChildMatcher.is("(", ruleName, opt("(", or(childMatcher), ")"), ")");

    pattern.plug(structuralSearchPattern);
    directParentMatcher.plug(DirectParentNodeMatcher.class);
    indirectParentMatcher.plug(IndirectParentNodeMatcher.class);
    thisMatcher.plug(ThisNodeMatcher.class);
    sequenceMatcher.plug(SequenceMatcher.class);
    beforeMatcher.plug(BeforeMatcher.class);
    afterMatcher.plug(AfterMatcher.class);
    directChildMatcher.plug(DirectChildNodeMatcher.class);
    indirectChildMatcher.plug(IndirectChildNodeMatcher.class);

    nodeNameOrTokenValue.isOr(nodeName, tokenValue).skip();
    tokenValue.is(LITERAL).plug(Literal.class);
    nodeName.is(WORD).plug(String.class);
    ruleName.is(WORD).plug(String.class);
  }

  public StructuralSearchPatternGrammar() {
    this(new StructuralSearchPattern());
  }

  @Override
  public Rule getRootRule() {
    return pattern;
  }
}