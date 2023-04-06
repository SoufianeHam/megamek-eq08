package megamek.client.ui.swing.boardview;

import java.awt.Image;

/**
 * 
 * @author arlith
 *
 */
public class HexImageCacheEntry {
    
    public final Image hexImage;
    
    public final boolean needsUpdating;
    
    HexImageCacheEntry(Image h) {
        hexImage = h;
        needsUpdating = false;
    }

}
