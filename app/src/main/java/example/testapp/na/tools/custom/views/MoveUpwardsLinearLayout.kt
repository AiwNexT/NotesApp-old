package example.testapp.na.tools.custom.views

import android.content.Context
import android.widget.LinearLayout
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import example.testapp.na.tools.custom.behaviors.MoveUpwardsBehavior

@CoordinatorLayout.DefaultBehavior(MoveUpwardsBehavior::class)
class MoveUpwardsLinearLayout : LinearLayout {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}