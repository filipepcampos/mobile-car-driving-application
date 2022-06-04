package pt.up.fe.mobilecardriving.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

public class PermissionView extends androidx.appcompat.widget.AppCompatImageView {
    private static final String ACTIVE_COLOR = "#8ED121", INACTIVE_COLOR = "#D12121";

    public PermissionView(@NonNull @NotNull Context context) {
        this(context, null);
    }

    public PermissionView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PermissionView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void changeIconState(boolean active) {
        this.setColorFilter(Color.parseColor(active ? ACTIVE_COLOR : INACTIVE_COLOR));
    }
}
