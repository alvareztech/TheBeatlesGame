package tech.alvarez.thebeatlesgame;

import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.modifier.PathModifier;
import org.anddev.andengine.entity.sprite.AnimatedSprite;

/**
 * Created on 1/25/17.
 *
 * @author Daniel Alvarez
 */

public class CharacterRoute implements PathModifier.IPathModifierListener {

    private AnimatedSprite sprite;
    private int finX;
    private int finY;

    public CharacterRoute(AnimatedSprite sprite, int finX, int finY) {
        this.sprite = sprite;
        this.finX = finX;
        this.finY = finY;
    }

    @Override
    public void onPathStarted(PathModifier pathModifier, IEntity iEntity) {

    }

    @Override
    public void onPathWaypointStarted(PathModifier pathModifier, IEntity iEntity, int i) {

    }

    @Override
    public void onPathWaypointFinished(PathModifier pathModifier, IEntity iEntity, int i) {
        switch (i) {
            case 0: // frontal
                sprite.animate(new long[]{200, 200, 200}, 0, 2, true);
                break;
            case 1: // derecha
                sprite.animate(new long[]{200, 200, 200}, 6, 8, true);
                break;
            case 2: // espalda
                sprite.animate(new long[]{200, 200, 200}, 9, 11, true);
                break;
            case 3: // izquierda
                sprite.animate(new long[]{200, 200, 200}, 3, 5, true);
                break;
        }
    }

    @Override
    public void onPathFinished(PathModifier pathModifier, IEntity iEntity) {
        sprite.setPosition(finX, finY);
        sprite.stopAnimation();
        sprite.setCurrentTileIndex(1);
    }
}
