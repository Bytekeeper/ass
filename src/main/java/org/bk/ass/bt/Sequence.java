package org.bk.ass.bt;

import org.bk.ass.StopWatch;

public class Sequence extends CompoundNode {

  public Sequence(TreeNode... children) {
    super(children);
  }

  @Override
  public void exec(ExecutionContext context) {
    StopWatch stopWatch = new StopWatch();
    for (TreeNode child : children) {
      execChild(child, context);
      if (child.status != NodeStatus.SUCCESS) {
        status = child.getStatus();
        if (child.status == NodeStatus.FAILURE) abortRunningChildren();
        stopWatch.registerWith(context, this);
        return;
      }
    }
    success();
    stopWatch.registerWith(context, this);
  }
}