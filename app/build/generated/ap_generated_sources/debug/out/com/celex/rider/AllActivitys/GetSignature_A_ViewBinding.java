// Generated code from Butter Knife. Do not modify!
package com.celex.rider.AllActivitys;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.celex.rider.R;
import com.celex.rider.Utils.DrawingViewUtils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class GetSignature_A_ViewBinding implements Unbinder {
  private GetSignature_A target;

  @UiThread
  public GetSignature_A_ViewBinding(GetSignature_A target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public GetSignature_A_ViewBinding(GetSignature_A target, View source) {
    this.target = target;

    target.mDrawingViewUtils = Utils.findRequiredViewAsType(source, R.id.main_drawing_view, "field 'mDrawingViewUtils'", DrawingViewUtils.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    GetSignature_A target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mDrawingViewUtils = null;
  }
}
