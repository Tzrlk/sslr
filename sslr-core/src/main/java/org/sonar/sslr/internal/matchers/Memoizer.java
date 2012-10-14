/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.sslr.internal.matchers;

public class Memoizer implements MatchHandler {

  private final ParseNode[] memos;

  public Memoizer(int length) {
    memos = new ParseNode[length + 1];
  }

  public boolean match(MatcherContext context) {
    ParseNode memo = memos[context.getCurrentIndex()];
    if (memo != null && memo.getMatcher() == context.getMatcher()) {
      context.currentIndex = memo.getEndIndex();
      context.createNode(memo);
      return true;
    }
    return false;
  }

  public void onMatch(MatcherContext context) {
    memos[context.getStartIndex()] = context.getNode();
  }

  public void onMissmatch(MatcherContext context) {
    // nop
  }

}