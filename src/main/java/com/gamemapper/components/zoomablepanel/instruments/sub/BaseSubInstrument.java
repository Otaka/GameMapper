package com.gamemapper.components.zoomablepanel.instruments.sub;

import com.gamemapper.components.zoomablepanel.ZoomablePanel;
import com.gamemapper.components.zoomablepanel.instruments.BaseInstrument;
import com.gamemapper.components.zoomablepanel.instruments.BaseSelectAndManipInstrument;

/**
 * @author Dmitry
 */
public class BaseSubInstrument extends BaseInstrument {

    protected BaseSelectAndManipInstrument parentInstrument;

    public BaseSubInstrument(ZoomablePanel panel) {
        super(panel);
    }

    public void setParentInstrument(BaseSelectAndManipInstrument parentInstrument) {
        this.parentInstrument = parentInstrument;
    }

    public void onSelectedObjectModified() {

    }
}
