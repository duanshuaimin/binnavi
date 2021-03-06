// Copyright 2011-2016 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.security.zynamics.binnavi.Gui.CriteriaDialog.Conditions.Not;

import java.util.List;

import com.google.security.zynamics.binnavi.Gui.CriteriaDialog.Conditions.ICachedCriterium;
import com.google.security.zynamics.binnavi.Gui.CriteriaDialog.ExpressionModel.CCachedExpressionTreeNode;
import com.google.security.zynamics.binnavi.yfileswrap.zygraph.NaviNode;


/**
 * Cached NOT criterium.
 */
public final class CCachedNotCriterium implements ICachedCriterium, IAbstractNotCriterium {
  @Override
  public String getFormulaString(final List<CCachedExpressionTreeNode> children) {
    if (children.size() != 1) {
      throw new IllegalStateException(
          "IE01115: NOT operator has more or less than one child criterium.");
    }

    return "!(" + children.get(0).getCriterium().getFormulaString(children.get(0).getChildren())
        + ")";
  }

  @Override
  public boolean matches(final NaviNode node) {
    return true;
  }
}
